# Инструкция 7. Меню Alt+B (полное), рейты и эксплуатация сервера

> Для будущего чата. Дополняет `04_МАГАЗИН_ALTB.md` (там только магазин) и `06_ЭКОНОМИКА.md`.
> Здесь: **все вкладки Community Board**, кастомные **Java-обработчики**, **точная математика рейтов**
> и **как запускать/останавливать сервер и не утонуть в логах**.

---

## 0. Контекст одним абзацем
Соло-сервер **L2J Mobius Grand Crusade**. Всё кастом-меню — это **Community Board (Alt+B)**.
HTML меню лежит в `server/game/data/html/CommunityBoard/Custom/`, логика — в Java-обработчиках
`server/game/data/scripts/handlers/bypass/communityboard/`. **Скрипты в `data/scripts/` компилируются
движком ПРИ СТАРТЕ сервера — пересборка jar НЕ нужна**, достаточно рестарта. Правки только в ветку
`main`, пуш ТОЛЬКО через github power (см. §6). Игрок забирает `git pull --ff-only origin main`.

---

## 1. Левое меню (`navigation.html`) — актуальные вкладки
Порядок кнопок сверху вниз (файл `CommunityBoard/Custom/navigation.html`):

| Кнопка | Бипас | Что открывает |
|---|---|---|
| Главная | `_bbstop;home.html` | приветствие |
| Баффер | `_bbstop;buffer/main.html` | схемы бафов (иконки + готовые наборы) |
| Магазин | `_bbstop;merchant/main.html` | 9 отделов (см. `04_МАГАЗИН_ALTB.md`) |
| Телепорт | `_bbstop;gatekeeper/main.html` | города/деревни/фарм/боссы (см. §3) |
| Мой телепорт | `_bbsmytp` | личные метки телепорта (см. §2) |
| Склад | `_bbswh` | личный + клановый склад (см. §2) |
| Премиум | `_bbspremium` | статус/бонусы/покупка (см. §2) |
| Информация | `_bbsinfo` | статы игрока + персональные рейты (см. §2, §4) |

**УДАЛЕНЫ из меню** (по просьбе): «Поиск дропа» (`dropsearch/`) и «Понижение уровня» (`delevel/`).
Их обработчики (`DropSearchBoard.java`, `_bbsdelevel` в HomeBoard) остались зарегистрированы, но ссылок в меню нет.

---

## 2. Кастомные Java-обработчики Community Board
Все в `server/game/data/scripts/handlers/bypass/communityboard/`. Регистрируются в
`handlers/MasterHandler.java` (импорт + класс в массиве, инстанцируется `getDeclaredConstructor().newInstance()`).
Валюта меню = **адена (itemId 57)**, конфиг `config/Custom/CommunityBoard.ini`.

### HomeBoard.java (штатный, мы правили)
- Обрабатывает `_bbstop`, `_bbshome`, `_bbsteleport;KEY`, `_bbsbuff`, `_bbsunbuff`, `_bbsheal`, `_bbsdelevel`, мультиселлы.
- **Цена телепорта уровневая:** `teleportPrice = (player.getLevel() > 99) ? 100000 : 0` — до 99 ур. включительно бесплатно, дальше 100k адены. (Раньше был флэт `COMMUNITYBOARD_TELEPORT_PRICE`.)
- Блок `_bbspremium` из HomeBoard **удалён** — премиум теперь в ServiceBoard (иначе конфликт команд).

### MyTeleportBoard.java (наш, вкладка «Мой телепорт»)
- Команды: `_bbsmytp` (список), `_bbstpadd <имя>` (создать метку в точке игрока), `_bbstpgo;<id>` (телепорт к метке), `_bbstpdel;<id>` (подтверждение удаления), `_bbstpdelok;<id>` (удалить).
- Хранение: таблица БД **`bbs_teleport_marks`** (playerId, markId, name, x, y, z) — **создаётся автоматически** в конструкторе (`CREATE TABLE IF NOT EXISTS`), ручной SQL не нужен.
- Лимит **10 меток**, имя ≤24 симв (санитайз опасных символов), создание **1 000 000 адены**.
- Телепорт к метке — та же уровневая цена (≤99 бесплатно, >99 = 100k).
- Ввод имени: в HTML `<edit var="tpname">` + `bypass _bbstpadd $tpname`.

