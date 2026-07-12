@echo off
chcp 65001 > nul
title Lineage 2 Grand Crusade - Solo Server
echo ============================================================
echo   Lineage 2 Grand Crusade - SOLO (L2J Mobius)
echo ============================================================
echo.
echo Перед запуском убедитесь, что:
echo   1. Установлена Java 25 (JDK 25).
echo   2. Запущен MySQL / MariaDB.
echo   3. База данных импортирована (см. README.md).
echo.
echo Запускаю логин-сервер и игровой сервер в отдельных окнах...
echo.
start "Login Server" cmd /k "cd /d %~dp0server\login && LoginServer.bat"
timeout /t 8 > nul
start "Game Server" cmd /k "cd /d %~dp0server\game && GameServer.bat"
echo.
echo Готово! Открылись два окна (Login и Game).
echo Дождитесь строки о полной загрузке игрового сервера, затем заходите в игру.
echo Это окно можно закрыть.
echo.
pause
