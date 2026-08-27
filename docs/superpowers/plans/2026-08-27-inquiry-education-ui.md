# Inquiry Education UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship Form / Success / Failure UI and ViewModel for `:feature:inquiryEducation` on one `InquiryEducationRoute`, wired to existing use cases and menu flag.

**Architecture:** MVI via `BaseViewModel`; same-route step enum (`Form` | `Success` | `Failure`); load sons with `GetDataForEducationUseCase`; submit with `InquiryEducationCertificateUseCase` (nationalId + education code); university name = API message string.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform, Koin `viewModelOf`, Turbine tests, core-ui theme tokens / components.

**Spec:** `docs/superpowers/specs/2026-08-27-inquiry-education-ui-design.md`

## Global Constraints

- File naming: feature UI under `feature/inquiryEducation/...`; PR models already in `core-ui` (`*PR.kt`).
- Theme: no hardcoded `Color(0x…)`, raw `.dp`/`.sp`, or Persian literals in composables — use `LocalTaminColors`, `Spacing` / `CornerRadius` / `Thickness` / `ButtonDimens` / `IconSize`, `stringResource(Res.string.*)`.
- Reuse: `TaminTopAppBar`, `TaminBottomBar`, `LoadingButton`, `TaminFilledButton`, `TaminOutlinedButton`, `DetailRow`, `TaminDivider`, `CopyIconButton` / `rememberCopyAction`, `SegmentedInputField` (or equivalent labeled field), `DecorativeBackgroundCircle` / glass header patterns from girlSurvivor / orotez-protez.
- Navigation between features: callbacks only (`onBack`); keep existing `FeatureFlag.INQUIRY_EDUCATION` wiring.
- Submit guard: `if (uiState.value.isSubmitting) return@flow`.
- Education code: Latin alphanumeric only, max length **10**, submit requires exactly 10.
- Do not edit `old_android/` or change certificate API/DTO shapes.
- Commit only when the user asks (skip commit steps unless instructed).

---

## File map

| File | Role |
|---|---|
| `core/.../composeResources/values/strings.xml` | All new Persian copy keys |
| `feature/.../ui/contract/InquiryEducationContract.kt` | Step, State, PartialState, Intent, Event |
| `feature/.../ui/InquiryEducationViewModel.kt` | Load / validate / submit / step transitions |
| `feature/.../ui/InquiryEducationScreen.kt` | Scaffold, header, step host, bottom bar, events |
| `feature/.../ui/components/InquiryEducationFormStep.kt` | Info + sons + code field |
| `feature/.../ui/components/InquiryEducationSuccessStep.kt` | Success card + DetailRows |
| `feature/.../ui/components/InquiryEducationFailureStep.kt` | Failure card |
| `feature/.../di/InquiryEducationModule.kt` | `viewModelOf(::InquiryEducationViewModel)` |
| `feature/.../Navigation.kt` | Unchanged route; Screen already uses `koinViewModel` |
| `feature/.../commonTest/.../InquiryEducationViewModelTest.kt` | Turbine tests |

---

### Task 1: Strings + Contract

**Files:**
- Modify: `core/core-ui/src/commonMain/composeResources/values/strings.xml` (append near existing `error_inquiry_education_failed`)
- Create: `feature/inquiryEducation/src/commonMain/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/ui/contract/InquiryEducationContract.kt`

**Interfaces:**
- Consumes: nothing new
- Produces: `InquiryEducationStep`, `InquiryEducationUiState` (+ nested `PartialState`), `InquiryEducationIntent`, `InquiryEducationEvent`, string resource names listed below

- [ ] **Step 1: Add string resources**

Append to `strings.xml` (exact keys — do not invent alternate names later):

