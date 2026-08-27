# Inquiry Education UI — Design Spec

**Date:** 2026-08-27  
**Branch:** `Feature-EM-2591-inquiry-education`  
**Module:** `:feature:inquiryEducation`  
**Status:** Approved for planning (flow A + same-route steps)

## Goal

Complete UI and in-feature navigation for «استعلام گواهی اشتغال به تحصیل» using the existing data/domain layer and menu wiring. Match the provided Form and Success mockups. Design Failure to mirror Success (no mockup provided).

Out of scope: changing network/DTO/repository contracts; editing `old_android/` / legacy.

## Context already in place

| Layer | Status |
|---|---|
| API / DTO / DataSource / Repository / UseCases | Done |
| PR models + UI mappers | Done |
| `InquiryEducationRoute` + `FeatureFlag.INQUIRY_EDUCATION` → `FeatureNavigation` | Done |
| `inquiryEducationScreen` in `TaminHamrahNavGraph` | Done (placeholder composable) |
| Feature Koin module | Stub (no ViewModel yet) |

**Legacy behaviour** (`InquiryStudyCodeViewModel` / Fragment):

1. Load sons via renew/education dependents list.
2. User picks a son (national ID) and enters education tracking code.
3. Call `extendEducation/{code}/{educationCode}`.
4. Response `data` is a **university name string** (or blank/null → “not found” info).
5. Legacy showed a dialog; KMP uses full-screen Success/Failure steps instead.

## Architecture

Match existing feature MVI (`BaseViewModel`, Contract with State / PartialState / Intent / Event).

- **One Nav route:** `InquiryEducationRoute` (already registered).
- **Three UI steps** in state (not separate destinations):
  - `Form`
  - `Success`
  - `Failure`
- **One ViewModel** owned by the feature module, registered in `inquiryEducationModule`.
- Cross-feature navigation only via existing callbacks (`onBack` / pop). No direct imports of other features.

```
Menu (FeatureFlag.INQUIRY_EDUCATION)
  → navigateToInquiryEducation()
  → InquiryEducationRoute
       ViewModel loads sons
       Form → Submit → Success | Failure
       Success: AnotherInquiry → Form (reset)
       Failure: Retry → Form (keep son + code)
       Either: BackToServices → NavigateBack / pop
```

## Data flow

| Action | Use case / source |
|---|---|
| Load sons | `GetDataForEducationUseCase` → `EducationDependentsDN` → `toPresentation()` → list in state |
| Submit | `InquiryEducationCertificateUseCase(code = selectedNationalId, educationCode)` |
| Success university | API message string (`InquiryEducationCertificatePR.message`) |
| Success name / national ID | Selected `EducationDependentItemPR` |
| Success inquiry code | User-entered education code |
| Success inquiry date | Client “today” Jalali via existing `PersianDateFormatter` helpers (e.g. format from `Clock.System.now()`); no new domain API |
| Copy MSRT URL | UI clipboard via existing `CopyIconButton` / `rememberCopyAction` pattern (no ViewModel network work) |

## Contract sketch

```kotlin
enum class InquiryEducationStep { Form, Success, Failure }

data class InquiryEducationUiState(
    val step: InquiryEducationStep = InquiryEducationStep.Form,
    val isLoading: Boolean = false,          // initial list load
    val isSubmitting: Boolean = false,       // certificate call
    val sons: List<EducationDependentItemPR> = emptyList(),
    val selectedNationalId: String? = null,
    val educationCode: String = "",
    val educationCodeError: String? = null,
    val sonSelectionError: String? = null,
    // Success fields
    val studentName: String = "",
    val studentNationalId: String = "",
    val universityName: String = "",
    val inquiryCode: String = "",
    val inquiryDate: String = "",
    val successMessage: String = "",
    // Failure
    val failureMessage: String = "",
)

// PartialState: Loading, Submitting, SonsLoaded, SonSelected, EducationCodeChanged,
// FieldErrors, SubmitSuccess(...), SubmitFailure(message), ResetToForm(keepInputs: Boolean), …

sealed interface InquiryEducationIntent {
    data object Load : InquiryEducationIntent
    data class SelectSon(val nationalId: String) : InquiryEducationIntent
    data class EducationCodeChanged(val value: String) : InquiryEducationIntent
    data object Submit : InquiryEducationIntent
    data object Retry : InquiryEducationIntent          // Failure → Form, keep inputs
    data object AnotherInquiry : InquiryEducationIntent // Success → Form, clear selection/code
    data object BackToServices : InquiryEducationIntent
}

sealed interface InquiryEducationEvent {
    data object NavigateBack : InquiryEducationEvent
    // Copy feedback handled by CopyIconButton toaster; no extra event required unless we diverge
}
```

