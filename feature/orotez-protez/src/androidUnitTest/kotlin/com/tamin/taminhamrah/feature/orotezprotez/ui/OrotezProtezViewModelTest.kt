package com.tamin.taminhamrah.feature.orotezprotez.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeOrotezProtezRepository
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.GetInsuredPersonsUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.GetRequestInsuredMainInfoUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.SaveShortTermOrthosisUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Covers branch loading and the step machine.
 *
 * Deliberately stays on paths that never resolve a Compose Multiplatform string resource:
 * `getString(Res.string...)` needs an initialized Android context, which plain JVM unit tests
 * don't have (this repo has no Robolectric). That is also why [OrotezProtezTestData.insuredPersons]
 * is empty here — building insured-person options calls `getString`, so the load would fail. The
 * insured person is instead picked directly through [OrotezProtezIntent.OnInsuredPersonPicked],
 * exactly as the screen does once the user taps an option.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrotezProtezViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var orotezProtezRepository: FakeOrotezProtezRepository
    private lateinit var viewModel: OrotezProtezViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        orotezProtezRepository = FakeOrotezProtezRepository().apply {
            mainInfoResult = OrotezProtezTestData.mainInfo
            insuredPersonsResult = OrotezProtezTestData.insuredPersons
        }
        viewModel = OrotezProtezViewModel(
            getRequestInsuredMainInfoUseCase = GetRequestInsuredMainInfoUseCase(orotezProtezRepository),
            getInsuredPersonsUseCase = GetInsuredPersonsUseCase(orotezProtezRepository),
            uploadImageUseCase = UploadImageUseCase(FakeContractsRepository()),
            saveShortTermOrthosisUseCase = SaveShortTermOrthosisUseCase(orotezProtezRepository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loadInitialData_selectsFirstBranchAndKeepsMainInfo() = runTest(testDispatcher) {
        val state = viewModel.uiState.value

        assertEquals(1, state.branchOptions.size)
        assertEquals("10-20", state.branch?.id)
        assertNotNull(state.mainInfo)
        assertNull(state.error)
    }

    @Test
    fun nextStep_advancesOnlyAfterInsuredPersonAndDateArePicked() = runTest(testDispatcher) {
        viewModel.sendIntent(OrotezProtezIntent.OnNextStepClicked)
        assertEquals(OrotezProtezStep.UserSelection, viewModel.uiState.value.currentStep)

        viewModel.sendIntent(
            OrotezProtezIntent.OnInsuredPersonPicked(OrotezProtezOptionUi(id = "insured-1", label = "علی رضایی")),
        )
        viewModel.sendIntent(OrotezProtezIntent.OnPrescriptionDatePicked(millis = 1_000L, label = "1403/01/01"))
        viewModel.sendIntent(OrotezProtezIntent.OnNextStepClicked)
        assertEquals(OrotezProtezStep.InsuredInfo, viewModel.uiState.value.currentStep)

        viewModel.sendIntent(OrotezProtezIntent.OnConfirmInsuredInfoClicked)
        assertEquals(OrotezProtezStep.Documents, viewModel.uiState.value.currentStep)
    }

    @Test
    fun backToPreviousStep_onFirstStep_sendsNavigateBackEvent() = runTest(testDispatcher) {
        viewModel.events.test {
            viewModel.sendIntent(OrotezProtezIntent.BackToPreviousStep)

            assertEquals(OrotezProtezEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
