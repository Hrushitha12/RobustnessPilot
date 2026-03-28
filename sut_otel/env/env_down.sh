#!/bin/bash
# env_down.sh — Stop the OpenTelemetry Astronomy Shop (Linux/Mac)
# Run from the root of the opentelemetry-demo repo clone

echo "Stopping OpenTelemetry Astronomy Shop..."

docker compose down --remove-orphans

if [ $? -ne 0 ]; then
    echo "ERROR: docker compose down failed."
    exit 1
fi

echo "Environment stopped cleanly."
