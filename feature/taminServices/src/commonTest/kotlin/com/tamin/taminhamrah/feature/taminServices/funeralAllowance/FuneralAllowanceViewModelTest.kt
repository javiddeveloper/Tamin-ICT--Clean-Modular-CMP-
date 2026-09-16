package com.tamin.taminhamrah.feature.taminServices.funeralAllowance

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceEvent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceIntent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDN
import com.tamin.taminhamrah.model.funeralAllowance.RegisteredFuneralRequestDN
import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN
import com.tamin.taminhamrah.useCases.bankAccount.GetBankAccountListUseCase
import com.tamin.taminhamrah.useCases.funeralAllowance.ConfirmFuneralAccountCorrectionUseCase
import com.tamin.taminhamrah.useCases.funeralAllowance.GetFuneralAllowanceInfoUseCase
import com.tamin.taminhamrah.useCases.funeralAllowance.SubmitFuneralAllowanceRequestUseCase
import com.tamin.taminhamrah.useCases.funeralAllowance.ValidateDeceasedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Covers the funeral-allowance wizard: the pre-flight bank-account gate fired from `init`, the
 * eligibility inquiry (eligible / not-eligible), step navigation (including "back off the first
 * step leaves the screen"), the terminal submit, and the bank-account-correction re-submit.
 * A valid Iranian national id ("1234567891") is used wherever the ViewModel runs the checksum, so
 * the invalid-id branch (which reaches `getString` and needs an Android context to resolve it —
 * see `feature/taminServices/build.gradle.kts`'s Robolectric setup) is never exercised here.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FuneralAllowanceViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var userRepository: FakeUserRepository
    private lateinit var funeralRepository: FakeFuneralAllowanceRepository
    private lateinit var viewModel: FuneralAllowanceViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userRepository = FakeUserRepository().apply { bankAccountListResult = listOf(bankAccountDN()) }
        funeralRepository = FakeFuneralAllowanceRepository()
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = FuneralAllowanceViewModel(
        getBankAccountListUseCase = GetBankAccountListUseCase(userRepository),
        getFuneralAllowanceInfoUseCase = GetFuneralAllowanceInfoUseCase(funeralRepository),
        validateDeceasedUseCase = ValidateDeceasedUseCase(funeralRepository),
        submitFuneralAllowanceRequestUseCase = SubmitFuneralAllowanceRequestUseCase(funeralRepository),
        confirmFuneralAccountCorrectionUseCase = ConfirmFuneralAccountCorrectionUseCase(funeralRepository),
    )

    // ── Pre-flight bank-account gate (fired from init) ────────────────────────

    @Test
    fun `init loads info when the user has a bank account`() = runTest(testDispatcher) {
        val state = viewModel.uiState.value

        assertNotNull(state.info)
        assertEquals("علی رضایی", state.info?.fullName)
        assertFalse(state.isLoading)
        assertFalse(state.showNoBankAccountDialog)
        assertNull(state.errorMessage)
    }

    @Test
    fun `init with no registered bank account blocks the flow behind the no-account dialog`() = runTest(testDispatcher) {
        userRepository.bankAccountListResult = emptyList()

        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showNoBankAccountDialog)
        assertNull(state.info)
        assertFalse(state.isLoading)
    }

    @Test
    fun `init records an error when the bank-account check fails`() = runTest(testDispatcher) {
        userRepository.bankAccountListError = RuntimeException("bank check failed")

        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertNull(state.info)
        assertFalse(state.isLoading)
    }

    @Test
    fun `init records an error when loading info fails`() = runTest(testDispatcher) {
        funeralRepository.shouldThrowError = true
        funeralRepository.error = RuntimeException("info load failed")

        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertNull(state.info)
        assertFalse(state.isLoading)
    }

    @Test
    fun `Retry reloads info after a transient failure`() = runTest(testDispatcher) {
        funeralRepository.shouldThrowError = true
        funeralRepository.error = RuntimeException("transient")
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.uiState.value.info)

        funeralRepository.shouldThrowError = false
        viewModel.sendIntent(FuneralAllowanceIntent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.info)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    // ── Deceased national code + eligibility inquiry ─────────────────────────

    @Test
    fun `DeceasedNationalCodeChanged keeps only digits and caps the length at ten`() = runTest(testDispatcher) {
        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("12ab34-56 78901234"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("1234567890", viewModel.uiState.value.deceasedNationalCode)
    }

    @Test
    fun `ValidateDeceased with an eligible result stores the validation`() = runTest(testDispatcher) {
        funeralRepository.validateResult = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = true,
            message = "",
        )

        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("1234567891", funeralRepository.lastValidateNationalCode)
        assertNull(state.deceasedNationalCodeError)
        assertNotNull(state.deceasedValidation)
        assertEquals(true, state.deceasedValidation?.isEligible)
        assertEquals("زهرا رضایی", state.deceasedValidation?.deceasedFullName)
        assertFalse(state.isValidatingDeceased)
    }

    @Test
    fun `ValidateDeceased with an ineligible result clears the validation and emits an info message`() = runTest(testDispatcher) {
        funeralRepository.validateResult = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = false,
            message = "متوفی در سوابق افراد تبعی شما ثبت نشده است",
        )
        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
            val event = assertIs<FuneralAllowanceEvent.ShowInfoMessage>(awaitItem())
            assertEquals("متوفی در سوابق افراد تبعی شما ثبت نشده است", event.message)
            cancelAndIgnoreRemainingEvents()
        }

        assertNull(viewModel.uiState.value.deceasedValidation)
    }

    @Test
    fun `ValidateDeceased failure emits an error toast and resets the loading flag`() = runTest(testDispatcher) {
        funeralRepository.shouldThrowError = true
        funeralRepository.error = RuntimeException("inquiry failed")
        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
            val event = assertIs<FuneralAllowanceEvent.ShowErrorToast>(awaitItem())
            assertTrue(event.message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.isValidatingDeceased)
    }

    // ── Step navigation ─────────────────────────────────────────────────────

    @Test
    fun `GoToNextStep advances to the deceased info step`() = runTest(testDispatcher) {
        viewModel.sendIntent(FuneralAllowanceIntent.GoToNextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(FuneralAllowanceStep.DECEASED_INFO, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `GoToPreviousStep on the first step sends NavigateBack`() = runTest(testDispatcher) {
        viewModel.events.test {
            viewModel.sendIntent(FuneralAllowanceIntent.GoToPreviousStep)
            assertEquals(FuneralAllowanceEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GoToPreviousStep on the second step moves back one step`() = runTest(testDispatcher) {
        viewModel.sendIntent(FuneralAllowanceIntent.GoToNextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(FuneralAllowanceIntent.GoToPreviousStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(FuneralAllowanceStep.APPLICANT_INFO, viewModel.uiState.value.currentStep)
    }

    // ── Submit ──────────────────────────────────────────────────────────────

    @Test
    fun `SubmitRequest with an eligible validation maps params and emits the success message`() = runTest(testDispatcher) {
        funeralRepository.validateResult = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = true,
            message = "",
        )
        funeralRepository.submitResult = "درخواست شما ثبت شد"

        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(FuneralAllowanceIntent.SubmitRequest)
            val event = assertIs<FuneralAllowanceEvent.ShowSuccessMessage>(awaitItem())
            assertEquals("درخواست شما ثبت شد", event.message)
            cancelAndIgnoreRemainingEvents()
        }

        val params: SubmitFuneralAllowanceParamsDN = assertNotNull(funeralRepository.lastSubmitParams)
        assertEquals("1234567891", params.deceasedNationalId)
        assertEquals("0012345678", params.nationalCode)
        assertEquals("10", params.branchCode)
        assertEquals("07", params.requestHelpType)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `SubmitRequest does nothing until the deceased is validated as eligible`() = runTest(testDispatcher) {
        // No ValidateDeceased intent sent -> deceasedValidation is null -> canSubmitRequest is false.
        viewModel.sendIntent(FuneralAllowanceIntent.SubmitRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(funeralRepository.lastSubmitParams)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `SubmitRequest failure emits an error toast and resets the submitting flag`() = runTest(testDispatcher) {
        funeralRepository.validateResult = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = true,
            message = "",
        )
        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
        testDispatcher.scheduler.advanceUntilIdle()

        funeralRepository.shouldThrowError = true
        funeralRepository.error = RuntimeException("submit failed")

        viewModel.events.test {
            viewModel.sendIntent(FuneralAllowanceIntent.SubmitRequest)
            assertIs<FuneralAllowanceEvent.ShowErrorToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    // ── Bank-account-correction re-submit ───────────────────────────────────

    @Test
    fun `ConfirmAccountCorrection re-submits the registered request and emits the success message`() = runTest(testDispatcher) {
        funeralRepository.infoResult = funeralRepository.infoResult.copy(
            hasBankAccountIssue = true,
            registeredRequest = RegisteredFuneralRequestDN(
                requestId = 998877L,
                deceasedNationalId = "0055667788",
                deathTimestamp = null,
                requestTimestamp = null,
                statusName = "در انتظار اصلاح حساب",
            ),
        )
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        funeralRepository.confirmResult = "درخواست مجدداً ثبت شد"

        viewModel.events.test {
            viewModel.sendIntent(FuneralAllowanceIntent.ConfirmAccountCorrection)
            val event = assertIs<FuneralAllowanceEvent.ShowSuccessMessage>(awaitItem())
            assertEquals("درخواست مجدداً ثبت شد", event.message)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals("998877", funeralRepository.lastConfirmRequestId)
        assertFalse(viewModel.uiState.value.isConfirmingCorrection)
    }

    // ── No-account dialog dismissal ────────────────────────────────────────

    @Test
    fun `DismissNoBankAccountDialog hides the dialog and navigates back`() = runTest(testDispatcher) {
        userRepository.bankAccountListResult = emptyList()
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.showNoBankAccountDialog)

        viewModel.events.test {
            viewModel.sendIntent(FuneralAllowanceIntent.DismissNoBankAccountDialog)
            assertEquals(FuneralAllowanceEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.showNoBankAccountDialog)
    }

    @Test
    fun `NavigateToBankAccount dismisses the dialog and emits the bank-account navigation event`() = runTest(testDispatcher) {
        userRepository.bankAccountListResult = emptyList()
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(FuneralAllowanceIntent.NavigateToBankAccount)
            assertEquals(FuneralAllowanceEvent.NavigateToBankAccount, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.showNoBankAccountDialog)
    }

    // ── Fixtures ──────────────────────────────────────────────────────────────

    private fun bankAccountDN(id: Long = 1L, confirmed: Boolean = true) = BankAccountDN(
        dateOfFinish = null,
        creationTime = null,
        lastModificationTime = null,
        lastModifiedBy = null,
        accounType = null,
        personal = null,
        accountNumber = "0203456789001",
        isValidAccount = null,
        confirmed = confirmed,
        organizationId = null,
        bank = null,
        createdBy = null,
        dateOfStart = null,
        id = id,
    )
}
