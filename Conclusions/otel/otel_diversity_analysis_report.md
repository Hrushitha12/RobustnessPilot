# LLM Robustness Test Diversity Analysis
## OTel Astronomy Shop SUT — Failure Mode Coverage Report

**Study:** Individual Study — LLM-Generated Robustness Test Evaluation  
**Student:** Hrushitha Tigulla | **Supervisor:** Prof. Marco Vieira | **University:** UNC Charlotte  
**Status:** Analysis complete — 12 valid runs, 14 verified failure modes, 10 Venn diagrams

---

## 1. Background and Setup

### What Was Tested

The System Under Test (SUT) is the **OpenTelemetry Astronomy Shop** — a cloud-native polyglot microservice e-commerce application deployed via Docker Compose (minimal configuration), accessed via HTTP at `localhost:8080`. Robustness tests were sent to 6 testable endpoints through the Envoy frontend proxy:

| Endpoint | Method | Description |
|---|---|---|
| `/api/products` | GET | List all products |
| `/api/products/{id}` | GET | Get product by ID |
| `/api/cart` | GET / POST / DELETE | View, add to, or empty cart |
| `/api/checkout` | POST | Place an order |
| `/api/recommendations` | GET | Get product recommendations |
| `/api/ads` | GET | Get contextual ads |

> **Note on service availability:** Currency (`POST /api/currency`) and Shipping (`POST /api/shipping`) services return HTTP 504 for ALL requests in the minimal Docker Compose setup — including valid ones. This was confirmed by D-ZeroShot sending a well-formed USD→EUR request and receiving 504. These endpoints are classified as **SYSTEM_UNAVAILABLE** and excluded from the FM matrix entirely. They are not testable in the minimal compose configuration.

**Oracle:** HTTP response < 500 = PASS. HTTP response ≥ 500 = FAIL (classified as *Abort* in the CRASH taxonomy).  
HTTP 504 on currency/shipping = SYSTEM_UNAVAILABLE (not an input-triggered failure).

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
| **FewShot** | 6 worked example tests shown, then ask for more |
| **Self** | Draft → self-review checklist → rewrite (3-step) |
| **CoT** | Chain-of-thought: 9-step reasoning scaffold before writing code |

---

### Run Status (15 generated, 12 valid)

| Run | Status | Notes |
|---|---|---|
| **C-ZeroShot** | **COMPILE_FAIL** | `response` variable never assigned (18 errors). Genuine generation failure — not fixed. |
| C-Structured | ✅ Fixed + Ran | Missing `import java.net.http.HttpResponse` + class name mismatch. Fixed mechanically. |
| C-FewShot | ✅ Ran | No changes needed. |
| **C-Self** | **MALFORMED_OUTPUT** | Model produced markdown with embedded snippets. Helper methods redefined, wrong HTTP methods. Too many logic errors — not fixed. |
| C-CoT | ✅ Ran | 1 Excluded test (whitespace-only path segment). |
| D-ZeroShot | ✅ Ran | |
| D-Structured | ✅ Ran | Encoding issue resolved via Notepad UTF-8 save. |
| D-FewShot | ✅ Ran | |
| D-Self | ✅ Ran | 0 FMs — model did not call `assertNoServerError()`; all tests trivially passed. |
| D-CoT | ✅ Ran | 1 Excluded test (whitespace path). |
| E-ZeroShot | ✅ Fixed + Ran | Static import of non-existent `assertNoServerError` from `Assertions` removed. |
| **E-Structured** | **CRASH × 2** | Canary failed after test_R3 and test_R16. Docker restart + clean rerun also crashed. Sequential currency 504 timeouts destabilized the system. Unusable. |
| E-FewShot | ✅ Fixed + Ran | 2 Excluded tests (XSS payload in sessionId; special chars in sessionId). |
| E-Self | ✅ Fixed + Ran | |
| E-CoT | ✅ Fixed + Ran | 2 Excluded tests (whitespace path; leading space in product ID path). |

---

### The 14 Verified Failure Modes

All 14 failure modes were verified by inspecting the actual Java test source code — not test names alone.

