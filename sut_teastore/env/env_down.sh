#!/usr/bin/env bash
set -euo pipefail

composeFile="$(cd "$(dirname "$0")" && pwd)/docker-compose_default.yaml"

echo "[TeaStore] Stopping containers + removing volumes..."
docker compose -f "$composeFile" down -v --remove-orphans
echo "[TeaStore] Environment cleaned."