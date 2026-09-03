---
tags: [domain, gotcha]
---

# پیگیری وضعیت اعتراض به بدهی — Debt-Objection Status Tracking

`FeatureFlag.FOLLOW_PROTEST_STATUS(1006)` · `:feature:workshops` → `ui/objectionStatus` ·
package `com.tamin.taminhamrah.feature.workshops.ui.objectionStatus`

An employer has already filed an objection against a debt notice (estimated-debt objection,
first-instance-board-ruling objection, or an Article-16 final-debt-review request — the **filing**
side of this, `ui/objectionableDebit`, is a separate feature — see [[History-Objection]]'s
disambiguation table). This screen shows every objection the employer has filed, across all their
workshops, with its current review status, and lets them view the SMS thread and download the
result PDF for one row.

Reached as its own top-level entry from the services menu — not from a picked workshop row, and
therefore has no `ui.model.WorkshopAction` entry, same as `CompleteEmployerInfoRoute`. Both the
`FeatureFlag` and the mock menu entry (`MockMenuData.kt`, icon `"protest"` → `Icons.Default.Gavel`
in `ServiceCard.kt`) already existed before this feature had a screen to route to.

## Endpoints

```
GET debit-objection/objection-all?page&start&limit&filter&sort
    → paged list of objection rows

GET debit-objection/objection-detail/{objectionCode}/?page&start&limit&filter&sort
    → paged list of SMS rows for one objection (objectionCode = the row's own seqNo)

GET debit-objection-reports/objection/{seqNumber}   (objectionType ESTIMATE / PRIMARY_VOTE)
GET debit-objection-reports/comitte/{seqNumber}      (objectionType ARTICLE_SIXTEEN)
```

The two PDF endpoints are **not new** — they already existed end-to-end for the filing flow
(`GetDebitObjectionPdfUseCase`, `GetArticleSixteenReportPdfUseCase` in
`core-domain/.../useCases/workshops/{WorkshopObjectionUseCases,ArticleSixteenUseCases}.kt`).
`ObjectionDocumentViewModel` just branches on `WorkShopObjectionType` to call the right one.

⚠️ The trailing slash in `objection-detail/{objectionCode}/` was copied verbatim from a confirmed
legacy capture and has not been independently re-verified against this backend. If SMS requests
404, try dropping it first.

## The `seqNo` filter fix — deliberate, not a silent behavior change

The legacy app's search sheet writes "شمارهٔ اعتراض" (objection number) into a filter key
(`branchCode`) that the backend's own filter builder never reads — a silent no-op there; the field
visibly does nothing. This port filters it on `FilterProperty.SEQ_NO` instead — the field that
actually represents an objection number on `WorkShopObjectionDN`/`WorkShopObjectionQuery`. Covered
by `WorkShopsRepositoryImplTest.getWorkShopObjections_filtersObjectionNumberOnSeqNo` specifically so
a future "cleanup" of the filter-building code doesn't quietly regress it back to the legacy bug.

`FilterProperty.PAYMENT_WORKSHOP_ID` (`"workshopId"`) and `FilterProperty.DEBIT_NUMBER`
(`"debitNumber"`) are reused as-is for the other two filters — their `@SerialName`s already matched
what this endpoint needs, and kotlinx.serialization forbids a second enum entry with an
already-used `@SerialName` (the same gotcha `PROVINCE_CODE_CITY` already documents in
`ApiFilterDN.kt`).

## `WorkShopObjectionStatus` — color mapping now follows Figma, not the legacy grid

Six status codes, decoded via `WorkShopObjectionStatus.fromCode`: `SUBMITTED`(1),
`CALCULATION_REVIEW`(2), `BOARD_REVIEW`(3), `RECALCULATED`(4), `TIME_ALLOCATED`(5), `APPROVED`(6).
Unlike `ArticleSixteenRequestStatus`, there is no rejected/failed case — every code here is a step
of one forward-moving review.

