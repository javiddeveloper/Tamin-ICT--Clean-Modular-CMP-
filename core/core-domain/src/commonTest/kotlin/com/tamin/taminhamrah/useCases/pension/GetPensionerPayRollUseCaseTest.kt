package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPensionerPayRollUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: GetPensionerPayRollUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = GetPensionerPayRollUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return payroll from repository`() = runTest {
        val expectedPayRoll = PayRollDN(
            id = 1,
            tprDesc = "Test Description",
            sumAmount = 1000000L,
            sumPay = 900000L,
            hisYear = "1402",
            hisMon = "01"
        )
        pensionRepository.payRollResult = expectedPayRoll

        useCase.invoke(emptyList()).test {
            val result = awaitItem()
            assertEquals(expectedPayRoll.id, result.id)
            assertEquals(expectedPayRoll.tprDesc, result.tprDesc)
            assertEquals(expectedPayRoll.sumAmount, result.sumAmount)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Get payroll failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        useCase.invoke(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
