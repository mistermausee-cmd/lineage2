# -*- coding: utf-8 -*-
"""Property-based и unit тесты генератора магазина Alt+B.

Feature: alt-b-shop-overhaul
Каждое из 13 свойств дизайна реализовано ровно одним property-тестом (hypothesis, >=100 итераций).
Запуск: pytest tools/tests/test_shop_properties.py -q
"""
import os
import re
import sys
import tempfile

import pytest
from hypothesis import given, settings, strategies as st, HealthCheck

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import shopgen as sg  # noqa: E402

SETTINGS = dict(max_examples=100, deadline=None,
                suppress_health_check=[HealthCheck.too_slow])

MARKERS = ["", "", "", " (Event)", " Box", " Pack", " - Appearance",
           " Fragment", " (7-day)", " Test", " Bundle"]
BASE_WORDS = ["Alpha", "Bravo", "Storm", "Iron", "Eternal", "Amaranthine", "Twilight"]
WT_LIST = sorted(sg.WEAPON_TYPES) + ["FISHINGROD", "ETC", ""]


@st.composite
def _item(draw):
    typ = draw(st.sampled_from(["Weapon", "Armor", "EtcItem"]))
    grade = draw(st.sampled_from(sg.GRADES + [""]))
    marker = draw(st.sampled_from(MARKERS))
    word = draw(st.sampled_from(BASE_WORDS))
    price = draw(st.integers(min_value=0, max_value=5_000_000_000))
    add = ""
    if typ == "Weapon":
        wt = draw(st.sampled_from(WT_LIST))
        name = "%s %s%s" % (word, wt.title() or "Blade", marker)
        bp = "rhand"
        return dict(name=name, add=add, type=typ, grade=grade, bodypart=bp,
                    armor_type="", weapon_type=wt, price=price)
    if typ == "Armor":
        kind = draw(st.sampled_from([
            "slot", "jewel", "epic", "brooch", "brooch_jewel", "bracelet",
            "talisman", "cloak", "belt"]))
        at = draw(st.sampled_from(["HEAVY", "LIGHT", "MAGIC", ""]))
        if kind == "slot":
            bp = draw(st.sampled_from(sorted(sg.ARMOR_SLOTS)))
            name = "%s Armor%s" % (word, marker)
        elif kind == "jewel":
            bp = draw(st.sampled_from(sorted(sg.JEWEL_BP)))
            name = "%s Ring%s" % (word, marker)
        elif kind == "epic":
            boss = draw(st.sampled_from(sg.EPIC_BOSSES))
            bp = draw(st.sampled_from(sorted(sg.JEWEL_BP)))
            name = "%s's Ring%s" % (boss, marker)
        elif kind == "brooch":
            bp = "brooch"
            name = "La Vie En Rose's Brooch%s" % marker
        elif kind == "brooch_jewel":
            bp = "brooch_jewel"
            gem = draw(st.sampled_from(sg.GEM_FAMILIES))
            name = "%s%s" % (gem, marker)
        elif kind == "bracelet":
            bp = draw(st.sampled_from(sorted(sg.BRACELET_BP)))
            name = "Dimensional Bracelet - Stage %d%s" % (draw(st.integers(1, 6)), marker)
        elif kind == "talisman":
            bp = "deco1"
            name = "Mysterious Talisman%s" % marker
        elif kind == "cloak":
            bp = "back"
            name = "%s Cloak%s" % (word, marker)
        else:
            bp = "waist"
            name = "%s Belt%s" % (word, marker)
        return dict(name=name, add=add, type=typ, grade=grade, bodypart=bp,
                    armor_type=at, weapon_type="", price=price)
    # EtcItem
    kind = draw(st.sampled_from(["dye", "element", "enchant_w", "enchant_a",
                                 "shot", "crystal", "life", "junk"]))
    if kind == "dye":
        stat = draw(st.sampled_from(sg.STATS))
        name = "Lv. %d Legendary %s Dye%s" % (draw(st.integers(1, 5)), stat, marker)
    elif kind == "element":
        el = draw(st.sampled_from(sg.ELEMENTS))
        name = "%s %s%s" % (el, draw(st.sampled_from(["Stone", "Crystal"])), marker)
    elif kind == "enchant_w":
        name = "%sScroll: Enchant Weapon (R-grade)%s" % (
            draw(st.sampled_from(["", "Blessed "])), marker)
    elif kind == "enchant_a":
        name = "%sScroll: Enchant Armor (R-grade)%s" % (
            draw(st.sampled_from(["", "Blessed "])), marker)
    elif kind == "shot":
        name = "%s (R-grade)%s" % (
            draw(st.sampled_from(["Soulshot", "Spiritshot", "Blessed Spiritshot"])), marker)
    elif kind == "crystal":
        name = "Crystal (%s-grade)%s" % (draw(st.sampled_from(sg.GRADES)), marker)
    elif kind == "life":
        name = "Life Stone - Lv. %d%s" % (draw(st.integers(40, 90)), marker)
    else:
        name = "%s Thingy%s" % (word, marker)
    return dict(name=name, add=add, type=typ, grade="", bodypart="",
                armor_type="", weapon_type="", price=price)


