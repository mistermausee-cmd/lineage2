"""Unit tests for CatalogLoader (Task 2.2)."""

import textwrap

import xml.etree.ElementTree as ET

from price_generator.catalog_loader import parse_item_element


def _item(xml):
    return parse_item_element(ET.fromstring(textwrap.dedent(xml)))


def test_resolves_grade_price_stats():
    ci = _item(
        """
        <item id="19938" name="Requiem Shaper" type="Weapon">
            <set name="crystal_type" val="R" />
            <set name="price" val="73249258" />
            <stats>
                <stat type="pAtk">380</stat>
                <stat type="mAtk">189</stat>
            </stats>
        </item>
        """
    )
    assert ci.id == 19938
    assert ci.grade == "R"
    assert ci.price == 73249258
    assert ci.stats["pAtk"] == 380.0
    assert ci.stats["mAtk"] == 189.0
    assert ci.has_stats


def test_missing_price_is_none():
    ci = _item(
        """
        <item id="1000" name="No Price" type="EtcItem">
            <set name="crystal_type" val="D" />
        </item>
        """
    )
    assert ci.price is None
    assert ci.grade == "D"


def test_missing_stats_is_empty():
    ci = _item(
        """
        <item id="1001" name="No Stats" type="EtcItem">
            <set name="price" val="500" />
        </item>
        """
    )
    assert ci.stats == {}
    assert not ci.has_stats
    # crystal_type absent -> NG.
    assert ci.grade == "NG"


def test_float_stat_parsed():
    ci = _item(
        """
        <item id="1002" name="Frac" type="Weapon">
            <set name="crystal_type" val="S" />
            <set name="price" val="10" />
            <stats>
                <stat type="accCombat">-3.75</stat>
            </stats>
        </item>
        """
    )
    assert ci.stats["accCombat"] == -3.75
