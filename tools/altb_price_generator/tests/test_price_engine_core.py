"""Property tests for the PriceEngine core (Tasks 4.2, 4.3, 4.5)."""

import math

from hypothesis import given, settings
from hypothesis import strategies as st

from price_generator.price_engine import PriceEngine
from tests.strategies import FakeCatalog, catalog_items, make_model

ROLES = ["consumable", "armor", "jewelry", "weapon", "accessory", "prestige"]


# Feature: altb-price-balance, Property 9: Инвариант анти-эксплойта — Buy_Price
# целое, строго > NPC_Sell_Price и >= anti_exploit_mult * NPC_Sell_Price.
# Validates: Requirements 7.1, 7.2, 8.1
@settings(max_examples=200)
@given(item=catalog_items(item_id=500), role=st.sampled_from(ROLES),
       cf=st.floats(min_value=0.1, max_value=5.0), sr=st.floats(min_value=1.0, max_value=6.0))
def test_property9_anti_exploit_invariant(item, role, cf, sr):
    model = make_model()
    catalog = FakeCatalog({item.id: item})
    engine = PriceEngine(model, catalog)
    res = engine.compute_price(item.id, role=role, category_factor=cf, stat_rank=sr)
    npc_sell = model.npc_sell_price(item.price)
    # Integer, >= 1.
    assert isinstance(res.buy_price, int)
    assert res.buy_price >= 1
    # >= anti_exploit_mult * NPC_Sell_Price.
    assert res.buy_price >= model.anti_exploit_mult * npc_sell
    # Strictly greater than NPC_Sell_Price (when there is a sell price at all).
    if npc_sell > 0:
        assert res.buy_price > npc_sell


# Feature: altb-price-balance, Property 16: Положительная целочисленная цена —
# count у id=57 всегда целое >= 1, дробные округляются вверх.
# Validates: Requirements 12.5
@settings(max_examples=200)
@given(item=catalog_items(item_id=501), role=st.sampled_from(ROLES),
       cf=st.floats(min_value=0.01, max_value=5.0), sr=st.floats(min_value=1.0, max_value=6.0))
def test_property16_positive_integer_price(item, role, cf, sr):
    model = make_model()
    catalog = FakeCatalog({item.id: item})
    engine = PriceEngine(model, catalog)
    res = engine.compute_price(item.id, role=role, category_factor=cf, stat_rank=sr)
    assert isinstance(res.buy_price, int)
    assert res.buy_price >= 1
    # Result is the ceiling of the max of base/floor.
    raw = max(res.base, res.anti_exploit_floor)
    assert res.buy_price == max(1, math.ceil(raw))


# Feature: altb-price-balance, Property 10: Применение валидного Manual_Override —
# при непротиворечащем анти-эксплойту override итоговая Buy_Price равна override.
# Validates: Requirements 8.3
@settings(max_examples=200)
@given(item=catalog_items(item_id=502), role=st.sampled_from(ROLES),
       extra=st.integers(min_value=0, max_value=5_000_000_000))
def test_property10_valid_manual_override_applied(item, role, extra):
    model = make_model()
    catalog = FakeCatalog({item.id: item})
    engine = PriceEngine(model, catalog)
    # A non-violating override is one at or above the anti-exploit floor.
    floor = model.anti_exploit_floor(item.price)
    override = int(math.ceil(floor)) + extra
    if override < 1:
        override = 1
    model.manual_override[item.id] = override
    res = engine.compute_with_override(item.id, role=role)
    assert res.buy_price == override
    assert res.override_applied


def test_violating_override_is_raised_and_flagged():
    # Override below the anti-exploit floor must be bumped up and flagged.
    from price_generator.models import CatalogItem

    model = make_model()
    item = CatalogItem(id=777, grade="R", price=1_000_000, stats={}, item_type="Weapon")
    catalog = FakeCatalog({item.id: item})
    engine = PriceEngine(model, catalog)
    floor = model.anti_exploit_floor(item.price)  # = price (half basis) = 1,000,000
    model.manual_override[item.id] = 10  # far below floor and below npc_sell
    res = engine.compute_with_override(item.id, role="weapon")
    assert res.buy_price >= floor
    assert res.override_raised
    assert not res.override_applied