### ServiceBoard.java (наш, вкладки «Информация», «Премиум», «Склад»)
Команды: `_bbsinfo`, `_bbspremium` (показ) / `_bbspremium;<дни>` (покупка), `_bbswh` (хаб склада), `_bbswhpd`/`_bbswhpw` (личный склад положить/забрать), `_bbswhcd`/`_bbswhcw` (клан-склад).
- **Склад:** повторяет логику `handlers/bypass/npc/PrivateWarehouse.java` и `ClanWarehouse.java`:
  `player.setActiveWarehouse(...)`, `new WareHouseDepositList(player, ...PRIVATE|CLAN)` / `WareHouseWithdrawalList`,
  проверки `GeneralConfig.ALLOW_WAREHOUSE`, для клана — `clan.getLevel()>=1` и `player.hasAccess(ClanAccess.ACCESS_WAREHOUSE)` (для «забрать»).
  Перед открытием окна склада шлём `new ShowBoard()` (закрыть доску).
- **Премиум:** пакеты-заглушки в коде `PREMIUM_PACKAGES = {3→50млн, 7→100млн, 30→350млн}` (адена).
  Статус/срок: `PremiumManager.getInstance().getPremiumExpiration(player.getAccountName())` (long, 0=нет),
  выдача `addPremiumTime(acc, days, TimeUnit.DAYS)`, флаг `player.hasPremiumStatus()`.
- **Информация:** статы игрока + сервер + таблица рейтов (см. §4).

### Проверка компиляции скриптов (без запуска сервера)
```bash
cd server/game/data/scripts
javac -cp "/…/server/libs/*" -d /tmp/out $(find . -name "*.java")   # весь дерево скриптов
# или только пакет:
javac -cp "/…/server/libs/*" -d /tmp/out handlers/bypass/communityboard/*.java
```
`server/libs/GameServer.jar` содержит все классы ядра. Так ловим ошибки до рестарта.
Полезно смотреть сигнатуры: `javap -cp server/libs/GameServer.jar -p <класс>`; декомпиляция логики: `javap -c`.

---