### Validation (Form submit)

- Son required when list is non-empty; if list empty, hide son section (legacy) and do not submit without a selected id (no valid target).
- Education code: required, exactly **10** Latin letters/digits (`[A-Za-z0-9]{10}`); filter input to Latin alnum and max length 10; show `۰/۱۰` counter.
- Guard: `if (state.isSubmitting) return@flow` on Submit.

### Success vs Failure rules

| API outcome | Step |
|---|---|
| Success with non-blank message | `Success` (university = message) |
| Success with blank/null message | `Failure` with legacy-equivalent “not find info” string |
| Throwable / repository error | `Failure` with error message from existing error handling |

## UI

Theme rules: `LocalTaminColors`, `Spacing` / `CornerRadius` / `Thickness` / `ButtonDimens`, `stringResource` only — no hardcoded colors, dp/sp, or Persian copy in composables.

### Shared chrome

- Hero header with title and subtitle card (graduation icon + coverage blurb), consistent with mockups / existing top-app-bar patterns (`TaminTopAppBar` or feature header used by similar services).

### Form step (mockup 1)

1. Info box (MSRT registration requirement) + copy row: URL `estelam.msrt.ir` + «کپی نشانی سامانه» via `CopyIconButton` / copy action.
2. Section header «پسر» + hint «یک نفر را انتخاب کنید».
3. Radio cards per son: name, national ID, relation chip (`relationDescription`), selection binds to `SelectSon`.
4. Text field «کد مجوز استعلام مدرک تحصیلی *» + helper + counter.
5. Bottom bar (`TaminBottomBar` + `LoadingButton`): «استعلام گواهی اشتغال به تحصیل از وزارت علوم».

### Success step (mockup 2)

1. Same header chrome.
2. Green success card: check icon, «اشتغال به تحصیل تأیید شد», composed body including student name + university.
3. `DetailRow` list: نام دانشجو، کد ملی، دانشگاه، کد مجوز استعلام، تاریخ استعلام.
4. Blue info note (coverage until semester end + mailbox copy).
5. Bottom: secondary «استعلام دیگر» | primary «بازگشت به خدمات».

### Failure step (designed; mirrors Success)

1. Same header chrome.
2. Error status card: error/info icon, title «استعلام ناموفق بود», body = `failureMessage`.
3. No DetailRows.
4. Bottom: secondary «تلاش مجدد» | primary «بازگشت به خدمات».

## Files to add/change

```
feature/inquiryEducation/
  ui/contract/InquiryEducationContract.kt     (new)
  ui/InquiryEducationViewModel.kt             (new)
  ui/InquiryEducationScreen.kt                (replace stub)
  ui/components/InquiryEducationFormStep.kt   (new)
  ui/components/InquiryEducationSuccessStep.kt(new)
  ui/components/InquiryEducationFailureStep.kt(new)
  Navigation.kt                               (wire koinViewModel + events)
  di/InquiryEducationModule.kt                (viewModelOf + use cases if needed)

core/core-ui/.../composeResources/values/strings.xml  (feature copy keys)

feature/inquiryEducation/.../InquiryEducationViewModelTest.kt  (unit tests)
```

No changes expected to `FeatureNavigation` / `TaminHamrahNavGraph` beyond what already exists, unless screen signature needs `onBack` event wiring adjustments.

## Testing

- ViewModel: load sons (0 / 1 / N → auto-select when size == 1); validation errors; submit success → Success fields; blank message → Failure; exception → Failure; Retry keeps inputs; AnotherInquiry clears.
- Compile: `.\gradlew.bat :feature:inquiryEducation:compileDebugKotlinAndroid`

## Non-goals

- Parsing university from free text beyond using the API string as-is.
- Separate Nav route for result.
- Dialog-only success (legacy) — replaced by Success step.
- Changes to certificate API shape.
