package com.tamin.taminhamrah.repository.agentRepository

import com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.AgentResponseDTO
import com.tamin.taminhamrah.model.agent.CancelResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.agent.PollingDataDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertIs

// ─── Fake Data Source ─────────────────────────────────────────────────────────

class FakeAgentRemoteDataSource : AgentRemoteDataSource {

    // Configurable results
    var sendServicePromptResult: PollingResponseDTO = PollingResponseDTO(
        data = PollingDataDTO(id = "req-1", eta = 1)
    )
    var sendLawPromptResult: PollingResponseDTO = PollingResponseDTO(
        data = PollingDataDTO(id = "req-1", eta = 1)
    )
    var trackRequestResult: PollingResponseDTO = PollingResponseDTO(
        data = PollingDataDTO(
            id = "req-1",
            status = "DONE",
            result = AgentResponseDTO(sessionId = "session-1", entities = emptyList())
        )
    )
    var checkChatAllowedResult: ChatAllowedDTO = ChatAllowedDTO()
    var cancelRequestResult: CancelResponseDTO = CancelResponseDTO()

    override suspend fun sendServicePrompt(request: AgentRequestDTO): PollingResponseDTO =
        sendServicePromptResult

    override suspend fun sendLawPrompt(request: AgentRequestDTO): PollingResponseDTO =
        sendLawPromptResult

    override suspend fun trackRequest(requestId: String): PollingResponseDTO =
        trackRequestResult

    override suspend fun checkChatAllowed(): ChatAllowedDTO =
        checkChatAllowedResult

    override suspend fun cancelRequest(requestId: String): CancelResponseDTO =
        cancelRequestResult
}

// ─── Tests ────────────────────────────────────────────────────────────────────

class AgentRepositoryImplTest {

    @Test
    fun `sendPrompt emits Pending then Done when track returns DONE`() = runTest {
        val fake = FakeAgentRemoteDataSource()
        fake.sendServicePromptResult = PollingResponseDTO(
            data = PollingDataDTO(id = "req-1", eta = 0)
        )
        fake.trackRequestResult = PollingResponseDTO(
            data = PollingDataDTO(
                id = "req-1",
                status = "DONE",
                result = AgentResponseDTO(sessionId = "session-1", entities = emptyList())
            )
        )
        val repository = AgentRepositoryImpl(fake)

        val states = repository.sendPrompt(AgentRequest("test", chatToken = "token")).toList()

        assertEquals(2, states.size)
        assertIs<AgentPollingState.Pending>(states[0])
        assertEquals("req-1", (states[0] as AgentPollingState.Pending).requestId)

        assertIs<AgentPollingState.Done>(states[1])
        assertEquals("session-1", (states[1] as AgentPollingState.Done).response.sessionId)
    }

    @Test
    fun `sendPrompt emits Failed when sendServicePrompt throws`() = runTest {
        val fake = FakeAgentRemoteDataSource()
        // Override to throw
        val failingFake = object : AgentRemoteDataSource {
            override suspend fun sendServicePrompt(request: AgentRequestDTO): PollingResponseDTO =
                throw RuntimeException("Network error")
            override suspend fun sendLawPrompt(request: AgentRequestDTO): PollingResponseDTO =
                throw RuntimeException("Network error")
            override suspend fun trackRequest(requestId: String): PollingResponseDTO =
                PollingResponseDTO()
            override suspend fun checkChatAllowed(): ChatAllowedDTO = ChatAllowedDTO()
            override suspend fun cancelRequest(requestId: String): CancelResponseDTO = CancelResponseDTO()
        }
        val repository = AgentRepositoryImpl(failingFake)

        val states = repository.sendPrompt(AgentRequest("test", chatToken = "token")).toList()

        assertEquals(1, states.size)
        assertIs<AgentPollingState.Failed>(states[0])
    }

    @Test
    fun `sendPrompt emits Failed when track returns FAILED`() = runTest {
        val fake = FakeAgentRemoteDataSource()
        fake.trackRequestResult = PollingResponseDTO(
            data = PollingDataDTO(
                id = "req-1",
                status = "FAILED",
                message = "Something went wrong"
            )
        )
        val repository = AgentRepositoryImpl(fake)

        val states = repository.sendPrompt(AgentRequest("test", chatToken = "token")).toList()

        assertEquals(2, states.size)
        assertIs<AgentPollingState.Pending>(states[0])
        assertIs<AgentPollingState.Failed>(states[1])
    }

    @Test
    fun `checkChatAllowed returns parsed domain model`() = runTest {
        val fake = FakeAgentRemoteDataSource()
        fake.checkChatAllowedResult = ChatAllowedDTO(
            data = com.tamin.taminhamrah.model.agent.ChatAllowedDataDTO(
                canStartChat = true,
                chatToken = "token-xyz",
                errorMessage = null
            )
        )
        val repository = AgentRepositoryImpl(fake)

        val result = repository.checkChatAllowed()

        assertTrue(result.isSuccess)
        assertEquals("token-xyz", result.getOrNull()?.chatToken)
        assertEquals(true, result.getOrNull()?.canStartChat)
    }
}
