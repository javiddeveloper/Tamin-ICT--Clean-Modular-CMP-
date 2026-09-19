---
tags: [domain, gotcha]
---

# مستمری از کارافتادگی — Disability Pension Status Tracking

`FeatureFlag.DISABILITY_PENSION(17) · `DISABILITY_PENSION_PENSIONER`(107)` · `:feature:pensioner` → `ui/disabilityPension`
(UI stub only — `DisabilityPensionViewModel` is currently a no-op, `Init` intent does
nothing) · network/domain/data layers live in `PensionApiService`/`PensionRepository`
(not `Personal*`, except the dependents endpoint — see table below).

Ported from the legacy native app, `C:\Users\h_arabameri\AndroidStudioProjects\my-tamin-droid`
(see [[Reference-old-android]] — that repo, not `old_android/`, is the real legacy
reference on this machine). Old app screen:
`app/src/main/java/com/tamin/taminhamrah/ui/home/services/disabilityPension/DisabilityPensionViewModel.kt`
+ `model/DisabilityPensionDataModel.kt`. Old-app menu id **113** appears twice in
`menu.json` (a type-1/type-2 pair for employee vs. employer variants) — only one
`FeatureFlag` value exists here; if the two variants turn out to need different
request shapes, that will surface when the UI/ViewModel work starts.

## Status: network → domain → usecase layer complete; UI steps 1-7 of 7 implemented

| Old-app endpoint | Status | Where |
|---|---|---|
| `GET disability-request/personal` | ✅ done (pre-existing) | `PensionApiService.getDisabilityPersonalInfo` → `GetDisabilityPersonalInfoUseCase` |
| `GET disability-request/subdominant` (dependents) | ✅ done (pre-existing) | `PersonalApiService.getDisabilityDependentInfo` → `GetDisabilityDependentInfoUseCase` — lives in `Personal*`, not `Pension*`, unlike everything else in this feature |
| `GET pension-request/age` | ✅ done (pre-existing, shared with retirement) | `PensionApiService.getUserAge` → `GetUserAgeUseCase` |
| `POST disability-request` (save application) | ✅ done | `SaveDisabilityUserInfoUseCase` |
| `PUT disability-request/{requestId}` (final confirm) | ✅ done | `FinalConfirmDisabilityRequestUseCase` |
| `PUT disability-request/{requestId}` (save documents) | ✅ done | `SaveDocumentDisabilityUseCase` — same path as final confirm, different body/method name, both valid Ktorfit methods on `PensionApiService` |
| `GET disability-request/report` (medical commission PDF) | ✅ done | `GetMedicalCommissionPdfUseCase` |
| `GET medical-committee-demand/get-last-demand-details` (registered commission list) | ✅ done | `GetRegisteredMedicalCommissionUseCase` — user explicitly confirmed including this in scope despite being a larger, semi-shared DTO |
| `POST subdominants/transfer` (refresh/transfer dependents) | ✅ done | Added on `AddDependentApiService`/`AddDependentRepository` (not a new module — same `subdominants/*` resource family as `getActiveBranches`/`addNewDependent`) → `RefreshDependentsUseCase`. Legacy endpoint path used verbatim per explicit user decision (no request body, returns a `GeneralRes`-equivalent) |
| Screen/ViewModel/Contract | ✅ all 7 steps done | `feature/pensioner/.../ui/disabilityPension/*` — see below |

### UI progress

`DisabilityPensionContract` now tracks `currentStep: DisabilityPensionStep` (`Terms`, `Dependents`),
mirroring `PensionSurvivorContract`'s `PensionSurvivorStep` enum + `AnimatedContent` + bottom-bar
convention (full-width button on step 1, `SquareIconButton` previous + primary `Row` from step 2
on) instead of the old bare `Int` step constant.

- **Step 1 (Terms)** — unchanged from the prior session: `DisabilityPensionTermsStep.kt`.
- **Step 2 (Dependents, "افراد تحت تکفل")** — `DisabilityPensionDependentsStep.kt`. Loads via
  `GetDisabilityDependentInfoUseCase`, each card expand/collapse locally (own `Column`, not the
  `feature/profile` `DependentCard` — no cross-feature import per architecture rules). "افزودن
  تبعی جدید" navigates to the existing, already-wired `:feature:addDependent` wizard via a new
  `onNavigateToAddDependent` callback threaded through `Navigation.kt` →
  `TaminHamrahNavGraph.kt` (reuses the existing `AddDependentRoute`, no new route) — the Figma
  mock drew this as an inline bottom sheet, but `feature/profile`'s `DependentsListScreen` already
  established the "navigate to the full wizard" convention for the identical concept, so that was
  followed instead of a hand-built simplified form. On return, the list refreshes via an
  `ON_RESUME` lifecycle hook (`DependentsResumed` intent), mirroring
  `PensionSurvivorScreen`'s `refreshSurvivorsOnResume` pattern. "بروزرسانی افراد تبعی تحت تکفل"
  shows a `TaminConfirmationDialog` before calling `RefreshDependentsUseCase` (guarded by
  `isRefreshingDependents` against double-submit, per [[MVI-Pattern]]). A confirmation checkbox
  gates `NextStepClicked` the same way step 1's terms checkbox does.

  **Tried and reverted (EM-2619 follow-up)**: replacing "افزودن تبعی جدید" with an inline inquiry
  bottom sheet matching the Figma mock (node `44:521`, fileKey `hYGzHjNiZRrUvPZNY2TKuA`) — relation
  chips + کد ملی + تاریخ تولد, calling `InquiryRegistryUseCase` then immediately
  `AddNewDependentUseCase` with no city/branch/document collection, appending the result straight
  into `state.dependents`. Built once, then reverted at the user's explicit request after they
  pointed out the flaw: `AddNewDependentUseCase`'s request (`RequestAddDependentDN`) is a real
  civil-registration payload — the old app (`my-tamin-droid`'s
  `AddDependentFragment`/`DependentsViewModel`, confirmed to also be a full multi-step stepper with
  no bottom-sheet precedent) always fills it completely (branch, city of birth/issue, required
  documents) before submitting. Sending that call with those fields null would very likely be
  rejected by the backend or create a broken record — not a legitimate simplification. Reverted
  back to the `onNavigateToAddDependent`/full-wizard behavior described above. **If an embedded
  (non-navigating) experience is wanted later**, it needs the *entire* sub-flow (inquiry →
  conditional education/commitment step → city/branch → documents) built inside this feature, not
  a shortcut around it — a materially bigger task than this bottom-sheet attempt, since
  `:feature:pensioner` cannot import `:feature:addDependent`'s UI and would need to duplicate its
  step components against the same core-domain use cases. Worth first checking whether the Figma
  file has follow-up screens after node `44:521` (city/branch/documents) that would confirm this
  was the design's actual intent — that wasn't verified (a Figma MCP rate limit was hit before it
  could be checked).
