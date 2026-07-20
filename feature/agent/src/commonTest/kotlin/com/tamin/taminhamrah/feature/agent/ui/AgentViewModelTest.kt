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

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        
        fakeFeatureManager = FakeFeatureManager()
        val registry = AgentServiceRegistry(emptyList())
        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        actionDispatcher = AgentActionDispatcher(registry, fakeFeatureManager, json)
        
        fakeAgentRepository = FakeAgentRepository()
        checkChatAllowedUseCase = CheckChatAllowedUseCase(fakeAgentRepository)
        sendAgentPromptUseCase = SendAgentPromptUseCase(fakeAgentRepository)
        
        // Start Koin if required by BaseViewModel, but BaseViewModel usually doesn't need it if dependencies are injected
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AgentViewModel {
        return AgentViewModel(
            sendAgentPromptUseCase,
            checkChatAllowedUseCase,
            actionDispatcher,
            fakeFeatureManager
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
}
