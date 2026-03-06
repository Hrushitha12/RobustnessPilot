# TeaStore Experiment Plan

## Goal

Extend the existing robustness-testing pipeline to TeaStore as a new SUT and run LLM-generated robustness tests using larger server-side models.

The TeaStore phase should answer:

- whether larger models generate more compilable robustness tests than the smaller local models
- whether a distributed microservice SUT changes failure patterns compared to earlier SUTs
- whether stronger models produce better system-level robustness reasoning for TeaStore

## Decision for This Phase

TeaStore will **not** be fully rerun first with the earlier small local models.

Reason:
- earlier small-model results on harder SUTs already showed limited compilation reliability
- the immediate research value is in extending the pipeline to TeaStore and testing larger models available on the university server
- small-model TeaStore runs can be added later if strict symmetry is required

## TeaStore Baseline Status

The manual TeaStore baseline has already been validated using the existing `run_suite.py` pipeline.

This confirms:
- TeaStore test harness works
- canary execution works
- result logging works
- the environment remains stable under manual robustness tests

Baseline results file:

```text
results/teastore_manual_r1.jsonl
```

## Target Models for Server Phase

The main TeaStore server runs should use:

- `qwen3:14b`
- `qwen2.5-coder:32b`
- `llama3.1:70b`

These give a progression from stronger medium-scale to larger models and align with the goal of moving closer to the larger-model setting discussed in TestForge-style evaluation.

## Prompt Types

TeaStore experiments will use five prompt types, consistent with the earlier study design:

1. zero-shot
2. structured
3. few-shot
4. self-check
5. chain-of-thought

## Repo Structure for TeaStore Prompting and Runs

Follow the existing repo organization.

Prompt folders should be saved under:

```text
prompts/
  model_a_teastore/
  model_b_teastore/
  model_c_teastore/
```

If model naming is updated later for larger server models, the same folder style should be preserved with clear model-specific directories.

Generated raw outputs should follow the same generations layout already used in the repo.

Recommended TeaStore run layout:

```text
generations/
  model_a/
    sut_teastore/
  model_b/
    sut_teastore/
  model_c/
    sut_teastore/

results/
  teastore_<model>_<prompt_type>.jsonl
```

## Experimental Protocol

For each model and prompt type:

1. generate TeaStore robustness test candidate(s)
2. save raw model output
3. extract Java test IDs / test class
4. run compile filter and cleanup
5. compile generated test(s)
6. execute generated robustness test(s)
7. run canary after each test
8. log results to JSONL
9. categorize failures

## Recommended Run Order

Do not launch the full matrix immediately.

### Phase 1 — Smoke Test
Run one stable prompt first:
- model: `qwen3:14b`
- prompt: structured

Purpose:
- verify generation format
- verify extraction
- verify compile filter
- verify TeaStore harness compatibility

### Phase 2 — Model Smoke Tests
Run the structured prompt for:
- `qwen2.5-coder:32b`
- `llama3.1:70b`

### Phase 3 — Full Prompt Sweep
If the structured prompt works, expand to:
- zero-shot
- few-shot
- self-check
- chain-of-thought

## Key Consistency Rules

The TeaStore phase should preserve the same evaluation assumptions as earlier SUTs:

- same compile-filter philosophy
- same canary-after-test strategy
- same JSONL logging style
- same failure interpretation
- same direct-oracle constraint where applicable
- no markdown code fences in final generated Java
- no `assertThrows` as the primary oracle if that remains part of the study design

## Deliverables for This Phase

The TeaStore server phase should produce:

- documented server setup notes
- documented baseline execution notes
- TeaStore prompt files
- raw generations for each model/prompt combination
- JSONL results for each run
- observations comparing TeaStore against earlier SUTs

## Next Immediate Step

The next immediate step is:

1. finalize TeaStore prompt files using the same style as the earlier SUT prompts
2. run one TeaStore smoke test on the server
3. verify compile + runtime + canary behavior
4. then scale to the full TeaStore large-model matrix
