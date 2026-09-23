package com.tamin.taminhamrah.useCases.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.FakeConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetConstructionFilesPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeConstructionInsuranceRepository
    private lateinit var useCase: GetConstructionFilesPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeConstructionInsuranceRepository()
        useCase = GetConstructionFilesPageUseCase(repository)
    }

    @Test
    fun `invoke should return the page from repository`() = runTest {
        val expected = PageDN(
            items = listOf(
                ConstructionFileDN(
                    fileNumber = 4_479_890_000L, requestNumber = 123_456_700L, requestDate = null,
                    workshopInfo = null, postalCode = null, address = null, mainPlaque = null,
                    subPlaque = null, block = null, propertyConstruction = null, apartment = null,
                    trade = null, partPlaque = null, sumOfComplications = null, debitNumber = null,
                    totalPayment = null, meterage = null, debitStatusCode = "51", protrusion = null,
                    applicationFees = null, residentialServiceInfrastructureFees = null,
                    excessDensitySurchargeFees = null, increasePropertyValue = null,
                    issuanceFencingWallConstructionFees = null, coveredClause3Fees = null,
                    article100 = null, paymentDeadLine = null,
                )
            ),
            total = 37,
        )
        repository.constructionFilesPageResult = expected

        useCase(ApiQueryParamDN()).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should forward the query to repository`() = runTest {
        val query = ApiQueryParamDN(page = 1, start = 10, limit = 10)

        useCase(query).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(query, repository.lastConstructionFilesPageQuery)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.thrownError = expectedException

        useCase(ApiQueryParamDN()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
