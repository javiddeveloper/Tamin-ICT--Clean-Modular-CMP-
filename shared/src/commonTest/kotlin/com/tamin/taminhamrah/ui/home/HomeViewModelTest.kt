package com.tamin.taminhamrah.ui.home

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.agent.AgentAccessDN
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.model.home.HomeContentDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.AgentAccessStore
import com.tamin.taminhamrah.repository.AgentRepository
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.home.HomeRepository
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.toApiException
import com.tamin.taminhamrah.ui.home.contract.HomeEvent
import com.tamin.taminhamrah.ui.home.contract.HomeIntent
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.home.GetHomeContentUseCase
import com.tamin.taminhamrah.useCases.home.SyncHomeContentUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
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

/**
 * Backs [GetTalfighInfosUseCase]: [talfighResult] is what the years call answers with, and
 * [talfighError] makes it fail instead.
 *
 * Only `getTalfighInfos` is meaningfully implemented — خلاصهٔ سابقه is the one thing on home that
 * reads this repository, and the identity call it never makes would cost a 30-field fixture.
 */
private class FakeHistoryRepository : HistoryRepository {
    var talfighError: Throwable? = null
    var talfighResult = TalfighInfoDN(list = null, total = null)

    /** Set to hold the call open, so a test can read the state while it is still in flight. */
    var talfighGate: CompletableDeferred<Unit>? = null

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN {
        talfighGate?.await()
        talfighError?.let { throw it }
        return talfighResult
    }

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>) =
        DastmozdInfoDN(list = null, total = null)

    override suspend fun getUserInfos(): UserInfoDN = error("home does not read the identity here")
    override suspend fun getUserRole() = UserRoleDN.INSURED
    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) = Unit
    override suspend fun sendHistoryNotice(): String? = null
    override fun downloadHistoryReport(type: HistoryCertificateType): Flow<PdfDownloadDN> = flowOf()
    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> = flowOf()
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
    private lateinit var fakeHistoryRepository: FakeHistoryRepository

    @BeforeTest
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeHomeRepository = FakeHomeRepository()
        fakeTokenStore = FakeTokenStoreManager()
        fakeHistoryRepository = FakeHistoryRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        featureManager: FakeFeatureManager = FakeFeatureManager(),
    ): HomeViewModel {
        val agentAccessStore = FakeAgentAccessStore()
        return HomeViewModel(
            checkChatAllowedUseCase = CheckChatAllowedUseCase(FakeAgentRepository(), fakeTokenStore, agentAccessStore),
            featureManager = featureManager,
            getHomeContentUseCase = GetHomeContentUseCase(fakeHomeRepository),
            syncHomeContentUseCase = SyncHomeContentUseCase(fakeHomeRepository),
            tokenStoreManager = fakeTokenStore,
            getTalfighInfosUseCase = GetTalfighInfosUseCase(fakeHistoryRepository),
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
        assertNull(viewModel.uiState.value.homeContent)
    }

    // ─── خلاصهٔ سابقه ───────────────────────────────────────────────────────────────────────────

    @Test
    fun `the summary loads beside the header and clears its own loading flag`() = runTest(testDispatcher) {
        fakeHistoryRepository.talfighResult = talfighInfo(year = "1402", months = listOf("31", "30"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isHistorySummaryLoading)
        assertFalse(state.historySummaryFailed)
        assertEquals("۱۴۰۲", state.historySummary?.yearLabel)
        assertEquals(2, state.historySummary?.registeredCount)
    }

    /** No year on record is an answer, not a failure: the card is left out and no retry is offered. */
    @Test
    fun `an empty year list finishes with no summary and no failure`() = runTest(testDispatcher) {
        fakeHistoryRepository.talfighResult = TalfighInfoDN(list = emptyList(), total = 0)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isHistorySummaryLoading)
        assertFalse(state.historySummaryFailed)
        assertNull(state.historySummary)
    }

    /**
     * The repository only throws when the call never arrived *and* nothing was cached. A connection
     * failure is the one case a retry can fix, so it is the one case that offers one.
     */
    @Test
    fun `a connection failure asks for a retry`() = runTest(testDispatcher) {
        fakeHistoryRepository.talfighError = ErrorUri.NO_CONNECTION_ERROR.toApiException()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isHistorySummaryLoading)
        assertTrue(state.historySummaryFailed)
        assertNull(state.historySummary)
    }

    /**
     * A کارفرما or a مستمری‌بگیر has no premiums of their own, and the service refusing to answer
     * for them is not something a retry can change — so that failure leaves the card out silently.
     */
    @Test
    fun `a failure the server answered leaves the card out without a retry`() = runTest(testDispatcher) {
        fakeHistoryRepository.talfighError = ErrorUri.FORBIDDEN.toApiException()

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isHistorySummaryLoading)
        assertFalse(state.historySummaryFailed)
        assertNull(state.historySummary)
    }

    /** What the retry pill sends — the same intent the first load used. */
    @Test
    fun `retrying the summary asks again and succeeds`() = runTest(testDispatcher) {
        fakeHistoryRepository.talfighError = ErrorUri.NO_CONNECTION_ERROR.toApiException()
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.historySummaryFailed)

        fakeHistoryRepository.talfighError = null
        fakeHistoryRepository.talfighResult = talfighInfo(year = "1403", months = listOf("31"))
        viewModel.sendIntent(HomeIntent.LoadHistorySummary)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.historySummaryFailed)
        assertEquals("۱۴۰۳", state.historySummary?.yearLabel)
    }

    /**
     * A retry has to clear the failure as it starts, not when it answers: the slot goes back to its
     * skeleton, so the person sees the tap do something rather than the same row they just tapped.
     */
    @Test
    fun `retrying puts the slot back to loading while the call is in flight`() = runTest(testDispatcher) {
        fakeHistoryRepository.talfighError = ErrorUri.NO_CONNECTION_ERROR.toApiException()
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.historySummaryFailed)

        val gate = CompletableDeferred<Unit>()
        fakeHistoryRepository.talfighGate = gate
        fakeHistoryRepository.talfighError = null
        fakeHistoryRepository.talfighResult = talfighInfo(year = "1404", months = listOf("31"))

        viewModel.sendIntent(HomeIntent.LoadHistorySummary)
        advanceUntilIdle()

        val inFlight = viewModel.uiState.value
        assertTrue(inFlight.isHistorySummaryLoading)
        assertFalse(inFlight.historySummaryFailed)

        gate.complete(Unit)
        advanceUntilIdle()

        val answered = viewModel.uiState.value
        assertFalse(answered.isHistorySummaryLoading)
        assertEquals("۱۴۰۴", answered.historySummary?.yearLabel)
    }

    @Test
    fun `tapping the summary opens the history screen through the feature gate`() = runTest(testDispatcher) {
        val viewModel = createViewModel(FakeFeatureManager(FeatureStatus.Enabled))
        advanceUntilIdle()

        val events = mutableListOf<HomeEvent>()
        val collector = launch { viewModel.events.toList(events) }
        viewModel.sendIntent(HomeIntent.OnHistorySummaryClick)
        advanceUntilIdle()
        collector.cancel()

        assertEquals(listOf<HomeEvent>(HomeEvent.NavigateToService(FeatureFlag.COMBINED_RECORD)), events)
    }

    /** The same gate every card goes through: a service switched off explains itself, not opens. */
    @Test
    fun `tapping the summary while the history service is off explains instead of opening`() = runTest(testDispatcher) {
        val viewModel = createViewModel(FakeFeatureManager(FeatureStatus.Disabled("سرویس غیرفعال است")))
        advanceUntilIdle()

        val events = mutableListOf<HomeEvent>()
        val collector = launch { viewModel.events.toList(events) }
        viewModel.sendIntent(HomeIntent.OnHistorySummaryClick)
        advanceUntilIdle()
        collector.cancel()

        assertEquals(listOf<HomeEvent>(HomeEvent.ShowMessage("سرویس غیرفعال است")), events)
    }

    /** Same number پروفایل › پشتیبانی dials. */
    @Test
    fun `tapping the header support icon dials 1420`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val events = mutableListOf<HomeEvent>()
        val collector = launch { viewModel.events.toList(events) }
        viewModel.sendIntent(HomeIntent.OnSupportClick)
        advanceUntilIdle()
        collector.cancel()

        assertEquals(listOf<HomeEvent>(HomeEvent.NavigateToWeb("tel:1420")), events)
    }

    private fun talfighInfo(year: String, months: List<String>) = TalfighInfoDN(
        list = listOf(
            TalfighInfoItemDN(
                months = months,
                risuid = null,
                historyYears = null,
                historyMonths = null,
                sumYear = null,
                historyDays = null,
                sumHistoryYears = null,
                id = null,
                hisYear = year,
            )
        ),
        total = 1,
    )
}
