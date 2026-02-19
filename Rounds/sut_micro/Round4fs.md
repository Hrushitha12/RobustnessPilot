# Round 4 — Few-Shot Prompt

## Objective

Round 4 introduced few-shot prompting.

Two example robustness tests were provided in the prompt.
Model was instructed to replicate style and complete remaining categories.

Goal:
Improve format stability and reduce hallucinations.

---

## Prompt Strategy

Prompt Type: Few-Shot

Differences from Round 3:
- Included two fully written example tests
- Model instructed to reuse examples and extend coverage

Class names:
- Model A: LLMModelA_Micro_Robustness_R4_01
- Model B: LLMModelB_Micro_Robustness_R4_01

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

1. Few-shot reduced formatting instability.
2. No markdown fences observed (improvement from earlier rounds).
3. No assertion contamination.
4. Failure behavior remained primarily validation-triggered.

---

## Interpretation

Few-shot improves output formatting stability.
However, failure diversity remains similar to structured prompting.

Prompt style refinement improves syntactic reliability more than behavioral diversity.
