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

# ---- prices (adena) ----
CAT_PRICE = {
    600001: 1_500_000_000, 600002: 650_000_000, 600003: 250_000_000,   # weapons R99/R95/R
    600004: 350_000_000,   600005: 150_000_000, 600006: 60_000_000,    # armor R99/R95/R (per piece)
    600007: 300_000_000,   600008: 200_000_000,                        # R99 jewelry / talismans
    600011: 100,           600017: 30_000_000,                         # shots (per unit) / life stones
    600025: 300_000_000,   600026: 250_000_000, 600031: 150_000_000,   # cloaks / belts / brooch jewels
}
EPIC_PRICE = {6660:1_500_000_000,6661:1_500_000_000,6662:1_500_000_000,
              6659:2_500_000_000,6658:3_000_000_000,8191:3_500_000_000,6657:5_000_000_000}
ENCH_PRICE = {17526:8_000_000,19447:25_000_000,17527:4_000_000,19448:12_000_000}

MULTISELL_TPL = '''<?xml version="1.0" encoding="UTF-8"?>
<list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="../xsd/multisell.xsd">
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
        600025: fixed(cloaks(items), 600025),
        600026: fixed(belts(items), 600026),
        600031: fixed(brooch_jewels(items), 600031),
        600030: [(items[i], EPIC_PRICE[i]) for i in EPIC_PRICE if i in items],
        600012: [(items[i], ENCH_PRICE[i]) for i in (17526,19447) if i in items],
        600013: [(items[i], ENCH_PRICE[i]) for i in (17527,19448) if i in items],
    }
    # dedup shots by NAME (keep lowest id)
    seen=set(); dd=[]
    for it,p in sorted(cats[600011], key=lambda x:x[0]["id"]):
        if it["name"] not in seen: seen.add(it["name"]); dd.append((it,p))
    cats[600011]=dd

    os.makedirs(MULTISELL_DIR, exist_ok=True)
    total = 0
    for msid, entries in cats.items():
        if not entries:
            print(f"!! EMPTY {msid}")
            continue
        write_multisell(msid, entries)
        total += len(entries)
        print(f"{msid}.xml : {len(entries)} items (пример: {entries[0][0]['name']})")
    print(f"\nВсего позиций: {total}")

if __name__ == "__main__":
    main()
