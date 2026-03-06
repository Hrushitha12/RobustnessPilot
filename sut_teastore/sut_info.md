# TeaStore SUT Adapter

## What this SUT is
TeaStore is a distributed microservice application deployed via Docker Compose.
We treat it as an integration-level SUT (HTTP endpoints), not a unit-test SUT.

## How we start it (reproducible)
Compose file:
- `sut_teastore/env/docker-compose_default.yaml` (copied from TeaStore `examples/docker/docker-compose_default.yaml`)

Start:
- PowerShell: `powershell -ExecutionPolicy Bypass -File sut_teastore/env/env_up.ps1`
- Bash: `bash sut_teastore/env/env_up.sh`

Stop + clean:
- PowerShell: `powershell -ExecutionPolicy Bypass -File sut_teastore/env/env_down.ps1`
- Bash: `bash sut_teastore/env/env_down.sh`

## Readiness check
We wait for WebUI readiness before running tests:

URL (default):
- http://localhost:8080/tools.descartes.teastore.webui/ready/isready

Command:
- `python sut_teastore/env/env_ready.py --timeout 180`

## Notes for robustness testing
- Tests should hit TeaStore over HTTP.
- Our robustness oracle focuses on: no server 500s, system stays reachable, and recovery after bad inputs.
- Environment failures must be logged separately from test failures.