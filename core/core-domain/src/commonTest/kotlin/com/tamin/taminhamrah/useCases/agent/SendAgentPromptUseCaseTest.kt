package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakePromptAgentRepository(
    private val expectedFlow: Flow<AgentPollingState>
) : AgentRepository {
    override fun sendPrompt(request: AgentRequest): Flow<AgentPollingState> = expectedFlow
    override suspend fun cancelRequest(requestId: String): Result<Unit> = Result.success(Unit)
    override suspend fun checkChatAllowed(): Result<ChatAllowedDN> = Result.success(ChatAllowedDN(true, null, null))
}

class SendAgentPromptUseCaseTest {

    @Test
    fun `invoke passes request to repository and returns Flow`() = runTest {
        // Arrange
        val request = AgentRequest(prompt = "Hello AI", chatToken = "token", sessionId = null)
        val expectedStates = listOf(
            AgentPollingState.Pending("req1", 5),
            AgentPollingState.Failed("req1", "Timeout Error")
        )
        val fakeRepo = FakePromptAgentRepository(flowOf(*expectedStates.toTypedArray()))
        val useCase = SendAgentPromptUseCase(fakeRepo)

        // Act
        val resultFlow = useCase(request)
        val results = resultFlow.toList()

        // Assert
        assertEquals(2, results.size)
        assertEquals(expectedStates[0], results[0])
        assertEquals(expectedStates[1], results[1])
    }
}
