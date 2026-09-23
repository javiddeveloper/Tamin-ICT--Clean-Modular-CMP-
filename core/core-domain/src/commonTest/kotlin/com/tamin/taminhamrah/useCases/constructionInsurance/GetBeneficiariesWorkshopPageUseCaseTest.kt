package com.tamin.taminhamrah.useCases.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.FakeConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class GetBeneficiariesWorkshopPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeConstructionInsuranceRepository
    private lateinit var useCase: GetBeneficiariesWorkshopPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakeConstructionInsuranceRepository()
        useCase = GetBeneficiariesWorkshopPageUseCase(repository)
    }

    @Test
    fun `invoke should return the page from repository`() = runTest {
        val expected = PageDN(
            items = listOf(
                BeneficiaryConstructionDN(
                    nationalCode = "0930123450", ownerType = "01", requestNumber = 123L,
                    fileNumber = 456L, requestDate = "14020901", name = "علی",
                    lastName = "توکلی", mobile = "09123456700",
                )
            ),
            total = 1,
        )
        repository.beneficiariesPageResult = expected

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

        assertEquals(query, repository.lastBeneficiariesPageQuery)
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
