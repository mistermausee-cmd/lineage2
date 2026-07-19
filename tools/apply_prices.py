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

# ---------- ПРАВИЛА ЦЕН ПО РАЗДЕЛАМ ----------
# Возвращает цену адены для (multisell_id, production_id, is_upgrade).

def price_weapon_full(pid):
    g = grade(pid)
    return {"R": sc(120*M), "R95": sc(400*M), "R99": sc(1200*M)}.get(g, sc(120*M))

def price_weapon_blessed(pid):  # апгрейд
    g = grade(pid)
    return {"R": sc(80*M), "R95": sc(250*M), "R99": sc(700*M)}.get(g, sc(120*M))

def price_weapon_bloody(pid, up):  # 600062 Bloody Amaranthine R99
    return sc(700*M) if up else sc(1500*M)

def armor_set_price(pid, n):
    g = grade(pid)
    if "Immortal" in n: return sc(15*M)
    if "Twilight" in n: return sc(30*M)
    if "Seraph" in n:   return sc(120*M)
    if "Eternal" in n:  return sc(350*M)
    return {"R": sc(30*M), "R95": sc(120*M), "R99": sc(350*M)}.get(g, sc(50*M))

def armor_blessed_price(pid, n):  # апгрейд 600064
    if "Immortal" in n or "Twilight" in n: return sc(20*M)
    if "Seraph" in n: return sc(80*M)
    if "Eternal" in n: return sc(250*M)
    return sc(80*M)

def price_transcendent(mid, up):
    lv = {600066:1,600067:2,600068:3,600069:4,600070:5,600071:6}[mid]
    base = {1:500*M, 2:800*M, 3:1200*M, 4:1800*M, 5:2600*M, 6:4000*M}[lv]
    return sc(base)

def price_leveling(mid, pid):
    is_w = itype(pid) == "Weapon"
    tbl = {
        600052: (1*M, 0.5*M),   # B
        600053: (5*M, 2*M),     # A
        600054: (20*M, 8*M),    # S
        600055: (60*M, 20*M),   # S80
    }[mid]
    return round_nice(tbl[0] if is_w else tbl[1])  # прокачка НЕ масштабируется вариантом

def price_epic(pid, up, n):
    # цепочки богов (…Ур.N / Stage N)
    if re.search(r"Ур\.\d", n) or re.search(r"Stage", n):
        return sc(1000*M) if not up else sc(400*M)
    if up:  # soul/blessed/immortal апгрейд классической эпики
        return sc(750*M)
    # полная покупка — тир по имени (RU+EN)
    t2 = ["Баюма","Антараса","Валакаса","Фреи","Линдвиора","Таути",
          "Baium","Antharas","Valakas","Freya","Lindvior","Tauti"]
    t3 = ["Лилит","Начала","Власти","Истины","Властного","Искателя","Истхины","Октависа",
          "Lilith","Ring of Insolence","Ring of Authority","Ring of Truth","Istina","Octavis"]
    if any(k in n for k in t3): return sc(1500*M)
    if any(k in n for k in t2): return sc(1000*M)
    return sc(600*M)  # tier1: Ядра/Белефа/Закена/Фринтезы/КоролевыМуравьев/Орфен/Байлора/ЗемлЧервя

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
    if "Семи Печатей" in n or "Seven Signs" in n: return sc(300*M)
    if any(k in n for k in ["Анаким","Лилит","Anakim","Lilith"]): return sc(150*M)
    if any(k in n for k in ["Сайха","Бенира","Изобилия","Sayha","Benira","Abundance"]):
        return round_nice(15*M)          # длинные цепочки — дёшево за шаг
    if up: return round_nice(40*M)        # Бессмертия→…→Позолоченного
    return round_nice(50*M)

def price_bracelet(pid, up):
    return round_nice(60*M) if up else round_nice(50*M)

def price_agathion(pid, up):
    return round_nice(8*M) if up else round_nice(15*M)

