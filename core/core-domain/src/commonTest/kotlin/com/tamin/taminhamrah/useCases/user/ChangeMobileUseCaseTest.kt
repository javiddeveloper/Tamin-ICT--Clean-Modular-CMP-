package com.tamin.taminhamrah.useCases.user

import app.cash.turbine.test
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.repository.FakeUserRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ChangeMobileUseCaseTest : BaseUseCaseTest() {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var useCase: ChangeMobileUseCase

    @BeforeTest
    fun setup() {
        userRepository = FakeUserRepository()
        useCase = ChangeMobileUseCase(userRepository)
    }

    @Test
    fun `invoke should return result from repository`() = runTest {
        val expectedResponse = EditMobileResponseDN(
            traceId = "test_trace_id",
            data = null
        )
        userRepository.changeMobileResult = expectedResponse

        val mobileNumber = "09123456789"

        useCase.invoke(mobileNumber).test {
            val result = awaitItem()
            assertEquals(expectedResponse, result)
            awaitComplete()
        }
    }
}
