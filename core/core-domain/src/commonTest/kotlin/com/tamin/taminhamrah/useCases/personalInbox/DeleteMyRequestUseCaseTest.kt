package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.repository.personalInbox.FakePersonalInboxRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeleteMyRequestUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalInboxRepository
    private lateinit var useCase: DeleteMyRequestUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalInboxRepository()
        useCase = DeleteMyRequestUseCase(repository)
    }

    @Test
    fun `invoke should call deleteMyRequest on repository`() = runTest {
        val requestId = "req-123"

        useCase(requestId)

        assertEquals(requestId, repository.deletedRequestId)
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val requestId = "req-123"
        val expectedException = RuntimeException("Delete failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        val actualException = assertFailsWith<RuntimeException> {
            useCase(requestId)
        }
        assertEquals(expectedException.message, actualException.message)
    }
}
