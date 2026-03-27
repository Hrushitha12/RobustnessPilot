# LLM Robustness Test Diversity Analysis
## TeaStore SUT — Failure Mode Coverage Report

**Study:** Individual Study — LLM-Generated Robustness Test Evaluation  
**Student:** Hrushitha Tigulla | **Supervisor:** Prof. Marco Vieira | **University:** UNC Charlotte  
**Status:** Analysis complete — 14 runs, 9 verified failure modes, 10 Venn diagrams

---

## 1. Background and Setup

### What Was Tested

The System Under Test (SUT) is **TeaStore** — a distributed microservice fake tea shop running across 6 Docker containers, accessed via HTTP at `localhost:8080`. Robustness tests were sent to 7 endpoints:

| Endpoint | Type |
|---|---|
| `GET /category?id=` | Retrieve category by ID |
| `GET /product?id=` | Retrieve product by ID |
| `GET /cart` | View cart |
| `GET /profile` | View profile |
| `POST /loginAction` | Login with username + password |
| `POST /cartAction` | Add or remove product from cart |
| `POST /order` | Confirm order |

**Oracle:** HTTP response < 500 = PASS. HTTP response ≥ 500 = FAIL (classified as *Abort* in the CRASH taxonomy).

---

### Models and Prompts

Three large language models were tested, each with up to 5 prompt strategies:

| Model ID | Model Name | Size |
|---|---|---|
| Model C | qwen3:14b | 14B |
| Model D | qwen2.5-coder:32b | 32B |
| Model E | llama3.1:70b | 70B |

| Prompt | Description |
|---|---|
| **ZeroShot** | Minimal instruction, no examples |
| **Structured** | Pre-built violation table (endpoint × violation × expected result) |
| **FewShot** | 3–4 working example tests shown, then ask for more |
| **Self** | Draft → self-critique → rewrite (3-step) |
| **CoT** | Chain-of-thought: reason through violation categories step by step |

> **Note:** C-ZeroShot has no results — qwen3:14b switched to Chinese mid-generation without few-shot grounding and was excluded.  
> **v2 prompts** added the line: *"Each test method must declare throws Exception."* This raised the compile rate from 40% → 89%.

---

### The 9 Verified Failure Modes

All 9 failure modes were verified by inspecting the actual Java test source code — not test names alone.

| FM | Description | Exact HTTP Request |
|---|---|---|
| **FM1** | product — non-numeric string id | `GET /product?id=abc` |
| **FM2** | product — empty string id | `GET /product?id=` |
| **FM3** | cartAction add — productid key **absent entirely** | `POST body: addToCart=` |
| **FM4** | cartAction add — productid key present, **value empty** | `POST body: addToCart=&productid=` |
| **FM5** | cartAction add — invalid string / oversized productid | `POST body: addToCart=&productid=abc` |
| **FM6** | cartAction remove — productid key **absent entirely** | `POST body: removeProduct=` |
| **FM7** | cartAction remove — productid key present, **value empty** | `POST body: removeProduct=&productid=` |
| **FM8** | cartAction remove — invalid/non-existent productid | `POST body: removeProduct=&productid=999999` |
| **FM9** | cartAction remove — valid id but **unauthenticated** | `POST body: removeProduct=&productid=1` |

> **Critical distinction:** FM3 ≠ FM4 and FM6 ≠ FM7. In FM3/FM6 the `productid` key is completely absent from the HTTP body. In FM4/FM7 the key is present but the value is an empty string. These are different HTTP requests and were triggered by different tests.

---

### Binary Coverage Matrix (Ground Truth)

This is the master reference table. All Venn diagrams are derived from it.

| Failure Mode | C-Few | C-Self | C-Str | C-CoT | D-Few | D-Str | D-CoT | D-Zero | D-Self | E-Few | E-Self | E-Str | E-Zero | E-CoT | Found in N runs |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| FM1 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ | ✅ | **10/14** |
| FM2 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | **2/14** |
| FM3 | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ | **4/14** |
| FM4 | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | **3/14** |
| FM5 | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ | **4/14** |
| FM6 | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | **1/14** |
| FM7 | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | **3/14** |
| FM8 | ✅ | ✅ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ | **6/14** |
| FM9 | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | **3/14** |
| **Total FMs** | **5** | **4** | **2** | **4** | **3** | **2** | **3** | **0** | **0** | **3** | **3** | **2** | **1** | **4** | — |

