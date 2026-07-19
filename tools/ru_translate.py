#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""EN->RU перевод имён рейд-боссов L2 (fallback, если нет в NpcName-RU.dat).
   Словарь общих терминов + транслитерация имён собственных в стиле RU-off."""
import re

# Словарь нарицательных слов (титулы, типы существ, прилагательные)
WORD = {
    "Lord": "Лорд", "Captain": "Капитан", "Guardian": "Страж", "Herald": "Вестник",
    "Vengeful": "Мстительный", "Statue": "Статуя", "Demon": "Демон", "Spirit": "Дух",
    "Priest": "Жрец", "Monster": "Чудовище", "Giant": "Гигант", "Leader": "Предводитель",
    "Evil": "Злой", "Chief": "Вождь", "Wizard": "Колдун", "King": "Король",
    "Corrupted": "Порочный", "Wandering": "Блуждающий", "Tiger": "Тигр",
    "Commander": "Командир", "Eye": "Око", "Queen": "Королева", "Golem": "Голем",
    "Hero": "Герой", "Angel": "Ангел", "Embryo": "Зародыш", "Knight": "Рыцарь",
    "Worshipper": "Поклонник", "Zombie": "Зомби", "Soul": "Душа", "Scavenger": "Падальщик",
    "Betrayer": "Предатель", "Patriarch": "Патриарх", "Cat": "Кот", "Agent": "Агент",
    "Messenger": "Посланник", "Servitor": "Прислужник", "Flame": "Пламенный",
    "Gargoyle": "Гаргулья", "Red": "Красный", "Water": "Водяной", "Earth": "Земляной",
    "Ghost": "Призрак", "Roaring": "Ревущий", "Behemoth": "Бегемот", "Last": "Последний",
    "Lesser": "Малый", "Bloody": "Кровавый", "Patrol": "Дозорный", "Avatar": "Аватар",
    "Enhanced": "Улучшенный", "Dark": "Тёмный", "Fallen": "Падший", "Orc": "Орк",
    "Elf": "Эльф", "Princess": "Принцесса", "Prince": "Принц", "Dryad": "Дриада",
    "Matriarch": "Матриарх", "Tracker": "Следопыт", "Ratman": "Крысолюд",
    "Collector": "Сборщик", "Hornet": "Шершень", "Mercenary": "Наёмник",
    "Basilisk": "Василиск", "Grave": "Могильный", "Robber": "Расхититель",
    "Royal": "Королевский", "Guard": "Гвардеец", "Corsair": "Корсар",
    "Warlock": "Чернокнижник", "Grand": "Верховный", "Partisan": "Партизан",
    "Spider": "Паук", "Wasteland": "Пустошь", "Bandit": "Бандит", "Beast": "Зверь",
    "Berserker": "Берсерк", "Blade": "Клинок", "Boar": "Вепрь", "Centaur": "Кентавр",
    "Chaos": "Хаос", "Death": "Смерть", "Deadman": "Мертвец", "Dragon": "Дракон",
    "Drake": "Дрейк", "Dread": "Ужасный", "Fire": "Огненный", "Goblier": "Гоблин",
    "Great": "Великий", "High": "Верховный", "Hound": "Гончая", "Iron": "Железный",
    "Lizardmen": "Люди-Ящеры", "Looter": "Мародёр", "Marsh": "Болотный",
    "Mechanic": "Механик", "Minotaur": "Минотавр", "Mirror": "Зеркальный",
    "Plague": "Чумной", "Prefect": "Префект", "Protector": "Защитник",
    "Ranger": "Рейнджер", "Refugee": "Беженец", "Servant": "Слуга", "Shaman": "Шаман",
    "Storm": "Штормовой", "Thief": "Вор", "Totem": "Тотемный", "Tree": "Древо",
    "Wild": "Дикий", "Witch": "Ведьма", "Wrath": "Гнев", "Blue": "Синий",
    "Ruby": "Рубиновый", "Immortal": "Бессмертный", "Doom": "Рока",
    "Destruction": "Разрушения", "Oblivion": "Забвения", "Abyss": "Бездны",
    "Fierce": "Свирепый", "Crazy": "Безумный", "Rotting": "Гниющий",
    "Soulless": "Бездушный", "Hard": "Крепкий", "Hot": "Горячий", "Sky": "Небесный",
    "Underwater": "Подводный", "Deity": "Божество", "Cherub": "Херувим",
    "Handmaiden": "Служанка", "Henchman": "Приспешник", "Envoy": "Посланец",
    "Applicant": "Претендент", "Savior": "Спаситель", "Avenger": "Мститель",
    "Archon": "Архонт", "Clone": "Клон", "Sample": "Образец", "Beacon": "Маяк",
    "Flag": "Знамя", "Road": "Дорога", "Retreat": "Убежище", "Well": "Колодец",
    "Garden": "Сад", "Springs": "Источники", "Negative": "Негатив", "Prime": "Первичный",
    "Splendor": "Великолепие", "Enmity": "Вражда", "Harp": "Арфа", "Ember": "Уголь",
    "Gang": "Банда", "Numa": "Нума", "Leo": "Лео", "Pan": "Пан",
    "Discarded": "Отвергнутый", "Guilt": "Вины", "Slaughter": "Резни",
    "Turmoil": "Смуты", "Insolence": "Дерзости", "Splendor": "Великолепия",
    "Goblin": "Гоблин", "Skeleton": "Скелет", "Undead": "Нежить",
    "Ancient": "Древний", "Elder": "Старший", "Young": "Молодой",
    "Great": "Великий", "Blooded": "Кровожадный", "Cursed": "Проклятый",
    "Frenzied": "Неистовый", "Furious": "Яростный", "Mad": "Безумный",
    "Ghostly": "Призрачный", "Silent": "Безмолвный", "Nightmare": "Кошмарный",
    # служебные
    "of": "", "the": "", "The": "", "and": "и",
}

# Транслитерация: диграфы (сначала), затем одиночные
DIGRAPHS = [
    ("sch", "ш"), ("tch", "ч"), ("sh", "ш"), ("ch", "ч"), ("ph", "ф"),
    ("th", "т"), ("kh", "х"), ("zh", "ж"), ("ya", "я"), ("yu", "ю"),
    ("yo", "ё"), ("ye", "е"), ("oo", "у"), ("ee", "и"), ("ck", "к"),
    ("ou", "у"), ("kn", "н"), ("qu", "кв"),
]
SINGLE = {
    "a": "а", "b": "б", "c": "к", "d": "д", "e": "е", "f": "ф", "g": "г",
    "h": "х", "i": "и", "j": "дж", "k": "к", "l": "л", "m": "м", "n": "н",
    "o": "о", "p": "п", "q": "к", "r": "р", "s": "с", "t": "т", "u": "у",
    "v": "в", "w": "в", "x": "кс", "y": "и", "z": "з",
}


def translit(token):
    """Транслитерация одного латинского токена в кириллицу."""
    low = token.lower()
    res = []
    i = 0
    while i < len(low):
        matched = False
        for dg, ru in DIGRAPHS:
            if low.startswith(dg, i):
                res.append(ru)
                i += len(dg)
                matched = True
                break
        if matched:
            continue
        ch = low[i]
        res.append(SINGLE.get(ch, ch))
        i += 1
    out = "".join(res)
    # капитализация как в исходнике
    if token[:1].isupper():
        out = out[:1].upper() + out[1:]
    return out


def translate_name(en):
    """Переводит английское имя босса в русское (словарь + транслит)."""
    # убрать служебные хвосты и притяжательное 's
    en = en.replace("Transformed:", "").strip()
    en = re.sub(r"'s\b", "", en)      # притяжательное -> опустить
    en = en.replace("'", "")           # прочие апострофы
    tokens = en.split()
    out = []
    for core in tokens:
        if core in WORD:
            w = WORD[core]
            if w:
                out.append(w)
        elif re.fullmatch(r"[A-Za-z][A-Za-z\-]*", core):
            out.append(translit(core))
        else:
            out.append(core)           # цифры, символы
    s = " ".join(x for x in out if x)
    s = re.sub(r"\s+", " ", s).strip()
    return s


if __name__ == "__main__":
    tests = ["Discarded Guardian", "Zombie Lord Ferkel", "Sukar Ratman Chief",
             "Evil Spirit Bifrons", "Dagoniel's Herald Malex", "Vengeful Statue of Guilt",
             "Corrupted Wandering Soul", "Queen Shyeed", "Grave Robber Akata",
             "Royal Guard Vuku", "Timak Orc Chief", "Water Spirit Lian"]
    for t in tests:
        print(f"{t:35s} -> {translate_name(t)}")