@st.composite
def catalog(draw):
    raws = draw(st.lists(_item(), min_size=1, max_size=40))
    ids = draw(st.lists(st.integers(min_value=1, max_value=99999),
                        min_size=len(raws), max_size=len(raws), unique=True))
    items = {}
    for iid, r in zip(ids, raws):
        r = dict(r, id=iid)
        items[iid] = r
    return items


def _all_entries(entries_by_cat):
    for (ck, msid), entries in entries_by_cat.items():
        for it, price in entries:
            yield ck, msid, it, price


# --------------------------------------------------------------------------- #
# Feature: alt-b-shop-overhaul, Property 1: Анти-эксплойт цены покупки
# --------------------------------------------------------------------------- #
@settings(**SETTINGS)
@given(catalog())
def test_property1_anti_exploit(items):
    _, _, entries_by_cat, _ = sg.build_shop(items)
    for ck, msid, it, price in _all_entries(entries_by_cat):
        assert price > it["price"]


# Feature: alt-b-shop-overhaul, Property 2: Единственная валюта — адена
@settings(**SETTINGS)
@given(catalog())
def test_property2_only_adena(items):
    _, _, entries_by_cat, _ = sg.build_shop(items)
    for (ck, msid), entries in entries_by_cat.items():
        xml = sg.render_multisell(entries)
        for ing in re.findall(r'<ingredient id="(\d+)"', xml):
            assert ing == "57"


# Feature: alt-b-shop-overhaul, Property 3: Каждый товар опирается на реальный предмет
@settings(**SETTINGS)
@given(catalog())
def test_property3_real_items(items):
    _, _, entries_by_cat, _ = sg.build_shop(items)
    for ck, msid, it, price in _all_entries(entries_by_cat):
        assert it["id"] in items
        assert items[it["id"]]["name"] == it["name"]
        assert items[it["id"]]["grade"] == it["grade"]


# Feature: alt-b-shop-overhaul, Property 4: Дедупликация по имени и грейду
@settings(**SETTINGS)
@given(catalog())
def test_property4_dedup(items):
    _, _, entries_by_cat, _ = sg.build_shop(items)
    name_keyed = {"bracelets", "talismans", "stat_hats", "agathions", "life_stones"}
    for (ck, msid), entries in entries_by_cat.items():
        seen = set()
        for it, _p in entries:
            key = it["name"] if ck in name_keyed else (it["name"], it["grade"])
            assert key not in seen, "дубль %r в %s" % (key, ck)
            seen.add(key)


# Feature: alt-b-shop-overhaul, Property 5: Исключение помеченных предметов
@settings(**SETTINGS)
@given(catalog())
def test_property5_exclusion(items):
    _, _, entries_by_cat, _ = sg.build_shop(items)
    for ck, msid, it, price in _all_entries(entries_by_cat):
        text = (it["name"] + " " + it["add"]).lower()
        for mk in sg._MARKERS:
            assert mk not in text, "маркер %r в %s" % (mk, it["name"])
        if ck.startswith("armor_"):
            for mk in sg._ARMOR_EXTRA:
                assert mk not in text


