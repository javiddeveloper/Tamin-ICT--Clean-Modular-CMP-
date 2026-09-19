package com.tamin.taminhamrah.ui.home

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.agent.AgentAccessDN
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.home.HomeContentDN
import com.tamin.taminhamrah.repository.AgentAccessStore
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.home.HomeRepository
import com.tamin.taminhamrah.ui.home.contract.HomeIntent
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.home.GetHomeContentUseCase
import com.tamin.taminhamrah.useCases.home.SyncHomeContentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Backs [GetHomeContentUseCase]/[SyncHomeContentUseCase]; [syncAction] lets a test control what
 *  a sync call does (throw, delay, or just count) without needing every real sub-repository. */
private class FakeHomeRepository : HomeRepository {
    val content = MutableStateFlow<HomeContentDN?>(null)
    var syncCallCount = 0
    var syncAction: suspend () -> Unit = {}

    override fun getHomeContent(): Flow<HomeContentDN?> = content.asStateFlow()

    override suspend fun syncHomeContent() {
        syncCallCount++
        syncAction()
    }
}

private class FakeFeatureManager(var status: FeatureStatus = FeatureStatus.Disabled(null)) : FeatureManager {
    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> = flowOf(status)
    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean = status is FeatureStatus.Enabled
    override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
}

private class FakeAgentRepository : AgentRepository {
    override fun sendPrompt(request: AgentRequest): Flow<AgentPollingState> = flowOf()
    override suspend fun cancelRequest(requestId: String): Result<Unit> = Result.success(Unit)
    override suspend fun checkChatAllowed(): Result<ChatAllowedDN> =
        Result.success(ChatAllowedDN(canStartChat = false, chatToken = null, errorMessage = null))
}

private class FakeAgentAccessStore : AgentAccessStore {
    private val state = MutableStateFlow<AgentAccessDN?>(null)
    override val access = state.asStateFlow()
    override fun save(access: AgentAccessDN) { state.value = access }
    override fun updateChatToken(token: String?) { state.value = state.value?.copy(chatToken = token) }
    override fun clearChatToken() = updateChatToken(null)
    override fun clear() { state.value = null }
}

/** Only the members `HomeViewModel`'s dependency chain actually touches are meaningfully
 *  implemented; the rest are inert stubs. */
private class FakeTokenStoreManager : TokenStoreManager {
    private val tokens = mutableMapOf<TokenSlot, String?>()
    private val refreshTokens = mutableMapOf<TokenSlot, String?>()
    private val activeSlot = MutableStateFlow(TokenSlot.USER)
    val tokenValid = MutableStateFlow(false)
    private val authProcessing = MutableStateFlow(false)

    override fun saveToken(token: String?) { tokens[TokenSlot.USER] = token }
    override fun getToken(): String? = tokens[activeSlot.value]
    override fun saveRefreshToken(refreshToken: String?) { refreshTokens[TokenSlot.USER] = refreshToken }
    override fun getRefreshToken(): String? = refreshTokens[activeSlot.value]
    override fun getToken(slot: TokenSlot): String? = tokens[slot]
    override fun saveToken(slot: TokenSlot, token: String?) { tokens[slot] = token }
    override fun getRefreshToken(slot: TokenSlot): String? = refreshTokens[slot]
    override fun saveRefreshToken(slot: TokenSlot, refreshToken: String?) { refreshTokens[slot] = refreshToken }
    override fun getActiveSlot(): TokenSlot = activeSlot.value
    override fun activeSlotFlow(): Flow<TokenSlot> = activeSlot.asStateFlow()
    override suspend fun setActiveSlot(slot: TokenSlot) { activeSlot.value = slot }
    override fun saveUserId(userId: String?) = Unit
    override fun getUserId(): String? = null
    override fun saveUserType(userType: String?) = Unit
    override fun getUserType(): String? = null
    override fun saveCodeVerifier(codeVerifier: String?) = Unit
    override fun getCodeVerifier(): String? = null
    override fun tokenValidFlow(): Flow<Boolean> = tokenValid.asStateFlow()
    override suspend fun setTokenValid(isValid: Boolean) { tokenValid.value = isValid }
    override fun isAuthProcessingFlow(): Flow<Boolean> = authProcessing.asStateFlow()
    override fun setAuthProcessing(isProcessing: Boolean) { authProcessing.value = isProcessing }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeHomeRepository: FakeHomeRepository
    private lateinit var fakeTokenStore: FakeTokenStoreManager

    @BeforeTest
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeHomeRepository = FakeHomeRepository()
        fakeTokenStore = FakeTokenStoreManager()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        val agentAccessStore = FakeAgentAccessStore()
        return HomeViewModel(
            checkChatAllowedUseCase = CheckChatAllowedUseCase(FakeAgentRepository(), fakeTokenStore, agentAccessStore),
            featureManager = FakeFeatureManager(),
            getHomeContentUseCase = GetHomeContentUseCase(fakeHomeRepository),
            syncHomeContentUseCase = SyncHomeContentUseCase(fakeHomeRepository),
            tokenStoreManager = fakeTokenStore,
        )
    }

    @Test
    fun `init triggers a sync via tokenValidFlow's initial emission`() = runTest(testDispatcher) {
        createViewModel()
        advanceUntilIdle()

        assertEquals(1, fakeHomeRepository.syncCallCount)
    }

    @Test
    fun `Retry intent triggers another sync`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(1, fakeHomeRepository.syncCallCount)

        viewModel.sendIntent(HomeIntent.Retry)
        advanceUntilIdle()

        assertEquals(2, fakeHomeRepository.syncCallCount)
    }

    @Test
    fun `a second distinct token value starts a new sync (collectLatest reacts to every change)`() = runTest(testDispatcher) {
        createViewModel()
        advanceUntilIdle()
        assertEquals(1, fakeHomeRepository.syncCallCount)

        fakeTokenStore.setTokenValid(true) // a new distinct value
        advanceUntilIdle()

        assertEquals(2, fakeHomeRepository.syncCallCount)
    }

    @Test
    fun `a plain sync failure is swallowed and does not crash init`() = runTest(testDispatcher) {
        fakeHomeRepository.syncAction = { throw RuntimeException("network error") }

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(1, fakeHomeRepository.syncCallCount)
        assertTrue(viewModel.uiState.value.homeContent == null)
    }
}
