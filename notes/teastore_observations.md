# TeaStore Robustness Testing — LLM Generation Observations

## Seed Baseline (Manual Tests)

File: `results/teastore_manual_r1.jsonl`

Tests run: 15 | PASS: 14 | FAIL: 1

Seed failure:
- `test_R1_product_string_id` — product endpoint returns 500 on non-numeric id

This is the reference point for all failure mode comparisons below.

---

## Model C — qwen3:14b

### Compile Summary

| Prompt     | Raw File | Compile | Failure Reason                               |
|------------|----------|---------|----------------------------------------------|
| ZeroShot   | raw_01   | FAIL    | MISSING_IMPORTS + MISSING_THROWS_DECLARATION |
| Structured | raw_02   | FAIL    | MISSING_THROWS_DECLARATION                   |
| FewShot    | raw_03   | PASS    | —                                            |
| Self       | raw_04   | PASS    | —                                            |
| CoT        | raw_05   | FAIL    | MISSING_THROWS_DECLARATION                   |

Compile rate: 2/5 (40%)

### Runtime Results

#### FewShot (raw_03)
Tests run: 18 | PASS: 13 | FAIL: 5 | canary: all PASS

#### Self (raw_04)
Tests run: 17 | PASS: 13 | FAIL: 4 | canary: all PASS

### Failure Mode Comparison

#### model_c FewShot vs Seed
Seed failures:
- product endpoint: non-numeric id → 500

LLM failures:
- product endpoint: non-numeric id → 500  [REPLICATED]
- cartAction: add with missing productid → 500  [NEW]
- cartAction: remove with missing productid → 500  [NEW]
- cartAction: add with invalid productid → 500  [NEW]
- cartAction: remove with invalid productid → 500  [NEW]

New failure modes discovered: 4
Seed failure modes replicated: 1
Seed failure modes missed: 0

#### model_c Self vs Seed
Seed failures:
- product endpoint: non-numeric id → 500

LLM failures:
- product endpoint: invalid id → 500  [REPLICATED]
- cartAction: remove with valid productid (unauthenticated) → 500  [NEW]
- cartAction: remove with invalid productid → 500  [NEW]
- cartAction: add with invalid productid → 500  [NEW]

New failure modes discovered: 3
Seed failure modes replicated: 1
Seed failure modes missed: 0

### Key Observations

1. qwen3:14b systematically omits `throws Exception` on all test methods
   across ZeroShot, Structured, and CoT prompts. Only FewShot and Self
   produced compilable output. This is a consistent model-level pattern
   across all 3 failing prompts — not a one-off.

2. qwen3:14b outputs extended `<think>...</think>` reasoning blocks before
   the Java class in all 5 generations. Manual extraction was required to
   strip all content before `package com.example;`. This is a pipeline
   compatibility concern for automated test generation workflows.

3. Both compilable generations (FewShot, Self) independently discovered
   cart action endpoints as a new failure area not covered by the seed.
   The model demonstrated system-level reasoning about endpoint interaction
   patterns.

4. The Self prompt produced method names using PascalCase
   (e.g. `test_R1_GetProductInvalidId`) rather than snake_case. The class
   still compiled and ran correctly.

---

## Model D — qwen2.5-coder:32b

### Compile Summary

| Prompt     | Raw File | Compile | Failure Reason              |
|------------|----------|---------|-----------------------------|
| ZeroShot   | raw_01   | FAIL    | MISSING_THROWS_DECLARATION  |
| Structured | raw_02   | FAIL    | MISSING_THROWS_DECLARATION  |
| FewShot    | raw_03   | PASS    | —                           |
| Self       | raw_04   | PASS    | —                           |
| CoT        | raw_05   | FAIL    | MISSING_THROWS_DECLARATION  |

Compile rate: 2/5 (40%)

### Runtime Results

#### FewShot (raw_03)
Tests run: 15 | PASS: 12 | FAIL: 3 | canary: all PASS

#### Self (raw_04)
Tests run: 14 | PASS: 4 | FAIL: 0 | SYSTEM_UNAVAILABLE: 5 (canary=FAIL from test 10 onward)
TeaStore became unresponsive mid-run. First 9 tests ran against live system.
Last 5 results are invalid — canary failed, system was unreachable.

### Failure Mode Comparison

#### model_d FewShot vs Seed
Seed failures:
- product endpoint: non-numeric id → 500

LLM failures:
- product endpoint: non-numeric id → 500  [REPLICATED]
- cartAction: add with missing productid param → 500  [NEW]
- cartAction: remove with missing productid param → 500  [NEW]

