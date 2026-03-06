# Round 5 — Self-Check Prompt

## Objective

Round 5 introduced self-check prompting.

Model instructed to:
- Internally verify constraints
- Ensure 12 categories
- Ensure no assertions
- Ensure correct class structure
- Output only Java

Goal:
Test whether self-reflection improves reliability and correctness.

---

## Execution Summary

### Model A

- Tests generated: 12
- Compilation: SUCCESS
- Status: All FAIL
- Exception type(s): java.lang.IllegalArgumentException
- TIMEOUT: None
- Canary: PASS for all tests

Observation:
Self-check did not significantly change failure diversity but maintained structural correctness.

---

### Model B

- Tests generated: 12
- Compilation: FAILED

Compiler errors observed:
- Illegal Unicode character (`\u0000`)
- Previous attempt: type mismatch (ParsedConfig passed instead of String)
- JSON-style inputs inconsistent with expected SUT format

Decision:
Artifact marked as REJECTED.
No manual correction applied.

---

## Observations

1. Self-check improved Model A stability.
2. Self-check did not prevent structural or semantic generation errors in Model B.
3. Internal model verification does not guarantee compilation correctness.
4. Increased prompt sophistication does not eliminate generation artifacts.

---

## Interpretation

Self-check prompting:
- May improve constraint adherence for stronger models.
- Does not ensure environmental compatibility.
- Introduces overconfidence in generation reliability.

---

## Research Insight

Prompt sophistication progression:

Zero-shot → Structured → Few-shot → Self-check

Results show:
- Structural compliance improves
- Formatting stability improves
- Behavioral failure diversity remains bounded by SUT design
- Model robustness differs across architectures
