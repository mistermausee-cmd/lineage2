#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Аудит фарм-зон: для каждого файла спавнов вычисляет реальное распределение
   уровней мобов (Monster) и репрезентативную координату телепорта.
   Обрабатывает точечные и territory-спавны.
Usage: python3 zone_audit.py            -> отчёт по всем зонам
       python3 zone_audit.py <substr>   -> детально по зонам с подстрокой в пути
"""
import os, re, glob, sys, statistics
import xml.etree.ElementTree as ET

BASE = "/projects/sandbox/lineage2/server/game/data"
NPCS = os.path.join(BASE, "stats/npcs")
SPAWNS = os.path.join(BASE, "spawns")

# --- 1. карта npc: id -> (level, type, name) ---
def load_npcs():
    m = {}
    for f in glob.glob(os.path.join(NPCS, "*.xml")):
        d = open(f, encoding="utf-8", errors="ignore").read()
        for mt in re.finditer(r'<npc\s+id="(\d+)"\s+level="(\d+)"\s+type="([^"]*)"\s+name="([^"]*)"', d):
            m[int(mt.group(1))] = (int(mt.group(2)), mt.group(3), mt.group(4))
    return m

NPC = load_npcs()

MONSTER_TYPES = {"Monster", "RaidBoss", "Guard", "GrandBoss", "Chest",
                 "FriendlyMonster", "DefenderMonster"}


def centroid(nodes):
    xs = [n[0] for n in nodes]; ys = [n[1] for n in nodes]
    return sum(xs) / len(xs), sum(ys) / len(ys)


def process_file(path):
    """Возвращает dict со статистикой зоны или None."""
    try:
        tree = ET.parse(path)
    except Exception:
        return None
    root = tree.getroot()
    # собираем (level, x, y, z, weight, is_monster)
    entries = []
    for spawn in root.iter("spawn"):
        for group in spawn.iter("group"):
            # territory?
            terr_nodes = []
            terr_z = []
            for terr in group.iter("territory"):
                for node in terr.iter("node"):
                    terr_nodes.append((int(node.get("x")), int(node.get("y"))))
                mn, mx = terr.get("minZ"), terr.get("maxZ")
                if mn and mx:
                    terr_z.append((int(mn) + int(mx)) // 2)
            tc = centroid(terr_nodes) if terr_nodes else None
            tz = int(statistics.mean(terr_z)) if terr_z else None
            for npc in group.iter("npc"):
                nid = npc.get("id")
                if not nid:
                    continue
                nid = int(nid)
                info = NPC.get(nid)
                if not info:
                    continue
                lvl, typ, name = info
                x, y, z = npc.get("x"), npc.get("y"), npc.get("z")
                cnt = int(npc.get("count", "1"))
                if x is not None:
                    px, py, pz = int(x), int(y), int(z)
                    w = 1
                elif tc is not None:
                    px, py = int(tc[0]), int(tc[1])
                    pz = tz if tz is not None else 0
                    w = cnt
                else:
                    continue
                entries.append((lvl, px, py, pz, w, typ))
    # только Monster для оценки уровня зоны
    mon = [(l, x, y, z, w) for (l, x, y, z, w, t) in entries if t in ("Monster", "FriendlyMonster", "DefenderMonster")]
    if not mon:
        return None
    # взвешенный список уровней
    levels = []
    for l, x, y, z, w in mon:
        levels.extend([l] * w)
    levels.sort()
    n = len(levels)
    def pct(p):
        return levels[min(n - 1, int(n * p))]
    # репрезентативная координата: медоид (самая частая точка/крупнейший кластер)
    # берём взвешенный «центр»: точку моба, ближайшую к медиане уровней
    med_lvl = levels[n // 2]
    # координата: усреднить точки мобов с уровнем близким к медиане
    near = [(x, y, z) for (l, x, y, z, w) in mon if abs(l - med_lvl) <= 2]
    if not near:
        near = [(x, y, z) for (l, x, y, z, w) in mon]
    cx = int(statistics.median([p[0] for p in near]))
    cy = int(statistics.median([p[1] for p in near]))
    cz = int(statistics.median([p[2] for p in near]))
    return {
        "name": os.path.splitext(os.path.basename(path))[0],
        "region": os.path.basename(os.path.dirname(path)),
        "count": n,
        "min": levels[0], "p10": pct(0.10), "med": med_lvl,
        "p90": pct(0.90), "max": levels[-1],
        "coord": (cx, cy, cz),
    }


def main():
    filt = sys.argv[1] if len(sys.argv) > 1 else None
    skip_dirs = {"old", "Castles", "ClassMaster", "Others", "FantasyIsle", "Fearon"}
    files = []
    for f in glob.glob(os.path.join(SPAWNS, "*/*.xml")):
        region = os.path.basename(os.path.dirname(f))
        if region in skip_dirs:
            continue
        files.append(f)
    rows = []
    for f in sorted(files):
        r = process_file(f)
        if r:
            rows.append(r)
    rows.sort(key=lambda r: (r["med"], r["region"], r["name"]))
    print(f"{'ЗОНА':32s} {'РЕГИОН':12s} {'N':>5s} {'мин':>4s} {'p10':>4s} {'мед':>4s} {'p90':>4s} {'макс':>4s}  координата")
    print("-" * 110)
    for r in rows:
        if filt and filt.lower() not in (r["name"] + r["region"]).lower():
            continue
        x, y, z = r["coord"]
        print(f"{r['name']:32.32s} {r['region']:12.12s} {r['count']:5d} "
              f"{r['min']:4d} {r['p10']:4d} {r['med']:4d} {r['p90']:4d} {r['max']:4d}  {x},{y},{z}")
    print(f"\nВсего зон с мобами: {len(rows)}")

if __name__ == "__main__":
    main()
