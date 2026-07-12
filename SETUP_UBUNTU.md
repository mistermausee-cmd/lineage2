# Запуск сервера на Ubuntu 24.04 (+ Navicat)

Инструкция для VPS (например Azure). Сервер ставится через два скрипта, БД —
MySQL, редактируется удобно через **Navicat** (рекомендую по SSH-туннелю — безопасно).

## 0. Подготовка
На VPS (Ubuntu 24.04) склонируй репозиторий и зайди в него:
```bash
sudo apt-get update && sudo apt-get install -y git
git clone https://github.com/mistermausee-cmd/lineage2.git
cd lineage2
```

## 1. Установка (JDK 25 + MySQL + БД + пользователь)
```bash
sudo bash setup/01_install_ubuntu.sh 'ТвойПарольБД'
```
Скрипт:
- ставит **JDK 25** (Adoptium Temurin) и **MySQL**,
- создаёт базу **`l2jmobius`** и пользователя **`l2j`** (с твоим паролем),
- прописывает этот логин/пароль в конфиги сервера (`game` и `login`).

> Если пароль не указать — будет `changeme_l2j` (лучше задать свой).

## 2. Импорт структуры БД
```bash
bash setup/02_import_db.sh 'ТвойПарольБД'
```
Импортирует 4 login-таблицы + 97 game-таблиц. В конце покажет число таблиц (~101).

## 3. Настройка внешнего IP (чтобы заходить со своего ПК)
```bash
cd server/game/config
cp default-ipconfig.xml ipconfig.xml
nano ipconfig.xml   # в поле address укажи ПУБЛИЧНЫЙ IP VPS
```

## 4. Открыть порты
**На Ubuntu (ufw):**
```bash
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 2106/tcp    # LoginServer
sudo ufw allow 7777/tcp    # GameServer
sudo ufw enable
```
**В Azure (Network Security Group)** добавь входящие правила Inbound на порты
**2106** и **7777** (TCP). Порт **3306 (MySQL) НЕ открывай** — для Navicat используем SSH-туннель (см. §6).

## 5. Запуск сервера
```bash
cd ~/lineage2
bash start_linux.sh
```
Стартуют логин- и игровой серверы. Логи: `server/login/log/` и `server/game/log/`.
Остановить: `pkill -f LoginServer.jar ; pkill -f GameServer.jar`.

Держать сервер запущенным после выхода из SSH — через `screen` или `tmux`:
```bash
sudo apt-get install -y screen
screen -S l2       # затем bash start_linux.sh ; отсоединиться Ctrl+A, D
```

## 6. Navicat — редактирование БД (безопасно, через SSH-туннель)
В Navicat создай новое подключение **MySQL** и заполни:

**Вкладка «SSH»** (галочка *Use SSH Tunnel*):
- Host: `ПУБЛИЧНЫЙ_IP_VPS`
- Port: `22`
- User Name: `azureuser` (или твой SSH-пользователь)
- Auth: пароль или приватный ключ (как заходишь по SSH)

**Вкладка «General»:**
- Host: `127.0.0.1`  (localhost со стороны сервера)
- Port: `3306`
- User Name: `l2j`
- Password: `ТвойПарольБД`
- Database: `l2jmobius`

Готово — Navicat подключится к БД через SSH, **не открывая порт 3306 наружу**.

<details>
<summary>Вариант без SSH-туннеля (прямой доступ, менее безопасно)</summary>

```bash
sudo sed -i 's/^bind-address.*/bind-address = 0.0.0.0/' /etc/mysql/mysql.conf.d/mysqld.cnf
sudo systemctl restart mysql
sudo ufw allow from ТВОЙ_ДОМАШНИЙ_IP to any port 3306   # ограничь своим IP!
```
В Azure NSG тоже открой 3306 **только со своего IP**. Пользователь `l2j@'%'` уже создан.
В Navicat: Host = публичный IP VPS, порт 3306, user `l2j`.
</details>

## 7. Создать аккаунт и GM
- Аккаунт создаётся автоматически при первом входе в игру.
- Сделать себя админом (после создания персонажа):
```sql
UPDATE characters SET accesslevel = 8 WHERE char_name = 'ИМЯ';
```
(можно прямо в Navicat). В игре — команды `//`.

## 8. Клиент (со своего ПК)
Клиент подключается к **публичному IP VPS** (порт 2106). Русский клиент из ветки `system`
(`system new`) — уже на русском. Если `l2.exe` стучится не на твой IP — нужно указать IP
сервера (через `hosts` или IP-changer пака). Напиши — помогу с конкретным клиентом.

---
## 9. Просмотр логов и мониторинг
```bash
tail -f server/game/log/stdout.log     # лог игрового сервера в реальном времени
tail -f server/login/log/stdout.log    # лог логин-сервера
```
Сервер полностью загружен, когда в игровом логе появится строка вида
`GameServer Started, free memory ...` / `Maximum numbers of connected players`.

**Память:** по умолчанию игровому серверу выделено `-Xmx2g` (файл `server/game/java.cfg`).
Для соло этого хватает с запасом. Проверь ОЗУ: `free -h`. Если VPS ≤ 2 ГБ — поставь `-Xmx1500m`.
Если 8+ ГБ и хочешь — можно поднять до `-Xmx4g`.

### Частые проблемы
- **Сервер не стартует / ошибка БД** — проверь пароль в `server/game/config/Database.ini` и `server/login/config/Database.ini` (должен совпадать с §1).
- **Клиент не коннектится** — проверь `ipconfig.xml` (публичный IP), порты 2106/7777 в ufw и Azure NSG.
- **JDK не 25** — `java -version` должно показывать 25; иначе `sudo apt-get install temurin-25-jdk`.
