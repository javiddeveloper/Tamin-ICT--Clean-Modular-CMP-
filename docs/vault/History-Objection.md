---
tags: [domain, gotcha]
---

# اعتراض به سوابق ناموجود — Objection to Missing History

`FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY(1)` · `:feature:history-objection` ·
package `com.tamin.taminhamrah.feature.historyobjection`

An insured person claims a period they worked is missing from their insurance record and
files an objection against it. Not to be confused with the two neighbouring services:

| Persian | Meaning | Where it lives |
|---|---|---|
| اعتراض به سوابق ناموجود | a period is **missing entirely** | `:feature:history-objection`, flag `10` |
| اعتراض به سابقه کسری دار | a recorded period is **short or wrong** | flag `42`, not implemented |
| اعتراض به بدهی | employer objects to a **debt** | `:feature:workshops` → `ui/objectionableDebit` (filing — backend layers exist, no UI yet); status tracked separately at `ui/objectionStatus`, flag `1006` — see [[Debt-Objection-Status]] |

## Status: full CRUD on `historyprotest-services` wired end-to-end

`historyprotest-services/checkstatusnotexist` (`GET`, no params, `BaseDTO<Boolean>`),
`historyprotest-services/getnotexistrequests` (`GET`, `page`/`start`/`limit`/`filter`/`sort`
query params, `BaseDTO<ListData<NotExistRequestDTO>>`),
`historyprotest-services/savenotexist` (`POST`, `@Body SaveNotExistRequestDTO`,
`BaseDTO<Boolean>`), and `historyprotest-services/deletenotexist/{requestNumber}/{rowIndex}`
(`DELETE`, no body, `BaseDTO<Boolean>`) are wired end-to-end and talk to the real backend:

```
core-network  HistoryObjectionApiService.checkStatusNotExist() / .getNotExistRequests() /
              .saveNotExist() / .deleteNotExist()
              model/historyObjection/NotExistRequestDTO, model/historyObjection/SaveNotExistRequestDTO
              HistoryObjectionRemoteDataSource(+Impl) — apiService/historyObjection, dataSource/historyObjection
core-domain   HistoryObjectionRepository.checkStatusNotExist() / .getNotExistRequests() /
              .saveNotExist() / .deleteNotExist() — repository/historyObjection
              model/historyObjection/NotExistRequestDN, model/historyObjection/SaveNotExistRequestDN
              CheckHistoryObjectionStatusNotExistUseCase, GetHistoryObjectionNotExistRequestsUseCase,
              SaveHistoryObjectionNotExistRequestUseCase, DeleteHistoryObjectionNotExistRequestUseCase
              — useCases/historyObjection
core-data     HistoryObjectionRepositoryImpl — data/repository/historyObjection (network-only, no Room cache)
              data/mapper/HistoryObjectionMapper.kt — NotExistRequestDTO.toDomain(), SaveNotExistRequestDN.toDTO()
core-ui       model/historyObjection/NotExistRequestPR (carries rowIndex — not rendered, only used as
              the second half of the delete key), mapper/historyObjection/HistoryObjectionMapper.kt —
              NotExistRequestDN.toPresentation()
feature       HistoryObjectionViewModel.init sends Intent.Load on screen open; the status check and
              the list fetch run concurrently (merge(...) of two inner flows) and land in
              HistoryObjectionUiState.hasActiveRequest / .notExistRequests.
              OnDeleteConfirmed calls DeleteHistoryObjectionNotExistRequestUseCase and, on success,
              re-runs the same loadHistoryObjectionData() the initial Load uses (full status+list
              reload, not a local list.filter { it != deleted }) — see "delete" note below.
              HistoryObjectionStepperViewModel.OnConfirmClicked (step 3, "ثبت") calls
              SaveHistoryObjectionNotExistRequestUseCase and shows a success dialog on completion —
              see "The Stepper" below.
```

### `deletenotexist` path shape — inferred, not directly confirmed by name