```xml
    <string name="inquiry_education_title">استعلام گواهی اشتغال به تحصیل</string>
    <string name="inquiry_education_subtitle">تمدید پوشش بیمه‌ای فرزند پسر دانشجو با کد رهگیری سامانه وزارت علوم</string>
    <string name="inquiry_education_info_body">متقاضی محترم، ثبت درخواست به منظور تحت پوشش قرار دادن فرد مورد نظر، منوط به ثبت نام وی در سامانه استعلام گواهی تحصیلی وزارت علوم، تحقیقات و فناوری و دریافت کد رهگیری از آن سامانه است.</string>
    <string name="inquiry_education_msrt_url_display">estelam.msrt.ir</string>
    <string name="inquiry_education_msrt_url_copy">https://estelam.msrt.ir</string>
    <string name="inquiry_education_copy_address">کپی نشانی سامانه</string>
    <string name="inquiry_education_son_section">پسر</string>
    <string name="inquiry_education_select_one">یک نفر را انتخاب کنید</string>
    <string name="inquiry_education_national_id_label">کد ملی %1$s</string>
    <string name="inquiry_education_code_label">کد مجوز استعلام مدرک تحصیلی</string>
    <string name="inquiry_education_code_helper">کد رهگیری دریافتی از سامانه وزارت علوم - حروف و ارقام لاتین</string>
    <string name="inquiry_education_code_counter">%1$s/۱۰</string>
    <string name="inquiry_education_submit">استعلام گواهی اشتغال به تحصیل از وزارت علوم</string>
    <string name="inquiry_education_error_select_son">نام فرزند پسر را انتخاب نمایید</string>
    <string name="inquiry_education_error_code">کد مجوز استعلام معتبر نیست.</string>
    <string name="inquiry_education_success_title">اشتغال به تحصیل تأیید شد</string>
    <string name="inquiry_education_success_body">متقاضی محترم، اطلاعات تحصیلی %1$s در دانشگاه %2$s با موفقیت در سامانه ثبت شد.</string>
    <string name="inquiry_education_label_student_name">نام دانشجو</string>
    <string name="inquiry_education_label_national_id">کد ملی</string>
    <string name="inquiry_education_label_university">دانشگاه</string>
    <string name="inquiry_education_label_inquiry_code">کد مجوز استعلام</string>
    <string name="inquiry_education_label_inquiry_date">تاریخ استعلام</string>
    <string name="inquiry_education_success_note">پوشش درمانی این فرزند تا پایان نیمسال تحصیلی ثبت‌شده تمدید می‌شود. رونوشت نتیجه استعلام در صندوق شخصی شما ذخیره شد.</string>
    <string name="inquiry_education_another">استعلام دیگر</string>
    <string name="inquiry_education_back_to_services">بازگشت به خدمات</string>
    <string name="inquiry_education_failure_title">استعلام ناموفق بود</string>
    <string name="inquiry_education_failure_empty">متقاضی محترم، اطلاعاتی در مورد اشتغال به تحصیل شما دریافت نگردید. در صورت اطمینان از صحت کد رهگیری وارد شده،برای تعیین تکلیف وضعیت اشتغال به تحصیل به دانشگاه مربوطه مراجعه نمائید.</string>
    <string name="inquiry_education_retry">تلاش مجدد</string>
    <string name="inquiry_education_empty_sons">فرزند پسری برای استعلام یافت نشد</string>
```

Keep existing `error_inquiry_education_failed` for generic fallback if useful.

- [ ] **Step 2: Create the contract**

Create `InquiryEducationContract.kt`:

