package com.tamin.taminhamrah.feature.treatment.ui.prescriptions

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.treatment.fake.FakeCityProvinceRepository
import com.tamin.taminhamrah.feature.treatment.fake.FakeUserRepository
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.feature.treatment.fake.FakeTreatmentRepository
import com.tamin.taminhamrah.feature.treatment.fake.TreatmentTestData
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.useCases.treatment.DownloadLabResultPdfUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionDetailUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionPriceUseCase
import com.tamin.taminhamrah.useCases.treatment.GetPrescriptionPdfFileUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PrescriptionsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val nationalCode = TreatmentTestData.MAIN_NATIONAL_CODE

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var viewModel: PrescriptionsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTreatmentRepository()
        viewModel = PrescriptionsViewModel(
            identityInfoUseCase = IdentityInfoUseCase(FakeUserRepository(), FakeCityProvinceRepository()),
            getElectronicPrescriptionListUseCase = GetElectronicPrescriptionListUseCase(repository),
            getElectronicPrescriptionDetailUseCase = GetElectronicPrescriptionDetailUseCase(repository),
            getElectronicPrescriptionPriceUseCase = GetElectronicPrescriptionPriceUseCase(repository),
            getPrescriptionPdfFileUseCase = GetPrescriptionPdfFileUseCase(repository),
            downloadLabResultPdfUseCase = DownloadLabResultPdfUseCase(repository)
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun testLoadList_populatesPrescriptions() = runTest(testDispatcher) {
        repository.prescriptionListResult = listOf(TreatmentTestData.prescription())

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(PrescriptionsIntent.LoadList(nationalCode, requestTypeIds = listOf("1")))

            var state = awaitItem()
            while (state.prescriptionList.isEmpty()) state = awaitItem()

            assertEquals(1, state.prescriptionList.size)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun testLoadList_whenRepositoryFails_emitsError() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(PrescriptionsIntent.LoadList(nationalCode, requestTypeIds = listOf("1")))

            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun testSelectPrescription_loadsDetailsAndPrices() = runTest(testDispatcher) {
        repository.prescriptionDetailResult = listOf(TreatmentTestData.prescriptionDetail())
        repository.prescriptionPriceResult = listOf(TreatmentTestData.prescriptionPrice())

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(PrescriptionsIntent.SelectPrescription("100", nationalCode, type = "1", flagSata = "0"))

            var state = awaitItem()
            while (state.prescriptionDetailList.isEmpty() || state.prescriptionPriceList.isEmpty()) {
                state = awaitItem()
            }

            assertEquals("100", state.selectedNoteHeadId)
            assertEquals(1, state.prescriptionDetailList.size)
            assertEquals(1, state.prescriptionPriceList.size)
        }
    }

    @Test
    fun testClearSelectedPrescription_resetsSelection() = runTest(testDispatcher) {
        repository.prescriptionDetailResult = listOf(TreatmentTestData.prescriptionDetail())
        repository.prescriptionPriceResult = listOf(TreatmentTestData.prescriptionPrice())

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(PrescriptionsIntent.SelectPrescription("100", nationalCode, type = "1", flagSata = "0"))

            // Wait for the selection flow to fully complete before clearing, so the
            // clear cannot race with still-pending detail/price emissions.
            var selected = awaitItem()
            while (selected.selectedNoteHeadId == null ||
                selected.prescriptionDetailList.isEmpty() ||
                selected.prescriptionPriceList.isEmpty()
            ) {
                selected = awaitItem()
            }

            viewModel.sendIntent(PrescriptionsIntent.ClearSelectedPrescription)

            var cleared = awaitItem()
            while (cleared.selectedNoteHeadId != null) cleared = awaitItem()

            assertNull(cleared.selectedNoteHeadId)
            assertTrue(cleared.prescriptionDetailList.isEmpty())
            assertTrue(cleared.prescriptionPriceList.isEmpty())
        }
    }

    @Test
    fun testDownloadPdf_setsViewerPdf() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(PrescriptionsIntent.DownloadPdf("presc1"))
            var state = awaitItem()
            while (state.viewerPdf == null) state = awaitItem()
            assertNotNull(state.viewerPdf)

            // Closing drops it: the payload is a single-use stream, so keeping it would make the
            // next open render an empty file.
            viewModel.sendIntent(PrescriptionsIntent.DismissPdfViewer)
            while (state.viewerPdf != null) state = awaitItem()
            assertNull(state.viewerPdf)
        }
    }

    @Test
    fun testDownloadLabResult_setsViewerPdf() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(PrescriptionsIntent.DownloadLabResult(patientID = "0", noteHeadEprescID = "100"))
            var state = awaitItem()
            while (state.viewerPdf == null) state = awaitItem()
            assertNotNull(state.viewerPdf)
        }
    }
}