Color mapping in `WorkshopStatusTint.kt` (`WorkShopObjectionStatus.tint`): `CALCULATION_REVIEW` =
`NEGATIVE`, `BOARD_REVIEW` = `INFO`, `RECALCULATED` = `WARNING`, `APPROVED` = `POSITIVE`; the
remaining codes (`SUBMITTED`, `TIME_ALLOCATED`, `UNKNOWN`) stay `NEUTRAL`. This originally mirrored
the legacy app's own `gridStatusTypeColor` (which left `APPROVED`/6, "تایید رای", neutral rather than
green), but the Figma design (node `1788:357`, پیگیری وضعیت اعتراض list cards) shows `APPROVED` in
green distinct from the other three — confirmed against that design and changed on explicit product
direction. Don't revert `APPROVED` back to `NEUTRAL` as a "legacy fidelity" cleanup.

## `VoteTypeDTO` wire key — was `description`, is actually `voteTypeDesc`

`WorkShopObjectionDTO.voteType.description` (`VoteTypeDTO` in `WorkShopObjectionDTO.kt`) used to
declare `@SerialName("description")`. The real backend field is `voteTypeDesc`, not `description` —
confirmed against the legacy production app's own model for the same `objection-all` response
(`WorkShopObjection.VoteType.voteTypeDesc` in `my-tamin-droid`'s `AllObjectionsResponse.kt`; Gson
there has no custom `FieldNamingPolicy`, so its Kotlin property names *are* the wire keys), and
corroborated by an unrelated, independently-declared `VoteType(val voteTypeDesc: String?)` on a
different endpoint's model in the same legacy app (`WorkShopDebtResponse.kt`). With the wrong key,
`voteTypeDescription` deserialized to null on every real response and the list card's expanded
«جزئیات بیشتر» always showed a blank/dash «نوع رای» row — silently, since nothing exercised this
field in a test. Fixed to `@SerialName("voteTypeDesc")`, with a JSON-decode regression test in
`core-network`'s `WorkShopObjectionDTOTest`, matching `ProvinceNameDtoTest`'s pattern for guarding
`@SerialName` correctness. `objectionType` (`WorkShopObjectionType`/`gridObjectionTypeTranslator`
in the legacy app) was checked at the same time and is correctly mapped — not affected.

**The legacy app (`my-tamin-droid`, not `old_android` — that reference directory does not exist on
this machine) is the authority for real backend field names/behavior when in doubt**, since its
Gson models reflect the actual wire shape with no abstraction in between.

## `WorkShopObjectionType` vs `ObjectionKind` — not the same enum

`ObjectionKind` (`WorkShopDebtDN.kt`) answers "can an objection still be filed against this debt
row, and against what filing-window deadline" (`ESTIMATE`/`PRIMARY_VOTE`/`FILED`) — it belongs to
the filing flow. `WorkShopObjectionType` (`WorkShopObjectionDN.kt`) answers "which report endpoint
does this already-filed row's document come from" (`ESTIMATE`/`PRIMARY_VOTE`/`ARTICLE_SIXTEEN`) —
it has a third case filing has no equivalent for, and no notion of a filing deadline. Keeping them
separate also respects the boundary that this feature never touches the filing flow.

## Three screens, one shared summary header

```
list/       ObjectionStatusScreen  — paged list, search sheet (شمارهٔ اعتراض / کد کارگاه / شمارهٔ بدهی),
                                       applied-filter chips with per-chip removal, identity card
                                       (GetIdentityInfoUseCase — national id + name)
sms/        ObjectionSmsScreen     — timeline of SmsMessagePR (numbered badge, connecting line,
                                       colored status pill per message)
document/   ObjectionDocumentScreen — metadata card + "دریافت فایل"; download-only, no inline PDF
                                       preview (unlike TaminPdfViewer's usual full-screen viewer)
components/ ObjectionSummaryHeader — status pill + type + number + a shortcut icon to the sibling
                                       screen (sms ↔ document); ObjectionLabels.kt holds the two
                                       code→text `label()` extensions every screen in this package
                                       shares, since the service sends codes, not display text
```

The SMS and document routes each carry `workshopId`/`objectionDate` in addition to
`seqNo`/`debitNumber`/`objectionTypeCode`/`statusCode`, specifically so the sibling-navigation
shortcut in `ObjectionSummaryHeader` (SMS → document, document → SMS) still has everything the
document screen's metadata card needs, regardless of which of the two screens was reached first
from the list.

