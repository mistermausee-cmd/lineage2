#!/usr/bin/env bash
# =============================================================================
#  Загрузка геодаты для Lineage 2 Grand Crusade (L2J Mobius, протокол 110)
# -----------------------------------------------------------------------------
#  Источник : геодата Grand Crusade от L2Script, хранится в ветке `system`
#             этого же репозитория, папка geodata/  (220 регионов, ~820 МБ).
#  Формат   : *.l2j (256x256 блоков: flat/complex/multilayer).
#  Проверено: все 220 файлов парсятся штатным движком geoengine из
#             GameServer.jar (Region(ByteBuffer)) без ошибок, буфер читается
#             до конца.
#
#  Почему L2Script, а не сторонняя bartty/mobius-geo (215 регионов):
#    * геодата именно под Grand Crusade (совпадает с хрониками сервера);
#    * полное покрытие: 5 регионов (12_22, 26_17, 27_15, 27_16, 27_17), которых
#      у bartty нет вовсе — там иначе игроки/мобы проваливались бы сквозь мир;
#    * лежит в нашем же репозитории (ветка system) — стабильный источник.
#
#  Геодата НЕ хранится в ветке main (см. .gitignore), поэтому её нужно скачать
#  один раз на сервере этим скриптом.
#
#  Запуск (из корня репозитория ИЛИ из любой папки):
#      bash server/game/download_geodata.sh
#
#  После успешной загрузки убедись, что в server/game/config/GeoEngine.ini
#  стоит  PathFinding = 2  (значение по умолчанию в репозитории), и перезапусти
#  игровой сервер.
# =============================================================================
set -euo pipefail

# --- пути / источник --------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"   # .../server/game
DEST="${SCRIPT_DIR}/data/geodata"                            # куда кладём .l2j
BRANCH="system"                                              # ветка с геодатой
SUBDIR="geodata"                                             # папка внутри ветки
FALLBACK_REPO="https://github.com/mistermausee-cmd/lineage2.git"
EXPECTED=220                                                 # ожидаемое кол-во регионов

# URL origin текущего репозитория (чтобы работало и на форках/зеркалах).
# Используем origin ТОЛЬКО если это github.com, иначе — прямой GitHub-URL
# (на некоторых окружениях origin проксируется через шлюз и недоступен напрямую).
ORIGIN_URL="$(git -C "${SCRIPT_DIR}" config --get remote.origin.url 2>/dev/null || true)"
case "${ORIGIN_URL}" in
    *github.com*) REPO_URL="${ORIGIN_URL}" ;;
    *)            REPO_URL="${FALLBACK_REPO}" ;;
esac

# raw-база: превращаем git@github.com:owner/repo.git или https в raw.githubusercontent
raw_base() {
    local u="${REPO_URL}"
    u="${u%.git}"
    u="${u/git@github.com:/https://github.com/}"
    # https://github.com/OWNER/REPO -> https://raw.githubusercontent.com/OWNER/REPO/BRANCH/SUBDIR
    local path="${u#https://github.com/}"
    echo "https://raw.githubusercontent.com/${path}/${BRANCH}/${SUBDIR}"
}
API_CONTENTS() {
    local u="${REPO_URL}"; u="${u%.git}"; u="${u/git@github.com:/https://github.com/}"
    local path="${u#https://github.com/}"
    echo "https://api.github.com/repos/${path}/contents/${SUBDIR}?ref=${BRANCH}"
}

echo "==> Репозиторий : ${REPO_URL}"
echo "==> Ветка/папка : ${BRANCH}/${SUBDIR}"
echo "==> Назначение  : ${DEST}"
mkdir -p "${DEST}"

have_git=0
command -v git >/dev/null 2>&1 && have_git=1

TMP="$(mktemp -d)"
cleanup() { rm -rf "${TMP}"; }
trap cleanup EXIT

download_via_git() {
    echo "==> Способ 1: git clone --depth 1 ветки '${BRANCH}' (sparse: только ${SUBDIR}/)"
    git clone --depth 1 --branch "${BRANCH}" --filter=blob:none --no-checkout \
        "${REPO_URL}" "${TMP}/repo" 2>/dev/null || \
        git clone --depth 1 --branch "${BRANCH}" "${REPO_URL}" "${TMP}/repo"
    ( cd "${TMP}/repo" && git sparse-checkout set "${SUBDIR}" 2>/dev/null || true; git checkout 2>/dev/null || true )
    if [ -d "${TMP}/repo/${SUBDIR}" ]; then
        cp -f "${TMP}/repo/${SUBDIR}/"*.l2j "${DEST}/"
        return 0
    fi
    return 1
}

download_via_curl() {
    echo "==> Способ 2: поштучная загрузка через raw.githubusercontent"
    command -v curl >/dev/null 2>&1 || { echo "ОШИБКА: нет ни git, ни curl."; exit 1; }
    local base list
    base="$(raw_base)"
    list="$(curl -sL "$(API_CONTENTS)" \
            | grep -oE '"name": *"[0-9]+_[0-9]+\.l2j"' | grep -oE '[0-9]+_[0-9]+\.l2j')"
    [ -z "${list}" ] && { echo "ОШИБКА: не удалось получить список файлов."; exit 1; }
    local n=0
    for f in ${list}; do
        n=$((n+1))
        printf '\r    загрузка %s (%d)      ' "${f}" "${n}"
        curl -sL -o "${DEST}/${f}" "${base}/${f}"
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
    echo "!!! ВНИМАНИЕ: файлов меньше ожидаемого. Проверь сеть/ветку и запусти скрипт ещё раз."
    exit 1
fi

echo ""
echo "Готово. Геодата установлена в: ${DEST}"
echo "Проверь server/game/config/GeoEngine.ini -> PathFinding = 2 и перезапусти сервер:"
echo "    bash stop_linux.sh && bash start_linux.sh"
