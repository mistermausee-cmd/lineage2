"""Orchestrator (main) — wires every component and runs the Department_Map.

Loads the Item_Catalog and each multisell of the Department_Map (+custom/),
computes a Buy_Price for every position (base formula + anti-exploit + override
+ stat ranking + Upgrade_Chain strict increase), validates via xmllint and
writes only the adena count. Emits a Price_Report.

Requirements: 11.1, 12.1, 12.6 (+ every pricing requirement via PriceEngine).
"""

import math
import os

from .catalog_loader import CatalogLoader
from .chain_resolver import ChainError, ChainResolver
from .constants import (
    CUSTOM_OVERRIDE_DEPARTMENTS,
    custom_multisell_dir,
    multisell_dir,
)
from .differentiation import enchant_category_factor
from .multisell_loader import MultisellLoader
from .multisell_writer import MultisellWriter, resolve_target_path
from .price_engine import PriceEngine
from .price_model import load_price_model
from .report_builder import ReportBuilder
from .schema_validator import SchemaValidator
from .stat_value import effective_stat_value


class Dept:
    """A department entry: how to price a given multisell."""

    def __init__(self, msid, name, role, kind="plain", stat_based=False, k=2.0):
        self.msid = msid
        self.name = name
        self.role = role          # role_weight key
        self.kind = kind          # plain | enchant | chain | jewelry
        self.stat_based = stat_based
        self.k = k                # Stat_Rank slope


# Department_Map from инструкции/04_МАГАЗИН_ALTB.md and design.md.
DEPARTMENT_MAP = [
    # R-Grade
    Dept(600060, "R-Grade/R-weapon", "weapon"),
    Dept(600061, "R-Grade/Bless-weapon", "weapon", kind="chain"),
    Dept(600062, "R-Grade/PVE-weapon", "weapon", kind="chain"),
    Dept(600063, "R-Grade/R-armor", "armor"),
    Dept(600064, "R-Grade/Bless-armor", "armor", kind="chain"),
    Dept(600065, "R-Grade/PVE-armor", "armor", kind="chain"),
    Dept(600066, "R-Grade/Beyond-1", "armor", kind="chain"),
    Dept(600067, "R-Grade/Beyond-2", "armor", kind="chain"),
    Dept(600068, "R-Grade/Beyond-3", "armor", kind="chain"),
    Dept(600069, "R-Grade/Beyond-4", "armor", kind="chain"),
    Dept(600070, "R-Grade/Beyond-5", "armor", kind="chain"),
    Dept(600071, "R-Grade/Beyond-6", "armor", kind="chain"),
    # Прокачка (levelling tiers B/A/S/S80)
    Dept(600052, "Level/Tier-B", "weapon"),
    Dept(600053, "Level/Tier-A", "weapon"),
    Dept(600054, "Level/Tier-S", "weapon"),
    Dept(600055, "Level/Tier-S80", "weapon"),
    # Бижутерия (jewelry ranked by Effective_Stat_Value)
    Dept(600044, "Jewelry/Bracelets", "jewelry", kind="jewelry", stat_based=True),
    Dept(600008, "Jewelry/Talismans", "accessory"),
    Dept(600030, "Jewelry/Epic", "jewelry", kind="jewelry", stat_based=True, k=4.0),
    Dept(600090, "Jewelry/Brooches", "jewelry", kind="jewelry", stat_based=True),
    Dept(600091, "Jewelry/Stones", "jewelry", kind="jewelry", stat_based=True),
    # Аксессуары
    Dept(600025, "Accessory/Cloaks", "accessory", kind="chain"),
    Dept(600026, "Accessory/Belts", "accessory"),
    Dept(600034, "Accessory/Headgear", "accessory"),
    Dept(600047, "Accessory/Shirts", "accessory"),
    Dept(600048, "Accessory/Agathions", "jewelry", kind="jewelry", stat_based=True),
    # Заточка
    Dept(600100, "Enchant/Scrolls", "enchant", kind="enchant"),
    Dept(600101, "Enchant/LifeStones", "enchant"),
    Dept(600102, "Enchant/SoulStones", "enchant"),
    Dept(600032, "Enchant/Dyes", "accessory"),
    Dept(600033, "Enchant/SkillBooks", "enchant"),
    # Крафт РБ/Клан
    Dept(600041, "Craft/Materials", "craft"),
    Dept(600045, "Craft/Crystals", "craft"),
    Dept(600113, "Craft/Gemstones", "craft"),
    Dept(600057, "Craft/Clan", "craft"),
    # Бакалея
    Dept(600035, "Grocery/Consumables", "consumable"),
    Dept(600011, "Grocery/SoulShots", "consumable"),
    Dept(600043, "Grocery/Boosters", "consumable"),
    Dept(600112, "Grocery/Bolts-Arrows", "consumable"),
    # Разное
    Dept(600103, "Misc/Pets", "spec"),
    Dept(600104, "Misc/PetFood", "consumable"),
    Dept(600105, "Misc/PetArmor", "spec"),
    Dept(600106, "Misc/PetJewelry", "spec"),
    Dept(600107, "Misc/PetWeapon", "spec"),
    # Внешний вид
    Dept(600108, "Appearance/ProcessStones", "cosmetic"),
    Dept(600109, "Appearance/WeaponProto", "cosmetic"),
    Dept(600110, "Appearance/ArmorProto", "cosmetic"),
    Dept(600111, "Appearance/HeadProto", "cosmetic"),
]