## 3. Вкладка «Телепорт» — генератор и источники данных
HTML: `CommunityBoard/Custom/gatekeeper/*.html` (main, cities, villages, farm_low1..2, farm_high, grandboss, raids1..N).
Конфиг точек: `config/Custom/CommunityBoard.ini` → секция `CommunityTeleportList = \` (строки `Ключ,X,Y,Z;\`).
Бипас телепорта: `_bbsteleport;Ключ`. **Ключи HTML обязаны 1:1 совпадать с ключами конфига.**

Генератор: **`tools/gen_teleport.py`** (Python 3.11 через pyenv; системный 3.9 не поддерживает f-string с backslash).
Запуск создаёт HTML + пишет `tools/_teleport_list.txt`; его вставляют в CommunityBoard.ini (строки 1..N до `CommunityTeleportList =` + список, без хвостового `\`).

Источники координат (ВСЕ — GM-выверенные админ-телепорты, НЕ угадывать):
- Города/деревни/фарм: `data/html/admin/teleports/TownAreas/*` и `WorldAreas/*` (первый `admin_move_to X Y Z`).
- Гранд/эпик-боссы: `data/html/admin/teleports/raid/raid_special.htm`. Инстансовые боссы → координата **NPC-входа** (менеджера) из спавнов, а не арены (Freya→Jinia 32781, Frintezza→Guide 32011, Octavis→Lydia 32892 и т.д. — ищи в `scripts/ai/bosses/*` + спавны).
- Рейд-боссы: `tools/boss_index.py raid` (id, уровень, координаты из всех спавн-файлов).

**Фарм-зоны выверяются по РЕАЛЬНЫМ уровням мобов сервера** (сервер сильно перебалансирован от ретейла!):
- `tools/zone_audit.py` — проходит все `data/spawns/**/*.xml`, join с уровнями NPC из `stats/npcs`, считает медиану уровня мобов на зону-файл. Пример реальности: Долина Драконов 80-83, Пещера Гигантов 100, Пылающая Топь 98-99, Кладбище 98-99. Зоны без мобов (Кетра/Варка = только рейд) исключены.
- `tools/build_farm.py` — сопоставляет зоны с безопасными админ-координатами.

### Русские названия боссов/NPC (расшифровка клиентских .dat)
Официальные имена берём из клиента сборки (ветка **`system`** этого же репо, папки `system-e` и `system-ru`):
- Файлы `NpcName-RU.dat` / `NpcName-e.dat` — формат **Lineage2Ver413 = RSA + zlib**.
- `tools/l2dat_decode.py` — расшифровка. **Рабочий ключ — `L2KEY=alt` (exp 0x1d)**, НЕ «orig».
- `tools/parse_ru.py` — парсит бинарь в `id→имя` (структура: header 4б, затем `[id u32][len:0x80|nchars][UTF-16 имя+null][desc][const 9c e8 a9 ff]`; англ. файл — однобайтовый).
- Результат: `tools/_ru/npcname_ru.json` (id→рус), `tools/_ru/en2ru.json` (англ.имя→рус, через общий client id из NpcName-e+RU).
- В `gen_teleport.py` имя рейда: `RU по id → EN2RU по англ.имени → ru_translate.py` (словарь L2-терминов + транслитерация как fallback).
- ⚠️ Клиент l2scripts содержит НЕ всех боссов сервера (совпало ~24 из 255) — остальным имя даёт fallback-перевод.
- Папки `tools/_ru/` и `tools/_l2s/` в `.gitignore` (большие расшифрованные файлы), пересоздаются из ветки `system`.

---

## 4. РЕЙТЫ — точная математика (важно, часто спрашивают)
Базовые рейты — `config/Rates.ini`; премиум-множители — `config/Custom/PremiumSystem.ini`.

| Показатель | База (конфиг) | Премиум × | Персональный бонус (руны/предметы) |
|---|---|---|---|
| Опыт (XP) | `RATE_XP` = 5 | `PREMIUM_RATE_XP` = 2 | `Stat.BONUS_EXP`/100 |
| Мастерство (SP) | `RATE_SP` = 5 | `PREMIUM_RATE_SP` = 2 | `Stat.BONUS_SP`/100 |
| Адена | `RATE_DROP_AMOUNT_BY_ID[57]` = 3 | premium `[57]` = 2 | `Stat.BONUS_DROP_ADENA` |
| Дроп кол-во | `RATE_DEATH_DROP_AMOUNT_MULTIPLIER` = 1 | `PREMIUM_RATE_DROP_AMOUNT` = 2 | `Stat.BONUS_DROP_AMOUNT` |
| Дроп шанс | `RATE_DEATH_DROP_CHANCE_MULTIPLIER` = 3 | `PREMIUM_RATE_DROP_CHANCE` = 1 | `Stat.BONUS_DROP_RATE` |
| Спойл шанс | `RATE_SPOIL_DROP_CHANCE_MULTIPLIER` = 4 | `PREMIUM_RATE_SPOIL_CHANCE` = 1 | `Stat.BONUS_SPOIL_RATE` |

**Реальная игровая формула опыта** (декомпилировано из `Attackable.calculateRewards`):
```
итог_опыта = базовый_опыт_моба × RATE_XP × (премиум? PREMIUM_RATE_XP : 1) × getExpBonusMultiplier()
где getExpBonusMultiplier() = вайталити(getVitalityExpBonus) × (getValue(Stat.BONUS_EXP,100)/100)
```
⚠️ **Ключевой нюанс:** `getExpBonusMultiplier()` включает **вайталити** (временный, до `RateVitalityExpMultiplier`=2).
Поэтому «полное» число могло показывать x40 (5×2×2вайт×2руна) — оно верное, но вайталити ВРЕМЕННЫЙ.
В `ServiceBoard.infoPage()` мы показываем **устойчивый** рейт = база × премиум × (BONUS_EXP/100, только руны/предметы),
а вайталити выводим **отдельной строкой**. Множители дропа берём `player.getStat().getValue(Stat.BONUS_*, 1)` (дефолт 1).
Готовой единой переменной «весь рейт» в ядре нет — считаем по компонентам.

---

## 5. Стиль HTML Alt+B (чтобы не было визуальных багов)
Единая палитра/элементы (как в магазине/баффере):
- Панель: `<table width=565 height=474 background="L2UI_CT1.Windows_DF_TooltipBG">` (у магазина 462).
- Заголовок: `<font name="hs12" color="CDB67F">`, подзаголовок `808A99`, описания `9AA4B0`, метки-лейблы `B0A070`, футер `696969`, буст/зелёный `70FFCA`.
- Разделитель: `<img src="L2UI.SquareGray" width=515 height=1>`. Кнопки: `back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF"`.
- Доступные крупные шрифты: **hs10, hs11, hs12, hs15** (hs9 НЕТ).

