"""
env_ready.py — Wait until the OpenTelemetry Astronomy Shop is ready to accept requests.

Polls GET /api/products (the product catalogue endpoint).
This endpoint is served by the frontend proxy and requires the product-catalog
service and the frontend to both be healthy — making it a reliable readiness indicator.

Usage:
    python env_ready.py --timeout 300

Exit code 0 = ready. Exit code 1 = timed out or unreachable.
"""

import argparse
import sys
import time
import urllib.request
import urllib.error

HEALTH_URL = "http://localhost:8080/api/products"
POLL_INTERVAL = 5  # seconds between polls


def wait_for_ready(timeout: int) -> bool:
    deadline = time.time() + timeout
    attempt = 0
    while time.time() < deadline:
        attempt += 1
        try:
            with urllib.request.urlopen(HEALTH_URL, timeout=5) as resp:
                if resp.status < 500:
                    print(f"[env_ready] Ready after {attempt} attempt(s). "
                          f"GET /api/products returned HTTP {resp.status}.")
                    return True
                else:
                    print(f"[env_ready] Attempt {attempt}: HTTP {resp.status} — not ready yet.")
        except urllib.error.HTTPError as e:
            # 4xx is still the proxy responding — not ready in terms of services
            # but means the proxy is up; keep waiting for full stack
            print(f"[env_ready] Attempt {attempt}: HTTP {e.code} — proxy up, services still starting.")
        except Exception as e:
            print(f"[env_ready] Attempt {attempt}: {type(e).__name__} — {e}")
        time.sleep(POLL_INTERVAL)
    return False


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--timeout", type=int, default=300,
                        help="Maximum seconds to wait (default: 300). "
                             "OTel demo has ~20 services and takes longer than TeaStore.")
    args = parser.parse_args()

    print(f"[env_ready] Waiting up to {args.timeout}s for {HEALTH_URL} ...")
    if wait_for_ready(args.timeout):
        print("[env_ready] OpenTelemetry Astronomy Shop is ready.")
        sys.exit(0)
    else:
        print(f"[env_ready] TIMEOUT: SUT not ready after {args.timeout}s. "
              f"Check 'docker compose ps' for unhealthy containers.")
        sys.exit(1)


if __name__ == "__main__":
    main()
