package com.tamin.taminhamrah.feature.agent.ui

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.agent.service.AgentActionDispatcher
import com.tamin.taminhamrah.feature.agent.service.FakeFeatureManager
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentIntent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentUiState
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.agent.SendAgentPromptUseCase
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentProcessingState
import com.tamin.taminhamrah.feature.agent.ui.contract.InputMode
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent

class FakeVoiceRecorder : com.tamin.taminhamrah.feature.agent.audio.VoiceRecorder {
    override val amplitude = MutableStateFlow(0)
    override val isRecording = MutableStateFlow(false)
    override fun start(filePath: String) { isRecording.value = true }
    override fun stop() { isRecording.value = false }
    override fun newRecordingPath(): String = "/tmp/fake_voice.m4a"
}

class FakeVoicePlayer : com.tamin.taminhamrah.feature.agent.audio.VoicePlayer {
    override val positionMs = MutableStateFlow(0)
    override val durationMs = MutableStateFlow(0)
    override val isPlaying = MutableStateFlow(false)
    override fun load(filePath: String, onReady: () -> Unit, onComplete: () -> Unit, onError: (String) -> Unit) { onReady() }
    override fun playPause() { isPlaying.value = !isPlaying.value }
    override fun seekTo(ms: Int) { positionMs.value = ms }
    override fun stop() { isPlaying.value = false; positionMs.value = 0 }
    override fun release() {}
}

/** In-memory stand-in for the Room-backed conversation cache. */
class FakeAgentChatCacheRepository : com.tamin.taminhamrah.repository.AgentChatCacheRepository {
    private val sessions = mutableMapOf<String, com.tamin.taminhamrah.model.agent.AgentSessionDN>()
    val messages = mutableListOf<com.tamin.taminhamrah.model.agent.AgentCachedMessageDN>()

    override suspend fun createSession(session: com.tamin.taminhamrah.model.agent.AgentSessionDN) {
        sessions[session.id] = session
    }

    override suspend fun getSession(sessionId: String) = sessions[sessionId]

    override suspend fun getLastSession(userNationalCode: String) =
        sessions.values.filter { it.userNationalCode == userNationalCode }
            .maxByOrNull { it.lastMessageAt }

    override fun observeSessions(userNationalCode: String) =
        kotlinx.coroutines.flow.flowOf(sessions.values.toList())

    override suspend fun getSessions(userNationalCode: String) =
        sessions.values.filter { it.userNationalCode == userNationalCode }
            .sortedByDescending { it.lastMessageAt }

    override suspend fun deleteSession(sessionId: String) {
        sessions.remove(sessionId)
        messages.removeAll { it.sessionId == sessionId }
    }

    override suspend fun updateSessionTitle(sessionId: String, title: String) {
        sessions[sessionId] = sessions[sessionId]?.copy(title = title) ?: return
    }

    override suspend fun updateSessionLastEntity(sessionId: String, lastEntity: String?) {
        sessions[sessionId] = sessions[sessionId]?.copy(lastEntity = lastEntity) ?: return
    }

    override suspend fun addMessage(message: com.tamin.taminhamrah.model.agent.AgentCachedMessageDN) {
        messages.add(message)
    }

    override fun observeMessages(sessionId: String) =
        kotlinx.coroutines.flow.flowOf(messages.filter { it.sessionId == sessionId })

    override suspend fun getMessages(sessionId: String) =
        messages.filter { it.sessionId == sessionId }.sortedBy { it.messageOrder }

    override suspend fun countMessages(sessionId: String) =
        messages.count { it.sessionId == sessionId }

    override suspend fun updateMessageStatus(
        messageId: String,
        status: com.tamin.taminhamrah.model.agent.CachedStatus
    ) = Unit

    override suspend fun deleteMessages(sessionId: String) {
        messages.removeAll { it.sessionId == sessionId }
    }

    override suspend fun deletePendingAgentMessages(sessionId: String) {
        val lastUserOrder = messages
            .filter { it.sessionId == sessionId && it.sender == com.tamin.taminhamrah.model.agent.CachedSender.USER }
            .maxOfOrNull { it.messageOrder } ?: -1
        messages.removeAll {
            it.sessionId == sessionId &&
                it.sender == com.tamin.taminhamrah.model.agent.CachedSender.AGENT &&
                it.messageOrder > lastUserOrder
        }
    }

    override suspend fun nextMessageOrder(sessionId: String) =
        (messages.filter { it.sessionId == sessionId }.maxOfOrNull { it.messageOrder } ?: -1) + 1
}

class FakeAgentRepository : AgentRepository {
    var checkChatAllowedResult: Result<ChatAllowedDN> = Result.success(ChatAllowedDN(true, null, "fake-token"))
    var sendPromptFlow: kotlinx.coroutines.flow.Flow<AgentPollingState> = flowOf()
    var cancelRequestCalled = false

