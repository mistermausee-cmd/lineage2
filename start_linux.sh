#!/bin/bash
# ============================================================
#   Lineage 2 Grand Crusade - SOLO (L2J Mobius)
#   Запуск логин-сервера и игрового сервера (Linux)
# ============================================================
# Требования: Java 25 (JDK 25), запущенный MySQL/MariaDB, импортированная база.

cd "$(dirname "$0")/server" || exit 1

echo "Запуск логин-сервера..."
( cd login && chmod +x *.sh 2>/dev/null; ./LoginServer.sh )

echo "Ожидание инициализации логин-сервера (8 сек)..."
sleep 8

echo "Запуск игрового сервера..."
( cd game && chmod +x *.sh 2>/dev/null; ./GameServer.sh )

echo ""
echo "Оба сервера запущены в фоне."
echo "Логи логин-сервера: server/login/log/"
echo "Логи игрового сервера: server/game/log/"
echo "Остановить: pkill -f LoginServer.jar ; pkill -f GameServer.jar"
