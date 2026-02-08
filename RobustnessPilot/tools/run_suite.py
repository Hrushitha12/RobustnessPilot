import argparse
import json
import os
import subprocess
import time
from datetime import datetime
from typing import Dict, Optional

import sys
import os
import subprocess
import json

def run_single(project_dir: str, test_id: str, timeout_s: int) -> dict:
    runner = os.path.join(os.path.dirname(__file__), "run_single_test.py")
    cmd = [sys.executable, runner,
           "--project_dir", project_dir,
           "--test_id", test_id,
           "--timeout_s", str(timeout_s)]
    p = subprocess.run(cmd, capture_output=True, text=True)

    if p.returncode != 0 and not p.stdout.strip():
        return {
            "test_id": test_id,
            "status": "RUNNER_ERROR",
            "return_code": p.returncode,
            "exception_type": None,
            "elapsed_ms": None,
            "runner_stderr": (p.stderr or "")[-1200:],
        }

    try:
        return json.loads(p.stdout.strip())
    except Exception:
        return {
            "test_id": test_id,
            "status": "RUNNER_ERROR",
            "return_code": p.returncode,
            "exception_type": None,
            "elapsed_ms": None,
            "runner_stdout": (p.stdout or "")[-1200:],
            "runner_stderr": (p.stderr or "")[-1200:],
        }

def category_from_test_id(test_id: str) -> Optional[str]:
    # test names in our code: test_R1_..., test_R2_...
    # test_id format: fqcn#method
    if "#test_R1_" in test_id:
        return "R1"
    if "#test_R2_" in test_id:
        return "R2"
    return None

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--sut", required=True, help="sut name label (commons_lang|micro_sut)")
    ap.add_argument("--project_dir", required=True, help="Maven project directory")
    ap.add_argument("--tests_file", required=True, help="File containing test ids (one per line)")
    ap.add_argument("--canary", required=True, help="Canary test id: FullyQualifiedClass#method")
    ap.add_argument("--timeout_s", type=int, default=8, help="Timeout per test")
    ap.add_argument("--out", required=True, help="Output JSONL file")
    args = ap.parse_args()

    with open(args.tests_file, "r", encoding="utf-8") as f:
        tests = [line.strip() for line in f if line.strip()]

    os.makedirs(os.path.dirname(args.out), exist_ok=True)

    with open(args.out, "a", encoding="utf-8") as out_f:
        for tid in tests:
            rec = run_single(args.project_dir, tid, args.timeout_s)
            rec.update({
                "timestamp": datetime.now().isoformat(timespec="seconds"),
                "sut": args.sut,
                "suite": "robustness",
                "category": category_from_test_id(tid),
            })

            # Canary immediately after robustness test
            can = run_single(args.project_dir, args.canary, args.timeout_s)
            rec["canary_test_id"] = args.canary
            rec["canary_status"] = can.get("status")
            rec["canary_exception_type"] = can.get("exception_type")

            out_f.write(json.dumps(rec, ensure_ascii=False) + "\n")
            out_f.flush()

            print(f"{tid} -> {rec['status']} | canary={rec['canary_status']}")

if __name__ == "__main__":
    main()
