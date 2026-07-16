# Implementation Plan: altb-price-balance

## Overview

Реализация детерминированного оффлайн-инструмента `Price_Generator` (Python 3) для балансового
прохода по ценам магазина Alt+B. Инструмент читает `Item_Catalog` (`data/stats/items/*.xml`) и
мультиселлы Department_Map (+`custom/`), применяет воспроизводимую `Price_Model`
(`Buy_Price = ceil(max(Base, AntiExploit))`) с учётом `Effective_Stat_Value`, апгрейд-цепей и
ручных оверрайдов, переписывает **только** `count` у `ingredient id="57"`, валидирует через
`xmllint` и выпускает `Price_Report`. Перекомпиляция jar НЕ требуется — только правка XML + рестарт.

Артефакты инструмента размещаются **вне игровых данных** — в отдельной папке
`tools/altb_price_generator/` (код, `price_model.yaml`, `manual_overrides.yaml`, тесты). Результат
записывается в игровые данные: `server/game/data/multisell/*.xml` и
`server/game/data/multisell/custom/*.xml`.

Порядок инкрементальный: загрузчики/модель → оценка силы и цепи → движок ценообразования →
writer/validator → отчёт → калибровка модели → прогон по всем отделам → финальная валидация и
проверка неизменности смежных файлов.

## Tasks

