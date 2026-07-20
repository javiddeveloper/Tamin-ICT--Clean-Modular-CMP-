package com.tamin.taminhamrah.repository.agentRepository

import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.AgentResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.agent.PollingDataDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertIs

class FakeAgentRemoteDataSource : AgentRemoteDataSource {
    var checkChatAllowedResult: Result<ChatAllowedDN> = Result.success(ChatAllowedDN(true, null, "fake"))
    var sendPromptResult: Result<PollingResponseDTO> = Result.success(PollingResponseDTO(null, null, "req-1"))
    var trackRequestResult: Result<PollingResponseDTO> = Result.success(PollingResponseDTO("DONE", PollingDataDTO(null, emptyList()), "req-1"))

    override suspend fun sendPrompt(request: AgentRequestDTO): Result<PollingResponseDTO> = sendPromptResult
    override suspend fun trackRequest(requestId: String): Result<PollingResponseDTO> = trackRequestResult
    override suspend fun checkChatAllowed(): Result<ChatAllowedDN> = checkChatAllowedResult
}

class AgentRepositoryImplTest {

    @Test
    fun `sendPrompt returns Pending and then Done when successful`() = runTest {
        // Arrange
        val fakeDataSource = FakeAgentRemoteDataSource()
        fakeDataSource.sendPromptResult = Result.success(PollingResponseDTO(null, null, "req-1"))
        fakeDataSource.trackRequestResult = Result.success(PollingResponseDTO("DONE", PollingDataDTO("session-1", emptyList()), "req-1"))
        val repository = AgentRepositoryImpl(fakeDataSource)
        
        // Act
        val flow = repository.sendPrompt(AgentRequest("test", null, "token"))
        val states = flow.toList()

        // Assert
        assertEquals(2, states.size)
        assertIs<AgentPollingState.Pending>(states[0])
        assertEquals("req-1", (states[0] as AgentPollingState.Pending).requestId)
        
        assertIs<AgentPollingState.Done>(states[1])
        assertEquals("session-1", (states[1] as AgentPollingState.Done).response.sessionId)
    }

    @Test
    fun `sendPrompt returns Failed when sendPrompt API fails`() = runTest {
        // Arrange
        val fakeDataSource = FakeAgentRemoteDataSource()
        fakeDataSource.sendPromptResult = Result.failure(RuntimeException("Network error"))
        val repository = AgentRepositoryImpl(fakeDataSource)
        
        // Act
        val flow = repository.sendPrompt(AgentRequest("test", null, "token"))
        val states = flow.toList()

        // Assert
        assertEquals(1, states.size)
        assertIs<AgentPollingState.Failed>(states[0])
    }

    @Test
    fun `checkChatAllowed returns data source result`() = runTest {
        // Arrange
        val fakeDataSource = FakeAgentRemoteDataSource()
        val expected = ChatAllowedDN(true, null, "token")
        fakeDataSource.checkChatAllowedResult = Result.success(expected)
        val repository = AgentRepositoryImpl(fakeDataSource)
        
        // Act
        val result = repository.checkChatAllowed()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }
}
