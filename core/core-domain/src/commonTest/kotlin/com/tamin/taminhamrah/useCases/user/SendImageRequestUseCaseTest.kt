package com.tamin.taminhamrah.useCases.user

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.FakeUserRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SendImageRequestUseCaseTest : BaseUseCaseTest() {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var useCase: SendImageRequestUseCase

    @BeforeTest
    fun setup() {
        userRepository = FakeUserRepository()
        useCase = SendImageRequestUseCase(userRepository)
    }

    @Test
    fun `invoke should return result from repository`() = runTest {
        val expectedResult = "Success"
        userRepository.sendImageResult = expectedResult

        val branchCode = "123"
        val serialId = "987654321"

        useCase.invoke(branchCode, serialId).test {
            val result = awaitItem()
            assertEquals(expectedResult, result)
            awaitComplete()
        }
    }
}
