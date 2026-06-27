package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserAgeUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: GetUserAgeUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = GetUserAgeUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return age from repository`() = runTest {
        val expectedAge = AgeDN(age = "30", birthDate = "1370/01/01")
        pensionRepository.userAgeResult = expectedAge

        useCase.invoke(emptyList()).test {
            val result = awaitItem()
            assertEquals("30", result.age)
            assertEquals("1370/01/01", result.birthDate)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val expectedException = RuntimeException("get age failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        useCase.invoke(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
