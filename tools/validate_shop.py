#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Валидатор вывода магазина Alt+B (Требования 19.2, 19.4, 15.3, 18.1, 1.4, 1.5, 16.x).

Проверки:
  1. Каждый 6000XX.xml — well-formed + валиден против multisell.xsd.
  2. В каждом мультиселле есть <npcs><npc>-1</npc></npcs> и ingredient id="57".
  3. Каждый production id резолвится в Item_Catalog.
  4. Все кнопки HTML указывают на существующие merchant/*.html либо
     _bbsmultisell;{msid} созданных категорий.
  5. Каталог вывода содержит только .xml/.html (нет .java/.class).
  6. Сверка с эталоном L2Scripts: крупные классы предметов покрыты (иначе l2scripts_gap).

Использование:
  python tools/validate_shop.py --data-dir server/game/data
"""
import os
import re
import sys
import glob
import argparse
import subprocess

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import shopgen  # noqa: E402


MS_GLOB = "6000[0-9][0-9].xml"

# Крупные классы предметов эталона L2Scripts (Adena/Community shop) — Требование 16.1
L2SCRIPTS_CLASSES = {
    "weapons_by_grade": lambda cats: any(k.startswith("weapon_") for k in cats),
    "armor_by_grade": lambda cats: any(k.startswith("armor_") for k in cats),
    "jewelry": lambda cats: any(k.startswith("jewel_") and k != "jewel_epic" for k in cats),
    "epic_jewelry": lambda cats: "jewel_epic" in cats,
    "brooch_jewels": lambda cats: "brooch_and_jewels" in cats,
    "bracelets": lambda cats: "bracelets" in cats,
    "talismans": lambda cats: "talismans" in cats,
    "dyes": lambda cats: "dyes" in cats,
    "stat_hats": lambda cats: "stat_hats" in cats,
    "cloaks": lambda cats: "cloaks" in cats,
    "belts": lambda cats: "belts" in cats,
    "enchant": lambda cats: "enchant_weapon" in cats or "enchant_armor" in cats,
    "attribute": lambda cats: "attribute_stones" in cats,
    "shots": lambda cats: "shots" in cats,
    "life_stones": lambda cats: "life_stones" in cats,
}


def validate(data_dir):
    multisell_dir = os.path.join(data_dir, "multisell")
    html_dir = os.path.join(data_dir, "html", "CommunityBoard", "Custom", "merchant")
    items_dir = os.path.join(data_dir, "stats", "items")
    xsd = os.path.join(data_dir, "xsd", "multisell.xsd")

    errors = []
    warnings = []

    # --- Item_Catalog ---
    rep = shopgen.new_report()
    items = shopgen.parse_items(items_dir, rep)

    ms_files = sorted(glob.glob(os.path.join(multisell_dir, MS_GLOB)))
    if not ms_files:
        errors.append("Не найдено ни одного 6000XX.xml — генератор не запускался?")
        return errors, warnings

    # Дубли id в подкаталогах (MultisellData грузит рекурсивно) — Требование 19.1
    dup = glob.glob(os.path.join(multisell_dir, "**", MS_GLOB), recursive=True)
    seen_names = {}
    for p in dup:
        base = os.path.basename(p)
        seen_names.setdefault(base, []).append(p)
    for base, paths in seen_names.items():
        if len(paths) > 1:
            errors.append("Дублирующийся multisell id %s в: %s"
                          % (base, ", ".join(paths)))

    have_xmllint = _has_xmllint()
    produced_msids = set()
    total_rows = 0

    for path in ms_files:
        name = os.path.basename(path)
        msid = name[:-4]
        produced_msids.add(msid)
        txt = open(path, encoding="utf-8").read()

        # 1. XSD / well-formed
        if have_xmllint:
            rc = subprocess.run(
                ["xmllint", "--noout", "--schema", xsd, path],
                capture_output=True, text=True)
            if rc.returncode != 0:
                errors.append("%s: XSD-валидация не пройдена: %s"
                              % (name, rc.stderr.strip().splitlines()[-1:]))
        else:
            try:
                import xml.dom.minidom as md
                md.parseString(txt)
            except Exception as exc:  # noqa: BLE001
                errors.append("%s: не well-formed XML: %s" % (name, exc))

        # 2. npcs -1
        if not re.search(r"<npcs>\s*<npc>\s*-1\s*</npc>\s*</npcs>", txt):
            errors.append("%s: отсутствует <npcs><npc>-1</npc></npcs>" % name)

        # 2b. ingredient только адена id=57
        for ing in re.findall(r'<ingredient id="(\d+)"', txt):
            if ing != "57":
                errors.append("%s: ingredient id=%s (ожидалось 57)" % (name, ing))

        # 3. production id резолвится в каталоге
        prods = re.findall(r'<production id="(\d+)"', txt)
        total_rows += len(prods)
        for pid in prods:
            if int(pid) not in items:
                errors.append("%s: production id=%s отсутствует в Item_Catalog"
                              % (name, pid))

    # --- HTML: ссылки кнопок ---
    html_files = {os.path.basename(p) for p in glob.glob(os.path.join(html_dir, "*.html"))}
    if "main.html" not in html_files:
        errors.append("Нет merchant/main.html")
    for hp in sorted(html_files):
        htxt = open(os.path.join(html_dir, hp), encoding="utf-8").read()
        for target in re.findall(r"_bbstop;merchant/([\w./-]+\.html)", htxt):
            if os.path.basename(target) not in html_files:
                errors.append("%s: ссылка на несуществующую страницу %s" % (hp, target))
        for msid in re.findall(r"_bbsmultisell;(\d+)", htxt):
            if msid not in produced_msids:
                errors.append("%s: ссылка на несозданный мультиселл %s" % (hp, msid))

    # 5. Только .xml/.html в выводе (нет .java/.class)
    for d in (multisell_dir, html_dir):
        for p in glob.glob(os.path.join(d, "*")):
            if os.path.isfile(p) and os.path.splitext(p)[1] in (".java", ".class"):
                errors.append("Недопустимый файл вывода: %s" % p)

    # 6. Сверка с эталоном L2Scripts
    covered = produced_cats(multisell_dir)
    gap = [cls for cls, fn in L2SCRIPTS_CLASSES.items() if not fn(covered)]
    for cls in gap:
        warnings.append("l2scripts_gap: класс не покрыт — %s" % cls)

    print("Проверено мультиселлов: %d" % len(ms_files))
    print("Всего строк <item> (production): %d" % total_rows)
    print("HTML-страниц merchant: %d" % len(html_files))
    print("Покрытых крупных классов L2Scripts: %d/%d"
          % (len(L2SCRIPTS_CLASSES) - len(gap), len(L2SCRIPTS_CLASSES)))
    return errors, warnings


def produced_cats(multisell_dir):
    """Восстанавливает множество категорий из структуры генератора + факта записи."""
    produced = set()
    ms_present = {os.path.basename(p)[:-4]
                  for p in glob.glob(os.path.join(multisell_dir, MS_GLOB))}
    for _dk, _dt, _p2, cats in shopgen.build_structure():
        for ck, _ct, msid in cats:
            if str(msid) in ms_present:
                produced.add(ck)
    return produced


def _has_xmllint():
    try:
        subprocess.run(["xmllint", "--version"], capture_output=True)
        return True
    except (OSError, FileNotFoundError):
        return False


def main(argv=None):
    ap = argparse.ArgumentParser(description="Валидатор вывода магазина Alt+B")
    ap.add_argument("--data-dir", default="server/game/data")
    args = ap.parse_args(argv)

    errors, warnings = validate(args.data_dir)
    print()
    if warnings:
        print("ПРЕДУПРЕЖДЕНИЯ (%d):" % len(warnings))
        for w in warnings:
            print("  - " + w)
    if errors:
        print("ОШИБКИ (%d):" % len(errors))
        for e in errors:
            print("  - " + e)
        print("\nВАЛИДАЦИЯ НЕ ПРОЙДЕНА")
        return 1
    print("\nВАЛИДАЦИЯ ПРОЙДЕНА: ошибок нет.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