```kotlin
package com.tamin.taminhamrah.feature.inquiryEducation.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class InquiryEducationStep {
    Form,
    Success,
    Failure,
}

@Immutable
data class InquiryEducationUiState(
    val step: InquiryEducationStep = InquiryEducationStep.Form,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val sons: ImmutableList<EducationDependentItemPR> = persistentListOf(),
    val selectedNationalId: String? = null,
    val educationCode: String = "",
    val educationCodeError: String? = null,
    val sonSelectionError: String? = null,
    val studentName: String = "",
    val studentNationalId: String = "",
    val universityName: String = "",
    val inquiryCode: String = "",
    val inquiryDate: String = "",
    val successMessage: String = "",
    val failureMessage: String = "",
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class SonsLoaded(
            val sons: ImmutableList<EducationDependentItemPR>,
            val selectedNationalId: String?,
        ) : PartialState
        data class SonSelected(val nationalId: String) : PartialState
        data class EducationCodeChanged(val value: String) : PartialState
        data class FieldErrors(
            val sonSelectionError: String? = null,
            val educationCodeError: String? = null,
        ) : PartialState
        data class SubmitSuccess(
            val studentName: String,
            val studentNationalId: String,
            val universityName: String,
            val inquiryCode: String,
            val inquiryDate: String,
            val successMessage: String,
        ) : PartialState
        data class SubmitFailure(val message: String) : PartialState
        data class ResetToForm(val keepInputs: Boolean) : PartialState
    }
}

sealed interface InquiryEducationIntent {
    data object Load : InquiryEducationIntent
    data class SelectSon(val nationalId: String) : InquiryEducationIntent
    data class EducationCodeChanged(val value: String) : InquiryEducationIntent
    data object Submit : InquiryEducationIntent
    data object Retry : InquiryEducationIntent
    data object AnotherInquiry : InquiryEducationIntent
    data object BackToServices : InquiryEducationIntent
}

sealed interface InquiryEducationEvent {
    data object NavigateBack : InquiryEducationEvent
}
```

- [ ] **Step 3: Sanity-check resources compile (optional quick)**

Run: `.\gradlew.bat :core:core-ui:compileDebugKotlinAndroid`

Expected: SUCCESS (or at least resource generation succeeds).

---

### Task 2: ViewModel + unit tests (TDD)

**Files:**
- Create: `feature/inquiryEducation/src/commonTest/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/ui/InquiryEducationViewModelTest.kt`
- Create: `feature/inquiryEducation/src/commonMain/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/ui/InquiryEducationViewModel.kt`
- Modify: `feature/inquiryEducation/src/commonMain/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/di/InquiryEducationModule.kt`

**Interfaces:**
- Consumes: `GetDataForEducationUseCase`, `InquiryEducationCertificateUseCase`, `EducationDependentsDN.toPresentation()`, `InquiryEducationCertificateDN.toPresentation()`, contract types from Task 1, `PersianDateFormatter.today()`, `getString(Res.string.*)`, `toPersianDigits()`
- Produces: `InquiryEducationViewModel` constructor `(GetDataForEducationUseCase, InquiryEducationCertificateUseCase)`

- [ ] **Step 1: Write failing ViewModel tests**

Create test file with fake use-case wrappers (flows), UnconfinedTestDispatcher, Turbine:

```kotlin
// Key cases (implement all):
// 1) load_autoSelectsSingleSon
// 2) load_multipleSons_noAutoSelect
// 3) submit_withoutSon_setsSonError
// 4) submit_invalidCode_setsCodeError
// 5) submit_success_movesToSuccess_withUniversityFromApi
// 6) submit_blankMessage_movesToFailure_withEmptyCopy
// 7) submit_throw_movesToFailure
// 8) retry_returnsToForm_keepsInputs
// 9) anotherInquiry_clearsInputs
// 10) backToServices_emitsNavigateBack
```

Minimal fake pattern:

```kotlin
class FakeGetDataForEducationUseCase(
    private val result: EducationDependentsDN = EducationDependentsDN(),
    private val error: Throwable? = null,
) : /* OR wrap real use case with fake repo */ {
    // Prefer: FakeInquiryEducationRepository implementing InquiryEducationRepository
    // then GetDataForEducationUseCase(repo) / InquiryEducationCertificateUseCase(repo)
}
```

Reuse the fake repository style from `core/core-domain/.../InquiryEducationUseCasesTest.kt` (copy into feature test as `FakeInquiryEducationRepository`).

Assert success path:

