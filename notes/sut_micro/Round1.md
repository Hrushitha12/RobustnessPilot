# Round 1 — LLM Robustness Generation (sut_micro)

Date: 2026-02-15

## Purpose
Baseline comparison of two LLMs generating robustness tests for the same SUT, executed via survivability harness (per-test timeout + canary).

## SUT
sut_micro (pilot.micro.ConfigParser#parse(String))

## Models
- Model A: qwen2.5-coder:7b
- Model B: llama3.1:8b

## Execution Harness
tools/run_suite.py with:
- timeout_s = 15 
- canary = pilot.micro.CanaryTest#canary_shouldAlwaysPass
- output = JSONL (status, exception_type, elapsed_ms, canary_status)

## Outputs frozen
- results/micro_llm_modelA.jsonl
- results/micro_llm_modelB.jsonl
- analysis/micro_llm_modelA_tests.txt
- analysis/micro_llm_modelB_tests.txt


# Round 1 Observations — sut_micro

## Model A (LLMModelA_Micro_Robustness_01)
- #tests executed: 11
- status: 11 FAIL
- exception_type: java.lang.IllegalArgumentException (dominant)
- timeouts: 0
- canary: PASS for all tests (no poisoning/state corruption observed)

Interpretation:
- Suite induces controlled input-rejection failures in the SUT.
- No hangs/crashes/poisoning detected.

## Model B (LLMModelB_Micro_Robustness_01)
- #tests executed: 11
- status: 11 FAIL
- exception_type: org.opentest4j.AssertionFailedError (dominant)
- timeouts: 0
- canary: PASS for all tests (no poisoning/state corruption observed)

Interpretation:
- Model B generated functional-style assertions (e.g., assertDoesNotThrow), causing failures at the test-oracle level rather than directly reflecting SUT exception types.
- This is a model behavior difference under the same task framing.

## Cross-model takeaways (Round 1)
- Both suites triggered failures, but failure-mode *type* differs:
  - Model A: SUT-level controlled exceptions (IllegalArgumentException).
  - Model B: test-oracle assertion failures (AssertionFailedError).
- Canary PASS in all cases → failures did not corrupt state.
- category field in JSONL is null → category labeling not captured in Round 1 logs (to be fixed in Round 2).


# Round 1 Limitations

1) category=null in JSONL logs: fault categories were not captured in the runtime log; analysis relies on test naming and source inspection.
2) Model B introduced functional assertions (assertDoesNotThrow) which shifts failures from SUT-level exception types to test-oracle assertion failures.
3) Round 1 is a baseline behavior snapshot; Round 2 will introduce stricter prompt constraints for fairness and improved logging metadata.
