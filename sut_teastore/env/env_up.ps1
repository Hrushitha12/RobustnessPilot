# env_up.ps1
# Brings up TeaStore using docker compose (default stack)
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\env_up.ps1

$ErrorActionPreference = "Stop"

$composeFile = Join-Path $PSScriptRoot "docker-compose_default.yaml"

Write-Host "[TeaStore] Starting containers..." -ForegroundColor Cyan
docker compose -f $composeFile up -d --remove-orphans

Write-Host "[TeaStore] Containers started." -ForegroundColor Green
Write-Host "[TeaStore] Tip: check status with: docker compose -f $composeFile ps" -ForegroundColor DarkGray