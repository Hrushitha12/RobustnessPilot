import sys
import os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from sut_teastore_adapter import (
    env_up_windows, env_down_windows, env_ready, compose_logs_tail
)

import argparse
import json
import subprocess
import traceback
from datetime import datetime
from pathlib import Path
from typing import Optional


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
    if "#test_R1_" in test_id:
        return "R1"
    if "#test_R2_" in test_id:
        return "R2"
    return None


def write_jsonl(out_f, obj: dict) -> None:
    out_f.write(json.dumps(obj, ensure_ascii=False) + "\n")
    out_f.flush()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--sut", required=True, help="sut name label (commons_lang|micro_sut|record_store|teastore)")
    ap.add_argument("--project_dir", required=True, help="Maven project directory")
    ap.add_argument("--tests_file", required=True, help="File containing test ids (one per line)")
    ap.add_argument("--canary", required=True, help="Canary test id: FullyQualifiedClass#method")
    ap.add_argument("--timeout_s", type=int, default=8, help="Timeout per test")
    ap.add_argument("--out", required=True, help="Output JSONL file")

    ap.add_argument("--env_timeout_s", type=int, default=300, help="TeaStore readiness timeout (seconds)")
    ap.add_argument("--teastore_logs_tail", type=int, default=300, help="Lines of docker compose logs to capture on failure")

    args = ap.parse_args()

    with open(args.tests_file, "r", encoding="utf-8") as f:
        tests = [line.strip() for line in f if line.strip()]

    os.makedirs(os.path.dirname(args.out), exist_ok=True)

    with open(args.out, "a", encoding="utf-8") as out_f:
        teastore_logs_file = None
        started_teastore = False

        try:
            if False and args.sut.lower() == "teastore":
                run_id = datetime.now().strftime("%Y%m%d_%H%M%S")
                os.makedirs("results", exist_ok=True)
                teastore_logs_file = Path("results") / f"teastore_env_logs_{run_id}.txt"

                write_jsonl(out_f, {
                    "timestamp": datetime.now().isoformat(timespec="seconds"),
                    "sut": args.sut,
                    "suite": "robustness",
                    "stage": "env_up",
                    "status": "START",
                })
                env_up_windows()
                started_teastore = True
                write_jsonl(out_f, {
                    "timestamp": datetime.now().isoformat(timespec="seconds"),
                    "sut": args.sut,
                    "suite": "robustness",
                    "stage": "env_up",
                    "status": "OK",
                })

                write_jsonl(out_f, {
                    "timestamp": datetime.now().isoformat(timespec="seconds"),
                    "sut": args.sut,
                    "suite": "robustness",
                    "stage": "env_ready",
                    "status": "START",
                    "timeout_s": args.env_timeout_s
                })
                env_ready(timeout_s=args.env_timeout_s)
                write_jsonl(out_f, {
                    "timestamp": datetime.now().isoformat(timespec="seconds"),
                    "sut": args.sut,
                    "suite": "robustness",
                    "stage": "env_ready",
                    "status": "OK",
                })

            for tid in tests:
                rec = run_single(args.project_dir, tid, args.timeout_s)
                rec.update({
                    "timestamp": datetime.now().isoformat(timespec="seconds"),
                    "sut": args.sut,
                    "suite": "robustness",
                    "category": category_from_test_id(tid),
                })

                can = run_single(args.project_dir, args.canary, args.timeout_s)
                rec["canary_test_id"] = args.canary
                rec["canary_status"] = can.get("status")
                rec["canary_exception_type"] = can.get("exception_type")

                write_jsonl(out_f, rec)
                print(f"{tid} -> {rec['status']} | canary={rec['canary_status']}")

        except Exception as e:
            if args.sut.lower() == "teastore" and teastore_logs_file is not None:
                try:
                    compose_logs_tail(teastore_logs_file, tail=args.teastore_logs_tail)
                except Exception:
                    pass

            write_jsonl(out_f, {
                "timestamp": datetime.now().isoformat(timespec="seconds"),
                "sut": args.sut,
                "suite": "robustness",
                "stage": "pipeline",
                "status": "FAILED",
                "error": str(e),
                "trace": traceback.format_exc(),
                "teastore_env_logs": str(teastore_logs_file) if teastore_logs_file else None,
            })
            raise

        finally:
            if args.sut.lower() == "teastore" and started_teastore:
                try:
                    write_jsonl(out_f, {
                        "timestamp": datetime.now().isoformat(timespec="seconds"),
                        "sut": args.sut,
                        "suite": "robustness",
                        "stage": "env_down",
                        "status": "START",
                    })
                    env_down_windows()
                    write_jsonl(out_f, {
                        "timestamp": datetime.now().isoformat(timespec="seconds"),
                        "sut": args.sut,
                        "suite": "robustness",
                        "stage": "env_down",
                        "status": "OK",
                    })
                except Exception as e2:
                    write_jsonl(out_f, {
                        "timestamp": datetime.now().isoformat(timespec="seconds"),
                        "sut": args.sut,
                        "suite": "robustness",
                        "stage": "env_down",
                        "status": "FAILED",
                        "error": str(e2),
                    })


if __name__ == "__main__":
    main()
