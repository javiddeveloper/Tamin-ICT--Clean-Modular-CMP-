package com.tamin.taminhamrah.useCases.constructionInsurance

import app.cash.turbine.test
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.repository.constructionInsurance.FakeConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class GetConstructionFilesUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeConstructionInsuranceRepository
    private lateinit var useCase: GetConstructionFilesUseCase

    @BeforeTest
    fun setup() {
        repository = FakeConstructionInsuranceRepository()
        useCase = GetConstructionFilesUseCase(repository)
    }

    @Test
    fun `invoke should return construction files from repository`() = runTest {
        val expected = listOf(
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
        )
        repository.constructionFilesResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
        assertNull(repository.lastConstructionFilesSearch)
    }

    @Test
    fun `invoke should forward the search parameters to repository`() = runTest {
        val search = ConstructionFileSearchParamsDN(fileNo = "1234", reqNo = "5678", workshopId = null, branchCode = null)

        useCase(search).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(search, repository.lastConstructionFilesSearch)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.thrownError = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