```kotlin
assertEquals(InquiryEducationStep.Success, state.step)
assertEquals("دانشگاه صنعتی امیرکبیر", state.universityName)
assertEquals("امیرحسین محمدی", state.studentName)
assertEquals(InquiryEducationStep.Form, /* after AnotherInquiry */)
```

- [ ] **Step 2: Run tests — expect FAIL**

Run: `.\gradlew.bat :feature:inquiryEducation:testDebugUnitTest --tests "*InquiryEducationViewModelTest*"`

Expected: compile failure (ViewModel missing) or test failures.

- [ ] **Step 3: Implement ViewModel**

```kotlin
package com.tamin.taminhamrah.feature.inquiryEducation.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationEvent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationIntent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationStep
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState.PartialState
import com.tamin.taminhamrah.mapper.inquiryEducation.toPresentation
import com.tamin.taminhamrah.useCases.inquiryEducation.GetDataForEducationUseCase
import com.tamin.taminhamrah.useCases.inquiryEducation.InquiryEducationCertificateUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.inquiry_education_error_code
import taminx.core.core_ui.inquiry_education_error_select_son
import taminx.core.core_ui.inquiry_education_failure_empty
import taminx.core.core_ui.inquiry_education_success_body

private const val EDUCATION_CODE_LENGTH = 10

class InquiryEducationViewModel(
    private val getDataForEducationUseCase: GetDataForEducationUseCase,
    private val inquiryEducationCertificateUseCase: InquiryEducationCertificateUseCase,
) : BaseViewModel<InquiryEducationUiState, PartialState, InquiryEducationEvent, InquiryEducationIntent>(
    initialState = InquiryEducationUiState(),
) {
    init {
        sendIntent(InquiryEducationIntent.Load)
    }

    override fun handleIntent(intent: InquiryEducationIntent): Flow<PartialState> = flow {
        when (intent) {
            InquiryEducationIntent.Load -> loadSons()
            is InquiryEducationIntent.SelectSon -> {
                emit(PartialState.SonSelected(intent.nationalId))
                emit(PartialState.FieldErrors(sonSelectionError = null))
            }
            is InquiryEducationIntent.EducationCodeChanged -> {
                val filtered = intent.value.filter { it.isLetterOrDigit() && it.code < 128 }
                    .take(EDUCATION_CODE_LENGTH)
                emit(PartialState.EducationCodeChanged(filtered))
                if (filtered.length == EDUCATION_CODE_LENGTH) {
                    emit(PartialState.FieldErrors(educationCodeError = null))
                }
            }
            InquiryEducationIntent.Submit -> submit()
            InquiryEducationIntent.Retry -> emit(PartialState.ResetToForm(keepInputs = true))
            InquiryEducationIntent.AnotherInquiry -> emit(PartialState.ResetToForm(keepInputs = false))
            InquiryEducationIntent.BackToServices -> sendEvent(InquiryEducationEvent.NavigateBack)
        }
    }.catch { e ->
        emit(PartialState.Submitting(false))
        emit(PartialState.Loading(false))
        emit(PartialState.SubmitFailure(e.message ?: getString(Res.string.inquiry_education_failure_empty)))
    }

    private suspend fun FlowCollector<PartialState>.loadSons() {
        emit(PartialState.Loading(true))
        val dn = getDataForEducationUseCase().first()
        val pr = dn.toPresentation()
        val list = pr.list.toImmutableList()
        val autoId = list.singleOrNull()?.nationalId?.takeIf { it.isNotBlank() }
        emit(PartialState.SonsLoaded(sons = list, selectedNationalId = autoId))
        emit(PartialState.Loading(false))
    }

    private suspend fun FlowCollector<PartialState>.submit() {
        if (uiState.value.isSubmitting) return
        val state = uiState.value
        val sonError = if (state.sons.isNotEmpty() && state.selectedNationalId.isNullOrBlank()) {
            getString(Res.string.inquiry_education_error_select_son)
        } else null
        val codeError = if (!state.educationCode.matches(Regex("^[A-Za-z0-9]{10}$"))) {
            getString(Res.string.inquiry_education_error_code)
        } else null
        if (sonError != null || codeError != null) {
            emit(PartialState.FieldErrors(sonSelectionError = sonError, educationCodeError = codeError))
            return
        }
        val nationalId = state.selectedNationalId ?: return
        emit(PartialState.Submitting(true))
        val result = inquiryEducationCertificateUseCase(
            code = nationalId,
            educationCode = state.educationCode,
        ).first().toPresentation()
        emit(PartialState.Submitting(false))
        if (result.message.isBlank()) {
            emit(PartialState.SubmitFailure(getString(Res.string.inquiry_education_failure_empty)))
            return
        }
        val son = state.sons.first { it.nationalId == nationalId }
        val (jy, jm, jd) = PersianDateFormatter.today()
        val date = "$jy/${jm.toString().padStart(2, '0')}/${jd.toString().padStart(2, '0')}".toPersianDigits()
        val body = getString(
            Res.string.inquiry_education_success_body,
            son.fullName,
            result.message,
        )
        emit(
            PartialState.SubmitSuccess(
                studentName = son.fullName,
                studentNationalId = son.nationalId,
                universityName = result.message,
                inquiryCode = state.educationCode,
                inquiryDate = date,
                successMessage = body,
            )
        )
    }

    override fun reduceState(
        currentState: InquiryEducationUiState,
        partialState: PartialState,
    ): InquiryEducationUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.SonsLoaded -> currentState.copy(
            sons = partialState.sons,
            selectedNationalId = partialState.selectedNationalId,
        )
        is PartialState.SonSelected -> currentState.copy(selectedNationalId = partialState.nationalId)
        is PartialState.EducationCodeChanged -> currentState.copy(educationCode = partialState.value)
        is PartialState.FieldErrors -> currentState.copy(
            sonSelectionError = partialState.sonSelectionError,
            educationCodeError = partialState.educationCodeError,
        )
        is PartialState.SubmitSuccess -> currentState.copy(
            step = InquiryEducationStep.Success,
            studentName = partialState.studentName,
            studentNationalId = partialState.studentNationalId,
            universityName = partialState.universityName,
            inquiryCode = partialState.inquiryCode,
            inquiryDate = partialState.inquiryDate,
            successMessage = partialState.successMessage,
            failureMessage = "",
        )
        is PartialState.SubmitFailure -> currentState.copy(
            step = InquiryEducationStep.Failure,
            failureMessage = partialState.message,
            isSubmitting = false,
        )
        is PartialState.ResetToForm -> if (partialState.keepInputs) {
            currentState.copy(step = InquiryEducationStep.Form, failureMessage = "")
        } else {
            currentState.copy(
                step = InquiryEducationStep.Form,
                selectedNationalId = currentState.sons.singleOrNull()?.nationalId,
                educationCode = "",
                educationCodeError = null,
                sonSelectionError = null,
                studentName = "",
                studentNationalId = "",
                universityName = "",
                inquiryCode = "",
                inquiryDate = "",
                successMessage = "",
                failureMessage = "",
            )
        }
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.SubmitFailure(message)
}

// Need: import kotlinx.coroutines.flow.FlowCollector
```

