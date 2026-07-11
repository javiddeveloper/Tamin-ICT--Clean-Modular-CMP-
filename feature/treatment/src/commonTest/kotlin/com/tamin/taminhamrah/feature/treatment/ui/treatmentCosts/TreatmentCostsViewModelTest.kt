package com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.treatment.fake.FakeTreatmentRepository
import com.tamin.taminhamrah.feature.treatment.fake.TreatmentTestData
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsIntent
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsPDFUseCase
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsUseCase
import com.tamin.taminhamrah.useCases.treatment.SendToInboxTreatmentCostsUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TreatmentCostsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var viewModel: TreatmentCostsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTreatmentRepository()
        viewModel = TreatmentCostsViewModel(
            getTreatmentCostsUseCase = GetTreatmentCostsUseCase(repository),
            getTreatmentCostsPDFUseCase = GetTreatmentCostsPDFUseCase(repository),
            sendToInboxTreatmentCostsUseCase = SendToInboxTreatmentCostsUseCase(repository)
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun testLoadList_populatesTreatmentCosts() = runTest(testDispatcher) {
        repository.treatmentCostsResult = listOf(TreatmentTestData.treatmentCost())

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(CostsIntent.LoadList)

            var state = awaitItem()
            while (state.treatmentCostList.isEmpty()) state = awaitItem()

            assertEquals(1, state.treatmentCostList.size)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun testLoadList_whenRepositoryFails_emitsError() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(CostsIntent.LoadList)

            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun testDownloadPdf_setsViewerPdfAndShowsDialog() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(CostsIntent.DownloadPdf("1"))

            var state = awaitItem()
            while (state.viewerPdf == null) state = awaitItem()

            assertNotNull(state.viewerPdf)
            assertTrue(state.showPdfDialog)
        }
    }

    @Test
    fun testSendToInbox_setsResult() = runTest(testDispatcher) {
        repository.sendToInboxResult = "SUCCESS"

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(CostsIntent.SendToInbox("1"))

            var state = awaitItem()
            while (state.sendToInboxResult == null) state = awaitItem()

            assertEquals("SUCCESS", state.sendToInboxResult)
        }
    }
}