| FM | Endpoint | Input / Scenario | HTTP |
|---|---|---|---|
| **FM1** | GET /api/products/{id} | Non-existent or invalid format ID | 500 |
| **FM2** | GET /api/products/{id} | Extremely long ID (500+ characters) | 500 |
| **FM3** | POST /api/cart | `item` field is `null` in body | 500 |
| **FM4** | POST /api/cart | `item` / fields missing entirely from body | 500 |
| **FM5** | POST /api/checkout | Empty `userId` | 500 |
| **FM6** | POST /api/checkout | Unknown/invalid currency code in `userCurrency` | 500 |
| **FM7** | POST /api/checkout | Empty or missing `creditCardNumber` | 500 |
| **FM8** | POST /api/checkout | Expired `creditCardExpirationYear` | 500 |
| **FM9** | POST /api/checkout | Empty `streetAddress` field | 500 |
| **FM10** | POST /api/checkout | Invalid email format | 500 |
| **FM11** | POST /api/checkout | Wrong credit card number format (wrong length) | 500 |
| **FM12** | POST /api/checkout | Empty `userCurrency` string (distinct from FM6) | 500 |
| **FM13** | POST /api/checkout | Out-of-range `creditCardExpirationMonth` (negative) | 500 |
| **FM14** | POST /api/checkout | Checkout with empty or invalid cart | 500 |

> **Critical distinctions:**
> - FM6 ≠ FM12: FM6 is an **unknown currency code** (e.g., "ZZZ"), FM12 is an **empty string** in the same field. Structurally different inputs.
> - FM3 ≠ FM4: FM3 is `item: null` (key present, value null), FM4 is the `item` key absent entirely. Different JSON bodies.
> - FM7 ≠ FM11: FM7 is an empty credit card number string, FM11 is a non-empty but wrong-format string.

---

### Binary Coverage Matrix (Ground Truth)

This is the master reference table. All Venn diagrams are derived from it.

| FM | C-Str | C-Few | C-CoT | D-Zero | D-Str | D-Few | D-Self | D-CoT | E-Zero | E-Few | E-Self | E-CoT | Found in N |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| FM1 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | **11/12** |
| FM2 | ✅ | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | 5/12 |
| FM3 | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ | ✅ | 5/12 |
| FM4 | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | 4/12 |
| FM5 | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | 3/12 |
| FM6 | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ | ❌ | ❌ | ✅ | ❌ | 4/12 |
| FM7 | ✅ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | 4/12 |
| FM8 | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | 2/12 |
| FM9 | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | 2/12 |
| FM10 | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ | ✅ | 3/12 |
| FM11 | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ❌ | ❌ | ❌ | 3/12 |
| FM12 | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | **1/12** |
| FM13 | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | **1/12** |
| FM14 | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | 2/12 |
| **Total FMs** | **7** | **4** | **3** | **4** | **7** | **3** | **0** | **6** | **3** | **2** | **6** | **5** | — |

---

## 2. Jaccard Similarity Overview

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
| C-Structured ↔ D-Structured | **1.00** | Identical — FM1,FM2,FM3,FM5,FM6,FM7,FM8 |
| D-Self ↔ any run | **0.00** | D-Self = ∅ (no oracle called) |
| C-CoT ↔ E-CoT | **0.60** | Highest non-Structured pair |
| D-CoT ↔ E-CoT | **0.57** | Strong CoT cross-model overlap |
| C-FewShot ↔ D-FewShot | **0.17** | Same prompt, very different results |
| E-FewShot ↔ E-Self | **0.14** | Same model, different prompt modes |

---

## 3. Venn Diagrams — Section 1: Same Prompt, Different Models

This section answers: *"If you give the same prompt to three different models, do they find the same failures?"*

---

### Diagram 1a — Structured: Model C vs D
*(E-Structured excluded — crashed twice)*

![Structured C vs D](1a_Structured_CvsD.png)

**FM sets:**
- C-Structured: {FM1, FM2, FM3, FM5, FM6, FM7, FM8}
- D-Structured: {FM1, FM2, FM3, FM5, FM6, FM7, FM8}

**What the diagram shows:**

Both circles overlap completely. Every FM found by C-Structured is identical to every FM found by D-Structured. The only region is the shared centre.

**Jaccard:**
- C ↔ D = **1.00**

**Conclusion:** The Structured prompt produced **identical FM sets** across both tested models — the same finding as TeaStore. A 14B and a 32B model, given the same violation table, converged on exactly the same 7 failure modes. E-Structured crashed twice due to sequential currency timeouts destabilizing the system, meaning we cannot confirm a three-way comparison, but the C↔D identity is itself a replication of the core TeaStore Structured finding.

The violation table specified checkout-level violations (empty userId, invalid currency, empty credit card, expired card), product ID violations, and cart null-item violations. All 7 are visible from the API documentation. The table did not specify FM4 (cart item field missing entirely), FM9 (empty address), FM10 (invalid email), FM11 (wrong card format), FM12 (empty currency string), FM13 (negative card month), or FM14 (empty cart checkout) — which require subtle structural reasoning below the specification level.

