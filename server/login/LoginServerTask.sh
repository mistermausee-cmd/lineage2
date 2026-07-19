#!/bin/bash

err=1
fails=0
until [ $err == 0 ];
do
	# Очистка старых архивных логов (старше 7 дней), чтобы они не копились тысячами.
	find log -maxdepth 1 -type f \( -name "*_java.log" -o -name "*_stdout.log" \) -mtime +7 -delete 2>/dev/null
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	start=`date +%s`
	java $(cat "java.cfg") -jar ../libs/LoginServer.jar > log/stdout.log 2>&1
	err=$?
	# Защита от runaway-цикла: если сервер упал быстрее чем за 30 секунд — считаем
	# это неудачным стартом. После 5 быстрых падений подряд останавливаемся, чтобы
	# не плодить тысячи лог-файлов. Смотрите причину в log/stdout.log
	if [ $(( `date +%s` - start )) -lt 30 ]; then
		fails=$((fails+1))
		if [ $fails -ge 5 ]; then
			echo "LoginServer 5 раз подряд упал при старте. Остановка цикла."
			echo "Причина — в последнем файле log/*_stdout.log (обычно нет связи с MySQL или занят порт)."
			break
		fi
		sleep 30
	else
		fails=0
		sleep 10
	fi
done
