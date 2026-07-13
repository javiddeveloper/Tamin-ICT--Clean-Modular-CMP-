package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticationAndGetPersonalInfoUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: AuthenticationAndGetPersonalInfoUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = AuthenticationAndGetPersonalInfoUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return personal info from repository`() = runTest {
        val expectedData = RetirementPersonalDN(
            branch = "1",
            branchName = "Branch 1",
            insuranceId = "123",
            mobileNumber = "0912",
            organizationId = "ORG",
            personal = null,
            provinceName = "Tehran",
            work = null,
            strAge = "30",
            verificationResult = "OK"
        )
        pensionRepository.authenticationAndGetPersonalInfoResult = expectedData

        useCase.invoke(123456L).test {
            val result = awaitItem()
            assertEquals("123", result.insuranceId)
            assertEquals("Tehran", result.provinceName)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val expectedException = RuntimeException("error")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        useCase.invoke(123456L).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
