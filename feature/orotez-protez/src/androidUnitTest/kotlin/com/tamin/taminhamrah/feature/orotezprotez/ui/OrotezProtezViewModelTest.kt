package com.tamin.taminhamrah.feature.orotezprotez.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeOrotezProtezRepository
import com.tamin.taminhamrah.feature.orotezprotez.test.FakeComposeResourceEnvironment
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDN
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Lives in androidUnitTest (not commonTest) because [loadInitialData] resolves insured-person
 * subtitle strings via `getString(Res.string...)`, which on the Android target needs
 * [FakeComposeResourceEnvironment] installed to avoid the "not mocked" `Resources.getSystem()`
 * crash under plain JVM unit tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrotezProtezViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var orotezProtezRepository: FakeOrotezProtezRepository
    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var viewModel: OrotezProtezViewModel

    @BeforeTest
    fun setUp() {
        FakeComposeResourceEnvironment.install()
        Dispatchers.setMain(testDispatcher)
        orotezProtezRepository = FakeOrotezProtezRepository().apply {
            mainInfoResult = RequestInsuredMainInfoDN(
                risuid = "risuid-1",
                nationalCode = "0012345678",
                firstName = "علی",
                lastName = "رضایی",
                mobileNumber = "09120000000",
                genderCode = "M",
                branchCode = "10",
                branchName = "شعبه مرکزی",
                bankAccount = null,
                bankName = null,
                insuranceTypeDesc = null,
                insuranceStatusDesc = null,
                branchWorkshops = listOf(
                    BranchWorkshopDN(
                        branchCode = "10",
                        branchName = "شعبه مرکزی",
                        workshopCode = "20",
                        workshopName = "کارگاه اصلی",
                    ),
                ),
            )
            insuredPersonsResult = listOf(
                InsuredPersonDN(
                    insuredId = "insured-1",
                    firstName = "علی",
                    lastName = "رضایی",
                    nationalCode = "0012345678",
                    birthCertificateNumber = "1",
                    cityName = "تهران",
                    birthDate = null,
                    relationship = "بیمه شده اصلی",
                    relationshipCode = "01",
                    bookletValidUntil = null,
                ),
            )
        }
        contractsRepository = FakeContractsRepository()
        viewModel = OrotezProtezViewModel(
            getRequestInsuredMainInfoUseCase = GetRequestInsuredMainInfoUseCase(orotezProtezRepository),
            getInsuredPersonsUseCase = GetInsuredPersonsUseCase(orotezProtezRepository),
            uploadImageUseCase = UploadImageUseCase(contractsRepository),
            saveShortTermOrthosisUseCase = SaveShortTermOrthosisUseCase(orotezProtezRepository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun loadInitialData_populatesBranchAndInsuredPersonOptions() = runTest(testDispatcher) {
        viewModel.uiState.test {
            val state = awaitUntil { it.branchOptions.isNotEmpty() && it.insuredPersonOptions.isNotEmpty() }

            assertEquals("10-20", state.branch?.id)
            assertEquals("insured-1", state.insuredPersonOptions.first().id)
            assertNotNull(state.mainInfo)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitDocuments_withNoDocumentsUploaded_failsMinCountValidationWithoutSubmitting() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.branchOptions.isNotEmpty() }
            advanceToDocumentsStep()
            awaitUntil { it.currentStep == OrotezProtezStep.Documents }

            viewModel.sendIntent(OrotezProtezIntent.OnSubmitDocumentsClicked)
            val state = awaitUntil { it.documentValidationError != null }

            assertNotNull(state.documentValidationError)
            assertFalse(state.hasSubmitted)
            assertFalse(state.isSubmitting)
            assertNull(orotezProtezRepository.lastSaveRequest)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun backToPreviousStep_onFirstStep_sendsNavigateBackEvent() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.currentStep == OrotezProtezStep.UserSelection }
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            viewModel.sendIntent(OrotezProtezIntent.BackToPreviousStep)
            assertEquals(OrotezProtezEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onNextStepClicked_withoutBranchOrInsuredPersonSelected_doesNotAdvance() = runTest(testDispatcher) {
        viewModel.uiState.test {
            val loaded = awaitUntil { it.branchOptions.isNotEmpty() }
            assertTrue(loaded.currentStep == OrotezProtezStep.UserSelection)

            viewModel.sendIntent(OrotezProtezIntent.OnNextStepClicked)
            assertEquals(OrotezProtezStep.UserSelection, viewModel.uiState.value.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun advanceToDocumentsStep() {
        val insuredPerson = OrotezProtezOptionUi(id = "insured-1", label = "علی رضایی")
        viewModel.sendIntent(OrotezProtezIntent.OnInsuredPersonPicked(insuredPerson))
        viewModel.sendIntent(OrotezProtezIntent.OnPrescriptionDatePicked(millis = 1_000L, label = "1403/01/01"))
        viewModel.sendIntent(OrotezProtezIntent.OnNextStepClicked)
        viewModel.sendIntent(OrotezProtezIntent.OnConfirmInsuredInfoClicked)
    }

    private suspend fun ReceiveTurbine<OrotezProtezUiState>.awaitUntil(
        predicate: (OrotezProtezUiState) -> Boolean,
    ): OrotezProtezUiState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}
