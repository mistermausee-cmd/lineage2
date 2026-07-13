#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Порт магазина L2Scripts (Alt+B / Community Board) в формат Mobius как ОТДЕЛЬНАЯ
вкладка-эталон «Магазин L2Scripts».

Источник (L2Scripts, извлечён из ветки alt-b-shop-overhaul во временный каталог):
    <SRC>/multisell/community/*.xml   — категории вкладки shop-adena + донат (-1001..-1008)
    <SRC>/multisell/gmshop/*.xml       — детальный adena-shop (1000001..1000020)

Назначение (Mobius, рабочее дерево репозитория):
    server/game/data/multisell/610xxx.xml                        — новые мультиселлы
    server/game/data/html/CommunityBoard/Custom/l2sref/*.html    — страницы вкладки-эталона

Ключевые правила:
  * Обёртка Mobius: <list ... xsd> + <npcs><npc>-1</npc></npcs>, ingredient(s) затем production(s).
  * production id/count переносятся из L2Scripts как есть; production-id, которых нет
    в каталоге Mobius (stats/items), пропускаются и логируются.
  * Валюта-ингредиент: исходный id сохраняется. Для не-адены count оставляем как в L2Scripts.
    Для адены (id=57) применяем АНТИ-ЭКСПЛОЙТ:
        цена = max(исходная_цена, NPC_sell_back+1, грейд-минимум для экипировки)
    где NPC_sell_back = price//2 (0 если предмет несдаваемый), а при count="-1"
    (авто-цена) или цене-заглушке база берётся от NPC store price (поле price).
  * Новый диапазон id 610000+ (не пересекается с существующими 6000XX).
  * Bypass как в текущем Alt+B Mobius:
        страницы   -> _bbstop;l2sref/<page>.html
        мультиселлы-> _bbsmultisell;<newid>,l2sref/<page>
"""
import os
import re
import sys
import glob

# ---------------------------------------------------------------------------
# Пути
# ---------------------------------------------------------------------------
REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.environ.get(
    "L2S_SRC",
    "/projects/sandbox/l2s_tmp/l2scripts/dist/gameserver/data/multisell",
)
DATA = os.path.join(REPO_ROOT, "server", "game", "data")
ITEMS_DIR = os.path.join(DATA, "stats", "items")
MULTISELL_DIR = os.path.join(DATA, "multisell")
HTML_DIR = os.path.join(DATA, "html", "CommunityBoard", "Custom", "l2sref")

# ---------------------------------------------------------------------------
# Индекс предметов Mobius (id -> price / sellable / grade / type / name)
# ---------------------------------------------------------------------------
ITEM_HEAD_RE = re.compile(
    r'<item\s+id="(\d+)"\s+name="([^"]*)"(?:\s+additionalName="[^"]*")?\s+type="(\w+)"'
)


def parse_items():
    items = {}
    for f in glob.glob(os.path.join(ITEMS_DIR, "*.xml")):
        txt = open(f, encoding="utf-8", errors="replace").read()
        for part in txt.split("<item ")[1:]:
            block = "<item " + part.split("</item>")[0]
            m = ITEM_HEAD_RE.search(block)
            if not m:
                continue
            iid = int(m.group(1))
            price = re.search(r'name="price"\s+val="(\d+)"', block)
            grade = re.search(r'name="crystal_type"\s+val="(\w+)"', block)
            sellable = re.search(r'name="is_sellable"\s+val="false"', block)
            items[iid] = {
                "id": iid,
                "name": m.group(2),
                "type": m.group(3),
                "price": int(price.group(1)) if price else 0,
                "grade": grade.group(1) if grade else "",
                "sellable": sellable is None,
            }
    return items


# Разумный грейд-минимум цены (только для экипировки Weapon/Armor)
GRADE_MIN = {
    "": 0, "NONE": 0, "D": 2000, "C": 15000, "B": 60000, "A": 300000,
    "S": 1000000, "S80": 2000000, "S84": 3000000,
    "R": 5000000, "R95": 50000000, "R99": 200000000,
}

ADENA = 57


def adena_price(items, pid, orig_count):
    """Анти-эксплойт цена покупки за адену для предмета pid."""
    it = items.get(pid)
    price = it["price"] if it else 0
    sellable = it["sellable"] if it else True
    grade = it["grade"] if it else ""
    itype = it["type"] if it else ""
    sell_back = (price // 2) if sellable else 0
    gmin = GRADE_MIN.get(grade, 0) if itype in ("Weapon", "Armor") else 0
    if orig_count is None or orig_count <= 0:          # count="-1" (авто) или заглушка
        base = price if price > 0 else gmin
    else:
        base = orig_count
    return max(base, sell_back + 1, gmin, 1)


# ---------------------------------------------------------------------------
# Разбор исходных мультиселлов L2Scripts
# ---------------------------------------------------------------------------
ITEM_BLOCK_RE = re.compile(r"<item>(.*?)</item>", re.S)
ING_RE = re.compile(r'<ingredient\s+id="(-?\d+)"\s+count="(-?\d+)"')
PROD_RE = re.compile(r'<production\s+id="(-?\d+)"\s+count="(-?\d+)"')


def parse_source_multisell(path):
    """Возвращает список item'ов: [{'ingredients': [(id,count)...],
                                    'productions': [(id,count)...]}]"""
    txt = open(path, encoding="utf-8", errors="replace").read()
    out = []
    for block in ITEM_BLOCK_RE.findall(txt):
        ings = [(int(i), int(c)) for i, c in ING_RE.findall(block)]
        prods = [(int(i), int(c)) for i, c in PROD_RE.findall(block)]
        if not prods:
            continue
        out.append({"ingredients": ings, "productions": prods})
    return out


# ---------------------------------------------------------------------------
# Каталог вкладки-эталона (порядок = назначение новых id)
#   каждая запись: (source_file_basename, human_title)
# ---------------------------------------------------------------------------
SECTION_GENERAL = [
    ("Оружие", [
        ("-100001", "Без грейда"), ("-100002", "D грейд"), ("-100003", "C грейд"),
        ("-100004", "B грейд"), ("-100005", "A грейд"), ("-100006", "S грейд"),
    ]),
    ("Броня", [
        ("-200001", "Без грейда"), ("-200002", "D грейд"), ("-200003", "C грейд"),
        ("-200004", "B грейд"), ("-200005", "A грейд"), ("-200006", "S грейд"),
    ]),
    ("Украшения и аксессуары", [
        ("-300001", "Бижутерия"), ("-300002", "Браслеты"), ("-300003", "Пояса"),
        ("-300004", "Рубашки"), ("-300005", "Плащи"), ("-300006", "Прически"),
        ("-300007", "Агатионы"),
    ]),
    ("Фарм", [
        ("-400001", "Расходники"), ("-400002", "SS / BSS"), ("-400003", "Руны"),
    ]),
    ("Прочее", [
        ("-400004", "Трансформации"), ("-400005", "Квест / Клан"), ("-400006", "Краски"),
        ("-400007", "Петы"), ("-400008", "Внешность"),
    ]),
    ("Улучшения", [
        ("-500001", "Заточка"), ("-500002", "Атрибуты"), ("-500003", "Кристаллы"),
    ]),
    ("Умения", [
        ("-600001", "Заточка умений"), ("-600002", "Книги"),
    ]),
    ("Валюта", [
        ("-700001", "Обмен на адену"), ("-700002", "Слава (Fame)"), ("-700003", "PC-очки"),
    ]),
]

SECTION_DONATE = [
    ("Донат", [
        ("-1001", "Сувениры"), ("-1002", "Улучшения"), ("-1003", "Агатионы"),
        ("-1004", "Прически"), ("-1005", "Плащи"), ("-1006", "Броня"),
        ("-1007", "Оружие"), ("-1008", "Бижутерия"),
    ]),
]

SECTION_GMSHOP = [
    ("Adena Shop (детально)", [
        ("1000001", "Заряды (шоты)"), ("1000002", "Благословл. SPS"),
        ("1000003", "Расходники"), ("1000004", "Свитки"),
        ("1000005", "Аксессуары"), ("1000006", "Оружие B"),
        ("1000007", "Оружие A"), ("1000008", "Оружие S"),
        ("1000009", "Оружие S80"), ("1000010", "Оружие SA"),
        ("1000011", "Броня B"), ("1000012", "Броня A"),
        ("1000013", "Броня S"), ("1000014", "Броня S80"),
        ("1000015", "Заточка брони"), ("1000016", "Краски"),
        ("1000017", "Петы"), ("1000018", "Аксессуары (доп.)"),
        ("1000019", "Броня R"), ("1000020", "Оружие R"),
    ]),
]

# Секция -> (подпапка источника, страница вкладки, базовый id, заголовок раздела)
SECTIONS = [
    ("general", "community", SECTION_GENERAL, 610001, "Общий магазин"),
    ("donate", "community", SECTION_DONATE, 610101, "Донат-магазин"),
    ("gmshop", "gmshop", SECTION_GMSHOP, 610201, "Adena Shop"),
]

# ---------------------------------------------------------------------------
# Шаблоны вывода
# ---------------------------------------------------------------------------
MULTISELL_TPL = (
    '<?xml version="1.0" encoding="UTF-8"?>\n'
    '<list xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" '
    'xsi:noNamespaceSchemaLocation="../xsd/multisell.xsd">\n'
    '\t<npcs>\n\t\t<npc>-1</npc>\n\t</npcs>\n'
    '{rows}\n'
    '</list>\n'
)


def render_multisell(entries):
    """entries: список item'ов уже в целевом виде."""
    rows = []
    for it in entries:
        lines = ["\t<item>"]
        for iid, cnt in it["ingredients"]:
            lines.append('\t\t<ingredient id="%d" count="%d" />' % (iid, cnt))
        for iid, cnt, name in it["productions"]:
            lines.append('\t\t<production id="%d" count="%d" /> <!-- %s -->'
                         % (iid, cnt, name))
        lines.append("\t</item>")
        rows.append("\n".join(lines))
    return MULTISELL_TPL.format(rows="\n".join(rows))


def build_target_items(items, src_items, report):
    """Преобразует исходные item'ы в целевой вид с анти-эксплойт ценами.
    Возвращает (target_entries, skipped_production_ids)."""
    target = []
    skipped = []
    for si in src_items:
        prods = []
        ok = True
        for pid, pcnt in si["productions"]:
            it = items.get(pid)
            if it is None:
                skipped.append(pid)
                ok = False
                break
            prods.append((pid, pcnt if pcnt > 0 else 1, it["name"]))
        if not ok:
            continue
        ings = []
        # если несколько production в item'e — для расчёта адены берём первый
        main_pid = si["productions"][0][0]
        for iid, cnt in si["ingredients"]:
            if iid == ADENA:
                ings.append((ADENA, adena_price(items, main_pid, cnt)))
            else:
                ings.append((iid, cnt if cnt > 0 else 1))
        target.append({"ingredients": ings, "productions": prods})
    return target, skipped


# ---------------------------------------------------------------------------
# HTML вкладки-эталона
# ---------------------------------------------------------------------------
def _btn(label, action, w=150):
    return ('<button value="%s" action="%s" width=%d height=28 '
            'back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">'
            % (label, action, w))


def _page(title, body_rows):
    rows = "\n".join(body_rows)
    return ('<html noscrollbar>\n\t<body>\n'
            '\t\t<table width=700><tr><td height=10></td></tr></table>\n'
            '\t\t<table width=20>\n\t\t\t<tr>\n'
            '\t\t\t\t<td>%%navigation%%</td>\n\t\t\t\t<td>\n\t\t\t\t\t<center>\n'
            '\t\t\t\t\t\t<table border=0 cellpadding=0 cellspacing=0 width=555 height=455 '
            'background="L2UI_CT1.Windows_DF_TooltipBG">\n'
            '\t\t\t\t\t\t\t<tr><td height=18></td></tr>\n'
            '\t\t\t\t\t\t\t<tr><td height=24 align="center">'
            '<font name="hs12" color="CDB67F">%s</font></td></tr>\n'
            '\t\t\t\t\t\t\t<tr><td><center><img src="L2UI.SquareGray" width=470 height=1>'
            '</center></td></tr>\n'
            '\t\t\t\t\t\t\t<tr><td height=12></td></tr>\n'
            '\t\t\t\t\t\t\t<tr><td align="center">\n'
            '\t\t\t\t\t\t\t\t<table align=center border=0 cellpadding=3 cellspacing=3>\n'
            '%s\n'
            '\t\t\t\t\t\t\t\t</table>\n'
            '\t\t\t\t\t\t\t</td></tr>\n'
            '\t\t\t\t\t\t</table>\n\t\t\t\t\t</center>\n\t\t\t\t</td>\n'
            '\t\t\t</tr>\n\t\t</table>\n\t</body>\n</html>\n' % (title, rows))


def _rows_pairs(cells):
    """cells: список готовых <td>...</td>, группируем по 2 в строку."""
    out = []
    for i in range(0, len(cells), 2):
        out.append("\t\t\t\t\t\t\t\t<tr>\n" +
                   "".join(cells[i:i + 2]) +
                   "\t\t\t\t\t\t\t\t</tr>")
    return out


def build_html(section_pages):
    """section_pages: список (page, title, [(btn_label, action)], back_rows)."""
    os.makedirs(HTML_DIR, exist_ok=True)

    # Главная вкладки-эталона
    cells = []
    for page, title, _btns in [(p, t, b) for p, t, b, _ in section_pages]:
        cells.append('\t\t\t\t\t\t\t\t\t<td><center>%s</center></td>\n'
                     % _btn(title, "bypass _bbstop;l2sref/%s.html" % page, 200))
    cells.append('\t\t\t\t\t\t\t\t\t<td><center>%s</center></td>\n'
                 % _btn("◄ В Alt+B", "bypass _bbstop;home.html", 200))
    main_rows = _rows_pairs(cells)
    open(os.path.join(HTML_DIR, "main.html"), "w", encoding="utf-8").write(
        _page("Магазин L2Scripts (эталон)", main_rows))

    # Страницы разделов
    for page, title, btns, _back in section_pages:
        cells = []
        for lbl, action in btns:
            cells.append('\t\t\t\t\t\t\t\t\t<td><center>%s</center></td>\n'
                         % _btn(lbl, action))
        rows = _rows_pairs(cells)
        rows.append('\t\t\t\t\t\t\t\t<tr><td height=10></td></tr>')
        rows.append('\t\t\t\t\t\t\t\t<tr>'
                    '<td><center>%s</center></td>'
                    '<td><center>%s</center></td></tr>'
                    % (_btn("◄ К разделам", "bypass _bbstop;l2sref/main.html"),
                       _btn("Продать вещи", "bypass _bbssell;l2sref/%s" % page)))
        open(os.path.join(HTML_DIR, "%s.html" % page), "w", encoding="utf-8").write(
            _page("L2Scripts — %s" % title, rows))


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------
def main():
    items = parse_items()
    print("Загружено предметов Mobius: %d" % len(items))
    os.makedirs(MULTISELL_DIR, exist_ok=True)

    report = []
    section_pages = []
    total_cats = 0
    total_items = 0
    all_skips = {}
    used_ids = []

    for page, subdir, groups, base_id, sec_title in SECTIONS:
        newid = base_id
        btns = []
        for group_title, cats in groups:
            for src_base, cat_title in cats:
                src_path = os.path.join(SRC, subdir, src_base + ".xml")
                if not os.path.exists(src_path):
                    report.append("!! НЕТ ИСХОДНИКА: %s" % src_path)
                    continue
                src_items = parse_source_multisell(src_path)
                target, skipped = build_target_items(items, src_items, report)
                if skipped:
                    all_skips[src_base] = skipped
                if not target:
                    report.append("!! ПУСТО после конвертации: %s" % src_base)
                    continue
                # защита от коллизии id
                out_path = os.path.join(MULTISELL_DIR, "%d.xml" % newid)
                if os.path.exists(out_path):
                    raise SystemExit("КОЛЛИЗИЯ id мультиселла: %d уже существует" % newid)
                open(out_path, "w", encoding="utf-8").write(render_multisell(target))
                btns.append(("%s: %s" % (group_title, cat_title),
                             "bypass _bbsmultisell;%d,l2sref/%s" % (newid, page)))
                report.append("%d.xml <- %s/%s  [%s / %s]  позиций=%d  пропущено=%d"
                              % (newid, subdir, src_base, group_title, cat_title,
                                 len(target), len(skipped)))
                used_ids.append(newid)
                total_cats += 1
                total_items += len(target)
                newid += 1
        section_pages.append((page, sec_title, btns, None))

    build_html(section_pages)

    # -------- Отчёт --------
    print("\n".join(report))
    print("\n================ ОТЧЁТ ================")
    print("Разделов вкладки: %d" % len(SECTIONS))
    print("Категорий (мультиселлов) перенесено: %d" % total_cats)
    print("Всего позиций: %d" % total_items)
    if used_ids:
        print("Диапазон multisell-id: %d .. %d" % (min(used_ids), max(used_ids)))
    if all_skips:
        print("Пропущенные production-id (нет в stats/items):")
        for k, v in all_skips.items():
            print("   %s: %s" % (k, sorted(set(v))))
    else:
        print("Пропусков по production-id нет — все резолвятся в stats/items.")
    print("HTML вкладки: %s" % HTML_DIR)
    print("Открытие в игре: Alt+B -> кнопка «Магазин L2Scripts» -> _bbstop;l2sref/main.html")


if __name__ == "__main__":
    main()
