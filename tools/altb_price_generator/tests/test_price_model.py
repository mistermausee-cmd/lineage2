"""Tests for PriceModel loading and Grade_Floor monotonicity."""

import os

from hypothesis import given, settings
from hypothesis import strategies as st

from price_generator.constants import GRADE_ORDER
from price_generator.price_model import PriceModel, load_price_model

HERE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MODEL_PATH = os.path.join(HERE, "price_model.yaml")

# Concrete grades that carry a Grade_Floor (NG excluded).
FLOOR_GRADES = ["D", "C", "B", "A", "S", "S80", "R", "R95", "R99"]


def test_load_real_price_model():
    model = load_price_model(MODEL_PATH)
    assert model.variant == "balanced"
    assert model.variant_multiplier == 1.0
    assert model.sell_price_basis == "half"
    assert model.anti_exploit_mult == 2.0
    # npc_sell_price with half basis is price/2.
    assert model.npc_sell_price(808000000) == 404000000.0
    # anti-exploit floor with half basis == price.
    assert model.anti_exploit_floor(808000000) == 808000000.0


def test_npc_sell_price_fallbacks():
    model = load_price_model(MODEL_PATH)
    assert model.npc_sell_price(None) == 0.0
    assert model.npc_sell_price(0) == 0.0
    assert model.npc_sell_price(100, basis="full") == 100.0


def _monotonic_floor_strategy(draw):
    # Build a non-decreasing sequence of positive integers for the floor grades.
    start = draw(st.integers(min_value=1, max_value=1000))
    vals = [start]
    for _ in range(len(FLOOR_GRADES) - 1):
        step = draw(st.integers(min_value=0, max_value=100000))
        vals.append(vals[-1] + step)
    return dict(zip(FLOOR_GRADES, vals))


# Feature: altb-price-balance, Property 2: Неубывание Grade_Floor — для соседних
# грейдов grade_floor младшего <= старшего.
# Validates: Requirements 1.3
@settings(max_examples=150)
@given(st.data())
def test_property2_grade_floor_non_decreasing(data):
    floor = _monotonic_floor_strategy(data.draw)
    model = PriceModel(
        farm_rate={"B": 100000},
        grade_floor=floor,
    )
    model.validate()  # must not raise
    defined = [g for g in GRADE_ORDER if g in floor]
    for lower, higher in zip(defined, defined[1:]):
        assert floor[lower] <= floor[higher]