---

### Diagram 1b — FewShot: Model C vs D vs E

![FewShot C vs D vs E](1b_FewShot_CvsDvsE.png)

**FM sets:**
- C-FewShot: {FM1, FM9, FM11, FM12}
- D-FewShot: {FM1, FM10, FM13}
- E-FewShot: {FM1, FM9}

**What the diagram shows:**

- **FM1** sits in the three-way intersection — the only failure found by all three models.
- **FM9** sits in the C∩E region — both C and E generated a checkout test with an empty street address.
- **FM11, FM12** are C-only — only Model C reasoned its way to wrong credit card format and empty currency string.
- **FM10, FM13** are D-only — only Model D found invalid email and negative card month.
- D∩E and the three-way intersection have no other FMs.

**Jaccard values:**
- C↔D = 0.17 (share only FM1 out of 6 total)
- C↔E = 0.50 (share FM1 and FM9 out of 4 total)
- D↔E = 0.25 (share only FM1 out of 4 total)

**Conclusion:** FewShot produces **high inter-model diversity** (avg Jaccard = 0.31). FM12 (empty userCurrency string, as opposed to unknown currency code) was found only by C-FewShot — the most subtle structural distinction in the OTel dataset, requiring the model to understand that an empty string and an unknown string both 500 but for different reasons. FM13 (negative card month) was found only by D-FewShot — a boundary case that required the code-tuned model's tendency toward parameter range testing when given checkout examples.

---

### Diagram 1c — CoT: Model C vs D vs E

![CoT C vs D vs E](1c_CoT_CvsDvsE.png)

**FM sets:**
- C-CoT: {FM1, FM2, FM4}
- D-CoT: {FM1, FM2, FM3, FM6, FM10, FM11}
- E-CoT: {FM1, FM2, FM3, FM4, FM10}

**What the diagram shows:**

- **FM1 and FM2** appear in all three — the only three-way intersection.
- **FM3** sits in the D∩E region — both D and E CoT reasoned their way to the null-item cart failure.
- **FM10** sits in the D∩E region as well — both D and E generated invalid email checkout tests via CoT reasoning.
- **FM4** sits in the C∩E region — both C and E found the missing-item-field cart failure.
- **FM6, FM11** are D-only — only Model D's CoT reasoning produced invalid currency in checkout and wrong card format.
- The C∩D intersection is empty — no failures found exclusively by C and D.

**Jaccard values:**
- C↔D = 0.29
- C↔E = 0.60 — the highest non-Structured pair in the entire OTel dataset
- D↔E = 0.57

**Conclusion:** CoT achieves the **highest inter-model diversity of any prompt type** in OTel (avg Jaccard = 0.49). The step-by-step reasoning scaffold led models to different checkout violation categories: C focused on product ID and cart structure, D explored checkout field validation extensively, E balanced both. The C↔E = 0.60 is remarkable — two architecturally different models (14B general vs 70B general) produced highly overlapping FM sets via identical reasoning steps. Unlike TeaStore where FewShot was the most diverse prompt, in OTel the richer API surface (14 possible FMs vs 9) gives CoT reasoning more space to diverge into different categories.

---

### Diagram 1d — ZeroShot: Model D vs E
*(C-ZeroShot: COMPILE_FAIL — excluded)*

![ZeroShot D vs E](1d_ZeroShot_DvsE.png)

**FM sets:**
- C-ZeroShot: NOT RUN (compile failure — response variable never assigned)
- D-ZeroShot: {FM1, FM4, FM7, FM14}
- E-ZeroShot: {FM1, FM11, FM14}

**What the diagram shows:**

- **FM1 and FM14** sit in the shared intersection.
- **FM4 and FM7** are D-only — Model D's unguided exploration led it to cart structural violations and missing credit card.
- **FM11** is E-only — Model E found wrong credit card format without any examples.

**Jaccard value:**
- D↔E = 0.40

**Conclusion:** ZeroShot achieves **moderate cross-model overlap** in OTel (Jaccard = 0.40). FM14 (checkout with empty/invalid cart) is the most interesting shared discovery — both D and E independently found that a checkout call with no items in the cart returns 500. This is a natural thing to try when exploring an e-commerce API without guidance: try to check out before adding anything. FM14 requires no specification knowledge — just realistic user journey intuition. D-ZeroShot additionally found FM7 (missing credit card) through what appears to be a partially-constructed checkout body, while E-ZeroShot found FM11 (wrong card format) through a placeholder string (`INVALID_CREDIT_CARD_NUMBER`).

