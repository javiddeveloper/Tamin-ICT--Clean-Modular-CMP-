package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDN
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetElectronicPrescriptionPriceUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetElectronicPrescriptionPriceUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetElectronicPrescriptionPriceUseCase(repository)
    }

    @Test
    fun `invoke should return prescription price`() = runTest {
        val expected = listOf(
            ElectronicPrescriptionPriceDN(
                headInsuPayment = 1000L, headSsoPayment = 800L,
                noteHeadEprescID = 100L, requestPrice = 1800L
            )
        )
        repository.getElectronicPrescriptionPriceResult = expected

        useCase("noteHeadID", "nationalCode").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("noteHeadID", "nationalCode").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit empty list when no price is available`() = runTest {
        repository.getElectronicPrescriptionPriceResult = emptyList()

        useCase("noteHeadID", "nationalCode").test {
            assertEquals(emptyList<ElectronicPrescriptionPriceDN>(), awaitItem())
            awaitComplete()
        }
    }
}
