package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAuthenticationCodeUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: GetAuthenticationCodeUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = GetAuthenticationCodeUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return authentication ticket from repository`() = runTest {
        val expectedTicket = AuthenticationTicketDN(mobileNumber = "09123456789")
        pensionRepository.authenticationTicketResult = expectedTicket

        useCase.invoke().test {
            val result = awaitItem()
            assertEquals("09123456789", result.mobileNumber)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val expectedException = RuntimeException("get ticket failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        useCase.invoke().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
