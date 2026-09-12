---
tags: [domain]
---

# پیگیری وضعیت اعتراض به بدهی — Debt Objection Status

`FeatureFlag.FOLLOW_PROTEST_STATUS` · `:feature:workshops` → `ui/objectionStatus` ·
package `com.tamin.taminhamrah.feature.workshops.ui.objectionStatus`

Every debt objection the employer has already filed, across all of their workshops, with its
review status, پیامک thread, and result PDF. Reached from the services menu
(`DebtObjectionStatusRoute`), not from a picked workshop row — unlike every other list under
کارگاه‌های کارفرما, it has no required `workshopId`/`branchCode`. Not to be confused with the two
neighbouring "اعتراض" services — see the disambiguation table in [[History-Objection]].

## The three screens

```
list/       ObjectionStatusScreen/ViewModel/Contract — the paged list + search sheet
sms/        ObjectionSmsScreen/ViewModel/Contract     — پیامک‌های one filed objection
document/   ObjectionDocumentScreen/ViewModel/Contract — the result PDF
```

All three share `ObjectionSummaryHeader` (`components/ObjectionSummaryHeader.kt`) — the card under
the top bar naming the objection and shortcutting to the sibling screen (list → sms/document,
sms ↔ document). `WorkShopsRepository` backs all of it directly; nothing here is cached in Room.

## Endpoints

```
debit-objection/objection-all                        GET   BaseDTO<ListData<WorkShopObjectionDTO>>
debit-objection/objection-detail/{objectionCode}/     GET   BaseDTO<ListData<SmsMessageDTO>>  (trailing slash required)
debit-objection-reports/objection/{seqNumber}         GET   استعلام/اعتراض به بدهی برآوردی + هیئت بدوی PDF
debit-objection-reports/comitte/{seqNumber}           GET   مادهٔ ۱۶ committee report PDF
```

`GetWorkShopObjectionsUseCase`/`GetWorkShopObjectionSmsUseCase` (`useCases/workshops/WorkShopObjectionStatusUseCases.kt`)
wrap the first two; `GetDebitObjectionPdfUseCase`/`GetArticleSixteenReportPdfUseCase` the PDFs.

## The two code-driven enums

`WorkShopObjectionType`/`WorkShopObjectionStatus` (`core-domain/.../model/workshop/WorkShopObjectionDN.kt`)
carry the service's numeric code and map to a Persian label via `labelRes` (a `StringResource`
property, `components/ObjectionLabels.kt`) — not a plain-`String` `label()` function as an earlier
revision had it. Every call site (`ObjectionSummaryHeader`, `ObjectionRow`, `SmsTimelineItem`)
resolves the label through `stringResource(...)` like the rest of the app, matching the
`PaymentSheetStatus.labelRes` precedent in `PaymentSheetsScreen.kt`. `WorkShopObjectionType` is
deliberately separate from `ObjectionKind` (which governs whether a *new* objection may still be
filed) — this enum only describes an *already-filed* row's document endpoint and label, and has a
third case (مادهٔ ۱۶) filing has no equivalent for.

`WorkShopObjectionType.ARTICLE_SIXTEEN`'s label is the shortened
`درخواست رسیدگی به بدهی قطعی (مادهٔ ۱۶)`, not the legacy screen's full regulatory wording
(`... موضوع ماده ۱۶ آیین نامه هیئت ها`) — a deliberate choice for the card's layout, confirmed
against the legacy source rather than an oversight.

## PDF routing — the one rule a refactor must not silently break

`ObjectionDocumentViewModel.downloadFile()`:

```kotlin
val pdf = if (state.objectionType == WorkShopObjectionType.ARTICLE_SIXTEEN) {
    getArticleSixteenReportPdf(state.seqNo)
} else {
    getDebitObjectionPdf(state.seqNo)
}
```

مادهٔ ۱۶ requests get their committee report from `debit-objection-reports/comitte/{seqNumber}`;
`ESTIMATE`/`PRIMARY_VOTE` (and `UNKNOWN`, see below) fall through to
`debit-objection-reports/objection/{seqNumber}`. Swap the branches and the user silently downloads
the wrong file with no error shown — `ObjectionDocumentViewModelTest` pins both branches plus the
success/failure/double-tap paths (`isDownloading` guards a second tap while the first request is
still in flight, same pattern as `HistoryObjectionViewModel`'s `isSubmitting` — see
[[MVI-Pattern]]).

