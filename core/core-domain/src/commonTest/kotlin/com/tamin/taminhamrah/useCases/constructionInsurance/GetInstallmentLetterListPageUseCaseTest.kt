package com.tamin.taminhamrah.useCases.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.FakeConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetInstallmentLetterListPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeConstructionInsuranceRepository
    private lateinit var useCase: GetInstallmentLetterListPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeConstructionInsuranceRepository()
        useCase = GetInstallmentLetterListPageUseCase(repository)
    }

    @Test
    fun `invoke should return the page from repository and forward workshopId and branchId`() = runTest {
        val expected = PageDN(
            items = listOf(
                InstallmentLetterDN(
                    workshopId = "14020901", debitNumber = "77640000001", debitStepDescription = "قسط اول",
                    debitStatusDescription = "پرداخت شده", debitStartDate = "14021001", debitEndDate = "14031001",
                    remainingAmount = 400_000L, debitNumberOld = "OLD-776400-0001",
                )
            ),
            total = 1,
        )
        repository.installmentLettersPageResult = expected

        useCase(workshopId = "14020901", branchId = "6400", query = ApiQueryParamDN()).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }

        assertEquals("14020901", repository.lastInstallmentWorkshopId)
        assertEquals("6400", repository.lastInstallmentBranchId)
    }

    @Test
    fun `invoke should forward the query to repository`() = runTest {
        val query = ApiQueryParamDN(page = 1, start = 10, limit = 10)

        useCase(workshopId = "1", branchId = "2", query = query).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(query, repository.lastInstallmentPageQuery)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.thrownError = expectedException

        useCase(workshopId = "1", branchId = "2", query = ApiQueryParamDN()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
