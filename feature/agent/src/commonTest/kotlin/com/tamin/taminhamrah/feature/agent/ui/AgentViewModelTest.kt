package com.tamin.taminhamrah.feature.agent.ui

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.agent.service.AgentActionDispatcher
import com.tamin.taminhamrah.feature.agent.service.FakeFeatureManager
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentIntent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentUiState
import com.tamin.taminhamrah.feature.agent.ui.contract.ChatSender
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.agent.SendAgentPromptUseCase
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
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
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent

class FakeVoiceRecorder : com.tamin.taminhamrah.feature.agent.audio.VoiceRecorder {
    override val amplitude = MutableStateFlow(0)
    override val isRecording = MutableStateFlow(false)
    override fun start(filePath: String) { isRecording.value = true }
    var cancelled = false
    override suspend fun stop() { isRecording.value = false }
    override fun cancel() { cancelled = true; isRecording.value = false }
    override fun newRecordingPath(): String = "/tmp/fake_voice.wav"
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

    override suspend fun updateSessionContext(sessionId: String, lastEntity: String?, state: String?, history: String?) {
        sessions[sessionId] = sessions[sessionId]?.copy(lastEntity = lastEntity, state = state, history = history) ?: return
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

/** Only [getIdentityInfo] is exercised by [AgentViewModel] — everything else is unused. */
class FakeIdentityUserRepository(
    private val identity: com.tamin.taminhamrah.model.identity.IdentityInfoDN? = null
) : com.tamin.taminhamrah.repository.UserRepository {
    override fun getIdentityInfo() = flow { identity?.let { emit(it) } }
    override suspend fun getUserProfileImage() = TODO("not used in AgentViewModelTest")
    override suspend fun fetchTaminRelation() = TODO("not used in AgentViewModelTest")
    override suspend fun sendImageRequest(branchCode: String, serialId: String) = TODO("not used in AgentViewModelTest")
    override suspend fun changeMobile(mobileNumber: String) = TODO("not used in AgentViewModelTest")
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String) = TODO("not used in AgentViewModelTest")
    override suspend fun getSubDominantsInfo(filters: List<com.tamin.taminhamrah.model.request.ApiFilterDN>) = TODO("not used in AgentViewModelTest")
    override suspend fun getBankAccountList(filters: List<com.tamin.taminhamrah.model.request.ApiFilterDN>) = TODO("not used in AgentViewModelTest")
    override suspend fun getInsuredActiveBranch() = TODO("not used in AgentViewModelTest")
    override suspend fun getRelationTaminAll(filters: List<com.tamin.taminhamrah.model.request.ApiFilterDN>) = TODO("not used in AgentViewModelTest")
    override fun getElectronicFile(filters: List<com.tamin.taminhamrah.model.request.ApiFilterDN>) = TODO("not used in AgentViewModelTest")
    override suspend fun downloadDocument(url: String) = TODO("not used in AgentViewModelTest")
    override suspend fun getUserProfile() = TODO("not used in AgentViewModelTest")
    override suspend fun getCurrentUser() = TODO("not used in AgentViewModelTest")
    override fun checkUserIsNew(nationalId: String) = TODO("not used in AgentViewModelTest")
    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ) = TODO("not used in AgentViewModelTest")
    override suspend fun getStatusCertificateReport(filters: List<com.tamin.taminhamrah.model.request.ApiFilterDN>) = TODO("not used in AgentViewModelTest")
    override suspend fun getWageCertificateReport(filters: List<com.tamin.taminhamrah.model.request.ApiFilterDN>) = TODO("not used in AgentViewModelTest")
    override suspend fun getRecipients(filters: List<com.tamin.taminhamrah.model.request.ApiFilterDN>) = TODO("not used in AgentViewModelTest")
}

/** Never called: the test identity has no city ids, so [IdentityInfoUseCase] never reaches it. */
class FakeIdentityCityProvinceRepository : com.tamin.taminhamrah.repository.CityProvinceRepository {
    override fun getCity(cityId: String) = TODO("not used in AgentViewModelTest")
    override fun getProvince(provinceId: String) = TODO("not used in AgentViewModelTest")
    override fun getProvincesPage(query: com.tamin.taminhamrah.model.request.ApiQueryParamDN) =
        TODO("not used in AgentViewModelTest")
    override fun getCitiesPage(query: com.tamin.taminhamrah.model.request.ApiQueryParamDN) =
        TODO("not used in AgentViewModelTest")
    override fun getCitiesByProvincePage(
        provinceCode: String,
        query: com.tamin.taminhamrah.model.request.ApiQueryParamDN,
    ) = TODO("not used in AgentViewModelTest")
}