def price_dye(pid, n):
    if any(k in n for k in ["Сопр","Защиты","Resistance"]): return round_nice(20*M)  # резист-краски
    return round_nice(50*M)                                      # стат-краски (важные)

def price_enchant_scroll(pid, n):
    w = "Оружие" in n or "Weapon" in n
    if "Священный" in n or "Divine" in n: return round_nice(150*M if w else 90*M)
    if "Благословенный Свиток" in n: return round_nice(60*M if w else 35*M)
    if "Камень" in n or "Кристалл" in n: return round_nice(80*M if w else 50*M)
    if "Свиток" in n and ("Оружие" in n or "Доспех" in n): return round_nice(20*M if w else 12*M)
    return round_nice(30*M)  # плащ/футболка/диадема

def price_hat(pid, up, n):
    if "Диадема" in n or "Диадему" in n:
        if "Радужная" in n: return sc(400*M)
        if "Драгоценная" in n: return sc(250*M)
        if "Свиток" in n: return round_nice(30*M)
        return sc(150*M)
    if "Камень" in n: return round_nice(100*M)
    return round_nice(30*M)  # обычные шапки/нимбы

def price_flat(v):
    return round_nice(v)

# ---------- ДИСПЕТЧЕР ----------
def compute_price(mid, pid, up, n):
    if mid == 600060: return price_weapon_full(pid)
    if mid == 600061: return price_weapon_blessed(pid)
    if mid == 600062: return price_weapon_bloody(pid, up)
    if mid == 600063: return armor_set_price(pid, n)
    if mid == 600064: return armor_blessed_price(pid, n)
    if mid == 600065: return sc(500*M)                       # Bloody Eternal/часть
    if mid in (600066,600067,600068,600069,600070,600071): return price_transcendent(mid, up)
    if mid in (600052,600053,600054,600055): return price_leveling(mid, pid)
    if mid == 600030: return price_epic(pid, up, n)
    if mid == 600090: return price_brooch(pid, up)
    if mid == 600091: return price_brooch_stone(pid, up, n)
    if mid == 600008: return price_talisman(pid, up, n)
    if mid == 600044: return price_bracelet(pid, up)
    if mid in (600048,600028): return price_agathion(pid, up)
    if mid == 600032: return price_dye(pid, n)
    if mid == 600033: return round_nice(200*M) if "Certificate" in n else round_nice(100*M)
    if mid == 600034: return price_hat(pid, up, n)
    if mid == 600100: return price_enchant_scroll(pid, n)
    if mid == 600101: return round_nice(100*M)               # камни духа
    if mid == 600102: return round_nice(200*M if "15" in n else 150*M)  # кристаллы души
    if mid == 600011: return round_nice(3*M)                 # 5000 зарядов/шотов
    if mid == 600041:                                        # мат-лы драк.пушек
        if "Elcyum" in n: return round_nice(50*M)
        if "Dragon" in n: return round_nice(20*M)
        return round_nice(10*M)
    if mid == 600045: return round_nice(1*M)                 # кристаллы
    if mid == 600113: return None                            # самоцветы — оставить как есть
    if mid == 600103: return round_nice(30*M)                # петы
    if mid in (600104,600105,600106,600107): return round_nice(10*M)  # снаряга петов
    if mid == 600043: return round_nice(50*M)                # руны опыта
    if mid in (600108,600109,600110,600111): return round_nice(20*M)  # внешний вид
    if mid == 600047: return round_nice(20*M)                # рубашки
    if mid == 600025: return sc(150*M)                       # плащи
    if mid == 600026: return sc(100*M)                       # пояса
    if mid == 600035: return round_nice(2*M)                 # бакалея/расходники
    if mid == 600057: return round_nice(1*M)                 # клан
    if mid == 600112: return round_nice(5*M)                 # стрелы/болты
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
        it = INDEX.get(pid)
        npc = it["price"] if it else 0
        if npc > 0 and not up:   # для полных покупок держим цену выше NPC-выкупа
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
