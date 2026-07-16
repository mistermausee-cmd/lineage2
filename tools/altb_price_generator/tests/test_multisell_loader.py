"""Round-trip tests for MultisellLoader (Task 2.4)."""

import io

import xml.etree.ElementTree as ET

from price_generator.models import Ingredient, Multisell, MultisellPosition, Production
from price_generator.multisell_loader import parse_position, serialize_multisell


def _pos(xml):
    return parse_position(ET.fromstring(xml))


def test_parse_preserves_order():
    pos = _pos(
        '<item>'
        '<ingredient id="6660" count="1" />'
        '<ingredient id="57" count="1000" />'
        '<production id="36254" count="1" />'
        '</item>'
    )
    assert [i.id for i in pos.ingredients] == [6660, 57]
    assert pos.adena_ingredient_index == 1
    assert pos.production_id == 36254
    assert [i.id for i in pos.non_currency_ingredients] == [6660]


def test_round_trip_preserves_structure():
    ms = Multisell(
        multisell_id=600060,
        path="/tmp/600060.xml",
        positions=[
            MultisellPosition(
                productions=[Production(id=19938, count=1)],
                ingredients=[Ingredient(id=57, count=500000000)],
            ),
            MultisellPosition(
                productions=[Production(id=36254, count=1)],
                ingredients=[Ingredient(id=6660, count=1), Ingredient(id=57, count=999)],
            ),
        ],
        is_custom=False,
    )
    text = serialize_multisell(ms)
    root = ET.parse(io.StringIO(text)).getroot()
    items = root.findall("item")
    assert len(items) == 2
    reparsed = [parse_position(it) for it in items]
    # Same production ids and order.
    assert [p.production_id for p in reparsed] == [19938, 36254]
    # Same ingredient id sequence per position.
    assert [i.id for i in reparsed[0].ingredients] == [57]
    assert [i.id for i in reparsed[1].ingredients] == [6660, 57]
    # Number of <item> preserved.
    assert len(reparsed) == len(ms.positions)


def test_schema_location_by_location():
    root_ms = Multisell(600060, "/x/600060.xml", [], is_custom=False)
    custom_ms = Multisell(600025, "/x/custom/600025.xml", [], is_custom=True)
    assert "../xsd/multisell.xsd" in serialize_multisell(root_ms)
    assert "../../xsd/multisell.xsd" in serialize_multisell(custom_ms)