> **Note on D-CoT:** D-CoT's verified FM set is {FM1, FM2, FM3}. However, D-CoT also generated two additional tests — an XSS payload and a whitespace-only string on the product endpoint — that could not be executed due to Java HTTP client URI validation. These are classified as **Excluded** and do not appear in the FM matrix. See **Section 7: Emergent Behavior Observation** for full details. This is the most notable secondary finding in the study.

---

Jaccard similarity measures how much two runs **overlap** in the failure modes they found.

```
Jaccard(A, B) = |A ∩ B| / |A ∪ B|
```

- **1.00** = identical FM sets  
- **0.00** = no shared FMs at all  
- Higher = more similar, less diverse

### Key Jaccard Values at a Glance

| Pair | Jaccard | Interpretation |
|---|---|---|
| C-Struct ↔ D-Struct ↔ E-Struct | **1.00** | All three identical — FM1 + FM8 only |
| D-ZeroShot ↔ D-Self | **1.00** | Both empty — trivially identical |
| E-Self ↔ E-CoT | **0.75** | Highest non-Structured similarity |
| D-FewShot ↔ E-FewShot | **0.00** | Same prompt, zero shared failures |
| FewShot avg (C↔D↔E) | **0.16** | Lowest cross-model average |

---

## 3. Venn Diagrams — Section 1: Same Prompt, Different Models

This section answers: *"If you give the same prompt to three different models, do they find the same failures?"*

---

### Diagram 1a — FewShot: Model C vs D vs E

![FewShot C vs D vs E](1a_FewShot_CvsDvsE.png)

**FM sets:**
- C-FewShot: {FM1, FM4, FM5, FM7, FM8}
- D-FewShot: {FM1, FM3, FM6}
- E-FewShot: {FM2, FM4, FM8}

**What the diagram shows:**

- **FM1 only** sits in the C∩D intersection — the only failure mode C and D share.
- **FM4 and FM8** sit in the C∩E intersection — C and E share these two cart action failures.
- **FM3 and FM6** are D-only — Model D uniquely explored the "key absent entirely" variants of cartAction.
- **FM2** is E-only — only Model E generated a test for `product?id=` (empty string).
- **FM5 and FM7** are C-only — only Model C generated tests for invalid string productid and empty remove.
- The D∩E intersection and the three-way C∩D∩E intersection are **both empty (∅)**.

**Jaccard values:**
- C↔D = 0.14 (share only FM1 out of 7 total)
- C↔E = 0.33 (share FM4, FM8 out of 6 total)
- D↔E = **0.00** (zero shared failures despite identical prompt)

**Conclusion:** FewShot produces the **highest inter-model diversity** of any prompt type. The same set of worked examples leads three models to explore completely different parts of the cartAction failure space. Model D focused on structural request violations (missing keys), Model C focused on value-level violations (empty/invalid values), and Model E focused on a mix of product endpoint and cart failures. The worked examples anchor format but not exploration direction.

---

### Diagram 1b — Structured: Model C vs D vs E

![Structured C vs D vs E](1b_Structured_CvsDvsE.png)

**FM sets:**
- C-Struct: {FM1, FM8}
- D-Struct: {FM1, FM8}
- E-Struct: {FM1, FM8}

**What the diagram shows:**

All three circles overlap completely in the centre — FM1 and FM8. Every other region is ∅. The diagram is the visual definition of zero diversity: three models, one shared outcome.

**Jaccard values:**
- C↔D = C↔E = D↔E = **1.00**

**Conclusion:** The violation table in the Structured prompt is both a strength and a ceiling. It guaranteed that all three models — regardless of architecture or size — would produce compilable, targeted tests that find FM1 (`product?id=abc`) and FM8 (`removeProduct=&productid=999999`). But because the table defined exactly those rows, the models had no reason to explore beyond them. This is the most dramatic result in the entire study: **prompt structure eliminated model-level variation entirely.** A Structured prompt is ideal when you need reproducible, predictable coverage of known violations, and a poor choice when you want to discover unknown ones.

