"""Property tests for grade/role/enchant/stat differentiation.

Tasks 4.7 (P1), 4.8 (P3), 4.9 (P4), 4.10 (P6), 4.11 (P7).

The differentiation guarantees concern the pricing model's shape, so prices are
kept in the base-dominated regime (small NPC price) to isolate the factor under
test; the anti-exploit floor is validated separately by Property 9.
"""

from hypothesis import assume, given, settings
from hypothesis import strategies as st

from price_generator.constants import GRADE_ORDER
from price_generator.differentiation import (
    enchant_category_factor,
    enchant_kind_rank,
    role_class_rank,
)
from price_generator.models import CatalogItem
from price_generator.price_engine import PriceEngine
from tests.strategies import FakeCatalog, make_model

FLOOR_GRADES = ["D", "C", "B", "A", "S", "S80", "R", "R95", "R99"]


def _engine_with(items):
    model = make_model()
    catalog = FakeCatalog({it.id: it for it in items})
    return PriceEngine(model, catalog), model


# Feature: altb-price-balance, Property 1: Монотонность цены по грейду для
# сопоставимой роли.
# Validates: Requirements 1.5, 3.1, 3.2, 3.4, 3.5, 5.5
@settings(max_examples=150)
@given(i=st.integers(min_value=0, max_value=len(FLOOR_GRADES) - 2),
       role=st.sampled_from(["weapon", "armor", "jewelry", "accessory"]))
def test_property1_grade_monotonicity(i, role):
    lower_g, higher_g = FLOOR_GRADES[i], FLOOR_GRADES[i + 1]
    # Comparable role/category/stats; base-dominated (price 0 -> no floor).
    low = CatalogItem(id=1, grade=lower_g, price=0, stats={}, item_type="Weapon")
    high = CatalogItem(id=2, grade=higher_g, price=0, stats={}, item_type="Weapon")
    engine, _ = _engine_with([low, high])
    p_low = engine.compute_price(1, role=role).buy_price
    p_high = engine.compute_price(2, role=role).buy_price
    assert p_high > p_low


# Feature: altb-price-balance, Property 3: Строгий порядок цен по роли внутри стадии.
# Validates: Requirements 2.5, 2.6
@settings(max_examples=150)
@given(grade=st.sampled_from(FLOOR_GRADES),
       r1=st.sampled_from(["consumable", "weapon", "armor", "prestige"]),
       r2=st.sampled_from(["consumable", "weapon", "armor", "prestige"]))
def test_property3_role_order_within_stage(grade, r1, r2):
    assume(role_class_rank(r1) != role_class_rank(r2))
    item = CatalogItem(id=1, grade=grade, price=0, stats={}, item_type="EtcItem")
    engine, _ = _engine_with([item])
    p1 = engine.compute_price(1, role=r1).buy_price
    p2 = engine.compute_price(1, role=r2).buy_price
    if role_class_rank(r1) < role_class_rank(r2):
        assert p1 < p2
    else:
        assert p1 > p2


# Feature: altb-price-balance, Property 4: Монотонность цены бижи по
# Effective_Stat_Value.
# Validates: Requirements 4.1, 4.2, 4.4
@settings(max_examples=150)
@given(esv_a=st.floats(min_value=0.0, max_value=10000.0),
       esv_b=st.floats(min_value=0.0, max_value=10000.0))
def test_property4_jewelry_monotonic_by_esv(esv_a, esv_b):
    assume(abs(esv_a - esv_b) > 1e-6)
    item = CatalogItem(id=1, grade="R", price=0, stats={"maxHp": 1}, item_type="EtcItem")
    engine, _ = _engine_with([item])
    lo, hi = min(esv_a, esv_b), max(esv_a, esv_b)
    rng = (lo, hi)
    pa = engine.compute_with_stats(1, role="jewelry", esv=esv_a, esv_range=rng, k=2.0).buy_price
    pb = engine.compute_with_stats(1, role="jewelry", esv=esv_b, esv_range=rng, k=2.0).buy_price
    if esv_a > esv_b:
        assert pa > pb
    else:
        assert pb > pa


# Feature: altb-price-balance, Property 6: Упорядочение заточки по типу свитка/камня.
# Validates: Requirements 5.1, 5.6
@settings(max_examples=150)
@given(grade=st.sampled_from(FLOOR_GRADES))
def test_property6_enchant_kind_order(grade):
    names = {
        "common": "Scroll: Enchant Weapon",
        "blessed": "Blessed Scroll: Enchant Weapon",
        "sacred": "Sacred Scroll: Enchant Weapon",
    }
    item = CatalogItem(id=1, grade=grade, price=0, stats={}, item_type="EtcItem")
    engine, model = _engine_with([item])
    prices = {}
    for kind, name in names.items():
        cf = enchant_category_factor(model, name)
        prices[kind] = engine.compute_price(1, role="enchant", category_factor=cf).buy_price
    assert prices["common"] < prices["blessed"] < prices["sacred"]
    assert enchant_kind_rank(names["common"]) < enchant_kind_rank(names["blessed"]) \
        < enchant_kind_rank(names["sacred"])


# Feature: altb-price-balance, Property 7: Заточка оружия не дешевле заточки брони.
# Validates: Requirements 5.3
@settings(max_examples=150)
@given(grade=st.sampled_from(FLOOR_GRADES),
       kind=st.sampled_from(["", "Blessed ", "Sacred "]))
def test_property7_weapon_enchant_not_cheaper_than_armor(grade, kind):
    weapon_name = "%sScroll: Enchant Weapon" % kind
    armor_name = "%sScroll: Enchant Armor" % kind
    item = CatalogItem(id=1, grade=grade, price=0, stats={}, item_type="EtcItem")
    engine, model = _engine_with([item])
    pw = engine.compute_price(
        1, role="enchant", category_factor=enchant_category_factor(model, weapon_name)
    ).buy_price
    pa = engine.compute_price(
        1, role="enchant", category_factor=enchant_category_factor(model, armor_name)
    ).buy_price
    assert pw >= pa
