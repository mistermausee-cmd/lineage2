#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Извлекает (название, x, y, z) из админских teleport htm."""
import re, os, glob, sys

BASE = "/projects/sandbox/lineage2/server/game/data/html/admin/teleports"
btn = re.compile(r'action="bypass\s+(?:-h\s+)?admin_move_to\s+(-?\d+)\s+(-?\d+)\s+(-?\d+)"\s+value="([^"]*)"', re.I)
btn2 = re.compile(r'value="([^"]*)"\s+action="bypass\s+(?:-h\s+)?admin_move_to\s+(-?\d+)\s+(-?\d+)\s+(-?\d+)"', re.I)

out = {}
for path in glob.glob(os.path.join(BASE, "**", "*.htm"), recursive=True) + glob.glob(os.path.join(BASE, "*.htm")):
    data = open(path, encoding="utf-8", errors="ignore").read()
    rel = os.path.relpath(path, BASE)
    for m in btn.finditer(data):
        x, y, z, name = m.group(1), m.group(2), m.group(3), m.group(4).strip()
        out.setdefault(rel, []).append((name, x, y, z))
    for m in btn2.finditer(data):
        name, x, y, z = m.group(1).strip(), m.group(2), m.group(3), m.group(4)
        out.setdefault(rel, []).append((name, x, y, z))

filt = sys.argv[1] if len(sys.argv) > 1 else ""
total = 0
for rel in sorted(out):
    if filt and filt.lower() not in rel.lower():
        continue
    print(f"\n### {rel} ({len(out[rel])})")
    for name, x, y, z in out[rel]:
        print(f"  {name} | {x},{y},{z}")
        total += 1
print(f"\nВСЕГО: {sum(len(v) for v in out.values())}")
