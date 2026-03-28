# env_up.ps1 — Start the OpenTelemetry Astronomy Shop (Windows PowerShell)
# Run from the root of the opentelemetry-demo repo clone

Write-Host "Starting OpenTelemetry Astronomy Shop..." -ForegroundColor Cyan

docker compose -f docker-compose.minimal.yml up --force-recreate --remove-orphans --detach

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: docker compose up failed." -ForegroundColor Red
    exit 1
}

Write-Host "Containers started. Run env_ready.py to wait for readiness." -ForegroundColor Green
