package com.tamin.taminhamrah.useCases.contractAffair

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contractAffair.CancelContractParamsDN
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contractAffair.ContractStateChange
import com.tamin.taminhamrah.model.contractAffair.ContractStateDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.contractAffair.FakeContractAffairRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GetContractsPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractAffairRepository
    private lateinit var useCase: GetContractsPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractAffairRepository()
        useCase = GetContractsPageUseCase(repository)
    }

    @Test
    fun `invoke forwards the query and returns the repository page`() = runTest {
        val query = ApiQueryParamDN(
            filters = listOf(
                ApiFilterDN(FilterProperty.CONTRACT_NUMBER, "123456", FilterOperator.EQ),
            ),
        )
        repository.contractsPageResult = PageDN(items = emptyList(), total = 42)

        useCase(query).test {
            assertEquals(42, awaitItem().total)
            awaitComplete()
        }

        assertSame(query, repository.lastPageQuery)
    }

    @Test
    fun `invoke propagates a repository failure`() = runTest {
        repository.shouldThrowError = true
        repository.error = IllegalStateException("boom")

        useCase(ApiQueryParamDN()).test {
            assertTrue(awaitError() is IllegalStateException)
        }
    }
}

class GetContractStatesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractAffairRepository
    private lateinit var useCase: GetContractStatesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractAffairRepository()
        useCase = GetContractStatesUseCase(repository)
    }

    @Test
    fun `invoke returns the termination reasons`() = runTest {
        val states = listOf(
            ContractStateDN(code = 5, description = "ابطال به درخواست بیمه‌شده"),
            ContractStateDN(code = 7, description = "عدم پرداخت حق بیمه"),
        )
        repository.contractStatesResult = states

        useCase().test {
            assertEquals(states, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke propagates a repository failure`() = runTest {
        repository.shouldThrowError = true

        useCase().test {
            assertTrue(awaitError() is RuntimeException)
        }
    }
}

class CancelContractUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractAffairRepository
    private lateinit var useCase: CancelContractUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractAffairRepository()
        useCase = CancelContractUseCase(repository)
    }

    @Test
    fun `invoke forwards the cancel params and completes on success`() = runTest {
        val params = CancelContractParamsDN(
            premiumType = ContractPremiumType.FREELANCE,
            stateCode = 5,
            description = "توضیحات کاربر",
            stateChange = ContractStateChange.CANCEL,
        )

        useCase(params).test {
            awaitItem()
            awaitComplete()
        }

        assertTrue(repository.cancelContractCalled)
        assertEquals(params, repository.lastCancelParams)
    }

    @Test
    fun `invoke propagates a repository failure`() = runTest {
        repository.shouldThrowError = true
        val params = CancelContractParamsDN(
            premiumType = ContractPremiumType.OPTIONAL,
            stateCode = 1,
            description = null,
        )

        useCase(params).test {
            assertTrue(awaitError() is RuntimeException)
        }
    }
}

class GetContractPaymentHistoryUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractAffairRepository
    private lateinit var useCase: GetContractPaymentHistoryUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractAffairRepository()
        useCase = GetContractPaymentHistoryUseCase(repository)
    }

    @Test
    fun `invoke forwards the contract number and returns the rows`() = runTest {
        val rows = listOf(
            ContractPaymentHistoryItemDN(
                nationalId = "0012345678",
                insuranceId = "77",
                debtNumber = "9001",
                startTermPayment = "140501",
                endTermPayment = "140506",
                totalDebt = 53_866_782.0,
                paymentDeadline = "14051015",
                amountPayment = 53_866_782.0,
                datePayment = "14050610",
                statusContract = "پرداخت شده",
                statusRecipient = "وصول شده",
            ),
        )
        repository.paymentHistoryResult = rows

        useCase("9001").test {
            assertEquals(rows, awaitItem())
            awaitComplete()
        }

        assertEquals("9001", repository.lastPaymentHistoryContractNumber)
    }

    @Test
    fun `invoke propagates a repository failure`() = runTest {
        repository.shouldThrowError = true

        useCase("9001").test {
            assertTrue(awaitError() is RuntimeException)
        }
    }
}

class DownloadContractReportUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractAffairRepository
    private lateinit var useCase: DownloadContractReportUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractAffairRepository()
        useCase = DownloadContractReportUseCase(repository)
    }

    @Test
    fun `invoke forwards the premium type and returns the pdf`() = runTest {
        val expected = PdfDownloadDN(pdf = null)
        repository.contractReportResult = expected

        useCase(ContractPremiumType.FRACTION).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals(ContractPremiumType.FRACTION, repository.lastReportPremiumType)
    }

    @Test
    fun `invoke propagates a repository failure`() = runTest {
        repository.shouldThrowError = true

        useCase(ContractPremiumType.OPTIONAL).test {
            assertTrue(awaitError() is RuntimeException)
        }
    }
}
