#!/usr/bin/env bash
# ============================================================================
#  Lineage 2 Grand Crusade (L2J Mobius) — установка на Ubuntu 24.04
#  Ставит JDK 25 + MySQL, создаёт БД l2jmobius и пользователя, правит конфиги.
#  Запуск:  sudo bash setup/01_install_ubuntu.sh [ПАРОЛЬ_БД]
# ============================================================================
set -euo pipefail

DB_NAME="l2jmobius"
DB_USER="l2j"
DB_PASS="${1:-changeme_l2j}"          # пароль БД: аргумент №1 или дефолт
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

if [[ $EUID -ne 0 ]]; then echo "Запусти через sudo: sudo bash $0 [пароль]"; exit 1; fi

echo ">>> [1/5] Обновление пакетов"
apt-get update -y
apt-get install -y wget gnupg apt-transport-https ca-certificates unzip

echo ">>> [2/5] Установка JDK 25 (Adoptium Temurin)"
if ! java -version 2>&1 | grep -q '"25'; then
  install -d -m 0755 /etc/apt/keyrings
  wget -qO- https://packages.adoptium.net/artifactory/api/gpg/key/public \
    | gpg --dearmor -o /etc/apt/keyrings/adoptium.gpg
  CODENAME="$(. /etc/os-release; echo "${VERSION_CODENAME}")"
  echo "deb [signed-by=/etc/apt/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb ${CODENAME} main" \
    > /etc/apt/sources.list.d/adoptium.list
  apt-get update -y
  apt-get install -y temurin-25-jdk || apt-get install -y temurin-25-jre
fi
java -version

echo ">>> [3/5] Установка MySQL Server"
apt-get install -y mysql-server
systemctl enable --now mysql

echo ">>> [4/5] Создание БД и пользователя"
mysql <<SQL
CREATE DATABASE IF NOT EXISTS ${DB_NAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
CREATE USER IF NOT EXISTS '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASS}';
CREATE USER IF NOT EXISTS '${DB_USER}'@'%'        IDENTIFIED BY '${DB_PASS}';
ALTER USER '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASS}';
ALTER USER '${DB_USER}'@'%'        IDENTIFIED BY '${DB_PASS}';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'localhost';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USER}'@'%';
FLUSH PRIVILEGES;
SQL

echo ">>> [5/5] Прописываю пользователя/пароль в конфиги сервера"
for cfg in "${REPO_ROOT}/server/game/config/Database.ini" "${REPO_ROOT}/server/login/config/Database.ini"; do
  sed -i "s/^Login = .*/Login = ${DB_USER}/"      "$cfg"
  sed -i "s/^Password = .*/Password = ${DB_PASS}/" "$cfg"
  echo "    обновлён: $cfg"
done

echo ""
echo "============================================================"
echo " Готово. БД: ${DB_NAME}  Пользователь: ${DB_USER}  Пароль: ${DB_PASS}"
echo " Дальше:  bash setup/02_import_db.sh ${DB_PASS}"
echo "============================================================"