Fix `handleIntent` so `Load`/`Submit` call the private suspend helpers correctly (same pattern as `GirlSurvivorViewModel`: private `suspend fun FlowCollector<PartialState>.…`).

Also clear field errors on `FieldErrors` reduce: when emitting cleared errors after select, pass only the field being cleared — merge in reduce:

```kotlin
is PartialState.FieldErrors -> currentState.copy(
    sonSelectionError = partialState.sonSelectionError,
    educationCodeError = partialState.educationCodeError,
)
```

When clearing one field after successful edit, emit `FieldErrors(sonSelectionError = null, educationCodeError = currentState.educationCodeError)` or add dedicated clear partials. Prefer reading current state inside the intent handler before emit.

- [ ] **Step 4: Register Koin**

```kotlin
package com.tamin.taminhamrah.feature.inquiryEducation.di

import com.tamin.taminhamrah.feature.inquiryEducation.ui.InquiryEducationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val inquiryEducationModule = module {
    viewModelOf(::InquiryEducationViewModel)
}
```

Use cases are already in `DomainModule` — do not re-register.

- [ ] **Step 5: Run tests — expect PASS**

Run: `.\gradlew.bat :feature:inquiryEducation:testDebugUnitTest --tests "*InquiryEducationViewModelTest*"`

