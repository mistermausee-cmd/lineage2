#!/bin/bash

# exit codes of GameServer:
#  0 normal shutdown
#  2 reboot attempt

while :; do
	# Очистка старых архивных логов (старше 7 дней), чтобы они не копились тысячами.
	find log -maxdepth 1 -type f \( -name "*_java.log" -o -name "*_stdout.log" \) -mtime +7 -delete 2>/dev/null
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	java $(cat "java.cfg") -jar ../libs/GameServer.jar > log/stdout.log 2>&1
	[ $? -ne 2 ] && break
#	/etc/init.d/mysql restart
	sleep 10
done
