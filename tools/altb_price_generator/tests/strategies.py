"""Shared Hypothesis strategies and helpers for Price_Generator tests."""

from hypothesis import strategies as st

from price_generator.constants import GRADE_ORDER
from price_generator.models import CatalogItem
from price_generator.price_model import PriceModel

FLOOR_GRADES = ["D", "C", "B", "A", "S", "S80", "R", "R95", "R99"]

# A calibrated, monotonic model reused by property tests.
BASE_FLOOR = {
    "D": 30000,
    "C": 70000,
    "B": 100000,
    "A": 150000,
    "S": 250000,
    "S80": 400000,
    "R": 2500000,
    "R95": 8000000,
    "R99": 35000000,
}

BASE_ROLE_WEIGHT = {
    "consumable": 0.05,
    "armor": 1.0,
    "jewelry": 1.2,
    "weapon": 3.0,
    "accessory": 1.75,
    "prestige": 12.0,
}


def make_model(**overrides):
    params = dict(
        farm_rate={"B": 100000, "A": 150000, "S": 250000, "S80": 400000,
                   "R": 2500000, "R95": 8000000, "R99": 35000000, "epic": 40000000},
        grade_floor=dict(BASE_FLOOR),
        role_weight=dict(BASE_ROLE_WEIGHT),
        category_factor={
            "default": 1.0,
            "enchant_common": 1.0,
            "enchant_blessed": 3.0,
            "enchant_sacred": 5.0,
            "enchant_weapon": 1.3,
            "upgrade_chain_step": 1.4,
        },
        anti_exploit_mult=2.0,
        sell_price_basis="half",
        variant="balanced",
        variant_multiplier=1.0,
        enchant_target_level=6,
    )
    params.update(overrides)
    return PriceModel(**params)


class FakeCatalog:
    """Minimal in-memory catalog for property tests."""

    def __init__(self, items=None):
        self.items = dict(items or {})

    def add(self, item):
        self.items[item.id] = item
        return item

    def get(self, item_id):
        return self.items.get(item_id)


grades = st.sampled_from(FLOOR_GRADES)
# Prices span the realistic range including 0/None edge cases.
prices = st.one_of(
    st.none(),
    st.integers(min_value=0, max_value=2_000_000_000),
)


@st.composite
def catalog_items(draw, item_id=None):
    iid = item_id if item_id is not None else draw(st.integers(min_value=1, max_value=10_000_000))
    grade = draw(grades)
    price = draw(prices)
    n_stats = draw(st.integers(min_value=0, max_value=5))
    stat_names = ["pAtk", "mAtk", "pDef", "mDef", "maxHp", "maxMp", "critRate"]
    stats = {}
    for i in range(n_stats):
        stats[stat_names[i % len(stat_names)]] = float(
            draw(st.integers(min_value=1, max_value=1000))
        )
    item_type = draw(st.sampled_from(["Weapon", "Armor", "EtcItem"]))
    return CatalogItem(id=iid, grade=grade, price=price, stats=stats, item_type=item_type)
