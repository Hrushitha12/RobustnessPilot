#### Failure Mode Comparison — model_c FewShot vs Seed

Seed failures (teastore_manual_r1):
- product endpoint: non-numeric id → 500

LLM failures (model_c fewshot):
- product endpoint: non-numeric id → 500  [REPLICATED from seed]
- cartAction: add with missing productid → 500  [NEW]
- cartAction: remove with missing productid → 500  [NEW]
- cartAction: add with invalid productid → 500  [NEW]
- cartAction: remove with invalid productid → 500  [NEW]

New failure modes discovered: 4
Seed failure modes replicated: 1
Seed failure modes missed: 0

--- 

#### Failure Mode Comparison — model_c Self vs Seed

Seed failures (teastore_manual_r1):
- product endpoint: non-numeric id → 500

LLM failures (model_c self):
- product endpoint: invalid id → 500  [REPLICATED from seed]
- cartAction: remove with valid productid → 500  [NEW — removing product from empty/unauthenticated cart]
- cartAction: remove with invalid productid → 500  [NEW]
- cartAction: add with invalid productid → 500  [NEW]

New failure modes discovered: 3
Seed failure modes replicated: 1
Seed failure modes missed: 0

---

#### Failure Mode Comparison — model_d FewShot vs Seed

Seed failures (teastore_manual_r1):
- product endpoint: non-numeric id → 500

LLM failures (model_d fewshot):
- product endpoint: non-numeric id → 500  [REPLICATED from seed]
- cartAction: add with missing productid param → 500  [NEW]
- cartAction: remove with missing productid param → 500  [NEW]

New failure modes discovered: 2
Seed failure modes replicated: 1
Seed failure modes missed: 0

---

#### Failure Mode Comparison — model_d Self vs Seed

All 5 failures recorded with canary=FAIL.
TeaStore became unresponsive mid-run — results are SYSTEM_UNAVAILABLE.
No valid failure mode comparison possible for this run.
Genuine failures before crash: 0 confirmed.