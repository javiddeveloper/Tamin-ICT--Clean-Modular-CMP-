package com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.contractaffair.fake.FakeContractAffairRepository
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.contract.ContractPaymentHistoryEvent
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.contract.ContractPaymentHistoryIntent
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.contract.ContractPaymentHistoryUiState
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.useCases.contractAffair.GetContractPaymentHistoryUseCase
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
class ContractPaymentHistoryViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeContractAffairRepository
    private lateinit var viewModel: ContractPaymentHistoryViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeContractAffairRepository()
        viewModel = ContractPaymentHistoryViewModel(GetContractPaymentHistoryUseCase(repository))
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `Load seeds the header and publishes the mapped rows`() = runTest(dispatcher) {
        repository.paymentHistoryResult = listOf(
            paymentItem(debtNumber = "1", statusContract = "پرداخت شده", amountPayment = 1_000.0),
            paymentItem(debtNumber = "2", statusContract = "پرداخت نشده", amountPayment = 0.0),
        )

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.sendIntent(
                ContractPaymentHistoryIntent.Load(contractNumber = "9001", insuranceType = "حرف و مشاغل آزاد"),
            )
            val state = awaitUntil { !it.isLoading && it.items.isNotEmpty() }

            assertEquals("9001", state.contractNumber)
            assertEquals("حرف و مشاغل آزاد", state.insuranceType)
            assertEquals(2, state.paymentCount)
            assertTrue(state.items.first().isPaid)
            assertNull(state.error)
            assertEquals("9001", repository.lastPaymentHistoryContractNumber)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Load only fetches once`() = runTest(dispatcher) {
        repository.paymentHistoryResult = listOf(paymentItem(debtNumber = "1"))

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ContractPaymentHistoryIntent.Load("9001", "حرف و مشاغل آزاد"))
            awaitUntil { it.items.isNotEmpty() }
            repository.lastPaymentHistoryContractNumber = null

            viewModel.sendIntent(ContractPaymentHistoryIntent.Load("OTHER", "x"))

            assertNull(repository.lastPaymentHistoryContractNumber)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a failure surfaces as an error and clears loading`() = runTest(dispatcher) {
        repository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ContractPaymentHistoryIntent.Load("9001", "x"))
            val state = awaitUntil { it.error != null && !it.isLoading }

            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBackClicked emits NavigateBack`() = runTest(dispatcher) {
        viewModel.events.test {
            viewModel.sendIntent(ContractPaymentHistoryIntent.OnBackClicked)
            assertEquals(ContractPaymentHistoryEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<ContractPaymentHistoryUiState>.awaitUntil(
        predicate: (ContractPaymentHistoryUiState) -> Boolean,
    ): ContractPaymentHistoryUiState {
        var state = awaitItem()
        while (!predicate(state)) state = awaitItem()
        return state
    }
}

private fun paymentItem(
    debtNumber: String,
    statusContract: String? = "پرداخت شده",
    amountPayment: Double? = 1_000.0,
): ContractPaymentHistoryItemDN = ContractPaymentHistoryItemDN(
    nationalId = "0012345678",
    insuranceId = "77",
    debtNumber = debtNumber,
    startTermPayment = "140501",
    endTermPayment = "140506",
    totalDebt = 1_000.0,
    paymentDeadline = "14051015",
    amountPayment = amountPayment,
    datePayment = "14050610",
    statusContract = statusContract,
    statusRecipient = "وصول شده",
)
