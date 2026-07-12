@echo off
chcp 65001 > nul
title Grand Crusade - Login Server
:start
java -server -Dfile.encoding=UTF-8 -Dorg.slf4j.simpleLogger.log.com.zaxxer.hikari=warn -XX:+UseZGC -Xms128m -Xmx256m -jar ../libs/LoginServer.jar
if ERRORLEVEL 1 goto restart
goto end
:restart
echo.
echo Перезапуск логин-сервера...
echo.
goto start
:end
echo.
echo Логин-сервер остановлен.
echo.
pause
