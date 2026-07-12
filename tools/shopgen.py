#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# Генератор магазина (multisell + русское меню) для соло-сервера Grand Crusade.
# Парсит data/stats/items/*.xml, выбирает ВАЖНЫЕ предметы по правилам,
# генерирует мультиселлы (цена в адене id=57) и русское меню Community Board.
import os, re, glob

BASE = "/projects/sandbox/mobius-src/L2J_Mobius_04.0_GrandCrusade/dist/game/data"
ITEMS_DIR = os.path.join(BASE, "stats/items")
MULTISELL_DIR = os.path.join(BASE, "multisell")
HTML_DIR = os.path.join(BASE, "html/CommunityBoard/Custom/merchant")

item_re = re.compile(r'<item\s+id="(\d+)"\s+name="([^"]*)"(?:\s+additionalName="[^"]*")?\s+type="(\w+)"', re.S)

def parse_items():
    items = {}
    for f in glob.glob(os.path.join(ITEMS_DIR, "*.xml")):
        txt = open(f, encoding="utf-8", errors="replace").read()
        for p in txt.split("<item ")[1:]:
            block = "<item " + p.split("</item>")[0]
            m = item_re.search(block)
            if not m:
                continue
            iid = int(m.group(1))
            ct = re.search(r'crystal_type"\s+val="(\w+)"', block)
            bp = re.search(r'bodypart"\s+val="([a-z_;]+)"', block)
            items[iid] = {"id": iid, "name": m.group(2), "type": m.group(3),
                          "grade": ct.group(1) if ct else "",
                          "bodypart": bp.group(1) if bp else ""}
    return items

WTYPES = ("Shaper","Cutter","Slasher","Avenger","Fighter","Stormer","Thrower",
          "Shooter","Buster","Caster","Retributer","Dualsword","Dual Dagger","Dual Blunt Weapon")
ARMOR_BP = ("chest","legs","feet","gloves","head","alldress","onepiece")

def dedup_by_name(lst):
    seen = {}
    for it in sorted(lst, key=lambda x: x["id"]):
        seen.setdefault(it["name"], it)
    return sorted(seen.values(), key=lambda x: x["id"])

def base_weapons(items, grade, line):
    res = []
    for it in items.values():
        if it["type"] != "Weapon" or it["grade"] != grade:
            continue
        n = it["name"]
        if any(b in n for b in ("Blessed","Coupon","Appearance"," - ","Bloody","Ultimate","Transcendent")):
            continue
        if any(n == f"{line} {w}" for w in WTYPES):
            res.append(it)
    return dedup_by_name(res)

def base_armor(items, grade, line):
    res = []
    for it in items.values():
        if it["type"] != "Armor" or it["grade"] != grade or it["bodypart"] not in ARMOR_BP:
            continue
        n = it["name"]
        if not n.startswith(line + " "):
            continue
        if any(b in n for b in ("Blessed","Bloody","Ultimate","Transcendent","Bound","Appearance","Coupon")):
            continue
        res.append(it)
    return dedup_by_name(res)

def by_ids(items, ids):
    return [items[i] for i in ids if i in items]

def cloaks(items):
    return dedup_by_name([it for it in items.values() if it["bodypart"] == "back" and "Cloak" in it["name"]
        and it["grade"] in ("R","R95","R99")
        and not any(b in it["name"] for b in ("Appearance","Coupon","Box","Pack","Not in Use","Test"))])

def belts(items):
    return dedup_by_name([it for it in items.values() if it["bodypart"] == "waist" and "Belt" in it["name"]
        and it["grade"] in ("R","R95","R99")
        and not any(b in it["name"] for b in ("Appearance","Coupon","Box","Pack","Not in Use","Test"))])

def talismans(items):
    return dedup_by_name([it for it in items.values() if it["bodypart"] == "talisman"
        and not any(b in it["name"] for b in ("Appearance","Coupon","Box","Pack"))])

