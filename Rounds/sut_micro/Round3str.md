# Round 3 — Structured Prompt (Failure-Diversity Enforcement)

## Objective

Round 3 introduced a structured prompt format with explicit:

- SUT specification
- Category enforcement (12 required failure types)
- Naming constraints
- No assertions / no try-catch rule
- Raw Java-only output constraint

Goal:
Increase failure-mode diversity and enforce systematic coverage.

---

## Prompt Strategy

Prompt Type: Structured

Enhancements compared to Round 2:
- Explicit 12-category enforcement
- Explicit baseline valid configuration provided
- Strict method naming format
- External survivability oracle only

Class names:
- Model A: LLMModelA_Micro_Robustness_R3_01
- Model B: LLMModelB_Micro_Robustness_R3_01

---

## Execution Summary

### Model A
- Tests generated: 12
- Compilation: SUCCESS
- Status: All FAIL
- Exception type(s): java.lang.IllegalArgumentException
- TIMEOUT: None
- Canary: PASS for all tests

### Model B
- Tests generated: 12
- Compilation: SUCCESS
- Status: All FAIL
- Exception type(s): java.lang.IllegalArgumentException
- TIMEOUT: None
- Canary: PASS for all tests

---

## Observations

1. Category compliance improved significantly.
2. All required failure categories were generated.
3. However, most failures still correspond to early validation paths.
4. No deeper state corruption or delayed failures observed.
5. No TIMEOUT behavior observed even with extreme_length input.

---

## Interpretation

Structured prompting improves:
- Coverage regularity
- Naming consistency
- Predictability of output

But does not significantly increase failure-mode depth for this SUT.

---

## Limitation

micro_sut is fail-fast and heavily validation-oriented.
Failure diversity may be bounded by SUT design.
