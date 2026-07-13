#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Alt+B Shop generator (Магазин за адену) for the solo Grand Crusade server.

Реализация дизайна `.kiro/specs/alt-b-shop-overhaul/design.md`:
  Item_Catalog (server/game/data/stats/items/*.xml)
      -> parse_items -> classify -> exclude -> dedup -> price
      -> multisell/6000XX.xml  (валюта: адена id 57, блок <npcs><npc>-1</npc></npcs>)
      -> html/CommunityBoard/Custom/merchant/*.html (13 отделов, ≤2 клика)
      -> Generation_Report (счётчики, пропуски, пустые категории, нарушения)

Пересборка jar НЕ требуется — применяется рестартом сервера.

CLI:
  python tools/shopgen.py --data-dir server/game/data [--dry-run] [--report shop_report.txt]
"""
import os
import re
import sys
import glob
import math
import argparse
from collections import defaultdict

# --------------------------------------------------------------------------- #
#  Константы предметной области
# --------------------------------------------------------------------------- #
ADENA_ID = 57
GRADES = ["D", "C", "B", "A", "S", "S80", "R", "R95", "R99"]
JEWEL_GRADES = ["A", "S", "S80", "R", "R95", "R99"]
ENDGAME_GRADES = ["R", "R95", "R99"]

WEAPON_TYPES = {
    "SWORD", "BLUNT", "DAGGER", "BOW", "POLE", "FIST", "DUALFIST",
    "DUAL", "DUALDAGGER", "DUALBLUNT", "CROSSBOW", "TWOHANDCROSSBOW",
    "RAPIER", "ANCIENTSWORD",
}
ARMOR_SLOTS = {"chest", "legs", "feet", "gloves", "head", "alldress", "onepiece"}
JEWEL_BP = {"neck", "rear;lear", "rfinger;lfinger"}
BRACELET_BP = {"rbracelet", "lbracelet"}
HAT_BP = {"hair", "hair2", "hairall", "head"}

EPIC_BOSSES = ["Queen Ant", "Orfen", "Core", "Zaken", "Baium",
               "Antharas", "Frintezza", "Valakas"]

GEM_FAMILIES = ["Ruby", "Sapphire", "Emerald", "Diamond", "Opal", "Obsidian",
                "Pearl", "Vital Spirit", "Cat's Eye", "Garnet", "Tanzanite",
                "Aquamarine"]

STATS = ["STR", "DEX", "CON", "INT", "WIT", "MEN"]
ELEMENTS = ["Fire", "Water", "Wind", "Earth", "Holy", "Dark"]

# Классические стат-шапки (+1 к характеристике / боевым статам)
STAT_HAT_IDS = [9883, 9884, 9885, 9886, 9887, 9888, 9889, 15484]

# Курируемые семейства стат-браслетов
BRACELET_PATTERNS = ("Dimensional Bracelet", "Bracelet of the Conqueror",
                     "Eternal Bracelet", "Immortal Bracelet", "Giant's Bracelet")

# Курируемые семейства стат-талисманов актуальной хроники
TALISMAN_PATTERNS = (
    "Mysterious Talisman", "Talisman of Insanity", "Talisman of Authority",
    "Talisman of Eva", "Talisman of Aden", "Talisman of Baium",
    "Yellow Talisman", "Blue Talisman", "Red Talisman", "White Talisman",
    "Black Talisman",
)
TALISMAN_GRADED = re.compile(r"^(Special )?Talisman \(R\d*-grade\)$")

# Эндгейм-линейки экипа для рецептов (крафт рейд-боссов)
ENDGAME_LINES = ("Twilight", "Seraph", "Eternal", "Apocalypse", "Specter",
                 "Amaranthine")

# Курируемые расходники (зелья/эликсиры) актуального грейда
POTION_PATTERNS = (
    "Elixir of Life (R-grade)", "Elixir of Mental Strength (R-grade)",
    "Elixir of CP (R-grade)", "Greater CP Potion", "Superior CP Potion",
    "Greater Healing Potion", "Superior Healing Potion",
)

# --------------------------------------------------------------------------- #
#  Ценообразование (формула-заглушка, Требование 15; точный баланс вне scope)
# --------------------------------------------------------------------------- #
MIN_DELTA = 1
GRADE_FLOOR = {
    "": 1000, "NG": 1000, "D": 5000, "C": 20000, "B": 100000, "A": 500000,
    "S": 2_000_000, "S80": 5_000_000, "R": 20_000_000, "R95": 80_000_000,
    "R99": 250_000_000,
}
MARKUP = {
    "weapon": 1.5, "armor": 1.5, "jewel": 2.0, "jewel_epic": 5.0,
    "brooch_and_jewels": 3.0, "bracelets": 2.0, "cloaks": 2.0, "belts": 2.0,
    "agathions": 2.0, "dyes": 1.5, "stat_hats": 1.5, "talismans": 2.0,
    "enchant_weapon": 3.0, "enchant_armor": 3.0, "life_stones": 3.0,
    "attribute_stones": 2.0, "materials": 1.5, "recipes": 2.0, "crystals": 1.5,
    "quest_items": 1.5, "shots": 1.2, "potions": 1.2,
}


def markup_for(cat_key):
    if cat_key.startswith("weapon_"):
        return MARKUP["weapon"]
    if cat_key.startswith("armor_"):
        return MARKUP["armor"]
    if cat_key.startswith("jewel_") and cat_key != "jewel_epic":
        return MARKUP["jewel"]
    return MARKUP.get(cat_key, 1.5)


def buy_price(item, cat_key):
    """Цена покупки в адене, строго > NPC_Sell_Price (Требование 15.1/15.2)."""
    grade = item["grade"] or "NG"
    base = max(item["price"], GRADE_FLOOR.get(grade, 1000))
    raw = math.ceil(base * markup_for(cat_key))
    price = max(raw, item["price"] + MIN_DELTA)
    return int(price)


# --------------------------------------------------------------------------- #
#  Слой доступа к данным
# --------------------------------------------------------------------------- #
_HDR = re.compile(
    r'<item\s+id="(\d+)"\s+name="([^"]*)"'
    r'(?:\s+additionalName="([^"]*)")?\s+type="(\w+)"')


def _find(block, key):
    m = re.search(r'name="%s"\s+val="([^"]*)"' % re.escape(key), block)
    return m.group(1) if m else ""


def parse_items(items_dir, report):
    """Строит карту id -> ItemDef. Некорректные блоки -> skipped_items."""
    items = {}
    files = sorted(glob.glob(os.path.join(items_dir, "*.xml")))
    if not files:
        raise SystemExit("ОШИБКА: каталог предметов пуст или не найден: %s" % items_dir)
    for f in files:
        try:
            txt = open(f, encoding="utf-8", errors="replace").read()
        except OSError as exc:
            raise SystemExit("ОШИБКА чтения %s: %s" % (f, exc))
        for part in txt.split("<item ")[1:]:
            block = "<item " + part.split("</item>")[0]
            m = _HDR.search(block)
            if not m:
                report["skipped_items"].append(
                    {"name": None, "reason": "неполный/некорректный блок item"})
                continue
            iid = int(m.group(1))
            price_raw = _find(block, "price")
            try:
                price = int(price_raw) if price_raw else 0
            except ValueError:
                price = 0
            items[iid] = {
                "id": iid,
                "name": m.group(2),
                "add": m.group(3) or "",
                "type": m.group(4),
                "grade": _find(block, "crystal_type"),
                "bodypart": _find(block, "bodypart"),
                "armor_type": _find(block, "armor_type"),
                "weapon_type": _find(block, "weapon_type"),
                "price": price,
            }
    return items


# --------------------------------------------------------------------------- #
#  Слой исключения (Excluded_Marker) и дедупликации
# --------------------------------------------------------------------------- #
_MARKERS = ("appearance", "coupon", "voucher", "box", "pack", "bundle",
            "fragment", "piece", "shard", "event", "test", "not in use",
            "(7-day)", "(14-day)", "(30-day)", "(1-day)")
_ARMOR_EXTRA = ("bloody", "ultimate", "transcendent", "bound")


def is_excluded(item, category_key):
    text = (item["name"] + " " + item["add"]).lower()
    for mk in _MARKERS:
        if mk in text:
            return True
    if category_key.startswith("armor_"):
        for mk in _ARMOR_EXTRA:
            if mk in text:
                return True
    return False


def dedup(items_list, key="grade"):
    """Дедуп: ключ (name, crystal_type) или name; остаётся минимальный id."""
    seen = {}
    for it in sorted(items_list, key=lambda x: x["id"]):
        k = (it["name"], it["grade"]) if key == "grade" else it["name"]
        seen.setdefault(k, it)
    return sorted(seen.values(), key=lambda x: x["id"])


def _pick_rep(cands):
    """Представитель группы: минимальный id (каноничная ретейл-линейка)."""
    return sorted(cands, key=lambda it: it["id"])[0]


# --------------------------------------------------------------------------- #
#  Слой классификации
# --------------------------------------------------------------------------- #
def is_epic_jewel(item):
    return (item["bodypart"] in JEWEL_BP
            and any(b in item["name"] for b in EPIC_BOSSES))


def classify(item):
    """Чистая функция: предмет -> ключ категории (или None).

    Каждый предмет попадает максимум в одну категорию (Требование 1.7).
    """
    t, bp, grade = item["type"], item["bodypart"], item["grade"]
    name = item["name"]

    # --- Оружие ---
    if t == "Weapon":
        if item["weapon_type"] in WEAPON_TYPES and grade in GRADES:
            return "weapon_%s" % grade.lower()
        return None

    if t == "Armor":
        # --- Броня ---
        if bp in ARMOR_SLOTS and grade in GRADES:
            return "armor_%s" % grade.lower()
        # --- Эпик-бижутерия ---
        if is_epic_jewel(item):
            return "jewel_epic"
        # --- Обычная бижутерия ---
        if bp in JEWEL_BP and grade in JEWEL_GRADES:
            return "jewel_%s" % grade.lower()
        # --- Броши и камни брошей ---
        if bp in ("brooch", "brooch_jewel"):
            return "brooch_and_jewels"
        # --- Браслеты / агатионы ---
        if bp in BRACELET_BP:
            # Агатионы в данных Mobius — косметические (без надёжного стат-сигнала),
            # поэтому в стат-ассортимент не включаются (см. Generation_Report).
            if "Agathion" in name:
                return None
            if any(p in name for p in BRACELET_PATTERNS):
                return "bracelets"
            return None
        # --- Талисманы (слот deco1 в Mobius) ---
        if bp in ("deco1", "talisman"):
            if any(p in name for p in TALISMAN_PATTERNS) or TALISMAN_GRADED.match(name):
                return "talismans"
            return None
        # --- Стат-шапки ---
        if item["id"] in STAT_HAT_IDS:
            return "stat_hats"
        # --- Плащи / пояса ---
        if bp == "back" and grade in ENDGAME_GRADES:
            return "cloaks"
        if bp == "waist" and grade in ENDGAME_GRADES:
            return "belts"
        return None

    # --- EtcItem категории ---
    if t == "EtcItem":
        # Краски (легендарные, дающие статы)
        if "Legendary" in name and "Dye" in name and any(s in name for s in STATS):
            return "dyes"
        # Заточка
        if re.match(r"^Scroll: Enchant Weapon \(", name) or \
           re.match(r"^Blessed Scroll: Enchant Weapon \(", name):
            return "enchant_weapon"
        if re.match(r"^Scroll: Enchant Armor \(", name) or \
           re.match(r"^Blessed Scroll: Enchant Armor \(", name):
            return "enchant_armor"
        # Камни жизни / аугментация
        if re.match(r"^Life Stone - Lv\. \d+$", name) or \
           name == "Life Stone Instilled with Giants' Power":
            return "life_stones"
        # Атрибутные камни/кристаллы
        if re.match(r"^(Fire|Water|Wind|Earth|Holy|Dark) (Stone|Crystal)$", name):
            return "attribute_stones"
        # Заряды
        if re.match(r"^(Soulshot|Spiritshot|Blessed Spiritshot) \(R-grade\)$", name):
            return "shots"
        # Зелья / эликсиры
        if name in POTION_PATTERNS:
            return "potions"
        # Кристаллы для крафта
        if re.match(r"^Crystal \((D|C|B|A|S|S80|R|R95|R99)-grade\)$", name):
            return "crystals"
        # Рецепты эндгейм-экипа
        if name.startswith("Recipe:") and any(l in name for l in ENDGAME_LINES):
            return "recipes"
        # Квестовые предметы: соул-кристаллы для крафта РБ-экипа
        if name.startswith("Soul Crystal"):
            return "quest_items"
        return None

    return None


# Категории, использующие представителя-по-ключу (а не полный список)
_REP_KEYS = {}  # cat_key -> function(item) -> group tuple


def _weapon_group(it):
    return (it["grade"], it["weapon_type"])


def _armor_group(it):
    at = it["armor_type"] or ("ROBE" if it["bodypart"] in ("alldress", "onepiece") else "")
    return (it["grade"], at, it["bodypart"])


def _jewel_group(it):
    return (it["grade"], it["bodypart"])


def build_categories(items, report):
    """Возвращает dict cat_key -> отсортированный список ItemDef (после фильтра)."""
    raw = defaultdict(list)
    for it in items.values():
        ck = classify(it)
        if ck is None:
            continue
        if is_excluded(it, ck):
            report["skipped_items"].append(
                {"id": it["id"], "name": it["name"], "reason": "excluded:%s" % ck})
            continue
        raw[ck].append(it)

    result = {}
    for ck, lst in raw.items():
        if ck.startswith("weapon_"):
            groups = defaultdict(list)
            for it in lst:
                groups[_weapon_group(it)].append(it)
            result[ck] = dedup([_pick_rep(g) for g in groups.values()], key="grade")
        elif ck.startswith("armor_"):
            groups = defaultdict(list)
            for it in lst:
                groups[_armor_group(it)].append(it)
            result[ck] = dedup([_pick_rep(g) for g in groups.values()], key="grade")
        elif ck.startswith("jewel_") and ck != "jewel_epic":
            groups = defaultdict(list)
            for it in lst:
                groups[_jewel_group(it)].append(it)
            result[ck] = dedup([_pick_rep(g) for g in groups.values()], key="grade")
        elif ck in ("bracelets", "talismans", "stat_hats", "agathions"):
            result[ck] = dedup(lst, key="name")
        elif ck == "life_stones":
            # высшие уровни + камень Гигантов
            lv = []
            for it in lst:
                m = re.match(r"^Life Stone - Lv\. (\d+)$", it["name"])
                if m:
                    lv.append((int(m.group(1)), it))
            lv.sort(key=lambda x: -x[0])
            top = [it for _, it in lv[:5]]
            top += [it for it in lst
                    if it["name"] == "Life Stone Instilled with Giants' Power"]
            result[ck] = dedup(top, key="name")
        else:
            result[ck] = dedup(lst, key="grade")

    # --- Отчёт: пропущенные семейства/боссы/стихии/характеристики ---
    epic_names = " ".join(it["name"] for it in result.get("jewel_epic", []))
    for boss in EPIC_BOSSES:
        if boss not in epic_names:
            report["missing_families"].append({"group": "epic_boss", "member": boss})

    brooch_names = " ".join(it["name"] for it in result.get("brooch_and_jewels", []))
    for gem in GEM_FAMILIES:
        if gem not in brooch_names:
            report["missing_families"].append({"group": "brooch_gem", "member": gem})

    dye_names = " ".join(it["name"] for it in result.get("dyes", []))
    for st in STATS:
        if st not in dye_names:
            report["missing_families"].append({"group": "dye_stat", "member": st})

    attr_names = " ".join(it["name"] for it in result.get("attribute_stones", []))
    for el in ELEMENTS:
        if el not in attr_names:
            report["missing_families"].append({"group": "attribute_element", "member": el})

    # Отделы/категории без надёжного стат-сигнала в данных Mobius — требуют ручной курации
    report["missing_families"].append(
        {"group": "needs_curation", "member": "agathions (стат-агатионы не определяются автоматически)"})
    report["missing_families"].append(
        {"group": "needs_curation", "member": "materials (крафт-материалы РБ требуют курации)"})

    return result


# --------------------------------------------------------------------------- #
#  Структура 13 отделов и карта категория -> отдел -> multisell-id
# --------------------------------------------------------------------------- #
def grade_title(prefix, key):
    return "%s %s" % (prefix, key.rsplit("_", 1)[1].upper())


# (dept_key, dept_title, phase2, [ (cat_key, cat_title, msid) ... ])
def build_structure():
    weapons = [("weapon_%s" % g.lower(), "Оружие %s" % g, 600001 + i)
               for i, g in enumerate(GRADES)]
    armor = [("armor_%s" % g.lower(), "Броня %s" % g, 600010 + i)
             for i, g in enumerate(GRADES)]
    jewels = [("jewel_%s" % g.lower(), "Бижутерия %s" % g, 600020 + i)
              for i, g in enumerate(JEWEL_GRADES)]
    jewels += [("jewel_epic", "Эпик-бижутерия", 600028),
               ("brooch_and_jewels", "Брошь и камни", 600029)]
    return [
        ("weapons", "Оружие", False, weapons),
        ("armor", "Броня", False, armor),
        ("jewelry", "Бижутерия", False, jewels),
        ("accessory", "Аксессуары", False, [
            ("bracelets", "Браслеты", 600030),
            ("cloaks", "Плащи", 600031),
            ("belts", "Пояса", 600032),
            ("agathions", "Агатионы", 600033),
        ]),
        ("stats", "Стат-усиления", False, [
            ("dyes", "Краски (статы)", 600040),
            ("stat_hats", "Стат-шапки", 600041),
            ("talismans", "Талисманы", 600042),
        ]),
        ("enchant", "Заточка и аугментация", False, [
            ("enchant_weapon", "Заточка: оружие", 600050),
            ("enchant_armor", "Заточка: броня", 600051),
            ("life_stones", "Камни жизни", 600052),
        ]),
        ("attribute", "Атрибут", False, [
            ("attribute_stones", "Атрибутные камни", 600060),
        ]),
        ("craft", "Крафт и материалы", False, [
            ("materials", "Материалы", 600070),
            ("recipes", "Рецепты", 600071),
            ("crystals", "Кристаллы", 600072),
        ]),
        ("quest", "Квестовые предметы", False, [
            ("quest_items", "Квестовые предметы", 600080),
        ]),
        ("consumables", "Расходники", False, [
            ("shots", "Заряды", 600085),
            ("potions", "Зелья и эликсиры", 600086),
        ]),
        ("pets", "Петы", True, []),
        ("clan", "Клановые предметы", True, []),
        ("cosmetic", "Косметика", True, []),
    ]


# --------------------------------------------------------------------------- #
#  Emit: мультиселлы
# --------------------------------------------------------------------------- #
_MS_TPL = ('<?xml version="1.0" encoding="UTF-8"?>\n'
           '<list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" '
           'xsi:noNamespaceSchemaLocation="../xsd/multisell.xsd">\n'
           '\t<npcs>\n\t\t<npc>-1</npc>\n\t</npcs>\n{rows}\n</list>\n')
_ROW_TPL = ('\t<item>\n\t\t<ingredient id="%d" count="%d" />\n'
            '\t\t<production id="%d" count="1" /> <!-- %s -->\n\t</item>')


def render_multisell(entries):
    rows = "\n".join(_ROW_TPL % (ADENA_ID, price, it["id"], it["name"])
                     for it, price in entries)
    return _MS_TPL.format(rows=rows)


def write_multisell(multisell_dir, msid, entries):
    path = os.path.join(multisell_dir, "%d.xml" % msid)
    with open(path, "w", encoding="utf-8") as fh:
        fh.write(render_multisell(entries))


# --------------------------------------------------------------------------- #
#  Emit: HTML-меню
# --------------------------------------------------------------------------- #
def _btn(label, action, w=170):
    return ('<button value="%s" action="%s" width=%d height=28 '
            'back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">'
            % (label, action, w))


def _page(title, body_rows):
    rows = "\n".join(body_rows)
    return ('<html noscrollbar>\n\t<body>\n'
            '\t\t<table width=700><tr><td height=10></td></tr></table>\n'
            '\t\t<table width=20>\n\t\t\t<tr>\n\t\t\t\t<td>%%navigation%%</td>\n'
            '\t\t\t\t<td>\n\t\t\t\t\t<center>\n'
            '\t\t\t\t\t\t<table border=0 cellpadding=0 cellspacing=0 width=555 '
            'height=455 background="L2UI_CT1.Windows_DF_TooltipBG">\n'
            '\t\t\t\t\t\t\t<tr><td height=18></td></tr>\n'
            '\t\t\t\t\t\t\t<tr><td height=24 align="center">'
            '<font name="hs12" color="CDB67F">%s</font></td></tr>\n'
            '\t\t\t\t\t\t\t<tr><td><center>'
            '<img src="L2UI.SquareGray" width=470 height=1></center></td></tr>\n'
            '\t\t\t\t\t\t\t<tr><td height=12></td></tr>\n'
            '\t\t\t\t\t\t\t<tr><td align="center">\n'
            '\t\t\t\t\t\t\t\t<table align=center border=0 cellpadding=3 cellspacing=3>\n'
            '%s\n\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t</td></tr>\n'
            '\t\t\t\t\t\t</table>\n\t\t\t\t\t</center>\n\t\t\t\t</td>\n'
            '\t\t\t</tr>\n\t\t</table>\n\t</body>\n</html>\n' % (title, rows))


def _grid(buttons, width=170):
    """buttons: list of (label, action). Раскладка по 2 в ряд."""
    rows = []
    for i in range(0, len(buttons), 2):
        cells = ""
        for label, action in buttons[i:i + 2]:
            cells += ('\t\t\t\t\t\t\t\t\t<td><center>%s</center></td>\n'
                      % _btn(label, action, width))
        rows.append("\t\t\t\t\t\t\t\t<tr>\n%s\t\t\t\t\t\t\t\t</tr>" % cells)
    return rows


def build_html(html_dir, structure, written_cats):
    os.makedirs(html_dir, exist_ok=True)

    # Удаляем устаревшие страницы прошлой схемы (детерминированный вывод)
    for old in glob.glob(os.path.join(html_dir, "*.html")):
        os.remove(old)

    # --- Корневая страница: 13 отделов + заглушки Фазы 2 ---
    dep_buttons = []
    for dkey, dtitle, phase2, cats in structure:
        active = (not phase2) and any(ck in written_cats for ck, _, _ in cats)
        if active:
            dep_buttons.append((dtitle, "bypass _bbstop;merchant/%s.html" % dkey))
        else:
            # видимая, но неактивная заглушка (Требование 1.9)
            dep_buttons.append((dtitle + " (скоро)", "bypass _bbstop;merchant/soon.html"))
    # Задел под сервисы Фазы 2 (профессии/топы/Герой)
    for extra in ("Профессии", "Топы", "Герой"):
        dep_buttons.append((extra + " (скоро)", "bypass _bbstop;merchant/soon.html"))

    with open(os.path.join(html_dir, "main.html"), "w", encoding="utf-8") as fh:
        fh.write(_page("Магазин за адену — отделы", _grid(dep_buttons)))

    # --- Страница-заглушка Фазы 2 ---
    soon_rows = ["\t\t\t\t\t\t\t\t<tr><td align=center>"
                 "<font color=696969>Раздел появится в Фазе 2.</font></td></tr>",
                 "\t\t\t\t\t\t\t\t<tr><td height=10></td></tr>",
                 "\t\t\t\t\t\t\t\t<tr><td><center>%s</center></td></tr>"
                 % _btn("◄ В отделы", "bypass _bbstop;merchant/main.html")]
    with open(os.path.join(html_dir, "soon.html"), "w", encoding="utf-8") as fh:
        fh.write(_page("Скоро", soon_rows))

    # --- Страницы отделов ---
    for dkey, dtitle, phase2, cats in structure:
        active_cats = [(ck, ct, mid) for ck, ct, mid in cats if ck in written_cats]
        if not active_cats:
            continue
        buttons = [(ct, "bypass _bbsmultisell;%d,merchant/%s" % (mid, dkey))
                   for ck, ct, mid in active_cats]
        rows = _grid(buttons, 170)
        rows.append("\t\t\t\t\t\t\t\t<tr><td height=10></td></tr>")
        rows.append("\t\t\t\t\t\t\t\t<tr><td><center>%s</center></td></tr>"
                    % _btn("◄ В отделы", "bypass _bbstop;merchant/main.html"))
        with open(os.path.join(html_dir, "%s.html" % dkey), "w", encoding="utf-8") as fh:
            fh.write(_page(dtitle, rows))


# --------------------------------------------------------------------------- #
#  Generation_Report
# --------------------------------------------------------------------------- #
def emit_report(report, path):
    lines = ["=== Generation_Report: Alt+B Shop ===", ""]
    lines.append("Всего позиций: %d" % report["total_items"])
    lines.append("")
    lines.append("-- Позиций по категориям --")
    for ck in sorted(report["per_category"]):
        lines.append("  %-22s : %d" % (ck, report["per_category"][ck]))
    lines.append("")
    lines.append("-- Пустые категории (файл не создан) --")
    lines.append("  " + (", ".join(report["empty_categories"]) or "нет"))
    lines.append("")
    lines.append("-- Пропущенные семейства/боссы/стихии/характеристики --")
    if report["missing_families"]:
        for mf in report["missing_families"]:
            lines.append("  %s: %s" % (mf["group"], mf["member"]))
    else:
        lines.append("  нет")
    lines.append("")
    lines.append("-- Нарушения анти-эксплойта (исключены) --")
    if report["anti_exploit_violations"]:
        for v in report["anti_exploit_violations"]:
            lines.append("  id=%s name=%s price=%s" % (v["id"], v["name"], v["price"]))
    else:
        lines.append("  нет")
    lines.append("")
    lines.append("-- l2scripts_gap --")
    lines.append("  " + (", ".join(report["l2scripts_gap"]) or "нет"))
    lines.append("")
    lines.append("-- Пропущено предметов (excluded/дубликат/нет в каталоге): %d --"
                 % len(report["skipped_items"]))
    text = "\n".join(lines) + "\n"
    if path:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(text)
    return text


# --------------------------------------------------------------------------- #
#  Оркестрация
# --------------------------------------------------------------------------- #
def new_report():
    return {
        "per_category": {}, "total_items": 0, "skipped_items": [],
        "empty_categories": [], "missing_families": [],
        "anti_exploit_violations": [], "l2scripts_gap": [],
    }


def build_shop(items, report=None):
    """Чистый конвейер: Item_Catalog -> (report, structure, entries_by_cat, written).

    Без файлового ввода-вывода — используется генератором и property-тестами.
    """
    if report is None:
        report = new_report()
    cats = build_categories(items, report)
    structure = build_structure()

    written_cats = set()
    entries_by_cat = {}
    for dkey, dtitle, phase2, cat_specs in structure:
        for ck, ct, msid in cat_specs:
            lst = cats.get(ck, [])
            entries = []
            for it in lst:
                price = buy_price(it, ck)
                if price <= it["price"]:
                    report["anti_exploit_violations"].append(
                        {"id": it["id"], "name": it["name"], "price": it["price"]})
                    continue
                entries.append((it, price))
            if not entries:
                report["empty_categories"].append(ck)
                continue
            entries_by_cat[(ck, msid)] = entries
            written_cats.add(ck)
            report["per_category"][ck] = len(entries)
            report["total_items"] += len(entries)
    return report, structure, entries_by_cat, written_cats


def generate(data_dir, dry_run=False, report_path=None):
    items_dir = os.path.join(data_dir, "stats", "items")
    multisell_dir = os.path.join(data_dir, "multisell")
    html_dir = os.path.join(data_dir, "html", "CommunityBoard", "Custom", "merchant")

    report = new_report()
    items = parse_items(items_dir, report)

    # Проверка доступности каталогов вывода (Требование 19.6) — «всё или ничего»
    if not dry_run:
        for d in (multisell_dir, html_dir):
            os.makedirs(d, exist_ok=True)
            if not os.access(d, os.W_OK):
                raise SystemExit("ОШИБКА записи: каталог недоступен: %s" % d)

    report, structure, entries_by_cat, written_cats = build_shop(items, report)

    if not dry_run:
        # Чистим предыдущий 6000XX-набор (в т.ч. в подкаталоге custom), чтобы не
        # осталось устаревших/дублирующих id — MultisellData грузит multisell рекурсивно.
        for pat in ("6000[0-9][0-9].xml", os.path.join("custom", "6000[0-9][0-9].xml")):
            for old in glob.glob(os.path.join(multisell_dir, pat)):
                os.remove(old)
        for (ck, msid), entries in entries_by_cat.items():
            write_multisell(multisell_dir, msid, entries)
        build_html(html_dir, structure, written_cats)

    report_text = emit_report(report, report_path if not dry_run else report_path)
    return report, report_text, written_cats


def main(argv=None):
    ap = argparse.ArgumentParser(description="Alt+B Shop generator (Магазин за адену)")
    ap.add_argument("--data-dir", default="server/game/data",
                    help="каталог данных активного сервера (по умолчанию server/game/data)")
    ap.add_argument("--dry-run", action="store_true",
                    help="генерация в память + отчёт, без записи файлов")
    ap.add_argument("--report", default=None, help="путь для Generation_Report")
    args = ap.parse_args(argv)

    report, text, written = generate(args.data_dir, args.dry_run, args.report)
    print(text)
    print("Категорий записано: %d%s" % (len(written), " (dry-run)" if args.dry_run else ""))
    return 0


if __name__ == "__main__":
    sys.exit(main())
