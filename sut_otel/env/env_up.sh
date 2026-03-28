#!/bin/bash
# env_up.sh — Start the OpenTelemetry Astronomy Shop (Linux/Mac)
# Run from the root of the opentelemetry-demo repo clone

echo "Starting OpenTelemetry Astronomy Shop..."

docker compose up --force-recreate --remove-orphans --detach

if [ $? -ne 0 ]; then
    echo "ERROR: docker compose up failed."
    exit 1
fi

echo "Containers started. Run env_ready.py to wait for readiness."