---

### Diagram 1e — Self: Model D vs E
*(C-Self: MALFORMED_OUTPUT — excluded)*

![Self D vs E](1e_Self_DvsE.png)

**FM sets:**
- C-Self: NOT USED (model produced markdown with embedded snippets instead of Java class)
- D-Self: ∅ (all 30 tests passed — no `assertNoServerError()` called)
- E-Self: {FM1, FM3, FM4, FM5, FM6, FM7}

**What the diagram shows:**

D-Self is shown as an empty circle. E-Self found 6 failure modes. Their intersection is ∅.

**Jaccard value:**
- D↔E = **0.00**

**Conclusion:** The Self prompt produced the **most model-dependent results** of any prompt type in OTel. D-Self called every API endpoint but omitted `assertNoServerError()` in all tests — the self-review process failed to catch that the oracle was missing. The model edited test logic but not the missing assertion. This is a different failure mode from TeaStore's D-Self (where tests had the oracle but generated safe inputs) — in OTel, the oracle was architecturally missing. E-Self performed well, finding 6 FMs including the complete set of checkout-field violations (empty userId, invalid currency, empty credit card). FM3 and FM4 (null item and missing item in cart) are notable — the Self-refine process led Model E to think carefully about JSON body structure, recognizing that null and absent are structurally different inputs.

---

## 4. Venn Diagrams — Section 2: Same Model, Different Prompts

This section answers: *"If you use the same model but change the prompt, does it find different failures?"*

---

### Diagram 2a — Model C: Structured vs FewShot vs CoT

![Model C Prompts](2a_ModelC_Prompts.png)

**Model C FM sets:**
- C-Structured: {FM1, FM2, FM3, FM5, FM6, FM7, FM8}
- C-FewShot: {FM1, FM9, FM11, FM12}
- C-CoT: {FM1, FM2, FM4}

**Three-way analysis:**

| Region | FMs |
|---|---|
| C-Struct only | FM3, FM5, FM6, FM7, FM8 |
| C-FewShot only | FM9, FM11, FM12 |
| C-CoT only | FM4 |
| Struct ∩ FewShot | ∅ |
| Struct ∩ CoT | FM2 |
| FewShot ∩ CoT | ∅ |
| All three | FM1 |

**Jaccard values:**

| Pair | Jaccard |
|---|---|
| Structured vs FewShot | 0.10 |
| Structured vs CoT | 0.25 |
| FewShot vs CoT | 0.17 |
| **Average** | **0.17** |

**Notable observations:**
- FM1 is the only FM found by all three of Model C's prompts.
- Structured and FewShot share **zero FMs** beyond FM1 — despite both being the two highest-coverage prompts for Model C.
- C-FewShot found FM9 (empty address), FM11 (wrong card format), and FM12 (empty currency string) — none of which Structured found despite Structured having 7 FMs total.
- C-Structured is a **strict superset** of nothing — its 7 FMs are largely orthogonal to what FewShot or CoT found.

**Conclusion:** Model C shows **high within-model diversity** (avg Jaccard = 0.17). The three prompts explore genuinely different parts of the OTel failure space. Structured went deep on checkout field validation (FM5–FM8) and product/cart basics (FM1–FM3). FewShot found checkout edge cases that require subtle structural distinctions (empty currency string vs. unknown currency code). CoT found missing-field cart violations. No single prompt dominates.

---

### Diagram 2b — Model D: Structured vs FewShot vs CoT

![Model D Prompts](2b_ModelD_Prompts.png)

**Model D FM sets:**
- D-ZeroShot: {FM1, FM4, FM7, FM14} *(not in three-way, documented separately)*
- D-Structured: {FM1, FM2, FM3, FM5, FM6, FM7, FM8}
- D-FewShot: {FM1, FM10, FM13}
- D-CoT: {FM1, FM2, FM3, FM6, FM10, FM11}
- D-Self: ∅

**Three-way analysis (Structured vs FewShot vs CoT):**

| Region | FMs |
|---|---|
| D-Struct only | FM5, FM7, FM8 |
| D-FewShot only | FM13 |
| D-CoT only | FM11 |
| Struct ∩ FewShot | ∅ |
| Struct ∩ CoT | FM2, FM3, FM6 |
| FewShot ∩ CoT | FM10 |
| All three | FM1 |

**Jaccard values:**

| Pair | Jaccard |
|---|---|
| Structured vs FewShot | 0.11 |
| Structured vs CoT | 0.44 |
| FewShot vs CoT | 0.29 |
| **Average** | **0.28** |

