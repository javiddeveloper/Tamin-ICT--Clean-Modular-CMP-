package com.tamin.taminhamrah.useCases.user

import app.cash.turbine.test
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.FakeUserRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserProfileUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeUserRepository
    private lateinit var useCase: GetUserProfileUseCase

    @BeforeTest
    fun setup() {
        repository = FakeUserRepository()
        useCase = GetUserProfileUseCase(repository)
    }

    @Test
    fun `invoke should return user profile`() = runTest {
        val expected = UserProfileDN(
            entityId = "1", login = "user", firstName = "John",
            lastName = "Doe", email = "john@example.com",
            nationalCode = "1234567890", mobile = "09123456789"
        )
        repository.userProfileResult = expected

        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