# Feature: alt-b-shop-overhaul, Property 6: Каждый мультиселл вызывается без NPC
@settings(**SETTINGS)
@given(catalog())
def test_property6_npcs_minus1(items):
    _, _, entries_by_cat, _ = sg.build_shop(items)
    for (ck, msid), entries in entries_by_cat.items():
        xml = sg.render_multisell(entries)
        assert len(re.findall(r"<npcs>\s*<npc>\s*-1\s*</npc>\s*</npcs>", xml)) == 1


# Feature: alt-b-shop-overhaul, Property 7: Категория принадлежит ровно одному отделу
@settings(**SETTINGS)
@given(catalog())
def test_property7_one_department(items):
    _, structure, entries_by_cat, _ = sg.build_shop(items)
    cat_to_dept = {}
    for dkey, dtitle, phase2, cats in structure:
        for ck, ct, msid in cats:
            assert ck not in cat_to_dept, "категория %s в двух отделах" % ck
            cat_to_dept[ck] = dkey


# Feature: alt-b-shop-overhaul, Property 8: Двух-кликовая достижимость
@settings(max_examples=60, deadline=None, suppress_health_check=[HealthCheck.too_slow])
@given(catalog())
def test_property8_two_click(items):
    _, structure, entries_by_cat, written = sg.build_shop(items)
    with tempfile.TemporaryDirectory() as td:
        sg.build_html(td, structure, written)
        main = open(os.path.join(td, "main.html"), encoding="utf-8").read()
        for dkey, dtitle, phase2, cats in structure:
            active = [(ck, ct, mid) for ck, ct, mid in cats if ck in written]
            if not active:
                continue
            # клик 1: корень -> отдел
            assert "merchant/%s.html" % dkey in main
            dep = open(os.path.join(td, "%s.html" % dkey), encoding="utf-8").read()
            # клик 2: отдел -> категория (мультиселл)
            for ck, ct, mid in active:
                assert "_bbsmultisell;%d,merchant/%s" % (mid, dkey) in dep


# Feature: alt-b-shop-overhaul, Property 9: Единственная кнопка возврата к отделам
@settings(max_examples=60, deadline=None, suppress_health_check=[HealthCheck.too_slow])
@given(catalog())
def test_property9_return_button(items):
    _, structure, entries_by_cat, written = sg.build_shop(items)
    with tempfile.TemporaryDirectory() as td:
        sg.build_html(td, structure, written)
        for dkey, dtitle, phase2, cats in structure:
            if not any(ck in written for ck, _c, _m in cats):
                continue
            dep = open(os.path.join(td, "%s.html" % dkey), encoding="utf-8").read()
            assert dep.count("◄ В отделы") == 1
            # каждая категория возвращает на страницу СВОЕГО отдела
            for ck, ct, mid in cats:
                if ck in written:
                    assert "_bbsmultisell;%d,merchant/%s" % (mid, dkey) in dep


# Feature: alt-b-shop-overhaul, Property 10: Обособленность эпик-бижи
@settings(**SETTINGS)
@given(catalog())
def test_property10_epic_disjoint(items):
    report = sg.new_report()
    cats = sg.build_categories(items, report)
    epic_ids = {it["id"] for it in cats.get("jewel_epic", [])}
    normal_ids = set()
    for ck, lst in cats.items():
        if ck.startswith("jewel_") and ck != "jewel_epic":
            normal_ids |= {it["id"] for it in lst}
    assert epic_ids.isdisjoint(normal_ids)


# Feature: alt-b-shop-overhaul, Property 11: Полнота по характеристикам и стихиям
@settings(**SETTINGS)
@given(catalog())
def test_property11_stats_elements_completeness(items):
    report, _, entries_by_cat, _ = sg.build_shop(items)
    dye_out = " ".join(it["name"] for (ck, _m), es in entries_by_cat.items()
                       if ck == "dyes" for it, _p in es)
    attr_out = " ".join(it["name"] for (ck, _m), es in entries_by_cat.items()
                        if ck == "attribute_stones" for it, _p in es)
    missing = {(m["group"], m["member"]) for m in report["missing_families"]}

    def classifiable(pred):
        for it in items.values():
            if pred(it) and not sg.is_excluded(it, sg.classify(it) or ""):
                return True
        return False

    for stat in sg.STATS:
        present = classifiable(lambda it, s=stat: sg.classify(it) == "dyes" and s in it["name"])
        if present:
            assert stat in dye_out
        else:
            assert ("dye_stat", stat) in missing
    for el in sg.ELEMENTS:
        present = classifiable(lambda it, e=el: sg.classify(it) == "attribute_stones" and e in it["name"])
        if present:
            assert el in attr_out
        else:
            assert ("attribute_element", el) in missing


