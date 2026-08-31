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
 * deceased eligibility inquiry (eligible / not-eligible), step navigation (including "back off the
 * first step leaves the screen"), the terminal submit, and the bank-account-correction re-submit.
 * A valid Iranian national id ("1234567891") is used everywhere the ViewModel runs the checksum,
 * so the invalid-id branch (which reaches `getString`) is never exercised here.
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
    fun `init loads info and auto-selects an active bank account when the user has one`() = runTest(testDispatcher) {
        val state = viewModel.uiState.value

        assertNotNull(state.info)
        assertEquals("علی رضایی", state.info?.fullName)
        assertEquals(1, state.bankAccounts.size)
        assertNotNull(state.selectedBankAccount)
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
    fun `Retry reloads info after a transient failure`() = runTest(testDispatcher) {
        userRepository.bankAccountListError = RuntimeException("transient")
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.uiState.value.info)

        userRepository.bankAccountListError = null
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
            message = "", dependentStatus = "همسر", deathDate = "۱۴۰۵/۰۱/۱۰",
        )

        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("1234567891", funeralRepository.lastValidateNationalCode)
        assertNotNull(state.deceasedValidation)
        assertEquals(true, state.deceasedValidation?.isEligible)
        assertEquals("زهرا رضایی", state.deceasedValidation?.deceasedFullName)
        assertFalse(state.isValidatingDeceased)
    }

    @Test
    fun `ValidateDeceased with an ineligible result clears the validation and emits an info message`() = runTest(testDispatcher) {
        funeralRepository.validateResult = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = false,
            message = "متوفی در سوابق افراد تبعی شما ثبت نشده است", dependentStatus = "", deathDate = "",
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
    fun `GoToNextStep advances to the deceased and bank info step`() = runTest(testDispatcher) {
        viewModel.sendIntent(FuneralAllowanceIntent.GoToNextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(FuneralAllowanceStep.DECEASED_AND_BANK_INFO, viewModel.uiState.value.currentStep)
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

    // ── Bank account selection / confirmation ────────────────────────────────

    @Test
    fun `SelectBankAccount replaces the selection and closes the sheet`() = runTest(testDispatcher) {
        val other = viewModel.uiState.value.selectedBankAccount!!.copy(id = 42L)

        viewModel.sendIntent(FuneralAllowanceIntent.SelectBankAccount(other))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(42L, viewModel.uiState.value.selectedBankAccount?.id)
        assertFalse(viewModel.uiState.value.showBankAccountBottomSheet)
    }

    @Test
    fun `ToggleAccountConfirmation flips the confirmation flag`() = runTest(testDispatcher) {
        viewModel.sendIntent(FuneralAllowanceIntent.ToggleAccountConfirmation(true))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAccountConfirmed)
    }

    // ── Submit ──────────────────────────────────────────────────────────────

    @Test
    fun `SubmitRequest with everything confirmed maps params and emits the success message`() = runTest(testDispatcher) {
        funeralRepository.validateResult = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = true,
            message = "", dependentStatus = "همسر", deathDate = "۱۴۰۵/۰۱/۱۰",
        )
        funeralRepository.submitResult = "درخواست شما ثبت شد"

        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ToggleAccountConfirmation(true))
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
    fun `SubmitRequest does nothing until the account is confirmed`() = runTest(testDispatcher) {
        funeralRepository.validateResult = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = true,
            message = "", dependentStatus = "همسر", deathDate = "",
        )
        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
        testDispatcher.scheduler.advanceUntilIdle()

        // isAccountConfirmed is still false -> canSubmitRequest is false -> submit is a no-op.
        viewModel.sendIntent(FuneralAllowanceIntent.SubmitRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(funeralRepository.lastSubmitParams)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `SubmitRequest failure emits an error toast and resets the submitting flag`() = runTest(testDispatcher) {
        funeralRepository.validateResult = DeceasedValidationDN(
            deceasedFullName = "زهرا رضایی", relationship = "همسر", isEligible = true,
            message = "", dependentStatus = "همسر", deathDate = "",
        )
        viewModel.sendIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged("1234567891"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ValidateDeceased)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(FuneralAllowanceIntent.ToggleAccountConfirmation(true))
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