## `WorkshopListScaffold.indexedRow` — new, for the SMS timeline

The SMS screen needs a client-computed 1-based row number (matching the legacy app's own
client-side `index` field — never a server value). `WorkshopListScaffold` previously only exposed
`row: (T) -> Unit`; it now also accepts an optional `indexedRow: (Int, T) -> Unit`, with `row` made
nullable/defaulted. All 8 pre-existing call sites (trailing-lambda syntax) are unaffected — `row` is
still the last parameter.

## `ObjectionSearchSheet` — Row child order was mirrored backwards from the design

The search bottom sheet (Figma node `1788:798`) originally had its filter fields and its two
buttons added to their `Row`s in *visual* left-to-right order (`objectionNumber, workshopId` /
`search, clear`). That is backwards under this app's global RTL layout direction: in a `Row` under
`LayoutDirection.Rtl`, the **first-added child renders on the right**, not the left — the same rule
already noted for the list card's «پیامک‌ها»/«سند اعتراض» buttons. Fixed by adding
`workshopId` before `objectionNumber` (so «کد کارگاه» sits right, «شمارهٔ اعتراض» sits left, per the
design), and «حذف فیلتر» before «جستجو» (so حذف فیلتر sits right, جستجو sits left). The primary
search button also needed `iconAtStart = true` (matching `InspectionSearchSheet`'s own search
button) so its icon lands on the button's right edge, not trailing after the label. Both buttons now
carry explicit weights (`0.3f`/`0.7f`, the same split `InspectionSearchSheet` uses) rather than one
weighted + one bare — `TaminOutlinedButton` calls `Modifier.fillMaxWidth()` internally, so a bare
(unweighted) instance beside a weighted sibling in the same `Row` claims the sibling's space before
weights are resolved.

**Rule of thumb for any new RTL `Row` in this codebase**: reason about child order by "first-added
lands on the right", not by re-deriving it from a screenshot each time — cross-check the design
screenshot once per row, since Figma's own React export order does not reflect this app's RTL
result.

## `SmsMessageDTO` was missing `id` — real messages silently disappeared

`GET debit-objection/objection-detail/{objectionCode}/` can legitimately return several messages
with **identical `smsDescription` and `status`** — the service resends the same committee notice
more than once for the same event. Confirmed against a real capture: `total: 5`, and 3 of the 5
messages (different `id`s, same text/status) were byte-for-byte identical otherwise.

`SmsMessageDTO`/`SmsMessageDN`/`SmsMessagePR` never carried the message's own `id` (only
`smsDescription`/`status`) — so after mapping, those three real, distinct messages became
structurally identical `SmsMessagePR` rows. `PagedListState.loaded()`'s shared dedup
(`rows.distinct()`, correct for the other seven کارگاه lists — see its own doc comment) then
silently collapsed them into one: **a server total of 5 rendered as 3 rows in the app**, with no
error, no loading-state glitch, nothing visibly wrong to point at. Root-caused and reported by the
user with an actual API capture, not found by static review alone — the bug is invisible unless you
already suspect a specific count mismatch and have a real response to diff against.

Fixed by adding `id: Long?` through the whole chain (`SmsMessageDTO` → `SmsMessageDN` →
`SmsMessagePR`) — note this `id` is the *message's own* identity, distinct from `seqNo`, which every
message in one objection's thread shares (it's the parent objection's id, not the message's).
Giving `SmsMessagePR` a real identity is what lets `.distinct()` — which must stay in
`PagedListState.loaded()` for the other seven lists — stop conflating genuinely different rows.
Also wired `key = { it.id ?: it.hashCode() }` on the SMS screen's `WorkshopListScaffold` call,
matching the list screen's own `key = { it.seqNo ?: it.hashCode() }` convention.

Regression coverage: `WorkShopObjectionDTOTest` (`core-network`) decodes a trimmed version of the
real 5-message capture and asserts all 5 `id`s survive distinct — guards the DTO side. No test
guards the `PagedListState.distinct()` interaction directly (that's exercised implicitly via `id`
now being present in the data), since `PagedListStateTest` is deliberately generic (`T = String`) —
adding a `SmsMessagePR`-specific case there would leak this feature's domain type into a shared test
file that intentionally knows nothing about any single caller.

