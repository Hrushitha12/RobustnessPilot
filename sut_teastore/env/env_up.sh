#!/usr/bin/env bash
set -euo pipefail

composeFile="$(cd "$(dirname "$0")" && pwd)/docker-compose_default.yaml"

echo "[TeaStore] Starting containers..."
docker compose -f "$composeFile" up -d --remove-orphans
echo "[TeaStore] Containers started."