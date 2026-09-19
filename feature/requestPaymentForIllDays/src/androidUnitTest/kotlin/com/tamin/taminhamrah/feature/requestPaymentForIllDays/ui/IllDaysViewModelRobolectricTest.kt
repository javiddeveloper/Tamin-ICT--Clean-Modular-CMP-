package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake.FakeIllDaysCityProvinceRepository
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake.FakeIllDaysContractsRepository
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake.FakeIllDaysRepository
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate.IllDaysCalculateEvent
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate.IllDaysCalculateIntent
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate.IllDaysCalculateViewModel
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate.IllDaysMaritalStatus
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard.IllDaysWizardEvent
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard.IllDaysWizardIntent
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard.IllDaysWizardStep
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard.IllDaysWizardViewModel
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.requestPaymentForIllDays.toPresentation
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.CalcIllnessAmountUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetCovidResultUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetIllDaysInsuredMainInfoUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.SendRequestForIllDayUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

/**
 * Paths that call formatted `getString(Res.string.*, ...)` need Robolectric — plain JVM unit
 * tests hang on Compose Multiplatform resource resolution (same constraint as orotez-protez).
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class IllDaysViewModelRobolectricTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var illDaysRepository: FakeIllDaysRepository
    private lateinit var cityRepository: FakeIllDaysCityProvinceRepository
    private lateinit var contractsRepository: FakeIllDaysContractsRepository
    private lateinit var wizardViewModel: IllDaysWizardViewModel
    private lateinit var calculateViewModel: IllDaysCalculateViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        illDaysRepository = FakeIllDaysRepository()
        cityRepository = FakeIllDaysCityProvinceRepository()
        contractsRepository = FakeIllDaysContractsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun buildWizardViewModel(): IllDaysWizardViewModel = IllDaysWizardViewModel(
        getIllDaysInsuredMainInfoUseCase = GetIllDaysInsuredMainInfoUseCase(illDaysRepository),
        getCitiesUseCase = GetCitiesUseCase(cityRepository),
        getCovidResultUseCase = GetCovidResultUseCase(illDaysRepository),
        uploadImageUseCase = UploadImageUseCase(contractsRepository),
        sendRequestForIllDayUseCase = SendRequestForIllDayUseCase(illDaysRepository),
    )

    @Test
    fun calculate_validInputs_loadsResult() = runTest(testDispatcher) {
        illDaysRepository.calcResult = listOf("1000000", "500000")
        calculateViewModel = IllDaysCalculateViewModel(
            calcIllnessAmountUseCase = CalcIllnessAmountUseCase(illDaysRepository),
        )
        val start = 1_700_000_000_000L
        val end = start + MILLIS_PER_DAY

        calculateViewModel.sendIntent(IllDaysCalculateIntent.StartDatePicked(millis = start, label = "start"))
        calculateViewModel.sendIntent(IllDaysCalculateIntent.EndDatePicked(millis = end, label = "end"))
        calculateViewModel.sendIntent(IllDaysCalculateIntent.SelectMarital(IllDaysMaritalStatus.Married))
        testDispatcher.scheduler.advanceUntilIdle()

        calculateViewModel.sendIntent(IllDaysCalculateIntent.Calculate)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(calculateViewModel.uiState.value.result)
    }

    @Test
    fun documentUpload_rejectsDuplicate() = runTest(testDispatcher) {
        contractsRepository.uploadGuid = "guid-1"
        wizardViewModel = buildWizardViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val bytes = byteArrayOf(10, 20, 30)
        wizardViewModel.sendIntent(IllDaysWizardIntent.DocumentImagePicked(fileName = "a.jpg", bytes = bytes))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, wizardViewModel.uiState.value.documents.size)

        wizardViewModel.events.test {
            wizardViewModel.sendIntent(IllDaysWizardIntent.DocumentImagePicked(fileName = "b.jpg", bytes = bytes))
            assertIs<IllDaysWizardEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(1, wizardViewModel.uiState.value.documents.size)
    }

    @Test
    fun documentUpload_rejectsWhenMaxCountReached() = runTest(testDispatcher) {
        wizardViewModel = buildWizardViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        repeat(5) { index ->
            contractsRepository.uploadGuid = "guid-$index"
            wizardViewModel.sendIntent(
                IllDaysWizardIntent.DocumentImagePicked(
                    fileName = "doc$index.jpg",
                    bytes = byteArrayOf(index.toByte()),
                )
            )
            testDispatcher.scheduler.advanceUntilIdle()
        }
        assertEquals(5, wizardViewModel.uiState.value.documents.size)

        wizardViewModel.events.test {
            wizardViewModel.sendIntent(IllDaysWizardIntent.OpenDocumentSource)
            assertIs<IllDaysWizardEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submit_buildsCorrectRequestWithLegacyCodes() = runTest(testDispatcher) {
        wizardViewModel = buildWizardViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        fillStepOne()
        advanceToRestDaysStep()
        fillValidRestDates()
        advanceToDoctorStep()
        fillDoctorStep()
        advanceToDocumentsStep()
        uploadOneDocument()
        testDispatcher.scheduler.advanceUntilIdle()

        wizardViewModel.sendIntent(IllDaysWizardIntent.CovidChanged(enabled = true))
        testDispatcher.scheduler.advanceUntilIdle()
        wizardViewModel.sendIntent(IllDaysWizardIntent.MedicalRecordChanged(enabled = true))
        testDispatcher.scheduler.advanceUntilIdle()

        wizardViewModel.events.test {
            wizardViewModel.sendIntent(IllDaysWizardIntent.Submit)
            assertIs<IllDaysWizardEvent.ShowSuccess>(awaitItem())
            assertIs<IllDaysWizardEvent.NavigateBack>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val request = illDaysRepository.lastRequest
        assertNotNull(request)
        assertEquals("1", request.illnessKind)
        assertEquals("1", request.workStatus)
        assertEquals("0101", request.requestFileList.first().documentType)
        assertEquals("Ali", request.insuranceFirstName)
        assertEquals("0012345678", request.nationalCode)
    }

    private fun fillStepOne() {
        val city = cityRepository.cities.toCityPresentation().first()
        val branch = wizardViewModel.uiState.value.branchOptions.firstOrNull()
            ?: illDaysRepository.branchWorkshops.first().toPresentation()
        wizardViewModel.sendIntent(IllDaysWizardIntent.BranchPicked(branch))
        wizardViewModel.sendIntent(IllDaysWizardIntent.CityPicked(city))
    }

    private fun advanceToRestDaysStep() {
        wizardViewModel.sendIntent(IllDaysWizardIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(IllDaysWizardStep.RestDays, wizardViewModel.uiState.value.currentStep)
    }

    private fun fillValidRestDates() {
        val start = 1_700_000_000_000L
        val end = start + MILLIS_PER_DAY
        wizardViewModel.sendIntent(IllDaysWizardIntent.StartDatePicked(millis = start, label = "start"))
        wizardViewModel.sendIntent(IllDaysWizardIntent.EndDatePicked(millis = end, label = "end"))
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun advanceToDoctorStep() {
        wizardViewModel.sendIntent(IllDaysWizardIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(IllDaysWizardStep.Doctor, wizardViewModel.uiState.value.currentStep)
    }

    private fun fillDoctorStep() {
        wizardViewModel.sendIntent(IllDaysWizardIntent.DoctorNameChanged("Dr. Test"))
        wizardViewModel.sendIntent(IllDaysWizardIntent.DoctorCodeChanged("12345678"))
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun advanceToDocumentsStep() {
        wizardViewModel.sendIntent(IllDaysWizardIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(IllDaysWizardStep.Documents, wizardViewModel.uiState.value.currentStep)
    }

    private fun uploadOneDocument() {
        wizardViewModel.sendIntent(
            IllDaysWizardIntent.DocumentImagePicked(
                fileName = "doc.jpg",
                bytes = byteArrayOf(1, 2, 3, 4),
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