---

### Diagram 1c — CoT: Model C vs D vs E

![CoT C vs D vs E](1c_CoT_CvsDvsE.png)

**FM sets:**
- C-CoT: {FM1, FM4, FM7, FM9}
- D-CoT: {FM1, FM2, FM3}
- E-CoT: {FM1, FM3, FM5, FM7}

**What the diagram shows:**

- **FM1** appears in all three — the only three-way intersection.
- **FM7** sits in the C∩E region — both C and E reasoned their way to the "remove with empty productid value" failure.
- **FM3** sits in the D∩E region — D and E both found the "add to cart with no productid key" failure.
- **FM4 and FM9** are C-only — Model C's reasoning led it to empty-value add and unauthenticated remove.
- **FM2** is D-only — only Model D's chain-of-thought generated `product?id=` (empty string).
- **FM5** is E-only — only Model E found the invalid string productid via add.
- C∩D is empty — no failures shared exclusively between C and D.

**Jaccard values:**
- C↔D = 0.17
- C↔E = 0.33
- D↔E = 0.40

**Conclusion:** CoT produces **moderate inter-model diversity** (avg = 0.30). Reasoning through violation categories step-by-step led each model to think about similar *categories* of failures (all three found FM1; two pairs share FM3 or FM7), but their specific conclusions diverged. This suggests that chain-of-thought prompting partially aligns reasoning paths without fully converging them. CoT is a good middle ground: better coverage diversity than Structured, more systematic than ZeroShot.

---

### Diagram 1d — Self: Model C vs E (D-Self = ∅)

![Self C vs D vs E](1d_Self_CvsDvsE.png)

**FM sets:**
- C-Self: {FM1, FM5, FM8, FM9}
- D-Self: ∅ (no failures — generated only safe inputs)
- E-Self: {FM1, FM3, FM5}

**What the diagram shows:**

Since D-Self produced zero failures, only C and E are shown in the Venn. The right panel documents D-Self's empty set explicitly.

- **FM1 and FM5** are shared between C and E.
- **FM8 and FM9** are C-only — only Model C's self-critique process produced tests for invalid remove productid and unauthenticated remove.
- **FM3** is E-only — Model E's self-critique led it to test for missing productid key.

**Jaccard values:**
- C↔E = 0.33
- C↔D = 0.00 (D-Self = ∅)
- D↔E = 0.00 (D-Self = ∅)

**Conclusion:** The Self prompt produces **the most model-dependent results** of any prompt type. D-Self (code-tuned 32B model) self-critiqued its way into only generating safe, well-formed inputs — the critique process reinforced conservative code generation rather than adversarial exploration. C and E, being general-purpose models, used self-critique to identify boundary cases. This finding shows that a model's base training bias overrides prompt strategy when given an open-ended reflective task with no explicit violation framing.

---

### Diagram 1e — ZeroShot: Model D vs E (C-ZeroShot not run)

![ZeroShot D vs E](1e_ZeroShot_DvsE.png)

**FM sets:**
- C-ZeroShot: NOT RUN (qwen3:14b switched to Chinese mid-generation without grounding examples)
- D-ZeroShot: ∅ (no failures — generated only safe inputs)
- E-ZeroShot: {FM9}

**What the diagram shows:**

D-ZeroShot is empty. E-ZeroShot found one failure — FM9 (authenticated remove with valid productid). Their intersection is ∅.

**Jaccard value:**
- D↔E = **0.00**

**Conclusion:** ZeroShot is the weakest prompt strategy for finding failures. Without any examples or explicit reasoning instructions, Model D (code-tuned) generated textbook-correct inputs that exercised the API nominally. Model E found one failure, but FM9 is arguably the easiest failure to stumble upon without adversarial intent (it is a valid input sent without authentication). The C-ZeroShot failure-to-generate is itself a finding: **qwen3:14b requires output-format grounding to produce stable English Java code at zero-shot.** Without examples, it falls back to its training language (Chinese).