GEMS = ("Ruby","Sapphire","Emerald","Diamond","Opal","Obsidian","Pearl","Vital Spirit",
        "Cat's Eye","Tanzanite","Garnet","Aquamarine")
def brooch_jewels(items):
    res = [it for it in items.values() if it["bodypart"] in ("brooch","brooch_jewel")
        and any(g in it["name"] for g in GEMS) or (it["bodypart"] == "brooch" and "Brooch" in it["name"])]
    res = [it for it in res if not any(b in it["name"] for b in ("Appearance","Coupon","Box","Pack","Fragment","Piece"))]
    return dedup_by_name(res)

def top_life_stones(items, n=6):
    ls = []
    for it in items.values():
        m = re.match(r"^Life Stone - Lv\. (\d+)$", it["name"])
        if m:
            ls.append((int(m.group(1)), it))
    ls.sort(key=lambda x: -x[0])
    return [it for _, it in ls[:n]]

def hair_accessories(items):
    res = [it for it in items.values() if it["bodypart"] == "hairall"
        and it["grade"] in ("A","S","R","R95","R99")
        and not any(b in it["name"] for b in ("Appearance","Coupon","Box","Pack","Not in Use","Test","Event"))]
    return dedup_by_name(res)

# ---- prices (adena) ----
CAT_PRICE = {
    600001: 1_500_000_000, 600002: 650_000_000, 600003: 250_000_000,   # weapons R99/R95/R
    600004: 350_000_000,   600005: 150_000_000, 600006: 60_000_000,    # armor R99/R95/R (per piece)
    600007: 300_000_000,   600008: 200_000_000,                        # R99 jewelry / talismans
    600011: 500,           600017: 30_000_000,                         # shots (per unit, выше цены продажи) / life stones
    600025: 300_000_000,   600026: 250_000_000, 600031: 150_000_000,   # cloaks / belts / brooch jewels
    600032: 150_000_000,   600033: 30_000_000,  600034: 50_000_000,    # dyes / skill-enchant codex / hair
    600040: 5_000_000,     600041: 2_000_000,   600042: 5_000_000,     # attribute stones / craft mats / R recipes
    600043: 1_000_000,     600044: 300_000_000,                        # boosters / bracelets
}
# Бакалея (расходники): (id, цена за 1 шт.)
GROCERY = [(1540,6000),(5592,6000),(728,4000),
           (30357,300000),(30358,300000),(30359,300000),
           (1538,80000),(3936,210000)]
DYES = [17709,17710,17711,17712,17713,17714]   # Lv.5 Legendary STR/DEX/CON/INT/WIT/MEN
CODEX = [30297,30298,30299,30300]              # Superior Giant's Codex (skill enchant)
# Головные уборы (дают +1 к характеристике): классические стат-шапки + Archangel Circlet
HAIR = [9883,9884,9885,9886,9887,9888,9889,15484]
# Атрибутные камни (заточка стихий): Stones (базовые) + Crystals (высшие) Fire/Water/Earth/Wind/Dark/Holy
ATTRIBUTE = [22635,22636,22637,22638,22639,22640, 22641,22642,22643,22644,22645,22646]
# Крафт-материалы для эндгейм-шмота (РБ-крафт)
CRAFTMATS = [36515,36514,36524,36563,17267]
# Рецепты R: оружие Requiem + броня Eternal
RECIPES = list(range(36732,36746)) + [32301,32302,32303]
# Бустеры: вайталити + свитки бонуса опыта
BOOSTERS = [15440,17095,17096,17097,17098,17099]
# Браслеты (rbracelet, для брошки) + браслет агатиона
BRACELETS = [19449,19455,19461,19471,19474,10139]
EPIC_PRICE = {6660:1_500_000_000,6661:1_500_000_000,6662:1_500_000_000,
              6659:2_500_000_000,6658:3_000_000_000,8191:3_500_000_000,6657:5_000_000_000}
ENCH_PRICE = {17526:8_000_000,19447:100_000_000,17527:4_000_000,19448:12_000_000}