**Notable observations:**
- D-Structured and D-CoT have the highest intra-model Jaccard (0.44) — both cover the product/cart/checkout structural failures but CoT goes further into email and card format.
- D-FewShot and D-Structured share **zero FMs** beyond FM1 — FewShot sent Model D toward checkout edge cases (invalid email, negative card month) that the violation table never specified.
- FM13 (negative credit card month) is D-FewShot-exclusive — the most unusual boundary value in the entire dataset.
- D-ZeroShot found FM14 (checkout with empty cart) — a natural ZeroShot discovery that guided prompts missed because they focused on field-value violations rather than state violations.
- D-Self = ∅. The oracle-omission failure makes this a non-contributor, not a safe-input generator.

**Conclusion:** Model D shows **moderate within-model diversity** (avg across all prompt pairs = 0.13). The code-tuned model benefits strongly from structured violation framing — D-Structured and D-CoT are the two richest runs. D-FewShot found rare edge cases (FM13) that no reasoning-based prompt found, suggesting the worked examples activated different thinking about parameter bounds. D-ZeroShot's FM14 discovery is arguably the most interesting isolated result — unconstrained exploration found a state-based failure (empty cart) that specification-focused prompts completely missed.

---

### Diagram 2c — Model E: FewShot vs Self vs CoT

![Model E Prompts](2c_ModelE_Prompts.png)

**Model E FM sets:**
- E-ZeroShot: {FM1, FM11, FM14} *(documented separately in 2d)*
- E-FewShot: {FM1, FM9}
- E-Self: {FM1, FM3, FM4, FM5, FM6, FM7}
- E-CoT: {FM1, FM2, FM3, FM4, FM10}
- E-Structured: CRASHED (excluded)

**Three-way analysis (FewShot vs Self vs CoT):**

| Region | FMs |
|---|---|
| E-FewShot only | FM9 |
| E-Self only | FM5, FM6, FM7 |
| E-CoT only | FM2, FM10 |
| FewShot ∩ Self | ∅ |
| FewShot ∩ CoT | ∅ |
| Self ∩ CoT | FM3, FM4 |
| All three | FM1 |

**Jaccard values:**

| Pair | Jaccard |
|---|---|
| FewShot vs Self | 0.14 |
| FewShot vs CoT | 0.00 |
| Self vs CoT | 0.38 |
| **Average** | **0.17** |

**Notable observations:**
- E-FewShot shares **zero FMs** with E-CoT — identical to the TeaStore pattern where E-FewShot was completely disjoint from E-CoT.
- E-Self and E-CoT both find FM3 and FM4 (cart null-item and missing-item field) — the two reasoning prompts converge on cart structural violations.
- FM9 (empty street address) is E-FewShot-exclusive within Model E — the worked examples led E to think about address field completeness in checkout.
- E-Self found the broadest range of checkout violations for Model E: FM5 (empty userId), FM6 (unknown currency), FM7 (empty card number) — suggesting self-review primed the model to consider required-field completeness.

**Conclusion:** Model E shows **high within-model diversity** (avg = 0.20). The same pattern as TeaStore emerges: **examples and reasoning activate different testing modes in llama3.1:70b.** FewShot → address/form validation testing. Self → required-field completeness testing. CoT → structural body testing (null vs absent fields). These three modes are largely orthogonal, meaning E's prompts are complementary rather than redundant.

---

### Diagram 2d — Model D: ZeroShot vs Structured, FewShot, CoT

![Model D ZeroShot vs Others](2d_ModelD_ZeroShotVsOthers.png)

**D-ZeroShot = {FM1, FM4, FM7, FM14}**

| Pair | Shared | D-ZeroShot only | Other only | Jaccard |
|---|---|---|---|---|
| ZeroShot vs Structured | FM1, FM7 | FM4, FM14 | FM2, FM3, FM5, FM6, FM8 | 0.17 |
| ZeroShot vs FewShot | FM1 | FM4, FM7, FM14 | FM10, FM13 | 0.11 |
| ZeroShot vs CoT | FM1, FM4 | FM7, FM14 | FM2, FM3, FM6, FM10, FM11 | 0.18 |

**What the diagram shows:**

FM14 (checkout with empty/invalid cart) sits in the ZeroShot-only region across all three comparisons. No other Model D prompt found FM14.

