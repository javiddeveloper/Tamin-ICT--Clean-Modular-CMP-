package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentAccessDN
import com.tamin.taminhamrah.model.agent.AgentPersonalInfoDN
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.agent.ChatTokenExpiredException
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.FakeAgentAccessStore
import com.tamin.taminhamrah.repository.FakeTokenStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Rejects tokens listed in [expiredTokens]; records every request it was sent. */
class FakePromptAgentRepository(
    private val states: List<AgentPollingState> = emptyList(),
    private val expiredTokens: Set<String?> = emptySet(),
    private val chatAllowed: Result<ChatAllowedDN> =
        Result.success(ChatAllowedDN(canStartChat = true, chatToken = "fresh", errorMessage = null)),
) : AgentRepository {
    val sent = mutableListOf<AgentRequest>()

    override fun sendPrompt(request: AgentRequest): Flow<AgentPollingState> = flow {
        sent.add(request)
        if (request.chatToken in expiredTokens) throw ChatTokenExpiredException()
        states.forEach { emit(it) }
    }

    override suspend fun cancelRequest(requestId: String): Result<Unit> = Result.success(Unit)
    override suspend fun checkChatAllowed(): Result<ChatAllowedDN> = chatAllowed
}

class SendAgentPromptUseCaseTest {

    private val done = listOf(AgentPollingState.Pending("req1", 5), AgentPollingState.Failed("Timeout Error"))

    private fun useCase(
        repository: FakePromptAgentRepository,
        store: FakeAgentAccessStore,
        tokens: FakeTokenStoreManager = FakeTokenStoreManager(),
        personalInfo: AgentPersonalInfoDN? = null,
    ) = SendAgentPromptUseCase(
        agentRepository = repository,
        checkChatAllowed = CheckChatAllowedUseCase(repository, tokens, store),
        agentAccessStore = store,
        tokenStoreManager = tokens,
        getPersonalInfo = { personalInfo },
    )

    private fun access(token: String?) =
        AgentAccessDN(canStartChat = true, canSendVoice = false, chatToken = token, errorMessage = null)

    @Test
    fun `states from the repository pass through unchanged`() = runTest {
        val repository = FakePromptAgentRepository(states = done)
        val results = useCase(repository, FakeAgentAccessStore(access("token")))(AgentRequest(prompt = "Hello AI")).toList()
        assertEquals(done, results)
    }

    @Test
    fun `the newest stored token is sent, not the one the screen had`() = runTest {
        val repository = FakePromptAgentRepository(states = done)
        useCase(repository, FakeAgentAccessStore(access("stored")))(AgentRequest(prompt = "hi", chatToken = "screen")).toList()
        assertEquals("stored", repository.sent.single().chatToken)
    }

    @Test
    fun `an expired token is refreshed once and the prompt is resent with the new one`() = runTest {
        val repository = FakePromptAgentRepository(states = done, expiredTokens = setOf("stale"))
        val store = FakeAgentAccessStore(access("stale"))

        val results = useCase(repository, store)(AgentRequest(prompt = "hi")).toList()

        assertEquals(listOf("stale", "fresh"), repository.sent.map { it.chatToken })
        assertEquals(done, results)
        assertEquals("fresh", store.access.value?.chatToken)
    }

    @Test
    fun `a refusal while refreshing ends the prompt with the server reason`() = runTest {
        val repository = FakePromptAgentRepository(
            expiredTokens = setOf("stale"),
            chatAllowed = Result.success(ChatAllowedDN(canStartChat = false, chatToken = null, errorMessage = "دسترسی ندارید")),
        )
        val store = FakeAgentAccessStore(access("stale"))

        val results = useCase(repository, store)(AgentRequest(prompt = "hi")).toList()

        assertEquals(listOf(AgentPollingState.Failed("دسترسی ندارید")), results)
        assertEquals(false, store.access.value?.canStartChat)
    }

    @Test
    fun `a token rejected again after refreshing fails instead of looping`() = runTest {
        val repository = FakePromptAgentRepository(expiredTokens = setOf("stale", "fresh"))
        val results = useCase(repository, FakeAgentAccessStore(access("stale")))(AgentRequest(prompt = "hi")).toList()

        assertEquals(2, repository.sent.size)
        assertEquals(listOf(AgentPollingState.Failed(null)), results)
    }

    @Test
    fun `user type follows the session - anonymous without a login, the stored type with one`() = runTest {
        val repository = FakePromptAgentRepository(states = done)
        val tokens = FakeTokenStoreManager()
        val send = useCase(repository, FakeAgentAccessStore(access("t")), tokens)

        send(AgentRequest(prompt = "a")).toList()
        tokens.saveToken("user-token")
        tokens.saveUserType("PENSIONER")
        send(AgentRequest(prompt = "b")).toList()
        tokens.saveUserType("TEMPORARY")
        send(AgentRequest(prompt = "c")).toList()

        assertEquals(listOf("ANONYMOUS", "PENSIONER", "temporary"), repository.sent.map { it.userType })
    }

    @Test
    fun `every prompt says who is asking, and the refreshed resend does too`() = runTest {
        val me = AgentPersonalInfoDN("0012345678", "555", "علی", "رضایی")
        val repository = FakePromptAgentRepository(states = done, expiredTokens = setOf("stale"))

        useCase(repository, FakeAgentAccessStore(access("stale")), personalInfo = me)(AgentRequest(prompt = "hi")).toList()

        assertEquals(listOf(me, me), repository.sent.map { it.personalInfo })
    }

    @Test
    fun `conversation context from the screen is sent unchanged`() = runTest {
        val repository = FakePromptAgentRepository(states = done)
        val request = AgentRequest(prompt = "hi", sessionId = "category_1", lastEntity = "fish", state = "{\"v\":1}", history = "[]")

        useCase(repository, FakeAgentAccessStore(access("t")))(request).toList()

        val sent = repository.sent.single()
        assertEquals(listOf("category_1", "fish", "{\"v\":1}", "[]"), listOf(sent.sessionId, sent.lastEntity, sent.state, sent.history))
    }

    @Test
    fun `nothing is sent twice when the token is fine`() = runTest {
        val repository = FakePromptAgentRepository(states = done)
        useCase(repository, FakeAgentAccessStore(access("ok")))(AgentRequest(prompt = "hi")).toList()
        assertEquals(1, repository.sent.size)
    }
}
