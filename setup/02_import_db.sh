#!/usr/bin/env bash
# ============================================================================
#  Импорт структуры БД (login + game) в l2jmobius — без GUI-установщика.
#  Запуск:  bash setup/02_import_db.sh [ПАРОЛЬ_БД]
# ============================================================================
set -euo pipefail

DB_NAME="l2jmobius"
DB_USER="l2j"
DB_PASS="${1:-changeme_l2j}"
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SQLDIR="${REPO_ROOT}/server/db_installer/sql"

echo ">>> Импорт login-таблиц"
for f in "${SQLDIR}"/login/*.sql; do
  echo "    $(basename "$f")"
  mysql -u"${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" < "$f"
done

echo ">>> Импорт game-таблиц (97 файлов)"
for f in "${SQLDIR}"/game/*.sql; do
  mysql -u"${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" < "$f"
done

CNT=$(mysql -u"${DB_USER}" -p"${DB_PASS}" -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${DB_NAME}';")
echo ""
echo "============================================================"
echo " Импорт завершён. Таблиц в ${DB_NAME}: ${CNT}"
echo " Запуск сервера:  bash start_linux.sh"
echo "============================================================"
