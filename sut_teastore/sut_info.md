# TeaStore SUT — Robustness Testing Reference

## Architecture
TeaStore is a distributed microservice e-commerce demo deployed via Docker Compose.
Services: registry, db, persistence, auth, image, recommender, webui.
All external traffic goes through WebUI on port 8080.

## Base URL
http://localhost:8080

## Readiness endpoint
GET /tools.descartes.teastore.webui/ready/isready
Returns 200 when system is fully ready.

## Endpoint Inventory for Robustness Testing

### Pages (GET)
| Path | Parameters | Notes |
|------|-----------|-------|
| /tools.descartes.teastore.webui/ | none | Homepage |
| /tools.descartes.teastore.webui/category | id (int) | Category page |
| /tools.descartes.teastore.webui/product | id (int) | Product detail |
| /tools.descartes.teastore.webui/cart | none | Cart view (requires session) |
| /tools.descartes.teastore.webui/profile | none | Profile (requires login) |

### Actions (POST, application/x-www-form-urlencoded)
| Path | Body Parameters | Notes |
|------|----------------|-------|
| /tools.descartes.teastore.webui/loginAction | username, password | Login |
| /tools.descartes.teastore.webui/cartAction | addToCart=, productid (int) | Add to cart |
| /tools.descartes.teastore.webui/cartAction | removeProduct=, productid (int) | Remove from cart |
| /tools.descartes.teastore.webui/order | confirm= | Place order (requires login+cart) |

## Robustness Oracle
A test PASSES robustness if:
  1. Response status code < 500
     - 2xx = success (expected for valid input)
     - 3xx = redirect (acceptable)
     - 4xx = client error (acceptable — system rejected bad input gracefully)
     - 5xx = SERVER FAILURE = robustness violation
  2. System still reachable after the test (verified by canary run after each test)

## Robustness Scenarios to Cover
- Empty / blank parameter values
- Null-like string values ("null", "undefined", "")
- Negative integer IDs (-1, -999)
- Zero integer IDs (0)
- Extremely large integer IDs (999999999, Integer.MAX_VALUE as string)
- String values where integer expected ("abc", "!@#")
- SQL injection payloads (e.g. admin' OR '1'='1)
- XSS payloads (e.g. <script>alert(1)</script>)
- Extremely long strings (5000+ characters)
- Missing required parameters entirely
- Extra / unknown parameters
- Unauthenticated access to protected endpoints (cart, profile, order)

## Failure Categories (logged in JSONL)
- COMPILE_FAIL         — generated test did not compile
- PASS                 — test ran, oracle satisfied (no 5xx)
- FAIL                 — assertion failed (5xx returned)
- TIMEOUT              — test exceeded time limit
- HTTP_500             — server error specifically
- SYSTEM_UNAVAILABLE   — canary failed after robustness test
- ENV_READY_FAIL       — Docker environment did not become ready
- RUNNER_ERROR         — pipeline-level error

## Test Harness Details
Package:    com.example
Base class: TeaStoreBaseTest
  - get(path)                 HTTP GET, returns HttpResponse<String>
  - post(path, body)          HTTP POST form-encoded, returns HttpResponse<String>
  - assertNoServerError(r)    Asserts status < 500
Canary:     com.example.CanaryTest#test_canary

## Naming Convention for Generated Tests
TeaStore_<ModelLabel>_<PromptType>_RobustnessTest
Examples:
  TeaStore_ModelA_ZeroShot_RobustnessTest
  TeaStore_ModelB_FewShot_RobustnessTest
  TeaStore_ModelA_CoT_RobustnessTest
