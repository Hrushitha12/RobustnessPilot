# RobustnessPilot — LLM-Generated Robustness Test Evaluation

Individual Study under Prof. Marco Vieira.
Evaluating whether LLM-generated robustness tests discover new failure modes
beyond seed tests, across SUTs of varying complexity.

---



## Robustness Oracle

A test **PASS**es if the SUT survives the input without crashing.
A test **FAIL**s if it triggers an unhandled exception or HTTP 5xx response.
A **canary test** runs after each robustness test to detect state corruption.
If canary=FAIL, subsequent results from that run are marked SYSTEM_UNAVAILABLE.

---

## Model Registry

| ID      | Model Name              | Size  | Where       |
|---------|-------------------------|-------|-------------|
| model_a | qwen2.5-coder:7b        | 7b    | local       |
| model_b | llama3.1:8b             | 8b    | local       |
| model_c | qwen3:14b               | 14b   | server      |
| model_d | qwen2.5-coder:32b       | 32b   | server      |
| model_e | llama3.1:70b            | 70b   | server      |



---

## SUTs

| SUT            | Type                    | Language | Failure Surface                                     |
|----------------|-------------------------|----------|-----------------------------------------------------|
| micro_sut      | Config parser library   | Java     | Strict — almost all invalid inputs throw IAE        |
| commons_lang   | String/number utilities | Java     | Defensive — most nulls handled gracefully           |
| record_store   | Stateful key-value store| Java     | Rich — 5 exception types, state-based failures      |
| TeaStore       | Distributed microservice| Java/Web | HTTP endpoints — cart, product, login, order        |

---

## Prompt Types

| ID         | Description                                                   |
|------------|---------------------------------------------------------------|
| zeroshot   | No examples, minimal instructions                             |
| structured | Violation table provided (endpoint × parameter × violation)   |
| fewshot    | 2–3 worked examples of seed tests included in prompt          |
| self       | Model asked to critique and improve its own first draft       |
| cot        | Chain-of-thought: model reasons through violation categories  |

**v2 prompts** add one explicit line: "Each test method must declare throws Exception"
This was identified as the cause of all compile failures in round 1.

---

## Repository Structure

```
RobustnessPilot/
├── prompts/                        # LLM prompts per model per SUT
│   ├── model_a_sut_micro/
│   ├── model_a_commons_lang/
│   ├── model_a_record_store/
│   ├── model_a_teastore/
│   ├── model_b_.../
│   ├── model_c_teastore/           # includes v2 prompts (zeroshot_v2, structured_v2, cot_v2)
│   ├── model_d_teastore/
│   └── model_e_teastore/
│
├── generations/                    # Raw LLM outputs (raw_01.txt ... raw_08.txt per model/SUT)
│   ├── model_a/sut_micro/
│   ├── model_a/sut_commons_lang/
│   ├── model_a/sut_record_store/
│   ├── model_a/sut_teastore/
│   └── model_b/ model_c/ model_d/ model_e/ (same structure)
│
├── sut_micro/                      # Micro-SUT Maven project
├── sut_commons_lang/               # Commons Lang wrapper Maven project
├── sut_record_store/               # RecordStore Maven project
├── sut_teastore/                   # TeaStore SUT
│   ├── env/                        # Docker Compose environment scripts
│   │   ├── env_up.ps1 / env_up.sh
│   │   ├── env_down.ps1 / env_down.sh
│   │   └── env_ready.py
│   └── test_harness/               # Maven project for TeaStore JUnit tests
│
├── tools/                          # Pipeline scripts
│   ├── extract_test_ids.py         # Extract test IDs from Surefire XML
│   ├── run_suite.py                # Run tests one-by-one with canary + JSONL output
│   └── run_single_test.py          # Run a single test in isolation
│
├── analysis/                       # Extracted test ID lists per run
├── results/                        # JSONL result files per run
├── notes/                          # Experiment planning and session notes
├── observations.md                 # Main findings log (per model, per SUT)
└── README.md
```

