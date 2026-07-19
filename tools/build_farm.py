#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Строит таблицу фарм-зон: реальный уровень мобов (из аудита) + БЕЗОПАСНАЯ
   координата (ближайшая точка админ-телепорта GM). Для ревью."""
import os, re, glob, math, sys
import zone_audit as za

BASE = "/projects/sandbox/lineage2/server/game/data"
ADMIN = os.path.join(BASE, "html/admin/teleports")

def load_admin_points():
    pts = []
    for f in glob.glob(os.path.join(ADMIN, "**/*.htm"), recursive=True):
        d = open(f, encoding="utf-8", errors="ignore").read()
        for m in re.finditer(r'admin_move_to (-?\d+) (-?\d+) (-?\d+)" value="([^"]+)"', d):
            x, y, z, name = int(m.group(1)), int(m.group(2)), int(m.group(3)), m.group(4)
            pts.append((x, y, z, name))
    return pts

ADMIN_PTS = load_admin_points()

def nearest_admin(x, y, z):
    best = None; bd = 1e18
    for ax, ay, az, name in ADMIN_PTS:
        d = (ax - x) ** 2 + (ay - y) ** 2  # 2D distance (Z менее важен)
        if d < bd:
            bd = d; best = (ax, ay, az, name)
    return best, math.sqrt(bd)

def main():
    skip_dirs = {"old", "Castles", "ClassMaster", "Others", "FantasyIsle", "Fearon"}
    files = []
    for f in glob.glob(os.path.join(za.SPAWNS, "*/*.xml")):
        if os.path.basename(os.path.dirname(f)) in skip_dirs:
            continue
        files.append(f)
    rows = []
    for f in sorted(files):
        r = za.process_file(f)
        if r:
            rows.append(r)
    rows.sort(key=lambda r: (r["med"], r["name"]))
    print(f"{'ЗОНА':30s} {'N':>4s} {'p10':>3s} {'мед':>3s} {'p90':>3s}  {'audit_coord':>22s}  расст  ближайший админ-тп")
    print("-" * 130)
    for r in rows:
        if r["count"] < 30:
            continue
        # исключить стартовые деревни/города
        if r["name"] in ("TalkingIslandVillage", "TownOfRune"):
            continue
        x, y, z = r["coord"]
        (ax, ay, az, aname), dist = nearest_admin(x, y, z)
        # безопасная координата: если админ-точка близко (<2500), берём её
        if dist < 2500:
            safe = f"{ax},{ay},{az}"
            src = f"ADMIN:{aname[:22]}"
        else:
            safe = f"{x},{y},{z}"
            src = f"audit (админ {aname[:16]} @{int(dist)})"
        print(f"{r['name']:30.30s} {r['count']:4d} {r['p10']:3d} {r['med']:3d} {r['p90']:3d}  "
              f"{safe:>22s}  {src}")

if __name__ == "__main__":
    main()