**Conclusion:** FM14 is the **state-based failure mode** unique to D-ZeroShot within Model D. This is the most conceptually interesting isolated finding: without a violation table or worked examples, Model D included a realistic user journey (add nothing to cart, then try to check out) that all guided prompts missed because they focused on input-field violations rather than application-state violations. This suggests that **unguided exploration probes different failure categories than guided testing** — even for a model that typically defaults to safe inputs.

---

## 5. Venn Diagram — Section 3: Overview Per Model (Best 3 Prompts)

### Diagram 3 — Structured/FewShot/CoT per Model

![Overview Per Model](3_OverviewPerModel.png)

This diagram compares the three most productive prompt types across all three models side by side.

---

**Model C — Structured + FewShot + CoT:**

| Region | FMs |
|---|---|
| C-Struct only | FM3, FM5, FM6, FM7, FM8 |
| C-FewShot only | FM9, FM11, FM12 |
| C-CoT only | FM4 |
| Struct ∩ CoT | FM2 |
| All three | FM1 |
| Struct ∩ FewShot | ∅ |
| FewShot ∩ CoT | ∅ |

Union: {FM1–FM9, FM11, FM12} — **11 FMs** from Model C's three prompts combined.

Model C has the **richest three-prompt union** — 11 of 14 FMs covered. FM10 (invalid email), FM13 (negative card month), and FM14 (empty cart) are only discoverable by Model D or via ZeroShot. The three prompts are largely orthogonal, making Model C the best candidate for an ensemble within a single model.

---

**Model D — Structured + FewShot + CoT:**

| Region | FMs |
|---|---|
| D-Struct only | FM5, FM7, FM8 |
| D-FewShot only | FM13 |
| D-CoT only | FM11 |
| Struct ∩ CoT | FM2, FM3, FM6 |
| FewShot ∩ CoT | FM10 |
| All three | FM1 |
| Struct ∩ FewShot | ∅ |

Union: {FM1, FM2, FM3, FM5, FM6, FM7, FM8, FM10, FM11, FM13} — **10 FMs**.

Model D has a **spoke pattern** for its structured three prompts. FM13 is D-FewShot-exclusive; FM11 is D-CoT-exclusive. D-ZeroShot adds FM4 and FM14, making Model D's full ensemble cover 12 of 14 FMs — the highest of any single model.

---

**Model E — FewShot + Self + CoT:**

| Region | FMs |
|---|---|
| E-FewShot only | FM9 |
| E-Self only | FM5, FM6, FM7 |
| E-CoT only | FM2, FM10 |
| Self ∩ CoT | FM3, FM4 |
| All three | FM1 |
| FewShot ∩ Self | ∅ |
| FewShot ∩ CoT | ∅ |

Union: {FM1, FM2, FM3, FM4, FM5, FM6, FM7, FM9, FM10} — **9 FMs**.

Model E confirms the **complete orthogonality** of FewShot vs reasoning prompts — FewShot shares nothing with Self or CoT beyond FM1. The Self ∩ CoT intersection (FM3, FM4) shows that Model E's reasoning-based prompts consistently generate structural cart body violations. E-ZeroShot adds FM11 and FM14, bringing Model E's full ensemble to 11 FMs.

**Overall cross-model observation from Diagram 3:**

FM1 (invalid product ID) is the universal constant — it appears in the three-way intersection for all three models. FM12 (empty userCurrency string) only appears in Model C's FewShot panel, and FM13 (negative card month) only in Model D's FewShot panel — the two rarest FMs in the dataset, each discoverable only by specific model+prompt combinations.

---

## 6. Emergent Behavior — Excluded Class

Five tests across four runs generated inputs with **illegal URI characters** without any instruction to do so. Java's `HttpClient` rejected these before any request reached the SUT. All are classified as **Excluded** in the CRASH taxonomy.

| Run | Test | Input Generated | Type |
|---|---|---|---|
| C-CoT | `test_R1_ProductWhitespacePath` | `GET /api/products/   ` | Whitespace-only path segment |
| D-CoT | `test_R1_GetProductWithPathSegmentWhitespace` | `GET /api/products/   ` | Whitespace-only path segment |
| E-FewShot | `test_R1_RecommendationsInvalidSessionId` | `sessionId=<script>alert('XSS')</script>` | XSS payload in query |
| E-FewShot | `test_R1_CartSessionIdWithSpecialChars` | `sessionId=abc!@#$%^&*()` | Special chars in query |
| E-CoT | `test_R1_productIdNonExistent` | `GET /api/products/ NONEXISTENT` | Leading space in path |
| E-CoT | `test_R4_pathSegmentWhitespaceOnly` | `GET /api/products/   ` | Whitespace-only path segment |