**`UNKNOWN` falls through to the debit-objection endpoint, not to "no document."** The legacy
screen branched on the three known type codes only and did nothing for anything else; this fires a
request that fails harmlessly through the normal download-error path if the type is ever genuinely
unrecognised. Deliberate, not fixed — flagged in code review round 1 and left as-is since a button
that does nothing is arguably worse UX than one that tries and reports failure. If that judgement
ever changes, `ObjectionRow.hasDocument` would need to also require
`objectionType != WorkShopObjectionType.UNKNOWN`.

## `WorkshopListScaffold` — one row lambda, not two

`ObjectionSmsScreen`'s `SmsTimelineItem` needs a 1-based row number (`index + 1`) that the other
seven screens under کارگاه‌های کارفرما don't. Rather than carry both an optional `row: (T) -> Unit`
and an optional `indexedRow: (index, T) -> Unit` on `WorkshopListScaffold` — mutually exclusive,
nothing enforcing either was supplied, silently rendering nothing if a caller forgot both — the
component takes one required `row: (index: Int, item: T) -> Unit`, and the seven callers that don't
need the index just ignore it (`{ _, item -> ... }`).

## Two legacy defects found and fixed, not reproduced

- **Searching by شمارهٔ اعتراض silently did nothing in the old app.** The legacy search wrote the
  objection number into a filter key named `branchCode`, while the legacy data source only ever
  read `seqNo`. Here it goes through `FilterProperty.SEQ_NO` —
  `WorkShopsRepositoryImplTest.getWorkShopObjections_filtersObjectionNumberOnSeqNo` pins it against
  a real captured request.
- **`SmsMessageDTO` was missing its own `id`.** Three پیامک messages carrying identical text and
  status were being collapsed into one by the shared paged-list dedup (`PagedListState.loaded`,
  which drops structurally-equal rows) — a server total of 5 rendered as 3. `SmsMessageDN.id` (the
  message's own identity, distinct from the parent objection's `seqNo` every message in the thread
  shares) fixes it; see the KDoc on `SmsMessageDN` for the full reasoning.
- **`voteTypeDesc` is the real wire key**, verified against two independent legacy models rather
  than assumed — the previous guess (`description`) deserialised to `null` on every real response.

## Business-rule test coverage

`ObjectionStatusViewModelTest`, `ObjectionSmsViewModelTest`, `ObjectionDocumentViewModelTest`
(`feature/workshops/src/commonTest/.../ui/objectionStatus/`) cover what the repository/DTO tests
above don't reach — the three ViewModels' own behaviour:

- Applying a filter restarts the list at page zero with only the applied filters; removing one
  filter chip leaves the others in place (`ObjectionStatusViewModelTest`).
- Scrolling to the end appends rows rather than replacing them, on both the objection list and the
  پیامک thread.
- Re-opening the same `seqNo` (the sibling-screen shortcut in `DebtObjectionStatusNavigation.kt`
  popping back instead of pushing a duplicate) does not refetch a page already loaded
  (`ObjectionSmsViewModel.open()`'s early-return guard).
- The PDF routing rule above, per type, plus a gated (`CompletableDeferred`) double-tap test
  matching `InquiryEducationViewModelTest`'s `submit_whileSubmitting_doesNotCallCertificateTwice`
  pattern — a synchronous fake repository can't otherwise put two intents genuinely in flight at
  once under `UnconfinedTestDispatcher`.

`FakeWorkShopsRepository` (`feature/workshops/src/commonTest/.../fake/`) gained
`lastDebitObjectionPdfSeqNo`/`lastArticleSixteenReportPdfSeqNo` (to prove *which* PDF endpoint was
called — both previously answered from the same `pdf` field, so nothing distinguished them) and a
`debitObjectionPdfGate: CompletableDeferred<Unit>?` for the double-tap test.

## Gotcha: Compose Multiplatform string resources need per-symbol imports

`Res.string.xxx` is generated as a top-level **extension property** in package
`taminx.core.core_ui` (`import taminx.core.core_ui.Res` alone does not bring it into scope from a
different package) — every existing screen in this codebase imports each string constant it uses
by name (e.g. `import taminx.core.core_ui.payment_sheet_type_collected` in
`PaymentSheetsScreen.kt`). Missing this while moving `ObjectionLabels.kt`'s hardcoded Persian
literals to `Res.string.*` produced `Unresolved reference` errors that persisted across several
full `--rerun-tasks` rebuilds — worth knowing before assuming a stale Gradle cache is the culprit.

Related: [[History-Objection]] · [[Feature-Flags]] · [[MVI-Pattern]] · [[Typography]]
