#### Failure Mode Comparison — model_c FewShot vs Seed

Seed failures (teastore_manual_r1):
- product endpoint: non-numeric id → 500

LLM failures (model_c fewshot):
- product endpoint: non-numeric id → 500  [REPLICATED from seed]
- cartAction: add with missing productid → 500  [NEW]
- cartAction: remove with missing productid → 500  [NEW]
- cartAction: add with invalid productid → 500  [NEW]
- cartAction: remove with invalid productid → 500  [NEW]

New failure modes discovered: 4
Seed failure modes replicated: 1
Seed failure modes missed: 0

--- 

#### Failure Mode Comparison — model_c Self vs Seed

Seed failures (teastore_manual_r1):
- product endpoint: non-numeric id → 500

LLM failures (model_c self):
- product endpoint: invalid id → 500  [REPLICATED from seed]
- cartAction: remove with valid productid → 500  [NEW — removing product from empty/unauthenticated cart]
- cartAction: remove with invalid productid → 500  [NEW]
- cartAction: add with invalid productid → 500  [NEW]

New failure modes discovered: 3
Seed failure modes replicated: 1
Seed failure modes missed: 0

---

#### Failure Mode Comparison — model_d FewShot vs Seed

Seed failures (teastore_manual_r1):
- product endpoint: non-numeric id → 500

LLM failures (model_d fewshot):
- product endpoint: non-numeric id → 500  [REPLICATED from seed]
- cartAction: add with missing productid param → 500  [NEW]
- cartAction: remove with missing productid param → 500  [NEW]

New failure modes discovered: 2
Seed failure modes replicated: 1
Seed failure modes missed: 0

---

#### Failure Mode Comparison — model_d Self vs Seed

All 5 failures recorded with canary=FAIL.
TeaStore became unresponsive mid-run — results are SYSTEM_UNAVAILABLE.
No valid failure mode comparison possible for this run.
Genuine failures before crash: 0 confirmed.

## Round 2 (v2) — Failure Mode Comparisons

Seed failures (teastore_manual_r1):
- product endpoint: non-numeric string id → 500

---

### Model C — qwen3:14b

#### model_c Structured v2 vs Seed
LLM failures:
- product endpoint: non-integer string id → 500  [REPLICATED]
- cartAction: remove with non-existent productid → 500  [NEW]

New failure modes discovered: 1
Seed failure modes replicated: 1
Seed failure modes missed: 0

---

#### model_c CoT v2 vs Seed
Note: CoT v2 used numeric method names (test_R1_01 through test_R1_18).
Exact input for each failure cannot be determined from method name alone.
The Java file must be opened to identify what input each failing test used.

Failing tests: test_R1_04, test_R1_14, test_R1_15, test_R1_16 (4 failures)
Failure mode mapping: UNDETERMINED — numeric method names carry no semantic information.

Recommendation: open TeaStore_ModelC_CoT_v2_RobustnessTest.java and check
what input each of these 4 methods sends to resolve this.

---

### Model D — qwen2.5-coder:32b

#### model_d ZeroShot v2 vs Seed
LLM failures: none — 14/14 PASS

New failure modes discovered: 0
Seed failure modes replicated: 0
Seed failure modes missed: 1

Note: model_d ZeroShot v2 did not trigger any robustness failures at all.
The model generated valid-looking inputs rather than adversarial ones.
This is itself a finding — ZeroShot without examples produces conservative
tests that pass but have low fault-detection value.

---

#### model_d Structured v2 vs Seed
LLM failures:
- product endpoint: non-integer string id → 500  [REPLICATED]
- cartAction: remove with non-existent productid → 500  [NEW]

New failure modes discovered: 1
Seed failure modes replicated: 1
Seed failure modes missed: 0

---

#### model_d CoT v2 vs Seed
LLM failures:
- product endpoint: whitespace-only string as id → 500  [NEW]
- product endpoint: XSS payload as id → 500  [NEW]
- product endpoint: non-integer string id → 500  [REPLICATED]
- product endpoint: empty string as id → 500  [NEW]
- cartAction: missing productid parameter → 500  [NEW]

New failure modes discovered: 4
Seed failure modes replicated: 1
Seed failure modes missed: 0

Note: CoT v2 was the strongest performing prompt for model_d — it explored
XSS payloads and whitespace inputs that no other model or prompt discovered.
This suggests CoT reasoning led the model to think about injection and
boundary cases more aggressively.

---

### Model E — llama3.1:70b

#### model_e ZeroShot v2 vs Seed
LLM failures:
- path: null-like path string → 500  [NEW — different failure surface from seed]
- cartAction: remove product → 500  [NEW]

New failure modes discovered: 2
Seed failure modes replicated: 0
Seed failure modes missed: 1

Note: model_e ZeroShot v2 did not replicate the exact seed failure but
found two new failure surfaces. The null path test (test_R3_NullPathGet)
found a different failure class entirely — the path itself rather than
a query parameter.

---

#### model_e Structured v2 vs Seed
LLM failures:
- product endpoint: non-integer string id → 500  [REPLICATED]
- cartAction: remove with non-existent productid → 500  [NEW]

New failure modes discovered: 1
Seed failure modes replicated: 1
Seed failure modes missed: 0

---

#### model_e CoT v2 vs Seed
LLM failures:
- product endpoint: wrong type id → 500  [REPLICATED]
- cartAction: missing required parameters → 500  [NEW]
- product endpoint: oversized string as product name field → 500  [NEW]
- cartAction: empty cart actions body → 500  [NEW]

New failure modes discovered: 3
Seed failure modes replicated: 1
Seed failure modes missed: 0

---

## Round 2 Cross-Model Failure Mode Summary

| Model | Prompt     | New Failures | Replicated | Missed | Notes                          |
|-------|------------|--------------|------------|--------|--------------------------------|
| C     | Structured | 1            | 1          | 0      |                                |
| C     | CoT        | UNDETERMINED | —          | —      | numeric method names           |
| D     | ZeroShot   | 0            | 0          | 1      | all tests passed, too safe     |
| D     | Structured | 1            | 1          | 0      |                                |
| D     | CoT        | 4            | 1          | 0      | best performer, found XSS      |
| E     | ZeroShot   | 2            | 0          | 1      | found null path failure        |
| E     | Structured | 1            | 1          | 0      |                                |
| E     | CoT        | 3            | 1          | 0      |                                |

## Combined Failure Mode Discovery Across Both Rounds

Unique new failure modes found across ALL runs (round 1 + round 2):

1. cartAction: add with missing/empty productid → 500
2. cartAction: remove with missing/empty productid → 500
3. cartAction: add with invalid/non-existent productid → 500
4. cartAction: remove with invalid/non-existent productid → 500
5. cartAction: remove unauthenticated with valid productid → 500
6. product endpoint: XSS payload as id → 500  [found only by model_d CoT v2]
7. product endpoint: whitespace-only string as id → 500  [found only by model_d CoT v2]
8. product endpoint: empty string as id → 500
9. path: null-like path string → 500  [found only by model_e ZeroShot v2]
10. cartAction: empty body → 500
11. product endpoint: oversized string value → 500

Total unique new failure modes beyond seed: 11
Seed failure replicated by: majority of compilable runs across both rounds