---

## 4. Venn Diagrams — Section 2: Same Model, Different Prompts

This section answers: *"If you use the same model but change the prompt, does it find different failures?"*

---

### Diagram 2a — Model C: All Pairwise Prompt Comparisons

![Model C All Prompt Pairs](2a_ModelC_AllPromptPairs.png)

**Model C FM sets:**
- C-FewShot: {FM1, FM4, FM5, FM7, FM8}
- C-Self: {FM1, FM5, FM8, FM9}
- C-Struct: {FM1, FM8}
- C-CoT: {FM1, FM4, FM7, FM9}

**Pair-by-pair analysis:**

| Pair | Shared | A-only | B-only | Jaccard |
|---|---|---|---|---|
| FewShot vs Self | FM1, FM5, FM8 | FM4, FM7 | FM9 | **0.50** |
| FewShot vs Struct | FM1, FM8 | FM4, FM5, FM7 | ∅ | **0.40** |
| FewShot vs CoT | FM1, FM4, FM7 | FM5, FM8 | FM9 | **0.50** |
| Self vs Struct | FM1, FM8 | FM5, FM9 | ∅ | **0.50** |
| Self vs CoT | FM1, FM9 | FM5, FM8 | FM4, FM7 | **0.33** |
| Struct vs CoT | FM1 | FM8 | FM4, FM7, FM9 | **0.20** |

**Notable observations:**
- FM1 appears in **all four** of Model C's prompts — it is a universal constant for Model C regardless of strategy.
- Structured is a strict subset of FewShot — everything Structured finds (FM1, FM8), FewShot also finds, plus 3 more.
- Self and FewShot share the most (FM1, FM5, FM8) — Jaccard = 0.50.
- CoT and Struct share the least (FM1 only) — Jaccard = 0.20.

**Conclusion:** Model C shows **moderate within-model diversity** (avg Jaccard across all pairs = 0.41). It is the most consistently explorative model — even its Structured run only found the 2 expected FMs while the other three prompts each found 4–5. The FewShot prompt brought out Model C's maximum breadth (5 FMs), the highest of any single run in the entire study.

---

### Diagram 2b — Model D: FewShot vs Struct vs CoT (ZeroShot and Self = ∅)

![Model D Prompts](2b_ModelD_Prompts.png)

**Model D FM sets:**
- D-FewShot: {FM1, FM3, FM6}
- D-Struct: {FM1, FM8}
- D-CoT: {FM1, FM2, FM3}
- D-ZeroShot: ∅
- D-Self: ∅

**Three-way analysis (non-empty runs):**

| Region | FMs |
|---|---|
| D-FewShot only | FM6 |
| D-Struct only | FM8 |
| D-CoT only | FM2 |
| FewShot ∩ CoT | FM3 |
| All three | FM1 |
| FewShot ∩ Struct | ∅ |
| Struct ∩ CoT | ∅ |

**Notable observations:**
- FM1 is the only failure found by all three non-empty prompts.
- FM3 (cartAction add, key absent) is shared only by FewShot and CoT — both required either examples or reasoning steps to reach this structural violation.
- FM6 (cartAction remove, key absent) is found **only by D-FewShot** and by no other run across the entire study except D-FewShot. It is the rarest FM in the dataset.
- D-ZeroShot and D-Self both = ∅. This makes Model D the only model where the majority of prompts (3 out of 5) produced no adversarial inputs at all.

**Conclusion:** Model D (qwen2.5-coder:32b) is a **code-completion specialist** that defaults to generating clean, correct, API-respecting inputs. Without explicit violation guidance (Structured) or worked examples (FewShot) or forced reasoning (CoT), it will generate test harnesses that simply exercise the happy path. Its highest within-model diversity is between FewShot and Struct (FM6 vs FM8, no overlap besides FM1). FM6's uniqueness to D-FewShot is the most surprising isolated result in the study — no other prompt strategy on any model discovered the missing-key remove variant.

---

### Diagram 2c — Model E: All Pairwise Prompt Comparisons