    override fun sendPrompt(request: AgentRequest): kotlinx.coroutines.flow.Flow<AgentPollingState> {
        return sendPromptFlow
    }

    override suspend fun cancelRequest(requestId: String): Result<Unit> {
        cancelRequestCalled = true
        return Result.success(Unit)
    }

    override suspend fun checkChatAllowed(): Result<ChatAllowedDN> {
        return checkChatAllowedResult
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AgentViewModelTest {

    private lateinit var viewModel: AgentViewModel
    private lateinit var fakeFeatureManager: FakeFeatureManager
    private lateinit var actionDispatcher: AgentActionDispatcher
    private lateinit var fakeAgentRepository: FakeAgentRepository
    private lateinit var checkChatAllowedUseCase: CheckChatAllowedUseCase
    private lateinit var sendAgentPromptUseCase: SendAgentPromptUseCase
    private lateinit var fakeCacheRepository: FakeAgentChatCacheRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        
        fakeFeatureManager = FakeFeatureManager()
        val registry = AgentServiceRegistry(emptyList())
        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        actionDispatcher = AgentActionDispatcher(registry, fakeFeatureManager, json)
        
        fakeAgentRepository = FakeAgentRepository()
        fakeCacheRepository = FakeAgentChatCacheRepository()
        checkChatAllowedUseCase = CheckChatAllowedUseCase(fakeAgentRepository)
        sendAgentPromptUseCase = SendAgentPromptUseCase(fakeAgentRepository)
        
        // Start Koin if required by BaseViewModel, but BaseViewModel usually doesn't need it if dependencies are injected
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AgentViewModel {
        val cache = fakeCacheRepository
        return AgentViewModel(
            sendAgentPromptUseCase,
            checkChatAllowedUseCase,
            actionDispatcher,
            fakeFeatureManager,
            FakeVoiceRecorder(),
            FakeVoicePlayer(),
            { TEST_NATIONAL_CODE },
            com.tamin.taminhamrah.useCases.agent.PruneEmptyAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.GetAgentSessionsUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.GetAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.DeleteAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.StartAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.SaveCachedMessageUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.GetCachedMessagesUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.DeletePendingAgentMessagesUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.UpdateAgentSessionUseCase(cache)
        )
    }

    @Test
    fun `initial state has isCheckingPermission true`() {
        viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertTrue(state.isCheckingPermission)
        assertFalse(state.isGenerating)
    }

    @Test
    fun `CheckPermission intent updates state to allowed when repository returns success`() = runTest {
        viewModel = createViewModel()
        fakeAgentRepository.checkChatAllowedResult = Result.success(ChatAllowedDN(true, null, "test-token"))
        
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertFalse(state.isCheckingPermission)
        assertFalse(state.isNotAllowed)
        assertEquals("test-token", state.chatToken)
    }

    @Test
    fun `CheckPermission intent updates state to not allowed when repository returns false`() = runTest {
        viewModel = createViewModel()
        fakeAgentRepository.checkChatAllowedResult = Result.success(ChatAllowedDN(false, "You are blocked", null))
        
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertFalse(state.isCheckingPermission)
        assertTrue(state.isNotAllowed)
        assertEquals("You are blocked", state.notAllowedMessage)
    }

    @Test
    fun `ChangeInputMode intent updates input mode`() = runTest {
        viewModel = createViewModel()

        viewModel.sendIntent(AgentIntent.ChangeInputMode(InputMode.Voice))
        runCurrent()

        assertEquals(InputMode.Voice, viewModel.uiState.value.inputMode)
    }

    // ─── Typing animation ─────────────────────────────────────────────────────

    @Test
    fun `a finished reveal clears the typing flag so recycling cannot replay it`() = runTest {
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        // Put an animating agent bubble on screen.
        fakeAgentRepository.sendPromptFlow = flowOf(
            AgentPollingState.Done(
                com.tamin.taminhamrah.model.agent.AgentResponseDN(
                    sessionId = "s",
                    lastEntity = null,
                    entities = listOf(
                        com.tamin.taminhamrah.model.agent.AiEntityDN(
                            action = com.tamin.taminhamrah.model.agent.AgentActionKey.GENERAL_RESPONSE,
                            stepNumber = 1,
                            payload = null,
                            data = null,
                            message = "پاسخ",
                            itemType = null
                        )
                    )
                )
            )
        )
        viewModel.sendIntent(AgentIntent.SendTextPrompt("سلام"))
        advanceUntilIdle()

        val animating = viewModel.uiState.value.chatItems.firstOrNull { it.isTypingAnimating }
        assertNotNull(animating, "expected an animating agent bubble")

        viewModel.sendIntent(AgentIntent.OnTypingFinished(animating.id))
        advanceUntilIdle()

        val after = viewModel.uiState.value.chatItems.first { it.id == animating.id }
        assertFalse(
            after.isTypingAnimating,
            "once revealed, the bubble must render instantly when the row is recycled"
        )
    }

    // ─── Conversation cache ───────────────────────────────────────────────────

    @Test
    fun `sending a prompt stores the user message in the cache`() = runTest {
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()

        viewModel.sendIntent(AgentIntent.SendTextPrompt("سابقه‌ام را نشان بده"))
        advanceUntilIdle()

        val stored = fakeCacheRepository.messages
        assertTrue(stored.isNotEmpty(), "the user message should have been cached")
        val userRow = stored.first()
        assertEquals(
            com.tamin.taminhamrah.model.agent.CachedSender.USER,
            userRow.sender
        )
        val decoded = com.tamin.taminhamrah.feature.agent.cache.ChatBubbleCodec
            .decode(userRow.contentType, userRow.contentJson)
        assertEquals(
            "سابقه‌ام را نشان بده",
            (decoded as? com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent.Text)?.message
        )
    }

    @Test
    fun `entering the assistant always opens an empty conversation`() = runTest {
        // A previous conversation with content must NOT be auto-restored — it stays
        // reachable from the history sheet instead.
        seedConversation("old-session", "پیام قدیمی")

        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()

        assertTrue(
            viewModel.uiState.value.chatItems.isEmpty(),
            "the chat should open empty, not on the previous conversation"
        )
        assertNotNull(viewModel.uiState.value.activeSessionId)
    }

    @Test
    fun `opening a saved conversation replaces the chat without replaying typing`() = runTest {
        seedConversation("old-session", "پیام قدیمی")
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()

        viewModel.sendIntent(AgentIntent.LoadChatSession("old-session"))
        advanceUntilIdle()

        val items = viewModel.uiState.value.chatItems
        assertEquals(1, items.size)
        assertFalse(items.first().isTypingAnimating, "restored bubbles must not re-run typing")
        assertEquals("old-session", viewModel.uiState.value.activeSessionId)
        assertFalse(viewModel.uiState.value.isHistoryVisible, "the sheet should close")
    }

    @Test
    fun `the history sheet lists saved conversations`() = runTest {
        seedConversation("old-session", "پیام قدیمی")
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()

        viewModel.sendIntent(AgentIntent.OpenChatHistory)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isHistoryVisible)
        assertEquals(listOf("old-session"), state.sessions.map { it.id })
    }

    @Test
    fun `deleting the open conversation leaves the user on a fresh one`() = runTest {
        seedConversation("old-session", "پیام قدیمی")
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        viewModel.sendIntent(AgentIntent.LoadChatSession("old-session"))
        advanceUntilIdle()

        viewModel.sendIntent(AgentIntent.DeleteChatSession("old-session"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.chatItems.isEmpty())
        assertNotNull(state.activeSessionId)
        assertEquals(null, fakeCacheRepository.getSession("old-session"))
    }

    @Test
    fun `renaming a conversation updates the history list`() = runTest {
        seedConversation("old-session", "پیام قدیمی")
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()

        viewModel.sendIntent(AgentIntent.RenameChatSession("old-session", "نام تازه"))
        advanceUntilIdle()

        assertEquals("نام تازه", fakeCacheRepository.getSession("old-session")?.title)
    }

    /** Seeds a stored conversation containing one agent text bubble. */
    private suspend fun seedConversation(sessionId: String, message: String) {
        fakeCacheRepository.createSession(
            com.tamin.taminhamrah.model.agent.AgentSessionDN(
                id = sessionId,
                title = "قبلی",
                userNationalCode = TEST_NATIONAL_CODE,
                createdAt = 1L,
                lastMessageAt = 2L,
                messageCount = 1
            )
        )
        val (type, payload) = com.tamin.taminhamrah.feature.agent.cache.ChatBubbleCodec.encode(
            com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent.Text(message)
        )!!
        fakeCacheRepository.addMessage(
            com.tamin.taminhamrah.model.agent.AgentCachedMessageDN(
                id = "m1",
                sessionId = sessionId,
                sender = com.tamin.taminhamrah.model.agent.CachedSender.AGENT,
                status = com.tamin.taminhamrah.model.agent.CachedStatus.SUCCESS,
                contentType = type,
                contentJson = payload,
                timestamp = 2L,
                messageOrder = 0
            )
        )
    }

    @Test
    fun `an empty previous session is discarded rather than restored`() = runTest {
        fakeCacheRepository.createSession(
            com.tamin.taminhamrah.model.agent.AgentSessionDN(
                id = "empty-session",
                title = "خالی",
                userNationalCode = TEST_NATIONAL_CODE,
                createdAt = 1L,
                lastMessageAt = 1L
            )
        )

        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.chatItems.isEmpty())
        assertNull(
            fakeCacheRepository.getSession("empty-session"),
            "a session that was never used should be cleaned up"
        )
    }

    private companion object {
        const val TEST_NATIONAL_CODE = "0012345678"
    }
}