MULTISELL_TPL = '''<?xml version="1.0" encoding="UTF-8"?>
<list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../xsd/multisell.xsd">
	<npcs>
		<npc>-1</npc>
	</npcs>
{rows}
</list>
'''
ROW_TPL = ('\t<item>\n\t\t<ingredient id="57" count="{price}" />\n'
           '\t\t<production id="{iid}" count="1" /> <!-- {name} -->\n\t</item>')

def write_multisell(msid, entries):
    rows = "\n".join(ROW_TPL.format(price=p, iid=it["id"], name=it["name"]) for it, p in entries)
    open(os.path.join(MULTISELL_DIR, f"{msid}.xml"), "w", encoding="utf-8").write(
        MULTISELL_TPL.format(rows=rows))

def main():
    items = parse_items()
    fixed = lambda lst, msid: [(it, CAT_PRICE[msid]) for it in lst]
    cats = {
        600001: fixed(base_weapons(items,"R99","Amaranthine"), 600001),
        600002: fixed(base_weapons(items,"R95","Specter"), 600002),
        600003: fixed(base_weapons(items,"R","Apocalypse"), 600003),
        600004: fixed(base_armor(items,"R99","Eternal"), 600004),
        600005: fixed(base_armor(items,"R95","Seraph"), 600005),
        600006: fixed(base_armor(items,"R","Twilight"), 600006),
        600007: fixed(by_ids(items,[17447,17448,17449]), 600007),
        600008: fixed(talismans(items), 600008),
        600011: fixed(by_ids(items, [i for i in (17754,) if i in items]) +
                      [items[i] for i in items if items[i]["name"] in
                       ("Spiritshot (R-grade)","Blessed Spiritshot (R-grade)","Soulshot (R-grade)")], 600011),
        600017: fixed(top_life_stones(items), 600017),
        600040: fixed(by_ids(items, ATTRIBUTE), 600040),
        600041: fixed(by_ids(items, CRAFTMATS), 600041),
        600042: fixed(by_ids(items, RECIPES), 600042),
        600043: fixed(by_ids(items, BOOSTERS), 600043),
        600044: fixed(by_ids(items, BRACELETS), 600044),
        600025: fixed(cloaks(items), 600025),
        600026: fixed(belts(items), 600026),
        600031: fixed(brooch_jewels(items), 600031),
        600030: [(items[i], EPIC_PRICE[i]) for i in EPIC_PRICE if i in items],
        600012: [(items[i], ENCH_PRICE[i]) for i in (17526,19447) if i in items],
        600013: [(items[i], ENCH_PRICE[i]) for i in (17527,19448) if i in items],
        600032: fixed(by_ids(items, DYES), 600032),
        600033: fixed(by_ids(items, CODEX), 600033),
        600034: fixed(by_ids(items, HAIR), 600034),
        600035: [(items[i], p) for i, p in GROCERY if i in items],
    }
    # dedup shots by NAME (keep lowest id)
    seen=set(); dd=[]
    for it,p in sorted(cats[600011], key=lambda x:x[0]["id"]):
        if it["name"] not in seen: seen.add(it["name"]); dd.append((it,p))
    cats[600011]=dd

    os.makedirs(MULTISELL_DIR, exist_ok=True)
    total = 0
    written = set()
    for msid, entries in cats.items():
        if not entries:
            print(f"!! EMPTY {msid}")
            continue
        write_multisell(msid, entries)
        written.add(msid)
        total += len(entries)
        print(f"{msid}.xml : {len(entries)} items (пример: {entries[0][0]['name']})")
    print(f"\nВсего позиций: {total}")
    build_html(written)
    print("HTML меню магазина сгенерировано.")

