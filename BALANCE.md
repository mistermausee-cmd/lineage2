# Баланс соло-сервера (Grand Crusade)

Документ описывает **все внесённые изменения** относительно чистого L2J Mobius Grand Crusade,
их смысл и **как их менять**. Все правки — на уровне конфигов/данных (без перекомпиляции),
если не указано иное.

---

## 1. Рейты — `server/game/config/Rates.ini`

| Параметр | Значение | Ретейл | Смысл |
|---|---|---|---|
| `RateXp` | **5** | 1 | Опыт (компенсация отсутствия пати-бонуса до x2 и саппортов) |
| `RateSp` | **5** | 1 | SP |
| `RatePartyXp` / `RatePartySp` | **5** | 1 | То же для группы (на случай пета/саммона) |
| `DropAmountMultiplierByItemId = 57,3` | **x3 адена** | 57,1 | Адена (предмет id 57) |
| `DeathDropChanceMultiplier` | **3** | 1 | Шанс дропа вещей с мобов |
| `SpoilDropChanceMultiplier` | **4** | 1 | Шанс спойла |
| `RaidDropChanceMultiplier` | **3** | 1 | Шанс дропа с рейд-боссов |
| `RateQuestRewardAdena` | **3** | 1 | Адена за квесты |
| `RateRaidbossPointsReward` | **3** | 1 | Очки рейд-боссов |

> Опыт/SP за квесты (`RateQuestRewardXP/SP`) намеренно оставлены **=1**, чтобы прокачка
> по квестам ощущалась «как в оригинале», а ускорение шло за счёт фарма мобов.

**Как менять:** открой `Rates.ini`, поменяй число, перезапусти сервер.

---

## 2. Сложность рейд-боссов — `server/game/config/Custom/NpcStatMultipliers.ini`

```
EnableNpcStatMultipliers = True
RaidbossHP   = 0.35   # 35% от ретейльного HP
RaidbossPAtk = 0.55   # 55% физ. урона
RaidbossMAtk = 0.55   # 55% маг. урона
RaidbossPDef = 0.6
RaidbossMDef = 0.6
```
Обычные мобы, стражи и защитники **не тронуты** (1.0) — мир сохраняет сложность.

> ⚠️ Эти множители действуют на обычных рейд-боссов (тип RaidBoss).
> **Эпик-боссы (Grand Boss)** — Antharas, Valakas, Helios и т.д. — сюда не входят,
> их статы задаются отдельно (планируется в фазе 2).

**Как менять:** увеличь значения к 1.0 — боссы станут сложнее; уменьши — легче.

---

## 3. Уровни и подклассы — `server/game/config/Player.ini`

- `MaxSubclassLevel = 110` (было 80) — подклассы качаются до 110, как основной класс.
- Основной класс уже качается до **110** (таблица `data/stats/players/experience.xml`, `maxLevel="110"`), капа «99» в GC нет.

---

## 4. Соло-доступ в инстансы — `server/game/data/instances/*.xml`

Во **всех 97** подземельях условие `GroupMin` (минимум людей в группе) выставлено в **1**.
Теперь любой инстанс можно открыть и пройти в одиночку.

> Ещё не сделано (фаза 2): сокращение кулдаунов повторного входа (блоки `<reenter>`)
> и снятие требований **командного канала** у эпик-рейдов.

**Как менять:** в нужном `*.xml` найди `type="GroupMin"` и поменяй `limit`.

---

## 5. Кастом-меню Community Board (Alt+B)

- `Custom/CommunityBoard.ini`: `CustomCommunityBoard = True`, включены баффы, телепорты, хил, мультиселлы; включена покупка премиума (`CommunityPremiumSystem = True`).
- Русифицированы: `data/html/CommunityBoard/Custom/home.html` и `navigation.html`
  (разделы: Главная, Баффер, Магазин, Телепорт, Поиск дропа, Понижение уровня, Премиум).

## 6. Баффер — `server/game/config/Custom/SchemeBuffer.ini`
- `BufferMaxSchemesPerChar = 8` (было 4) — до 8 сохранённых схем баффов.
- Список/цены баффов: `data/SchemeBufferSkills.xml`.

## 7. Премиум-система — `server/game/config/Custom/PremiumSystem.ini`
- `EnablePremiumSystem = True`. Премиум удваивает XP/SP/дроп (`PremiumRateXp = 2` и т.д.).
- Покупка премиума за адену через Alt+B (цена: `CommunityPremiumPricePerDay` в `CommunityBoard.ini`).

---

## Куда смотреть дальше (для будущих правок)
- Магазины/ассортимент: `data/multisells/` + HTML в `data/html/CommunityBoard/Custom/merchant/`.
- Заточка: `server/game/config/` (файлы, отвечающие за enchant) — планируется настройка в фазе 2.
- Список баффов новичка и NPC-диалоги: `data/html/` и скрипты в `data/scripts/`.