**Key observations:**

1. **All three CoT runs** (C-CoT, D-CoT, E-CoT) generated whitespace-in-path inputs. This is a cross-model pattern specific to chain-of-thought reasoning: step-by-step boundary analysis leads all three models to consider whitespace as a path edge case.

2. **E-FewShot** generated XSS and special-character sessionId inputs — consistent with the worked examples activating security-oriented thinking for Model E.

3. This is a **stronger emergent signal than TeaStore** (where only D-CoT and E-CoT showed this behavior). In OTel, the pattern appears across 3 models and 2 distinct prompt types.

4. The SUT's behavior against these inputs is **unknown** — the Java HTTP client enforces URI validity strictly and never forwarded the request. To test security robustness against these inputs, URL-encoding would be required (e.g., `%20` for spaces, `%3Cscript%3E` for `<script>`).

---

## 7. Summary of Key Findings

### Finding 1 — Structured prompt collapses diversity (again)

C-Structured and D-Structured found **identical FM sets** {FM1, FM2, FM3, FM5, FM6, FM7, FM8} → Jaccard = **1.00**. This is a direct replication of the TeaStore Structured finding. The violation table specified 7 violations visible from the API contract, and both models (14B and 32B) converged on exactly those 7.

The 7 FMs not in the Structured set (FM4, FM9, FM10, FM11, FM12, FM13, FM14) all require either sub-specification structural knowledge or boundary-value reasoning that a standard violation table does not encode.

> *Implication: Structured prompts guarantee consistent coverage of specified violations but cannot discover what isn't specified. This is a feature for regression testing and a limitation for exploratory robustness testing.*

---

### Finding 2 — CoT achieves the highest cross-model diversity in OTel

Unlike TeaStore (where FewShot led), OTel's CoT prompt achieves the highest inter-model diversity (avg Jaccard = 0.49). C-CoT ↔ E-CoT = 0.60, the highest non-Structured pair in the dataset.

