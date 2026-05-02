#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RESET_DB="${RESET_DB:-true}"

cleanup() {
	local pid_file
	for pid_file in /tmp/hospital-phase4/inventory-service.pid /tmp/hospital-phase4/hospital-core.pid; do
		if [[ -f "${pid_file}" ]]; then
			local pid
			pid="$(cat "${pid_file}")"
			if [[ -n "${pid}" ]] && kill -0 "${pid}" >/dev/null 2>&1; then
				kill "${pid}" >/dev/null 2>&1 || true
			fi
		fi
	done
}

trap cleanup EXIT

cd "${ROOT_DIR}"

if [[ "${RESET_DB}" == "true" ]]; then
	bash scripts/phase4-db-reset.sh
fi

bash -lc 'rm -f /tmp/hospital-phase4/*.pid /tmp/hospital-phase4/*.log'
bash scripts/phase4-up.sh
python3 scripts/inventory-standalone-smoke.py
python3 scripts/phase4-clinical-smoke.py

echo "Phase 4 dual-service smoke passed."
