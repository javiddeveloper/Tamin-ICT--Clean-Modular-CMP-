package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.ContractDebitDN
import com.tamin.taminhamrah.model.contracts.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.model.contracts.PaymentCalculationRowDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetContractDebitUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetContractDebitUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetContractDebitUseCase(repository)
    }

    @Test
    fun `invoke passes premium type and month and returns the debit`() = runTest {
        val expected = ContractDebitDN(
            total = 53_866_782L,
            insurancePremiums = 53_866_782L,
            previousDebit = 0L,
            startDate = 1L,
            endDate = 2L,
            payPremiumDate = "14051001",
            infoMessage = null,
        )
        repository.contractDebitResult = expected

        useCase(ContractPremiumType.OPTIONAL, month = 3).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals(ContractPremiumType.OPTIONAL, repository.lastDebitPremiumType)
        assertEquals(3, repository.lastDebitMonth)
    }
}

class GetContractLastPaymentUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetContractLastPaymentUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetContractLastPaymentUseCase(repository)
    }

    @Test
    fun `invoke passes premium type and returns the last payment`() = runTest {
        val expected = ContractLastPaymentDN(
            lastPaymentTimestamp = 1_700_000_000_000L,
            checkReloLap = "1",
            medicalResultResend = null,
        )
        repository.contractLastPaymentResult = expected

        useCase(ContractPremiumType.FREELANCE).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals(ContractPremiumType.FREELANCE, repository.lastLastPaymentPremiumType)
    }
}

class GetPaymentCalculationDetailsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetPaymentCalculationDetailsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetPaymentCalculationDetailsUseCase(repository)
    }

    @Test
    fun `invoke passes premium type and period and returns the rows`() = runTest {
        val rows = listOf(
            PaymentCalculationRowDN("1405", "06", "23", "حق بیمه", 6_650_220.0, 45_886_518.0),
            PaymentCalculationRowDN("1405", "06", "23", "کمک دولت", 6_650_220.0, -4_588_652.0),
        )
        repository.paymentCalculationDetailsResult = rows

        useCase(ContractPremiumType.FREELANCE, startDate = 10L, endDate = 20L).test {
            assertEquals(rows, awaitItem())
            awaitComplete()
        }

        assertEquals(
            Triple(ContractPremiumType.FREELANCE, 10L, 20L),
            repository.lastCalcDetailsArgs,
        )
    }
}
