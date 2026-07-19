#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Делает указанные предметы непродаваемыми NPC (is_sellable=false),
   чтобы их можно было безопасно продавать в магазине за 1 адену без эксплойта.
   Затрагивает: бесконечные колчаны/болты (600112) и еду петов (600104)."""
import re, glob, os

ITEMS_DIR = "/projects/sandbox/lineage2/server/game/data/stats/items"

IDS = [
    # болты/стрелы (бесконечные колчаны) 600112
    30375,30376,30377,30378,30379,30380,
    32249,32250,32251,32252,32253,32254,32256,32257,32258,32259,32260,32261,
    # еда петов 600104
    2515,4038,5168,5169,6643,6644,7582,9668,10425,14818,
]
IDS = set(str(i) for i in IDS)

patched, already = 0, 0
for path in glob.glob(os.path.join(ITEMS_DIR, "*.xml")):
    # newline='' сохраняет исходные окончания строк (CRLF) без нормализации
    data = open(path, encoding="utf-8", newline='').read()
    changed = False

    def repl(m):
        global patched, already, changed
        block = m.group(0)
        iid = re.match(r'<item\s+id="(\d+)"', block).group(1)
        if iid not in IDS:
            return block
        if 'name="is_sellable"' in block:
            already += 1
            # уже есть — принудительно выставим false
            nb = re.sub(r'(<set name="is_sellable" val=")[a-z]+(")', r'\1false\2', block)
            if nb != block: changed = True; patched += 1; already -= 1
            return nb
        # вставляем после открывающего тега <item ...> (сохраняя стиль переноса строк файла)
        nl = "\r\n" if "\r\n" in block else "\n"
        nb = re.sub(r'(<item\s+[^>]*>)', r'\1' + nl + '\t\t<set name="is_sellable" val="false" />',
                    block, count=1)
        changed = True; patched += 1
        return nb

    new = re.sub(r'<item\s+id="\d+".*?</item>', repl, data, flags=re.S)
    if changed:
        # newline='' — записываем строку как есть, сохраняя CRLF
        with open(path, "w", encoding="utf-8", newline='') as fh:
            fh.write(new)

print(f"Проставлено is_sellable=false: {patched}; уже было: {already}")
