#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
CORE_DB_NAME="${CORE_DB_NAME:-hospital}"
INVENTORY_DB_NAME="${INVENTORY_DB_NAME:-hospital_inventory}"

MYSQL_ARGS=(-h"${MYSQL_HOST}" -P"${MYSQL_PORT}" -u"${MYSQL_USER}")
if [[ -n "${MYSQL_PASSWORD}" ]]; then
	MYSQL_ARGS+=(-p"${MYSQL_PASSWORD}")
fi

echo "Resetting MySQL databases '${CORE_DB_NAME}' and '${INVENTORY_DB_NAME}'..."
mysql "${MYSQL_ARGS[@]}" -e "
DROP DATABASE IF EXISTS \`${CORE_DB_NAME}\`;
DROP DATABASE IF EXISTS \`${INVENTORY_DB_NAME}\`;
CREATE DATABASE \`${CORE_DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE \`${INVENTORY_DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
"

echo "Database reset complete."
echo "Core DB: ${CORE_DB_NAME}"
echo "Inventory DB: ${INVENTORY_DB_NAME}"
echo "Next: run ${ROOT_DIR}/scripts/phase4-up.sh"
