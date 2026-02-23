# Main Observations
## Compilation Reliability

On micro_sut, compilation success was high across both models.

On sut_commons_lang, compilation failures increased, especially due to:

Incorrect assertions

Instantiating private constructors

API misuse

This suggests that as SUT complexity increases, LLM structural reliability decreases.

## Runtime Behavior

On micro_sut, most failures collapse into a single exception type (IllegalArgumentException), limiting diversity.

On sut_commons_lang, we observed more varied runtime behaviors (e.g., NumberFormatException, assertion mismatches, null handling issues).

However, many failures were still oracle-related rather than discovering fundamentally new behavioral classes.

## Failure-Mode Diversity

micro_sut shows very low diversity — it is mostly fail-fast and stateless.

sut_commons_lang provides better diversity (string manipulation, numeric parsing, boundary indexing), but it is still stateless and utility-based.

We have not yet observed strong evidence that LLM-generated robustness tests significantly expand failure-mode diversity beyond what a careful human would likely write.

## Effectiveness of Generated Robustness Tests

Effectiveness depends on what we measure:

Compilation robustness: Moderate to good on simple SUT, weaker on more complex SUT.

Runtime robustness: Tests do trigger boundary and malformed inputs.

Diversity gain: Limited on micro_sut, moderate on commons_lang.

Systematic exploration: Prompt strategy influences structure but has not yet produced dramatically different behavioral coverage.

At this stage, the generated tests are syntactically viable and do explore malformed inputs, but the measurable increase in failure-mode diversity is not yet strong.