package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.model.treatment.TreatmentCostDN
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTreatmentCostsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetTreatmentCostsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetTreatmentCostsUseCase(repository)
    }

    @Test
    fun `invoke should return treatment costs list`() = runTest {
        val expected = listOf(
            TreatmentCostDN(
                accountNumber = "123", bimeCode = "456", datePaz = "14020101",
                famil = "Doe", healthcenterName = "Hospital", mainNational = "111",
                maliCode = "222", name = "John", nameAsli = "John", nameFamil = "Doe",
                noPazir = "789", payNatCode = "333", payOtherService = "0",
                payPrice = "1000", payService = "service", payStatus = "1",
                payType = "Type", province = "Tehran", rahgiriCode = "555",
                releaseDate = "14020102", repId = 1, serviceDate = "14020101",
                status = "Status", statusDesc = "Description", payStatusDesc = "PayDescription",
                returnReason = "None"
            )
        )
        repository.getTreatmentCostsResult = expected

        useCase(emptyList()).test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit empty list when no treatment costs exist`() = runTest {
        repository.getTreatmentCostsResult = emptyList()

        useCase(emptyList()).test {
            assertEquals(emptyList<TreatmentCostDN>(), awaitItem())
            awaitComplete()
        }
    }
}