# ---- department menu (Russian) ----
DEPARTMENTS = [
    ("weapons",  "Оружие",       [("Оружие R99",600001),("Оружие R95",600002),("Оружие R",600003)]),
    ("armor",    "Броня",        [("Броня R99",600004),("Броня R95",600005),("Броня R",600006)]),
    ("jewelry",  "Бижутерия",    [("Бижутерия R99",600007),("Эпик-бижутерия",600030),("Брошь и камни",600031),("Браслеты",600044)]),
    ("accessory","Аксессуары",   [("Талисманы",600008),("Плащи",600025),("Пояса",600026),("Головные уборы",600034)]),
    ("dyes",     "Краски",       [("Легендарные Lv.5",600032)]),
    ("books",    "Книги умений", [("Заточка умений (Кодекс)",600033)]),
    ("enchant",  "Заточка",      [("Свитки: оружие",600012),("Свитки: броня",600013),("Камни жизни",600017),("Атрибут (стихии)",600040)]),
    ("craft",    "Крафт РБ",     [("Материалы",600041),("Рецепты R",600042)]),
    ("grocery",  "Бакалея",      [("Расходники",600035),("Заряды (шоты)",600011),("Бустеры XP/вайт",600043)]),
]

def _btn(label, action, w=150):
    return (f'<button value="{label}" action="{action}" width={w} height=28 '
            f'back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">')

def _page(title, body_rows):
    rows = "\n".join(body_rows)
    return f'''<html noscrollbar>
	<body>
		<table width=700><tr><td height=10></td></tr></table>
		<table width=20>
			<tr>
				<td>%navigation%</td>
				<td>
					<center>
						<table border=0 cellpadding=0 cellspacing=0 width=555 height=455 background="L2UI_CT1.Windows_DF_TooltipBG">
							<tr><td height=18></td></tr>
							<tr><td height=24 align="center"><font name="hs12" color="CDB67F">{title}</font></td></tr>
							<tr><td><center><img src="L2UI.SquareGray" width=470 height=1></center></td></tr>
							<tr><td height=12></td></tr>
							<tr><td align="center">
								<table align=center border=0 cellpadding=3 cellspacing=3>
{rows}
								</table>
							</td></tr>
						</table>
					</center>
				</td>
			</tr>
		</table>
	</body>
</html>
'''

def build_html(written):
    os.makedirs(HTML_DIR, exist_ok=True)
    # main department page
    dep_rows = []
    deps = [d for d in DEPARTMENTS if any(mid in written for _, mid in d[2])]
    for i in range(0, len(deps), 2):
        cells = ""
        for key, title, _ in deps[i:i+2]:
            cells += f'\t\t\t\t\t\t\t\t\t<td><center>{_btn(title, f"bypass _bbstop;merchant/{key}.html", 170)}</center></td>\n'
        dep_rows.append(f"\t\t\t\t\t\t\t\t<tr>\n{cells}\t\t\t\t\t\t\t\t</tr>")
    open(os.path.join(HTML_DIR, "main.html"), "w", encoding="utf-8").write(
        _page("Магазин за адену — отделы", dep_rows))
    # department pages
    for key, title, buttons in DEPARTMENTS:
        buttons = [(lbl, mid) for lbl, mid in buttons if mid in written]
        if not buttons:
            continue
        rows = []
        for j in range(0, len(buttons), 2):
            cells = ""
            for lbl, mid in buttons[j:j+2]:
                cells += f'\t\t\t\t\t\t\t\t\t<td><center>{_btn(lbl, f"bypass _bbsmultisell;{mid},merchant/{key}")}</center></td>\n'
            rows.append(f"\t\t\t\t\t\t\t\t<tr>\n{cells}\t\t\t\t\t\t\t\t</tr>")
        rows.append('\t\t\t\t\t\t\t\t<tr><td height=10></td></tr>')
        rows.append(f'\t\t\t\t\t\t\t\t<tr><td><center>{_btn("Продать вещи","bypass _bbssell;merchant/"+key)}</center></td>'
                    f'<td><center>{_btn("◄ В отделы","bypass _bbstop;merchant/main.html")}</center></td></tr>')
        open(os.path.join(HTML_DIR, f"{key}.html"), "w", encoding="utf-8").write(_page(title, rows))

if __name__ == "__main__":
    main()
