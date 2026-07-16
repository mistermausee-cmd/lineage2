"""Property and unit tests for MultisellWriter + custom routing.

Covers:
  Property 8  — Сохранение ассортимента и структуры (round-trip/idempotency)
  Property 12 — Запись перекрываемых отделов только в custom/
  Property 13 — Корректность пути к схеме по расположению файла
  Property 14 — Единственность валюты
"""

import os
import xml.etree.ElementTree as ET

from hypothesis import given, settings
from hypothesis import strategies as st

from price_generator.constants import (
    ADENA_ID,
    CUSTOM_OVERRIDE_DEPARTMENTS,
    SCHEMA_LOCATION_CUSTOM,
    SCHEMA_LOCATION_ROOT,
)
from price_generator.multisell_writer import (
    MultisellWriter,
    expected_schema_location,
    resolve_target_path,
)

SETTINGS = settings(max_examples=150, deadline=None)


# ---- multisell XML text generator ------------------------------------
@st.composite
def multisell_text(draw, is_custom=False):
    """Generate a realistic multisell file text with an adena ingredient in
    every item, optional single non-currency base item and inline comments."""
    n_items = draw(st.integers(min_value=1, max_value=8))
    schema = SCHEMA_LOCATION_CUSTOM if is_custom else SCHEMA_LOCATION_ROOT
    lines = ['<?xml version="1.0" encoding="UTF-8"?>']
    lines.append(
        '<list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" '
        'xsi:noNamespaceSchemaLocation="%s">' % schema
    )
    lines.append("\t<npcs>")
    lines.append("\t\t<npc>-1</npc>")
    lines.append("\t</npcs>")
    for _ in range(n_items):
        count = draw(st.integers(min_value=1, max_value=2_000_000_000))
        prod_id = draw(st.integers(min_value=100, max_value=99999))
        has_base = draw(st.booleans())
        adena_first = draw(st.booleans())
        reversed_attrs = draw(st.booleans())
        lines.append("\t<item>")
        base_line = None
        if has_base:
            base_id = draw(st.integers(min_value=100, max_value=99999))
            base_line = '\t\t<ingredient id="%d" count="1" />' % base_id
        if reversed_attrs:
            adena_line = '\t\t<ingredient count="%d" id="57" />' % count
        else:
            adena_line = '\t\t<ingredient id="57" count="%d" />' % count
        if has_base and adena_first:
            lines.append(adena_line)
            lines.append(base_line)
        elif has_base:
            lines.append(base_line)
            lines.append(adena_line)
        else:
            lines.append(adena_line)
        lines.append('\t\t<production id="%d" count="1" /> <!-- item -->' % prod_id)
        lines.append("\t</item>")
    lines.append("</list>")
    return "\n".join(lines) + "\n"


def _signature(text):
    """(npcs, [(productions, non_adena_ings, adena_count_or_None)])."""
    root = ET.fromstring(text)
    npcs = ET.tostring(root.find("npcs"), encoding="unicode")
    items = []
    for item in root.findall("item"):
        prods = [(int(p.get("id")), int(p.get("count"))) for p in item.findall("production")]
        non_adena = []
        adena = None
        for ing in item.findall("ingredient"):
            iid = int(ing.get("id"))
            if iid == ADENA_ID:
                adena = int(ing.get("count"))
            else:
                non_adena.append((iid, int(ing.get("count"))))
        items.append((tuple(prods), tuple(non_adena), adena))
    return npcs, items


# Feature: altb-price-balance, Property 8: Сохранение ассортимента и структуры
# (round-trip/идемпотентность) — повторное чтение результата даёт тот же
# упорядоченный набор production-id, те же count, ту же последовательность
# прочих ingredient-id и их count, то же число <item> и дословно тот же <npcs>;
# отличаться может только count у ingredient id="57".
# Validates: Requirements 6.2, 6.3, 10.1, 10.3, 10.4, 13.1, 13.6
@given(text=multisell_text(), seed=st.data())
@SETTINGS
def test_property8_structure_preserved(text, seed):
    writer = MultisellWriter()
    n_items = text.count("<item>")
    new_prices = [
        seed.draw(st.integers(min_value=1, max_value=3_000_000_000))
        for _ in range(n_items)
    ]
    new_text = writer.build_text(text, new_prices)

    before = _signature(text)
    after = _signature(new_text)
    # <npcs> verbatim, item count, productions and non-adena ingredients equal.
    assert before[0] == after[0]
    assert len(before[1]) == len(after[1])
    for (pb, ib, _ab), (pa, ia, aa_new), price in zip(
        before[1], after[1], new_prices
    ):
        assert pb == pa            # productions unchanged
        assert ib == ia            # non-adena ingredients unchanged
        assert aa_new == price     # only the adena count changed
    # verify_structure agrees the write is safe.
    assert writer.verify_structure(text, new_text) == []


# Feature: altb-price-balance, Property 8 (idempotency): applying the same
# prices twice yields identical text.
@given(text=multisell_text(), seed=st.data())
@SETTINGS
def test_property8_idempotent(text, seed):
    writer = MultisellWriter()
    n_items = text.count("<item>")
    prices = [
        seed.draw(st.integers(min_value=1, max_value=3_000_000_000))
        for _ in range(n_items)
    ]
    once = writer.build_text(text, prices)
    twice = writer.build_text(once, prices)
    assert once == twice