class Orchestrator:
    def __init__(self, model_path, overrides_path=None, catalog=None):
        self.model = load_price_model(model_path)
        if overrides_path and os.path.exists(overrides_path):
            self._merge_overrides(overrides_path)
        if catalog is None:
            catalog = CatalogLoader()
            catalog.load()
        self.catalog = catalog
        self.engine = PriceEngine(self.model, self.catalog)
        self.loader = MultisellLoader(multisell_dir(), custom_multisell_dir())
        self.writer = MultisellWriter()
        self.validator = SchemaValidator()
        self.report = ReportBuilder()
        # Track which override ids are actually used (Requirement 8.7).
        self._used_override_ids = set()

    def _merge_overrides(self, overrides_path):
        import yaml

        with open(overrides_path, "r", encoding="utf-8") as fh:
            data = yaml.safe_load(fh) or {}
        raw = data.get("manual_override", data) or {}
        for k, v in raw.items():
            try:
                self.model.manual_override[int(k)] = int(v)
            except (TypeError, ValueError):
                continue

    # ---- pricing of a single multisell --------------------------------
    def price_multisell(self, ms, dept):
        """Return a list of PriceResult (one per position, in order)."""
        cr = ChainResolver(ms.positions)

        # Effective_Stat_Value range across this multisell (for stat ranking).
        esv_values = []
        if dept.stat_based:
            for pos in ms.positions:
                item = self.catalog.get(pos.production_id)
                if item is not None and item.has_stats:
                    val, _ = effective_stat_value(
                        item, cr, self.catalog, self.model.enchant_target_level
                    )
                    esv_values.append(val)
        esv_range = (
            (min(esv_values), max(esv_values)) if len(esv_values) >= 2 else None
        )

        results = []
        for pos in ms.positions:
            pid = pos.production_id
            item = self.catalog.get(pid)
            cf = self.model.category_factor.get("default", 1.0)
            if dept.kind == "enchant" and item is not None:
                cf = enchant_category_factor(self.model, item.name)

            override = self.model.manual_override.get(pid)
            if override is not None:
                self._used_override_ids.add(pid)

            esv = None
            fallback = False
            if dept.stat_based:
                if item is not None and item.has_stats:
                    esv, fb = effective_stat_value(
                        item, cr, self.catalog, self.model.enchant_target_level
                    )
                    fallback = fb
                else:
                    fallback = True

            if esv is not None and esv_range is not None:
                res = self.engine.compute_with_stats(
                    pid,
                    role=dept.role,
                    category_factor=cf,
                    esv=esv,
                    esv_range=esv_range,
                    k=dept.k,
                    override=override,
                    fallback_no_stats=fallback,
                )
            else:
                res = self.engine.compute_price(
                    pid,
                    role=dept.role,
                    category_factor=cf,
                    override=override,
                    fallback_no_stats=fallback,
                )
            results.append(res)

        # Upgrade_Chain strict-increase post-pass for internal chains
        # (Requirement 6.1). Endpoints already exceed their parents.
        self._apply_chain_increase(ms, cr, results)
        return results

    def _apply_chain_increase(self, ms, cr, results):
        if not cr.by_production:
            return
        try:
            topo = cr.topological_order()
        except ChainError:
            # Cyclic/unresolvable line: leave the independent Grade_Floor prices
            # (already computed above), which is the rollback behaviour
            # (Requirement 6.5).
            return
        step = self.model.category_factor.get("upgrade_chain_step", 1.4)
        pid2idx = {}
        for idx, pos in enumerate(ms.positions):
            pid2idx.setdefault(pos.production_id, idx)
        for pid in topo:
            parent = cr.predecessor.get(pid)
            if (
                parent is None
                or parent not in cr.by_production
                or parent == pid
                or parent not in pid2idx
                or pid not in pid2idx
            ):
                continue
            child = results[pid2idx[pid]]
            parent_price = results[pid2idx[parent]].buy_price
            target = int(math.ceil(parent_price * step))
            if child.buy_price < target:
                child.buy_price = target
            if child.buy_price <= parent_price:
                child.buy_price = parent_price + 1

    # ---- full run (Requirement 11.1, 12.6) ----------------------------
    def run(self, dry_run=True):
        for dept in DEPARTMENT_MAP:
            entry = self.report.new_entry(dept.msid)
            try:
                self._process_dept(dept, entry, dry_run)
            except Exception as exc:  # isolate per-file errors (Req 12.6)
                entry.notes.append("processing error: %s" % exc)
        self._finalise_unused_overrides()
        return self.report

    def _process_dept(self, dept, entry, dry_run):
        prefer_custom = dept.msid in CUSTOM_OVERRIDE_DEPARTMENTS
        ms = self.loader.load_by_id(dept.msid, prefer_custom=prefer_custom)

        # Custom_Override department without a custom file: uncovered, never
        # touch the root file (Requirement 9.5).
        if prefer_custom:
            target = resolve_target_path(
                dept.msid, multisell_dir(), custom_multisell_dir()
            )
            if target is None:
                entry.notes.append("custom file missing; not writing root")
                if ms is not None:
                    for pos in ms.positions:
                        ReportBuilder.record_uncovered(
                            entry, pos.production_id, "(no custom file)"
                        )
                return

        if ms is None:
            ReportBuilder.record_unread(entry, "%d.xml" % dept.msid)
            entry.notes.append("multisell not found/unreadable")
            return

        # Resolve every id against the catalog (Requirement 12.3).
        unresolved = self.validator.resolve_ids(ms, self.catalog)
        if unresolved:
            ReportBuilder.record_unresolved(entry, unresolved)

        results = self.price_multisell(ms, dept)
        for pos, res in zip(ms.positions, results):
            ReportBuilder.record_price(entry, pos.production_id, res)

        # Build the new file text and verify structure before writing.
        target_path = (
            resolve_target_path(dept.msid, multisell_dir(), custom_multisell_dir())
            if prefer_custom
            else os.path.join(multisell_dir(), "%d.xml" % dept.msid)
        )
        with open(ms.path, "r", encoding="utf-8") as fh:
            original_text = fh.read()
        prices = [r.buy_price for r in results]
        new_text = self.writer.build_text(original_text, prices)
        violations = self.writer.verify_structure(original_text, new_text)
        if violations:
            entry.notes.append("structure violation, write skipped: %s" % violations)
            return

        if dry_run:
            # Validate the produced text without writing (Requirement 12.2).
            ok, msg = self.validator.validate_text(new_text)
            if not ok:
                entry.notes.append("dry-run schema validation failed: %s" % msg)
            return

        written, msg = self.validator.transactional_write(target_path, new_text)
        if not written:
            entry.notes.append("write skipped: %s" % msg)

    def _finalise_unused_overrides(self):
        """Record overrides referencing ids absent from all multisells (8.7)."""
        unused = []
        for pid, price in self.model.manual_override.items():
            if pid not in self._used_override_ids:
                unused.append({"production_id": pid, "buy_price": price})
        self.report.add_unused_overrides(unused)


def default_paths():
    here = os.path.dirname(os.path.abspath(__file__))
    tool_root = os.path.normpath(os.path.join(here, ".."))
    return (
        os.path.join(tool_root, "price_model.yaml"),
        os.path.join(tool_root, "manual_overrides.yaml"),
    )


def main(argv=None):
    import argparse

    model_path, overrides_path = default_paths()
    parser = argparse.ArgumentParser(description="Alt+B price balance generator")
    parser.add_argument("--model", default=model_path)
    parser.add_argument("--overrides", default=overrides_path)
    parser.add_argument(
        "--apply",
        action="store_true",
        help="write prices to the real multisell files (default: dry-run)",
    )
    args = parser.parse_args(argv)

    orch = Orchestrator(args.model, args.overrides)
    report = orch.run(dry_run=not args.apply)
    print(report.render())
    return 0 if report.is_success() else 1


if __name__ == "__main__":
    raise SystemExit(main())
