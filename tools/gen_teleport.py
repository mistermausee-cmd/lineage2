#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Генератор вкладки телепорта (L2 Grand Crusade, соло-сервер).
   Источники данных:
     * координаты — GM-выверенные админ-телепорты (WorldAreas / TownAreas / raid_special.htm)
     * русские названия боссов — официальный клиентский NpcName-RU.dat (tools/_ru/npcname_ru.json)
     * рейд-боссы — все мировые спавны (boss_index.py raid)
   Стиль HTML — единый с магазином/баффером."""
import re, os, glob, json, subprocess, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from ru_translate import translate_name

BASE = "/projects/sandbox/lineage2"
HTML = os.path.join(BASE, "server/game/data/html/CommunityBoard/Custom/gatekeeper")
NPCS = os.path.join(BASE, "server/game/data/stats/npcs")
SPAWNS_DIR = os.path.join(BASE, "server/game/data/spawns")
RU_JSON = os.path.join(BASE, "tools/_ru/npcname_ru.json")

PY = sys.executable

# ---------------------------------------------------------------------------
# 1. ГОРОДА  (key, Русское название, X, Y, Z)  — координаты из админ TownAreas
# ---------------------------------------------------------------------------
CITIES = [
    ("cAden",       "Аден",              147450, 27064, -2208),
    ("cGiran",      "Гиран",             82698, 148638, -3473),
    ("cGludio",     "Глудио",            -14225, 123540, -3121),
    ("cDion",       "Дион",              18748, 145437, -3132),
    ("cOren",       "Орен",              82321, 55139, -1529),
    ("cHeine",      "Хейне",             111115, 219017, -3547),
    ("cGoddard",    "Годдард",           147725, -56517, -2780),
    ("cRune",       "Руна",              44070, -50243, -796),
    ("cSchuttgart", "Шутгарт",           87358, -141982, -1341),
    ("cHunters",    "Деревня Охотников", 116589, 76268, -2734),
]

# ---------------------------------------------------------------------------
# 2. ДЕРЕВНИ  — стартовые деревни рас + Глудин/Флоран
# ---------------------------------------------------------------------------
VILLAGES = [
    ("vTalking",  "Деревня Говорящего Острова",  -114351, 255286, -1520),
    ("vElven",    "Деревня Эльфов",              45873, 49288, -3064),
    ("vDarkElf",  "Деревня Тёмных Эльфов",       12428, 16551, -4588),
    ("vDwarven",  "Деревня Гномов",              116551, -182493, -1525),
    ("vOrc",      "Деревня Орков",               -44133, -113911, -244),
    ("vKamael",   "Остров Душ (Камаэли)",        -116934, 46616, 368),
    ("vErtheia",  "Деревня Эртея",               -80353, 247981, -3507),
    ("vGludin",   "Деревня Глудин",              -83063, 150791, -3133),
    ("vFloran",   "Деревня Флоран",              17144, 170156, -3502),
]

# ---------------------------------------------------------------------------
# 3. ФАРМ до 99 — (key, Русское название, минУр, максУр, X, Y, Z)
# ---------------------------------------------------------------------------
# Уровни и координаты выверены по РЕАЛЬНЫМ данным сервера (tools/zone_audit.py):
# уровень = медиана уровней мобов в файле спавнов зоны; координата — безопасная
# точка админ-телепорта (GM). Зоны без мобов/с неверным уровнем удалены.
FARM_LOW = [
    ("fElvenForest","Эльфийский Лес",             13, 19, 21362, 51122, -3688),
    ("fRuinsAgony", "Руины Страданий",            24, 28, -42628, 119766, -3528),
    ("fPlainsDion", "Равнины Диона",              24, 35, 630, 179184, -3720),
    ("fDespair",    "Руины Отчаяния",             29, 34, -19120, 136816, -3762),
    ("fAntNest",    "Муравейник",                 30, 38, -9959, 176184, -4160),
    ("fCruma",      "Башня Крумы",                40, 51, 17724, 114004, -11672),
    ("fOutlaw",     "Лес Разбойников",            50, 55, 91539, -12204, -2440),
    ("fSpores",     "Море Спор",                  54, 60, 64328, 26803, -3768),
    ("fForsaken",   "Покинутые Равнины",          55, 62, 168217, 37990, -4072),
    ("fMassacre",   "Равнины Неистовства",        60, 65, 183543, -14974, -2768),
    ("fToI",        "Башня Дерзости",             60, 75, 111249, 16031, -2127),
    ("fForestDead", "Лес Мёртвых",                65, 73, 52107, -54328, -3158),
    ("fShrine",     "Храм Верности",              74, 80, 190112, -61776, -2944),
    ("fDragon",     "Долина Драконов",            80, 83, 73024, 118485, -3720),
    ("fBeast",      "Загон Диких Зверей",         84, 84, 43129, -87973, -2886),
    ("fAltarEvil",  "Алтарь Зла",                 85, 87, -13309, 21949, -3714),
    ("fBloody",     "Кровавое Болото",            87, 90, -27476, 49487, -3684),
    ("fSeedAnni",   "Семя Уничтожения",           89, 89, -175520, 154505, 2712),
    ("fGenesis",    "Сад Зарождения",             90, 94, 207868, 112199, -2063),
    ("fMimir",      "Лес Мимира",                 93, 96, -103032, 46457, -1136),
    ("fShilen",     "Печать Шилен",               95, 96, 188611, 20588, -3696),
    ("fOrbis",      "Храм Орбиса",                95, 97, 213023, 51135, -8412),
    ("fGuillotine", "Крепость Гильотины",         97, 97, 45010, 148000, -3694),
    ("fCemetery",   "Кладбище",                   98, 99, 167047, 20304, -3328),
    ("fBlazing",    "Пылающая Топь",              98, 99, 155310, -16339, -3320),
    ("fBeleth",     "Волшебные Мегалиты Белефа",  99, 99, -22432, 243491, -3068),
    ("fLandChaos",  "Земля Хаоса",                99, 99, -13218, -123208, -2984),
    ("fImmortal",   "Плато Бессмертия",           99, 99, -9284, -134199, -2190),
    ("fRaiders",    "Перекрёсток Разбойников",    99, 99, 13585, -140295, -676),
    ("fPagan",      "Храм Язычников",             99, 99, -16368, -38912, -10720),
]

# ---------------------------------------------------------------------------
# 4. ФАРМ 99+  (медиана уровня мобов >= 100, выверено по данным сервера)
# ---------------------------------------------------------------------------
FARM_HIGH = [
    ("hGiantCave",  "Пещера Гигантов",            100, 100, 181737, 46469, -4276),
    ("hPhantasm",   "Призрачный Хребет",          100, 100, -13475, 253323, -3440),
    ("hGardenSpir", "Сад Духов",                  100, 101, -50592, 83219, -5134),
    ("hMirrors",    "Лес Зеркал (Разлом)",        101, 101, 142065, 81300, -3000),
    ("hAtelia",     "Крепость Ателии",            101, 102, -47803, 60858, -3229),
    ("hEValley",    "Зачарованная Долина",        100, 102, 114681, 48255, -4576),
    ("hSuperion",   "Крепость Суперион",          102, 103, 79827, 152588, 2304),
]

# ---------------------------------------------------------------------------
# 5. ГРАНД / ЭПИК-БОССЫ  (key, Русское название, уровень, X, Y, Z)
#    Инстансовые боссы -> координата NPC-менеджера, открывающего вход (из спавнов).
#    Открытые боссы -> локация арены. Имена -> NpcName-RU.dat.
# ---------------------------------------------------------------------------
GRANDBOSS = [
    # --- открытый мир (телепорт к арене босса) ---
    ("gbQueenAnt",  "Королева Муравьёв",        40,  -21466, 182892, -5722),
    ("gbCore",      "Ядро",                     50,  17719, 112904, -6584),
    ("gbOrfen",     "Орфен",                    60,  54274, 17803, -595),
    ("gbShyeed",    "Королева Шид",             84,  80287, -55930, -6138),
    ("gbAntharas",  "Антарас",                  104, 183409, 114824, -8020),
    ("gbValakas",   "Валакас",                  104, 213896, -115436, -1644),
    ("gbLindvior",  "Линдвиор",                 104, 48450, -30337, -1681),
    # --- инстансы (телепорт к NPC-входу) ---
    ("gbSailren",   "Сейлрен",                  80,  23664, -8464, -1344),
    ("gbBaium",     "Баюм",                     78,  112624, 14032, 10080),
    ("gbZaken",     "Закен",                    83,  52106, 219171, -3224),
    ("gbBeleth",    "Белеф",                    83,  -28009, 253706, -2200),
    ("gbFrintezza", "Фринтеза",                 85,  181376, -81008, -2728),
    ("gbFreya",     "Фрея",                     88,  102402, -124491, -2776),
    ("gbTeredor",   "Траджан (Тередор)",        88,  85572, -142477, -1336),
    ("gbKimerian",  "Кимериан",                 95,  208176, 87360, -1024),
    ("gbTiat",      "Тиада",                    96,  -248793, 250271, 4328),
    ("gbSpezion",   "Спасия (Спезион)",         98,  175467, 148287, -11648),
    ("gbBaylor",    "Байлор",                   99,  148278, 172794, -944),
    ("gbBalok",     "Валлок",                   99,  148278, 172794, -944),
    ("gbTrasken",   "Земляной Червь Траскен",   99,  87700, -143253, -1288),
    ("gbTauti",     "Таути",                    99,  -146238, 186523, -11728),
    ("gbIstina",    "Истхина",                  99,  -178471, 147060, 2128),
    ("gbOctavis",   "Октавис",                  99,  210422, 50506, -8326),
    ("gbKelbim",    "Кельбим",                  104, -55805, 56157, -1872),
]

# ---------------------------------------------------------------------------
# 6. РЕЙД-БОССЫ — все мировые спавны, русские имена по id
# ---------------------------------------------------------------------------
def load_ru():
    with open(RU_JSON, encoding="utf-8") as f:
        d = json.load(f)
    return {k: v.get("name", "") for k, v in d.items()}

RU = load_ru()

# Официальные руофф-имена по английскому названию (клиент l2scripts: NpcName-e + NpcName-RU)
EN2RU_PATH = os.path.join(BASE, "tools/_ru/en2ru.json")
try:
    with open(EN2RU_PATH, encoding="utf-8") as f:
        EN2RU = json.load(f)
except FileNotFoundError:
    EN2RU = {}

def _norm(s):
    return " ".join(s.lower().replace("'", "").split())

def load_raids():
    """Возвращает [(lvl, id, RUname, x, y, z)] отсортированный по уровню."""
    out = subprocess.check_output([PY, os.path.join(BASE, "tools/boss_index.py"), "raid"],
                                  text=True)
    rows = []
    for line in out.splitlines():
        m = re.match(r'\s*(\d+)\s*\|\s*(\d+)\s*\|\s*(.+?)\s*\|\s*(-?\d+),(-?\d+),(-?\d+)', line)
        if not m:
            continue
        lvl, iid, enname, x, y, z = m.groups()
        # 1) руофф-имя по id клиента; 2) руофф-имя по англ.названию; 3) перевод
        ru = RU.get(iid) or EN2RU.get(_norm(enname)) or translate_name(enname)
        rows.append((int(lvl), iid, ru, int(x), int(y), int(z)))
    rows.sort(key=lambda r: (r[0], r[2]))
    return rows

RAIDS = load_raids()

# ---------------------------------------------------------------------------
# HTML-помощники (единый стиль магазина/баффера)
# ---------------------------------------------------------------------------
BACK = "\u25c4 \u041d\u0430\u0437\u0430\u0434"       # ◄ Назад
NEXT = "\u0414\u0430\u043b\u0435\u0435 \u25ba"        # Далее ►
PREV = "\u25c4 \u041d\u0430\u0437\u0430\u0434"        # ◄ Назад

def esc(s):
    return s

def shell(title, subtitle, inner, footer_html):
    """Общая рамка страницы (единый стиль)."""
    return f'''<html noscrollbar>
	<body>
		<table width=700><tr><td height=6></td></tr></table>
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
							<tr><td height=6></td></tr>
							<tr><td align="center">
{inner}
							</td></tr>
							<tr><td height=6></td></tr>
							<tr><td><center><img src="L2UI.SquareGray" width=515 height=1></center></td></tr>
							<tr><td height=6></td></tr>
							<tr><td align="center">{footer_html}</td></tr>
							<tr><td height=4></td></tr>
						</table>
					</center>
				</td>
			</tr>
		</table>
	</body>
</html>
'''

def back_btn(target="main.html", label=BACK, w=150):
    return (f'<button value="{label}" action="bypass _bbstop;gatekeeper/{target}" '
            f'width={w} height=28 back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">')

def tp_btn(key, label, w=250, h=27):
    return (f'<button value="{label}" action="bypass _bbsteleport;{key}" '
            f'width={w} height={h} back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">')

def nav_btn(label, target, w=250, h=30):
    return (f'<button value="{label}" action="bypass _bbstop;gatekeeper/{target}" '
            f'width={w} height={h} back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">')

def home_btn(label, w=250, h=30):
    return (f'<button value="{label}" action="bypass _bbstop;home.html" '
            f'width={w} height={h} back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">')

def grid(cells, cols):
    out = ['\t\t\t\t\t\t\t\t<table border=0 cellpadding=2 cellspacing=2>']
    for i in range(0, len(cells), cols):
        out.append('\t\t\t\t\t\t\t\t\t<tr>')
        for c in cells[i:i+cols]:
            out.append(f'\t\t\t\t\t\t\t\t\t\t<td align=center>{c}</td>')
        out.append('\t\t\t\t\t\t\t\t\t</tr>')
    out.append('\t\t\t\t\t\t\t\t</table>')
    return '\n'.join(out)

def write(path, content):
    with open(os.path.join(HTML, path), "w", encoding="utf-8") as f:
        f.write(content)

# ---------------------------------------------------------------------------
# Генерация страниц
# ---------------------------------------------------------------------------
def farm_label(name, lo, hi):
    rng = f"{lo}" if lo == hi else f"{lo}-{hi}"
    return f"{name} (\u0423\u0440.{rng})"

def gen():
    entries = []   # строки конфига

    # ---- HUB ----
    hub_cells = [
        nav_btn("\u0413\u043e\u0440\u043e\u0434\u0430", "cities.html"),
        nav_btn("\u0414\u0435\u0440\u0435\u0432\u043d\u0438", "villages.html"),
        nav_btn("\u0424\u0430\u0440\u043c: \u0434\u043e 99 \u0443\u0440.", "farm_low1.html"),
        nav_btn("\u0424\u0430\u0440\u043c: 99+ \u0443\u0440.", "farm_high.html"),
        nav_btn("\u0420\u0435\u0439\u0434-\u0431\u043e\u0441\u0441\u044b", "raids1.html"),
        nav_btn("\u0413\u0440\u0430\u043d\u0434-\u0431\u043e\u0441\u0441\u044b", "grandboss.html"),
        home_btn("\u0413\u043b\u0430\u0432\u043d\u0430\u044f"),
    ]
    hub_inner = grid(hub_cells, 2)
    hub = shell("\u0422\u0415\u041b\u0415\u041f\u041e\u0420\u0422",
                "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044e \u043d\u0430\u0437\u043d\u0430\u0447\u0435\u043d\u0438\u044f",
                hub_inner,
                '<font color="696969">Lineage II . Grand Crusade</font>')
    write("main.html", hub)

    # ---- ГОРОДА ----
    for k, l, x, y, z in CITIES:
        entries.append(f"{k},{x},{y},{z}")
    write("cities.html", shell("\u0413\u041e\u0420\u041e\u0414\u0410",
          "\u041e\u0441\u043d\u043e\u0432\u043d\u044b\u0435 \u0433\u043e\u0440\u043e\u0434\u0430 \u043a\u043e\u043d\u0442\u0438\u043d\u0435\u043d\u0442\u0430",
          grid([tp_btn(k, l) for k, l, *_ in CITIES], 2), back_btn()))

    # ---- ДЕРЕВНИ ----
    for k, l, x, y, z in VILLAGES:
        entries.append(f"{k},{x},{y},{z}")
    write("villages.html", shell("\u0414\u0415\u0420\u0415\u0412\u041d\u0418",
          "\u0421\u0442\u0430\u0440\u0442\u043e\u0432\u044b\u0435 \u0434\u0435\u0440\u0435\u0432\u043d\u0438 \u0432\u0441\u0435\u0445 \u0440\u0430\u0441",
          grid([tp_btn(k, l) for k, l, *_ in VILLAGES], 2), back_btn()))

    # ---- ФАРМ до 99 (пагинация) ----
    for k, name, lo, hi, x, y, z in FARM_LOW:
        entries.append(f"{k},{x},{y},{z}")
    PERF = 24
    low_pages = [FARM_LOW[i:i+PERF] for i in range(0, len(FARM_LOW), PERF)]
    for idx, chunk in enumerate(low_pages, 1):
        cells = [tp_btn(k, farm_label(name, lo, hi), 250) for k, name, lo, hi, x, y, z in chunk]
        footer = ""
        if idx < len(low_pages):
            footer += nav_btn(NEXT, f"farm_low{idx+1}.html", 150, 28) + "&nbsp;"
        footer += back_btn()
        sub = f"\u041b\u0443\u0447\u0448\u0438\u0435 \u0437\u043e\u043d\u044b \u043e\u043f\u044b\u0442\u0430/\u0430\u0434\u0435\u043d\u044b/\u0434\u0440\u043e\u043f\u0430 (\u0441\u0442\u0440. {idx}/{len(low_pages)})"
        write(f"farm_low{idx}.html", shell("\u0424\u0410\u0420\u041c: \u0414\u041e 99 \u0423\u0420.", sub, grid(cells, 2), footer))

    # ---- ФАРМ 99+ ----
    for k, name, lo, hi, x, y, z in FARM_HIGH:
        entries.append(f"{k},{x},{y},{z}")
    cells = [tp_btn(k, farm_label(name, lo, hi), 250) for k, name, lo, hi, x, y, z in FARM_HIGH]
    write("farm_high.html", shell("\u0424\u0410\u0420\u041c: 99+ \u0423\u0420.",
          "\u041b\u0443\u0447\u0448\u0438\u0435 \u044d\u043d\u0434\u0433\u0435\u0439\u043c-\u0437\u043e\u043d\u044b",
          grid(cells, 2), back_btn()))

    # ---- ГРАНД-БОССЫ ----
    for k, name, lvl, x, y, z in GRANDBOSS:
        entries.append(f"{k},{x},{y},{z}")
    cells = [tp_btn(k, f"{name} ({lvl})", 250) for k, name, lvl, x, y, z in GRANDBOSS]
    write("grandboss.html", shell("\u0413\u0420\u0410\u041d\u0414-\u0411\u041e\u0421\u0421\u042b",
          "\u041e\u0442\u043a\u0440\u044b\u0442\u044b\u0435 \u2014 \u043a \u0430\u0440\u0435\u043d\u0435; \u0438\u043d\u0441\u0442\u0430\u043d\u0441\u044b \u2014 \u043a NPC-\u0432\u0445\u043e\u0434\u0443",
          grid(cells, 2), back_btn()))

    # ---- РЕЙД-БОССЫ (пагинация по 30, 3 колонки) ----
    for lvl, iid, name, x, y, z in RAIDS:
        entries.append(f"rb{iid},{x},{y},{z}")
    PER = 30
    chunks = [RAIDS[i:i+PER] for i in range(0, len(RAIDS), PER)]
    n = len(chunks)
    for idx, chunk in enumerate(chunks, 1):
        lo, hi = chunk[0][0], chunk[-1][0]
        cells = [tp_btn(f"rb{iid}", f"{name} ({lvl})", 165) for lvl, iid, name, x, y, z in chunk]
        footer = ""
        if idx > 1:
            footer += nav_btn(PREV, f"raids{idx-1}.html", 130, 28) + "&nbsp;"
        if idx < n:
            footer += nav_btn(NEXT, f"raids{idx+1}.html", 130, 28) + "&nbsp;"
        footer += back_btn("main.html", "\u0412 \u043c\u0435\u043d\u044e", 130)
        sub = f"\u0423\u0440\u043e\u0432\u043d\u0438 {lo}-{hi} . \u0441\u0442\u0440. {idx}/{n}"
        write(f"raids{idx}.html", shell(f"\u0420\u0415\u0419\u0414-\u0411\u041e\u0421\u0421\u042b (\u0423\u0440.{lo}-{hi})",
              sub, grid(cells, 3), footer))

    return entries, n

if __name__ == "__main__":
    entries, npages = gen()
    with open(os.path.join(BASE, "tools/_teleport_list.txt"), "w", encoding="utf-8") as f:
        f.write(";\\\n".join(entries) + ";")
    print(f"Точек телепорта: {len(entries)}")
    print(f"Рейд-боссов: {len(RAIDS)}  (страниц рейдов: {npages})")
    print(f"Города:{len(CITIES)} Деревни:{len(VILLAGES)} Фарм<99:{len(FARM_LOW)} Фарм99+:{len(FARM_HIGH)} Гранд:{len(GRANDBOSS)}")
    print("Конфиг: tools/_teleport_list.txt")
