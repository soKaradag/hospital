#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="${LOG_DIR:-/tmp/hospital-phase4}"
INVENTORY_PORT="${INVENTORY_PORT:-8081}"
CORE_PORT="${CORE_PORT:-8080}"
INVENTORY_LOG="${LOG_DIR}/inventory-service.log"
CORE_LOG="${LOG_DIR}/hospital-core.log"
INVENTORY_PID_FILE="${LOG_DIR}/inventory-service.pid"
CORE_PID_FILE="${LOG_DIR}/hospital-core.pid"

mkdir -p "${LOG_DIR}"

ensure_database() {
	local db_name="$1"
	local mysql_host="${MYSQL_HOST:-127.0.0.1}"
	local mysql_port="${MYSQL_PORT:-3306}"
	local mysql_user="${MYSQL_USER:-root}"
	local mysql_password="${MYSQL_PASSWORD:-}"

	local mysql_args=(-h"${mysql_host}" -P"${mysql_port}" -u"${mysql_user}")
	if [[ -n "${mysql_password}" ]]; then
		mysql_args+=(-p"${mysql_password}")
	fi

	mysql "${mysql_args[@]}" -e \
		"CREATE DATABASE IF NOT EXISTS \`${db_name}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
}

wait_for_http() {
	local url="$1"
	local service_name="$2"
	local attempts="${3:-60}"

	for ((i = 1; i <= attempts; i++)); do
		local status
		status="$(curl -sS -o /dev/null -w "%{http_code}" "${url}" || true)"
		if [[ "${status}" != "000" && -n "${status}" ]]; then
			echo "${service_name} is reachable at ${url} (HTTP ${status})"
			return 0
		fi
		sleep 2
	done

	echo "${service_name} did not become reachable: ${url}" >&2
	return 1
}

start_service() {
	local pid_file="$1"
	local log_file="$2"
	shift 2

	if [[ -f "${pid_file}" ]] && kill -0 "$(cat "${pid_file}")" >/dev/null 2>&1; then
		echo "Service already running with PID $(cat "${pid_file}") from ${pid_file}" >&2
		return 1
	fi

	(
		cd "${ROOT_DIR}"
		nohup "$@" >"${log_file}" 2>&1 &
		echo $! > "${pid_file}"
	)
}

ensure_database "${CORE_DB_NAME:-hospital}"
ensure_database "${INVENTORY_DB_NAME:-hospital_inventory}"

start_service \
	"${INVENTORY_PID_FILE}" \
	"${INVENTORY_LOG}" \
	./mvnw -q -f inventory-service/pom.xml -DskipTests spring-boot:run
wait_for_http "http://127.0.0.1:${INVENTORY_PORT}/api/inventory/system/health" "inventory-service"

start_service \
	"${CORE_PID_FILE}" \
	"${CORE_LOG}" \
	./mvnw -q -DskipTests spring-boot:run
wait_for_http "http://127.0.0.1:${CORE_PORT}/api/auth/login" "hospital-core"

echo "Phase 4 local runtime is up."
echo "Inventory log: ${INVENTORY_LOG}"
echo "Core log: ${CORE_LOG}"
