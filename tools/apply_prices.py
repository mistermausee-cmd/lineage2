#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Балансовый движок цен для магазина Alt+B (соло-сервер GC).
- Берёт грейд/имя предмета из stats/items (item_index).
- Назначает цену адены (id=57) по правилам, зависящим от раздела, грейда, имени.
- Различает ПОЛНУЮ ПОКУПКУ и АПГРЕЙД-ЦЕПОЧКУ (если в <item> есть не-адена ingredient).
- Меняет только count у ingredient id=57 (оба порядка атрибутов).
- ВАРИАНТ экономики масштабирует эндгейм/престиж-цены: A=0.6, B=1.0, C=1.8.

Запуск:
  python3 tools/apply_prices.py B            # dry-run отчёт (вариант B)
  python3 tools/apply_prices.py B --write     # записать в файлы
"""
import re, os, sys, glob, importlib.util

BASE = "/projects/sandbox/lineage2/server/game/data/multisell"
CUSTOM = os.path.join(BASE, "custom")

# --- подключаем item_index ---
spec = importlib.util.spec_from_file_location("item_index", "/projects/sandbox/lineage2/tools/item_index.py")
ii = importlib.util.module_from_spec(spec)
spec.loader.exec_module(ii)
INDEX = ii.index  # id(str) -> {name, add, type, grade, price}

M = 1_000_000
B = 1_000_000_000

VARIANT = sys.argv[1] if len(sys.argv) > 1 else "B"
WRITE = "--write" in sys.argv
SCALE = {"A": 0.6, "B": 1.0, "C": 1.8}[VARIANT]

def gname(pid):
    it = INDEX.get(str(pid))
    return (it["name"] + " " + it["add"]).strip() if it else ""

def grade(pid):
    it = INDEX.get(str(pid))
    return it["grade"] if it else "-"

def itype(pid):
    it = INDEX.get(str(pid))
    return it["type"] if it else "-"

def round_nice(v):
    if v <= 0: return 0
    if v < 1*M: step = 100_000
    elif v < 20*M: step = 1*M
    elif v < 100*M: step = 5*M
    elif v < 1*B: step = 25*M
    elif v < 5*B: step = 100*M
    else: step = 250*M
    return int(round(v / step) * step)

def sc(v):  # применить вариант-масштаб к «престиж/эндгейм» цене
    return round_nice(v * SCALE)

def stage_num(n):  # номер стадии/уровня из имени (Stage N / Ур.N / Lv.N)
    m = re.search(r'(?:Stage|Ур\.|Lv\.)\s*(\d+)', n)
    return int(m.group(1)) if m else None

def round_small(v):  # мелкое округление для дешёвых предметов (кристаллы/самоцветы)
    if v <= 0: return 0
    if v < 10_000: step = 500
    elif v < 100_000: step = 1_000
    elif v < 1_000_000: step = 5_000
    else: return round_nice(v)
    return int(round(v / step) * step)

def npc_price(pid):
    it = INDEX.get(str(pid))
    return it["price"] if it else 0

# ---------- ПРАВИЛА ЦЕН ПО РАЗДЕЛАМ ----------
# Возвращает цену адены для (multisell_id, production_id, is_upgrade).

def price_weapon_full(pid):
    g = grade(pid)
    return {"R": sc(150*M), "R95": sc(500*M), "R99": sc(1500*M)}.get(g, sc(150*M))

def price_weapon_blessed(pid):  # апгрейд (доплата)
    g = grade(pid)
    return {"R": sc(100*M), "R95": sc(300*M), "R99": sc(800*M)}.get(g, sc(150*M))

def price_weapon_bloody(pid, up):  # 600062 Bloody Amaranthine R99 (PVE)
    return sc(900*M) if up else sc(2000*M)

def armor_set_price(pid, n):
    g = grade(pid)
    if "Immortal" in n: return sc(20*M)
    if "Twilight" in n: return sc(40*M)
    if "Seraph" in n:   return sc(150*M)
    if "Eternal" in n:  return sc(450*M)
    return {"R": sc(40*M), "R95": sc(150*M), "R99": sc(450*M)}.get(g, sc(60*M))

def armor_blessed_price(pid, n):  # апгрейд 600064 (доплата за часть)
    if "Immortal" in n or "Twilight" in n: return sc(25*M)
    if "Seraph" in n: return sc(100*M)
    if "Eternal" in n: return sc(300*M)
    return sc(100*M)

def price_transcendent(mid, up):  # Запредельный Lv.1-6 (доплата за часть)
    lv = {600066:1,600067:2,600068:3,600069:4,600070:5,600071:6}[mid]
    base = {1:600*M, 2:1000*M, 3:1500*M, 4:2200*M, 5:3200*M, 6:5000*M}[lv]
    return sc(base)

def price_leveling(mid, pid):
    # Прокачка (B/A/S/S80) — по цене, по которой предмет продаёт NPC (его reference price).
    npc = npc_price(pid)
    if npc > 0:
        return round_nice(npc)
    is_w = itype(pid) == "Weapon"
    return round_nice(2*M if is_w else 0.5*M)  # запасной вариант

def price_epic(pid, up, n):
    it = INDEX.get(pid); npc = it["price"] if it else 0
    # 1) Цепочки богов (Einhasad/GranKain/Paagrio/Eva/Sayha/Maphr — Stage/Ур.N) —
    #    ПРОГРЕССИВНО: старт доступный, топ очень дорогой (это сильнейшая бижа).
    if re.search(r"Ур\.\d", n) or re.search(r"Stage", n) or re.search(r"-Stage", n):
        s = stage_num(n) or 1
        step = {1:300, 2:300, 3:500, 4:800, 5:1200, 6:2000}.get(s, 300)  # доплата за стадию, млн
        return sc(step * M)   # cum Stage6 ≈ 5.1 млрд
    # 2) Апгрейды классической эпики (Души/Благословенная/Бессмертная), кроме модерна
    if up:
        return sc(750*M)
    # 3) Модерн R-бижа (Истина/Октавис/Ring of Truth/Authority/Creation/Tauti/Lindvior) —
    #    у неё реальная NPC-цена R-грейда; якорим цену к ней (×4, пол 250M)
    if npc >= 30*M:
        return max(sc(round_nice(npc * 4)), sc(250*M))
    # 4) Классические грандбосс-эпики (низкая NPC, но культовые скиллы) — по престижу
    top = ["Baium","Antharas","Valakas","Freya","Zaken","Frintezza",
           "Queen Ant","Orfen","Baium's","Тиара"]
    if any(k in n for k in top):
        return sc(1000*M)
    return sc(600*M)  # прочая классика: Core/Beleth/Baylor/Earthworm/Tauti(деш. NPC)

def price_brooch(pid, up):
    m = {38766:300*M, 38767:700*M, 38768:1300*M, 26474:2200*M,
         28358:3500*M, 28486:5000*M, 28488:5000*M, 28487:5000*M, 28530:300*M}
    return sc(m.get(int(pid), 1000*M))

def price_brooch_stone(pid, up, n):
    if "Greater" in n or "Большой" in n: return sc(1500*M)
    mlv = re.search(r"(?:Lv\.|Ур\.)\s*(\d)", n)
    lv = int(mlv.group(1)) if mlv else 1
    return sc({1:80*M,2:120*M,3:200*M,4:350*M,5:550*M}.get(lv, 80*M))

def price_talisman(pid, up, n):
    pid_i = int(pid)
    # --- ВРЕМЕННЫЕ / событийные / стартовые — символическая цена ---
    if any(k in n for k in ["Christmas","Новогодн","Обычный","Все Параметры","7 дней","Event","дней"]) \
       or pid_i == 17061:
        return round_nice(1*M)
    # --- одиночные стат-талисманы (СИЛ/ИНТ/ЛВК/МДР, PC-exclusive) — постоянные, мелкие ---
    if "Commendation" in n or pid_i in (46039,46040,46041,46042):
        return round_nice(20*M)
    # --- Infinity-цепь (Бессмертия→Разрушения→…→Вожделения→Позолоченного/Insanity) — ПРОГРЕССИВНО ---
    infinity = {34984:50, 34985:100, 34986:150, 34987:250, 34988:350, 34989:500, 39572:1000}
    if pid_i in infinity:
        return sc(infinity[pid_i] * M)   # 34989 Вожделения=500M, 39572 Золотое дерево=1B
    # --- ПРЕМИУМ одиночные (редкие, сильные) ---
    if "Seven Signs" in n or "Семи Печатей" in n: return sc(400*M)
    if any(k in n for k in ["Anakim","Lilith","Анаким","Лилит"]): return sc(150*M)
    # --- ЦЕПОЧКИ ---
    if "Venir" in n or "Бенир" in n: return round_nice(25*M)       # Бенира: 24 стадии, 25M/улучшение
    if "Abundance" in n or "Изобил" in n: return round_nice(15*M)
    if "Sayha" in n or "Сайха" in n:                                # 10 стадий, сильный — прогрессия за уровень
        s = stage_num(n) or 1
        step = {1:30,2:40,3:55,4:75,5:100,6:130,7:165,8:205,9:250,10:300}.get(s, 40)  # cum ≈ 1.35 млрд
        return sc(step * M)
    if up: return round_nice(100*M)                                 # прочие апгрейд-талисманы
    return round_nice(40*M)

def price_bracelet(pid, up, n):
    # Браслеты дают немного статов, их ценность — слоты под талисманы. Цены низкие.
    if "Duo" in n or "Дуо" in n: return round_nice(8*M)
    s = stage_num(n) or 1
    step = {1:5, 2:8, 3:12, 4:18, 5:25, 6:35}.get(s, 10)   # доплата за стадию, млн
    return round_nice(step * M)

def price_belt(pid, up, n):
    # Временные (3-7 дней) — символически
    if any(k in n for k in ["3-day","7-day","Anniversary","Mysterious","Таинственный","Юбилея","дней"]):
        return round_nice(5*M)
    # ХАЙ-тир: урон +7-8% (Ruler's Authority, Chef Monkey)
    if any(k in n for k in ["Ruler's Authority","Полномочия Правителя","Chef Monkey","Обезьянки"]):
        return sc(350*M)
    # СИЛЬНЫЕ: урон +5%/-5-6% (Nurka/Lidia/Gustav/Tiat/Ekimus)
    if any(k in n for k in ["Nurka","Lidia","Gustav","Нурки","Лидии","Густава",
                            "Tiat","Ekimus","Tiada","Тиады","Экимуса"]):
        return sc(250*M)
    # R/R95/R99 рун-клипы (заточиваемые)
    if any(k in n for k in ["Immortal Belt","Twilight Belt","Seraph Belt","Eternal Belt",
                            "Бессмертия","Ада","Кадейры","Айдиоса"]):
        return sc(100*M)
    if any(k in n for k in ["Istina","Octavis","Истхины","Октависа"]):
        return sc(80*M)                                    # Истина/Октавис (recovery + слоты)
    return round_nice(40*M)                                # grade/утилитарные/декоративные пояса

def price_agathion(pid, up):
    return round_nice(8*M) if up else round_nice(15*M)

def price_dye(pid, n):
    if any(k in n for k in ["Сопр","Защиты","Resistance"]): return round_nice(15*M)  # резист-краски
    return round_nice(40*M)                                      # стат-краски (средний тир, хорошие статы)

def price_enchant_scroll(pid, n):
    w = "Оружие" in n or "Weapon" in n
    # --- точилки плащей (расходка, много штук) ---
    if "Плащ" in n or "Cloak" in n:
        if "Легендарн" in n or "Legendary" in n or "Ancient" in n or "Древн" in n: return round_nice(40*M)
        return round_nice(20*M)
    # --- точилки оружия/брони ---
    if "Священный" in n or "Divine" in n: return round_nice(150*M if w else 90*M)
    if "Благословенный Свиток" in n: return round_nice(60*M if w else 35*M)
    if "Камень" in n or "Кристалл" in n: return round_nice(80*M if w else 50*M)
    if "Свиток" in n and ("Оружие" in n or "Доспех" in n): return round_nice(20*M if w else 12*M)
    return round_nice(30*M)  # футболка/диадема

def price_hat(pid, up, n):
    # Сначала расходка: камни аугмента/духа/жизни и свитки модификации
    if "Свиток" in n or "Scroll" in n: return round_nice(40*M)   # свиток модификации диадемы
    if "Камень" in n or "Stone" in n:  return round_nice(100*M)  # камни духа/жизни/аугмента
    # Диадемы (Circlet of Power) — прогрессивно: база даёт мало → топ (Радужная) переворачивает игру
    if "Диадема" in n or "Диадему" in n:
        if "Радужная" in n: return sc(1200*M)          # Radiant: +15% крит.дмг, SkillPower+10%, HP/MP/CP+10%
        if "Драгоценная" in n: return sc(600*M)        # Noble: напр. M.Atk+14%
        return sc(200*M)                               # базовая диадема
    return round_nice(30*M)                            # обычные шапки/нимбы/венцы

def price_cloak(pid, up, n):
    # Плащи (600025). Почти все — СРЕДНИЙ тир (хорошие статы, не ломают игру).
    # Исключение — ЛЕГЕНДАРНЫЕ Ancient (+20, слоты аугмента, до +200% PvE-урона) = эндгейм.
    if "Legendary" in n or "Легендарн" in n: return sc(700*M)         # апгрейд база->Легендарный (эндгейм)
    if "Камень Духа" in n or "Spirit Stone" in n: return sc(150*M)    # камни эффектов (аугмент) плаща
    if any(k in n for k in ["Аден","Феррит","Эльмор","Эльмореден"]):  # база Ancient-цепочек (заточка +10)
        return sc(250*M)
    if "Radiant" in n or "Ослепительн" in n or "Холодной" in n: return sc(120*M)  # Сияющие (-15% урон)
    return sc(60*M)                                    # прочие прямые плащи (Света/Тьмы/Славы/Героя/Избранного)

def price_skillbook(pid, n):
    # Кодексы Гигантов для заточки умений после пробуждения. Тир по диапазону (+1-10/+11-20/+21-30)
    # и типу (обычный / Mastery=благой safe / Discipline=смена пути). Сертификаты — отдельно.
    if "Certificate" in n or "Сертификат" in n: return round_nice(200*M)
    ch = 2 if ("Chapter 2" in n or "Глава 2" in n) else (1 if ("Chapter 1" in n or "Глава 1" in n) else 0)
    if "Mastery" in n:      base = {0:80, 1:150, 2:300}[ch]   # благие (safe-заточка)
    elif "Discipline" in n: base = {0:60, 1:120, 2:200}[ch]   # смена пути
    elif "Immortal" in n:   base = {0:60, 1:120, 2:200}[ch]   # Immortal Scroll
    else:                   base = {0:50, 1:100, 2:200}[ch]   # обычный Superior Giant Codex
    return round_nice(base * M)

def price_soul_crystal(pid, n):
    # Ensoul-кристаллы (в слоты оружия). Kain(P.Atk)/Mermoden(M.Atk) — топ-урон; спец-слот — премиум.
    if "Anakim" in n or "Seven Signs" in n or "Анаким" in n or "Печат" in n: return round_nice(250*M)
    if "Kain" in n or "Mermoden" in n or "Каина" in n or "Мермедена" in n:   return round_nice(200*M)
    return round_nice(150*M)   # Leona/Pantheon/Lionel (скорость/крит/HP-MP)

def price_spirit_stone(pid, n):
    # Камни духа/аугмента (600101): плащевые/брошь/диадема/аксессуары.
    if "брош" in n.lower() or "brooch" in n.lower(): return sc(300*M)      # лучший камень броши
    if any(k in n for k in ["Aden","Ferios","Elmor","Elmoreden","Аден","Феррит","Эльмор"]):
        return sc(150*M)                                                    # плащевой аугмент (Легендарные)
    if "Диадем" in n or "Circlet" in n: return round_nice(100*M)           # камень диадемы
    return round_nice(50*M)                                                 # аксессуарные/hair/life-камни

def price_consumable(pid, n):
    # Расходники-бафы (600035): поднять умеренно (это расходка).
    strong = ["Angel Cat","Кота-Ангела","Divine Protection","Покровительства"]
    medium = ["Cookie","Печенье","Stew","Рагу","Storm","Шторма","Energy","Всплеска",
              "Blessing","Благословл","Temptation","Искушение"]
    if any(k in n for k in strong): return round_nice(10*M)
    if any(k in n for k in medium): return round_nice(5*M)
    return round_nice(2*M)                                                  # мелкие зелья (Luck и т.п.)

def price_flat(v):
    return round_nice(v)

# ---------- ДИСПЕТЧЕР ----------
def compute_price(mid, pid, up, n):
    if mid == 600060: return price_weapon_full(pid)
    if mid == 600061: return price_weapon_blessed(pid)
    if mid == 600062: return price_weapon_bloody(pid, up)
    if mid == 600063: return armor_set_price(pid, n)
    if mid == 600064: return armor_blessed_price(pid, n)
    if mid == 600065: return sc(650*M)                       # Bloody Eternal/часть (PVE R99)
    if mid in (600066,600067,600068,600069,600070,600071): return price_transcendent(mid, up)
    if mid in (600052,600053,600054,600055): return price_leveling(mid, pid)
    if mid == 600030: return price_epic(pid, up, n)
    if mid == 600090: return price_brooch(pid, up)
    if mid == 600091: return price_brooch_stone(pid, up, n)
    if mid == 600008: return price_talisman(pid, up, n)
    if mid == 600044: return price_bracelet(pid, up, n)
    if mid in (600048,600028): return price_agathion(pid, up)
    if mid == 600032: return price_dye(pid, n)
    if mid == 600033: return price_skillbook(pid, n)         # книги умений/кодексы
    if mid == 600034: return price_hat(pid, up, n)
    if mid == 600100: return price_enchant_scroll(pid, n)
    if mid == 600101: return price_spirit_stone(pid, n)      # камни духа/аугмента
    if mid == 600102: return price_soul_crystal(pid, n)      # ensoul-кристаллы души
    if mid == 600011: return 1                               # соски/заряды — 1 адена (is_sellable=false)
    if mid == 600041:                                        # мат-лы драк.пушек
        if "Elcyum" in n: return round_nice(50*M)
        if "Dragon" in n: return round_nice(20*M)
        return round_nice(10*M)
    if mid == 600045: return round_small(npc_price(pid) * 2.5) or round_small(2000)  # кристаллы: NPC+наценка
    if mid == 600113: return round_small(npc_price(pid) * 2.5) or round_small(2000)  # самоцветы: NPC+наценка
    if mid == 600103: return round_nice(30*M)                # петы
    if mid == 600104: return 1                               # еда петов — 1 адена (is_sellable=false)
    if mid in (600105,600106,600107): return round_nice(10*M)  # снаряга петов (броня/бижа/оружие)
    if mid == 600043: return round_nice(50*M)                # руны опыта
    if mid in (600108,600109,600110,600111): return round_nice(20*M)  # внешний вид
    if mid == 600047:                                        # рубашки (средний тир) + свитки футболок
        if "Scroll" in n or "Свиток" in n:
            if "Shining" in n or "Сияющий" in n: return round_nice(40*M)
            return round_nice(30*M)                          # blessed/modify свитки футболок
        if any(k in n for k in ["Pa'agrio","Sayha","Eva","Maphr","Elemental","Paagrio"]):
            return round_nice(60*M)                          # элементальные рубашки (активки при +7)
        return round_nice(50*M)                              # Power Shirt и пр.
    if mid == 600025: return price_cloak(pid, up, n)         # плащи
    if mid == 600026: return price_belt(pid, up, n)          # пояса
    if mid == 600035: return price_consumable(pid, n)        # бакалея/расходники-бафы
    if mid == 600057: return round_nice(1*M)                 # клан
    if mid == 600112: return 1                               # стрелы/болты — 1 адена (сделаны is_sellable=false)
    return None

# ---------- ОБРАБОТКА ФАЙЛОВ ----------
ACTIVE = [600008,600011,600025,600026,600028,600030,600032,600033,600034,600035,
          600041,600043,600044,600045,600047,600048,600052,600053,600054,600055,600057,
          600060,600061,600062,600063,600064,600065,600066,600067,600068,600069,600070,600071,
          600090,600091,600100,600101,600102,600103,600104,600105,600106,600107,600108,600109,
          600110,600111,600112,600113]

def find_file(mid):
    c = os.path.join(CUSTOM, f"{mid}.xml")
    if os.path.exists(c): return c
    m = os.path.join(BASE, f"{mid}.xml")
    return m if os.path.exists(m) else None

item_block = re.compile(r"<item\b[^>]*>.*?</item>", re.S)
ing_re = re.compile(r'<ingredient\b[^>]*/>')
prod_re = re.compile(r'<production\b[^>]*id="(\d+)"')
id_attr = re.compile(r'id="(\d+)"')
count57_a = re.compile(r'(<ingredient\s+id="57"\s+count=")(\d+)(")')
count57_b = re.compile(r'(<ingredient\s+count=")(\d+)("\s+id="57")')

def process(mid, write):
    path = find_file(mid)
    if not path:
        print(f"!! {mid}: файл не найден"); return 0
    data = open(path, encoding="utf-8").read()
    changes = []

    def repl(mobj):
        block = mobj.group(0)
        if 'id="57"' not in block:
            return block
        prod = prod_re.search(block)
        if not prod: return block
        pid = prod.group(1)
        # метка = английское имя из индекса + русский комментарий из блока
        comment = " ".join(re.findall(r'<!--\s*(.*?)\s*-->', block))
        label = (gname(pid) + " ¦ " + comment).strip()
        # апгрейд? есть ingredient с id != 57
        up = False
        for ing in ing_re.findall(block):
            ida = id_attr.search(ing)
            if ida and ida.group(1) != "57":
                up = True; break
        newp = compute_price(mid, pid, up, label)
        if newp is None or newp <= 0:
            return block
        # АНТИ-ЭКСПЛОЙТ: цена покупки строго > выкупа NPC (~price/2). Пол = npc_price*0.7.
        # Исключение — категории по 1 адене (соски/болты/еда петов): им проставляется is_sellable=false.
        it = INDEX.get(pid)
        npc = it["price"] if it else 0
        if npc > 0 and not up and mid not in (600011, 600104, 600112):
            newp = max(newp, round_nice(npc * 0.7))
        # текущая цена
        cur = None
        ma = count57_a.search(block) or count57_b.search(block)
        if ma: cur = int(ma.group(2))
        nb = count57_a.sub(lambda m: m.group(1)+str(newp)+m.group(3), block)
        nb = count57_b.sub(lambda m: m.group(1)+str(newp)+m.group(3), nb)
        if nb != block:
            changes.append((pid, cur, newp, gname(pid)))
        return nb

    new_data = item_block.sub(repl, data)
    if write and new_data != data:
        open(path, "w", encoding="utf-8").write(new_data)
    # отчёт (первые несколько + сводка уникальных цен)
    uniq = {}
    for pid, cur, newp, nm in changes:
        uniq.setdefault(newp, 0)
        uniq[newp]+=1
    tag = "WRITE" if write else "dry"
    print(f"### {mid}  [{tag}] изменено {len(changes)} позиций; цены: " +
          ", ".join(f"{v}шт×{k/1_000_000:g}M" for k,v in sorted(uniq.items())))
    return len(changes)

total = 0
for mid in ACTIVE:
    total += process(mid, WRITE)
print(f"\nВАРИАНТ {VARIANT} (scale {SCALE}). Всего изменено позиций: {total}")
