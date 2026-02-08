import argparse
import json
import re
import subprocess
import time
from typing import Optional, Tuple

EXC_RE = re.compile(r"(?m)^\s*([a-zA-Z0-9_.]+Exception|[a-zA-Z0-9_.]+Error)\b")

def run_cmd(cmd, cwd, timeout_s: int) -> Tuple[int, str, str, bool, int]:
    """
    Returns: (returncode, stdout, stderr, timed_out, elapsed_ms)
    """
    start = time.time()
    try:
        p = subprocess.run(
            cmd,
            cwd=cwd,
            capture_output=True,
            text=True,
            timeout=timeout_s,
            shell=False,
        )
        elapsed_ms = int((time.time() - start) * 1000)
        return p.returncode, p.stdout, p.stderr, False, elapsed_ms
    except subprocess.TimeoutExpired as e:
        elapsed_ms = int((time.time() - start) * 1000)
        out = e.stdout if isinstance(e.stdout, str) else (e.stdout.decode("utf-8", errors="ignore") if e.stdout else "")
        err = e.stderr if isinstance(e.stderr, str) else (e.stderr.decode("utf-8", errors="ignore") if e.stderr else "")
        return 124, out, err, True, elapsed_ms

def extract_exception(stdout: str, stderr: str) -> Optional[str]:
    text = (stdout or "") + "\n" + (stderr or "")
    m = EXC_RE.search(text)
    return m.group(1) if m else None

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--project_dir", required=True, help="Maven project directory containing pom.xml")
    ap.add_argument("--test_id", required=True, help="FullyQualifiedClass#method")
    ap.add_argument("--timeout_s", type=int, default=8, help="Timeout per test in seconds")
    ap.add_argument("--surefire_trimstacktrace", action="store_true", help="Use surefire to trim stacktraces")
    args = ap.parse_args()

    # Maven command: run exactly one test method
    # Quotes matter in PowerShell, but subprocess handles args list safely.
    import os

# Maven command: run exactly one test method
# On Windows, mvn is typically mvn.cmd; invoke via cmd /c to be safe.
    if os.name == "nt":
        cmd = ["cmd", "/c", "mvn", f"-Dtest={args.test_id}", "test"]
    else:
        cmd = ["mvn", f"-Dtest={args.test_id}", "test"]

    if args.surefire_trimstacktrace:
        cmd.insert(1, "-DtrimStackTrace=true")

    rc, out, err, timed_out, elapsed_ms = run_cmd(cmd, cwd=args.project_dir, timeout_s=args.timeout_s)

    # Interpret status
    if timed_out:
        status = "TIMEOUT"
    elif rc == 0:
        status = "PASS"
    else:
        # mvn returns non-zero for test failures/errors
        status = "FAIL"

    exc = extract_exception(out, err)

    result = {
        "test_id": args.test_id,
        "status": status,
        "return_code": rc,
        "exception_type": exc,
        "elapsed_ms": elapsed_ms,
    }

    print(json.dumps(result, ensure_ascii=False))

if __name__ == "__main__":
    main()