- [x] 1. Каркас инструмента и модель цены
  - [x] 1.1 Создать структуру инструмента и модуль конфигурации
    - Создать папку `tools/altb_price_generator/` (вне игровых данных) с пакетом `price_generator/`
      и подкаталогом `tests/`
    - Определить типы данных: `CatalogItem`, `MultisellPosition`, `Ingredient`, `PriceReportEntry`
      (по разделу Data Models дизайна)
    - Добавить `requirements.txt`/зависимости: `PyYAML`, `Hypothesis`, `pytest`; настроить запуск
      тестов через `pytest`
    - Зафиксировать константу пути к схеме `server/game/data/xsd/multisell.xsd`
    - _Requirements: 12.1_

  - [x] 1.2 Реализовать `PriceModel` и загрузчик `price_model.yaml`
    - Класс `PriceModel` с полями `farm_rate`, `grade_floor`, `role_weight`, `category_factor`,
      `manual_override`, `anti_exploit_mult=2.0`, `sell_price_basis="half"`, `variant="balanced"`,
      `variant_multiplier=1.0`, `enchant_target_level`
    - Загрузка из `price_model.yaml`; функция `npc_sell_price(price, basis)` (basis="half" → price/2)
    - Валидация: `grade_floor` неубывающий по D<C<B<A<S<S80<R<R95<R99; все `farm_rate` — целые > 0
    - _Requirements: 1.1, 1.3, 7.6, 8.5_

  - [x]* 1.3 Property-тест неубывания Grade_Floor
    - **Property 2: Неубывание Grade_Floor** — для соседних грейдов `grade_floor` младшего ≤ старшего
    - **Validates: Requirements 1.3**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 2: ...`

- [x] 2. Загрузчики каталога и мультиселлов
  - [x] 2.1 Реализовать `CatalogLoader`
    - Парсинг `server/game/data/stats/items/*.xml`; индекс `id → {grade(crystal_type), price, stats}`
    - Извлечение блока `<stats>` для последующей оценки `Effective_Stat_Value`
    - Обработка отсутствующего/нулевого `price` (для анти-эксплойт-фолбэка 7.4) и отсутствия статов
      (фолбэк 4.5)
    - _Requirements: 8.5, 12.3, 4.5, 7.4_

  - [x]* 2.2 Unit-тесты `CatalogLoader`
    - Резолв id в грейд/price/статы; отсутствие price; отсутствие статов
    - _Requirements: 8.5, 4.5, 7.4_

  - [x] 2.3 Реализовать `MultisellLoader`
    - Чтение мультиселлов Department_Map (+`custom/`) с сохранением порядка узлов
    - Модель позиции: `production_id/count`, упорядоченный список `ingredient`, индекс валюты id=57
    - Дословное сохранение блока `<npcs><npc>-1</npc></npcs>`
    - _Requirements: 10.1, 10.2, 11.1_

  - [x]* 2.4 Unit round-trip чтения мультиселла
    - Парс→сериализация→парс сохраняет набор/порядок production/ingredient и число `<item>`
    - _Requirements: 10.1, 10.3_

- [x] 3. Оценка фактической силы и апгрейд-цепи
  - [x] 3.1 Реализовать `ChainResolver`
    - Построение линий Upgrade_Chain: production звена N-1 = невалютный ingredient (id≠57) звена N
    - Топологическая сортировка; определение финальных звеньев (production не потребляется дальше);
      детект циклов/неразрешимости
    - _Requirements: 6.1, 6.4, 6.5_

  - [x]* 3.2 Unit-тесты `ChainResolver`
    - Корректная линия → порядок и endpoint; циклическая линия → сигнал ошибки (для отката 6.5)
    - _Requirements: 6.1, 6.5_

  - [x] 3.3 Реализовать `Effective_Stat_Value`
    - `S_base` — взвешенная свёртка базовых статов из `<stats>`
    - `S_upgrade` — прирост статов на endpoint цепи (через `ChainResolver`), 0 вне цепи
    - `S_enchant` — прирост статов при заточке до `enchant_target_level` относительно +0
    - Нормировка в грейде/категории → `Stat_Rank = 1.0 + k × normalized_score`; фолбэк при отсутствии
      статов с фиксацией для Price_Report
    - _Requirements: 3.6, 4.1, 5.6, 4.5_

- [x] 4. Движок ценообразования (`PriceEngine`)
  - [x] 4.1 Реализовать ядро формулы цены
    - `Base = Grade_Floor(grade) × Role_Weight × Category_Factor × Stat_Rank`, затем
      `× variant_multiplier`
    - Анти-эксплойт: `AntiExploit = anti_exploit_mult × npc_sell_price`; итог
      `Buy_Price = ceil(max(Base, AntiExploit))`, `≥ 1`
    - Все входы (grade, price, статы) — из `CatalogLoader`, без хардкода
    - _Requirements: 8.1, 7.1, 7.2, 7.3, 12.5, 8.5_

  - [x]* 4.2 Property-тест анти-эксплойта
    - **Property 9: Инвариант анти-эксплойта** — Buy_Price целое, строго > NPC_Sell_Price и
      ≥ anti_exploit_mult × NPC_Sell_Price
    - **Validates: Requirements 7.1, 7.2, 8.1**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 9: ...`

  - [x]* 4.3 Property-тест положительной целочисленной цены
    - **Property 16: Положительная целочисленная цена** — count у id=57 всегда целое ≥ 1, дробные
      округляются вверх
    - **Validates: Requirements 12.5**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 16: ...`

  - [x] 4.4 Реализовать применение `Manual_Override`
    - Override заменяет `Base`, но проходит `max` с анти-эксплойтом; нарушающий анти-эксплойт override
      не применяется (поднять > NPC_Sell_Price) и фиксируется; override на несуществующий id — как
      неиспользованный
    - _Requirements: 8.2, 8.3, 8.6, 8.7_

  - [x]* 4.5 Property-тест применения валидного Manual_Override
    - **Property 10: Применение валидного Manual_Override** — при непротиворечащем анти-эксплойту
      override итоговая Buy_Price равна значению override
    - **Validates: Requirements 8.3**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 10: ...`

  - [x] 4.6 Реализовать дифференциацию по грейду, роли и типу свитка
    - Монотонность цены по грейду для сопоставимой роли; строгий порядок ролей внутри стадии
      (расходник < ключевой < престиж)
    - Заточка (600100): обычн. < благ. < свящ./древн.; заточка оружия ≥ заточка брони того же типа/грейда
    - Дифференциация по всем типам экипировки; запрет плоской единой цены в категории с разными грейдами
    - _Requirements: 1.5, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 3.5, 5.1, 5.2, 5.3, 5.4, 5.5_

  - [x]* 4.7 Property-тест монотонности цены по грейду
    - **Property 1: Монотонность цены по грейду для сопоставимой роли**
    - **Validates: Requirements 1.5, 3.1, 3.2, 3.4, 3.5, 5.5**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 1: ...`

  - [x]* 4.8 Property-тест строгого порядка цен по роли внутри стадии
    - **Property 3: Строгий порядок цен по роли внутри стадии**
    - **Validates: Requirements 2.5, 2.6**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 3: ...`

  - [x]* 4.9 Property-тест монотонности цены бижи по Effective_Stat_Value
    - **Property 4: Монотонность цены бижи по Effective_Stat_Value**
    - **Validates: Requirements 4.1, 4.2, 4.4**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 4: ...`

  - [x]* 4.10 Property-тест упорядочения заточки по типу свитка/камня
    - **Property 6: Упорядочение заточки по типу свитка/камня**
    - **Validates: Requirements 5.1, 5.6**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 6: ...`

  - [x]* 4.11 Property-тест «заточка оружия не дешевле заточки брони»
    - **Property 7: Заточка оружия не дешевле заточки брони**
    - **Validates: Requirements 5.3**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 7: ...`

  - [x] 4.12 Реализовать ценообразование Upgrade_Chain
    - Цена звена N = цена звена N-1 × 1.4 (строго возрастает); финальные звенья → роль престижа
      (Target_Farm_Hours ≥ 15); прирост статов endpoint учтён через `Effective_Stat_Value`
    - Цикл/неразрешимость → откат линии к `Grade_Floor` грейда с сохранением анти-эксплойта и >0
    - Сохранять структуру ингредиентов: базовый предмет (count=1) + Adena (count=Buy_Price), один
      production (count=1); менять только count у id=57
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 3.6, 4.3_

  - [x]* 4.13 Property-тест прогрессивного удорожания Upgrade_Chain
    - **Property 5: Прогрессивное удорожание Upgrade_Chain**
    - **Validates: Requirements 3.6, 4.3, 6.1, 6.4**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 5: ...`

- [x] 5. Checkpoint — движок ценообразования
  - Ensure all tests pass, ask the user if questions arise.

- [x] 6. Запись мультиселлов и валидация схемы
  - [x] 6.1 Реализовать `MultisellWriter`
    - Изменять исключительно числовое значение `count` у `ingredient id="57"`; сохранять набор/порядок
      production-id и их count, набор/порядок прочих ingredient-id и их count, число `<item>`, блок `<npcs>`
    - Маршрутизация записи: для отделов {600008, 600011, 600025, 600026} писать только в
      `data/multisell/custom/`, верхний файл не трогать; отсутствие custom-файла → не писать в верхний,
      отметить непокрытым
    - Путь схемы по расположению: `../../xsd/multisell.xsd` для `custom/`, `../xsd/multisell.xsd` для корня
    - Adena (id=57) — единственная валюта; допускается один невалютный ingredient (базовый предмет цепи, count=1)
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 10.1, 10.2, 10.4, 10.5, 10.6, 13.1, 13.5, 13.6_

  - [x]* 6.2 Property-тест сохранения структуры (round-trip/идемпотентность)
    - **Property 8: Сохранение ассортимента и структуры**
    - **Validates: Requirements 6.2, 6.3, 10.1, 10.3, 10.4, 13.1, 13.6**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 8: ...`

  - [x]* 6.3 Property-тест «запись перекрываемых отделов только в custom/»
    - **Property 12: Запись перекрываемых отделов только в custom/**
    - **Validates: Requirements 9.1, 9.2**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 12: ...`

  - [x]* 6.4 Property-тест корректности пути к схеме
    - **Property 13: Корректность пути к схеме по расположению файла**
    - **Validates: Requirements 9.3**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 13: ...`

  - [x]* 6.5 Property-тест единственности валюты
    - **Property 14: Единственность валюты**
    - **Validates: Requirements 10.5**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 14: ...`

  - [x] 6.6 Реализовать `SchemaValidator` и транзакционную запись
    - `xmllint --noout --schema multisell.xsd` во временный файл ПЕРЕД записью; атомарная замена только
      при коде 0, иначе исходник цел и ошибка в отчёт
    - Резолв каждого production/ingredient id в существующее определение `Item_Catalog`; нерезолвимый id
      → не применять вывод, зафиксировать
    - Изоляция ошибок: сбой одного файла не прерывает обработку остальных
    - _Requirements: 12.2, 12.3, 12.4, 12.6, 13.2, 13.3, 13.4_

- [x] 7. Отчёт покрытия (`ReportBuilder`)
  - [x] 7.1 Реализовать `ReportBuilder`
    - По каждой категории: total/covered/uncovered (≥0), min/max Buy_Price (>0), применённые
      Manual_Override с production_id, нарушения анти-эксплойта, неиспользованные override, нерезолвимые id,
      непрочитанные файлы
    - Сводка покрытия; прогон успешен только при `uncovered == 0`; непокрытая роль/позиция фиксируется
      без цены по умолчанию
    - _Requirements: 2.7, 11.2, 11.3, 11.4, 11.5, 11.6, 5.5, 7.5_

  - [x]* 7.2 Property-тест полноты покрытия и резолвимости id
    - **Property 15: Полнота покрытия и резолвимость id** — каждой позиции назначена цена либо отметка
      непокрытой (total = covered + uncovered), каждый id резолвится в каталог
    - **Validates: Requirements 11.1, 11.5, 12.3**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 15: ...`

- [x] 8. Калибровка ценовой модели
  - [x] 8.1 Заполнить `price_model.yaml` под вариант «Сбалансированная»
    - `Farm_Rate` по стадиям (B 100k … R99 35M, эпик 40M), `Grade_Floor` (D 30k … R99 35M),
      `Role_Weight`, `Category_Factor` (заточка ×1.0/×3.0/×5.0, оружие-заточка ×1.3, цепь ×1.4),
      `Target_Farm_Hours` по категориям/ролям
    - `anti_exploit_mult=2.0`, `sell_price_basis="half"`, `variant="balanced"`, `variant_multiplier=1.0`,
      `enchant_target_level`
    - _Requirements: 1.1, 1.2, 2.1, 2.2, 2.3, 2.4, 7.6, 8.1_

  - [x] 8.2 Заполнить `manual_overrides.yaml` для опорных эпиков
    - Опорные точки Buy_Price для эпик-бижи (QA/Orfen/Core ~1.2B, Zaken ~2.0B, Baium/Antharas/Frintezza
      ~3.5B, Valakas ~5.0B) как нижние опоры; упорядочение между бижей определяется Effective_Stat_Value
    - Каждое значение — целое > 0
    - _Requirements: 8.2, 4.4_

- [x] 9. Интеграция и прогон по всем отделам
  - [x] 9.1 Реализовать оркестратор (main) и прогон Department_Map
    - Связать `CatalogLoader` + `MultisellLoader` + `PriceModel` + `PriceEngine` + `ChainResolver` +
      `MultisellWriter` + `SchemaValidator` + `ReportBuilder`
    - Прогон по всем мультиселлам Department_Map (+custom/), выпуск `Price_Report`
    - _Requirements: 11.1, 12.1, 12.6_

  - [x]* 9.2 Property-тест детерминизма на уровне позиции
    - **Property 11: Детерминизм на уровне позиции** — при неизменных входах два прогона дают идентичный
      count у id=57 каждой позиции
    - **Validates: Requirements 8.4**
    - Hypothesis, ≥100 итераций, тег `Feature: altb-price-balance, Property 11: ...`

  - [x]* 9.3 Интеграционный round-trip на реальных файлах
    - Парс→запись→парс на копии реальных 600060/600030/600100/600052 и custom/600025: структура,
      ассортимент, путь схемы сохранены; изменился только count у id=57
    - _Requirements: 10.1, 10.3, 9.3, 12.2_

  - [x]* 9.4 Интеграционный тест примера анти-эксплойта
    - R99-оружие с реальной `price` 808M (basis=half → пол 808M): Buy_Price строго > NPC_Sell_Price и ≥ пола
    - _Requirements: 7.1, 7.2_

  - [x]* 9.5 Интеграционный тест неизменности смежных файлов
    - Хэш до/после: `server/game/config/Rates.ini`, `server/game/data/stats/items/`, HTML-меню магазина —
      не изменены
    - _Requirements: 13.2, 13.3, 13.4_

  - [x]* 9.6 Финальная валидация всех записанных файлов через xmllint
    - `xmllint --noout --schema multisell.xsd` возвращает 0 по всем записанным мультиселлам
    - _Requirements: 12.2_

- [x] 10. Финальный checkpoint — приёмка
  - Ensure all tests pass, ask the user if questions arise.
  - Прогон даёт `Price_Report` с `uncovered == 0`, нулём нарушений анти-эксплойта и структуры; все
    Manual_Override применены либо зафиксированы как неиспользованные.
  - ПРИЁМКА ПРОЙДЕНА: 41/41 теста зелёные; полный прогон `--apply` записал 48 файлов
    (44 корневых 6000XX + 4 custom), 2179 позиций, uncovered=0, 0 нарушений анти-эксплойта/структуры,
    0 нерезолвимых id, 8/8 эпик-override применены; xmllint OK по всем 48 файлам; Rates.ini/stats-items/HTML
    не изменены (проверено хэшами); git-дифф затрагивает только count у ingredient id=57 (4354 строки).

## Notes

- Инструмент оффлайн, XML-only: без изменения движка/jar, только правка XML + рестарт.
- Задачи, помеченные `*`, — тесты (property/unit/integration), опциональны и могут быть пропущены для
  быстрого MVP; каждое свойство P1–P16 реализуется одним property-тестом на Hypothesis (≥100 итераций)
  с тег-комментарием `Feature: altb-price-balance, Property {N}: {текст}`.
- Каждая задача ссылается на конкретные Requirements/Properties для трассируемости.
- Артефакты (`price_model.yaml`, `manual_overrides.yaml`, код, тесты) — в `tools/altb_price_generator/`
  вне игровых данных; запись цен — только в `data/multisell/*.xml` и `data/multisell/custom/*.xml`.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2", "2.1", "2.3"] },
    { "id": 2, "tasks": ["1.3", "2.2", "2.4", "3.1"] },
    { "id": 3, "tasks": ["3.2", "3.3"] },
    { "id": 4, "tasks": ["4.1"] },
    { "id": 5, "tasks": ["4.2", "4.3", "4.4"] },
    { "id": 6, "tasks": ["4.5", "4.6"] },
    { "id": 7, "tasks": ["4.7", "4.8", "4.9", "4.10", "4.11", "4.12"] },
    { "id": 8, "tasks": ["4.13", "6.1"] },
    { "id": 9, "tasks": ["6.2", "6.3", "6.4", "6.5", "6.6"] },
    { "id": 10, "tasks": ["7.1"] },
    { "id": 11, "tasks": ["7.2", "8.1", "8.2"] },
    { "id": 12, "tasks": ["9.1"] },
    { "id": 13, "tasks": ["9.2", "9.3", "9.4", "9.5", "9.6"] }
  ]
}
```