![Model E All Prompt Pairs](2c_ModelE_AllPromptPairs.png)

**Model E FM sets:**
- E-FewShot: {FM2, FM4, FM8}
- E-Self: {FM1, FM3, FM5}
- E-Struct: {FM1, FM8}
- E-ZeroShot: {FM9}
- E-CoT: {FM1, FM3, FM5, FM7}

**Pair-by-pair analysis (excluding ZeroShot, shown separately in 2d):**

| Pair | Shared | A-only | B-only | Jaccard |
|---|---|---|---|---|
| FewShot vs Self | ∅ | FM2, FM4, FM8 | FM1, FM3, FM5 | **0.00** |
| FewShot vs Struct | FM8 | FM2, FM4 | FM1 | **0.25** |
| FewShot vs CoT | ∅ | FM2, FM4, FM8 | FM1, FM3, FM5, FM7 | **0.00** |
| Self vs Struct | FM1 | FM3, FM5 | FM8 | **0.20** |
| Self vs CoT | FM1, FM3, FM5 | ∅ | FM7 | **0.75** |
| Struct vs CoT | FM1 | FM8 | FM3, FM5, FM7 | **0.20** |

**Notable observations:**
- E-Self = {FM1, FM3, FM5} is a **strict subset** of E-CoT = {FM1, FM3, FM5, FM7}. Jaccard = 0.75, the highest non-Structured similarity in the entire dataset.
- E-FewShot shares **zero FMs** with E-Self (Jaccard = 0.00) and **zero FMs** with E-CoT (Jaccard = 0.00). FewShot explored FM2/FM4/FM8 exclusively; Self and CoT explored FM1/FM3/FM5/FM7 exclusively.
- E-FewShot found FM2 and FM4 — failures that Model E's reasoning-based prompts never found.

**Conclusion:** Model E shows **the highest within-model diversity** (avg Jaccard = 0.15), driven by the complete disjointness between FewShot and the two reasoning prompts. This reveals a deep split in how Model E responds to different prompt types: **examples activate product-and-cart-removal-focused testing, while reasoning activates add-focused and structure-focused testing.** The E-Self ⊂ E-CoT relationship suggests that adding chain-of-thought on top of self-critique produces a strict superset — CoT found everything Self found and also discovered FM7.

---

### Diagram 2d — Model E: ZeroShot vs Each Other Prompt

![Model E ZeroShot vs Others](2d_ModelE_ZeroShotVsOthers.png)

**E-ZeroShot = {FM9}**

| Pair | ZeroShot shares with | Jaccard |
|---|---|---|
| ZeroShot vs FewShot | ∅ | 0.00 |
| ZeroShot vs Self | ∅ | 0.00 |
| ZeroShot vs Struct | ∅ | 0.00 |
| ZeroShot vs CoT | ∅ | 0.00 |

**What the diagram shows:**

FM9 sits in the ZeroShot-only region in all four sub-diagrams. No other Model E prompt found FM9.

**Conclusion:** FM9 (unauthenticated remove with a valid productid) is a **failure mode unique to E-ZeroShot** within Model E. This is counterintuitive — one might expect ZeroShot to be the weakest prompt. The explanation is that without any violation table or examples to guide it toward specific input types, Model E included a legitimate-but-unauthenticated request in its default exploration, which happened to trigger a 500. The other prompts were directed toward specific parameter-structure violations and never tested authentication state. This shows that **ZeroShot, despite its low total coverage, can find failures that guided prompts miss** because it explores different parts of the input space entirely.

---

## 5. Venn Diagram — Section 3: Overview Per Model (Best 3 Prompts)

### Diagram 3 — FewShot + CoT + Self per Model

![Overview Per Model](3_OverviewPerModel.png)

This diagram compares the three most expressive prompt types (FewShot, CoT, Self) across all three models side by side.

---

**Model C — FewShot + CoT + Self:**

| Region | FMs |
|---|---|
| FewShot only | ∅ |
| CoT only | ∅ |
| Self only | ∅ |
| FewShot ∩ CoT | FM4, FM7 |
| FewShot ∩ Self | FM5, FM8 |
| CoT ∩ Self | FM9 |
| All three | FM1 |