- **Step 3 (Identity & contact, "اطلاعات هویتی و تماس")** — `DisabilityPensionIdentityContactStep.kt`.
  Reuses the `DisabilityPersonalInfoDN`/`PR` already fetched by `GetDisabilityPersonalInfoUseCase`
  at `Init` (for step 1's commitment text) instead of re-fetching on step transition — one network
  call now backs both step 1's applicant name/gender and step 3's whole read-only info grid.
  Age ("سن") is resolved by chaining the previously-unused `GetUserAgeUseCase` (`pension-request/age`,
  shared with retirement) right after that load and parsing its comma-separated `"years,months,days"`
  response — same technique `feature/pensionSurvivor`'s `PensionSurvivorViewModel.toPresentationAgeParts`
  already uses for the identical shape (can't share the code directly — feature-local/private — so
  this is a smaller, years-only port, since the design only shows years). Two collapsible read-only
  info grids (4 fields collapsed, 6 more when "نمایش جزئیات" is expanded) plus two **editable**
  fields — تلفن ثابت / آدرس — validated on `NextStepClicked` against the exact legacy rules
  (`DisabilityPensionFragment.checkValidEnterStepIdentityInfo`: phone non-blank + starts with "0" +
  11 digits; address non-blank + ≥10 chars + no Latin letters/symbols) via `LandlinePhoneError`/
  `AddressError` enums in state — the enum-in-state-then-`stringResource`-in-Composable pattern
  (not a ViewModel-side `getString()`) is deliberate, see [[ui-design-system]]'s `getString()`-in-
  ViewModel test hazard. A confirmation checkbox gates `NextStepClicked` the same way steps 1-2 do.
  **Also fixed in this pass**: `DisabilityPersonalDN.toPresentation()`'s `dateOfBirth` had the exact
  same raw-epoch-millis-as-string bug already fixed on `DisabilityDependentDN` — routed through
  `PersianDateFormatter.formatTimestamp` now that step 3 is the first screen to actually display it.
- **Step 4 (Workshop & branch, "اطلاعات کارگاه و شعبه")** — `DisabilityPensionWorkshopStep.kt`. Figma
  node `44:829` (fileKey `hYGzHjNiZRrUvPZNY2TKuA`). No new network call — reuses the same
  `state.identityInfo` (`DisabilityPersonalInfoPR`) already fetched at `Init` for steps 1/3: the
  read-only 2×2 grid (نام استان/کد و نام آخرین شعبه/شماره کارگاه) reads
  `provinceName`/`branch`/`branchName`/`work?.workshopId` straight off it, matching the legacy
  app's `DisabilityPersonalInfoResponse.getBranchInfo()` exactly (verified against
  `my-tamin-droid`'s `DisabilityPensionFragment.kt` — the branch/workshop-number grid is
  read-only there too, and the 4 editable fields below it are pure user input with no
  prefill, confirmed by `initialBranchInfoStep()`/`checkValidEnterStepBranchInfo()` in that
  file: only نام کارگاه and آدرس کارگاه are validated (non-blank only, no length/format rules
  unlike step 3's phone/address), نوع فعالیت and نام کارفرما are optional with no validation).
  The Figma mock shows sample text already filled into all four editable fields (dark, not
  placeholder-gray) purely as mockup dummy data — not a real prefill source, so the
  implementation starts all four blank per the legacy data flow. A confirmation checkbox gates
  `NextStepClicked` the same way steps 1-3 do. `workshopNameError`/`workshopAddressError` are
  plain `Boolean`s rather than an error-reason enum (unlike step 3's `LandlinePhoneError`/
  `AddressError`) since the only rule is "non-blank" — one reason doesn't need an enum.
- **Step 5 (Insurance record & medical commission opinion, "سوابق و رأی کمیسیون")** —
  `DisabilityPensionCommissionRecordStep.kt`. Figma nodes `44:939` (base state), `44:1047`
  ("بله، معترض هستم" selected state), `44:1198` (registered-requests bottom sheet), fileKey
  `hYGzHjNiZRrUvPZNY2TKuA`. Two cards:
  - **"سابقهٔ بیمه‌ای شما"** — day/month/year `StatTile`s + a highlighted total-days tile. Backed
    by `GetTalfighInfosUseCase` (`HistoryRepository.getTalfighInfos`), the **same** endpoint
    `:feature:history`'s تلفیق سوابق tab already calls — not a new disability-specific endpoint.
    Legacy app's `DisabilityPensionViewModel.getCombinedRecordList()`/`CombinedRecordModel`
    (`historyYears`/`historyMonths`/`historyDays`/`sumHistoryYears`) maps field-for-field onto
    `TalfighInfoItemDN`'s identically-named fields — confirmed by comparing both, not guessed —
    so this reuses the existing use case/DN rather than porting a second copy of the same
    response shape. Only `list.firstOrNull()` is read, matching the legacy app's `list[0]` usage.
    "اعتراض به سوابق" is a static link, not wired to real navigation yet — tapping it shows a
    "به‌زودی" toast. The legacy fragment's equivalent action opens
    `ObjectionInsuranceHistoryFragment`/`action_to_objectionInsuranceHistoryFragment`, which is a
    **different** model (`ObjectionInsuranceHistoryModel`) from the already-ported
    `:feature:history-objection` (`NotExistRequestDN`, flag `10`, "اعتراض به سوابق ناموجود") —
    see [[History-Objection]]'s flag `42` row ("اعتراض به سابقه کسری دار", not implemented). Do
    not wire this link to `:feature:history-objection`'s route; they are not the same feature
    despite the similar Persian label, and flag `42`'s backend/UI hasn't been ported here yet.
  - **"نظر کمیسیون پزشکی"** — a two-option radio choice (`hasCommissionObjection: Boolean?`,
    `null` = unanswered). Selecting "بله، معترض هستم" shows a `BannerCard(Warning)` explaining the
    objection must be filed in person at a branch, and disables "مرحلهٔ بعدی" — the *only* step in
    this wizard where the primary button's `enabled` depends on a field value rather than a
    separate confirmation checkbox (no checkbox exists on this step at all, unlike steps 1-4).
    Selecting "خیر، معترض نیستم" (or leaving it unanswered) keeps the button enabled. "درخواست‌های
    ثبت‌شده" opens a `ModalBottomSheet` (`DisabilityPensionRegisteredRequestsSheet.kt`, matching
    `EdictPensionerSheet.kt`'s plain-list convention rather than the selection-oriented
    `TaminBottomSheet`) backed by `GetRegisteredMedicalCommissionUseCase`, client-filtered to
    `demandTypeCode == "01"` per this file's own earlier guidance (the use case returns every
    commission-referral type, not just disability's). Each row shows only `demandSaveDate`
    (formatted) and `commissionPollDesc` ("رای: ...") — `RegisteredMedicalCommissionDN` has no
    confirmed field for "کمیسیون پزشکی بدوی"-style stage naming (`demandStage`/`referTypeCode`
    exist but their value mapping is unconfirmed), so the row title is a static "کمیسیون پزشکی"
    label rather than a fabricated stage name; revisit if a real API sample clarifies the mapping.
    "مشاهده فایل" opens `TaminPdfViewer` (`core-ui`) — the **same** full-screen PDF dialog
    `PayRollScreen`/`EdictScreen` already use, backed by `GetMedicalCommissionPdfUseCase(lastWorkshop
    = identityInfo.work.workshopId)` (the endpoint's query param is literally named `lastWorkshop`
    in `PensionApiService`, ported verbatim from the old app — it takes the workshop id, not a
    request id). New `RegisteredMedicalCommissionPR` (`core-ui/.../model/pension/disabilityRequest/medicalCommission/`)
    + `RegisteredMedicalCommissionDN.toPresentation()` (`core-ui/.../mapper/pension/PensionInquiryMapper.kt`,
    same file as every other `model.pension.*` DN→PR mapper) were added since none existed yet.
- **Step 6 (Documents, "مدارک لازم")** — `DisabilityPensionDocumentsStep.kt`. No Figma node was
  actually consulted for this step's visuals — the user explicitly directed that it be built to
  visually and structurally match `:feature:orotez-protez`'s document-upload step rather than a
  fresh design, and to base the upload mechanics on that feature's already-shipped
  camera/gallery-picker flow. Since that flow has since been extracted into shared `core-ui`
  components (`ui/components/document/TaminDocumentUploadCard.kt`,
  `ui/components/document/TaminDocumentSourceSheet.kt`, `ui/components/LiquidWaveProgressBar.kt` —
  also already reused by `:feature:taminServices`'s `Step6DocumentSubmitStep.kt`), this step
  consumes those directly instead of re-duplicating orotez-protez's now-superseded feature-local
  copies, per [[architecture]]'s "search core-ui components first" rule. Camera permission goes
  through the shared `com.tamin.taminhamrah.util.CameraPermission`/`rememberCameraPermission()`
  (core-ui, expect/actual) — **not** `:feature:orotez-protez`'s own older feature-local
  `camera/CameraPermission.kt` copy, which predates the shared one and is now a known duplicate
  (see `:feature:pregnancyPay`/`:feature:requestPaymentForIllDays`/`:feature:taminServices` for
  other current consumers of the shared version).

  Document checklist (`DisabilityPensionDocumentChecklist`-equivalent, in
  `ui/disabilityPension/contract/DisabilityPensionDocumentModels.kt`) is 5 items, all **optional**,
  one image per type — ported from the legacy app's `DisabilityPensionDataModel.documentsTitle` /
  `Constants.kt` document-type codes (confirmed via `DisabilityPensionFragment.kt`'s
  `initialUploadDocumentStep()`/`chooseImage()`/`checkFileUploadedSize()`, which caps the list at
  5 — one per type — and shows the "add" row until all 5 are filled):

  | Code | Title |
  |---|---|
  | `13` | نظریه کمیسیون پزشکی بدوی |
  | `14` | نظریه کمیسیون پزشکی تجدیدنظر |
  | `15` | گزارش حادثه |
  | `16` | گزارش بازرسی کار |
  | `17` | آراء قطعی مراجع قضایی |

  These are the same five documents step 1's rules dialog already lists descriptively
  (`disability_pension_rules_doc_1..5`) — step 6 adds shorter, card-title-appropriate string
  resources (`disability_pension_document_*`) rather than reusing those long sentences.

  Each card picks an image (camera or gallery, via shared `FileKit` launchers) and immediately
  uploads it through the existing generic `UploadImageUseCase` (`ContractsRepository.uploadImage`,
  already used by orotez-protez) to obtain a `guid` — same immediate-upload-for-guid pattern as
  orotez-protez, even though the *legacy* app only persisted the aggregate document list to the
  server at final submission; the per-image GUID upload is a technical prerequisite either way
  since `DisabilitySaveDocumentDN.pensionRequestDocList` needs one guid per document type. Image
  validation (jpeg-only, ≤2MB, duplicate-content rejection across document types) and the
  uploading/uploaded card states are a straight port of `OrotezProtezViewModel`'s equivalent
  logic — but the **rejection-reporting path was corrected to match `:feature:pregnancyPay`'s
  `PregnancyPayViewModel`** instead, after the user asked for a careful re-check against that
  screen: a pre-upload validation rejection (wrong format, unreadable file, duplicate content)
  emits a page-level `DocumentPickRejected` (rendered as one plain error line below the whole
  document list, document card left untouched) rather than flipping that specific card to a
  `Failed` state — orotez-protez's original `emitDocumentRejection` helper did the latter, which
  this feature initially copied verbatim before the correction. The per-card `Failed` state (with
  inline message + "tap to retry") is reserved for the one case both features agree on: the
  `uploadImageUseCase` call itself throwing (network/server error) — that catch block already
  matched pregnancyPay from the start. `handleDocumentRemoveClicked` also now emits
  `DocumentSourceSheetDismissed` after clearing a document, mirroring
  `PregnancyPayViewModel.handleDocumentRemoveClicked`'s `PickerChanged(NONE)` (a defensive no-op
  in both features today, since removal is only reachable via the card's own delete icon, never
  through the source-selection sheet itself). `UploadImageUseCase` is **not** mocked by the
  `disabilityPensionMock` package (see below) — it's a generic, already-working endpoint unrelated
  to disability-specific APIs, same reasoning as leaving `GetTalfighInfosUseCase` unmocked.

  The document-source bottom sheet (`TaminDocumentSourceSheet`) is opened only from the card's
  `onCardClick`, which is itself `null` (disabled) while that card is `Uploading` or `Uploaded` —
  again ported from `PregnancyPayScreen`'s identical conditional — so removal for an uploaded
  document is only ever reachable through the card's dedicated delete icon (`onDeleteClick`), never
  through the sheet. Accordingly `showRemoveOption` on that sheet call is hardcoded `false` and no
  `onRemove` callback is passed at all, matching `PregnancyPayScreen`'s call exactly — a duplicate
  "remove" affordance in both places at once was flagged by the user as wrong and removed.

  Clicking "مرحلهٔ بعدی" shows a plain confirmation dialog (title "تایید مدارک", message "مدارک
  مورد نیاز را بارگذاری کرده‌ام یا نیازی به ارسال مدارک و مستندات وجود ندارد.") rather than an
  inline checkbox — this matches the legacy app's `showConfirmDialog()` at this exact step, the
  **only** step in the whole wizard that gates progression with a dialog instead of a checkbox,
  since none of the 5 documents are actually required. Confirming just advances the state machine;
  `DisabilityPensionViewModel.handleIntentInternal`'s `ConfirmDocumentsSubmission` branch carries a
  `// TODO(EM-2619): navigate to step 7 once its design is delivered.` — the real 3-call submit
  chain (see below) is **not** wired here, since step 7 (final review/confirm) doesn't exist yet
  and is where that chain is expected to actually fire.
- **Step 7 (Final review & submit, "ثبت نهایی")** — Figma nodes `44:1386` (summary screen), `44:1523`
  (submitting overlay), `44:1549` (success dialog), fileKey `hYGzHjNiZRrUvPZNY2TKuA`.
  `DisabilityPensionSummaryStep.kt` renders 6 review rows — one per prior step (Terms, Dependents,
  IdentityContact, Workshop, CommissionRecord, Documents) — each showing a value pulled straight off
  already-loaded state (no new network call): terms → static "تأیید شد", dependents → `dependents.size`,
  identity → `landlinePhone` (via `NumericText`), workshop → `workshopName.ifBlank { employerName }`
  (a judgment call — the Figma mock's sample value read as a company name, and `workshopName` is the
  more title-appropriate field of the two; revisit if product feedback disagrees), commission → "بدون
  اعتراض"/"معترض" off `hasCommissionObjection`, documents → `uploaded/total` count off
  `DisabilityDocumentChecklist`. Each row's pencil icon is fully clickable and dispatches
  `EditSummarySectionClicked(step)` → `PartialState.StepChanged(step)`, jumping straight to that step
  (the shared `AnimatedContent` already animates backward/forward off ordinal comparison, so no new
  transition logic was needed). A final confirmation checkbox (dynamic text embedding
  `applicantFullName`) gates the submit button the same way steps 1-4 gate `NextStepClicked` —
  reusing that same intent rather than a new one, so `Summary` slots into `DisabilityPensionViewModel
  .handleNextStepClicked`'s existing `when` like every other step.

  Submitting fires the exact 3-call chain from the "3-step submit flow" section below, sequentially,
  short-circuiting on the first failure (guarded by `isSubmitting` against double-submit per
  [[MVI-Pattern]]): `SaveDisabilityUserInfoUseCase` → `SaveDocumentDisabilityUseCase(requestId,
  ...)` → `FinalConfirmDisabilityRequestUseCase(requestId, ...)`. The request body for step 1 is built
  in `DisabilityPensionViewModel.buildSaveInfoRequest()`, sourced from `identityInfo`
  (`DisabilityPersonalInfoPR`, already loaded at `Init`) for the read-only fields and from `state`
  (`landlinePhone`/`address`/`workshopName`/`employerName`/`activityType`/`workshopAddress`) for the
  user-entered ones, matching old-app `DisabilityPensionDataModel.loadRequestInfo()`/
  `checkValidEnterStepBranchInfo()` field-for-field. Two gaps versus the legacy model, both forced by
  what the KMP DN actually carries (not fixable without a backend/DN change, so left as documented
  approximations rather than blocked on): `birthDate` needs the raw epoch-millis `Long` the
  presentation model doesn't expose (`DisabilityPersonalPR.dateOfBirth` is pre-formatted to a Jalali
  string) — worked around with a ViewModel-private `applicantBirthDate: Long?` cached from the raw
  `DisabilityPersonalInfoDN` at `loadApplicantInfo()`, never surfaced in `UiState`; and `gender` wants
  the legacy numeric `genderCode`, which this DN never captured (only `genderDesc`, a description
  string) — passed through as-is since there is no code-lookup available. Document GUIDs for step 2's
  body come from a new `ImmutableMap<String, DisabilityDocumentState>.toDisabilityDocumentDNs()`
  extension (`contract/DisabilityPensionDocumentModels.kt`), filtering to `Uploaded` entries only.
  `DisabilityPensionDocumentsStep`'s `ConfirmDocumentsSubmission` handler (previously a
  `// TODO(EM-2619)` stub) now just emits `StepChanged(Summary)` — the real submit chain lives here,
  on step 7, exactly as the earlier TODO comment anticipated.

  UI for the two non-step-body designs: `DisabilityPensionSubmittingDialog` (a non-dismissable
  `Dialog`, matching node `44:1523`, shown while `state.isSubmitting`) and
  `DisabilityPensionSubmitSuccessDialog` (built on the existing `TaminConfirmationDialog` + its
  `content` slot, matching node `44:1549`) live in a new
  `components/DisabilityPensionSubmitDialogs.kt`. The success dialog's dashed tracking-code chip
  reuses `Modifier.dashedOutline` (`core-ui/.../RecordCardParts.kt`, the same idiom
  `feature/girlSurvivor`'s details step already uses) plus the existing `CopyIconButton`/
  `rememberCopyAction` pair (`core-ui/.../CopyIconButton.kt`) — the whole chip is one clickable copy
  target, with `CopyIconButton(interactive = false)` as the icon-only visual per that component's own
  documented convention for "an enclosing row already copies this value." On acknowledgement
  (`SubmitSuccessAcknowledged` intent) the ViewModel sends `DisabilityPensionEvent.NavigateBack`,
  which `DisabilityPensionScreen` wires straight to the screen's own `onBack` callback — same
  "acknowledge closes the whole flow" pattern as `:feature:orotez-protez`'s
  `OnSubmitSuccessAcknowledged`/`NavigateBack`, not a new pattern. The tracking code shown/copied is
  `DisabilityRequestRefDN.refCode` (falling back to the numeric `id` as a string if the backend ever
  omits `refCode`).

  Contract additions (`DisabilityPensionContract.kt`): `DisabilityPensionStep.Summary` (7th enum
  value), `isFinalConfirmed`/`showFinalConfirmationError`/`isSubmitting`/`submitTrackingCode` on
  `UiState`, and `FinalConfirmedChanged`/`EditSummarySectionClicked`/`SubmitSuccessAcknowledged`
  intents. No new Koin wiring was needed — `SaveDisabilityUserInfoUseCase`/
  `SaveDocumentDisabilityUseCase`/`FinalConfirmDisabilityRequestUseCase` were already registered in
  `core-domain`'s `DomainModule.kt` from the earlier network/domain phase; `viewModelOf(::
  DisabilityPensionViewModel)` in `PensionInquiryModule.kt` resolves the three new constructor
  params automatically.

  `DisabilityPensionViewModelTest.kt`'s `FakeDisabilityPensionRepository` already had
  `saveDisabilityUserInfo`/`saveDocumentDisability`/`finalConfirmDisabilityRequest` stubbed as
  `error("not used...")` from an earlier pass — this step's tests swapped those for real fakes
  (configurable result/error + call-args capture) and added 3 new test cases covering the
  confirmation-checkbox gate, the full 3-call success path (asserting the tracking code and the
  request id threaded through calls 2-3), and a step-1 failure short-circuiting the chain.

### TEMPORARY: manual-QA mock data (no test account yet)

The developer has no backend test account that can reach this feature, so
`core/core-domain/.../repository/disabilityPensionMock/` (`DisabilityPensionMocks`,
`MockDisabilityPensionRepository`, `MockDisabilityPersonalRepository`,
`MockDisabilityAddDependentRepository`) + `feature/pensioner/.../di/DisabilityPensionMockModule.kt`
+ one line in `shared/.../di/Koin.kt`'s `sharedModules` fake the handful of disability-pension-
specific repository methods so all 7 steps can be clicked through by hand as they get built.
Each `Mock*Repository` decorates the real repository via Kotlin's `by` delegation — only the
1-2 disability-specific methods are overridden, everything else (payroll, retirement, the
`:feature:addDependent` wizard, etc.) still hits the real backend through the same instance.
**This must be removed before merging** — delete the `disabilityPensionMock` package, delete
`DisabilityPensionMockModule.kt`, and remove its line from `sharedModules`. First attempt at this
subclassed the `UseCase` classes instead (requiring `open` on 4 production files) — rejected by
the user in favor of this decorator approach specifically because it touches zero production
files; see [[Mock-Data-Pattern]] if adding mocks for a future feature.

Step 5 extended `MockDisabilityPensionRepository` with a `getRegisteredMedicalCommission` fake
(plain list data, easy to fake) but deliberately left `getMedicalCommissionPdf` un-mocked — faking
a byte-perfect renderable PDF (`PdfDownloadDN`'s `ByteReadChannel`, checked by `TaminPdfViewer`'s
`looksLikePdf()` magic-header + Android `PdfRenderer` needing a structurally valid file) was judged
not worth the effort for a QA-only aid that gets deleted before merge; tapping "مشاهده فایل" during
manual QA hits the real backend and falls back to `TaminPdfViewer`'s already-built "file
unavailable" empty state instead of crashing. `GetTalfighInfosUseCase` (insurance-record summary)
was also left un-mocked since it belongs to the already-shipped `:feature:history`, not to
disability-specific endpoints — if it turns out to be unreachable with the developer's test
account too, extend `MockDisabilityPensionRepository`'s sibling scope to a `HistoryRepository`
decorator rather than assuming it's covered.

### Gotcha: the dependent's relation label is not a server field — it's computed from `tendencyCode`

`personal.relation` on the `disability-request/subdominant` payload (`PersonaDTO.relation`) is
**not actually populated by the backend** — confirmed by the user testing the same account
against both apps: legacy correctly shows "همسر" (spouse), this app showed "-" when the UI first
read `DisabilityDependentDN.relation` directly. The legacy app never reads that field for
display either — `DisabilityPensionFragment.onDependentInfoResponse` computes the label
client-side via `Utility.getTendencyResId(tendencyCode, genderCode)`, a lookup table keyed on
`relationWithTamin.tendencyInfo.baseTendency.tendencyCode` (+ `personal.gender.genderCode` for
the gender-ambiguous codes), and only then writes the computed string into that same field name
for convenience — that's why it looked like a real API field.

Fixed by: removing `DisabilityDependentDN`/`PR`'s bogus `relation: String?` field and replacing
it with the raw `tendencyCode`/`genderCode` pair (mirroring `SurvivorDependentDN`/`PR`'s existing
identical shape), then adding `feature/pensioner/.../ui/disabilityPension/relation/DisabilityRelationClassifier.kt`
— a small port of the same lookup table `feature/pensionSurvivor`'s `SurvivorRelationClassifier.relationTitleRes`
already implements (can't import it directly — cross-feature imports are disallowed — so this
duplicates just the `relationTitleRes` function, reusing the same shared `pension_survivor_relation_*`
string resources rather than duplicating strings too). If a third feature ever needs this same
lookup, it should be promoted to a shared location (`core-ui` or `core-domain`) instead of a third
copy-paste.

**That alone didn't fix it** — the user re-tested and still saw "-". The deeper bug: the whole
`tendencyCode`/`tendencyDescription` chain was **never deserializing at all**, on top of the
above. `TendencyInfoDTO.baseTendency`
(`core-network/.../model/personal/disabilityRequest/TendencyInfoDTO.kt`) was annotated
`@SerialName("relationWithTamin")` instead of `@SerialName("baseTendency")` — a copy-paste of
the outer nesting level's key. Confirmed against the reference-correct sibling model
(`core-network/.../model/subDominant/SubRelationWithTamin.kt`, used by the already-working
`feature/profile` dependents list): the real JSON nests `relationWithTamin.relationWithTamin.baseTendency.{tendencyCode,tendencyDescription}`
— three `relationWithTamin` keys is correct up to that point, but the field *inside* that JSON
key is `"baseTendency"`, not another `"relationWithTamin"`. Because of the wrong key,
`tendencyCode`/`tendencyDescription` silently deserialized to `null` for every dependent,
regardless of what the UI did with them. No existing test caught it — `getDisabilityDependentInfo`
had zero JSON-fixture coverage in `PersonalApiServiceTest.kt` before this; a regression test
(`getDisabilityDependentInfo should parse tendencyCode and genderCode from nested baseTendency`)
was added there. **Lesson**: when a nested DTO's `@SerialName` looks suspiciously identical to a
sibling/ancestor level's key, verify against a working reference model in the same package
family before trusting it — don't assume a matching `data class` shape means a matching JSON
key.

### Fixed: birth date rendered as a raw epoch-millis number

`DisabilityDependentDN.toPresentation()` (`core-ui/.../mapper/personal/PersonalMapper.kt`) used
to do `dateOfBirth?.toString() ?: ""`, i.e. stringify the raw Long timestamp — showing something
like `316310400000` instead of a date. Fixed by routing it through the already-shared
`PersianDateFormatter.formatTimestamp(...)` (`core-ui/.../util/PersianDateFormatter.kt`), the same
helper `SubdominantUiMapper.kt` already uses for the identical shape
(`SubdominantItemPR.birthDateJalali`). `DisabilityPersonalDN`/`SurvivorDependentDN` in the same
file still have the same `dateOfBirth?.toString()` pattern — not fixed here since neither is
consumed by a screen that displays the raw string yet (survivor's only feeds an age
calculation), but apply the same `PersianDateFormatter.formatTimestamp` fix there the moment
either one is.

New model package (both core-network DTOs and core-domain DN share the same package
path, per this repo's convention): `com.tamin.taminhamrah.model.pension.disabilityRequest`
(+ `.medicalCommission` subpackage for the commission-list DTO/DN, ~50 fields incl. two
nested lists). Mapper functions added to `core-data/.../data/mapper/PensionInquiryMapper.kt`
(that file, despite its name, already holds every other `model.pension.*` DTO↔DN mapper —
followed that precedent rather than creating a new mapper file).

## The 3-step submit flow — critical for the future ViewModel

The old app's `DisabilityPensionViewModel.saveAndConfirmRequest()` is **not** one API
call — it's a sequential 3-call chain, each gated on the previous succeeding, with a
specific hardcoded `status` value at each step:

1. `saveDisabilityUserInfo(body)` with `status = "3"` (initial request) → response
   carries the created `request.id` (`DisabilityRequestRefDN.id`, `Long`) and `refCode`.
   **This id is required for steps 2 and 3** — that's why `saveDisabilityUserInfo`
   returns a typed `DisabilityRequestRefDN?` here instead of a plain message string
   (unlike the structurally similar `saveSurvivorInfo`, which only needs to return a
   message because survivor pension's PUT calls don't chain off a freshly-created id
   the same way).
2. `saveDocumentDisability(requestId, body)` with `status = "4"` — uploads the document
   list built from whatever the user attached in the UI.
3. `finalConfirmDisabilityRequest(requestId, DisabilityFinalConfirmDN(id = requestId, status = "0"))`.

Any step failing short-circuits the chain in the old app and surfaces that step's own
error — the future ViewModel should replicate this rather than firing all three
concurrently (also required by [[MVI-Pattern]]'s non-idempotent-intent guard, since this
whole flow is a non-idempotent submit).

`getDisabilityPersonalInfo`'s response is also the source of most of `saveDisabilityUserInfo`'s
request fields — the old app's `DisabilityPensionDataModel.loadRequestInfo(...)` maps
`DisabilityPersonalInfoDataModel → DisabilitySaveInfoRequest` directly (name, national
id, birth date, workshop info, etc. all come from the already-loaded personal-info
response, not fresh user input) — the future ViewModel should load personal info first
and prefill the save-request from it, matching that old-app behavior.

`getUserAge` is chained off personal info too: after loading personal info, the old app
calls `getUserAge(birthDate)` and parses its `"y,m,d"`-style age string client-side into
`yearsAge`/`monthsAge`/`daysAge`/`strAge` display fields — this parsing logic has no
equivalent in this repo yet and will need to be written in the future ViewModel/PR-mapper
layer, not the domain layer (it's presentation formatting, not a data-shape concern).

## Disability-specific server error codes — not yet mapped in `ErrorParser`

The old app's `BaseRemoteDataSource` matches these by substring on the server's error
`data.message`, inside its generic HTTP 400/500 handling (`ErrorParser`/`ErrorUri` in
this repo — see [[Networking]] — has no equivalent cases yet):

- `pension.disability.commission.notready.exception` → "نتیجه رای کمیسیون پزشکی هنوز صادر نشده است..."
- `pension.disability.saved.before.exception` → "درخواست قبلا ذخیره شده است..."
- `pension.disability.history.not.found.exception` → "اطلاعات سابقه یافت نشد..."
- `pension.disability.commission.not.possible.exception` → "شما دارای حکم مستمری فعال می‌باشید و ثبت درخواست از کارافتادگی مقدور نیست."

These will need a home in `ErrorUri`/`ErrorParser`'s `when` branches (or wherever this
repo's error-substring matching for feature-specific server exceptions lives) once the
save/confirm flow gets a real ViewModel and these errors start actually surfacing.

## Medical commission list — separate but related sub-feature

`GET medical-committee-demand/get-last-demand-details` is not disability-exclusive in the
old app — `demandTypeCode` distinguishes disability (`"01"`) from other commission-referral
types (retirement/light-duty). This repo's `GetRegisteredMedicalCommissionUseCase` exposes
the raw endpoint (all types) rather than filtering client-side to `"01"` — whichever
ViewModel consumes it for the disability screen should filter/display by `demandTypeCode`
itself rather than expecting the use case to pre-filter, since the same use case may end
up reused by a future retirement/light-duty commission screen. **Done**: step 5's
`DisabilityPensionViewModel.loadRegisteredRequests()` is that consumer — it filters to
`demandTypeCode == "01"` before mapping to `RegisteredMedicalCommissionPR`.

## UI polish pass (post-EM-2619)

Four fixes requested after reviewing the built flow, none behavior-porting from the old app —
pure UI/UX corrections:

- **Step 1's primary button had a visibly different color from steps 2-7.** Root cause:
  `DisabilityPensionScreen.kt`'s bottom bar used `TaminBottomActionBar` (core-ui) for the Terms
  step only — that component defaults its button to `TaminFilledButton`, whose own default
  background is `LocalTaminColors.current.heroGradient`. Every other step instead builds its
  bottom bar directly with `LoadingButton`, whose default background is
  `LocalTaminColors.current.buttonGradient` — a different token. Fixed by dropping
  `TaminBottomActionBar` for the Terms step and rendering the same `TaminBottomBar { LoadingButton(...) }`
  shape the other steps already use, so all 7 steps now share one gradient token. Worth checking
  any other screen that mixes `TaminBottomActionBar` with a `LoadingButton`-based bottom bar
  elsewhere in the app for the same mismatch — this wasn't audited outside this feature.
- **"مشاهده شرایط" (step 1) is now a bottom sheet, not a full-screen `Dialog`.**
  `DisabilityPensionRulesDialog.kt` was rewritten from a `Dialog` + its own `Scaffold`/`TaminTopAppBar`
  into a `ModalBottomSheet` (`skipPartiallyExpanded = true`, matching this feature's other sheets),
  with the same rules content now in a scrollable middle section and a sticky
  `disability_pension_rules_acknowledge` ("متوجه شدم") `LoadingButton` pinned at the bottom that
  calls the same `onDismiss`.
- **Step 5 ("نظر کمیسیون پزشکی") now requires an explicit بله/خیر answer** —
  previously `hasCommissionObjection == null` (unanswered) silently let `NextStepClicked` advance
  to Documents; only an explicit "بله" blocked it. Added `showCommissionValidationError` to state +
  `PartialState.CommissionValidationErrorChanged`; `handleCommissionRecordNextStep()` now blocks and
  surfaces the error on `null` too, clearing it as soon as either radio is picked
  (`CommissionObjectionChanged`). Also fixed the bottom bar's `enabled` condition
  (`DisabilityPensionScreen.kt`) from a global `state.hasCommissionObjection != true` to a
  step-scoped `state.currentStep != CommissionRecord || state.hasCommissionObjection == false` —
  the old global form happened to work only because the objection field is never non-null before
  reaching step 5, but was fragile (would have silently disabled the button on every other step
  too the moment it stopped being coincidentally scoped).
- **Removed the built-in gap around every `Checkbox` on all 5 confirmation checkboxes** (Terms,
  Dependents, IdentityContact, Workshop, Summary). Material3's `Checkbox` always reserves a 48dp
  interactive touch target around its ~20dp visual box — that reserved padding is what read as
  "space I didn't ask for" around the checkbox, and it can't be shrunk by passing a smaller
  `modifier` (the component's own `minimumInteractiveComponentSize()` wins). Rather than fight that
  framework internal, added `TaminCheckbox` (core-ui, `ui/components/TaminCheckbox.kt`) — a
  purely presentational, no-padding box (border + fill + checkmark) with a `size` param, default
  20dp. Each usage site now wraps the *whole row* (checkbox + label) in `Modifier.clickable { ... }`
  to toggle, restoring a touch target at least as generous as Material3's default, and controls the
  checkbox-to-label gap explicitly via that row's own `Arrangement.spacedBy(Spacing.sm)` — which is
  exactly the "space I want to set myself" the request asked for. `TaminCheckbox` has no
  `onCheckedChange` of its own by design (purely visual); if a future screen needs a checkbox that
  isn't the leading element of a clickable label row, give it its own `.toggleable(...)`/`.clickable(...)`
  at the call site rather than adding click-handling back into the component.
- **Hero header step-progress bar now fills cumulatively instead of highlighting only the current
  step.** `TaminHeroStepProgress` (core-ui, used only by this screen today) originally lit only the
  segment matching `currentStep`, documented at the time as a deliberate match to an early
  pension-survivor mockup ("not cumulative fill"). Changed to `index <= clampedStep` so completed
  steps stay lit too — communicates progress made, not just current position. Each segment now
  fills progressively (a track `Box` + a width-animated overlay via `animateFloatAsState`,
  `Modifier.fillMaxWidth(fillFraction)`) rather than snapping or cross-fading color — the same
  technique `StepIndicator.kt`'s `StepConnector` already uses between step circles elsewhere in
  core-ui, reused here per explicit user request for visual consistency with that component. Since
  already-complete segments are already at fraction `1f`, only the segment newly becoming
  complete/current visibly animates on each step change. Since no other screen consumes this
  component, the default changed with no other screen affected; the component's own doc comment
  was updated to match.
- **Top app bar back-chevron vs. close(X) now behave differently, per explicit QA request.**
  Previously both `navigationIcon` and `action` in `DisabilityPensionScreen.kt`'s `TaminTopAppBar`
  called the exact same lambda (`PreviousStepClicked` on any non-`Terms` step, outer `onBack()` —
  i.e. `popBackStack()`, which lands on Home since this feature is pushed directly from Home — only
  on `Terms`), so the X button never showed any confirmation and the chevron only ever stepped back
  one wizard step instead of leaving the flow. The bottom bar's own `SquareIconButton` already owns
  "previous step" on every non-`Terms` step, so the top-bar chevron was redundant with it. Fixed:
  the top-bar back chevron now calls the screen's outer `onBack` directly (always exits to Home,
  regardless of `currentStep`); the top-bar close (X) now dispatches a new `CloseClicked` intent
  that shows a `TaminConfirmationDialog` (new `showExitConfirmDialog` state,
  `disability_pension_exit_confirm_*` strings, `Icons.Default.Warning` on `orangeBg`/`orangeText`
  matching the Figma warning-dialog style) — confirm ("ادامهٔ تکمیل فرم") just dismisses the dialog,
  dismiss ("رها می‌کنم") dismisses it and fires `DisabilityPensionEvent.NavigateBack`. Mirrors
  `:feature:taminServices`'s `OccurrenceScreen` exit-confirmation pattern (`onExitRequested`/
  `showExitConfirmation`) rather than inventing a new shape, though this feature always shows the
  dialog on close rather than gating it to a subset of steps like Occurrence's
  `STEPS_REQUIRING_EXIT_CONFIRMATION` — revisit if product wants the dialog skipped on `Terms`
  (nothing entered yet) once real design feedback comes in. The system/hardware back
  gesture had no handler at all before this pass (fell through to Navigation-Compose's default
  `popBackStack()`, silently exiting to Home from any step with zero warning — the same
  data-loss gap the exit dialog was built to close). Explicitly confirmed with the user which of
  three options it should mirror (top-bar chevron / bottom-bar previous-step / top-bar close) —
  answer was **close (X)**: added `BackHandler { viewModel.sendIntent(DisabilityPensionIntent.CloseClicked) }`
  in `DisabilityPensionScreen`, so system back now shows the same exit-confirmation dialog from
  every step, same as tapping X. This is a different choice than `HistoryObjectionStepperScreen`/
  `OccurrenceScreen`, where system back mirrors the top-bar chevron's *previous-step* behavior
  instead — don't copy that convention here without re-asking, since this feature's chevron was
  deliberately wired to always-exit-to-Home instead of step-back (see above), which would have
  made "mirror the chevron" behaviorally identical to the unprotected pre-fix state.

## Fixed: back/next on a step reached via "edit from summary" re-entered the linear wizard instead of returning to Summary

Step 7's per-row pencil icon (`EditSummarySectionClicked(step)`) jumps straight to that step via
`StepChanged(step)`, but originally that was the *only* thing it did — nothing recorded that the
user had arrived via a summary edit rather than normal linear progression. Consequence: `PreviousStepClicked`/
`NextStepClicked` on that step still resolved to the fixed neighbor in `DisabilityPensionStep`'s
linear order (`handlePreviousStepClicked()`/`handleNextStepClicked()` in
`DisabilityPensionViewModel.kt`), so fixing e.g. step 4 (Workshop) from the summary and tapping
"مرحله بعدی" forced the user through step 5's commission radio and step 6's document-confirm
dialog all over again just to get back to step 7 — and tapping back pulled them *deeper* into the
wizard's history instead of out of it. Worse: if the user used the edit-from-summary path to answer
"بله، معترض هستم" on step 5, `handleCommissionRecordNextStep()`'s existing block-on-objection rule
left them with no forward path and a backward-only escape that didn't lead back to the summary
either.

Fixed by adding `isEditingFromSummary: Boolean` to `DisabilityPensionUiState`, set by
`EditSummarySectionClicked` (new `PartialState.EditingFromSummaryChanged`). While set:
`handlePreviousStepClicked()` short-circuits straight to a new `returnToSummary()` helper
regardless of `currentStep` (checked once, before the per-step `when`), and every per-step
"next" handler (`handleNextStepClicked`'s `Terms`/`Dependents` branches,
`handleIdentityContactNextStep`, `handleWorkshopNextStep`, `handleCommissionRecordNextStep`) routes
to `returnToSummary()` instead of the next linear step once that step's own validation/confirmation
passes — `returnToSummary()` also clears the flag. This intentionally skips the side-loads tied to
the steps being bypassed (e.g. editing Workshop no longer triggers `loadInsuranceRecord()` for a
CommissionRecord screen the user never sees) since Summary only reads already-loaded state. The
Documents step's existing `ConfirmDocumentsSubmission` handler already unconditionally went to
`Summary` (coincidentally correct since Documents precedes Summary) — it now also clears the flag
for consistency.

Since `DisabilityPensionStep.Terms` never had a back button in the bottom bar (nothing precedes it
in the normal flow), editing Terms from the summary previously had no way back to Summary short of
the top-bar close (X) confirmation dialog. `DisabilityPensionScreen.kt`'s `DisabilityPensionBottomBar`
now renders the same back-arrow + next `Row` as every other step when `state.isEditingFromSummary`
is true on `Terms`, falling back to the original full-width single button otherwise.

4 new test cases in `DisabilityPensionViewModelTest.kt` cover: the flag being set on
`EditSummarySectionClicked`, next-from-edit returning directly to Summary (Workshop case, verifying
CommissionRecord is never the resulting step), back-from-edit returning directly to Summary
(IdentityContact case), and the commission-objection-block escape case (back still reaches Summary
even when `hasCommissionObjection == true` blocks forward progress).

## Old-app source files consulted (for re-verification if needed)

```
data/remote/services/ServicesService.kt                         — Retrofit interface
data/remote/models/services/disabilityPension/*.kt               — request/response DTOs
data/remote/models/services/medicalCommission/*.kt                — commission-list DTOs
data/repository/ServiceRepository.kt                              — thin pass-through repo
data/remote/services/ServicesRemoteDataSourceImpl.kt              — getResultNew()/getPdfResult{} wrapping
data/remote/BaseRemoteDataSource.kt                                — the 4 disability-specific error strings
ui/home/services/disabilityPension/DisabilityPensionViewModel.kt  — the 3-step submit chain
ui/home/services/disabilityPension/model/DisabilityPensionDataModel.kt — personal-info → save-request mapping
app/src/main/assets/menu.json                                     — id 113 (×2), sibling ids 40/112 (survivor), 41 (retirement)
```

Related: [[Networking]] · [[MVI-Pattern]] · [[Adding-a-Feature]] · [[Feature-Flags]] ·
[[Reference-old-android]] · [[Glossary]] · [[History-Objection]]
