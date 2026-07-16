"""Unit tests for ChainResolver (Task 3.2)."""

import pytest

from price_generator.chain_resolver import ChainError, ChainResolver
from price_generator.models import Ingredient, MultisellPosition, Production


def _pos(prod_id, ingredient_ids):
    return MultisellPosition(
        productions=[Production(id=prod_id, count=1)],
        ingredients=[Ingredient(id=i, count=1) for i in ingredient_ids],
    )


def test_linear_chain_order_and_endpoint():
    # base 100 -> 200 -> 300 (each buys with adena + previous item).
    positions = [
        _pos(100, [57]),
        _pos(200, [100, 57]),
        _pos(300, [200, 57]),
    ]
    cr = ChainResolver(positions)
    order = cr.topological_order()
    assert order.index(100) < order.index(200) < order.index(300)
    assert cr.chain_depth(100) == 0
    assert cr.chain_depth(200) == 1
    assert cr.chain_depth(300) == 2
    assert cr.is_endpoint(300)
    assert not cr.is_endpoint(100)
    assert cr.endpoints() == [300]
    assert not cr.has_cycle()


def test_cycle_detected():
    # 100 -> 200 -> 100 forms a cycle.
    positions = [
        _pos(100, [200, 57]),
        _pos(200, [100, 57]),
    ]
    cr = ChainResolver(positions)
    assert cr.has_cycle()
    with pytest.raises(ChainError):
        cr.topological_order()


def test_non_chain_positions_are_depth_zero():
    positions = [_pos(100, [57]), _pos(200, [57])]
    cr = ChainResolver(positions)
    assert cr.chain_depth(100) == 0
    assert cr.chain_depth(200) == 0
    assert not cr.is_chain_link(100)
