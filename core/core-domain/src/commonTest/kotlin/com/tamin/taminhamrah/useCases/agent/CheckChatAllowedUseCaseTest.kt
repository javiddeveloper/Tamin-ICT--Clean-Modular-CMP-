package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeChatAgentRepository(
    private val expectedResult: Result<ChatAllowedDN>
) : AgentRepository {
    override fun sendPrompt(request: AgentRequest): Flow<AgentPollingState> = flowOf()
    override suspend fun cancelRequest(requestId: String): Result<Unit> = Result.success(Unit)
    override suspend fun checkChatAllowed(): Result<ChatAllowedDN> = expectedResult
}

class CheckChatAllowedUseCaseTest {

    @Test
    fun `invoke returns success result from repository`() = runTest {
        // Arrange
        val expectedData = ChatAllowedDN(isAllowed = true, errorMessage = null, chatToken = "token123")
        val fakeRepo = FakeChatAgentRepository(Result.success(expectedData))
        val useCase = CheckChatAllowedUseCase(fakeRepo)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedData, result.getOrNull())
    }

    @Test
    fun `invoke returns error result from repository`() = runTest {
        // Arrange
        val exception = RuntimeException("Network Error")
        val fakeRepo = FakeChatAgentRepository(Result.failure(exception))
        val useCase = CheckChatAllowedUseCase(fakeRepo)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
