@echo off
chcp 65001 > nul
title Grand Crusade - Game Server
:start
java -server -Dfile.encoding=UTF-8 -Djava.util.logging.manager=org.l2jmobius.log.ServerLogManager -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn -XX:+UseZGC -Xmx8g -Xms2g -jar ../libs/GameServer.jar
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Перезапуск игрового сервера...
echo.
goto start
:error
echo.
echo Игровой сервер завершился с ошибкой!
echo.
:end
echo.
echo Игровой сервер остановлен.
echo.
pause
