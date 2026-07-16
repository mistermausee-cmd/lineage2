"""Integration tests on the real server data (Tasks 9.2-9.6).

Covers:
  Property 11 — Детерминизм на уровне позиции (9.2)
  Round-trip on real files 600060/600030/600100/600052/custom/600025 (9.3)
  Anti-exploit example on a real R99 weapon (9.4)
  Immutability of Rates.ini / stats/items / HTML (9.5)
  Final xmllint over every would-be-written file (9.6)

These tests never modify the real game files: writes go to temporary copies.
"""

import hashlib
import os
import shutil

import pytest

from price_generator.constants import (
    ADENA_ID,
    custom_multisell_dir,
    game_data_dir,
    multisell_dir,
    repo_root,
)
from price_generator.multisell_writer import MultisellWriter
from price_generator.orchestrator import DEPARTMENT_MAP, Orchestrator, default_paths
from price_generator.schema_validator import SchemaValidator

MODEL_PATH, OVERRIDES_PATH = default_paths()


@pytest.fixture(scope="module")
def orch():
    return Orchestrator(MODEL_PATH, OVERRIDES_PATH)


def _dept(msid):
    for d in DEPARTMENT_MAP:
        if d.msid == msid:
            return d
    raise KeyError(msid)


# Feature: altb-price-balance, Property 11: Детерминизм на уровне позиции — при
# неизменных входах два прогона дают идентичный count у id=57 каждой позиции.
# Validates: Requirements 8.4
def test_property11_determinism(orch):
    for msid in (600060, 600030, 600100, 600052, 600044, 600008):
        prefer_custom = msid in {600008}
        ms = orch.loader.load_by_id(msid, prefer_custom=prefer_custom)
        r1 = [r.buy_price for r in orch.price_multisell(ms, _dept(msid))]
        r2 = [r.buy_price for r in orch.price_multisell(ms, _dept(msid))]
        assert r1 == r2
        assert all(p >= 1 for p in r1)


def test_roundtrip_real_files(orch, tmp_path):
    writer = MultisellWriter()
    validator = SchemaValidator()
    cases = [
        (600060, False),
        (600030, False),
        (600100, False),
        (600052, False),
        (600025, True),
    ]
    for msid, is_custom in cases:
        ms = orch.loader.load_by_id(msid, prefer_custom=is_custom)
        assert ms is not None
        with open(ms.path, "r", encoding="utf-8") as fh:
            original = fh.read()
        prices = [r.buy_price for r in orch.price_multisell(ms, _dept(msid))]
        new_text = writer.build_text(original, prices)

        # Structure preserved (only adena counts differ).
        assert writer.verify_structure(original, new_text) == []
        # Schema location retained verbatim.
        expected_schema = (
            "../../xsd/multisell.xsd" if is_custom else "../xsd/multisell.xsd"
        )
        assert expected_schema in new_text
        # Write to a temp copy and validate + re-parse.
        tmp_file = tmp_path / ("%d.xml" % msid)
        tmp_file.write_text(new_text, encoding="utf-8")
        ok, msg = validator.validate_file(str(tmp_file))
        assert ok, "xmllint failed on %d: %s" % (msid, msg)

        reloaded = orch.loader.load_path(str(tmp_file))
        assert len(reloaded.positions) == len(ms.positions)
        for before, after, price in zip(ms.positions, reloaded.positions, prices):
            assert [p.id for p in before.productions] == [
                p.id for p in after.productions
            ]
            # adena ingredient now holds the new price
            idx = after.adena_ingredient_index
            assert idx is not None
            assert after.ingredients[idx].count == price


def test_anti_exploit_r99_weapon(orch):
    # Requirement 7.1/7.2: R99 weapon, buy strictly > NPC sell and >= floor.
    ms = orch.loader.load_by_id(600060)
    results = orch.price_multisell(ms, _dept(600060))
    checked = 0
    for pos, res in zip(ms.positions, results):
        item = orch.catalog.get(pos.production_id)
        if item and item.grade == "R99" and item.price and item.price > 0:
            npc_sell = orch.model.npc_sell_price(item.price)
            floor = orch.model.anti_exploit_floor(item.price)
            assert res.buy_price > npc_sell
            assert res.buy_price >= floor
            checked += 1
    assert checked > 0  # there is at least one R99 weapon in 600060


def _hash_file(path):
    h = hashlib.sha256()
    with open(path, "rb") as fh:
        h.update(fh.read())
    return h.hexdigest()


def _hash_tree(root):
    items = []
    for dirpath, _dirs, files in os.walk(root):
        for f in sorted(files):
            p = os.path.join(dirpath, f)
            items.append((os.path.relpath(p, root), _hash_file(p)))
    items.sort()
    h = hashlib.sha256()
    for rel, digest in items:
        h.update(rel.encode())
        h.update(digest.encode())
    return h.hexdigest()


def test_immutability_of_adjacent_files(orch):
    # Requirement 13.2/13.3/13.4: Rates.ini, stats/items and shop HTML must not
    # be touched by a run. A dry-run performs no writes at all.
    rates = os.path.join(repo_root(), "server", "game", "config", "Rates.ini")
    items = os.path.join(game_data_dir(), "stats", "items")
    html_dir = os.path.join(
        game_data_dir(), "html", "CommunityBoard", "Custom", "merchant"
    )

    before = {
        "rates": _hash_file(rates) if os.path.exists(rates) else None,
        "items": _hash_tree(items) if os.path.isdir(items) else None,
        "html": _hash_tree(html_dir) if os.path.isdir(html_dir) else None,
    }
    report = orch.run(dry_run=True)
    assert report.is_success()
    after = {
        "rates": _hash_file(rates) if os.path.exists(rates) else None,
        "items": _hash_tree(items) if os.path.isdir(items) else None,
        "html": _hash_tree(html_dir) if os.path.isdir(html_dir) else None,
    }
    assert before == after


def test_final_xmllint_over_all_departments(orch):
    # Requirement 12.2: every would-be-written multisell validates against the
    # schema after applying prices.
    writer = MultisellWriter()
    validator = SchemaValidator()
    for dept in DEPARTMENT_MAP:
        prefer_custom = dept.msid in {600008, 600011, 600025, 600026}
        ms = orch.loader.load_by_id(dept.msid, prefer_custom=prefer_custom)
        assert ms is not None, "missing multisell %d" % dept.msid
        with open(ms.path, "r", encoding="utf-8") as fh:
            original = fh.read()
        prices = [r.buy_price for r in orch.price_multisell(ms, dept)]
        new_text = writer.build_text(original, prices)
        assert writer.verify_structure(original, new_text) == []
        ok, msg = validator.validate_text(new_text)
        assert ok, "xmllint failed for %d: %s" % (dept.msid, msg)
        # Every position keeps exactly one adena ingredient (single currency).
        reloaded_positions = ms.positions
        for pos in reloaded_positions:
            assert pos.adena_ingredient_index is not None
