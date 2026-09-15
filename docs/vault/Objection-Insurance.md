---
tags: [domain]
---

# اعتراض به سابقه کسری‌دار — Objection Insurance History

`FeatureFlag.OBJECTION_INSURANCE_HISTORY(42)` · `FeatureFlag.OBJECTION_INSURANCE_HISTORY_45(45)` ·
`:feature:objectionInsurance` · package `com.tamin.taminhamrah.feature.objectionInsurance`

An insured person objects that a **recorded** insurance period is short or wrong (کسری کارکرد /
اشکال). Not to be confused with neighbouring history-protest services:

| Persian | Meaning | Where it lives |
|---|---|---|
| اعتراض به سوابق ناموجود | a period is **missing entirely** | `:feature:history-objection`, flag `10` — see [[History-Objection]] |
| اعتراض به سابقه کسری دار | a recorded period is **short or wrong** | this page, flags `42` / `45` |
| اعتراض به بدهی | employer objects to a **debt** | `:feature:workshops` — see [[Debt-Objection-Status]] |

## Status: Phase 1 — data vertical + feature stub

Legacy source: `ObjectionInsuranceHistoryFragment` /
`ObjectionInsuranceHistoryViewModel` in my-tamin-droid. UI / Contract / ViewModel land in a later
session; navigation from the menu already opens a placeholder screen.

```
core-network  ObjectionInsuranceApiService
              model/objectionInsurance/ObjectionInsuranceHistoryDTO, ConfirmConflictItemDTO
              ObjectionInsuranceRemoteDataSource(+Impl)
core-domain   ObjectionInsuranceRepository
              model/objectionInsurance/ObjectionInsuranceHistoryDN
              Check / Get / Save / Confirm / FinalConfirm *UseCase
core-data     ObjectionInsuranceRepositoryImpl (network-only; list limit = 60)
              data/mapper/ObjectionInsuranceMapper.kt — DTO ↔ DN
core-ui       model/objectionInsurance/ObjectionInsuranceHistoryPR
              mapper/objectionInsurance/ObjectionInsuranceMapper.kt — DN → PR
feature       stub Navigation + empty Koin module + placeholder Screen
```

### Endpoints (`historyprotest-services/*conflict*`)

| Call | Method | Path | Notes |
|---|---|---|---|
| check status | GET | `checkstatusconflict` | `BaseDTO<Boolean>` — `true` = already reviewing; do not load list |
| list | GET | `conflicthistories` | `page`/`start`/`limit`/`filter`/`sort`; Impl uses `limit = 60` |
| save edits | POST | `saveconflict` | body `List<item>`; `data` may be null on 2xx |
| confirm | POST | `confirmconflict` | body `List<{userDesc}>` (one item); `BaseDTO<Boolean>` |
| final confirm | POST | `finalconfirmconflict` | empty string body; `BaseDTO<String>` tracking number |

Do **not** put these on `HistoryObjectionApiService` — that vertical owns `*notexist*` only.

Related: [[History-Objection]] · [[Feature-Flags]] · [[Adding-a-Feature]]