Model C shows the **most balanced three-way coverage** — FM1 is the only FM in the true centre, but the pairwise intersections are rich. Every FM is shared by at least two prompts, and the FewShot circle encompasses the broadest total coverage. Model C's prompts are complementary rather than redundant.

---

**Model D — FewShot + CoT + Struct (Self and ZeroShot = ∅):**

| Region | FMs |
|---|---|
| FewShot only | FM6 |
| Struct only | FM8 |
| CoT only | FM2 |
| FewShot ∩ CoT | FM3 |
| FewShot ∩ Struct | ∅ |
| CoT ∩ Struct | ∅ |
| All three | FM1 |

Model D has a **spoke pattern** — three prompts each find largely distinct FMs, converging only at FM1. This means for Model D, each non-trivial prompt strategy discovers something the others don't. The ensemble of FewShot + CoT + Struct covers 5 unique FMs from Model D alone.

---

**Model E — FewShot + CoT + Self:**

| Region | FMs |
|---|---|
| FewShot only | FM2, FM4, FM8 |
| CoT only | FM7 |
| Self only | ∅ |
| FewShot ∩ CoT | ∅ |
| FewShot ∩ Self | ∅ |
| CoT ∩ Self | FM3, FM5 |
| All three | FM1 |

Model E shows the **most extreme separation** — FewShot finds failures that CoT and Self never find, while CoT and Self share failures that FewShot never finds. The only common ground across all three is FM1. This confirms the split observed in 2c: examples and reasoning activate different test-generation modes in llama3.1:70b.

---

**Overall cross-model observation from Diagram 3:**

Across all three model panels, FM1 (`product?id=abc`) is the universal constant — it appears in the three-way intersection for all three models' best prompts. No other FM is universally found. FM6 only appears in Model D's FewShot panel and nowhere else in the entire diagram — it is the rarest FM and was only reached by Model D's specific response to the FewShot examples.

---

## 6. Summary of Key Findings

### Finding 1 — Structured prompt collapses diversity

All 3 models on Structured v2 found **exactly FM1 and FM8** (Jaccard = 1.00 for all pairs). The violation table constrained exploration to precisely what was specified. High compile rate, zero diversity. This is the clearest evidence that prompt structure can fully override model-level variation.

> *Implication: Use Structured prompts when you need deterministic, reproducible coverage of known violations. Avoid them when you want to discover new failure modes.*

---

### Finding 2 — FewShot maximises inter-model diversity

Same FewShot prompt on 3 models: avg Jaccard = 0.16. D-FewShot ↔ E-FewShot Jaccard = **0.00** — identical prompt, zero shared failures. Worked examples anchor the test format but leave each model free to explore different parts of the input space. FewShot is the best prompt strategy for ensemble diversity.

> *Implication: If you want an ensemble that covers the most FMs with the fewest runs, use FewShot across all available models.*

---

### Finding 3 — Prompt type explains more variance than model size

Within-model diversity (same model, different prompts):

| Model | Avg Jaccard (within-model) |
|---|---|
| Model C | 0.41 |
| Model D | 0.20 |
| Model E | 0.15 |

Across-model on same prompt:

| Prompt | Avg Jaccard (cross-model) |
|---|---|
| Structured | **1.00** |
| FewShot | 0.16 |
| CoT | 0.30 |

The Structured result alone proves this: changing from a 14B to a 70B model made no difference at all when the prompt was the same. Changing the prompt (e.g., from Structured to FewShot on the same model) produced far more variation than changing the model.

> *Implication: Prompt engineering effort yields higher returns than model selection for failure mode diversity.*

---

### Finding 4 — Model D (code-tuned) generates safe inputs by default

D-ZeroShot and D-Self both produced **0 failures** across all tests. qwen2.5-coder:32b is trained to generate correct, idiomatic code — it defaults to valid API calls. Only when explicitly given violation examples (FewShot), a violation table (Structured), or a reasoning chain (CoT) does it produce adversarial inputs. The Self prompt's reflection loop reinforced conservative generation rather than adversarial exploration.