class FakeAgentRepository : AgentRepository {
    var checkChatAllowedResult: Result<ChatAllowedDN> = Result.success(ChatAllowedDN(canStartChat = true, chatToken = "fake-token", errorMessage = null))
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

/**
 * Only here because [com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase] files the chat
 * token away for the Developer Options token screen. Nothing in these tests reads it back.
 */
class FakeAgentTokenStore : com.tamin.taminhamrah.repository.TokenStoreManager {
    private val tokens = mutableMapOf<com.tamin.taminhamrah.model.auth.TokenSlot, String?>()
    private val tokenValid = MutableStateFlow(false)
    private val authProcessing = MutableStateFlow(false)
    private val activeSlot = MutableStateFlow(com.tamin.taminhamrah.model.auth.TokenSlot.USER)

    override fun saveToken(token: String?) = Unit
    override fun getToken(): String? = null
    override fun saveRefreshToken(refreshToken: String?) = Unit
    override fun getRefreshToken(): String? = null
    override fun getToken(slot: com.tamin.taminhamrah.model.auth.TokenSlot): String? = tokens[slot]
    override fun saveToken(slot: com.tamin.taminhamrah.model.auth.TokenSlot, token: String?) {
        tokens[slot] = token
    }

    override fun getRefreshToken(slot: com.tamin.taminhamrah.model.auth.TokenSlot): String? = null
    override fun saveRefreshToken(
        slot: com.tamin.taminhamrah.model.auth.TokenSlot,
        refreshToken: String?,
    ) = Unit

    override fun getActiveSlot() = activeSlot.value
    override fun activeSlotFlow() = activeSlot
    override suspend fun setActiveSlot(slot: com.tamin.taminhamrah.model.auth.TokenSlot) {
        activeSlot.value = slot
    }

    override fun saveUserId(userId: String?) = Unit
    override fun getUserId(): String? = null
    override fun saveUserType(userType: String?) = Unit
    override fun getUserType(): String? = null
    override fun saveCodeVerifier(codeVerifier: String?) = Unit
    override fun getCodeVerifier(): String? = null
    override fun tokenValidFlow() = tokenValid
    override suspend fun setTokenValid(isValid: Boolean) { tokenValid.value = isValid }
    override fun isAuthProcessingFlow() = authProcessing
    override fun setAuthProcessing(isProcessing: Boolean) { authProcessing.value = isProcessing }
}

/** In-memory chat permission cache. */
class FakeAgentAccessStore : com.tamin.taminhamrah.repository.AgentAccessStore {
    private val state = MutableStateFlow<com.tamin.taminhamrah.model.agent.AgentAccessDN?>(null)
    override val access: kotlinx.coroutines.flow.StateFlow<com.tamin.taminhamrah.model.agent.AgentAccessDN?> = state
    override fun save(access: com.tamin.taminhamrah.model.agent.AgentAccessDN) { state.value = access }
    override fun updateChatToken(token: String?) { state.value = state.value?.copy(chatToken = token) }
    override fun clearChatToken() = updateChatToken(null)
    override fun clear() { state.value = null }
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
    private lateinit var fakeTokenStore: FakeAgentTokenStore
    private lateinit var fakeAccessStore: FakeAgentAccessStore

    // A single StandardTestDispatcher shared between Dispatchers.Main (viewModelScope)
    // and the runTest scope, so advanceUntilIdle() drains ALL pending coroutines.
    private lateinit var testDispatcher: TestDispatcher

    @BeforeTest
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        fakeFeatureManager = FakeFeatureManager()
        val registry = AgentServiceRegistry(emptyList())
        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        actionDispatcher = AgentActionDispatcher(registry, fakeFeatureManager, json)