The richer OTel API surface (14 possible FMs vs TeaStore's 9) gives CoT reasoning more space to diverge into different categories. Model C's CoT explored cart structure, D's CoT explored checkout field validation, and E's CoT explored a mix. The same reasoning steps led to different FM categories.

> *Implication: On SUTs with complex, multi-field POST bodies, CoT reasoning is more effective than FewShot for promoting inter-model diversity.*

---

### Finding 3 — D-Self = ∅ for the second consecutive SUT

D-Self produced zero failures in **both TeaStore and OTel**. In TeaStore, D-Self generated safe inputs (oracle present but no adversarial inputs). In OTel, D-Self omitted the oracle entirely (tests called endpoints but never called `assertNoServerError()`). Both result in zero failures, but for different reasons.

The consistent outcome — zero FM discovery from qwen2.5-coder:32b with a Self-Refine prompt — is SUT-independent. The Self prompt's reflection loop reinforces conservative, oracle-omitting, or safe-input generation in this code-specialized model.

> *Implication: The Self-Refine prompt is ineffective for robustness test generation with Model D. Code-specialized models require explicit violation framing.*

---

### Finding 4 — No single run covers all 14 FMs

| Coverage | Details |
|---|---|
| Max FMs in one run | **7** (C-Structured, D-Structured, E-Self — tied) |
| FM12 | Found by **1 run only** (C-FewShot) |
| FM13 | Found by **1 run only** (D-FewShot) |
| FM8 | Found by 2 runs (C-Structured, D-Structured only) |
| FM14 | Found by 2 runs (D-ZeroShot, E-ZeroShot only) |

To cover all 14 FMs would require a minimum ensemble of **C-Structured + C-FewShot + D-FewShot + D-ZeroShot + E-CoT** (or similar). No single model or prompt achieves this. FM12 and FM13 are each discoverable only by one specific run combination.

> *Implication: An ensemble of multiple models and multiple prompt strategies is necessary. Even the broadest single run misses half the failure modes.*

---

### Finding 5 — ZeroShot discovers state-based failures that guided prompts miss

D-ZeroShot and E-ZeroShot both found FM14 (checkout with empty/invalid cart) — a state-based failure that no Structured, FewShot, CoT, or Self run found. ZeroShot's unconstrained exploration naturally includes realistic user journeys (try to check out before adding anything), while guided prompts focus on parameter-level violations.

E-ZeroShot additionally found FM11 (wrong credit card format) via a placeholder string (`INVALID_CREDIT_CARD_NUMBER`) — unguided exploration that happened to trigger a format validation.

> *Implication: ZeroShot has unique exploratory value for state-based failures. A complete ensemble should include at least one unguided run.*

---

### Finding 6 — FM12 and FM13 are the rarest discoveries in the dataset

FM12 (empty `userCurrency` string) was found only by **C-FewShot**. FM13 (negative `creditCardExpirationMonth`) was found only by **D-FewShot**. Both require model-specific responses to worked examples — Model C learned from the examples to test empty-string variants of fields it normally tests for unknown values, while Model D learned to probe integer boundary violations in date fields.

These are structurally distinct from the other checkout failures and require different test inputs. Their rarity reflects how narrow the discovery window is — only one model, one prompt type, one example interpretation.

> *Implication: The hardest failure modes to discover require specific model+prompt combinations that are difficult to predict in advance. Ensemble diversity is the only reliable strategy.*

---

### Finding 7 — Emergent security-aware test generation is stronger in OTel

Three models on CoT/FewShot prompts generated whitespace, XSS, and special-character inputs without instruction — compared to only 2 runs in TeaStore (D-CoT, E-CoT). The whitespace-in-path pattern appeared in **all three CoT runs** across all three models. E-FewShot generated an XSS payload in a session ID parameter.

> *Implication: CoT reasoning consistently activates latent security testing knowledge — likely because step-by-step boundary analysis includes "what characters are invalid in this field?" as a natural reasoning step. This spans model architectures and sizes.*

---

## 8. Coverage Gap Analysis

FMs ranked by how hard they were to find:

| FM | Found by N | Which runs reliably find it |
|---|---|---|
| FM1 | 11/12 | Almost all — universal except D-Self |
| FM2 | 5/12 | Structured (C, D), CoT (C, D, E) |
| FM3 | 5/12 | Structured (C, D), CoT (D, E), Self (E) |
| FM4 | 4/12 | CoT (C, E), ZeroShot (D), Self (E) |
| FM6 | 4/12 | Structured (C, D), CoT (D), Self (E) |
| FM7 | 4/12 | Structured (C, D), ZeroShot (D), Self (E) |
| FM5 | 3/12 | Structured (C, D), Self (E) |
| FM10 | 3/12 | FewShot (D), CoT (D, E) |
| FM11 | 3/12 | FewShot (C), CoT (D), ZeroShot (E) |
| FM8 | 2/12 | Structured only (C, D) |
| FM9 | 2/12 | FewShot (C, E) |
| FM14 | 2/12 | ZeroShot only (D, E) |
| **FM12** | **1/12** | **C-FewShot only** |
| **FM13** | **1/12** | **D-FewShot only** |

**FM8** (expired card year) is notably Structured-only — the violation table specified it directly, but no reasoning or example prompt generated it. This is the reverse of the typical pattern: a specification-level violation that free exploration consistently misses because models probe format errors before date validity.

**FM14** (empty cart checkout) is ZeroShot-only — no guided prompt generated it. State-based failures are invisible to parameter-focused testing.

**FM12 and FM13** are the critical coverage gaps — each requiring exactly one specific model+prompt combination.

---

## 9. Limitations

1. **C-ZeroShot and C-Self excluded** — qwen3:14b failed on both ZeroShot (compile error) and Self (malformed output) in OTel. Cross-model ZeroShot and Self comparisons are incomplete for Model C.

2. **E-Structured crashed twice** — Three-way Structured comparison not available. Only C↔D Structured confirmed at Jaccard = 1.00.

3. **D-Self oracle omission** — D-Self's 0 FMs reflects a test design failure (missing `assertNoServerError()`), not safe input generation. This is qualitatively different from TeaStore's D-Self = ∅. Both result in 0 FMs but the mechanism differs.

4. **Currency and Shipping services unavailable** — The minimal Docker Compose configuration excludes full-stack service dependencies. Currency (C++, gRPC) and Shipping (Rust, gRPC) return 504 for all inputs. Two of 8 endpoints are untestable without the full compose on a Linux host.

5. **Binary oracle only** — HTTP ≥ 500 detects Abort-class failures. Silent failures (wrong data returned) and Hindering failures (latency degradation) are undetectable with the current oracle.

6. **Single execution per run** — Results may vary across re-runs; no repeated-measures analysis.

---

*All FM mappings verified from Java test source code in `sut_otel/test_harness/src/test/java/com/example/`. Binary matrix verified cell-by-cell. Venn diagrams generated from verified FM sets. Currency/Shipping SYSTEM_UNAVAILABLE classification based on D-ZeroShot valid-request evidence.*