> *Implication: Code-specialised models require strong violation framing to function as robustness test generators.*

---

### Finding 5 — No single run covers all 9 FMs

| Coverage | Details |
|---|---|
| Max FMs in one run | **5** (C-FewShot only) |
| FM6 | Found by **1 run only** (D-FewShot) |
| FM2 | Found by **2 runs only** (D-CoT, E-FewShot) |
| FM9 | Found by 3 runs (C-Self, C-CoT, E-ZeroShot) |

To cover all 9 FMs, a minimum ensemble of **C-FewShot + D-FewShot + D-CoT** is required (covers FM1–9). A practical ensemble recommendation: **FewShot + CoT** across at least 2 models.

> *Implication: No single prompt strategy or model is sufficient. Ensembles are necessary for comprehensive robustness coverage.*

---

### Finding 6 — E-Self ⊂ E-CoT: reasoning prompts converge on same model

E-Self = {FM1, FM3, FM5} is a strict subset of E-CoT = {FM1, FM3, FM5, FM7}. Jaccard = **0.75** — the highest similarity between any two non-Structured runs. Both are reasoning-heavy prompts applied to the same model. CoT found everything Self found and additionally discovered FM7.

> *Implication: On the same model, adding chain-of-thought on top of self-critique produces a strict superset. CoT dominates Self for Model E.*

---

### Finding 7 — ZeroShot can find FM9 that guided prompts miss

E-ZeroShot = {FM9} shares zero FMs with all other E prompts. FM9 (valid productid, no session) was stumbled upon by unconstrained exploration but never targeted by examples or a violation table. This represents a **discovery mode** — ZeroShot covers input territory that structured strategies ignore because they focus on parameter-level violations rather than auth-state violations.

> *Implication: Even weak prompts have exploratory value. A fully guided ensemble should include at least one unguided run.*

---

## 7. Emergent Behavior Observation — Model D CoT v2

> **This section is separate from the diversity analysis.** The two inputs described here are classified as **Excluded** in the CRASH taxonomy and do not appear in the binary matrix or Jaccard calculations. They are documented here as a secondary finding about model generative capability.

---

### What Happened

During the D-CoT v2 run, two tests failed with `java.lang.IllegalArgumentException` — thrown by the **Java HTTP client itself**, before any request reached TeaStore:

| Test Method | Input Sent | Exception | Crash Class |
|---|---|---|---|
| `test_R1_XssAttackInProductId` | `GET /product?id=<script>alert('xss')</script>` | `IllegalArgumentException` | **Excluded** |
| `test_R1_WhitespaceOnlyProductId` | `GET /product?id=   ` | `IllegalArgumentException` | **Excluded** |

**Why Excluded:** The characters `<`, `>`, `'` in the XSS payload are **illegal in a URI** by RFC 3986. Java's `HttpClient` rejects the URI at construction time and throws before sending anything. Whitespace-only query values trigger the same validation. TeaStore's response to these inputs is **unknown** — it was never asked.

Because the SUT was never reached, these cannot be classified as Abort (HTTP 500) and cannot contribute to FM discovery. The oracle requires a confirmed HTTP round-trip.

---

### Why It Matters

Despite being unexecutable by the harness, the fact that D-CoT *generated* these inputs is significant:

**Model D (qwen2.5-coder:32b) using CoT v2 was the only model and prompt combination in the entire study that autonomously produced security-oriented attack payloads** — XSS injection and whitespace attacks targeting the product endpoint — **without any security-specific instruction in the prompt.**

Compare this to every other run:
- No other model generated XSS payloads in any form
- No other prompt strategy on Model D produced security-oriented inputs
- The CoT prompt asked for step-by-step reasoning through *robustness violation categories* — security injection is not explicitly named as a category

This is **emergent security-aware test generation**: the CoT reasoning chain led Model D to independently reason that untrusted user-controlled input flowing into a URL parameter is a candidate for injection attacks. The 32B code-tuned model, when given explicit reasoning scaffolding, applied security intuition that the 70B general model never did.

---

### Why It Cannot Be Retrospectively Fixed

