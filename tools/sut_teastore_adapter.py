import os
import subprocess
import sys
from pathlib import Path

TEASTORE_DIR = Path("sut_teastore")
ENV_DIR = TEASTORE_DIR / "env"
COMPOSE_FILE = ENV_DIR / "docker-compose_default.yaml"
READY_SCRIPT = ENV_DIR / "env_ready.py"
UP_SCRIPT_PS   = ENV_DIR / "env_up.ps1"
DOWN_SCRIPT_PS = ENV_DIR / "env_down.ps1"
UP_SCRIPT_SH   = ENV_DIR / "env_up.sh"
DOWN_SCRIPT_SH = ENV_DIR / "env_down.sh"


def _run(cmd, cwd=None, timeout=None):
    return subprocess.run(
        cmd, cwd=cwd, timeout=timeout,
        shell=False, check=True,
        capture_output=True, text=True,
    )


def env_up():
    if os.name == "nt":
        _run(["powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", str(UP_SCRIPT_PS)])
    else:
        _run(["bash", str(UP_SCRIPT_SH)])


def env_down():
    if os.name == "nt":
        _run(["powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", str(DOWN_SCRIPT_PS)])
    else:
        _run(["bash", str(DOWN_SCRIPT_SH)])


def env_ready(timeout_s=300):
    _run([sys.executable, str(READY_SCRIPT), "--timeout", str(timeout_s)])


def compose_logs_tail(out_path: Path, tail=200):
    with out_path.open("w", encoding="utf-8") as f:
        p = subprocess.run(
            ["docker", "compose", "-f", str(COMPOSE_FILE),
             "logs", "--tail", str(tail)],
            check=False, text=True,
            stdout=f, stderr=subprocess.STDOUT,
        )
        return p.returncode


env_up_windows = env_up
env_down_windows = env_down