#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""List all GrandBoss npcs (id, level, EN name) joined with RU name from json."""
import re, glob, os, json

DATA = "/projects/sandbox/lineage2/server/game/data"
NPCS = os.path.join(DATA, "stats/npcs")
ru = json.load(open("/projects/sandbox/lineage2/tools/_ru/npcname_ru.json", encoding="utf-8"))

rows = []
for f in glob.glob(os.path.join(NPCS, "*.xml")):
    d = open(f, encoding="utf-8", errors="ignore").read()
    for m in re.finditer(r'<npc\s+id="(\d+)"\s+level="(\d+)"\s+type="([^"]*)"\s+name="([^"]*)"', d):
        iid, lvl, typ, name = m.group(1), int(m.group(2)), m.group(3), m.group(4)
        if typ != "GrandBoss":
            continue
        run = ru.get(iid, {}).get("name", "")
        rows.append((lvl, iid, name, run))
rows.sort()
for lvl, iid, name, run in rows:
    print(f"{lvl:>3} | {iid} | {name:32s} | {run}")
print("total GrandBoss:", len(rows))
