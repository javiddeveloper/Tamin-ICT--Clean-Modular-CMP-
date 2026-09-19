package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentAccessDN
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.FakeAgentAccessStore
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

    private val allowed = ChatAllowedDN(canStartChat = true, chatToken = "chat-abc", errorMessage = null, canSendVoice = true)
    private val refused = ChatAllowedDN(canStartChat = false, chatToken = null, errorMessage = "not allowed")

    private fun useCase(
        result: Result<ChatAllowedDN>,
        store: FakeAgentAccessStore = FakeAgentAccessStore(),
        tokens: FakeTokenStoreManager = FakeTokenStoreManager(),
    ) = CheckChatAllowedUseCase(FakeChatAgentRepository(result), tokens, store)

    @Test
    fun `a granted answer is cached with its token and voice permission`() = runTest {
        val store = FakeAgentAccessStore()
        val result = useCase(Result.success(allowed), store)()

        assertEquals(allowed, result.getOrNull())
        assertEquals(
            AgentAccessDN(canStartChat = true, canSendVoice = true, chatToken = "chat-abc", errorMessage = null),
            store.access.value,
        )
    }

    @Test
    fun `a refusal is cached so the entry point hides, with the server reason`() = runTest {
        val store = FakeAgentAccessStore(
            AgentAccessDN(canStartChat = true, canSendVoice = true, chatToken = "old", errorMessage = null)
        )
        useCase(Result.success(refused), store)()

        assertEquals(
            AgentAccessDN(canStartChat = false, canSendVoice = false, chatToken = null, errorMessage = "not allowed"),
            store.access.value,
        )
    }

    @Test
    fun `the old chat token is dropped before asking`() = runTest {
        val store = FakeAgentAccessStore(
            AgentAccessDN(canStartChat = true, canSendVoice = false, chatToken = "stale", errorMessage = null)
        )
        useCase(Result.success(allowed), store)()
        assertEquals(listOf<String?>("stale"), store.tokensSeenAtClear)
    }

    @Test
    fun `a transport failure keeps the last known permission`() = runTest {
        val previous = AgentAccessDN(canStartChat = true, canSendVoice = true, chatToken = "stale", errorMessage = null)
        val store = FakeAgentAccessStore(previous)
        val exception = RuntimeException("Network Error")

        val result = useCase(Result.failure(exception), store)()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        // Permission survives; only the token the server may have retired is gone.
        assertEquals(previous.copy(chatToken = null), store.access.value)
    }

    @Test
    fun `a granted chat token is mirrored into the developer options slot`() = runTest {
        val tokens = FakeTokenStoreManager()
        useCase(Result.success(allowed), tokens = tokens)()
        assertEquals("chat-abc", tokens.getToken(TokenSlot.AGENT))
    }

    @Test
    fun `a refused check leaves the developer options slot alone`() = runTest {
        val tokens = FakeTokenStoreManager()
        tokens.saveToken(TokenSlot.AGENT, "previous-token")
        useCase(Result.success(refused), tokens = tokens)()
        assertEquals("previous-token", tokens.getToken(TokenSlot.AGENT))
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
