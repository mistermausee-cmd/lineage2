#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Генератор вкладки телепорта: строит CommunityTeleportList (конфиг) и HTML-страницы.
   Данные: курируемые локации (координаты из админ-телепортов) + рейд-боссы из RaidbossSpawns.xml."""
import re, os, glob

HTML = "/projects/sandbox/lineage2/server/game/data/html/CommunityBoard/Custom/gatekeeper"
NPCS = "/projects/sandbox/lineage2/server/game/data/stats/npcs"
SPAWNS = "/projects/sandbox/lineage2/server/game/data/spawns/RaidbossSpawns.xml"

# ---------------------------------------------------------------------------
# Курируемые точки: key -> (Русское название, X, Y, Z)
# ---------------------------------------------------------------------------
VILLAGES = [
    ("vTalking",  "Остров Талкинг-Айленд", 18748, 145437, -3132),
    ("vElven",    "Деревня Эльфов",        45873, 49288, -3064),
    ("vDarkElf",  "Деревня Тёмных Эльфов", 12428, 16551, -4588),
    ("vDwarven",  "Деревня Гномов",        115551, -182493, -1525),
    ("vOrc",      "Деревня Орков",         -80353, 247981, -3507),
    ("vKamael",   "Остров Душ (Камаэли)",  -114351, 255286, -1520),
    ("vGludin",   "Деревня Глудин",        -80684, 149770, -3043),
    ("vFloran",   "Деревня Флоран",        17144, 170156, -3502),
    ("vHunters",  "Деревня Охотников",     116819, 76994, -2714),
    ("vErtheia",  "Деревня Эртея",         -116934, 46616, 368),
]

CITIES = [
    ("cGludio",     "Глудио",     -14225, 123540, -3121),
    ("cDion",       "Дион",       15470, 142880, -2700),
    ("cGiran",      "Гиран",      82698, 148638, -3473),
    ("cOren",       "Орен",       82321, 55139, -1529),
    ("cAden",       "Аден",       147450, 27064, -2208),
    ("cHeine",      "Хейне",      111115, 219017, -3547),
    ("cGoddard",    "Годдард",    147725, -56517, -2780),
    ("cRune",       "Руна",       43070, -47837, -797),
    ("cSchuttgart", "Шутгарт",    87358, -141982, -1341),
    ("cPrimeval",   "Древний Остров", 10468, -24569, -3650),
    ("cGainak",     "Гайнак",     43408, 206881, -3752),
]

# Фарм до 99: (key, Русское название (Ур.), X, Y, Z)
FARM_LOW = [
    ("fCruma",     "Башня Крума (Ур.40-46)",         17724, 114004, -11672),
    ("fExecution", "Поле Казни (Ур.36-42)",          -49853, 147089, -2784),
    ("fAntNest",   "Муравейник (Ур.36-45)",          -9959, 176184, -4160),
    ("fDeathPass", "Тропа Смерти (Ур.40-46)",        67933, 117045, -3544),
    ("fMirrors",   "Лес Зеркал (Ур.55-62)",          142065, 81300, -3000),
    ("fMassacre",  "Поля Побоищ (Ур.55-62)",         183543, -14974, -2768),
    ("fEValley",   "Зачарованная Долина (Ур.62-70)", 124904, 61992, -3973),
    ("fTanor",     "Каньон Танор (Ур.58-66)",        58316, 163851, -2816),
    ("fToI",       "Башня Дерзости (Ур.65-75)",      114496, 132416, -3101),
    ("fDragon",    "Долина Драконов (Ур.63-75)",     73024, 118485, -3720),
    ("fArgos",     "Стена Аргоса (Ур.68-75)",        165054, -47861, -3560),
    ("fScreams",   "Болото Криков (Ур.68-76)",       69340, -50203, -3314),
    ("fSilent",    "Долина Тишины (Ур.70-78)",       170838, 55776, -5280),
    ("fGiantCave", "Пещера Гигантов (Ур.75-82)",     181737, 46469, -4276),
    ("fKetra",     "Форпост Кетра (Ур.76-82)",       146990, -67128, -3640),
    ("fVarka",     "Казармы Варка (Ур.76-82)",       125740, -40864, -3736),
    ("fMonastery", "Монастырь Тишины (Ур.80-86)",    113824, 83706, -6489),
    ("fStakato",   "Гнездо Стакатов (Ур.80-86)",     88969, -45307, -2112),
    ("fBlazing",   "Пылающее Болото (Ур.84-90)",     155310, -16339, -3320),
    ("fSelMahum",  "Лагерь Сел Махум (Ур.85-92)",    84517, 62538, -3480),
    ("fLizard",    "Равнина Людей-Ящеров (Ур.90-95)", 87252, 85514, -3103),
    ("fAlligator", "Остров Аллигаторов (Ур.92-97)",  115583, 192261, -3488),
]

# Фарм 99+ (каждый уровень плотнее)
FARM_HIGH = [
    ("hBeleth",    "Магический Круг Белет (Ур.99-101)", -22432, 243491, -3068),
    ("hEValley",   "Зачарованная Долина (Ур.102-104)",  114681, 48255, -4576),
    ("hPhantasm",  "Призрачный Хребет (Ур.103-105)",    -13475, 253323, -3440),
    ("hSilent",    "Долина Тишины (Ур.105-106)",        170838, 55776, -5280),
    ("hAlligator", "Остров Аллигаторов (Ур.106-108)",   115583, 192261, -3488),
    ("hTanor",     "Каньон Танор (Ур.107-109)",         58316, 163851, -2816),
    ("hMirrors",   "Лес Зеркал (Ур.108-110)",           142065, 81300, -3000),
    ("hFairy",     "Поселение Фей (Ур.106-110)",        214361, 80813, 821),
    ("hGarden",    "Сад Духов (Ур.104-108)",            -50592, 83219, -5134),
    ("hIsleSouls", "Остров Душ (Ур.107-110)",           -121436, 56288, -1586),
    ("hSeedAnni",  "Семя Уничтожения (Ур.100-105)",     -175520, 154505, 2712),
    ("hGiantCave", "Пещера Гигантов (Ур.100-104)",      181737, 46469, -4276),
    ("hBlazing",   "Пылающее Болото (Ур.108-112)",      155310, -16339, -3320),
    ("hCemetery",  "Кладбище (Ур.110-115)",             167047, 20304, -3328),
    ("hPagan",     "Языческий Храм (Ур.100-105)",       -16368, -38912, -10720),
    ("hFieldSil",  "Поле Тишины (Ур.100-104)",          91088, 182384, -3192),
    ("hFieldWhis", "Поле Шёпота (Ур.100-104)",          74592, 207656, -3032),
]

GRANDBOSS = [
    ("gbQueenAnt",  "Королева Муравьёв (Ур.40)",  -21466, 182892, -5722),
    ("gbCore",      "Ядро (Ур.50)",               17719, 112904, -6584),
    ("gbOrfen",     "Орфен (Ур.50)",              54274, 17803, -595),
    ("gbZaken",     "Закен (Ур.60)",              48130, 186789, -3486),
    ("gbFrintezza", "Фринтесса (Ур.85)",          181382, -80872, -2733),
    ("gbBaium",     "Баюм (Ур.75)",               115213, 16623, 10080),
    ("gbAntharas",  "Антарас (Ур.85)",            131557, 114509, -3712),
    ("gbValakas",   "Валакас (Ур.85)",            213896, -115436, -1644),
    ("gbBeleth",    "Белет (Ур.85)",              16327, 209228, -9357),
    ("gbTiat",      "Тиат (Ур.90)",               -248673, 250268, 4336),
]

SERVICES = [
    ("nBlacksmith", "Кузнец (Аден)",             150631, 28341, -2252),
    ("nSymbol",     "Мастер символов (Аден)",    147450, 27064, -2208),
    ("nClassMaster","Подклассы / Классы (Аден)", 147728, 27408, -2208),
]

# ---------------------------------------------------------------------------
# Рейд-боссы из RaidbossSpawns.xml
# ---------------------------------------------------------------------------
def load_raids():
    npc = {}
    for f in glob.glob(os.path.join(NPCS, "*.xml")):
        d = open(f, encoding="utf-8", errors="ignore").read()
        for m in re.finditer(r'<npc\s+id="(\d+)"[^>]*level="(\d+)"', d):
            npc[m.group(1)] = int(m.group(2))
    data = open(SPAWNS, encoding="utf-8").read()
    seen = set(); rows = []
    for sp in re.finditer(r'<spawn\b.*?</spawn>', data, re.S):
        b = sp.group(0)
        nodes = re.findall(r'<node x="(-?\d+)" y="(-?\d+)"', b)
        zm = re.search(r'minZ="(-?\d+)" maxZ="(-?\d+)"', b)
        nl = re.search(r'<npc id="(\d+)"[^>]*/>\s*(?:<!--\s*(.*?)\s*-->)?', b)
        if not nl or not nodes: continue
        iid = nl.group(1)
        if iid in seen: continue
        seen.add(iid)
        nm = (nl.group(2) or "").strip() or f"Raid {iid}"
        xs = [int(x) for x, y in nodes]; ys = [int(y) for x, y in nodes]
        cx, cy = sum(xs)//len(xs), sum(ys)//len(ys)
        cz = (int(zm.group(1))+int(zm.group(2)))//2 if zm else -3000
        lvl = npc.get(iid, 0)
        rows.append((lvl, iid, nm, cx, cy, cz))
    rows.sort()
    return rows

RAIDS = load_raids()

# ---------------------------------------------------------------------------
# HTML-помощники (единый стиль магазина/баффера)
# ---------------------------------------------------------------------------
BACK_LABEL = "\u25c4 \u041d\u0430\u0437\u0430\u0434"  # ◄ Назад

def page(title, subtitle, body_rows_html, back="main.html", extra_footer=""):
    back_btn = (f'<button value="{BACK_LABEL}" action="bypass _bbstop;gatekeeper/{back}" '
                f'width=150 height=28 back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">')
    return f'''<html noscrollbar>
	<body>
		<table width=700><tr><td height=8></td></tr></table>
		<table width=20>
			<tr>
				<td>%navigation%</td>
				<td>
					<center>
						<table border=0 cellpadding=0 cellspacing=0 width=565 height=474 background="L2UI_CT1.Windows_DF_TooltipBG">
							<tr><td height=14></td></tr>
							<tr><td align="center"><font name="hs12" color="CDB67F">{title}</font></td></tr>
							<tr><td align="center"><font color="808A99">{subtitle}</font></td></tr>
							<tr><td height=6></td></tr>
							<tr><td><center><img src="L2UI.SquareGray" width=515 height=1></center></td></tr>
							<tr><td height=8></td></tr>
							<tr><td align="center">
{body_rows_html}
							</td></tr>
							<tr><td height=8></td></tr>
							<tr><td><center><img src="L2UI.SquareGray" width=515 height=1></center></td></tr>
							<tr><td height=6></td></tr>
							<tr><td align="center">{extra_footer}{back_btn}</td></tr>
						</table>
					</center>
				</td>
			</tr>
		</table>
	</body>
</html>
'''

def tp_btn(key, label, w=250):
    return (f'<button value="{label}" action="bypass _bbsteleport;{key}" '
            f'width={w} height=27 back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">')

def nav_btn(label, target, w=250):
    return (f'<button value="{label}" action="bypass _bbstop;gatekeeper/{target}" '
            f'width={w} height=28 back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">')

def grid(items, cols, btnfn, w):
    """items: список html-кнопок. Возвращает html таблицы cols в ряд."""
    out = ['\t\t\t\t\t\t\t\t<table border=0 cellpadding=3 cellspacing=2>']
    for i in range(0, len(items), cols):
        out.append('\t\t\t\t\t\t\t\t\t<tr>')
        for cell in items[i:i+cols]:
            out.append(f'\t\t\t\t\t\t\t\t\t\t<td align=center>{cell}</td>')
        out.append('\t\t\t\t\t\t\t\t\t</tr>')
    out.append('\t\t\t\t\t\t\t\t</table>')
    return '\n'.join(out)

# ---------------------------------------------------------------------------
# Сборка конфига
# ---------------------------------------------------------------------------
def cfg_entries():
    ents = []
    for group in (VILLAGES, CITIES, FARM_LOW, FARM_HIGH, GRANDBOSS, SERVICES):
        for key, _lbl, x, y, z in group:
            ents.append(f"{key},{x},{y},{z}")
    for lvl, iid, nm, cx, cy, cz in RAIDS:
        ents.append(f"rb{iid},{cx},{cy},{cz}")
    return ents

# ---------------------------------------------------------------------------
# Генерация страниц
# ---------------------------------------------------------------------------
def write(path, content):
    open(os.path.join(HTML, path), "w", encoding="utf-8").write(content)

def gen():
    # HUB
    hub_items = [
        nav_btn("Деревни", "villages.html"),
        nav_btn("Города", "cities.html"),
        nav_btn("Фарм: до 99", "farm_low.html"),
        nav_btn("Фарм: 99+", "farm_high.html"),
        nav_btn("Рейд-боссы", "raids1.html"),
        nav_btn("Гранд-боссы", "grandboss.html"),
        nav_btn("Услуги NPC", "services.html"),
    ]
    hub_body = grid(hub_items, 2, None, 250)
    hub = f'''<html noscrollbar>
	<body>
		<table width=700><tr><td height=8></td></tr></table>
		<table width=20>
			<tr>
				<td>%navigation%</td>
				<td>
					<center>
						<table border=0 cellpadding=0 cellspacing=0 width=565 height=474 background="L2UI_CT1.Windows_DF_TooltipBG">
							<tr><td height=16></td></tr>
							<tr><td align="center"><font name="hs12" color="CDB67F">ТЕЛЕПОРТ</font></td></tr>
							<tr><td align="center"><font color="808A99">Выберите категорию назначения</font></td></tr>
							<tr><td height=8></td></tr>
							<tr><td><center><img src="L2UI.SquareGray" width=515 height=1></center></td></tr>
							<tr><td height=16></td></tr>
							<tr><td align="center">
{hub_body}
							</td></tr>
							<tr><td height=16></td></tr>
							<tr><td><center><img src="L2UI.SquareGray" width=515 height=1></center></td></tr>
							<tr><td align="center"><font color="696969">Lineage II • Grand Crusade</font></td></tr>
						</table>
					</center>
				</td>
			</tr>
		</table>
	</body>
</html>
'''
    write("main.html", hub)

    # Villages / Cities / Services / Grandboss: сетка 2 колонки, ширина 250
    write("villages.html", page("ДЕРЕВНИ", "Стартовые деревни всех рас",
          grid([tp_btn(k, l) for k, l, *_ in VILLAGES], 2, None, 250)))
    write("cities.html", page("ГОРОДА", "Основные города континента",
          grid([tp_btn(k, l) for k, l, *_ in CITIES], 2, None, 250)))
    write("grandboss.html", page("ГРАНД-БОССЫ", "Логова эпических боссов (уровень указан)",
          grid([tp_btn(k, l) for k, l, *_ in GRANDBOSS], 2, None, 250)))
    write("services.html", page("УСЛУГИ NPC", "Быстрый доступ к ключевым NPC в Адене",
          grid([tp_btn(k, l) for k, l, *_ in SERVICES], 1, None, 320)))

    # Farm: 2 колонки, широкие кнопки с уровнем
    write("farm_low.html", page("ФАРМ: ДО 99", "Лучшие зоны опыта/адены/дропа по уровням",
          grid([tp_btn(k, l, 250) for k, l, *_ in FARM_LOW], 2, None, 250)))
    write("farm_high.html", page("ФАРМ: 99+", "Лучшие эндгейм-зоны для фарма",
          grid([tp_btn(k, l, 250) for k, l, *_ in FARM_HIGH], 2, None, 250)))

    # Raids: постраничная разбивка по 30 (умещается в окно), заголовок по диапазону уровней
    PER = 30
    chunks = [RAIDS[i:i+PER] for i in range(0, len(RAIDS), PER)]
    n = len(chunks)
    for idx, chunk in enumerate(chunks, 1):
        fname = f"raids{idx}.html"
        lo, hi = chunk[0][0], chunk[-1][0]
        items = [tp_btn(f"rb{iid}", f"{nm} ({lvl})", 165) for lvl, iid, nm, cx, cy, cz in chunk]
        footer = ""
        if idx < n:
            footer = (f'<button value="\u0414\u0430\u043b\u0435\u0435 \u25ba" '
                      f'action="bypass _bbstop;gatekeeper/raids{idx+1}.html" '
                      f'width=150 height=28 back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">&nbsp;&nbsp;')
        sub = f"Уровни {lo}-{hi} • страница {idx}/{n} • нажмите для телепорта"
        write(fname, page(f"РЕЙД-БОССЫ (Ур.{lo}-{hi})", sub, grid(items, 3, None, 165), extra_footer=footer))

    # Конфиг
    return cfg_entries()

if __name__ == "__main__":
    ents = gen()
    print(f"Страниц HTML записано. Точек телепорта: {len(ents)}")
    print("RAIDS:", len(RAIDS))
    # вывести конфиг-строку в файл
    with open("/projects/sandbox/lineage2/tools/_teleport_list.txt", "w", encoding="utf-8") as f:
        f.write(";\\\n".join(ents) + ";\n")
    print("Конфиг сохранён в tools/_teleport_list.txt")