New failure modes discovered: 2
Seed failure modes replicated: 1
Seed failure modes missed: 0

#### model_d Self vs Seed
All 5 failures recorded with canary=FAIL.
TeaStore crashed mid-run — results classified as SYSTEM_UNAVAILABLE.
No valid failure mode comparison possible for this run.

### Key Observations

1. qwen2.5-coder:32b shows identical compile failure pattern to qwen3:14b —
   MISSING_THROWS_DECLARATION on ZeroShot, Structured, and CoT. This
   strongly confirms the failure is prompt-driven: the few-shot examples
   implicitly demonstrate `throws Exception` while the other prompts do not
   instruct the model to include it.

2. The Self prompt run caused a mid-run TeaStore crash. The canary began
   failing after `test_R1_GetCart`. A generated test input was aggressive
   enough to destabilize the distributed system — a notable stability
   finding in itself.

3. FewShot again produced the most reliable runtime results, consistent
   with model_c behavior.

---

## Model E — llama3.1:70b

### Compile Summary

| Prompt     | Raw File | Compile | Failure Reason              |
|------------|----------|---------|-----------------------------|
| ZeroShot   | raw_01   | FAIL    | MISSING_THROWS_DECLARATION  |
| Structured | raw_02   | FAIL    | MISSING_THROWS_DECLARATION  |
| FewShot    | raw_03   | PASS    | —                           |
| Self       | raw_04   | PASS    | —                           |
| CoT        | raw_05   | FAIL    | MISSING_THROWS_DECLARATION  |

Compile rate: 2/5 (40%)

### Runtime Results

#### FewShot (raw_03)
Tests run: 14 | PASS: 11 | FAIL: 3 | canary: all PASS

#### Self (raw_04)
Tests run: 15 | PASS: 12 | FAIL: 3 | canary: all PASS

### Failure Mode Comparison

#### model_e FewShot vs Seed
Seed failures:
- product endpoint: non-numeric id → 500

LLM failures:
- product endpoint: empty string id → 500  [NEW — different variant from seed]
- cartAction: remove with nonexistent product → 500  [NEW]
- cartAction: add with empty product id → 500  [NEW]

New failure modes discovered: 3
Seed failure modes replicated: 0
Seed failure modes missed: 1

Note: model_e fewshot did not directly replicate the seed's exact
non-numeric id test, but discovered a closely related variant (empty string
id) and two new cart action failures.

#### model_e Self vs Seed
Seed failures:
- product endpoint: non-numeric id → 500

LLM failures:
- product endpoint: non-integer id → 500  [REPLICATED]
- cartAction: missing productid parameter → 500  [NEW]
- cartAction: invalid productid → 500  [NEW]

New failure modes discovered: 2
Seed failure modes replicated: 1
Seed failure modes missed: 0

### Key Observations

1. llama3.1:70b shows the same MISSING_THROWS_DECLARATION pattern on
   ZeroShot, Structured, and CoT as both qwen models. This is now confirmed
   as a cross-model, prompt-driven failure — all three models of different
   families and sizes fail the same way on the same three prompt types.

2. model_e FewShot was the only run across all models and prompts that did
   not directly replicate the seed failure. Instead it found a related
   variant (empty string id vs non-numeric string id). This suggests
   llama3.1:70b explored slightly different input space than the other models.

3. model_e Self produced method names with a non-standard prefix pattern
   (test_R1_, test_R2_, test_R3_... per method rather than descriptive
   snake_case names). The class compiled and ran correctly.

---

## Cross-Model Summary

### Compile Rates

| Model                | Compile Rate | Compiled Prompts       |
|----------------------|--------------|------------------------|
| qwen3:14b            | 2/5 (40%)    | FewShot, Self          |
| qwen2.5-coder:32b    | 2/5 (40%)    | FewShot, Self          |
| llama3.1:70b         | 2/5 (40%)    | FewShot, Self          |

All three models compiled on exactly the same two prompt types.
Compile failure reason was identical across all models and all failing
prompts: MISSING_THROWS_DECLARATION.

### Runtime Results (compilable runs only)

| Model  | Prompt  | Tests | PASS | FAIL | UNAVAIL | Canary crash |
|--------|---------|-------|------|------|---------|--------------|
| C      | FewShot | 18    | 13   | 5    | 0       | No           |
| C      | Self    | 17    | 13   | 4    | 0       | No           |
| D      | FewShot | 15    | 12   | 3    | 0       | No           |
| D      | Self    | 14    | 4    | 0    | 5       | Yes          |
| E      | FewShot | 14    | 11   | 3    | 0       | No           |
| E      | Self    | 15    | 12   | 3    | 0       | No           |

