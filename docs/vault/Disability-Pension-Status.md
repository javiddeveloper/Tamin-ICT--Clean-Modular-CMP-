---
tags: [domain, gotcha]
---

# مستمری از کارافتادگی — Disability Pension Status Tracking

`FeatureFlag.DISABILITY_PENSION(113)` · `:feature:pensioner` → `ui/disabilityPension`
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

## Status: network → domain → usecase layer complete; UI steps 1-3 of 7 implemented

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
| Screen/ViewModel/Contract | 🚧 steps 1-3 of 7 done | `feature/pensioner/.../ui/disabilityPension/*` — see below |

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
- **Steps 4-7** — still `TODO(EM-2619)`, no design delivered yet.

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
up reused by a future retirement/light-duty commission screen.

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
[[Reference-old-android]] · [[Glossary]]
