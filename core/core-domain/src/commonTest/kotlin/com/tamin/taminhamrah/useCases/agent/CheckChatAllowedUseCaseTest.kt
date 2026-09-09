package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.FakeTokenStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
        val expectedData = ChatAllowedDN(canStartChat = true, chatToken = "token123", errorMessage = null)
        val fakeRepo = FakeChatAgentRepository(Result.success(expectedData))
        val useCase = CheckChatAllowedUseCase(fakeRepo, FakeTokenStoreManager())

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
        val useCase = CheckChatAllowedUseCase(fakeRepo, FakeTokenStoreManager())

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `a granted chat token is kept in the agent slot`() = runTest {
        // So the token screen shows the chat token actually issued, not only whichever one that
        // screen last fetched for itself.
        val tokenStore = FakeTokenStoreManager()
        val useCase = CheckChatAllowedUseCase(
            FakeChatAgentRepository(
                Result.success(ChatAllowedDN(canStartChat = true, chatToken = "chat-abc", errorMessage = null))
            ),
            tokenStore,
        )

        useCase()

        assertEquals("chat-abc", tokenStore.getToken(TokenSlot.AGENT))
    }

    @Test
    fun `a refused check leaves the stored agent token alone`() = runTest {
        // A "not allowed" answer carries no token; overwriting with null would throw away the last
        // good one the screen may still be using.
        val tokenStore = FakeTokenStoreManager()
        tokenStore.saveToken(TokenSlot.AGENT, "previous-token")
        val useCase = CheckChatAllowedUseCase(
            FakeChatAgentRepository(
                Result.success(
                    ChatAllowedDN(canStartChat = false, chatToken = null, errorMessage = "not allowed")
                )
            ),
            tokenStore,
        )

        useCase()

        assertEquals("previous-token", tokenStore.getToken(TokenSlot.AGENT))
    }

    @Test
    fun `the agent slot is never what the app authenticates with`() = runTest {
        // AGENT is a body field on the assistant's own calls, not a bearer — activating it would
        // send a chat token as the app's Authorization header.
        val tokenStore = FakeTokenStoreManager()
        tokenStore.saveToken(TokenSlot.USER, "user-token")
        tokenStore.saveToken(TokenSlot.AGENT, "chat-abc")

        tokenStore.setActiveSlot(TokenSlot.AGENT)

        assertEquals(TokenSlot.USER, tokenStore.getActiveSlot())
        assertEquals("user-token", tokenStore.getToken())
        assertNull(tokenStore.getToken(TokenSlot.BACK_TO_BACK))
    }
}
