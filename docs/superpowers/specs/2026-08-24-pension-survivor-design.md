# Pension Survivor (`feature:pensionSurvivor`) — Design

Date: 2026-08-24  
Status: Implemented  
Figma: [Survivor Pension Activation](https://www.figma.com/design/m8q9QNKL2dWPlIqDQFdb5O/Tamin-Man-Final-design-Repo?node-id=1701-31) (`1701:31`)  
Legacy: `my-tamin-droid` → `PensionSurvivorFragment` / `PensionSurvivorViewModel`  
Reference KMP modules: `feature:girlSurvivor`, `feature:addDependent`

## Goal

Create a standalone `:feature:pensionSurvivor` module for **برقراری مستمری توسط بازماندگان**, matching the Figma 4-step flow and legacy behavior, reusing existing Personal APIs/use cases where possible.

## Scope (V1)

In scope:

1. New Gradle module `:feature:pensionSurvivor` (same plugin/setup as `girlSurvivor`).
2. Move / replace the stub currently under `feature/pensioner/.../ui/pensionSurvivor`.
3. Four in-screen steps driven by one main ViewModel (Figma stepper).
4. Nested `SurvivorInfo` route when tapping a survivor (legacy-style edit → `SaveSurvivorInfo`).
5. Wire menu/agent entry: `FeatureFlag.REQUEST_PENSION_BY_SURVIVOR_112`, `AgentDestination.PENSION_SURVIVOR`.
6. Reuse `core-ui` components (`TaminTopBar`, bottom sheets, date pickers, cards, buttons, etc.) and form/step patterns from `feature:addDependent`.
7. Theme tokens only (`LocalTaminColors`, `Spacing`, strings in `core-ui`).

Out of scope / deferred:

- Elaborate custom error UX (other branch). Use girlSurvivor-style handling only: safe-call / error parser → toast Event.
- Shared image-upload component from another branch — **do not invent a full uploader**. Placeholders + `TODO` comments so it can be swapped later.
- Deep polish of every Figma micro-variant beyond the four primary steps + survivor detail.

## Architecture

```
feature:pensionSurvivor
  Navigation.kt          (PensionSurvivorRoute, SurvivorInfoRoute, graphs)
  di/PensionSurvivorModule.kt
  ui/
    contract/PensionSurvivorContract.kt
    PensionSurvivorViewModel.kt
    PensionSurvivorScreen.kt
    components/          (step UIs, stepper, survivor list item)
    survivorInfo/        (nested edit screen + optional small VM if needed)

core layers (reuse / extend Personal only)
  PersonalApiService + RemoteDataSource + Repository + UseCases + DTO/DN/PR mappers
```

Pattern: **Approach A** — mirror `girlSurvivor` (standalone feature module, single main route with step state).

Cleanup in `:feature:pensioner`:

- Delete stub package `ui/pensionSurvivor`.
- Remove `PensionSurvivorRoute` / `pensionSurvivorScreen` / Koin `PensionSurvivorViewModel` from pensioner.
- Point `shared` navigation at the new module.

## UI flow (Figma)

| Step | Title | Behavior |
|------|--------|----------|
| 1 | مقررات و ضوابط | Load applicant via `GetPersonalInfoUseCase`. Info banner + “مشاهده ضوابط و مقررات”. Commitment checkbox with highlighted full name. Next enabled when checked. |
| 2 | مشخصات متوفی | National ID → `GetDeceasedInfoUseCase` + `GetAgeUseCase`. Show deceased details. Document slots as **placeholders** with TODO for shared upload component. |
| 3 | اطلاعات بازماندگان | Load survivors for deceased (`GET survivor-request/subdominant` — **missing in KMP today; add**). List; tap → `SurvivorInfoRoute` → save via `SaveSurvivorInfoUseCase` → return. |
| 4 | ثبت نهایی درخواست | `GetConfirmSurvivorsListUseCase` → `GetFinalSurvivorPensionPDFUseCase` → user confirms → `SubmitFinalSurvivorPensionUseCase` → success → navigate back. |

Shared chrome: gradient/`TaminTopBar`-style header titled «برقراری مستمری توسط بازماندگان», 4-step indicator, sticky «مرحله بعد» / back actions — reuse existing components; follow `addDependent` long-form layout habits.

## Data / API map

Already present (reuse):

| Need | Existing |
|------|----------|
| Applicant profile | `GetPersonalInfoUseCase` |
| Deceased by national ID | `GetDeceasedInfoUseCase` |
| Age from birthDate | `GetAgeUseCase` |
| Confirm survivors list | `GetConfirmSurvivorsListUseCase` (`survivor-request/list`) |
| Save survivor | `SaveSurvivorInfoUseCase` |
| Final PDF | `GetFinalSurvivorPensionPDFUseCase` |
| Final submit | `SubmitFinalSurvivorPensionUseCase` |

Must add (legacy gap):

| Need | Legacy endpoint | Action |
|------|-----------------|--------|
| Survivor dependents list | `GET survivor-request/subdominant?id=` | Add ApiService + DTO/DN/PR + RemoteDataSource + Repository + `GetSurvivorListUseCase` (name TBD to match conventions). Do **not** reuse `disability-request/subdominant` unless payloads are verified identical. |

Upload:

- V1: placeholder UI slots + clear `TODO(upload-component): replace with shared uploader from other branch`.
- Persist GUIDs only when a temporary/local path is unavoidable; prefer no fake upload implementation.

## MVI contract (sketch)

- `PensionSurvivorStep`: `Rules`, `Deceased`, `Survivors`, `Final`
- State: step, loading flags, applicant name/nationalId, deceased info rows, survivors list, commitment flags, PDF viewer state, `requestId`, field errors as needed
- Intents: Init, toggle commitment, view rules, next/prev, deceased ID changed/search, open survivor, save survivor result, download/confirm PDF, submit final, back
- Events: ShowToast, NavigateBack, OpenPdfViewer, NavigateToSurvivorInfo(…)
- Errors: same as girlSurvivor — `catch` + `toSingleLineMessage()` / existing error parser; no new error-handling framework

## Navigation & DI

1. `include(":feature:pensionSurvivor")` in `settings.gradle.kts`
2. `shared/build.gradle.kts` dependency
3. Register Koin module in `sharedModules` (`Koin.kt`)
4. Attach graph in `TaminHamrahNavGraph.kt`
5. `FeatureNavigation.kt`: keep `REQUEST_PENSION_BY_SURVIVOR_112` → `navigateToPensionSurvivor()` (new package)
6. Agent destination string unchanged (`pension_survivor`)

## Testing / verification

- Compile: `.\gradlew.bat :feature:pensionSurvivor:compileDebugKotlinAndroid`
- Any **new** UseCase: `BaseUseCaseTest` smoke test
- ViewModel tests optional for V1; prefer compile-green first
- No elaborate error-path test matrix (deferred with error UX branch)

## Open TODOs to leave in code

1. `TODO(upload-component): wire shared image upload component from other branch (death cert / ID pages).`
2. Rules PDF/URL source if not already a known constant — confirm against legacy strings during implementation.

## Success criteria

- New module compiles and opens from menu/agent flag.
- Four Figma steps navigable with real Personal APIs for profile, deceased, save, confirm list, PDF, submit.
- Survivor list API added end-to-end.
- Stub removed from pensioner.
- Upload UI clearly marked TODO; no overbuilt custom uploader.
- Uses core-ui / addDependent patterns; no hardcoded colors/dp/copy.