**Грабли вёрстки (реально ловили):**
1. **Наложение строк:** крупный шрифт (`hs10/hs12`) + `<br>` → `<br>` двигает на высоту ОБЫЧНОГО шрифта, крупный текст наезжает. **Решение:** заголовки/секции и кнопочные ряды класть в `<table>` со строками фиксированной высоты (`<td height=…>`), а не через `<br>`.
2. **Текст вылезает за кнопку:** в дефолтном шрифте ~6-7px/символ. Кнопка 250px вмещает ~34 симв. Длинные имена рейдов **обрезать** (`boss_label(... , 34)` в gen_teleport). Рейды: 2 колонки × 250px.
3. **Пагинация:** кнопки Назад/В меню/Далее — в таблице из **3 фиксированных ячеек** (пустая ячейка если направления нет), иначе при появлении «Назад» «Далее» съезжает.
4. **Влезаемость по высоте:** окно ~474px. Считать строки: заголовок+подзаг+разделители ≈ 100px оверхед, дальше ~отбор. Инфо-страницу делали в 2 колонки / компактными таблицами, чтобы влезла.
5. Динамические страницы строим в Java (ServiceBoard.wrap()), плейсхолдер `%navigation%` заменяем сами.

---

## 6. ЭКСПЛУАТАЦИЯ сервера (важно!)

### Запуск / остановка (Linux)
- **Запуск:** `bash start_linux.sh` (стартует login, ждёт 8с, стартует game; сам делает `chmod +x`).
- **Остановка:** `bash stop_linux.sh` — гасит СНАЧАЛА циклы `*Task.sh`, ПОТОМ java. 
- ⚠️ **НЕЛЬЗЯ** останавливать только `pkill -f *.jar`: скрипты-циклы `LoginServerTask.sh`/`GameServerTask.sh` (внутри `LoginServer.sh`/`GameServer.sh`) тут же перезапустят jar. Всегда убивать и `*Task.sh`.

### Проблема «400к файлов логов» (была, решена)
Причина: `LoginServerTask.sh` (`until [ $err == 0 ]`) бесконечно перезапускал упавший логин-сервер, каждый цикл архивируя `stdout.log`/`java0.log.0` в файлы с меткой времени, БЕЗ очистки. Усугублялось параллельными зомби-циклами (несколько запусков `LoginServer.sh`), дравшимися за порт 2106.
Что сделано:
- `LoginServerTask.sh`: **защита от runaway** — стоп после 5 быстрых падений (<30с) подряд + удаление архивов `*_java.log`/`*_stdout.log` старше 7 дней. `GameServerTask.sh`: тоже очистка (и он и так `break` при не-2 коде выхода).
- `log.cfg` (game и login): `ConsoleHandler.level` и `network.serverpackets/clientpackets.level` **FINER → INFO** (FINER логировал КАЖДЫЙ пакет — раздувало логи и грузило сервер).
- Диагностика краш-цикла: метки времени файлов идут каждые 1-2 сек = сервер падает при старте и рестартится. Смотреть причину: `cat server/login/log/stdout.log`, `tail server/login/log/error0.log`, `ss -tlnp | grep -E '2106|9014'`.

### Права на скрипты (частая засада с git pull)
- `.sh` скрипты в репо помечены исполняемыми (`100755`). Раньше были `100644` → `Permission denied` после checkout.
- На сервере частые конфликты `git pull` из-за бита `+x`. **Решение раз и навсегда на сервере:** `git config core.fileMode false` (git перестаёт реагировать на различия режима), затем `git pull --ff-only origin main`.
- Если pull ругается «local changes would be overwritten» — обычно это только режим файла: `git checkout -- <файл>` затем pull, или `core.fileMode false`.

### Порты
- **2106** — клиент↔login. **9014** — game↔login (внутренний, слушает login). **7777** — игровой мир (game↔клиент).
- Успешный старт логина в логе: `Login client listener started on 0.0.0.0:2106` + `Game server listener is listening on 127.0.0.1:9014`.

### Пуш изменений (для агента в будущем чате)
- Рабочая копия: `/projects/sandbox/lineage2`. Ветка `main`.
- **Прямой `git push` НЕ работает (auth).** Коммитить обычным `git commit`, пушить ТОЛЬКО через
  **kiro_powers → github → push_to_remote** (owner=`mistermausee-cmd`, repository_name=`lineage2`, remote_branch_name=`main`, path=`/projects/sandbox/lineage2`).
- Общение с пользователем — на русском.

---

