"""Property and unit tests for ReportBuilder + SchemaValidator id resolution.

Covers:
  Property 15 — Полнота покрытия и резолвимость id (total = covered + uncovered,
  каждый id резолвится в каталог).
"""

from hypothesis import given, settings
from hypothesis import strategies as st

from price_generator.models import (
    Ingredient,
    Multisell,
    MultisellPosition,
    Production,
)
from price_generator.price_engine import PriceResult
from price_generator.report_builder import ReportBuilder
from price_generator.schema_validator import SchemaValidator

from .strategies import FakeCatalog, catalog_items

SETTINGS = settings(max_examples=150, deadline=None)


def _fake_result(pid, price, override_applied=False, override_raised=False):
    return PriceResult(
        production_id=pid,
        buy_price=price,
        base=float(price),
        anti_exploit_floor=0.0,
        npc_sell_price=0.0,
        override_applied=override_applied,
        override_raised=override_raised,
    )


# Feature: altb-price-balance, Property 15: Полнота покрытия и резолвимость id —
# каждой позиции назначена цена либо отметка непокрытой (total = covered +
# uncovered), каждый id резолвится в каталог.
# Validates: Requirements 11.1, 11.5, 12.3
@given(
    covered_prices=st.lists(
        st.integers(min_value=1, max_value=5_000_000_000), max_size=20
    ),
    uncovered_ids=st.lists(st.integers(min_value=1, max_value=99999), max_size=10),
)
@SETTINGS
def test_property15_coverage_accounting(covered_prices, uncovered_ids):
    rb = ReportBuilder()
    entry = rb.new_entry(600060)
    for i, price in enumerate(covered_prices):
        ReportBuilder.record_price(entry, 1000 + i, _fake_result(1000 + i, price))
    for uid in uncovered_ids:
        ReportBuilder.record_uncovered(entry, uid, "(test)")

    # total = covered + uncovered for every entry (Requirement 11.5).
    assert entry.total == entry.covered + entry.uncovered
    assert entry.covered == len(covered_prices)
    assert entry.uncovered == len(uncovered_ids)
    # min/max are positive when there is coverage (Requirement 11.2).
    if covered_prices:
        assert entry.min_price is not None and entry.min_price > 0
        assert entry.max_price is not None and entry.max_price >= entry.min_price
    # Success iff no uncovered / violations.
    totals = rb.totals()
    assert totals["total"] == totals["covered"] + totals["uncovered"]
    assert rb.is_success() == (len(uncovered_ids) == 0)


# Feature: altb-price-balance, Property 15 (id resolution): every production and
# ingredient id of a processed multisell resolves to an Item_Catalog definition;
# an unknown id is reported as unresolved.
@given(data=st.data())
@SETTINGS
def test_property15_id_resolution(data):
    # Build a small catalog and a multisell whose ids either resolve or not.
    n = data.draw(st.integers(min_value=1, max_value=6))
    catalog = FakeCatalog()
    positions = []
    expected_unresolved = set()
    for idx in range(n):
        item = data.draw(catalog_items())
        item.id = 1000 + idx  # ensure unique ids (avoid id collisions)
        resolves = data.draw(st.booleans())
        if resolves:
            catalog.add(item)
        else:
            expected_unresolved.add(item.id)
        positions.append(
            MultisellPosition(
                productions=[Production(id=item.id, count=1)],
                ingredients=[Ingredient(id=57, count=100)],
            )
        )
    ms = Multisell(multisell_id=600060, path="x", positions=positions)
    validator = SchemaValidator()
    unresolved = set(validator.resolve_ids(ms, catalog))
    # Adena (57) is never counted as unresolved.
    assert 57 not in unresolved
    assert unresolved == expected_unresolved


def test_unused_override_reporting():
    rb = ReportBuilder()
    rb.add_unused_overrides([{"production_id": 999999, "buy_price": 100}])
    out = rb.render()
    assert "Unused overrides" in out
    assert "999999" in out


def test_override_below_floor_recorded_as_violation():
    rb = ReportBuilder()
    entry = rb.new_entry(600030)
    res = _fake_result(6660, 500, override_raised=True)
    res.override_value = 100
    ReportBuilder.record_price(entry, 6660, res)
    assert entry.anti_exploit_violations
    assert not rb.is_success()
