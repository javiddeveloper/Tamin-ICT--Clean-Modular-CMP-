package com.tamin.taminhamrah.useCases.treatment

import app.cash.turbine.test
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.repository.FakeTreatmentRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetElectronicPrescriptionDetailUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var useCase: GetElectronicPrescriptionDetailUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreatmentRepository()
        useCase = GetElectronicPrescriptionDetailUseCase(repository)
    }

    @Test
    fun `invoke should return prescription details`() = runTest {
        val expected = listOf(
            ElectronicPrescriptionDetailDN(
                sumPriceItem = 1000L, ssoPayment = 800L, insurancePayment = 200L,
                serviceQuantity = 1, noteHeadEprescID = 100L, serverCode = "srvCode",
                serverName = "srvName", serviceName = "serviceName", drugInst = "instruction",
                registerDate = "14020101", drugInstruction = "drugInstruction",
                deliveredNo = 1, drugAmount = "10"
            )
        )
        repository.getElectronicPrescriptionDetailResult = expected

        useCase("noteHeadID", "nationalCode", "childCode", "flagSata", "type").test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase("noteHeadID", "nationalCode", "childCode", "flagSata", "type").test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }

    @Test
    fun `invoke should emit empty list when prescription has no details`() = runTest {
        repository.getElectronicPrescriptionDetailResult = emptyList()

        useCase("noteHeadID", "nationalCode", "childCode", "flagSata", "type").test {
            assertEquals(emptyList<ElectronicPrescriptionDetailDN>(), awaitItem())
            awaitComplete()
        }
    }
}