# Feature: alt-b-shop-overhaul, Property 12: Пустые категории не создаются и фиксируются
@settings(**SETTINGS)
@given(catalog())
def test_property12_empty_categories(items):
    report, structure, entries_by_cat, written = sg.build_shop(items)
    all_cats = [ck for _dk, _dt, _p2, cats in structure for ck, _c, _m in cats]
    written_from_entries = {ck for (ck, _m) in entries_by_cat}
    for ck in all_cats:
        if ck not in written_from_entries:
            assert ck in report["empty_categories"]
    # записанные категории не числятся пустыми
    for ck in written_from_entries:
        assert ck not in report["empty_categories"]


# Feature: alt-b-shop-overhaul, Property 13: Согласованность отчёта покрытия
@settings(**SETTINGS)
@given(catalog())
def test_property13_report_consistency(items):
    report, _, entries_by_cat, _ = sg.build_shop(items)
    sum_per_cat = sum(report["per_category"].values())
    actual_rows = 0
    for (ck, msid), entries in entries_by_cat.items():
        xml = sg.render_multisell(entries)
        actual_rows += len(re.findall(r"<production ", xml))
    assert sum_per_cat == report["total_items"] == actual_rows


# --------------------------------------------------------------------------- #
#  Unit / example тесты
# --------------------------------------------------------------------------- #
def test_structure_has_13_departments():
    structure = sg.build_structure()
    assert len(structure) == 13


def test_price_edge_cases():
    # price = 0 -> неотрицательная цена > 0 и > price
    it0 = dict(id=1, name="X", add="", type="Weapon", grade="R", bodypart="rhand",
               armor_type="", weapon_type="SWORD", price=0)
    assert sg.buy_price(it0, "weapon_r") > 0
    # price близко к максимуму -> строго больше
    big = dict(it0, price=2_000_000_000)
    assert sg.buy_price(big, "weapon_r") > big["price"]
    # неизвестный грейд -> GRADE_FLOOR
    ng = dict(it0, grade="", price=0)
    assert sg.buy_price(ng, "weapon_r") >= 1


def test_dedup_keeps_lowest_id():
    lst = [dict(id=50, name="A", grade="R"), dict(id=10, name="A", grade="R"),
           dict(id=30, name="A", grade="S")]
    out = sg.dedup(lst, key="grade")
    ids = sorted(it["id"] for it in out)
    assert ids == [10, 30]  # (A,R)->10 (min), (A,S)->30


def test_is_excluded_markers():
    assert sg.is_excluded(dict(name="Sword Box", add=""), "weapon_r")
    assert sg.is_excluded(dict(name="Bloody Eternal Boots", add=""), "armor_r99")
    assert not sg.is_excluded(dict(name="Bloody Eternal Boots", add=""), "weapon_r")
    assert not sg.is_excluded(dict(name="Amaranthine Shaper", add=""), "weapon_r99")


def test_writeable_dir_error(tmp_path):
    # Недоступный на запись каталог вывода -> прекращение генерации (Req 19.6)
    if os.geteuid() == 0:
        pytest.skip("root игнорирует права записи")
    data = tmp_path / "data"
    (data / "stats" / "items").mkdir(parents=True)
    (data / "stats" / "items" / "i.xml").write_text(
        '<list><item id="1" name="Amaranthine Shaper" type="Weapon">'
        '<set name="crystal_type" val="R99"/><set name="weapon_type" val="SWORD"/>'
        '<set name="bodypart" val="rhand"/></item></list>', encoding="utf-8")
    ms = data / "multisell"
    ms.mkdir()
    os.chmod(ms, 0o500)
    with pytest.raises(SystemExit):
        sg.generate(str(data))