One might ask: *"Can we URL-encode the XSS payload (`%3Cscript%3E...`) and re-run the test to get a proper HTTP result?"*

Technically yes — but it would not be a fair comparison to the other 13 runs. The other FM sets were all produced from a single consistent test execution cycle. Re-running only D-CoT with a modified harness would create an asymmetry in the data. The FM set {FM1, FM2, FM3} for D-CoT is the correct, verified, comparable result.

The XSS and whitespace results belong in a capability observation, not the FM matrix.

---

### What This Tells Us About CoT on Code-Tuned Models

| Prompt | Model D FM Set | Notable Behaviour |
|---|---|---|
| ZeroShot | ∅ | Safe inputs only — no adversarial intent |
| Self | ∅ | Self-critique reinforced conservative generation |
| Structured | {FM1, FM8} | Followed the violation table exactly |
| FewShot | {FM1, FM3, FM6} | Examples guided structural violations |
| **CoT** | **{FM1, FM2, FM3}** | **+ generated XSS and whitespace (unexecutable)** |

CoT is the only prompt that caused Model D to reason beyond the literal input space of the SUT and into security threat modelling. The chain-of-thought scaffolding activated a different mode of test generation — one that a code-tuned model reaches when asked to *reason through violation categories* rather than imitate examples or follow a table.

---

### Recommendation for Future Work

To properly evaluate whether TeaStore is vulnerable to XSS payloads via the product endpoint, the test harness would need to URL-encode special characters before constructing the HTTP request, or use a harness that handles raw byte injection. This is out of scope for the current study but is a direct research lead produced by D-CoT's generation behaviour.

> **Summary:** D-CoT's XSS and whitespace generation is excluded from diversity analysis (Excluded crash class, SUT unreachable) but stands as the strongest evidence in this study that chain-of-thought prompting on a code-specialised model can produce security-aware test generation without explicit security prompting.

---

## 8. Coverage Gap Analysis

FMs ranked by how hard they were to find:

| FM | Found by N runs | Which prompts reliably find it |
|---|---|---|
| FM1 | 10/14 | Almost all except ZeroShot and D/E-Self |
| FM8 | 6/14 | Structured (all), FewShot (C, E), Self (C) |
| FM3 | 4/14 | FewShot (D), CoT (D, E), Self (E) |
| FM5 | 4/14 | FewShot (C), Self (C, E), CoT (E) |
| FM4 | 3/14 | FewShot (C, E), CoT (C) |
| FM7 | 3/14 | FewShot (C), CoT (C, E) |
| FM9 | 3/14 | Self (C), CoT (C), ZeroShot (E) |
| FM2 | 2/14 | CoT (D), FewShot (E) |
| **FM6** | **1/14** | **D-FewShot only** |

FM6 is the most critical coverage gap. It requires a code-tuned model (D) responding to worked examples (FewShot) to produce the `removeProduct=` body without any `productid` key. No reasoning prompt, no structured table, and no self-critique ever reached it.

---

## 9. Limitations

1. **C-ZeroShot not run** — qwen3:14b's language instability without grounding examples means the ZeroShot cross-model comparison is incomplete. This is documented as a finding rather than a gap to be filled.

2. **Binary oracle only** — HTTP ≥ 500 detects Abort-class failures. Silent failures (wrong data returned silently) and Hindering failures (performance degradation) are not detectable with the current oracle.

3. **Single SUT** — All findings are from TeaStore only. Whether they generalise to other microservice architectures is an open question.

4. **Catastrophic class resolved** — 5 Catastrophic entries from D-Self Round 1 (TeaStore crash) were replaced by the clean rerun file. The original crash was non-reproducible (transient system state), confirmed by a full clean restart and rerun.

5. **D-Self and D-ZeroShot Jaccard edge case** — Jaccard({}, {}) = 1.00 by the empty-set convention. Their similarity to any run with failures = 0.00. These values are mathematically correct but require explanation when presenting.

---

*All FM mappings verified from Java test source code in `sut_teastore/test_harness/src/test/java/com/example/`. Binary matrix verified cell-by-cell. Venn diagrams verified against binary matrix.*