**If a future کارگاه list shows fewer rows than the server's `total` and nothing else in this file's
checklist explains it, check whether that list's row type has a real unique identity before assuming
a pagination bug** — `PagedListState`'s dedup is where a type without one will lose rows completely
silently, exactly like this.

The same real capture also had `"smsDescription":"null"` on one message (id `65188505`) — the
service sends the literal four-character string `"null"`, not a JSON null, for at least some
messages. `orEmpty()` alone doesn't catch that. `SmsMessageDTO.toDomain()` now treats a literal
`"null"` string the same as a blank/absent value, matching the existing
`HttpStatusErrorMapper.sanitizeRaw`'s `!it.equals("null", ignoreCase = true)` idiom for the same
backend quirk elsewhere. Covered by `WorkshopMapperTest` in `core-data`.

## `ObjectionDocumentViewModel.downloadFile()` — `isDownloading` used to end before the download did

The metadata card's shimmer (and `LoadingButton`'s own spinner, same `state.isDownloading` flag) is
meant to show for the duration of the actual PDF fetch. It used to show for effectively zero time:
`downloadFile()` emitted `Downloading` (sets `isDownloading = true`) immediately followed by
`PdfChanged(null)` — a deliberate early clear of `state.pdf` so the screen's
`LaunchedEffect(state.pdf, state.downloadFailed)` reliably re-fires even when a *repeat* download
produces a `PdfDownloadPR` equal to the one already shown. But the `PdfChanged` reducer branch
unconditionally sets `isDownloading = false` — including for that early clearing call, which runs
with no suspension before it and before the real network call (`getDebitObjectionPdf`/
`getArticleSixteenReportPdf`) even starts. So `isDownloading` flips `true → false` in the same
instant, well before the request that should be "the loading" actually begins or ends.

Fixed by folding the `pdf = null` clear into `Downloading`'s own reducer branch instead of a
separate `PdfChanged(null)` emission — `Downloading` now sets `isDownloading = true` **and**
`pdf = null` together, and only the real result (`PdfChanged(pdf.toPresentation())` on success, or
`DownloadFailed` via `.catch`) ends the loading state. `state.pdf` still goes through null before
every real result exactly as before, so the repeat-download re-fire guarantee is unchanged.

Also fixed alongside this: the metadata card's shimmer in `ObjectionDocumentScreen.kt` was gated on
`downloadedBytes == null` — true for the entire time *before* any download too, not just during one
— so it used to show permanently until the first successful download. Now gated on
`state.isDownloading` directly.

## List screen header now folds — TopArea motion system

`ObjectionStatusScreen`'s hero (back/search bar + ring icon + subtitle) now folds away on scroll,
matching the exact scenario `LegalRepresentativeWorkshopsScreen` (معرفی نماینده اشخاص حقوقی's hub
page) already implements, per explicit product request. Structure:

- `ObjectionStatusContent` builds a `TopAreaState` via `rememberMeasuredTopAreaState`, drives it
  from `WorkshopListScaffold`'s own `listState` via `Modifier.driveTopArea(...)` (passed through
  that composable's existing `modifier`/`listState`/`contentPadding` parameters — no changes needed
  to `WorkshopListScaffold` itself), and overlays the floating header via `Box` +
  `Modifier.align(Alignment.TopCenter).reportTopAreaHeight(...)`, replacing the previous plain
  `Column`.
- `ObjectionStatusTopArea` (new, private) is the measured-and-real header block: `TaminTopAppBar` +
  `ObjectionStatusHeroContent` (now `topAreaState`-aware, wraps its icon+subtitle in
  `Modifier.topAreaHide(...)` and threads `animated = !topAreaState.isMeasureProbe` into
  `AnimatedRingHeaderIcon` — the same infinite-animation-during-the-measure-probe gotcha
  `docs/vault/TopArea-System.md` documents) + `IdentityCard`.
