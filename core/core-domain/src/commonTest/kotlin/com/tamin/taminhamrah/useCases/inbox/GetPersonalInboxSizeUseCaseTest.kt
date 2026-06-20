package com.tamin.taminhamrah.useCases.inbox

import app.cash.turbine.test
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDN
import com.tamin.taminhamrah.repository.inbox.FakePersonalInboxRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPersonalInboxSizeUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalInboxRepository
    private lateinit var useCase: GetPersonalInboxSizeUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalInboxRepository()
        useCase = GetPersonalInboxSizeUseCase(repository)
    }

    @Test
    fun `invoke should return inbox size from repository`() = runTest {
        val expectedSize = PersonalInboxSizeDN(usage = "0.53", total = "10")
        repository.inboxSizeResult = expectedSize

        useCase().test {
            val result = awaitItem()
            assertEquals(expectedSize, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
