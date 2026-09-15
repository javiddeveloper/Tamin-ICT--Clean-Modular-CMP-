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

## Status: Phase 2 — UI built

Legacy source: `ObjectionInsuranceHistoryFragment` / `ObjectionInsuranceHistoryViewModel` /
`ObjectionInsuranceHistoryDetailDialog` (season/month editor) in my-tamin-droid.

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
              mapper/objectionInsurance/ObjectionInsuranceMapper.kt — DN ↔ PR (`toDomain()` added
              for Phase 2, to rebuild the save payload from staged UI edits)
feature       ObjectionInsuranceContract/ViewModel/Screen — see below
```

### UI design

No Claude Design mockup existed for the per-record editor or the multi-workshop picker — only the
list screen (year-card grid) was designed. Built the rest against legacy behavior + this repo's
own components:

- **Year grid** (`ui/components/ObjectionYearGrid.kt`) — one donut-ring card per year (`Canvas`
  `drawArc`, no chart library). Ring fraction/color/subtitle rules were read directly out of the
  design mockup's own JS (`yearCards` builder): blue when the year has a staged edit this session,
  else green at ≥350 declared days, else orange. Subtitle is "ویرایش‌شده" / "`N` کارگاه" (year has
  multiple workshop records) / "`N` روز" (single record).
- **Multi-workshop picker** — reuses the generic `TaminBottomSheet` (`CUSTOM` type,
  `singleSelection` + `style.selectOnTap`) rather than `HistoryChartCard`'s employer-split view;
  legacy just needs a name to disambiguate before opening one record's editor, not a comparison.
- **Per-record editor** (`ui/components/ObjectionRecordDetailSheet.kt`) — replaces legacy's
  four-season grouped list with one `TaminBarChart` (12 months, tap a bar to focus it, registered
  days as the bar fill, a pill showing any pending/staged value) plus a single editable field for
  the focused month (`TaminStyledTextField`, `InputRestriction.DigitsOnly`, clamped to that
  Jalali month's real length via `PersianDateFormatter.daysInMonth`, leap-`اسفند` included for
  free).
- `PersianDateFormatter.kt` gained `toEnglishDigits()` (mirrors the existing `toPersianDigits()`)
  since a Persian-keyboard day count has to be parsed back to ASCII before clamping.

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
