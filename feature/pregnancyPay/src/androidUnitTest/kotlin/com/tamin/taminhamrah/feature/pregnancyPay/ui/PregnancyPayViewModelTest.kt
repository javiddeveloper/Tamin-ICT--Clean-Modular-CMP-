package com.tamin.taminhamrah.feature.pregnancyPay.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pregnancyPay.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.pregnancyPay.fake.FakePregnancyPayRepository
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayEvent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayIntent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayOptionUi
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyMainInfoUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyStatusListUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyTypeListUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.SendPregnancyPayRequestUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Covers the gender gate, branch/date loading, the pregnancy-type -> child-national-code field
 * count rule, and the per-step "can advance" gates of the 4-step wizard (Landing is not a numbered
 * step).
 *
 * Assertions deliberately avoid depending on [PregnancyPayViewModel]'s request-type option labels
 * or its cross-field validation messages (both built via a suspend `getString(Res.string...)` call)
 * — unreliable under this project's plain-JVM test runner (see `getstring-viewmodel-test-hazard`
 * project memory). Options that need to exist for a test are instead picked directly through the
 * relevant intent, exactly as the screen does once the user taps one — the same workaround
 * `OrotezProtezViewModelTest`/`OrotezProtezTestData` use for the same hazard. The per-step "Next"
 * gates (`canGoNextFrom*`) are plain booleans with no `getString` involved, so those are safe to
 * assert on directly.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PregnancyPayViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var pregnancyPayRepository: FakePregnancyPayRepository

    private fun createViewModel(): PregnancyPayViewModel = PregnancyPayViewModel(
        getPregnancyMainInfoUseCase = GetPregnancyMainInfoUseCase(pregnancyPayRepository),
        getPregnancyStatusListUseCase = GetPregnancyStatusListUseCase(pregnancyPayRepository),
        getPregnancyTypeListUseCase = GetPregnancyTypeListUseCase(pregnancyPayRepository),
        uploadImageUseCase = UploadImageUseCase(FakeContractsRepository()),
        sendPregnancyPayRequestUseCase = SendPregnancyPayRequestUseCase(pregnancyPayRepository),
    )

    /** Advances a fresh [viewModel] all the way to [PregnancyPayStep.DoctorAndRequest] with the minimal valid data each earlier step's gate requires. */
    private fun advanceToDoctorAndRequestStep(viewModel: PregnancyPayViewModel) {
        viewModel.sendIntent(PregnancyPayIntent.OnLandingStartClicked)
        viewModel.sendIntent(PregnancyPayIntent.OnRestStartDatePicked(millis = 0L, label = "۱۴۰۵/۰۱/۰۱"))
        viewModel.sendIntent(PregnancyPayIntent.OnRestEndDatePicked(millis = 10_000_000L, label = "۱۴۰۵/۰۲/۰۱"))
        viewModel.sendIntent(PregnancyPayIntent.OnNextFromBranchAndRestClicked)
        viewModel.sendIntent(PregnancyPayIntent.OnBabyBirthDatePicked(millis = 100_000L, label = "۱۴۰۵/۰۱/۰۲"))
        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyStatusPicked(PregnancyPayOptionUi(id = "1", label = "بارداری طبیعی")),
        )
        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyTypePicked(PregnancyPayOptionUi(id = "1", label = "تک قلو")),
        )
        viewModel.sendIntent(PregnancyPayIntent.OnChildNationalCodeChanged(1, "0011122233"))
        viewModel.sendIntent(PregnancyPayIntent.OnNextFromPregnancyAndNewbornClicked)
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        pregnancyPayRepository = FakePregnancyPayRepository().apply {
            mainInfoResult = PregnancyPayTestData.femaleMainInfo
            pregnancyStatusListResult = PregnancyPayTestData.pregnancyStatusOptions
            pregnancyTypeListResult = PregnancyPayTestData.pregnancyTypeOptions
        }
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loadInitialData_femaleUser_setsMainInfoAndBranchWithoutBlocking() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertEquals(PregnancyPayStep.Landing, state.currentStep)
        assertFalse(state.isMaleBlocked)
        assertNotNull(state.mainInfo)
        assertEquals(1, state.branchOptions.size)
        assertEquals("10", state.branch?.id)
    }

    @Test
    fun loadInitialData_maleUser_blocksWithoutCrashing() = runTest(testDispatcher) {
        pregnancyPayRepository.mainInfoResult = PregnancyPayTestData.maleMainInfo
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertTrue(state.isMaleBlocked)
    }

    @Test
    fun genderBlockAcknowledged_sendsNavigateBackEvent() = runTest(testDispatcher) {
        pregnancyPayRepository.mainInfoResult = PregnancyPayTestData.maleMainInfo
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(PregnancyPayIntent.OnGenderBlockAcknowledged)
            assertEquals(PregnancyPayEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun pregnancyTypePicked_twins_requiresTwoChildCodes() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyTypePicked(PregnancyPayOptionUi(id = "2", label = "دوقلو")),
        )

        assertEquals(2, viewModel.uiState.value.requiredChildNationalCodeCount)
    }

    @Test
    fun pregnancyTypePicked_switchingBackToSingle_clearsExtraChildCodes() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyTypePicked(PregnancyPayOptionUi(id = "3", label = "سه قلو")),
        )
        viewModel.sendIntent(PregnancyPayIntent.OnChildNationalCodeChanged(2, "0011122233"))
        viewModel.sendIntent(PregnancyPayIntent.OnChildNationalCodeChanged(3, "0022233344"))

        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyTypePicked(PregnancyPayOptionUi(id = "1", label = "تک قلو")),
        )

        val state = viewModel.uiState.value
        assertEquals(1, state.requiredChildNationalCodeCount)
        assertEquals("", state.childNationalCode2)
        assertEquals("", state.childNationalCode3)
    }

    @Test
    fun landingStart_advancesToBranchAndRestStep() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.sendIntent(PregnancyPayIntent.OnLandingStartClicked)

        assertEquals(PregnancyPayStep.BranchAndRest, viewModel.uiState.value.currentStep)
    }

    @Test
    fun nextFromBranchAndRest_doesNotAdvance_whenRestDatesAreMissing() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(PregnancyPayIntent.OnLandingStartClicked)
        // femaleMainInfo has exactly one branch workshop, so it is already auto-selected — only the
        // rest-date range is still missing.
        assertEquals("10", viewModel.uiState.value.branch?.id)

        viewModel.sendIntent(PregnancyPayIntent.OnNextFromBranchAndRestClicked)

        assertEquals(PregnancyPayStep.BranchAndRest, viewModel.uiState.value.currentStep)
    }

    @Test
    fun nextFromBranchAndRest_advancesToPregnancyAndNewborn_whenBranchAndDatesAreValid() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.sendIntent(PregnancyPayIntent.OnLandingStartClicked)
        viewModel.sendIntent(PregnancyPayIntent.OnRestStartDatePicked(millis = 0L, label = "۱۴۰۵/۰۱/۰۱"))
        viewModel.sendIntent(PregnancyPayIntent.OnRestEndDatePicked(millis = 10_000_000L, label = "۱۴۰۵/۰۲/۰۱"))

        viewModel.sendIntent(PregnancyPayIntent.OnNextFromBranchAndRestClicked)

        assertEquals(PregnancyPayStep.PregnancyAndNewborn, viewModel.uiState.value.currentStep)
    }

    @Test
    fun nextFromDoctorAndRequest_withNothingFilled_doesNotAdvanceToDocumentsStep() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceToDoctorAndRequestStep(viewModel)
        assertEquals(PregnancyPayStep.DoctorAndRequest, viewModel.uiState.value.currentStep)

        viewModel.sendIntent(PregnancyPayIntent.OnNextFromDoctorAndRequestClicked)

        assertEquals(PregnancyPayStep.DoctorAndRequest, viewModel.uiState.value.currentStep)
    }

    @Test
    fun backToPreviousStep_onLandingStep_sendsNavigateBackEvent() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(PregnancyPayIntent.BackToPreviousStep)
            assertEquals(PregnancyPayEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun backToPreviousStep_walksBackThroughEachStep() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceToDoctorAndRequestStep(viewModel)
        assertEquals(PregnancyPayStep.DoctorAndRequest, viewModel.uiState.value.currentStep)

        viewModel.sendIntent(PregnancyPayIntent.BackToPreviousStep)
        assertEquals(PregnancyPayStep.PregnancyAndNewborn, viewModel.uiState.value.currentStep)

        viewModel.sendIntent(PregnancyPayIntent.BackToPreviousStep)
        assertEquals(PregnancyPayStep.BranchAndRest, viewModel.uiState.value.currentStep)

        viewModel.sendIntent(PregnancyPayIntent.BackToPreviousStep)
        assertEquals(PregnancyPayStep.Landing, viewModel.uiState.value.currentStep)
    }
}