- The identity card's ride-up switched from the older `Modifier.rideUpIntoHeader(...)` (which was
  only ever called here with `progress = { 0f }` and equal expanded/collapsed overlaps — already a
  static ride, never actually animated) to a `straddlePreviousSibling(Dp)` extension, file-local to
  this screen — the same idiom, independently declared, `LegalRepresentativeWorkshopsScreen` uses.
  Don't extract a third copy without checking `docs/vault/TopArea-System.md` first, since a fourth
  consumer is exactly the trigger point that doc names for finally sharing it.

## Empty state — `WorkshopListScaffold` gained an `emptyContent` slot

The «هیچ اعتراضی یافت نشد» empty state (Figma node `1788:906`) is a dashed-border card with a title
+ subtitle, not `WorkshopListScaffold`'s existing default (`EmptyStateMessage`'s plain icon + single
line). Rather than special-case this in `WorkshopListScaffold` itself, it now takes an optional
`emptyContent: (@Composable () -> Unit)? = null` that fully replaces the default when provided —
same additive pattern as `indexedRow` (see the section below): every other caller passes nothing and
is unaffected. **The new parameter was inserted before `row`, not after** — `row` must stay the
trailing parameter for the existing `WorkshopListScaffold(...) { item -> ... }` trailing-lambda call
sites (all 8) to keep binding to `row` instead of silently binding to the new parameter. Getting this
order wrong doesn't fail loudly in an obvious way; it fails as a confusing type-inference error at
every call site simultaneously, one per screen.

The dashed-border shell itself now lives in `feature/workshops/ui/components/DashedEmptyStateCard.kt`
— a plain `content: @Composable ColumnScope.() -> Unit` slot, no title/subtitle shape baked in —
because it has two callers with different text shapes: `ObjectionEmptyState` (list screen, title +
muted subtitle) and `ObjectionSmsEmptyState` (SMS screen, one bold line only — «پیامکی برای این
اعتراض ارسال نشده است»). Both wire it into their own `WorkshopListScaffold` via `emptyContent`.
Uses the same `drawBehind`/`PathEffect.dashPathEffect` idiom as `TaminDocumentUploadCard`'s empty
upload slot rather than inventing a new one. A third caller needing this look should reuse
`DashedEmptyStateCard` rather than re-deriving the dashed-border modifier chain a third time.

## List card action icons and the SMS-count badge

The two per-row action buttons (`ObjectionRow` in `list/ObjectionStatusScreen.kt`) use custom
outline vectors matching the Figma design pixel-for-pixel — `ic_tamin_objection_sms.xml` and
`ic_tamin_objection_document.xml` in `core-ui/composeResources/drawable/` — rather than the generic
`Icons.AutoMirrored.Filled.Chat`/`Icons.Default.Description` Material glyphs used before.

**Known gap, left unimplemented on purpose:** the design's «پیامک‌ها» button also carries a small
badge with that objection's SMS count. `GET debit-objection/objection-all` (the list endpoint) does
not return a count field — only `GET debit-objection/objection-detail/{objectionCode}` does, as one
paged `ListData<SmsMessageDTO>` (its `total`) per objection. Showing a real count on the list would
mean one extra network call per row (N+1) for every page loaded. Raised explicitly and declined —
don't add it as a "finish the design" cleanup without re-confirming that cost is now acceptable, or
until `objection-all` gains a count field server-side.

## Known gap — no PDF preview on the document screen

`ObjectionDocumentScreen` downloads-and-saves via the same `PdfSaver`/`drainBytesOrNull()`
machinery `TaminPdfViewer` uses internally (exposed as a small reusable helper on
`PdfDownloadPR` in `core-ui/.../ui/components/PdfSaver.kt` specifically so this feature module
doesn't need ktor on its classpath), but does not render the PDF inline — the design shows a
metadata card and a "دریافت فایل" (download) button, not a page-by-page preview. If in-app preview
is wanted later, swap the shimmer placeholder for `PdfPagesView` the same way `TaminPdfViewer`
does.

Related: [[History-Objection]] · [[Feature-Flags]] · [[Adding-a-Feature]] · [[Glossary]]
