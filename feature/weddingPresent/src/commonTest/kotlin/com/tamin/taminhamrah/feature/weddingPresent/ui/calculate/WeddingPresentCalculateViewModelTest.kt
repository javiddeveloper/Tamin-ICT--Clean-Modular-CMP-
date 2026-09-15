package com.tamin.taminhamrah.feature.weddingPresent.ui.calculate

import app.cash.turbine.test
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import com.tamin.taminhamrah.repository.weddingPresent.WeddingPresentRepository
import com.tamin.taminhamrah.useCases.weddingPresent.CalculateMarriageAllowanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class WeddingPresentCalculateViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWeddingPresentCalculateRepository
    private lateinit var viewModel: WeddingPresentCalculateViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWeddingPresentCalculateRepository()
        viewModel = WeddingPresentCalculateViewModel(
            calculateMarriageAllowanceUseCase = CalculateMarriageAllowanceUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun calculate_blankPayable_showsToastAndClearsResult() = runTest(testDispatcher) {
        repository.calculateResult = listOf("error message", "")
        viewModel.sendIntent(
            WeddingPresentCalculateIntent.MarriageDatePicked(
                millis = 1_700_000_000_000L,
                label = "۱۴۰۲/۰۱/۰۱",
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(WeddingPresentCalculateIntent.Calculate)
            assertIs<WeddingPresentCalculateEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun calculate_success_loadsResult() = runTest(testDispatcher) {
        repository.calculateResult = listOf("130300000", "130300000")
        viewModel.sendIntent(
            WeddingPresentCalculateIntent.MarriageDatePicked(
                millis = 1_700_000_000_000L,
                label = "۱۴۰۲/۰۱/۰۱",
            ),
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(WeddingPresentCalculateIntent.Calculate)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.uiState.value.result
        assertNotNull(result)
        assertEquals("۱۴۰۲/۰۱/۰۱", result.marriageDateLabel)
        assertEquals("۱۳۰٬۳۰۰٬۰۰۰ ریال", result.averageSalaryLabel)
        assertEquals("۱۳۰٬۳۰۰٬۰۰۰ ریال", result.payableAmountLabel)
        assertEquals("1700000000000", repository.lastCalculateTimeStamp)
    }
}

private class FakeWeddingPresentCalculateRepository : WeddingPresentRepository {
    var calculateResult: List<String> = listOf("1000", "2000")
    var lastCalculateTimeStamp: String? = null

    override fun getWeddingPresentInfo(): Flow<WeddingPresentInfoDN> = flowOf(WeddingPresentInfoDN())

    override fun submitWeddingPresent(request: WeddingPresentSubmitRequestDN): Flow<Unit> =
        flowOf(Unit)

    override fun calculateMarriageAllowance(timeStamp: String): Flow<List<String>> {
        lastCalculateTimeStamp = timeStamp
        return flowOf(calculateResult)
    }
}
