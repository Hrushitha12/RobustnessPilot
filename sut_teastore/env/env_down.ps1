# env_down.ps1
# Tears down TeaStore and removes volumes for a clean state each run
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\env_down.ps1

$ErrorActionPreference = "Stop"

$composeFile = Join-Path $PSScriptRoot "docker-compose_default.yaml"

Write-Host "[TeaStore] Stopping containers + removing volumes..." -ForegroundColor Cyan
docker compose -f $composeFile down -v --remove-orphans

Write-Host "[TeaStore] Environment cleaned." -ForegroundColor Green