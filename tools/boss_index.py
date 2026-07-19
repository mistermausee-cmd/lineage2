#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Полный индекс боссов: RaidBoss/GrandBoss из npcs, сшитые с координатами из ВСЕХ spawn-файлов."""
import re, glob, os, sys

DATA = "/projects/sandbox/lineage2/server/game/data"
NPCS = os.path.join(DATA, "stats/npcs")
SPAWNS = os.path.join(DATA, "spawns")

# 1) индекс npc: id -> (name, level, type)
npc = {}
for f in glob.glob(os.path.join(NPCS, "*.xml")):
    d = open(f, encoding="utf-8", errors="ignore").read()
    for m in re.finditer(r'<npc\s+id="(\d+)"\s+level="(\d+)"\s+type="([^"]*)"\s+name="([^"]*)"', d):
        npc[m.group(1)] = (m.group(4), int(m.group(2)), m.group(3))

# 2) индекс спавнов: id -> [(x,y,z), ...]  (мировые спавны)
spawncoords = {}
for f in glob.glob(os.path.join(SPAWNS, "**", "*.xml"), recursive=True):
    d = open(f, encoding="utf-8", errors="ignore").read()
    # разбиваем на <spawn>..</spawn>
    for sp in re.finditer(r'<spawn\b.*?</spawn>', d, re.S):
        b = sp.group(0)
        # территориальный центр (если есть)
        nodes = re.findall(r'<node x="(-?\d+)" y="(-?\d+)"', b)
        zm = re.search(r'minZ="(-?\d+)"\s+maxZ="(-?\d+)"', b)
        terr_c = None
        if nodes:
            xs = [int(x) for x, y in nodes]; ys = [int(y) for x, y in nodes]
            zc = (int(zm.group(1)) + int(zm.group(2)))//2 if zm else -3000
            terr_c = (sum(xs)//len(xs), sum(ys)//len(ys), zc)
        # npc внутри
        for nm in re.finditer(r'<npc\s+id="(\d+)"([^>]*)/?>', b):
            iid = nm.group(1); attrs = nm.group(2)
            xm = re.search(r'x="(-?\d+)"\s+y="(-?\d+)"\s+z="(-?\d+)"', attrs)
            if xm:
                spawncoords.setdefault(iid, []).append((int(xm.group(1)), int(xm.group(2)), int(xm.group(3))))
            elif terr_c:
                spawncoords.setdefault(iid, []).append(terr_c)

# 3) объединение
mode = sys.argv[1] if len(sys.argv) > 1 else "raid"
rows = []
for iid, (name, lvl, typ) in npc.items():
    if mode == "raid" and typ != "RaidBoss": continue
    if mode == "grand" and typ != "GrandBoss": continue
    coords = spawncoords.get(iid)
    if not coords:
        continue  # нет мирового спавна — пропускаем
    x, y, z = coords[0]
    rows.append((lvl, iid, name, x, y, z))

rows.sort()
for lvl, iid, name, x, y, z in rows:
    print(f"{lvl:>3} | {iid} | {name} | {x},{y},{z}")
print(f"\n{mode}: {len(rows)} боссов с мировым спавном")
