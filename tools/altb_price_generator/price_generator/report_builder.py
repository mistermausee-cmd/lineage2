"""ReportBuilder — the Price_Report (Task 7.1).

Aggregates per-category (per-multisell) records into a full report: totals,
covered/uncovered counts, min/max Buy_Price, applied Manual_Override, anti-
exploit violations, unused overrides, unresolvable ids and unread files. The run
is successful only when ``uncovered == 0`` and there are no anti-exploit or
structure violations (Requirement 11.5).

Requirements: 2.7, 11.2, 11.3, 11.4, 11.5, 11.6, 5.5, 7.5.
"""

from .models import PriceReportEntry


class ReportBuilder:
    def __init__(self):
        self.entries = []  # list[PriceReportEntry]
        self.global_unused_overrides = []  # overrides referencing absent ids

    def new_entry(self, multisell_id):
        entry = PriceReportEntry(multisell_id=multisell_id)
        self.entries.append(entry)
        return entry

    def add_unused_overrides(self, overrides):
        """overrides: iterable of dicts {production_id, buy_price}."""
        self.global_unused_overrides.extend(overrides)

    # ---- record a single priced position ------------------------------
    @staticmethod
    def record_price(entry, production_id, price_result):
        """Fold one PriceResult into an entry, updating coverage & ranges."""
        entry.total += 1
        entry.covered += 1
        p = price_result.buy_price
        entry.min_price = p if entry.min_price is None else min(entry.min_price, p)
        entry.max_price = p if entry.max_price is None else max(entry.max_price, p)
        if price_result.override_applied:
            entry.applied_overrides.append(
                {"production_id": production_id, "buy_price": p}
            )
        if price_result.override_raised:
            # Override provided but bumped by the anti-exploit floor
            # (Requirement 8.6) — recorded as a violation of the override.
            entry.anti_exploit_violations.append(
                {
                    "production_id": production_id,
                    "override": price_result.override_value,
                    "applied": p,
                    "npc_sell_price": price_result.npc_sell_price,
                    "reason": "override below anti-exploit floor; raised",
                }
            )

    @staticmethod
    def record_uncovered(entry, production_id, reason=""):
        entry.total += 1
        entry.uncovered += 1
        entry.notes.append("uncovered production %s %s" % (production_id, reason))

    @staticmethod
    def record_unresolved(entry, ids):
        for i in ids:
            if i not in entry.unresolved_ids:
                entry.unresolved_ids.append(i)

    @staticmethod
    def record_unread(entry, path):
        entry.unread_files.append(path)

    # ---- summary (Requirement 11.5) -----------------------------------
    def totals(self):
        total = sum(e.total for e in self.entries)
        covered = sum(e.covered for e in self.entries)
        uncovered = sum(e.uncovered for e in self.entries)
        return {"total": total, "covered": covered, "uncovered": uncovered}

    def anti_exploit_violation_count(self):
        return sum(len(e.anti_exploit_violations) for e in self.entries)

    def unresolved_count(self):
        return sum(len(e.unresolved_ids) for e in self.entries)

    def unread_count(self):
        return sum(len(e.unread_files) for e in self.entries)

    def is_success(self):
        """Run succeeds only with zero uncovered and no anti-exploit/structure
        violations (Requirement 11.5)."""
        t = self.totals()
        return (
            t["uncovered"] == 0
            and self.anti_exploit_violation_count() == 0
            and self.unresolved_count() == 0
        )

    # ---- rendering -----------------------------------------------------
    def render(self):
        lines = []
        t = self.totals()
        lines.append("==== Price_Report ====")
        lines.append(
            "TOTAL positions: %d | covered: %d | uncovered: %d"
            % (t["total"], t["covered"], t["uncovered"])
        )
        lines.append(
            "anti-exploit violations: %d | unresolved ids: %d | unread files: %d"
            % (
                self.anti_exploit_violation_count(),
                self.unresolved_count(),
                self.unread_count(),
            )
        )
        lines.append(
            "unused overrides (absent id): %d" % len(self.global_unused_overrides)
        )
        lines.append("RUN SUCCESS: %s" % ("YES" if self.is_success() else "NO"))
        lines.append("")
        for e in sorted(self.entries, key=lambda x: x.multisell_id):
            rng = (
                "%s..%s" % (e.min_price, e.max_price)
                if e.min_price is not None
                else "n/a"
            )
            lines.append(
                "  [%d] total=%d covered=%d uncovered=%d price=%s overrides=%d "
                "ae_viol=%d unresolved=%d"
                % (
                    e.multisell_id,
                    e.total,
                    e.covered,
                    e.uncovered,
                    rng,
                    len(e.applied_overrides),
                    len(e.anti_exploit_violations),
                    len(e.unresolved_ids),
                )
            )
            if e.unresolved_ids:
                lines.append("      unresolved ids: %s" % e.unresolved_ids)
            if e.anti_exploit_violations:
                for v in e.anti_exploit_violations:
                    lines.append("      anti-exploit: %s" % v)
            if e.notes:
                for n in e.notes:
                    lines.append("      note: %s" % n)
        if self.global_unused_overrides:
            lines.append("")
            lines.append("Unused overrides (production_id absent in all multisells):")
            for o in self.global_unused_overrides:
                lines.append("  %s" % o)
        return "\n".join(lines)