Expected: PASS.

---

### Task 3: Form step UI

**Files:**
- Create: `feature/inquiryEducation/src/commonMain/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/ui/components/InquiryEducationFormStep.kt`

**Interfaces:**
- Consumes: `InquiryEducationUiState`, `InquiryEducationIntent`, string resources from Task 1, `EducationDependentItemPR`
- Produces: `@Composable fun InquiryEducationFormStep(state, onIntent, modifier = Modifier)`

- [ ] **Step 1: Implement Form step**

Structure (scrollable column, RTL-safe):

1. Info surface (`colors` info/primary soft bg + border) with `ic_info` + `inquiry_education_info_body`.
2. Inner copy row: display URL text + `rememberCopyAction(stringResource(Res.string.inquiry_education_msrt_url_copy))` on a clickable row with `CopyIconButton` + `inquiry_education_copy_address`.
3. If `state.sons.isNotEmpty()`: section header (`son_section` + `select_one`); for each son a bordered radio card (`RadioButton` + name + `national_id_label` + relation chip). `onClick` → `SelectSon(nationalId)`.
4. If empty and not loading: `TaminEmptyState(inquiry_education_empty_sons)`.
5. `SegmentedInputField` (or labeled `OutlinedTextField` matching project field chrome) for education code; show helper + counter `stringResource(code_counter, educationCode.length.toString().toPersianDigits())`; show `educationCodeError` / `sonSelectionError` under fields.

Do not put the bottom submit button here — Screen owns `TaminBottomBar`.

- [ ] **Step 2: Compile feature Android**

Run: `.\gradlew.bat :feature:inquiryEducation:compileDebugKotlinAndroid`

Expected: SUCCESS (Screen may still be stub until Task 5; Form step alone must compile).

---

### Task 4: Success + Failure steps

**Files:**
- Create: `feature/inquiryEducation/src/commonMain/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/ui/components/InquiryEducationSuccessStep.kt`
- Create: `feature/inquiryEducation/src/commonMain/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/ui/components/InquiryEducationFailureStep.kt`

**Interfaces:**
- Consumes: success/failure fields on `InquiryEducationUiState`
- Produces: `InquiryEducationSuccessStep(state)`, `InquiryEducationFailureStep(state)`

- [ ] **Step 1: Success step**

Scrollable column:

- Green check circle (`ic_tamin_check` / success colors from `LocalTaminColors`)
- Title `inquiry_education_success_title`
- Body `state.successMessage`
- Card with `DetailRow` + `TaminDivider` for:
  - student name (`numeric = false`)
  - national ID
  - university (`numeric = false`)
  - inquiry code
  - inquiry date
- Info note box with `inquiry_education_success_note`

- [ ] **Step 2: Failure step**

Same layout language as Success:

- Error/info icon circle (`ic_error` or `ic_info` + error/warning colors)
- Title `inquiry_education_failure_title`
- Body `state.failureMessage`
- No DetailRows

- [ ] **Step 3: Compile**

Run: `.\gradlew.bat :feature:inquiryEducation:compileDebugKotlinAndroid`

Expected: SUCCESS for new composables.

---

### Task 5: Screen host + bottom bars + navigation wiring

