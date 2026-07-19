#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Рейд-боссы: id + уровень + имя (из npcs) + центр территории (из RaidbossSpawns.xml)."""
import re, glob, os

NPCS = "/projects/sandbox/lineage2/server/game/data/stats/npcs"
SPAWNS = "/projects/sandbox/lineage2/server/game/data/spawns/RaidbossSpawns.xml"

# индекс npc: id -> (name, level, type)
npc = {}
for f in glob.glob(os.path.join(NPCS, "*.xml")):
    d = open(f, encoding="utf-8", errors="ignore").read()
    for m in re.finditer(r'<npc\s+id="(\d+)"[^>]*level="(\d+)"[^>]*type="([^"]*)"[^>]*?(?:name="([^"]*)")?', d):
        iid = m.group(1)
        # name may be before level; re-extract robustly
        tag = m.group(0)
        nm = re.search(r'name="([^"]*)"', tag)
        npc[iid] = (nm.group(1) if nm else "", int(m.group(2)), m.group(3))

data = open(SPAWNS, encoding="utf-8").read()
rows = []
for sp in re.finditer(r'<spawn\b.*?</spawn>', data, re.S):
    block = sp.group(0)
    nodes = re.findall(r'<node x="(-?\d+)" y="(-?\d+)"', block)
    zmatch = re.search(r'minZ="(-?\d+)" maxZ="(-?\d+)"', block)
    npcline = re.search(r'<npc id="(\d+)"[^>]*/>\s*(?:<!--\s*(.*?)\s*-->)?', block)
    if not npcline or not nodes:
        continue
    iid = npcline.group(1)
    cmt = (npcline.group(2) or "").strip()
    xs = [int(x) for x, y in nodes]; ys = [int(y) for x, y in nodes]
    cx, cy = sum(xs)//len(xs), sum(ys)//len(ys)
    cz = (int(zmatch.group(1)) + int(zmatch.group(2)))//2 if zmatch else -3000
    name, lvl, typ = npc.get(iid, ("", 0, "?"))
    name = name or cmt or "?"
    rows.append((lvl, iid, name, cx, cy, cz, typ))

rows.sort()
for lvl, iid, name, cx, cy, cz, typ in rows:
    print(f"{lvl:>3} | {iid} | {name} | {cx},{cy},{cz} | {typ}")
print(f"\nВСЕГО РБ-спавнов: {len(rows)}")
