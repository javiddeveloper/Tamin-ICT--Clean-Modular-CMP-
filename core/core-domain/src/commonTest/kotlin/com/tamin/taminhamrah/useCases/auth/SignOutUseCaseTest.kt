package com.tamin.taminhamrah.useCases.auth

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.FakeAuthRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SignOutUseCaseTest : BaseUseCaseTest() {

    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var signOutUseCase: SignOutUseCase

    @BeforeTest
    fun setupUseCase() {
        fakeAuthRepository = FakeAuthRepository()
        signOutUseCase = SignOutUseCase(fakeAuthRepository)
    }

    @Test
    fun `invoke should return success message when repository sign out is successful`() = runTest {
        // Given
        val expectedMessage = "Success"
        val token = "sample_token"
        fakeAuthRepository.signOutResult = expectedMessage

        // When & Then
        signOutUseCase(token).test {
            assertEquals(expectedMessage, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw exception when repository sign out fails`() = runTest {
        // Given
        val token = "sample_token"
        val expectedException = RuntimeException("Sign out failed")
        fakeAuthRepository.signOutError = expectedException

        // When & Then
        signOutUseCase(token).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