---

## Experiment Status

### Phase 1 — Small Models, Small SUTs (model_a, model_b)

| SUT          | Model   | Rounds run | Compilable runs | Key finding                          |
|--------------|---------|------------|-----------------|--------------------------------------|
| micro_sut    | model_a | 5          | 4               | All failures = IAE, no new modes     |
| micro_sut    | model_b | 4          | 4               | TIMEOUT hang on multiEquals input    |
| commons_lang | model_a | 2 accepted | 2               | Oracle confusion dominant        |
| commons_lang | model_b | 2 accepted | 2               | Oracle confusion dominant            |
| record_store | model_a | 2 accepted | 2               | All 5 exception types replicated     |
| record_store | model_b | 3 accepted | 3               | 100% failure rate R3, hangs in R5    |

### Phase 2 — Large Models, TeaStore (model_c, model_d, model_e)

#### Round 1 — Original prompts

| Model   | Prompt     | Compile | Tests | PASS | FAIL | Notes                          |
|---------|------------|---------|-------|------|------|--------------------------------|
| model_c | zeroshot   | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |
| model_c | structured | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |
| model_c | fewshot    | PASS    | 18    | 13   | 5    |                                |
| model_c | self       | PASS    | 17    | 13   | 4    |                                |
| model_c | cot        | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |
| model_d | zeroshot   | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |
| model_d | structured | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |
| model_d | fewshot    | PASS    | 15    | 12   | 3    |                                |
| model_d | self       | PASS    | 14    | 4    | 0    | TeaStore crash mid-run         |
| model_d | cot        | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |
| model_e | zeroshot   | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |
| model_e | structured | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |
| model_e | fewshot    | PASS    | 14    | 11   | 3    |                                |
| model_e | self       | PASS    | 15    | 12   | 3    |                                |
| model_e | cot        | FAIL    | —     | —    | —    | MISSING_THROWS_DECLARATION     |

Round 1 compile rate: 6/15 (40%)
Root cause: all three failing prompt types omitted `throws Exception` on test methods.

#### Round 2 — v2 prompts (throws Exception fix applied to zeroshot, structured, cot)

| Model   | Prompt     | Compile | Tests | PASS | FAIL | Notes                                     |
|---------|------------|---------|-------|------|------|-------------------------------------------|
| model_c | zeroshot   | FAIL    | —     | —    | —    | MALFORMED_OUTPUT — Chinese text in output |
| model_c | structured | PASS    | 16    | 14   | 2    |                                           |
| model_c | cot        | PASS    | 18    | 14   | 4    | Numeric method names (test_R1_01...)      |
| model_d | zeroshot   | PASS    | 14    | 14   | 0    | All tests passed — safe inputs only       |
| model_d | structured | PASS    | 16    | 14   | 2    |                                           |
| model_d | cot        | PASS    | 14    | 9    | 5    | Found XSS and whitespace inputs → 500     |
| model_e | zeroshot   | PASS    | 18    | 16   | 2    |                                           |
| model_e | structured | PASS    | 16    | 14   | 2    |                                           |
| model_e | cot        | PASS    | 17    | 13   | 4    |                                           |

Round 2 compile rate: 8/9 (89%)
Only failure: model_c zeroshot — qwen3:14b switched to Chinese mid-output without few-shot grounding.

---

## Key Findings

### Finding 1 — Compile failures are prompt-driven, not model-driven
All three large models failed to compile on the same three prompt types
(zeroshot, structured, cot) for the same reason: missing `throws Exception`
declaration on test methods. A single added instruction fixed this across
all three models. Compile rate went from 40% to 89%.

### Finding 2 — LLMs discover new failure modes the seed missed
The manual seed found 1 failure mode in TeaStore (product endpoint, non-numeric id).
Across both rounds, LLM-generated tests discovered 11 new failure modes,
concentrated in cart action endpoints: missing productid, invalid productid,
empty productid, XSS payloads, whitespace-only inputs.

