#!/usr/bin/env python3
"""Строит индекс предметов (id -> name, grade, price, type) из stats/items
   и печатает данные для запрошенных id."""
import re, os, glob, sys

ITEMS_DIR = "/projects/sandbox/lineage2/server/game/data/stats/items"

item_open = re.compile(r'<item\s+id="(\d+)"\s+name="([^"]*)"(?:\s+additionalName="([^"]*)")?\s+type="([^"]*)"')
grade_re = re.compile(r'<set name="crystal_type" val="([^"]*)"')
price_re = re.compile(r'<set name="price" val="(\d+)"')

index = {}
for path in glob.glob(os.path.join(ITEMS_DIR, "*.xml")):
    with open(path, encoding="utf-8") as f:
        data = f.read()
    # split by <item  ... </item>
    for m in re.finditer(r'<item\s+id="(\d+)".*?</item>', data, re.S):
        block = m.group(0)
        om = item_open.search(block)
        if not om:
            continue
        iid = om.group(1)
        name = om.group(2)
        add = om.group(3) or ""
        typ = om.group(4)
        g = grade_re.search(block)
        p = price_re.search(block)
        index[iid] = {
            "name": name, "add": add, "type": typ,
            "grade": g.group(1) if g else "-",
            "price": int(p.group(1)) if p else 0,
        }

if __name__ == "__main__":
    ids = sys.argv[1:]
    if not ids:
        print(f"Всего предметов в индексе: {len(index)}")
        sys.exit(0)
    for iid in ids:
        it = index.get(iid)
        if not it:
            print(f"{iid}: НЕ НАЙДЕН")
            continue
        print(f"{iid:>6}  grade={it['grade']:>4}  npc_price={it['price']:>13,}  [{it['type']}] {it['name']} {it['add']}")
