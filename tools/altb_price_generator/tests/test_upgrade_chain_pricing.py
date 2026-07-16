"""Property test for progressive Upgrade_Chain pricing (Task 4.13, Property 5)."""

from hypothesis import given, settings
from hypothesis import strategies as st

from price_generator.chain_resolver import ChainResolver
from price_generator.models import CatalogItem, Ingredient, MultisellPosition, Production
from price_generator.price_engine import PriceEngine
from tests.strategies import FakeCatalog, make_model


def _chain_positions(n):
    """A linear chain of n links: root -> ... -> endpoint."""
    positions = []
    positions.append(
        MultisellPosition(
            productions=[Production(id=1000, count=1)],
            ingredients=[Ingredient(id=57, count=1)],
        )
    )
    for i in range(1, n):
        pid = 1000 + i
        base = 1000 + i - 1
        positions.append(
            MultisellPosition(
                productions=[Production(id=pid, count=1)],
                ingredients=[Ingredient(id=base, count=1), Ingredient(id=57, count=1)],
            )
        )
    return positions


# Feature: altb-price-balance, Property 5: Прогрессивное удорожание Upgrade_Chain
# — цена звена N строго больше звена N-1; финальные звенья получают роль престижа.
# Validates: Requirements 3.6, 4.3, 6.1, 6.4
@settings(max_examples=150)
@given(n=st.integers(min_value=2, max_value=7),
       prices=st.lists(st.integers(min_value=0, max_value=500_000_000), min_size=7, max_size=7))
def test_property5_progressive_upgrade_chain(n, prices):
    positions = _chain_positions(n)
    items = {}
    for i in range(n):
        pid = 1000 + i
        items[pid] = CatalogItem(
            id=pid, grade="R", price=prices[i], stats={"pAtk": 100 + i},
            item_type="Weapon",
        )
    model = make_model()
    engine = PriceEngine(model, FakeCatalog(items))
    cr = ChainResolver(positions)
    result = engine.price_upgrade_chain(cr, root_role="weapon")
    assert result is not None
    order = [1000 + i for i in range(n)]
    # Strictly increasing along the chain.
    for a, b in zip(order, order[1:]):
        assert result[b] > result[a]
    # Endpoint is the last link and is detected as an endpoint (prestige role).
    assert cr.is_endpoint(order[-1])
    assert not cr.is_endpoint(order[0])


def test_cyclic_chain_triggers_rollback():
    positions = [
        MultisellPosition(
            productions=[Production(id=1, count=1)],
            ingredients=[Ingredient(id=2, count=1), Ingredient(id=57, count=1)],
        ),
        MultisellPosition(
            productions=[Production(id=2, count=1)],
            ingredients=[Ingredient(id=1, count=1), Ingredient(id=57, count=1)],
        ),
    ]
    model = make_model()
    items = {
        1: CatalogItem(id=1, grade="R", price=1000, stats={}, item_type="Weapon"),
        2: CatalogItem(id=2, grade="R", price=1000, stats={}, item_type="Weapon"),
    }
    engine = PriceEngine(model, FakeCatalog(items))
    cr = ChainResolver(positions)
    assert engine.price_upgrade_chain(cr) is None
    # Rollback yields positive, anti-exploit-safe prices.
    fallback = engine.rollback_chain_prices(cr)
    assert all(p >= 1 for p in fallback.values())
    assert set(fallback.keys()) == {1, 2}
