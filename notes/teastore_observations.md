## TeaStore — Model C (qwen3:14b) Results

Model C (qwen3:14b) compile rate: 2/5. Runtime pass rate across compiled tests: 26/35 (74%). Real bugs found: cart actions and product endpoint return 500 on invalid input.

### Compile Summary

| Prompt    | Raw File | Compile | Failure Reason                              |
|-----------|----------|---------|---------------------------------------------|
| ZeroShot  | raw_01   | FAIL    | MISSING_IMPORTS + MISSING_THROWS_DECLARATION |
| Structured| raw_02   | FAIL    | MISSING_THROWS_DECLARATION                  |
| FewShot   | raw_03   | PASS    | —                                           |
| Self      | raw_04   | PASS    | —                                           |
| CoT       | raw_05   | FAIL    | MISSING_THROWS_DECLARATION                  |

Compile rate: 2/5 (40%)

### Runtime Results

#### FewShot (raw_03)
Tests run: 18 | PASS: 13 | FAIL: 5
Failing tests triggered real TeaStore 500 errors:
- test_R1_product_non_numeric_id
- test_R1_cartAction_add_missing_productid
- test_R1_cartAction_remove_missing_productid
- test_R1_cartAction_add_invalid_productid
- test_R1_cartAction_remove_invalid_productid

#### Self (raw_04)
Tests run: 17 | PASS: 13 | FAIL: 4
Failing tests triggered real TeaStore 500 errors:
- test_R1_GetProductInvalidId
- test_R1_CartActionRemoveProductValid
- test_R1_CartActionRemoveProductInvalid
- test_R1_CartActionAddToCartInvalid

### Key Observations

1. qwen3:14b systematically omits `throws Exception` on test methods across
   ZeroShot, Structured, and CoT prompts. Only FewShot and Self prompts
   produced compilable output. This is a consistent model-level failure
   pattern, not prompt-specific.

2. qwen3:14b outputs extended <think>...</think> reasoning blocks before
   the Java class in all generations. This requires manual extraction and
   is a pipeline compatibility concern for automated test generation.

3. Both compilable generations (FewShot, Self) successfully identified
   real robustness weaknesses in TeaStore — specifically cart actions and
   product endpoints returning HTTP 500 on invalid input.

4. The Self prompt produced test method names that do not follow the
   test_R1_ naming convention strictly (e.g. test_R1_GetProductInvalidId
   uses PascalCase not snake_case). The class still compiled and ran.