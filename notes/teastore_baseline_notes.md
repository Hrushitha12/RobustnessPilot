# TeaStore Baseline Robustness Test Execution Notes

## Purpose

Before running LLM-generated tests, the TeaStore pipeline must be
validated using manually written robustness tests.

This ensures: - the SUT environment works correctly - the test harness
executes tests correctly - the canary mechanism detects system health -
the result logging pipeline works correctly.

## Baseline Execution Command

The following command was used:

    python tools/run_suite.py   --sut teastore   --project_dir sut_teastore/test_harness   --tests_file analysis/teastore_test_ids.txt   --canary com.example.CanaryTest#test_canary   --timeout_s 30   --out results/teastore_manual_r1.jsonl

## Example Output

    test_R1_category_negative_id -> PASS | canary=PASS
    test_R1_product_string_id -> FAIL | canary=PASS
    test_R1_login_empty_credentials -> PASS | canary=PASS

## Interpretation of Results

Each output line contains two components:

    <Test Result> | canary=<Canary Result>

Possible interpretations:

  Result        Meaning
  ------------- ----------------------------------------------------
  PASS / PASS   robustness test passed and system remained healthy
  FAIL / PASS   request failed but system stayed operational
  FAIL / FAIL   system failure or instability occurred

In the baseline run:

-   most tests passed
-   one robustness test failed
-   **the canary always passed**

This indicates that: - TeaStore handled invalid inputs without
crashing - the system remained stable after robustness tests - the
canary health check is reliable.

## Output Artifact

Results were saved to:

    results/teastore_manual_r1.jsonl

This file serves as the **baseline robustness dataset** for TeaStore
before introducing LLM-generated tests.

## Role in the Experiment Pipeline

The baseline stage corresponds to the following steps in the
experimental workflow:

1.  Start TeaStore environment
2.  Execute manually designed robustness tests
3.  Run canary after each test
4.  Log results
5.  Verify environment stability

Only after this stage is validated should LLM-generated robustness tests
be executed.

This ensures that any later failures can be attributed to
model-generated tests rather than infrastructure problems.