**Files:**
- Modify: `feature/inquiryEducation/src/commonMain/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/ui/InquiryEducationScreen.kt`
- Modify: `feature/inquiryEducation/src/commonMain/kotlin/com/tamin/taminhamrah/feature/inquiryEducation/Navigation.kt` (only if Screen signature needs adjustment — prefer keep `onBack`)

**Interfaces:**
- Consumes: ViewModel, all three steps, contract events
- Produces: working `InquiryEducationScreen(onBack)` using `koinViewModel()`

- [ ] **Step 1: Replace stub Screen**

Mirror `GirlSurvivorScreen` structure:

```kotlin
@Composable
fun InquiryEducationScreen(
    onBack: () -> Unit,
    viewModel: InquiryEducationViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    collectWithLifecycleAware(viewModel.events) { event ->
        when (event) {
            InquiryEducationEvent.NavigateBack -> onBack()
        }
    }

    Scaffold(
        topBar = { /* gradient header: TaminTopAppBar title + subtitle card with graduation/request icon */ },
        bottomBar = {
            InquiryEducationBottomBar(state = state, onIntent = viewModel::sendIntent)
        },
    ) { padding ->
        when {
            state.isLoading && state.step == InquiryEducationStep.Form -> { /* simple loading */ }
            else -> when (state.step) {
                InquiryEducationStep.Form -> InquiryEducationFormStep(...)
                InquiryEducationStep.Success -> InquiryEducationSuccessStep(...)
                InquiryEducationStep.Failure -> InquiryEducationFailureStep(...)
            }
        }
    }
}
```

Header: follow orotez/girlSurvivor gradient + `TaminTopAppBar` back; subtitle uses `inquiry_education_subtitle` in a glass/light card (mockup). Action icon optional (`ic_request` / document) if it fits without new assets.

Bottom bar:

| Step | Content |
|---|---|
| Form | Full-width `LoadingButton` submit (`isSubmitting`) |
| Success | `Row`: `TaminOutlinedButton` (AnotherInquiry, weight 1) + `TaminFilledButton` (BackToServices, weight 1). In RTL composition order: outlined first so it sits on the start (right). |
| Failure | Same row: outlined Retry + filled BackToServices |

System back on Success/Failure: prefer `BackToServices` (pop) — handle via `BackHandler` calling `BackToServices` or `onBack` consistently.

- [ ] **Step 2: Confirm Navigation.kt still valid**

```kotlin
fun NavGraphBuilder.inquiryEducationScreen(onBack: () -> Unit) {
    composableWithFadeTransitions<InquiryEducationRoute> {
        InquiryEducationScreen(onBack = onBack)
    }
}
```

No graph / FeatureNavigation changes required.

- [ ] **Step 3: Full compile**

Run: `.\gradlew.bat :feature:inquiryEducation:compileDebugKotlinAndroid`

Expected: SUCCESS.

- [ ] **Step 4: Re-run ViewModel tests**

Run: `.\gradlew.bat :feature:inquiryEducation:testDebugUnitTest --tests "*InquiryEducationViewModelTest*"`

Expected: PASS.

---

## Spec coverage checklist

| Spec item | Task |
|---|---|
| Same-route Form/Success/Failure | 1, 2, 5 |
| Load sons + auto-select single | 2 |
| Radio son list + code field + bottom submit | 3, 5 |
| Copy MSRT URL | 3 |
| Success DetailRows + composed message | 2, 4 |
| Failure mirror Success | 4, 5 |
| Retry keep inputs / AnotherInquiry clear | 2 |
| BackToServices event | 2, 5 |
| Theme / stringResource rules | Global + 1, 3, 4 |
| Koin registration | 2 |
| Menu wiring already present | — no task |

## Self-review notes

- No TBD placeholders.
- University = API `message` string (legacy).
- Inquiry date = `PersianDateFormatter.today()` formatted with Persian digits.
- Copy uses full `https://estelam.msrt.ir` while display matches mockup host-only string.