### Finding 3 — Hang behavior is a distinct failure class
On micro_sut, commons_lang, and record_store, LLM-generated inputs caused
the SUT to hang rather than throw an exception. The seed never found this
failure class. Notable: commons_lang substr(null) caused a 3.6-hour hang.

### Finding 4 — qwen3:14b outputs internal reasoning blocks
qwen3:14b produces `<think>...</think>` reasoning before the Java output
in all generations. Manual extraction was required. Without few-shot examples
to anchor the output format, it also switches to Chinese mid-generation.

### Finding 5 — TeaStore crash was transient, not deterministic
One run (model_d self, round 1) caused a mid-run TeaStore crash
(canary=FAIL from test 10). On rerun after a clean env restart, all 14
tests passed. The crash was not reproducible — likely caused by accumulated
system state from prior runs without environment restart.

---

## Running the Pipeline

### Java SUTs (micro, commons_lang, record_store)

```powershell
# Compile
mvn test-compile -f sut_<name>\pom.xml

# Run a generated test class
mvn test -Dtest="ClassName" -f sut_<name>\pom.xml

# Extract test IDs
python .\tools\extract_test_ids.py `
  --project_dir .\sut_<name> `
  --pattern "TEST-*ClassName*.xml" `
  --out .\analysis\<run_name>_tests.txt

# Run harness
python .\tools\run_suite.py `
  --sut <sut_name> `
  --project_dir .\sut_<name> `
  --tests_file .\analysis\<run_name>_tests.txt `
  --canary "<package>.CanaryTest#canary_shouldAlwaysPass" `
  --timeout_s 30 `
  --out .\results\<run_name>.jsonl
```

### TeaStore

```powershell
# Start environment
.\sut_teastore\env\env_up.ps1
python sut_teastore/env/env_ready.py --timeout 180

# Compile test harness
mvn test-compile -f sut_teastore\test_harness\pom.xml

# Run generated class
mvn test -Dtest="TeaStore_ModelX_PromptType_RobustnessTest" `
  -f sut_teastore\test_harness\pom.xml

# Extract IDs
python .\tools\extract_test_ids.py `
  --project_dir .\sut_teastore\test_harness `
  --pattern "TEST-*ClassName*.xml" `
  --out .\analysis\<run_name>_tests.txt

# Run harness
python .\tools\run_suite.py `
  --sut teastore `
  --project_dir .\sut_teastore\test_harness `
  --tests_file .\analysis\<run_name>_tests.txt `
  --canary "com.example.CanaryTest#test_canary" `
  --timeout_s 30 `
  --out .\results\<run_name>.jsonl

# Stop environment
.\sut_teastore\env\env_down.ps1
```

---

## File Naming Convention

### Generations
`generations/<model_id>/sut_<name>/raw_<N>.txt`
- raw_01 to raw_05 = round 1 (zeroshot, structured, fewshot, self, cot)
- raw_06 to raw_08 = round 2 v2 (zeroshot_v2, structured_v2, cot_v2)

### Java test classes (TeaStore)
`TeaStore_Model<X>_<PromptType>_RobustnessTest.java`
e.g. `TeaStore_ModelC_fewshot_RobustnessTest.java`
e.g. `TeaStore_ModelD_CoT_v2_RobustnessTest.java`

### Results
`results/<sut>_<model>_<prompt>.jsonl`
e.g. `results/teastore_modelC_fewshot.jsonl`
e.g. `results/teastore_modelD_structured_v2.jsonl`

---

## Requirements

- Windows PowerShell (TeaStore pipeline)
- Java 17
- Maven 3.x
- Python 3.10+
- Docker Desktop (for TeaStore)

---

## Observations Log

See `observations.md` for detailed per-model, per-SUT compile summaries,
runtime results, failure mode comparisons, and cross-model analysis.