        fakeAgentRepository = FakeAgentRepository()
        fakeTokenStore = FakeAgentTokenStore()
        fakeCacheRepository = FakeAgentChatCacheRepository()
        fakeAccessStore = FakeAgentAccessStore()
        checkChatAllowedUseCase = CheckChatAllowedUseCase(fakeAgentRepository, fakeTokenStore, fakeAccessStore)
        sendAgentPromptUseCase = SendAgentPromptUseCase(
            fakeAgentRepository,
            checkChatAllowedUseCase,
            fakeAccessStore,
            fakeTokenStore,
        ) { null }
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
            com.tamin.taminhamrah.useCases.agent.CancelAgentRequestUseCase(fakeAgentRepository),
            actionDispatcher,
            fakeFeatureManager,
            FakeVoiceRecorder(),
            FakeVoicePlayer(),
            com.tamin.taminhamrah.feature.agent.audio.MediaPlaybackCoordinator(),
            { TEST_NATIONAL_CODE },
            com.tamin.taminhamrah.useCases.agent.PruneEmptyAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.GetAgentSessionsUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.GetAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.DeleteAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.StartAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.SaveCachedMessageUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.GetCachedMessagesUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.DeletePendingAgentMessagesUseCase(cache),
            com.tamin.taminhamrah.useCases.agent.UpdateAgentSessionUseCase(cache),
            com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase(
                FakeIdentityUserRepository(),
                FakeIdentityCityProvinceRepository()
            )
        )
    }

    @Test
    fun `initial state has isCheckingPermission true`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertTrue(state.isCheckingPermission)
        assertFalse(state.isGenerating)
    }

    @Test
    fun `CheckPermission intent updates state to allowed when repository returns success`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        fakeAgentRepository.checkChatAllowedResult = Result.success(
            ChatAllowedDN(canStartChat = true, chatToken = "test-token", errorMessage = null, canSendVoice = true)
        )

        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isCheckingPermission)
        assertFalse(state.isNotAllowed)
        assertTrue(state.canSendVoice)
        // The token lives in the shared store, where every prompt reads it.
        assertEquals("test-token", fakeAccessStore.access.value?.chatToken)
    }

    @Test
    fun `CheckPermission intent updates state to not allowed when repository returns false`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        fakeAgentRepository.checkChatAllowedResult = Result.success(ChatAllowedDN(canStartChat = false, chatToken = null, errorMessage = "You are blocked"))

        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isCheckingPermission)
        assertTrue(state.isNotAllowed)
        assertEquals("You are blocked", state.notAllowedMessage)
        assertNull(state.activeSessionId, "a refused user must not get a conversation row")
        assertEquals(false, fakeAccessStore.access.value?.canStartChat)
    }

    @Test
    fun `voice input stays off unless the server allows it`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.canSendVoice)
    }

    @Test
    fun `retry while offline checks the permission again`() = runTest(testDispatcher) {
        fakeAgentRepository.checkChatAllowedResult = Result.failure(RuntimeException("offline"))
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isOffline)

        fakeAgentRepository.checkChatAllowedResult =
            Result.success(ChatAllowedDN(canStartChat = true, chatToken = "t", errorMessage = null))
        viewModel.sendIntent(AgentIntent.OnRetryClick)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isOffline)
    }

    @Test
    fun `cancelling stops the prompt in flight and tells the server`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        fakeAgentRepository.sendPromptFlow = flow {
            emit(AgentPollingState.Pending(requestId = "req-1", etaSeconds = 1))
            kotlinx.coroutines.awaitCancellation()
        }

        viewModel.sendIntent(AgentIntent.SendTextPrompt("سلام"))
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isGenerating)

        viewModel.sendIntent(AgentIntent.CancelGeneration)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isGenerating)
        assertTrue(fakeAgentRepository.cancelRequestCalled)
    }

    @Test
    fun `server markdown becomes one markdown reply with its suggestions`() = runTest(testDispatcher) {
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        val data = kotlinx.serialization.json.Json.parseToJsonElement(
            """[{"item_type":"prompt_item","prompt":"بعدی"}]"""
        )
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
                            data = data,
                            message = null,
                            itemType = com.tamin.taminhamrah.model.agent.AgentItemType.MARKDOWN,
                            markdown = listOf("### عنوان\n\nمتن")
                        )
                    )
                )
            )
        )

        viewModel.sendIntent(AgentIntent.SendTextPrompt("سلام"))
        advanceUntilIdle()

        val reply = viewModel.uiState.value.chatItems.single { it.sender == ChatSender.Agent }
        assertEquals(
            com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent.Markdown("### عنوان\n\nمتن"),
            reply.content,
        )
        assertEquals(listOf("بعدی"), reply.suggestedPrompts)
    }

    @Test
    fun `ChangeInputMode intent updates input mode`() = runTest(testDispatcher) {
        viewModel = createViewModel()

        viewModel.sendIntent(AgentIntent.ChangeInputMode(InputMode.Voice))
        advanceUntilIdle()

        assertEquals(InputMode.Voice, viewModel.uiState.value.inputMode)
    }

    @Test
    fun `suggestions ride inside the reply instead of becoming their own chat row`() = runTest {
        viewModel = createViewModel()
        viewModel.sendIntent(AgentIntent.CheckPermission)
        advanceUntilIdle()
        // An entity whose data carries prompt_items: the dispatcher appends them as a
        // separate SuggestedPrompts bubble, which must be folded into the reply.
        val data = kotlinx.serialization.json.Json.parseToJsonElement(
            """[{"item_type":"prompt_item","prompt":"سابقه من"},
                {"item_type":"prompt_item","prompt":"حقوق من"}]"""
        )
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
                            data = data,
                            message = "پاسخ",
                            itemType = null
                        )
                    )
                )
            )
        )

        viewModel.sendIntent(AgentIntent.SendTextPrompt("سلام"))
        advanceUntilIdle()

        val agentItems = viewModel.uiState.value.chatItems.filter { it.sender == ChatSender.Agent }
        assertEquals(1, agentItems.size, "the reply and its suggestions must be one item")
        assertEquals(listOf("سابقه من", "حقوق من"), agentItems.single().suggestedPrompts)
        assertTrue(
            viewModel.uiState.value.chatItems.none {
                it.content is com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent.SuggestedPrompts
            },
            "no standalone suggestions row should remain"
        )
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
