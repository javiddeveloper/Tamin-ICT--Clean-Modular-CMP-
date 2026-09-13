package com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.contractaffair.fake.FakeContractAffairRepository
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.contract.ContractPaymentCalcDetailEvent
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.contract.ContractPaymentCalcDetailIntent
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.contract.ContractPaymentCalcDetailUiState
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDN
import com.tamin.taminhamrah.useCases.contractAffair.GetPaymentCalculationDetailsUseCase
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ContractPaymentCalcDetailViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeContractAffairRepository
    private lateinit var viewModel: ContractPaymentCalcDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeContractAffairRepository()
        viewModel = ContractPaymentCalcDetailViewModel(GetPaymentCalculationDetailsUseCase(repository))
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `Load groups the rows into one card per month and forwards the period`() =
        runTest(dispatcher) {
            repository.paymentCalculationDetailsResult = listOf(
                PaymentCalculationRowDN("1405", "06", "23", "حق بيمه", 6_650_220.0, 45_886_518.0),
                PaymentCalculationRowDN("1405", "06", "23", "کمک دولت", 6_650_220.0, -4_588_652.0),
                PaymentCalculationRowDN("1405", "07", "30", "حق بيمه", 6_650_220.0, 53_866_782.0),
            )

            viewModel.uiState.test {
                awaitItem()
                viewModel.sendIntent(
                    ContractPaymentCalcDetailIntent.Load(
                        premiumTypeCode = "01",
                        startDate = 10L,
                        endDate = 20L,
                    ),
                )
                val state = awaitUntil { !it.isLoading && it.rows.isNotEmpty() }

                assertEquals(2, state.rows.size) // شهریور + مهر
                assertNull(state.error)
                assertEquals(
                    Triple(com.tamin.taminhamrah.model.contractAffair.ContractPremiumType.FREELANCE, 10L, 20L),
                    repository.lastCalcDetailsArgs,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Load only fetches once`() = runTest(dispatcher) {
        repository.paymentCalculationDetailsResult =
            listOf(PaymentCalculationRowDN("1405", "06", "23", "حق بيمه", 1.0, 1.0))

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ContractPaymentCalcDetailIntent.Load("01", 1L, 2L))
            awaitUntil { it.rows.isNotEmpty() }
            repository.lastCalcDetailsArgs = null

            viewModel.sendIntent(ContractPaymentCalcDetailIntent.Load("02", 9L, 9L))

            assertNull(repository.lastCalcDetailsArgs)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a failure surfaces as an error and clears loading`() = runTest(dispatcher) {
        repository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ContractPaymentCalcDetailIntent.Load("01", 1L, 2L))
            val state = awaitUntil { it.error != null && !it.isLoading }

            assertFalse(state.isLoading)
            assertTrue(state.rows.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBackClicked emits NavigateBack`() = runTest(dispatcher) {
        viewModel.events.test {
            viewModel.sendIntent(ContractPaymentCalcDetailIntent.OnBackClicked)
            assertEquals(ContractPaymentCalcDetailEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<ContractPaymentCalcDetailUiState>.awaitUntil(
        predicate: (ContractPaymentCalcDetailUiState) -> Boolean,
    ): ContractPaymentCalcDetailUiState {
        var state = awaitItem()
        while (!predicate(state)) state = awaitItem()
        return state
    }
}