### Failure Mode Discovery Summary

| Model  | Prompt  | New failures | Replicated | Missed | Notes               |
|--------|---------|--------------|------------|--------|---------------------|
| C      | FewShot | 4            | 1          | 0      |                     |
| C      | Self    | 3            | 1          | 0      |                     |
| D      | FewShot | 2            | 1          | 0      |                     |
| D      | Self    | —            | —          | —      | SYSTEM_UNAVAILABLE  |
| E      | FewShot | 3            | 0          | 1      | found variant       |
| E      | Self    | 2            | 1          | 0      |                     |

Total unique new failure modes discovered across all runs: 5
- cartAction: add with missing/empty productid → 500
- cartAction: remove with missing/empty productid → 500
- cartAction: add with invalid productid → 500
- cartAction: remove with invalid/nonexistent productid → 500
- cartAction: remove with unauthenticated valid productid → 500

Seed failure mode replicated by: model_c fewshot, model_c self,
model_d fewshot, model_e self (4 out of 6 valid runs).

### Cross-Prompt Pattern

The FewShot prompt was the highest performing prompt type across all
models for both compilability and new failure discovery. The likely reason
is that the few-shot examples in the prompt implicitly demonstrated the
correct `throws Exception` signature, which the model learned from context
rather than explicit instruction.

The Self prompt compiled on all three models and produced valid runtime
results in 2 out of 3 cases (model_d self crashed the system). This
suggests self-critique reasoning helps with structural correctness but
may produce more aggressive test inputs that stress the system harder.

ZeroShot, Structured, and CoT all failed to compile across all three models
due to the same missing throws declaration. This is a direct prompt
engineering finding: these prompt types need an explicit instruction
stating that every test method must declare `throws Exception`.



---

## Round 2 (v2 Prompts) — throws Exception Fix Applied

### Change from Round 1
All ZeroShot, Structured, and CoT prompts were updated to include:
"Each test method must declare throws Exception in its signature"
FewShot and Self were not rerun as they already compiled in round 1.

### Compile Results

| Model  | Prompt     | Round 1  | Round 2  |
|--------|------------|----------|----------|
| C      | ZeroShot   | FAIL     | FAIL (MALFORMED_OUTPUT — Chinese text in generation) |
| C      | Structured | FAIL     | PASS     |
| C      | CoT        | FAIL     | PASS     |
| D      | ZeroShot   | FAIL     | PASS     |
| D      | Structured | FAIL     | PASS     |
| D      | CoT        | FAIL     | PASS     |
| E      | ZeroShot   | FAIL     | PASS     |
| E      | Structured | FAIL     | PASS     |
| E      | CoT        | FAIL     | PASS     |

Round 2 compile rate: 8/9 (89%)
Only failure: model_c ZeroShot v2 — qwen3:14b produced Chinese text
mixed into the Java output when no few-shot examples were present.

### Runtime Results v2

| Model | Prompt     | Tests | PASS | FAIL |
|-------|------------|-------|------|------|
| C     | Structured | 16    | 14   | 2    |
| C     | CoT        | 18    | 14   | 4    |
| D     | ZeroShot   | 14    | 14   | 0    |
| D     | Structured | 16    | 14   | 2    |
| D     | CoT        | 14    | 9    | 5    |
| E     | ZeroShot   | 18    | 16   | 2    |
| E     | Structured | 16    | 14   | 2    |
| E     | CoT        | 17    | 13   | 4    |

### Key Finding from Round 2
Adding a single explicit instruction ("each test method must declare
throws Exception") raised compile rate from 40% to 89% across all
three models. This confirms the round 1 failures were entirely
prompt-driven, not model capability failures. The models knew how
to write correct robustness tests — they just needed to be told
about the Java checked exception requirement explicitly.

### model_d Self — Crash Investigation Result

The original run of model_d Self (round 1) showed canary=FAIL for the
last 5 tests, suggesting TeaStore crashed mid-run.

Crash investigation rerun (after fresh env_down + env_up): all 14 tests
PASSED with canary=PASS across all. The crash was NOT reproducible.

Conclusion: The original failure was a transient environment instability,
not a deterministic robustness vulnerability triggered by a specific test
input. Likely caused by accumulated system state from prior test runs
during the same session without a full environment restart between runs.