# env_ready.py
# Poll TeaStore until it's ready.
# Default readiness URL:
#   http://localhost:8080/tools.descartes.teastore.webui/ready/isready
#
# Usage:
#   python .\env_ready.py
#   python .\env_ready.py --url http://localhost:8080/tools.descartes.teastore.webui/ready/isready --timeout 180

import argparse
import sys
import time
import urllib.request
import urllib.error

DEFAULT_URL = "http://localhost:8080/tools.descartes.teastore.webui/ready/isready"

def is_ready(url: str) -> bool:
    try:
        req = urllib.request.Request(url, method="GET")
        with urllib.request.urlopen(req, timeout=5) as resp:
            # Accept any 2xx as ready.
            return 200 <= resp.status < 300
    except (urllib.error.URLError, urllib.error.HTTPError, TimeoutError):
        return False

def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--url", default=DEFAULT_URL)
    parser.add_argument("--timeout", type=int, default=180, help="Seconds to wait before failing")
    parser.add_argument("--interval", type=float, default=1.5, help="Seconds between polls")
    args = parser.parse_args()

    start = time.time()
    print(f"[TeaStore] Waiting for readiness: {args.url}")
    while True:
        if is_ready(args.url):
            elapsed = time.time() - start
            print(f"[TeaStore] READY after {elapsed:.1f}s")
            return 0

        if time.time() - start > args.timeout:
            print(f"[TeaStore] NOT READY after {args.timeout}s (timeout).", file=sys.stderr)
            return 2

        time.sleep(args.interval)

if __name__ == "__main__":
    raise SystemExit(main())