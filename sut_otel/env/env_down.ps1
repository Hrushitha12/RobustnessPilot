# env_down.ps1 — Stop the OpenTelemetry Astronomy Shop (Windows PowerShell)
# Run from the root of the opentelemetry-demo repo clone

Write-Host "Stopping OpenTelemetry Astronomy Shop..." -ForegroundColor Cyan

docker compose -f docker-compose.minimal.yml down --remove-orphans

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: docker compose down failed." -ForegroundColor Red
    exit 1
}

Write-Host "Environment stopped cleanly." -ForegroundColor Green
