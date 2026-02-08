# Robustness Pilot: Apache Commons Lang + Micro-SUT (Java, JUnit 5, Maven)

This repository contains a **small, focused robustness-testing pilot experiment** designed to (1) learn robustness testing properly and (2) create **clean experimental artifacts** you can later connect to **LLM-based test generation/benchmarking (TestForge)**.

We evaluate robustness using a **survivability oracle**:
- A robustness test is **PASS** if the system survives the malformed/stress input without crashing (it may still return a default or a safe value).
- A robustness test is **FAIL** if it triggers an exception or otherwise fails the test run.
- A **Canary test** runs immediately after each robustness test to detect **state corruption / poisoning**. Canary **PASS** indicates the system remains stable after the robustness test.

## Contents
- `sut_commons_lang/` — wrapper SUT around **Apache Commons Lang** targets
- `sut_micro/` — a small **micro-SUT** config parser (strict input validation)
- `tools/` — scripts to run robustness tests **one-by-one** with timeouts and JSONL logging
- `analysis/` — extracted test ID lists
- `results/` — JSONL logs produced by Step 5 (per-test outcomes + canary status)

---

## Requirements
- Windows PowerShell (tested)
- Java 17 (or compatible with `maven.compiler.release` in each `pom.xml`)
- Maven 3.x (`mvn` available in terminal)
- Python 3.10+ (for `tools/` runner scripts)

---

## Folder Structure (high-level)

```
robustness_pilot/
  sut_commons_lang/
    pom.xml
    src/main/java/pilot/commons/CommonsLangTargets.java
    src/test/java/pilot/commons/BaselineTests.java
    src/test/java/pilot/commons/RobustnessTests.java
    src/test/java/pilot/commons/CanaryTest.java
  sut_micro/
    pom.xml
    src/main/java/pilot/micro/ConfigParser.java
    src/main/java/pilot/micro/ParsedConfig.java
    src/test/java/pilot/micro/BaselineTests.java
    src/test/java/pilot/micro/RobustnessTests.java
    src/test/java/pilot/micro/CanaryTest.java
  tools/
    extract_test_ids.py
    run_single_test.py
    run_suite.py
  analysis/
    commons_robustness_tests.txt
    micro_robustness_tests.txt
  results/
    commons_lang.jsonl
    micro_sut.jsonl
```

---

## Experiment Steps (what we ran)

### Step 1–4: Build two SUTs with baseline + robustness + canary tests
- **Baseline tests**: normal behavior, should pass (control group)
- **Robustness tests**: malformed / extreme / semantic violations
- **Canary test**: executed after robustness tests to detect state corruption

### Step 5: Run robustness tests **one-by-one** and log results (Windows-safe)
We generate Surefire XML reports once, extract test IDs, then run each test in isolation.

#### 5.1 Generate Surefire XML for robustness suite (expected to fail)
Commons Lang:
```powershell
cd .\sut_commons_lang
mvn "-Dtest=RobustnessTests" test
cd ..
```

Micro-SUT:
```powershell
cd .\sut_micro
mvn "-Dtest=RobustnessTests" test
cd ..
```

#### 5.2 Extract test IDs from Surefire XML
From `robustness_pilot/`:
```powershell
python .\tools\extract_test_ids.py --project_dir .\sut_commons_lang --pattern "TEST-*RobustnessTests*.xml" --out .\analysis\commons_robustness_tests.txt
python .\tools\extract_test_ids.py --project_dir .\sut_micro --pattern "TEST-*RobustnessTests*.xml" --out .\analysis\micro_robustness_tests.txt
```

#### 5.3 Run suite with per-test timeout and canary (JSONL logging)
```powershell
python .\tools\run_suite.py --sut commons_lang --project_dir .\sut_commons_lang --tests_file .\analysis\commons_robustness_tests.txt --canary "pilot.commons.CanaryTest#canary_shouldAlwaysPass" --timeout_s 15 --out .\results\commons_lang.jsonl

python .\tools\run_suite.py --sut micro_sut --project_dir .\sut_micro --tests_file .\analysis\micro_robustness_tests.txt --canary "pilot.micro.CanaryTest#canary_shouldAlwaysPass" --timeout_s 15 --out .\results\micro_sut.jsonl
```

---

## Output Format (JSONL)
Each line in `results/*.jsonl` is one robustness test execution + the immediately-following canary result.

Key fields:
- `test_id`: `FullyQualifiedClass#method`
- `status`: `PASS | FAIL | TIMEOUT | RUNNER_ERROR`
- `exception_type`: extracted exception/error type (best-effort)
- `elapsed_ms`: wall-clock execution time
- `canary_status`: should be `PASS` if no poisoning
- `category`: derived from naming (`R1` structural vs `R2` semantic)

---

## Pilot Results (from the clean run)
- **Commons Lang**: 12 robustness tests; 10 PASS; 2 FAIL; Canary PASS for all
- **Micro-SUT**: 13 robustness tests; 2 PASS; 11 FAIL; Canary PASS for all

Interpretation:
- Commons Lang is defensive for many malformed inputs but throws on specific invalid numeric/parameter cases.
- Micro-SUT is strict-by-design: robustness is achieved via early rejection (controlled exceptions), not recovery.
- Canary PASS indicates failures are localized, not state-corrupting.

---

