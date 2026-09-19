package com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.contractaffair.fake.FakeContractAffairRepository
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.ContractPremiumPaymentEvent
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.ContractPremiumPaymentIntent
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.contract.ContractPremiumPaymentUiState
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDN
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.feature.contractaffair.fake.FakeContractsRepository
import com.tamin.taminhamrah.useCases.contractAffair.GetContractDebitUseCase
import com.tamin.taminhamrah.useCases.contractAffair.GetContractLastPaymentUseCase
import com.tamin.taminhamrah.useCases.contracts.GetInsurancePaymentUseCase
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
class ContractPremiumPaymentViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeContractAffairRepository
    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var viewModel: ContractPremiumPaymentViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeContractAffairRepository()
        contractsRepository = FakeContractsRepository()
        viewModel = ContractPremiumPaymentViewModel(
            getContractLastPaymentUseCase = GetContractLastPaymentUseCase(repository),
            getContractDebitUseCase = GetContractDebitUseCase(repository),
            getInsurancePaymentUseCase = GetInsurancePaymentUseCase(contractsRepository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `Load seeds the header, resolves freelance from the code and loads the last payment`() =
        runTest(dispatcher) {
            repository.contractLastPaymentResult = ContractLastPaymentDN(
                lastPaymentTimestamp = 1_700_000_000_000L,
                checkReloLap = "1",
                medicalResultResend = null,
            )

            viewModel.uiState.test {
                awaitItem()
                viewModel.sendIntent(
                    ContractPremiumPaymentIntent.Load(
                        contractNumber = "9001",
                        premiumTypeCode = "01",
                        insuranceType = "حرف و مشاغل آزاد",
                    ),
                )
                val state = awaitUntil { !it.isInitLoading && it.lastPayment != null }

                assertEquals("9001", state.contractNumber)
                assertEquals("01", state.premiumTypeCode)
                assertTrue(state.isFreelance)
                assertEquals(ContractPremiumType.FREELANCE, repository.lastLastPaymentPremiumType)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Load resolves the optional contract kind`() = runTest(dispatcher) {
        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ContractPremiumPaymentIntent.Load("9001", "02", "بیمه اختیاری"))
            val state = awaitUntil { it.lastPayment != null }

            assertTrue(!state.isFreelance)
            assertEquals(ContractPremiumType.OPTIONAL, repository.lastLastPaymentPremiumType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `the month stepper clamps to 1 and 12`() = runTest(dispatcher) {
        viewModel.uiState.test {
            assertEquals(1, awaitItem().months)

            viewModel.sendIntent(ContractPremiumPaymentIntent.DecrementMonths) // already at min
            viewModel.sendIntent(ContractPremiumPaymentIntent.IncrementMonths)
            assertEquals(2, awaitUntil { it.months == 2 }.months)

            repeat(20) { viewModel.sendIntent(ContractPremiumPaymentIntent.IncrementMonths) }
            assertEquals(12, awaitUntil { it.months == 12 }.months)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Calculate publishes the debit and changing months clears it`() = runTest(dispatcher) {
        repository.contractDebitResult = ContractDebitDN(
            total = 53_866_782L,
            insurancePremiums = 50_000_000L,
            previousDebit = 0L,
            startDate = 1L,
            endDate = 2L,
            payPremiumDate = "14051001",
            infoMessage = null,
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ContractPremiumPaymentIntent.Load("9001", "01", "حرف و مشاغل آزاد"))
            awaitUntil { it.lastPayment != null }

            viewModel.sendIntent(ContractPremiumPaymentIntent.Calculate)
            assertNotNull(awaitUntil { it.debit != null }.debit)
            assertEquals(ContractPremiumType.FREELANCE, repository.lastDebitPremiumType)
            assertEquals(1, repository.lastDebitMonth)

            viewModel.sendIntent(ContractPremiumPaymentIntent.IncrementMonths)
            assertNull(awaitUntil { it.months == 2 }.debit)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a non-ok chekReloLap raises a warning event`() = runTest(dispatcher) {
        repository.contractLastPaymentResult = ContractLastPaymentDN(
            lastPaymentTimestamp = 1_700_000_000_000L,
            checkReloLap = "قرارداد شما نیاز به بازبینی دارد",
            medicalResultResend = null,
        )

        viewModel.events.test {
            viewModel.sendIntent(ContractPremiumPaymentIntent.Load("9001", "01", "حرف و مشاغل آزاد"))
            val event = awaitItem()
            assertTrue(event is ContractPremiumPaymentEvent.ShowWarning)
            assertEquals("قرارداد شما نیاز به بازبینی دارد", event.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a calculate failure raises an error event`() = runTest(dispatcher) {
        repository.shouldThrowError = true

        viewModel.events.test {
            viewModel.sendIntent(ContractPremiumPaymentIntent.Calculate)
            var event = awaitItem()
            while (event !is ContractPremiumPaymentEvent.ShowError) event = awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Pay intent emits NavigateToPayment when ticket is fetched`() = runTest(dispatcher) {
        repository.contractDebitResult = ContractDebitDN(
            total = 53_866_782L,
            insurancePremiums = 50_000_000L,
            previousDebit = 0L,
            startDate = 1000L,
            endDate = 2000L,
            payPremiumDate = "14051001",
            infoMessage = null,
        )
        contractsRepository.insurancePaymentResult = InsurancePaymentDN(
            paymentTicket = "TICKET-12345",
            paymentUrl = "https://tfh.tamin.ir/payment",
            responseMessage = "OK",
            succeed = true,
        )

        viewModel.sendIntent(ContractPremiumPaymentIntent.Calculate)

        viewModel.events.test {
            viewModel.sendIntent(ContractPremiumPaymentIntent.Pay)
            val event = awaitItem()
            assertTrue(event is ContractPremiumPaymentEvent.NavigateToPayment)
            assertEquals("TICKET-12345", event.request.ticket)
            assertEquals(PaymentVerifierKey.SPECIAL_INSURED, event.request.verifierKey)
            assertEquals("03", event.request.verifierReference)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Pay intent sends correct systemType 01 for optional insurance`() = runTest(dispatcher) {
        repository.contractLastPaymentResult = ContractLastPaymentDN(1L, "1", null)
        repository.contractDebitResult = ContractDebitDN(
            total = 53_866_782L,
            insurancePremiums = 50_000_000L,
            previousDebit = 0L,
            startDate = 1000L,
            endDate = 2000L,
            payPremiumDate = "14051001",
            infoMessage = null,
        )
        contractsRepository.insurancePaymentResult = InsurancePaymentDN(
            paymentTicket = "TICKET-12345",
            paymentUrl = "https://tfh.tamin.ir/payment",
            responseMessage = "OK",
            succeed = true,
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ContractPremiumPaymentIntent.Load("9001", "02", "بیمه اختیاری"))
            awaitUntil { it.lastPayment != null }
            viewModel.sendIntent(ContractPremiumPaymentIntent.Calculate)
            awaitUntil { it.debit != null }
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            viewModel.sendIntent(ContractPremiumPaymentIntent.Pay)
            val event = awaitItem()
            assertTrue(event is ContractPremiumPaymentEvent.NavigateToPayment)
            assertEquals("01", event.request.verifierReference)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Pay intent sends correct systemType 04 for fraction insurance`() = runTest(dispatcher) {
        repository.contractLastPaymentResult = ContractLastPaymentDN(1L, "1", null)
        repository.contractDebitResult = ContractDebitDN(
            total = 53_866_782L,
            insurancePremiums = 50_000_000L,
            previousDebit = 0L,
            startDate = 1000L,
            endDate = 2000L,
            payPremiumDate = "14051001",
            infoMessage = null,
        )
        contractsRepository.insurancePaymentResult = InsurancePaymentDN(
            paymentTicket = "TICKET-12345",
            paymentUrl = "https://tfh.tamin.ir/payment",
            responseMessage = "OK",
            succeed = true,
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ContractPremiumPaymentIntent.Load("9001", "38", "تکمیل سوابق کسری از ماه"))
            awaitUntil { it.lastPayment != null }
            viewModel.sendIntent(ContractPremiumPaymentIntent.Calculate)
            awaitUntil { it.debit != null }
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            viewModel.sendIntent(ContractPremiumPaymentIntent.Pay)
            val event = awaitItem()
            assertTrue(event is ContractPremiumPaymentEvent.NavigateToPayment)
            assertEquals("04", event.request.verifierReference)
            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `Pay intent emits ShowError when ticket fetch fails`() = runTest(dispatcher) {
        repository.contractDebitResult = ContractDebitDN(
            total = 53_866_782L,
            insurancePremiums = 50_000_000L,
            previousDebit = 0L,
            startDate = 1000L,
            endDate = 2000L,
            payPremiumDate = "14051001",
            infoMessage = null,
        )
        contractsRepository.shouldThrowError = true

        viewModel.sendIntent(ContractPremiumPaymentIntent.Calculate)

        viewModel.events.test {
            viewModel.sendIntent(ContractPremiumPaymentIntent.Pay)
            val event = awaitItem()
            assertTrue(event is ContractPremiumPaymentEvent.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBackClicked emits NavigateBack`() = runTest(dispatcher) {
        viewModel.events.test {
            viewModel.sendIntent(ContractPremiumPaymentIntent.OnBackClicked)
            assertEquals(ContractPremiumPaymentEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Pay intent does not emit ShowError when operation is cancelled via CancellationException`() = runTest(dispatcher) {
        repository.contractDebitResult = ContractDebitDN(
            total = 53_866_782L,
            insurancePremiums = 50_000_000L,
            previousDebit = 0L,
            startDate = 1000L,
            endDate = 2000L,
            payPremiumDate = "14051001",
            infoMessage = null,
        )
        contractsRepository.shouldThrowError = true
        contractsRepository.error = kotlinx.coroutines.CancellationException("Job cancelled")

        viewModel.sendIntent(ContractPremiumPaymentIntent.Calculate)

        viewModel.events.test {
            viewModel.sendIntent(ContractPremiumPaymentIntent.Pay)
            expectNoEvents()
        }
    }

    private suspend fun ReceiveTurbine<ContractPremiumPaymentUiState>.awaitUntil(
        predicate: (ContractPremiumPaymentUiState) -> Boolean,
    ): ContractPremiumPaymentUiState {
        var state = awaitItem()
        while (!predicate(state)) state = awaitItem()
        return state
    }
}
