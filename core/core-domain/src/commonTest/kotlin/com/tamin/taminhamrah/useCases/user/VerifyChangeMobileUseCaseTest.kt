package com.tamin.taminhamrah.useCases.user

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.FakeUserRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class VerifyChangeMobileUseCaseTest : BaseUseCaseTest() {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var useCase: VerifyChangeMobileUseCase

    @BeforeTest
    fun setup() {
        userRepository = FakeUserRepository()
        useCase = VerifyChangeMobileUseCase(userRepository)
    }

    @Test
    fun `invoke should return result from repository`() = runTest {
        val expectedResult = "Verified"
        userRepository.verifyChangeMobileResult = expectedResult

        val mobile = "09123456789"
        val otp = "12345"
        val otpHashCode = "hash123"

        useCase.invoke(mobile, otp, otpHashCode).test {
            val result = awaitItem()
            assertEquals(expectedResult, result)
            awaitComplete()
        }
    }
}