## 7. Инструменты `tools/` (актуальные для меню Alt+B)
| Файл | Назначение |
|---|---|
| `gen_teleport.py` | генератор HTML телепорта + список точек в CommunityBoard.ini |
| `zone_audit.py` | реальные уровни мобов по всем зонам (медиана из спавнов+статов) |
| `build_farm.py` | фарм-зоны + безопасные админ-координаты |
| `boss_index.py` | `raid`/`grand` — спавны боссов (id, уровень, координаты) |
| `list_grand.py` | гранд-боссы из stats/npcs + рус.имена |
| `l2dat_decode.py` | расшифровка клиентских .dat (Ver413, ключ `L2KEY=alt`) |
| `parse_ru.py` | парс NpcName bin → id→имя JSON |
| `ru_translate.py` | fallback EN→RU (словарь L2-терминов + транслит) |

> Для магазина — свои инструменты, но они УСТАРЕЛИ (см. предупреждение в `04_МАГАЗИН_ALTB.md`): магазин правится вручную.
> `tools/_ru/`, `tools/_l2s/` — временные (gitignored), пересоздаются из ветки `system`.


---

## 8. Геодата (подключена)

**Что это:** данные проходимости/высот мира (`.l2j`). Без них мобы проваливаются
сквозь текстуры, криво ходят, ломается pathfinding.

**Где в git:** в ветке `main` файлы `*.l2j` НЕ хранятся (`.gitignore` →
`server/game/data/geodata/*.l2j`), лежит только `Readme.txt`. Сама геодата
хранится в ветке **`system`**, папка `geodata/` (220 регионов, ~820 МБ).

**Как ставится (один раз на сервере):**
```bash
bash server/game/download_geodata.sh
```
Скрипт качает **220 регионов (~820 МБ)** — геодату **Grand Crusade от L2Script**
из ветки `system` этого же репо. Способ 1 — `git clone --depth 1 --branch system
+ sparse-checkout geodata` (~11 сек); фолбэк — поштучно через `raw.githubusercontent`.
URL берётся из `remote.origin.url` (если это github.com), иначе — хардкод
`github.com/mistermausee-cmd/lineage2`.

**Выбор источника (сравнивали 2 набора):**
| | L2Script (взяли) | bartty/mobius-geo |
|---|---|---|
| регионов | **220** | 215 |
| хроники | **Grand Crusade** | общая Mobius (старее) |
| покрытие | полное (+`12_22,26_17,27_15,27_16,27_17`) | нет этих 5 |
| детализация общих 215 | flat 66% / complex 26% | flat 52% / **complex 40%** |
| совместимость с движком | 220/220 OK | 215/215 OK |

bartty чуть детальнее по горизонтали (больше complex-блоков), НО это общая
геодата неясной хроники: на зонах, изменённых в GC, её точная геометрия
может НЕ совпадать с клиентом. Для GC-сервера решают совпадение хроник +
покрытие → L2Script.

**Формат / как движок её ищет:**
- Имена файлов: `%d_%d.l2j` (напр. `20_18.l2j`) — константа `FILE_NAME_FORMAT`
  в `org.l2jmobius.gameserver.geoengine.GeoEngine`.
- Регион = 256×256 блоков; блок: 1 байт типа → `0` FlatBlock (1 short высота, →
  плоский регион = 256·256·3 = **196608 байт**), `1` ComplexBlock (64 short),
  `2` MultilayerBlock (переменная длина). Заголовка у файла нет.
- Чтение: `loadRegion()` через `RandomAccessFile` + `FileChannel.map` (mmap),
  парсит `new Region(ByteBuffer)`.

**Проверка совместимости (как делали):** извлечь классы geoengine из
`GameServer.jar`, скормить каждый `.l2j` в `new Region(buf)` и убедиться, что
`buf.position() == size` (буфер прочитан ровно до конца, без исключений).
Итог: **220/220 OK** — геодата L2Script (ветка `system`) совместима с этим `GameServer.jar`.
Для переписи типов блоков (flat/complex/multilayer) — replicate формат в Python
(flat +2б, complex +128б, multilayer: 64 ячейки × [1б слои + слои·2б]).

**Конфиг:** `server/game/config/GeoEngine.ini`
- `PathFinding = 2` — включено (значение по умолчанию в репо). Требует наличия
  файлов геодаты, иначе лаги + ошибки в логах → пока не скачал, ставь `0`.
- `GeoDataPath = ./data/geodata/`.

**Память:** `server/game/java.cfg` уже `-Xmx8g -Xms2g` — с запасом, менять не надо.
