# Round 2 — LLM-Generated Robustness Tests (Prompt-Controlled, No Assertions)

## Objective

Round 2 aimed to refine the LLM-generated robustness tests by:

1. Removing assertion-based test oracles (e.g., assertDoesNotThrow).
2. Enforcing raw Java output (no markdown/code fences).
3. Capturing pure SUT-level failure behavior.
4. Logging results using the survivability-based harness with canary validation.

This round isolates SUT failure modes without interference from JUnit assertion failures.

---

## Experimental Setup

### SUT
- micro_sut (ConfigParser-based strict configuration parser)
- JUnit 5
- Maven-based execution
- Survivability harness with:
  - Per-test execution
  - Canary test after each robustness test
  - Timeout: 20 seconds
  - JSONL logging

### Models Used
- Model A: qwen2.5-coder:7b
- Model B: llama3.1:8b

### Prompt Strategy (Round 2)

- Zero-shot style prompt
- Explicit constraints:
  - No assertions
  - No markdown
  - Raw Java only
  - One public class
- Class names:
  - `LLMModelA_Micro_Robustness_R2_01`
  - `LLMModelB_Micro_Robustness_R2_01`

---

## Execution Summary

### Model A

- Tests generated: 6
- Execution result:
  - FAIL: 5
  - PASS: <<ADD>>
- Exception types observed:
  - java.lang.IllegalArgumentException
- Canary status:
  - PASS for all tests

### Model B

- Tests generated: 10
- Execution result:
  - FAIL: 10
- Exception types observed:
  - java.lang.IllegalArgumentException
- Canary status:
  - PASS for all tests

---

## Observations

### 1️⃣ Assertion Contamination Removed

Unlike Round 1, no `AssertionFailedError` was observed.  
All failures were directly attributable to SUT behavior.

This confirms that:

- Prompt constraints successfully removed assertion-layer noise.
- Logged exception types now reflect actual SUT failure behavior.

---

### 2️⃣ Failure Mode Concentration

All failures correspond to early validation checks in `ConfigParser`, primarily:

- missing required key
- malformed line
- multiple '='
- numeric range violations
- null input

No deeper or delayed failures were observed.

This suggests:

- The parser is fail-fast.
- The generated tests primarily target input validation logic.
- LLMs gravitate toward structural invalidity rather than state corruption or performance stress.

---

### 3️⃣ Survivability Behavior

Across all executions:

- No TIMEOUT observed.
- No crash beyond controlled IllegalArgumentException.
- Canary always PASS.
- No state corruption detected.

Interpretation:

The SUT remains stable under all adversarial inputs generated in this round.

---

### 4️⃣ Model Comparison (Preliminary)

Both models:

- Generated structurally similar robustness tests.
- Triggered similar exception types.
- Did not expand the diversity of failure modes.

Preliminary implication:
LLM diversity (Model A vs Model B) did not translate into observable failure-mode diversity.

---

## Limitations

1. Tests target primarily invalid inputs.
2. No resource stress or large-input tests observed.
3. No concurrency or multi-call state tests generated.
4. Only one SUT (micro_sut) evaluated in Round 2.
5. Category labeling not enforced (category field = null).

---

## Key Insight from Round 2

Removing assertion logic clarified that:

LLM-generated robustness tests are currently:

- Strong at detecting input validation faults.
- Weak at provoking deeper behavioral failures (timeouts, state corruption, resource stress).

Round 2 establishes a clean baseline for:

- Failure-mode diversity analysis.
- Prompt-family experimentation in future rounds.

---



