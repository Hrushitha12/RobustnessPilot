# LLM Robustness Test Diversity — Cross-SUT Analysis
**Study:** ITCS 6882 Individual Study | UNC Charlotte  
**Supervisor:** Prof. Marco Vieira  
**Student:** Hrushitha Goud Tigulla  
**SUTs:** TeaStore (microservice e-commerce, Java) · OTel Astronomy Shop (microservice e-commerce, polyglot)  
**Models:** Model C — qwen3:14b · Model D — qwen2.5-coder:32b · Model E — llama3.1:70b  
**Prompt types:** ZeroShot · Structured · FewShot · CoT · Self  

---

## Table of Contents
1. [Study Configuration](#1-study-configuration)
2. [TeaStore — Individual Analysis](#2-teastore--individual-analysis)
3. [OTel Astronomy Shop — Individual Analysis](#3-otel-astronomy-shop--individual-analysis)
4. [Cross-SUT Comparison](#4-cross-sut-comparison)
5. [Replicated Findings](#5-replicated-findings)
6. [Limitations](#6-limitations)

---

## 1. Study Configuration

### Models
| Label | Model | Size | Host |
|---|---|---|---|
| Model C | qwen3:14b | 14B | Local (Windows) |
| Model D | qwen2.5-coder:32b | 32B | UNC server (cci-marco, Linux) |
| Model E | llama3.1:70b | 70B | UNC server (cci-marco, Linux) |

### Prompt Types
| Type | Description |
|---|---|
| ZeroShot | API description only, no examples or reasoning guidance |
| Structured | Violation table listing exact inputs and expected exceptions |
| FewShot | 6 worked example tests demonstrating the pattern |
| CoT | 9-step reasoning scaffold (boundary analysis before code) |
| Self | Draft → self-review checklist → corrected final output |

### Robustness Oracle
A test **passes** if the HTTP response status code is `< 500`.  
A response `≥ 500` is a **robustness failure** (Abort class in CRASH taxonomy).

---

## 2. TeaStore — Individual Analysis

### 2.1 Run Status (15 generated, 14 valid)

| Run | Status | Notes |
|---|---|---|
| **C-ZeroShot** | **EXCLUDED** | Language instability — model switched to Chinese mid-generation |
| C-Structured | ✅ Ran | |
| C-FewShot | ✅ Ran | |
| C-Self | ✅ Ran | |
| C-CoT | ✅ Ran | |
| D-ZeroShot | ✅ Ran | 0 FMs — generated safe inputs only |
| D-Structured | ✅ Ran | |
| D-FewShot | ✅ Ran | |
| D-Self | ✅ Ran | 0 FMs — safe inputs; no assertNoServerError omitted |
| D-CoT | ✅ Ran | 1 Excluded test (XSS payload, illegal URI) |
| E-ZeroShot | ✅ Ran | |
| E-Structured | ✅ Ran | |
| E-FewShot | ✅ Ran | |
| E-Self | ✅ Ran | |
| E-CoT | ✅ Ran | 1 Excluded test (whitespace path, illegal URI) |

### 2.2 Failure Mode Definitions (9 FMs, verified from Java source)

| FM | Endpoint / Input | HTTP |
|---|---|---|
| FM1 | `GET product?id=abc` — non-numeric string | 500 |
| FM2 | `GET product?id=` — empty string | 500 |
| FM3 | `POST cartAction=addToCart` — productid key absent | 500 |
| FM4 | `POST cartAction=addToCart&productid=` — productid value empty | 500 |
| FM5 | `POST cartAction=addToCart&productid=abc` — invalid string | 500 |
| FM6 | `POST cartAction=removeProduct` — productid key absent | 500 |
| FM7 | `POST cartAction=removeProduct&productid=` — productid value empty | 500 |
| FM8 | `POST cartAction=removeProduct&productid=999999` — non-existent id | 500 |
| FM9 | `POST cartAction=removeProduct&productid=1` — valid id, unauthenticated | 500 |

### 2.3 Binary Matrix (14 runs × 9 FMs)

| FM | C-FewShot | C-Self | C-Struct | C-CoT | D-FewShot | D-Struct | D-CoT | D-Zero | D-Self | E-FewShot | E-Self | E-Struct | E-Zero | E-CoT | Found in N |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| FM1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 0 | 0 | 1 | 1 | 0 | 1 | **10/14** |
| FM2 | 0 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 2/14 |
| FM3 | 0 | 0 | 0 | 0 | 1 | 0 | 1 | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 4/14 |
| FM4 | 1 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 3/14 |
| FM5 | 1 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 4/14 |
| FM6 | 0 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | **1/14** |
| FM7 | 1 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 1 | 3/14 |
| FM8 | 1 | 1 | 1 | 0 | 0 | 1 | 0 | 0 | 0 | 1 | 0 | 1 | 0 | 0 | 6/14 |
| FM9 | 0 | 1 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 3/14 |
| **Total** | **5** | **4** | **2** | **4** | **3** | **2** | **3** | **0** | **0** | **3** | **3** | **2** | **1** | **4** | |

### 2.4 FM Discoverability Tiers

| Tier | FMs | Found in |
|---|---|---|
| Easy (any competent tester) | FM1, FM8 | ≥6/14 runs |
| Hard (requires HTTP boundary reasoning) | FM3, FM4, FM5, FM7, FM9 | 3–4/14 runs |
| Very Hard (requires protocol/source knowledge) | FM2, FM6 | 1–2/14 runs |

FM6 (`removeProduct` with key absent rather than value empty) was found by **D-FewShot only** across all 14 runs — the rarest discovery in the dataset.

### 2.5 TeaStore Jaccard — Same Prompt, Different Models

| Prompt | C↔D | C↔E | D↔E | Average |
|---|---|---|---|---|
| **Structured** | **1.00** | **1.00** | **1.00** | **1.00** |
| FewShot | 0.14 | 0.33 | 0.00 | 0.16 |
| CoT | 0.17 | 0.33 | 0.40 | 0.30 |
| Self | 0.00 | 0.40 | 0.00 | 0.13 |
| ZeroShot | — | 0.00 | 0.00 | 0.00 |

### 2.6 TeaStore Jaccard — Same Model, Different Prompts

| Model | Avg Jaccard | Range | Interpretation |
|---|---|---|---|
| Model C | 0.41 | 0.20–0.50 | Moderate diversity; all share FM1 |
| Model D | 0.12 | 0.00–1.00 | High diversity; ZeroShot/Self = ∅ pulls average down |
| Model E | 0.15 | 0.00–0.75 | High diversity; E-Self ⊂ E-CoT (0.75) |

### 2.7 TeaStore Key Insights

**T1 — Structured collapses all three models to {FM1, FM8}.** The violation table was built to standard practice (boundary values, type violations, auth failures) but all three models converged on the same two failure modes. The table specified at the API documentation level, missing 7/9 FMs that require HTTP body structure knowledge below the API spec. This is a finding about specification-bounded testing, not a methodology error.

**T2 — FewShot achieves the highest inter-model diversity** (avg Jaccard = 0.16). Each model interpreted the same examples differently and probed different parts of the failure space. This makes FewShot the most effective prompt type for discovering diverse failure modes when multiple models are used.

**T3 — FM6 discovered by D-FewShot exclusively.** The `removeProduct` with the key entirely absent (rather than key-present-but-value-empty) is a structurally harder failure mode requiring protocol-level reasoning. Only the code-tuned model on a FewShot prompt generated this input.

**T4 — D-ZeroShot and D-Self both = ∅.** qwen2.5-coder:32b generates only safe, well-formed inputs on ZeroShot and Self prompts — consistent across both prompt types. This suggests the model defaults to "helpfully valid" inputs when not guided toward adversarial thinking.

**T5 — Emergent XSS/whitespace generation (Excluded class).** D-CoT and E-CoT independently generated inputs with XSS payloads and whitespace-only path segments. Java's `HttpClient` rejected these before reaching the SUT. Neither model was instructed to generate security-oriented inputs.

---

## 3. OTel Astronomy Shop — Individual Analysis

### 3.1 Run Status (15 generated, 12 valid)

| Run | Status | Notes |
|---|---|---|
| **C-ZeroShot** | **COMPILE_FAIL** | Response variable never assigned (18 errors); genuine generation failure |
| **C-Self** | **MALFORMED_OUTPUT** | Markdown with snippets; helper methods redefined; wrong HTTP methods |
| C-Structured | ✅ Fixed + Ran | Missing import + class name; fixed mechanically |
| C-FewShot | ✅ Ran | |
| C-CoT | ✅ Ran | 1 Excluded (whitespace path) |
| D-ZeroShot | ✅ Ran | |
| D-Structured | ✅ Ran | Encoding issue resolved |
| D-FewShot | ✅ Ran | |
| D-Self | ✅ Ran | 0 FMs — no assertNoServerError() called |
| D-CoT | ✅ Ran | 1 Excluded (whitespace path) |
| E-ZeroShot | ✅ Fixed + Ran | Static import removed |
| **E-Structured** | **CRASH × 2** | Repeated canary failure; sequential 504s destabilized system |
| E-FewShot | ✅ Fixed + Ran | 2 Excluded (XSS, special chars in sessionId) |
| E-Self | ✅ Fixed + Ran | |
| E-CoT | ✅ Fixed + Ran | 2 Excluded (whitespace path, leading space in path) |

> **Note on fixes:** C-Structured (missing import), E-ZeroShot/Structured/FewShot/Self/CoT (static import of non-existent assertNoServerError from Assertions). These are mechanical omissions — no test logic was changed.

### 3.2 System Unavailability Finding

Currency (`POST /api/currency`) and Shipping (`POST /api/shipping`) return **HTTP 504 for ALL requests** in the minimal Docker Compose setup. Evidence: D-ZeroShot sent a valid USD→EUR request and a valid shipping request — both returned 504. This confirms service unavailability rather than input-triggered failures. All currency/shipping 504s are classified as **SYSTEM_UNAVAILABLE** and excluded from the FM matrix. This affects 2 of 8 testable API endpoints.

### 3.3 Failure Mode Definitions (14 FMs, verified from Java source)

| FM | Endpoint | Input / Scenario | HTTP |
|---|---|---|---|
| FM1 | GET /api/products/{id} | Non-existent or invalid format ID | 500 |
| FM2 | GET /api/products/{id} | Extremely long ID (500+ chars) | 500 |
| FM3 | POST /api/cart | item field is null in body | 500 |
| FM4 | POST /api/cart | item/fields missing entirely from body | 500 |
| FM5 | POST /api/checkout | empty userId | 500 |
| FM6 | POST /api/checkout | unknown/invalid currency code | 500 |
| FM7 | POST /api/checkout | empty or missing credit card number | 500 |
| FM8 | POST /api/checkout | expired credit card year | 500 |
| FM9 | POST /api/checkout | empty street address field | 500 |
| FM10 | POST /api/checkout | invalid email format | 500 |
| FM11 | POST /api/checkout | wrong credit card number format (wrong length) | 500 |
| FM12 | POST /api/checkout | empty userCurrency string | 500 |
| FM13 | POST /api/checkout | out-of-range card month (negative) | 500 |
| FM14 | POST /api/checkout | checkout with empty or invalid cart | 500 |

### 3.4 Binary Matrix (12 runs × 14 FMs)

| FM | C-Struct | C-Few | C-CoT | D-Zero | D-Struct | D-Few | D-Self | D-CoT | E-Zero | E-Few | E-Self | E-CoT | Found in N |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| FM1 | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 | 1 | 1 | 1 | 1 | **11/12** |
| FM2 | 1 | 0 | 1 | 0 | 1 | 0 | 0 | 1 | 0 | 0 | 0 | 1 | 5/12 |
| FM3 | 1 | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 0 | 0 | 1 | 1 | 5/12 |
| FM4 | 0 | 0 | 1 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 1 | 1 | 4/12 |
| FM5 | 1 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 3/12 |
| FM6 | 1 | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 0 | 0 | 1 | 0 | 4/12 |
| FM7 | 1 | 0 | 0 | 1 | 1 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 4/12 |
| FM8 | 1 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 2/12 |
| FM9 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 0 | 2/12 |
| FM10 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 1 | 0 | 0 | 0 | 1 | 3/12 |
| FM11 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 1 | 1 | 0 | 0 | 0 | 3/12 |
| FM12 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | **1/12** |
| FM13 | 0 | 0 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | **1/12** |
| FM14 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 2/12 |
| **Total** | **7** | **4** | **3** | **4** | **7** | **3** | **0** | **6** | **3** | **2** | **6** | **5** | |

### 3.5 OTel Jaccard — Same Prompt, Different Models

| Prompt | C↔D | C↔E | D↔E | Average |
|---|---|---|---|---|
| **Structured** | **1.00** | — | — | **1.00** |
| FewShot | 0.17 | 0.50 | 0.25 | 0.31 |
| CoT | 0.29 | 0.60 | 0.57 | 0.49 |
| ZeroShot | — | 0.40 | — | 0.40 |
| Self | — | 0.00 | — | 0.00 |

> Structured: only C and D ran (E-Structured crashed). ZeroShot: only D and E ran (C-ZeroShot compile failed). Self: only D and E ran (C-Self malformed).

### 3.6 OTel Jaccard — Same Model, Different Prompts

| Model | Avg Jaccard | Range | Interpretation |
|---|---|---|---|
| Model C | 0.17 | 0.10–0.25 | Moderate diversity across 3 valid runs |
| Model D | 0.13 | 0.00–1.00 | High diversity; D-Self = ∅ pulls average down |
| Model E | 0.20 | 0.00–0.38 | Highest diversity; E-Self ↔ E-CoT = 0.38 |

### 3.7 OTel Key Insights

**O1 — Structured collapses diversity again.** C-Structured and D-Structured found identical FM sets {FM1,FM2,FM3,FM5,FM6,FM7,FM8} → Jaccard = 1.00. Same finding as TeaStore — the violation table locks all models into the same specification-visible failures.

**O2 — CoT achieves highest inter-model diversity** (avg 0.49 across pairs vs 0.31 for FewShot). Unlike TeaStore where FewShot led, OTel's CoT prompts led each model to reason about different violation categories — products for some, cart for others, checkout for others.

**O3 — D-Self = ∅ again.** D-Self ran 30 tests and all passed (no assertNoServerError was called — model did not assign return values to variables). This is a consistent behavior pattern for this model+prompt combination across both SUTs.

**O4 — FM12 and FM13 are extremely rare.** FM12 (empty userCurrency string) was found only by C-FewShot. FM13 (negative card month) was found only by D-FewShot. These checkout edge cases require the model to reason about empty-string currency as distinct from unknown-currency (FM6) — a subtle structural distinction.

**O5 — Emergent behavior is stronger in OTel.** 5 Excluded tests across 4 runs (C-CoT, D-CoT, E-FewShot, E-CoT) generated illegal URI characters without instruction. All three CoT runs generated whitespace-in-path inputs. E-FewShot generated an XSS payload in a sessionId parameter. This spans 3 models and 2 prompt types, compared to TeaStore where only 2 runs on 2 models showed this behaviour.

---

## 4. Cross-SUT Comparison

### 4.1 Overview

| Metric | TeaStore | OTel Astronomy Shop |
|---|---|---|
| SUT type | Java microservice | Polyglot microservice |
| Valid runs | 14 / 15 | 12 / 15 |
| FMs confirmed | 9 | 14 |
| Max FMs by single run | 5 (C-FewShot) | 7 (C-Struct, D-Struct, E-Self) |
| Runs with 0 FMs | 2 (D-ZeroShot, D-Self) | 1 (D-Self) |
| FM1 ubiquity | 10/14 (71%) | 11/12 (92%) |
| Rarest FM | FM6 — 1/14 runs | FM12, FM13 — 1/12 runs each |
| Emergent XSS/whitespace | 2 runs | 4 runs |
| System_Unavailable | None | Currency + Shipping (always 504) |

### 4.2 Prompt-Level Jaccard Comparison

| Prompt | TeaStore avg J | OTel avg J | Δ | Interpretation |
|---|---|---|---|---|
| **Structured** | **1.00** | **1.00** | 0.00 | Perfectly replicated finding across both SUTs |
| FewShot | 0.16 | 0.31 | +0.15 | More cross-model agreement in OTel |
| CoT | 0.30 | 0.49 | +0.19 | CoT achieves higher diversity in OTel (richer failure surface) |
| ZeroShot | 0.00 | 0.40 | +0.40 | D-ZeroShot=∅ in TeaStore; both find FMs in OTel |
| Self | 0.13 | 0.00 | −0.13 | D-Self=∅ in both; C-Self valid in TeaStore only |

### 4.3 Within-Model Jaccard Comparison

| Model | TeaStore avg J | OTel avg J | Interpretation |
|---|---|---|---|
| Model C | 0.41 | 0.17 | C has fewer valid OTel runs (3 vs 4); more constrained comparison |
| Model D | 0.12 | 0.13 | Consistent; D-Self=∅ pulls both down similarly |
| Model E | 0.15 | 0.20 | Slight increase in OTel; broader FM surface gives more variation |

---

## 5. Replicated Findings

### R1 — Structured prompt collapses diversity across both SUTs

The Structured prompt produced **Jaccard = 1.00** between all tested model pairs in both TeaStore and OTel. In TeaStore, all three models found exactly `{FM1, FM8}`. In OTel, both tested models found `{FM1, FM2, FM3, FM5, FM6, FM7, FM8}`.

The violation table specifies inputs at the API documentation level. It captures violations visible from the interface contract but misses structural violations that require protocol-level or source-level knowledge. FM3 and FM6 in TeaStore (key absent vs. key-present-but-empty) require distinguishing between HTTP body structures below the specification, which a violation table built from documentation cannot specify.

**Implication:** Structured prompts are reliable and consistent, but not diverse. They are useful for regression-style baseline coverage, not for discovering novel failure modes.

### R2 — D-Self generates 0 failure modes in both SUTs

qwen2.5-coder:32b on a Self-Refine prompt generates exclusively well-formed, valid inputs that do not trigger any server errors, regardless of SUT. In TeaStore, D-Self generated inputs like `productid=1` and `productid=-1` — valid product IDs. In OTel, the model called API endpoints but omitted `assertNoServerError()`, causing all tests to trivially pass.

This behavior is SUT-independent. It appears to reflect the model's coding-optimised tendency to generate correct, non-adversarial inputs when asked to self-review for quality.

**Implication:** The Self-Refine prompt, when applied to a code-specialized model, may induce a "correctness bias" that is counterproductive for robustness testing.

### R3 — Emergent security-aware test generation (Excluded class)

In both SUTs, models generated inputs with illegal URI characters (XSS payloads, whitespace-only path segments, special characters in query parameters) without any instruction to do so. These tests fail before reaching the SUT because Java's `HttpClient` rejects malformed URIs, placing them in the **Excluded** CRASH class.

The pattern is stronger in OTel: 5 Excluded tests across 4 runs (C-CoT, D-CoT, E-FewShot, E-CoT) versus 2 in TeaStore (D-CoT, E-CoT). Notably, all three CoT runs in OTel generated whitespace-in-path inputs — suggesting that step-by-step reasoning about path parameters leads models to consider whitespace as an edge case.

**Implication:** LLMs have latent security testing knowledge that activates under certain prompting conditions, particularly CoT. Future work could investigate whether making this boundary testing explicit (via a dedicated prompt dimension) increases genuine FM discovery.

### R4 — No single run covers all FMs in either SUT

The maximum FM coverage per single run was 5/9 (56%) in TeaStore and 7/14 (50%) in OTel. No individual LLM generation provides complete robustness coverage. Diversity across models and prompt types is required to approach full coverage — and even the full ensemble of 14 TeaStore runs did not discover all FMs simultaneously with high confidence.

**Implication:** Robustness testing using LLMs benefits from an ensemble approach — multiple models, multiple prompt strategies — rather than relying on any single generation.

### R5 — Prompt strategy explains more variance than model size

The Structured prompt collapsed three different models (7B–70B) to identical FM sets. Different prompts applied to the same model produced Jaccard values ranging from 0.10 to 1.00 within the same model. This suggests that the choice of prompt strategy is a stronger predictor of FM coverage and diversity than model capability or parameter count.

**Implication:** For researchers and practitioners deploying LLM-based testing, investment in prompt design may yield greater returns than investment in larger models.

---

## 6. Limitations

| Limitation | SUT | Impact |
|---|---|---|
| C-ZeroShot excluded in both SUTs | Both | Model C not comparable on ZeroShot; asymmetric comparisons |
| C-Self only valid in TeaStore | Both | C-Self comparison not available for OTel |
| E-Structured crashed in OTel | OTel | E-Structured excluded from OTel matrix; no three-way Structured comparison |
| Currency + Shipping SYSTEM_UNAVAILABLE | OTel | 2 of 8 endpoints untestable in minimal compose; FM surface artificially reduced |
| D-Self oracle error in OTel | OTel | Model omitted assertNoServerError(); 0 FMs not due to safe inputs but missing oracle |
| Single execution per run | Both | Results may vary across re-runs; no repeated-measures analysis |
| Manual FM verification | Both | FM mapping from source code is human-validated; potential misclassification |

---

*Generated as part of ITCS 6882 Individual Study, UNC Charlotte, Spring 2026.*  
*Data sources: TeaStore binary matrix verified from Java source (teastore_crash_analysis_fixed.zip). OTel binary matrix verified from Java source (sut_otel test harness). All Jaccard values computed from verified FM sets.*