The captured request was `DELETE historyprotest-services/deletenotexist/1837708/1` — two path
segments, no body. `1837708` matches the pattern of `NotExistRequestDN.requestNumber`, and `1`
matches `NotExistRequestDN.rowIndex` (both fields already existed on this DN/DTO for the **read**
side, from `getnotexistrequests`). No field-name confirmation exists beyond that positional/type
match — there was no captured request with an accompanying labeled payload (it's a bodyless
`DELETE`, so there's nothing to compare field names against, unlike `savenotexist`). Treat
`{requestNumber}/{rowIndex}` as the working assumption, not a confirmed contract; if a
`getnotexistrequests` response is ever captured with a `rowi`/`rowIndex` value that clearly isn't
a small sequential row index, revisit this.

`NotExistRequestPR.rowIndex: String?` was added (nullable, same as the DN field it's sourced
from) purely to carry this identifier from the list screen's card down to the delete intent —
`HistoryObjectionViewModel.handleDeleteConfirmed()` treats a `null` rowIndex as unrecoverable
(shows `history_objection_delete_missing_data_error` instead of calling the endpoint with a bad
path segment) rather than guessing a fallback value.

### `savenotexist` request shape — captured from a real device, not guessed

Unlike the rest of this page's original "assumed, not confirmed" caveats, the `savenotexist`
payload was captured directly from a real request/response pair:

```json
{
  "branchCode": "0950", "branchName": "...", "cityCode": "1306", "cityName": "...",
  "endDate": "1724013000000", "insuranceType": "02", "provinceCode": "14", "provinceName": "...",
  "rwshAddress": "...", "rwshManager": "...", "rwshid": "...", "rwshname": "...",
  "startDate": "1660937400000", "workDays": "11"
}
```
Response: `{ "status": 200, "family": "SUCCESSFUL", "reason": "OK", "data": true }` — same
`BaseDTO<Boolean>` envelope as `checkstatusnotexist`.

Two things worth flagging for anyone extending this:
- **`startDate`/`endDate` are epoch millis stringified, not a compact Jalali date** (this page
  previously guessed a `"14030512"`-style format modeled on `toApiFormat()`/`JalaliDate`, neither
  of which actually exist anywhere in this codebase — see the "no `JalaliDate`" note below, now
  resolved). `SaveNotExistRequestDN.toDTO()` in `core-data/.../data/mapper/HistoryObjectionMapper.kt`
  does the conversion with a plain `.toString()` on the `Long` timestamp already sitting in
  `HistoryObjectionStepperState.startDateTimestamp`/`.endDateTimestamp` — no date-formatting
  helper was needed after all.
- **`SaveNotExistRequestDTO` field names mirror the raw backend keys exactly** (`rwshid`,
  `rwshname`, `rwshManager`, `rwshAddress`) via `@SerialName`, same as the pre-existing
  `NotExistRequestDTO`; the DN layer uses the same clean names (`workshopId`, `workshopName`,
  `workshopManager`, `workshopAddress`) as the existing `NotExistRequestDN` for the same concepts.

The literal endpoint name was kept rather than invented business meaning (e.g.
"canSubmitNewObjection") since what `true`/`false` should trigger in the UI was originally
unspecified; it has since been wired to gate a blocking `ActiveRequestDialog` on screen open.

### `NotExistRequestPR` card — field mapping and known gaps

Field mapping (confirmed by product, not derived from the sample payload alone):

| Card element | Source field |
|---|---|
| Title | `branchName` |
| Subtitle | `insuranceTypeDesc` |
| "نام کارگاه" | `workshopName` (`rwshname`) |
| "شماره بیمه" | `insuredId` (`risuid`), defaults to `"0"` if blank |
| "کد کارگاه" (dashed copy chip) | `workshopId` (`rwshid`) — **the whole chip row is omitted, not just blanked, when this is null/blank** |
| "تاریخ شروع" / "تاریخ پایان" | `startDate` / `endDate`, epoch millis via `PersianDateFormatter.formatTimestamp` |

Two things are **intentionally fixed for now, not derived from API data**:
- The status pill always reads "ارسال نشده" — not switched off `confirmed`. `NotExistRequestDN.confirmed`
  still carries the real API value (kept for whenever the pill is revisited), but
  `NotExistRequestPR` doesn't surface it at all.
- The "حذف"/"ویرایش" (`TaminOutlinedButton`, `colors.dangerBg/dangerText/dangerBorder` and
  `colors.blueBg/blueText`) buttons render unconditionally on every card, not gated by status.

Edit is still **UI/Intent/Event skeleton only** — `OnEditNotExistRequestClicked` routes through
the ViewModel to `HistoryObjectionEvent.NavigateToEditNotExistRequest`, an empty branch in
`HandleHistoryObjectionEvents` in `HistoryObjectionScreen.kt`, since there is no edit
destination (the Stepper) built yet.

Delete has a real confirmation dialog (`OnDeleteNotExistRequestClicked` →
`HistoryObjectionUiState.deleteConfirmationRequestNumber`/`.deleteConfirmationRowIndex` →
`DeleteConfirmationDialog`, dismiss/confirm via `OnDeleteConfirmationDismissed`/`OnDeleteConfirmed`)
and the confirm path calls `DeleteHistoryObjectionNotExistRequestUseCase(requestNumber, rowIndex)`
for real. `HistoryObjectionViewModel.handleDeleteConfirmed()`: guards double-tap via
`if (uiState.value.isDeleting) return@flow` (same reasoning as the stepper's `isSubmitting`
guard — `flatMapMerge` runs intents concurrently), hides the dialog immediately, bails with
`PartialState.Error(...)` if `rowIndex` is `null` (see "`deletenotexist` path shape" above), then
on success re-invokes the same `loadHistoryObjectionData()` the initial screen-open `Load` uses —
a full status+list reload rather than filtering the deleted item out of `notExistRequests`
locally, so the list and the "has active request" gate both stay authoritative after a delete.

`state.error` is now actually rendered (`ErrorStateView`, dismissed via `OnErrorDismissed` →
`PartialState.ErrorDismissed`) — earlier revisions set `error` in state but nothing displayed
it.

### `getnotexistrequests` pagination is not real — don't wire up `Paginator`

A captured request against this endpoint used `page=0&start=0&limit=10&filter=[]&sort=[]`
(the same shape `ApiQueryBuilder`/`ApiQueryParamDN` already produce), but the params were
found not to reliably page the backend result set. Rather than build the full
`Paginator`/`OnLoadMore`/`PagingFooter` machinery MyInbox uses ([[Pagination]]), this reuses
`apiQueryBuilder.defaultQuery()` (`ApiQueryParamDN(page = 0, start = 0, limit = 10)`, matching
the captured real request exactly) and returns the whole list in one shot — `HistoryObjectionRepositoryImpl`
injects `ApiQueryBuilder` directly the same way `PersonalInboxRepositoryImpl` does.
`HistoryObjectionRepository.getNotExistRequests()` therefore takes no query parameter at all —
there is no "load next page" concept on this screen. Note the default `limit = 10` means a user
with more than 10 not-exist requests would silently see only the first page; bump the query if
that turns out to matter in practice.

The **detail read** half is a separate, still-unrelated endpoint —
`historyprotest-services/getprotestresult/{referenceId}` in `UserRequestApiService`, mapped
through `FollowUpObjectionHistoryDTO` → `FollowUpObjectionDetailDN`, and shown by
`:feature:userRequest`. `historyprotest-services/savenotexist` (the write/submit endpoint) is
now implemented — see "`savenotexist` request shape" above and "The Stepper" below.
`old_android/`, which [[Reference-old-android]] names as the source of truth for legacy API
shapes, was not present on the machine this repo lives on, so the field set came from a real
captured device request instead.

## The Stepper (add/edit a not-exist request)

Reached from the list screen's `NavigateToAddNewObjection`/`NavigateToEditNotExistRequest`
events → `HistoryObjectionStepperRoute(requestNumber: String? = null)`
(`feature/history-objection/.../Navigation.kt`, registered in the central NavHost). Lives under
`feature/history-objection/.../ui/stepper/` — `HistoryObjectionStepperScreen`/
`HistoryObjectionStepperViewModel`/`HistoryObjectionStepperContract`, one file per step
(`BranchInfoStep`/`WorkshopInfoStep`/`RecordInfoStep`). Architecturally mirrors
`feature/addDependent` (not `feature/orotez-protez`'s sealed-step model) — `currentStep: Int`,
`StepIndicator` + `TaminBottomSheet` both reused as-is, and a `BottomSheetTarget`-style enum
(`HistoryObjectionBottomSheetTarget`) resolving picker selections back to intents exactly like
`AddDependentScreen.resolvePickerSelection`.

**Step 1** (اطلاعات شعبه) needed a 4th and 5th backing piece beyond what already existed:
- Province: reused `CityProvinceRepository.getProvinces()` as-is (new thin `GetProvincesUseCase`
  wrapper only, since no use case existed for it before — `AddDependent`/`StudentInsuranceContract`
  inject the repository directly, an inconsistency not propagated here).
- City-by-province: **new** — `CommonApiService.getCitiesByProvince` hits
  `special-insured-services/cities`, a different endpoint from the existing `getCityName`
  (`proxy/models/city/`) used by `GetCitiesUseCase` elsewhere. Filters by
  `FilterProperty.PROVINCE_CODE_CITY` (`"provincecode"`, deliberately lowercase — that
  endpoint's own response field is already lowercase, unlike the shared `PROVINCE_CODE`
  enum entry which is camelCase for other endpoints). Reuses the same enum entry as
  `proxy/models/city`'s province filter rather than adding a second one — a prior version of
  this code added a separate `CITY_LIST_PROVINCE_CODE` entry with the identical `"provincecode"`
  `@SerialName`, which kotlinx.serialization rejects as a duplicate serial name within one enum
  class. Cached the same way as branches
  (`CityProvinceDao.getCitiesByProvinceCode`/`replaceCitiesForProvince`).
- Branch: reused `ContractsRepository.getBranches(cityCode)`/`GetBranchesUseCase` as-is (already
  used by `orotez-protez`).
- Insurance type: **new** — `CommonApiService.getInsuranceTypes` hits
  `proxy/models/insurance-type`, added to `CommonRepository`/`CommonRepositoryImpl` (the existing
  home for generic `CommonRemoteDataSource`-backed lookups like `getJobTitle`/`getBeneficiary`),
  not a new dedicated repository. Network-only, no Room cache — a deliberate default for this
  small reference list, not yet confirmed against a real offline requirement.
- `TaminBottomSheetType` gained `BRANCH`/`INSURANCE_TYPE` entries (`showSearch = true`) so those
  two pickers render as a searchable list like `PROVINCE`/`CITY`, not `CUSTOM`'s chip layout.

**Steps 2/3** are local form state only (workshop id/name/employer/address; start/end date via
`TaminJalaliDatePicker` + `PersianDateFormatter`; work days) — no new network code.

**Edit mode** re-calls `GetHistoryObjectionNotExistRequestsUseCase()` and matches client-side —
there's still no per-id GET, so this reuses the same list data the list screen just fetched
rather than adding a new endpoint. The match key is `(requestNumber, rowIndex)`, not
`requestNumber` alone: one submission can list several missing periods sharing the same
`requestNumber`, distinguished only by `rowIndex` (the same composite key `deletenotexist`
already needs — see above). An earlier revision matched by `requestNumber` only, which silently
populated the stepper from whichever row happened to come first in the list whenever a
`requestNumber` had more than one row — a real bug reported as "correct data doesn't transfer to
the steps on edit." `HistoryObjectionStepperRoute` now carries `rowIndex` alongside
`requestNumber`, threaded through `HistoryObjectionIntent.OnEditNotExistRequestClicked` →
`HistoryObjectionEvent.NavigateToEditNotExistRequest` → the route → `HistoryObjectionStepperIntent.Load`
→ `HistoryObjectionStepperViewModel.loadEditModeData()`. `rowIndex == null` still falls back to
matching by `requestNumber` alone (there's nothing else to disambiguate on legacy/incomplete
data), so this only tightens the match when a `rowIndex` is actually available — see
`HistoryObjectionStepperViewModelTest.editMode_withSharedRequestNumber_usesRowIndexToPickTheRightRow`.

**`OnConfirmClicked`** (step 3's "ثبت" action) calls `SaveHistoryObjectionNotExistRequestUseCase`.
`HistoryObjectionStepperViewModel.toSaveNotExistRequestDN()` (a private `HistoryObjectionStepperState`
extension) builds the request from the accumulated 3-step state and returns `null` if anything
required is missing — mirroring `OrotezProtezViewModel.toSaveShortTermOrthosisRequestDN()` — in
which case a `ShowMessage` event fires instead of calling the use case. On success the screen shows
a `TaminConfirmationDialog` (matching `OrotezProtezScreen`'s `OrotezProtezSubmitSuccessDialog`
pattern) and `OnSubmitSuccessAcknowledged` sends `NavigateBack`. Guarded against double-tap via
`if (uiState.value.isSubmitting) return@flow` in `handleConfirmClicked()` (`flatMapMerge` runs
intents concurrently — see [[MVI-Pattern]]).

### Gotcha: cache-then-network use cases must be `.collect`ed, not `.first()`'d

`GetCitiesByProvinceUseCase`/`GetBranchesUseCase` wrap cache-then-network repository methods
(`CityProvinceRepositoryImpl.getCitiesByProvince`/`ContractsRepositoryImpl.getBranches`) that
emit **twice**: once with whatever's already in the local Room cache (stale, partial, or empty),
then again with the fresh network result — and the second emission never arrives if the caller
only takes the first one. `HistoryObjectionStepperViewModel.handleProvinceSelected`/
`handleCitySelected` originally called `.first()` here, which silently returned only the
pre-network cache snapshot — surfaced as a real bug (selecting a province with many cities showed
only a couple, matching whatever a few other cities happened to already be cached from). Fixed to
`.collect { emit(...) }` inside a `try/finally`, matching the established real-world precedent for
this exact situation: `StudentInsuranceContractViewModel.loadBranches()`. Because the underlying
Room `Flow` never completes on its own (it keeps observing table changes), this collecting flow
never completes either — that's expected and already how the codebase's own precedent behaves,
not something to "fix" further.

## Notes

- Dates are held in stepper state as plain `Long?` epoch-millis (`startDateTimestamp`/
  `endDateTimestamp`, set from `TaminJalaliDatePicker`'s `(year, month, day)` callback via
  `PersianDateFormatter.toEpochMillis(...)`) — there is no `JalaliDate` class or `toApiFormat()`
  helper anywhere in this codebase (an earlier revision of this page assumed both existed). The
  `savenotexist` request wants the epoch millis as a plain string, so
  `SaveNotExistRequestDN.toDTO()` in `core-data`'s `HistoryObjectionMapper.kt` does nothing more
  than `.toString()` the `Long`.
- Validation is derived (`missingFields`, `isDateRangeInverted` are computed properties on
  the state), while `invalidFields` holds only what the user has already been shown. The
  submit button stays enabled while the form is incomplete so the tap can mark the offending
  fields — a disabled button would leave the user with no explanation.
- **This applies to the list screen's own submit, not the stepper's.** The **stepper's**
  `OnConfirmClicked` (step 3's "ثبت", covered above) calls `savenotexist` per declared period. The
  **list screen's** `OnSubmitClicked` (with its free-text `description` field) is the separate
  "confirm and send the whole declared list to Social Security" action — see "Confirm-and-send
  (list screen)" below.
- `HistoryObjectionViewModel` no longer loads its list in an `init` block — it's loaded via
`LaunchedEffect(Unit) { viewModel.sendIntent(Load) }` in `HistoryObjectionScreen` instead. This
matters because the ViewModel is scoped to the list route's nav back stack entry and survives a
round trip through the stepper (add/edit) unchanged; an `init`-only load would only ever run
once, so after editing a record and popping back, the list would still show pre-edit data. The
screen composable itself is disposed while the stepper is on top and freshly recomposed on
return, so a `LaunchedEffect(Unit)` there re-fires exactly once per re-entry — first appearance
and every return from the stepper alike. This was a repo-wide gap (no other list+add/edit-stepper
feature in this codebase reloads on return either — `feature/profile`'s dependents list has the
identical bug, unaddressed) rather than a pattern already established elsewhere; there's no
`savedStateHandle`-based "did something change" signal anywhere in the app, so this reloads
unconditionally on every re-entry rather than only after an actual save.

The `isLoading` skeleton condition in `HistoryObjectionScreen.kt` keys on `isLoading` alone
  (not `isLoading && notExistRequests.isEmpty()`) — `checkStatusNotExist()` and
  `getNotExistRequests()` run concurrently via `merge()` in `loadHistoryObjectionData()`, so a
  fast list response arriving before the status check resolves must not render the interactive
  list/Add button early; `isLoading` only flips to `false` once both finish.

## Confirm-and-send (list screen)

Tapping the list screen's bottom "تأیید و ارسال به تأمین اجتماعی" button (`OnSubmitClicked`) no
longer just fires a no-op event — it shows a confirmation dialog first
(`showSubmitConfirmationDialog`, "تأیید و ارسال درخواست" / "ارسال درخواست" vs. "انصراف"). Tapping
"ارسال درخواست" (`OnSubmitConfirmed`) chains two endpoints, both new:

```
historyprotest-services/confirmnotexist   POST  @Body List<ConfirmNotExistItemDTO>  BaseDTO<Boolean>
historyprotest-services/finalconfirmnotexist POST @Body String (empty)              BaseDTO<String>
```

`ConfirmNotExistItemDTO(userDesc: String?)` — a **list of one item** carrying the screen's single
free-text `description` field (`null` if blank, matching the field's own nullability). The list
shape is the real backend contract (captured, not guessed, mirroring the `savenotexist` precedent
above) even though this screen only ever has one description to send; nothing here batches
multiple descriptions per row.

`finalconfirmnotexist` is only ever called automatically, immediately after `confirmnotexist`
resolves `true` — never on its own, and never if `confirmnotexist` resolves `false` (that case
surfaces `history_objection_confirm_send_rejected_error` through the normal `ErrorStateView`
instead). Its response is a tracking number string (`BaseDTO<String>`, not `Boolean` like every
other endpoint on this repository) shown via `HistoryObjectionUiState.trackingNumber` in a second
dialog (a copyable `NumericText` chip inside `TaminConfirmationDialog`'s `content` slot — the
component's first real caller of that slot). Acknowledging it ("متوجه شدم",
`OnTrackingNumberAcknowledged`) clears the dialog and re-runs `loadHistoryObjectionData()`, the
same full status+list reload the delete flow uses, since finalizing very plausibly flips
`checkStatusNotExist()` to `true` (blocking further declarations) — there's no cheaper way to
learn that without a dedicated endpoint.

Wiring: `HistoryObjectionRepository.confirmNotExist(description: String?)`/`.finalConfirmNotExist()`
→ `ConfirmHistoryObjectionNotExistUseCase`/`FinalConfirmHistoryObjectionNotExistUseCase` →
`HistoryObjectionViewModel.handleSubmitConfirmed()`, guarded by the same `isSubmitting`-flag
double-tap pattern as `handleDeleteConfirmed()`/the stepper's `handleConfirmClicked()`. The
`ConfirmNotExistItemDTO` → `List` wrapping happens in
`HistoryObjectionRemoteDataSourceImpl.confirmNotExist()` itself (core-network), not in a
`core-data` mapper — there's no domain (`DN`) model for a single optional string, so the usual
DTO/DN mapper-file convention doesn't apply here.

Related: [[Feature-Flags]] · [[Adding-a-Feature]] · [[Glossary]] · [[Debt-Objection-Status]]
