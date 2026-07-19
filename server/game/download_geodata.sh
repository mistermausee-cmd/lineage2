#!/usr/bin/env bash
# =============================================================================
#  Загрузка геодаты для L2J Mobius Grand Crusade (протокол 110)
# -----------------------------------------------------------------------------
#  Источник : https://github.com/bartty/mobius-geo  (L2J Mobius geodata)
#  Формат   : *.l2j (256x256 блоков: flat/complex/multilayer) — 215 регионов.
#  Проверено: файлы парсятся штатным движком geoengine из GameServer.jar
#             (Region(ByteBuffer)) без ошибок, буфер читается до конца.
#
#  Геодата НЕ хранится в git (см. .gitignore), поэтому её нужно скачать один раз
#  на сервере этим скриптом. Общий объём ~650 МБ.
#
#  Запуск (из корня репозитория ИЛИ из любой папки):
#      bash server/game/download_geodata.sh
#
#  После успешной загрузки убедись, что в server/game/config/GeoEngine.ini
#  стоит  PathFinding = 2  (значение по умолчанию в репозитории), и перезапусти
#  игровой сервер.
# =============================================================================
set -euo pipefail

# --- пути -------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"   # .../server/game
DEST="${SCRIPT_DIR}/data/geodata"                            # куда кладём .l2j
REPO="https://github.com/bartty/mobius-geo.git"
RAW="https://raw.githubusercontent.com/bartty/mobius-geo/master/geodata"
EXPECTED=215                                                 # ожидаемое кол-во регионов

echo "==> Каталог назначения: ${DEST}"
mkdir -p "${DEST}"

have_git=0
command -v git >/dev/null 2>&1 && have_git=1

TMP="$(mktemp -d)"
cleanup() { rm -rf "${TMP}"; }
trap cleanup EXIT

download_via_git() {
    echo "==> Способ 1: git clone --depth 1 (быстро, одним архивом)"
    git clone --depth 1 --filter=blob:none --no-checkout "${REPO}" "${TMP}/repo" 2>/dev/null || \
        git clone --depth 1 "${REPO}" "${TMP}/repo"
    ( cd "${TMP}/repo" && git sparse-checkout set geodata 2>/dev/null || true; git checkout 2>/dev/null || true )
    if [ -d "${TMP}/repo/geodata" ]; then
        cp -f "${TMP}/repo/geodata/"*.l2j "${DEST}/"
        return 0
    fi
    return 1
}

download_via_curl() {
    echo "==> Способ 2: поштучная загрузка через raw.githubusercontent"
    command -v curl >/dev/null 2>&1 || { echo "ОШИБКА: нет ни git, ни curl."; exit 1; }
    # получаем список файлов через GitHub API
    local list
    list="$(curl -sL 'https://api.github.com/repos/bartty/mobius-geo/contents/geodata' \
            | grep -oE '"name": *"[0-9]+_[0-9]+\.l2j"' | grep -oE '[0-9]+_[0-9]+\.l2j')"
    [ -z "${list}" ] && { echo "ОШИБКА: не удалось получить список файлов."; exit 1; }
    local n=0
    for f in ${list}; do
        n=$((n+1))
        printf '\r    загрузка %s (%d)      ' "${f}" "${n}"
        curl -sL -o "${DEST}/${f}" "${RAW}/${f}"
    done
    echo ""
}

if [ "${have_git}" -eq 1 ]; then
    download_via_git || download_via_curl
else
    download_via_curl
fi

# --- проверка ---------------------------------------------------------------
COUNT="$(find "${DEST}" -maxdepth 1 -name '*.l2j' | wc -l | tr -d ' ')"
echo ""
echo "==> Скачано регионов: ${COUNT} (ожидалось ~${EXPECTED})"
if [ "${COUNT}" -lt "${EXPECTED}" ]; then
    echo "!!! ВНИМАНИЕ: файлов меньше ожидаемого. Проверь сеть и запусти скрипт ещё раз."
    exit 1
fi

echo ""
echo "Готово. Геодата установлена в: ${DEST}"
echo "Проверь server/game/config/GeoEngine.ini -> PathFinding = 2 и перезапусти сервер:"
echo "    bash stop_linux.sh && bash start_linux.sh"
