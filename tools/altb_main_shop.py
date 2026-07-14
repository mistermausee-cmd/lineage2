#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
altb_main_shop.py  --  Reproducible builder for the MAIN Alt+B community-board shop.

Rebuilds ONLY the main Alt+B shop of the Lineage 2 (L2J Mobius, Grand Crusade) server:
  * multisell/6000XX.xml            (Alt+B shop multisells, npcs = -1)
  * html/CommunityBoard/Custom/merchant/*.html   (shop menu pages + navigation)

It sources item ids from the existing admin GM-shop buylists (buylists/custom/*.xml)
and the item catalog (stats/items/*.xml), so the generated shop always matches game data.

STRICTLY out of scope (never written/touched by this script):
  * L2Scripts reference tab (multisell 610xxx, html .../l2sref/*)
  * admin GM shops (buylists/custom, html/admin/*)
  * server configs, l2scripts/ folder

Departments built here:
  1) "Прокачка"  (tiers B, A, S, S80)  -- MW weapon + MW armor-set per tier
  2) "R-Grade"   -- R weapon(2SA) / Bless weapon / PVE weapon
                    R armor / Bless armor / PVE armor / Transcendent set (lv1-6)
  3) Belts       -- permanent belts only
  4) T-Shirts    -- fixed curated list + enchant scrolls

Run:
  python3 tools/altb_main_shop.py            # generate + report
  python3 tools/altb_main_shop.py --dry-run  # report only, write nothing
"""

import os
import re
import glob
import sys
import argparse

# --------------------------------------------------------------------------- paths
HERE = os.path.dirname(os.path.abspath(__file__))
DATA = os.path.normpath(os.path.join(HERE, "..", "server", "game", "data"))
ITEMS_DIR = os.path.join(DATA, "stats", "items")
BUYLIST_DIR = os.path.join(DATA, "buylists", "custom")
MULTISELL_DIR = os.path.join(DATA, "multisell")
MERCHANT_DIR = os.path.join(DATA, "html", "CommunityBoard", "Custom", "merchant")

ADENA = 57

# --------------------------------------------------------------------------- catalog
_ITEM_RE = re.compile(
    r'<item id="(\d+)" name="([^"]*)"(?:\s+additionalName="([^"]*)")?\s+type="([^"]*)">(.*?)</item>',
    re.S,
)


def _setval(body, key):
    m = re.search(r'<set name="%s" val="([^"]*)"' % key, body)
    return m.group(1) if m else ""


def load_catalog():
    cat = {}
    for f in sorted(glob.glob(os.path.join(ITEMS_DIR, "*.xml"))):
        with open(f, encoding="utf-8") as fh:
            txt = fh.read()
        for m in _ITEM_RE.finditer(txt):
            iid = int(m.group(1))
            body = m.group(5)
            cat[iid] = {
                "name": m.group(2),
                "add": m.group(3) or "",
                "type": m.group(4),
                "ct": _setval(body, "crystal_type"),
                "wt": _setval(body, "weapon_type"),
                "bp": _setval(body, "bodypart"),
                "price": int(_setval(body, "price") or 0),
            }
    return cat


def read_buylist(list_id):
    """Return list of item ids (in file order) from buylists/custom/<7digit>.xml."""
    path = os.path.join(BUYLIST_DIR, "%07d.xml" % list_id)
    with open(path, encoding="utf-8") as fh:
        return [int(x) for x in re.findall(r'id="(\d+)"', fh.read())]


# --------------------------------------------------------------------------- pricing
GRADE_FLOOR = {
    "B": 2_000_000,
    "A": 8_000_000,
    "S": 30_000_000,
    "S80": 80_000_000,
    "S84": 80_000_000,
    "R": 500_000_000,
    "R95": 800_000_000,
    "R99": 1_500_000_000,
}


def roundup(n, step=100_000):
    return ((n + step - 1) // step) * step


def buy_price(cat, iid, floor):
    """Anti-exploit: buy price strictly above NPC sell price (<= item price).
    price*2 always exceeds sell (sell<=price); floored per grade."""
    p = cat.get(iid, {}).get("price", 0) or 0
    return max(floor, roundup(p * 2))


# --------------------------------------------------------------------------- writers
XML_HEAD = ('<?xml version="1.0" encoding="UTF-8"?>\n'
            '<list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"'
            ' xsi:noNamespaceSchemaLocation="../xsd/multisell.xsd">\n')
XML_HEAD_ENCH = ('<?xml version="1.0" encoding="UTF-8"?>\n'
                 '<list maintainEnchantment="true"'
                 ' xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"'
                 ' xsi:noNamespaceSchemaLocation="../xsd/multisell.xsd">\n')


def _cmt(cat, iid):
    d = cat.get(iid, {})
    nm = d.get("name", "?")
    add = d.get("add", "")
    return (nm + (" - " + add if add else "")).replace("--", "-")


def write_sale_multisell(msid, rows, cat, written):
    """rows = list of (production_id, price). Straight adena sale."""
    out = [XML_HEAD, "\t<npcs>\n\t\t<npc>-1</npc>\n\t</npcs>\n"]
    for pid, price in rows:
        out.append("\t<item>\n")
        out.append('\t\t<ingredient id="57" count="%d" />\n' % price)
        out.append('\t\t<production id="%d" count="1" /> <!-- %s -->\n' % (pid, _cmt(cat, pid)))
        out.append("\t</item>\n")
    out.append("</list>\n")
    _write(os.path.join(MULTISELL_DIR, "%d.xml" % msid), "".join(out), written)


def write_upgrade_multisell(msid, rows, cat, written):
    """rows = list of (base_id, cost_adena, prod_id). base item + adena -> upgraded item."""
    out = [XML_HEAD_ENCH, "\t<npcs>\n\t\t<npc>-1</npc>\n\t</npcs>\n"]
    for base, cost, prod in rows:
        out.append("\t<item>\n")
        out.append('\t\t<ingredient id="%d" count="1" /> <!-- %s -->\n' % (base, _cmt(cat, base)))
        out.append('\t\t<ingredient id="57" count="%d" />\n' % cost)
        out.append('\t\t<production id="%d" count="1" /> <!-- %s -->\n' % (prod, _cmt(cat, prod)))
        out.append("\t</item>\n")
    out.append("</list>\n")
    _write(os.path.join(MULTISELL_DIR, "%d.xml" % msid), "".join(out), written)


def _write(path, content, written):
    if not DRY_RUN:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(content)
    written.append(path)


# --------------------------------------------------------------------------- helpers
def base_weapons(cat, buylist_ids):
    """From a MW/R weapon buylist, keep one base item per weapon name (lowest id).
    Prefers the un-SA'd variant (add == '') if present."""
    by_name = {}
    for iid in buylist_ids:
        d = cat.get(iid)
        if not d or d["type"] != "Weapon":
            continue
        by_name.setdefault(d["name"], []).append(iid)
    chosen = []
    for name, ids in by_name.items():
        ids.sort()
        pick = next((i for i in ids if cat[i]["add"] == ""), ids[0])
        chosen.append(pick)
    chosen.sort()
    return chosen


def set_pieces(cat, buylist_ids, drop_shields=False):
    """From an armor-set buylist, keep armor pieces (dedup by name+add, lowest id)."""
    seen, out = {}, []
    for iid in buylist_ids:
        d = cat.get(iid)
        if not d:
            continue
        if drop_shields and d["bp"] in ("lhand", "rhand"):
            continue
        key = (d["name"], d["add"])
        if key in seen:
            continue
        seen[key] = iid
        out.append(iid)
    out.sort()
    return out


def weight_of(cat, iid):
    """Return armor weight class from add/name: Heavy / Light / Robe."""
    d = cat.get(iid, {})
    blob = (d.get("add", "") + " " + d.get("name", "")).lower()
    if "light" in blob or "leather" in blob:
        return "Light"
    if "robe" in blob or any(w in d.get("name", "").lower() for w in
                             ("circlet", "tunic", "stockings")) :
        return "Robe"
    return "Heavy"



# --------------------------------------------------------------------------- config
# Multisell ids for the main Alt+B shop (6000XX namespace)
MS = {
    # Прокачка tiers (weapon MW + set MW combined per tier)
    "tier_B": 600052,
    "tier_A": 600053,
    "tier_S": 600054,
    "tier_S80": 600055,
    # R-Grade department
    "r_weapon": 600060,
    "bless_weapon": 600061,
    "pve_weapon": 600062,
    "r_armor": 600063,
    "bless_armor": 600064,
    "pve_armor": 600065,
    "transcend": [600066, 600067, 600068, 600069, 600070, 600071],  # lv1..lv6
    # Accessories reused
    "belts": 600026,
    "tshirts": 600047,
}

# MW source buylists (weapons / armor sets)
MW_WEAPON = {"B": 10995, "A": 10996, "S": 10997, "S80_ICARUS": 10999, "S80_VESPER": 11000}
MW_SET = {"B": 10000, "A": 10001, "S": 10002, "S80_VESPER": 11006}

# R weapon lines: normal(2SA), blessed(2SA)  -> buylists
R_WEAPON_LINES = [
    ("Requiem",     "R",   11027, 11011),
    ("Apocalypse",  "R",   11028, 11016),
    ("Specter",     "R95", 11029, 11021),
    ("Amaranthine", "R99", 11030, 11026),
]
PVE_WEAPON_BLOODY = 11075          # Bloody Amaranthine R99 weapons

# R armor sets: normal, blessed
R_ARMOR_SETS = [
    ("Immortal", 11031, 11032),
    ("Twilight", 11033, 11034),
    ("Seraph",   11035, 11036),
    ("Eternal",  11037, 11038),
]
# Bloody Eternal (R99 PVE) armor set, positional heavy/light/robe (helmet,chest,legs,gloves,feet)
BLOODY_ETERNAL = list(range(35033, 35048))       # 15 pieces
TRANSCEND_LV1_BASE = 28034                       # standard Transcendent Eternal lv1 helmet(heavy)
TRANSCEND_LEVEL_STRIDE = 45                       # id gap between consecutive levels
TRANSCEND_PIECES = 15                             # heavy5 + light5 + robe5 standard pieces

# Upgrade adena costs
COST_BLESS_WEAPON = 800_000_000
COST_PVE_WEAPON = 2_000_000_000
COST_BLESS_ARMOR = 600_000_000
COST_PVE_ARMOR = 1_500_000_000
COST_TRANSCEND = 1_000_000_000       # * level

# Permanent belts (exist in catalog; temporary/event/45906/45907 excluded per owner)
BELTS = [13894, 13895, 13896, 13897, 17358, 19454, 19460, 19470, 19473, 19476,
         19479, 28384, 30307, 30369, 33510, 35564, 36167, 36168, 36169, 36189,
         36190, 38591, 38592, 47044]

# T-shirts + shirt enchant scrolls (fixed curated list)
TSHIRTS = [23240, 23301, 23304, 23307, 21580, 37718, 23241, 21582, 47392]

# Orphan multisells to delete (old weapon/armor R depts + NG/D/C level tiers)
ORPHAN_MS = [600001, 600002, 600003, 600004, 600005, 600006, 600050, 600051]
# Orphan merchant pages folded into R-Grade
ORPHAN_HTML = ["weapons.html", "armor.html"]

DRY_RUN = False


# --------------------------------------------------------------------------- builders
def build_level_tiers(cat, report, written):
    """Прокачка: one page-multisell per tier = MW weapons + MW set of that grade."""
    def tier(grade_floor, weapon_bls, set_bl):
        rows = []
        wids = []
        for bl in weapon_bls:
            wids += base_weapons(cat, read_buylist(bl))
        for wid in sorted(set(wids)):
            rows.append((wid, buy_price(cat, wid, grade_floor)))
        for sid in set_pieces(cat, read_buylist(set_bl)):
            rows.append((sid, buy_price(cat, sid, grade_floor)))
        return rows

    tiers = {
        "tier_B":   (GRADE_FLOOR["B"],   [MW_WEAPON["B"]],   MW_SET["B"]),
        "tier_A":   (GRADE_FLOOR["A"],   [MW_WEAPON["A"]],   MW_SET["A"]),
        "tier_S":   (GRADE_FLOOR["S"],   [MW_WEAPON["S"]],   MW_SET["S"]),
        "tier_S80": (GRADE_FLOOR["S80"], [MW_WEAPON["S80_ICARUS"], MW_WEAPON["S80_VESPER"]],
                     MW_SET["S80_VESPER"]),
    }
    for key, (floor, wbls, sbl) in tiers.items():
        rows = tier(floor, wbls, sbl)
        write_sale_multisell(MS[key], rows, cat, written)
        report["Прокачка/%s" % key] = len(rows)


def build_r_weapons(cat, report, written):
    """R-Оружие (2SA) + Bless-оружие + PVE-оружие."""
    # blessed index over all blessed 2SA buylists: (name, add) -> id
    bless_idx = {}
    for _, _, _, bless_bl in R_WEAPON_LINES:
        for iid in read_buylist(bless_bl):
            d = cat.get(iid)
            if d and d["type"] == "Weapon":
                bless_idx.setdefault((d["name"], d["add"]), iid)

    sale_rows, bless_rows = [], []
    for line, grade, norm_bl, _bless_bl in R_WEAPON_LINES:
        floor = GRADE_FLOOR.get(grade, GRADE_FLOOR["R"])
        for nid in base_weapons(cat, read_buylist(norm_bl)):
            nd = cat[nid]
            sale_rows.append((nid, buy_price(cat, nid, floor)))
            bkey = ("Blessed " + nd["name"], nd["add"])
            bid = bless_idx.get(bkey)
            if bid:
                bless_rows.append((nid, COST_BLESS_WEAPON, bid))
            else:
                report.setdefault("_todo", []).append(
                    "bless weapon map miss: %s [%s]" % (nd["name"], nd["add"]))
    write_sale_multisell(MS["r_weapon"], sale_rows, cat, written)
    write_upgrade_multisell(MS["bless_weapon"], bless_rows, cat, written)
    report["R-Grade/R-Оружие(2SA)"] = len(sale_rows)
    report["R-Grade/Bless-оружие"] = len(bless_rows)

    # PVE weapon: Blessed Amaranthine(R99) -> Bloody Amaranthine
    bloody_core = {}
    for iid in base_weapons(cat, read_buylist(PVE_WEAPON_BLOODY)):
        nm = cat[iid]["name"]
        core = nm.replace("Bloody ", "")
        bloody_core.setdefault(core, iid)
    pve_rows = []
    # blessed Amaranthine 2SA buylist = last line entry
    _, _, _, ama_bless_bl = R_WEAPON_LINES[-1]
    for bid in base_weapons(cat, read_buylist(ama_bless_bl)):
        core = cat[bid]["name"].replace("Blessed ", "")
        pid = bloody_core.get(core)
        if pid:
            pve_rows.append((bid, COST_PVE_WEAPON, pid))
        else:
            report.setdefault("_todo", []).append("pve weapon map miss: %s" % core)
    write_upgrade_multisell(MS["pve_weapon"], pve_rows, cat, written)
    report["R-Grade/PVE-оружие"] = len(pve_rows)


def build_r_armor(cat, report, written):
    """R-Броня + Bless-броня + PVE-броня."""
    # blessed armor index: (name, add) -> id
    bless_idx = {}
    for _, _norm, bless_bl in R_ARMOR_SETS:
        for iid in read_buylist(bless_bl):
            d = cat.get(iid)
            if d:
                bless_idx.setdefault((d["name"], d["add"]), iid)

    sale_rows, bless_rows = [], []
    floor = GRADE_FLOOR["R99"]
    for _name, norm_bl, _bless_bl in R_ARMOR_SETS:
        for nid in set_pieces(cat, read_buylist(norm_bl)):
            nd = cat[nid]
            sale_rows.append((nid, buy_price(cat, nid, floor)))
            bid = bless_idx.get(("Blessed " + nd["name"], nd["add"]))
            if bid:
                bless_rows.append((nid, COST_BLESS_ARMOR, bid))
            else:
                report.setdefault("_todo", []).append(
                    "bless armor map miss: %s [%s]" % (nd["name"], nd["add"]))
    write_sale_multisell(MS["r_armor"], sale_rows, cat, written)
    write_upgrade_multisell(MS["bless_armor"], bless_rows, cat, written)
    report["R-Grade/R-Броня"] = len(sale_rows)
    report["R-Grade/Bless-броня"] = len(bless_rows)

    # PVE armor: Blessed Eternal -> Bloody Eternal, mapped by (bodypart, weight)
    bloody_idx = {}
    for iid in BLOODY_ETERNAL:
        d = cat.get(iid)
        if d:
            bloody_idx[(d["bp"], weight_of(cat, iid))] = iid
    _, _eternal_norm, eternal_bless_bl = R_ARMOR_SETS[-1]
    pve_rows = []
    for bid in set_pieces(cat, read_buylist(eternal_bless_bl), drop_shields=True):
        d = cat[bid]
        pid = bloody_idx.get((d["bp"], weight_of(cat, bid)))
        if pid:
            pve_rows.append((bid, COST_PVE_ARMOR, pid))
        else:
            report.setdefault("_todo", []).append(
                "pve armor map miss: %s [%s]" % (d["name"], d["add"]))
    write_upgrade_multisell(MS["pve_armor"], pve_rows, cat, written)
    report["R-Grade/PVE-броня"] = len(pve_rows)


def build_transcend(cat, report, written):
    """Запредельный сет ур.1..6. Chain: Bloody->Lv1, Lv1->Lv2, ... Lv5->Lv6."""
    for level, msid in enumerate(MS["transcend"], start=1):
        cur_base = TRANSCEND_LV1_BASE + (level - 1) * TRANSCEND_LEVEL_STRIDE
        cur = [cur_base + k for k in range(TRANSCEND_PIECES)]
        if level == 1:
            prev = BLOODY_ETERNAL[:]           # Bloody Eternal R99 base set
        else:
            prev_base = TRANSCEND_LV1_BASE + (level - 2) * TRANSCEND_LEVEL_STRIDE
            prev = [prev_base + k for k in range(TRANSCEND_PIECES)]
        rows = []
        for base, prod in zip(prev, cur):
            if base in cat and prod in cat:
                rows.append((base, COST_TRANSCEND * level, prod))
            else:
                report.setdefault("_todo", []).append(
                    "transcend lv%d id miss base=%d prod=%d" % (level, base, prod))
        write_upgrade_multisell(msid, rows, cat, written)
        report["R-Grade/Запредельный ур.%d" % level] = len(rows)


def build_belts(cat, report, written):
    rows = [(b, buy_price(cat, b, 100_000_000)) for b in BELTS if b in cat]
    missing = [b for b in BELTS if b not in cat]
    if missing:
        report.setdefault("_todo", []).append("belt ids missing in catalog: %s" % missing)
    write_sale_multisell(MS["belts"], rows, cat, written)
    report["Аксессуары/Пояса"] = len(rows)


def build_tshirts(cat, report, written):
    rows = [(t, buy_price(cat, t, 10_000_000)) for t in TSHIRTS if t in cat]
    missing = [t for t in TSHIRTS if t not in cat]
    if missing:
        report.setdefault("_todo", []).append("tshirt ids missing in catalog: %s" % missing)
    write_sale_multisell(MS["tshirts"], rows, cat, written)
    report["Аксессуары/Футболки"] = len(rows)


# --------------------------------------------------------------------------- HTML
def _page(title, buttons, back=None):
    """buttons: list of (label, action). back: (label, action) or None."""
    rows_html = []
    for i in range(0, len(buttons), 2):
        pair = buttons[i:i + 2]
        cells = "".join(
            '\t\t\t\t\t\t\t\t\t<td><center><button value="%s" action="%s" '
            'width=170 height=28 back="L2UI_CT1.Button_DF_Down" '
            'fore="L2UI_CT1.Button_DF"></center></td>\n' % (lbl, act)
            for lbl, act in pair)
        rows_html.append("\t\t\t\t\t\t\t\t<tr>\n%s\t\t\t\t\t\t\t\t</tr>\n" % cells)
    back_html = ""
    if back:
        back_html = ('\t\t\t\t\t\t\t\t<tr><td height=10></td></tr>\n'
                     '\t\t\t\t\t\t\t\t<tr><td colspan=2><center><button value="%s" '
                     'action="%s" width=170 height=28 back="L2UI_CT1.Button_DF_Down" '
                     'fore="L2UI_CT1.Button_DF"></center></td></tr>\n' % (back[0], back[1]))
    return (
        '<html noscrollbar>\n\t<body>\n'
        '\t\t<table width=700><tr><td height=10></td></tr></table>\n'
        '\t\t<table width=20>\n\t\t\t<tr>\n\t\t\t\t<td>%%navigation%%</td>\n\t\t\t\t<td>\n'
        '\t\t\t\t\t<center>\n'
        '\t\t\t\t\t\t<table border=0 cellpadding=0 cellspacing=0 width=555 height=455 '
        'background="L2UI_CT1.Windows_DF_TooltipBG">\n'
        '\t\t\t\t\t\t\t<tr><td height=18></td></tr>\n'
        '\t\t\t\t\t\t\t<tr><td height=24 align="center"><font name="hs12" color="CDB67F">'
        '%s</font></td></tr>\n'
        '\t\t\t\t\t\t\t<tr><td><center><img src="L2UI.SquareGray" width=470 height=1>'
        '</center></td></tr>\n'
        '\t\t\t\t\t\t\t<tr><td height=12></td></tr>\n'
        '\t\t\t\t\t\t\t<tr><td align="center">\n'
        '\t\t\t\t\t\t\t\t<table align=center border=0 cellpadding=3 cellspacing=3>\n'
        '%s%s'
        '\t\t\t\t\t\t\t\t</table>\n'
        '\t\t\t\t\t\t\t</td></tr>\n\t\t\t\t\t\t</table>\n\t\t\t\t\t</center>\n'
        '\t\t\t\t</td>\n\t\t\t</tr>\n\t\t</table>\n\t</body>\n</html>\n'
    ) % (title, "".join(rows_html), back_html)


def build_html(written):
    top = "bypass _bbstop;merchant/main.html"
    # main departments (Оружие+Броня replaced by единый R-Grade)
    main_buttons = [
        ("R-Grade", "bypass _bbstop;merchant/rgrade.html"),
        ("Прокачка", "bypass _bbstop;merchant/level.html"),
        ("Бижутерия", "bypass _bbstop;merchant/jewelry.html"),
        ("Аксессуары", "bypass _bbstop;merchant/accessory.html"),
        ("Краски", "bypass _bbstop;merchant/dyes.html"),
        ("Книги умений", "bypass _bbstop;merchant/books.html"),
        ("Заточка", "bypass _bbstop;merchant/enchant.html"),
        ("Крафт РБ", "bypass _bbstop;merchant/craft.html"),
        ("Бакалея", "bypass _bbstop;merchant/grocery.html"),
        ("Косметика/Клан", "bypass _bbstop;merchant/misc.html"),
    ]
    _writef(os.path.join(MERCHANT_DIR, "main.html"),
            _page("Магазин за адену - отделы", main_buttons), written)

    # Прокачка tiers
    lvl = lambda k: "bypass _bbsmultisell;%d,merchant/level" % MS[k]
    level_buttons = [
        ("Грейд B", lvl("tier_B")), ("Грейд A", lvl("tier_A")),
        ("Грейд S", lvl("tier_S")), ("Грейд S80", lvl("tier_S80")),
    ]
    _writef(os.path.join(MERCHANT_DIR, "level.html"),
            _page("Прокачка (оружие + сет по тирам)", level_buttons,
                  back=("<= В отделы", top)), written)

    # R-Grade department (all categories reachable in <=2 clicks)
    rg = lambda k: "bypass _bbsmultisell;%d,merchant/rgrade" % MS[k]
    tr = lambda i: "bypass _bbsmultisell;%d,merchant/rgrade" % MS["transcend"][i]
    rgrade_buttons = [
        ("R-Оружие (2 SA)", rg("r_weapon")),
        ("Bless-оружие", rg("bless_weapon")),
        ("PVE-оружие", rg("pve_weapon")),
        ("R-Броня (2 SA)", rg("r_armor")),
        ("Bless-броня", rg("bless_armor")),
        ("PVE-броня", rg("pve_armor")),
        ("Запредельный ур.1", tr(0)), ("Запредельный ур.2", tr(1)),
        ("Запредельный ур.3", tr(2)), ("Запредельный ур.4", tr(3)),
        ("Запредельный ур.5", tr(4)), ("Запредельный ур.6", tr(5)),
    ]
    _writef(os.path.join(MERCHANT_DIR, "rgrade.html"),
            _page("R-Grade (оружие и броня)", rgrade_buttons,
                  back=("<= В отделы", top)), written)


def _writef(path, content, written):
    if not DRY_RUN:
        with open(path, "w", encoding="utf-8") as fh:
            fh.write(content)
    written.append(path)


# --------------------------------------------------------------------------- cleanup
def cleanup_orphans(written):
    removed = []
    for msid in ORPHAN_MS:
        p = os.path.join(MULTISELL_DIR, "%d.xml" % msid)
        if os.path.exists(p):
            if not DRY_RUN:
                os.remove(p)
            removed.append(p)
    for name in ORPHAN_HTML:
        p = os.path.join(MERCHANT_DIR, name)
        if os.path.exists(p):
            if not DRY_RUN:
                os.remove(p)
            removed.append(p)
    return removed


# --------------------------------------------------------------------------- main
def main():
    global DRY_RUN
    ap = argparse.ArgumentParser()
    ap.add_argument("--dry-run", action="store_true")
    args = ap.parse_args()
    DRY_RUN = args.dry_run

    cat = load_catalog()
    report, written = {}, []

    build_level_tiers(cat, report, written)
    build_r_weapons(cat, report, written)
    build_r_armor(cat, report, written)
    build_transcend(cat, report, written)
    build_belts(cat, report, written)
    build_tshirts(cat, report, written)
    build_html(written)
    removed = cleanup_orphans(written)

    print("=== Alt+B main shop generation %s ===" % ("(DRY-RUN)" if DRY_RUN else ""))
    todo = report.pop("_todo", [])
    total = 0
    for k in sorted(report):
        print("  %-32s %d" % (k, report[k]))
        total += report[k]
    print("  %-32s %d" % ("TOTAL rows", total))
    print("--- written %d files, removed %d orphans ---" % (len(written), len(removed)))
    for p in removed:
        print("    removed:", os.path.relpath(p, DATA))
    if todo:
        print("--- TODO / unmapped (%d) ---" % len(todo))
        for t in todo:
            print("    TODO:", t)


if __name__ == "__main__":
    main()