# Feature: altb-price-balance, Property 12: Запись перекрываемых отделов только
# в custom/ — для отделов {600008,600011,600025,600026} запись идёт в custom/,
# верхний файл не трогается.
# Validates: Requirements 9.1, 9.2
@given(msid=st.integers(min_value=600001, max_value=600120))
@SETTINGS
def test_property12_custom_routing(msid, tmp_path_factory):
    base = tmp_path_factory.mktemp("ms")
    root_dir = os.path.join(str(base), "multisell")
    custom_dir = os.path.join(root_dir, "custom")
    os.makedirs(custom_dir, exist_ok=True)
    fname = "%d.xml" % msid
    # Create both root and custom copies.
    with open(os.path.join(root_dir, fname), "w") as fh:
        fh.write("<list/>")
    with open(os.path.join(custom_dir, fname), "w") as fh:
        fh.write("<list/>")

    target = resolve_target_path(msid, root_dir, custom_dir)
    if msid in CUSTOM_OVERRIDE_DEPARTMENTS:
        assert target == os.path.join(custom_dir, fname)
        assert "custom" in target
        assert target != os.path.join(root_dir, fname)
    else:
        assert target == os.path.join(root_dir, fname)


# Feature: altb-price-balance, Property 12 (missing custom): a custom-override
# department without a custom file must NOT fall back to the root file.
@given(msid=st.sampled_from(sorted(CUSTOM_OVERRIDE_DEPARTMENTS)))
@SETTINGS
def test_property12_missing_custom_no_root_write(msid, tmp_path_factory):
    base = tmp_path_factory.mktemp("ms2")
    root_dir = os.path.join(str(base), "multisell")
    custom_dir = os.path.join(root_dir, "custom")
    os.makedirs(custom_dir, exist_ok=True)
    with open(os.path.join(root_dir, "%d.xml" % msid), "w") as fh:
        fh.write("<list/>")
    # No custom file created.
    target = resolve_target_path(msid, root_dir, custom_dir)
    assert target is None


# Feature: altb-price-balance, Property 13: Корректность пути к схеме по
# расположению файла — ../../xsd для custom/, ../xsd для корня; writer сохраняет
# путь схемы дословно.
# Validates: Requirements 9.3
@given(is_custom=st.booleans(), seed=st.data())
@SETTINGS
def test_property13_schema_location(is_custom, seed):
    expected = SCHEMA_LOCATION_CUSTOM if is_custom else SCHEMA_LOCATION_ROOT
    assert expected_schema_location(is_custom) == expected

    text = seed.draw(multisell_text(is_custom=is_custom))
    writer = MultisellWriter()
    n_items = text.count("<item>")
    prices = [seed.draw(st.integers(min_value=1, max_value=10 ** 9)) for _ in range(n_items)]
    new_text = writer.build_text(text, prices)
    assert expected in new_text
    other = SCHEMA_LOCATION_ROOT if is_custom else SCHEMA_LOCATION_CUSTOM
    # The wrong location string must not appear.
    assert ('="%s"' % other) not in new_text


# Feature: altb-price-balance, Property 14: Единственность валюты — валютным
# ingredient является ровно id="57"; допускается не более одного невалютного
# ingredient (базовый предмет, count=1).
# Validates: Requirements 10.5
@given(text=multisell_text(), seed=st.data())
@SETTINGS
def test_property14_single_currency(text, seed):
    writer = MultisellWriter()
    n_items = text.count("<item>")
    prices = [seed.draw(st.integers(min_value=1, max_value=10 ** 9)) for _ in range(n_items)]
    new_text = writer.build_text(text, prices)
    root = ET.fromstring(new_text)
    for item in root.findall("item"):
        currency = [i for i in item.findall("ingredient") if int(i.get("id")) == ADENA_ID]
        non_currency = [i for i in item.findall("ingredient") if int(i.get("id")) != ADENA_ID]
        assert len(currency) == 1                    # exactly one currency
        assert len(non_currency) <= 1                # at most one base item
        for nc in non_currency:
            assert int(nc.get("count")) == 1         # base item count = 1


# ---- unit tests ------------------------------------------------------
def test_verify_structure_detects_item_removal():
    writer = MultisellWriter()
    text = (
        '<?xml version="1.0" encoding="UTF-8"?>\n'
        '<list xsi:noNamespaceSchemaLocation="../xsd/multisell.xsd" '
        'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
        "\t<npcs><npc>-1</npc></npcs>\n"
        '\t<item><ingredient id="57" count="10" />'
        '<production id="100" count="1" /></item>\n'
        '\t<item><ingredient id="57" count="20" />'
        '<production id="200" count="1" /></item>\n'
        "</list>\n"
    )
    tampered = text.replace(
        '\t<item><ingredient id="57" count="20" />'
        '<production id="200" count="1" /></item>\n',
        "",
    )
    violations = writer.verify_structure(text, tampered)
    assert violations
    assert any("item count changed" in v for v in violations)


def test_writer_changes_only_adena_count_reversed_attrs():
    writer = MultisellWriter()
    text = (
        '<?xml version="1.0" encoding="UTF-8"?>\n'
        '<list xsi:noNamespaceSchemaLocation="../../xsd/multisell.xsd" '
        'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n'
        "\t<npcs><npc>-1</npc></npcs>\n"
        '\t<item><ingredient count="200000000" id="57" />'
        '<production id="300" count="1" /></item>\n'
        "</list>\n"
    )
    new_text = writer.build_text(text, [12345])
    assert 'count="12345" id="57"' in new_text
    assert writer.verify_structure(text, new_text) == []
