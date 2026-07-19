#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Печатает полные <item> блоки из stats/items по списку id.
   Использование: python3 tools/show_item.py 47214 47215 ..."""
import re, os, glob, sys

ITEMS_DIR = "/projects/sandbox/lineage2/server/game/data/stats/items"
ids = set(sys.argv[1:])
found = {}
for path in glob.glob(os.path.join(ITEMS_DIR, "*.xml")):
    data = open(path, encoding="utf-8").read()
    for m in re.finditer(r'<item\s+id="(\d+)".*?</item>', data, re.S):
        iid = m.group(1)
        if iid in ids:
            found[iid] = m.group(0)
for iid in sys.argv[1:]:
    print("="*80)
    print(found.get(iid, f"{iid}: НЕ НАЙДЕН"))
