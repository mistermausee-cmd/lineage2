#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Находит production-предметы, встречающиеся в НЕСКОЛЬКИХ активных мультиселлах,
   и показывает назначенную движком цену в каждом — для синхронизации."""
import re, os, importlib.util

BASE = "/projects/sandbox/lineage2/server/game/data/multisell"
CUSTOM = os.path.join(BASE, "custom")
ACTIVE = [600008,600011,600025,600026,600028,600030,600032,600033,600034,600035,
          600041,600043,600044,600045,600047,600048,600052,600053,600054,600055,600057,
          600060,600061,600062,600063,600064,600065,600066,600067,600068,600069,600070,600071,
          600090,600091,600100,600101,600102,600103,600104,600105,600106,600107,600108,600109,
          600110,600111,600112,600113]

def find_file(mid):
    c = os.path.join(CUSTOM, f"{mid}.xml")
    if os.path.exists(c): return c
    m = os.path.join(BASE, f"{mid}.xml")
    return m if os.path.exists(m) else None

prod_re = re.compile(r'<production\b[^>]*id="(\d+)"')
c57a = re.compile(r'<ingredient\s+id="57"\s+count="(\d+)"')
c57b = re.compile(r'<ingredient\s+count="(\d+)"\s+id="57"')

item_to_mids = {}   # pid -> {mid: adena_price}
names = {}
for mid in ACTIVE:
    path = find_file(mid)
    if not path: continue
    data = open(path, encoding="utf-8", newline='').read()
    for m in re.finditer(r'<item\b[^>]*>.*?</item>', data, re.S):
        block = m.group(0)
        p = prod_re.search(block)
        if not p: continue
        pid = p.group(1)
        ma = c57a.search(block) or c57b.search(block)
        price = int(ma.group(1)) if ma else None
        item_to_mids.setdefault(pid, {})[mid] = price
        cm = re.findall(r'<!--\s*(.*?)\s*-->', block)
        if cm and pid not in names: names[pid] = cm[-1][:40]

print("=== ПРЕДМЕТЫ В НЕСКОЛЬКИХ ОТДЕЛАХ (цена адены) ===")
for pid, mids in sorted(item_to_mids.items()):
    if len(mids) > 1:
        prices = set(v for v in mids.values() if v is not None)
        flag = "  <<< РАЗНЫЕ ЦЕНЫ!" if len(prices) > 1 else ""
        detail = ", ".join(f"{mid}={(v or 0)//1000000}M" for mid, v in sorted(mids.items()))
        print(f"{pid} ({names.get(pid,'')}): {detail}{flag}")
