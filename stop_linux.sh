#!/bin/bash
# ============================================================
#   Lineage 2 - остановка логин- и игрового серверов
# ============================================================
# ВАЖНО: сначала гасим скрипты-циклы (*Task.sh), которые перезапускают сервер,
# и только потом сами java-процессы. Если убить только .jar — цикл поднимет
# сервер заново (именно это раньше вызывало бесконечный рестарт).

echo "1) Останавливаю циклы перезапуска (LoginServerTask/GameServerTask)..."
pkill -f LoginServerTask.sh 2>/dev/null
pkill -f GameServerTask.sh 2>/dev/null
sleep 1

echo "2) Останавливаю сами серверы (java)..."
pkill -f LoginServer.jar 2>/dev/null
pkill -f GameServer.jar 2>/dev/null
sleep 3

# Если что-то ещё живо — добиваем принудительно.
if ps aux | grep -E "ServerTask.sh|Server\.jar" | grep -v grep | grep -q .; then
	echo "3) Остались процессы, завершаю принудительно (-9)..."
	pkill -9 -f LoginServerTask.sh 2>/dev/null
	pkill -9 -f GameServerTask.sh 2>/dev/null
	pkill -9 -f LoginServer.jar 2>/dev/null
	pkill -9 -f GameServer.jar 2>/dev/null
	sleep 1
fi

echo ""
echo "Проверка (список ниже должен быть ПУСТЫМ):"
ps aux | grep -E "ServerTask.sh|Server\.jar" | grep -v grep
echo "Готово."
