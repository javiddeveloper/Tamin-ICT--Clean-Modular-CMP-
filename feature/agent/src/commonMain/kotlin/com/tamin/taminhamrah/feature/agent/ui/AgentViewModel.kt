package com.tamin.taminhamrah.feature.agent.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.agent.service.AgentActionDispatcher
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentEvent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentIntent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentProcessingState
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentUiState
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentUiState.PartialState
import com.tamin.taminhamrah.feature.agent.ui.contract.ChatItem
import com.tamin.taminhamrah.feature.agent.ui.contract.ChatSender
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.agent.toFeatureFlag
import com.tamin.taminhamrah.model.agent.toProcessingStepTitle
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.agent.SendAgentPromptUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

/**
 * ViewModel for the Agent (AI Chatbot) screen.
 *
 * Implements MVI via [BaseViewModel].
 * All business logic lives in [handleIntent] → [Flow<PartialState>].
 *
 * Main pipeline:
 * 1. User sends a message → [AgentIntent.SendTextPrompt]
 * 2. [SendAgentPromptUseCase] → Flow<AgentPollingState>
 * 3. Each Pending state → Extension Card is updated with ETA
 * 4. Done state → entities are forwarded to [AgentActionDispatcher]
 * 5. Each dispatcher result is appended to the chat list
 */
class AgentViewModel(
    private val sendAgentPromptUseCase: SendAgentPromptUseCase,
    private val checkChatAllowedUseCase: CheckChatAllowedUseCase,
    private val actionDispatcher: AgentActionDispatcher,
    private val featureManager: FeatureManager
) : BaseViewModel<AgentUiState, PartialState, AgentEvent, AgentIntent>(
    initialState = AgentUiState()
) {

    /** Session context shared across pipeline steps */
    private val sessionContext = AgentSessionContext()

    override fun handleIntent(intent: AgentIntent): Flow<PartialState> = when (intent) {
        is AgentIntent.CheckPermission         -> handleCheckPermission()
        is AgentIntent.SendTextPrompt          -> handleSendPrompt(intent.message)
        is AgentIntent.CancelGeneration        -> handleCancelGeneration()
        is AgentIntent.StartNewSession         -> handleStartNewSession()
        is AgentIntent.OnRetryClick            -> handleRetryClick()
        is AgentIntent.ChangeInputMode         -> flow {
            emit(PartialState.InputModeChanged(intent.mode))
        }
        is AgentIntent.ExecuteServiceAction    -> handleServiceAction(intent.actionKey, intent.payload)
    }

    // ─── Intent Handlers ──────────────────────────────────────────────────────

    private fun handleCheckPermission(): Flow<PartialState> = flow {
        emit(PartialState.CheckingPermission(true))

        // ── Step 1: Check the global AGENT FeatureFlag ────────────────────────
        // This check happens before any API call is made.
        // If an admin has disabled the Agent feature entirely, the server-defined
        // message is shown without any network request.
        val isAgentEnabled = featureManager.isFeatureEnabled(FeatureFlag.AGENT)
        if (!isAgentEnabled) {
            val message = featureManager.getDisabledMessage(FeatureFlag.AGENT)
                ?: "The AI assistant service is currently unavailable."
            emit(PartialState.NotAllowed(message))
            emit(PartialState.CheckingPermission(false))
            return@flow
        }

        // ── Step 2: Check user-level permission via API ───────────────────────
        val result = checkChatAllowedUseCase()
        result.fold(
            onSuccess = { data ->
                if (data.canStartChat) {
                    emit(PartialState.ChatAllowedReceived(chatToken = data.chatToken))
                } else {
                    emit(PartialState.NotAllowed(data.errorMessage))
                }
            },
            onFailure = {
                emit(PartialState.NotAllowed("Unable to connect to the service."))
            }
        )
        emit(PartialState.CheckingPermission(false))
    }

    private fun handleSendPrompt(message: String, isRetry: Boolean = false): Flow<PartialState> = flow {
        val currentState = uiState.value
        if (currentState.isGenerating) return@flow

        emit(PartialState.Loading(true))

        if (!isRetry) {
            // Append the user's message to the chat
            val userItem = ChatItem(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.User,
                content = ChatBubbleContent.Text(message)
            )
            emit(PartialState.NewChatItems(listOf(userItem)))
            sendEvent(AgentEvent.ScrollToBottom)
        }

        val request = AgentRequest(
            prompt = message,
            sessionId = currentState.sessionId,
            lastEntity = currentState.lastEntity,
            chatToken = currentState.chatToken
        )

        sendAgentPromptUseCase(request).collect { pollingState ->
            when (pollingState) {
                is AgentPollingState.Pending -> {
                    // Update the Extension Card with the ETA information
                    emit(PartialState.PendingReceived(
                        requestId = pollingState.requestId,
                        etaSeconds = pollingState.etaSeconds
                    ))
                    
                    val attemptSteps = mutableListOf("درحال بررسی درخواست...")
                    for (i in 1..pollingState.attempt) {
                        attemptSteps.add("درحال ارسال درخواست ($i/${pollingState.maxAttempts})")
                    }
                    
                    emit(PartialState.ProcessingStateUpdated(
                        AgentProcessingState(
                            steps = attemptSteps,
                            currentActiveIndex = attemptSteps.size - 1,
                            isCompleted = false
                        )
                    ))
                }

                is AgentPollingState.Done -> {
                    val response = pollingState.response
                    // Update session context
                    emit(PartialState.SessionUpdated(
                        sessionId = response.sessionId,
                        lastEntity = response.lastEntity
                    ))

                    // Determine the pipeline steps with friendly Persian names
                    val serviceSteps = response.entities.mapNotNull { it.action.toProcessingStepTitle() }.distinct()
                    val allSteps = mutableListOf("در حال بررسی درخواست...")
                    allSteps.addAll(serviceSteps)
                    allSteps.add("در حال آماده‌سازی پاسخ...")

                    emit(PartialState.ProcessingStateUpdated(
                        AgentProcessingState(steps = allSteps, currentActiveIndex = 1.coerceAtMost(allSteps.size - 1), isCompleted = false)
                    ))

                    // --- Phase 1: Show the ProcessingSteps bubble and run all steps ---
                    var currentStepIndex = 1
                    val processingBubbleId = "processing_${UUID.randomUUID()}"
                    sendEvent(AgentEvent.ScrollToBottom)

                    // Collect all result bubbles while animating steps
                    val allResultItems = mutableListOf<ChatItem>()

                    response.entities.forEach { entity ->
                        // Advance step indicator
                        val stepTitle = entity.action.toProcessingStepTitle()
                        if (stepTitle != null) {
                            val stepIndex = allSteps.indexOf(stepTitle)
                            if (stepIndex > currentStepIndex) {
                                currentStepIndex = stepIndex
                                val updatedState = AgentProcessingState(
                                    steps = allSteps,
                                    currentActiveIndex = currentStepIndex,
                                    isCompleted = false
                                )
                                emit(PartialState.ProcessingStateUpdated(updatedState))
                                kotlinx.coroutines.delay(500L)
                            }
                        }

                        // Dispatch entity and COLLECT results (don't emit yet)
                        val result = dispatchEntity(entity)
                        val newItems = result.map { bubble ->
                            ChatItem(
                                id = "${entity.action.key}_${UUID.randomUUID()}",
                                sender = ChatSender.Agent,
                                content = bubble,
                                isTypingAnimating = true
                            )
                        }
                        allResultItems.addAll(newItems)
                    }

                    // --- Phase 2: Mark steps as complete ---
                    emit(PartialState.ProcessingStateUpdated(null))

                    // Brief pause so user sees the completed stepper before content appears
                    kotlinx.coroutines.delay(400L)

                    // --- Phase 3: Emit all result bubbles sequentially ---
                    allResultItems.forEach { item ->
                        emit(PartialState.NewChatItems(listOf(item)))
                        sendEvent(AgentEvent.ScrollToBottom)
                        
                        // Calculate how long this bubble takes to animate
                        val typingDuration = when (val content = item.content) {
                            is ChatBubbleContent.Text -> {
                                val lines = content.message.split("\n")
                                lines.sumOf { (it.length * 15L).coerceAtLeast(150L) }
                            }
                            is ChatBubbleContent.KeyValue -> {
                                (content.items.size * 150L) + 500L
                            }
                            is ChatBubbleContent.SuggestedPrompts -> {
                                500L
                            }
                            else -> 500L
                        }
                        
                        // Wait for this bubble to finish before emitting the next
                        kotlinx.coroutines.delay(typingDuration + 200L)
                    }

                }

                is AgentPollingState.Failed -> {
                    emit(PartialState.ProcessingStateUpdated(null))
                    val errorItem = ChatItem(
                        id = UUID.randomUUID().toString(),
                        sender = ChatSender.Agent,
                        content = ChatBubbleContent.ServiceError(pollingState.message, canRetryPrompt = true)
                    )
                    emit(PartialState.NewChatItems(listOf(errorItem)))
                    sendEvent(AgentEvent.ShowError(pollingState.message))
                }
                
                is AgentPollingState.Cancelled -> {
                    emit(PartialState.GenerationCancelled)
                    emit(PartialState.ProcessingStateUpdated(null))
                }
            }
        }

        emit(PartialState.Loading(false))
    }

    private fun handleCancelGeneration(): Flow<PartialState> = flow {
        emit(PartialState.GenerationCancelled)
        emit(PartialState.ProcessingStateUpdated(null))
        emit(PartialState.Loading(false))
    }

    private fun handleRetryClick(): Flow<PartialState> {
        val lastUserMessage = uiState.value.chatItems.lastOrNull { it.sender == ChatSender.User }
        val textMessage = (lastUserMessage?.content as? ChatBubbleContent.Text)?.message
        if (!textMessage.isNullOrBlank()) {
            return handleSendPrompt(textMessage, isRetry = true)
        }
        return flow { }
    }

    private fun handleStartNewSession(): Flow<PartialState> = flow {
        sessionContext.clear()
        emit(PartialState.SessionUpdated(sessionId = null, lastEntity = null))
        emit(PartialState.NewChatItems(emptyList()))
        emit(PartialState.CheckingPermission(false))
    }

    private fun handleServiceAction(
        actionKey: AgentActionKey,
        payload: kotlinx.serialization.json.JsonElement?
    ): Flow<PartialState> = flow {
        // Build a synthetic entity for dispatch
        val entity = AiEntityDN(
            action = actionKey,
            stepNumber = 0,
            payload = payload,
            data = null,
            message = null,
            itemType = null
        )
        emit(PartialState.Loading(true))
        val bubbles = dispatchEntity(entity)
        if (bubbles.isNotEmpty()) {
            val items = bubbles.map {
                ChatItem(
                    id = "${actionKey.key}_${UUID.randomUUID()}",
                    sender = ChatSender.Agent,
                    content = it,
                    isTypingAnimating = true
                )
            }
            emit(PartialState.NewChatItems(items))
            sendEvent(AgentEvent.ScrollToBottom)
        }
        emit(PartialState.Loading(false))
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private suspend fun dispatchEntity(entity: AiEntityDN): List<ChatBubbleContent> {
        return when (val result = actionDispatcher.dispatch(entity, sessionContext)) {
            is AgentServiceResult.Success -> result.bubbles

            is AgentServiceResult.FeatureDisabled ->
                listOf(ChatBubbleContent.ServiceError(result.message))

            is AgentServiceResult.NoHandler -> {
                // If the entity has a plain text message, render it as a text bubble
                entity.message?.let { listOf(ChatBubbleContent.Text(it)) } ?: emptyList()
            }

            is AgentServiceResult.Error ->
                listOf(ChatBubbleContent.ServiceError(result.message, actionKey = entity.action, payload = entity.payload))
        }
    }

    // ─── State Reduction ──────────────────────────────────────────────────────

    override fun reduceState(
        currentState: AgentUiState,
        partialState: PartialState
    ): AgentUiState = when (partialState) {
        is PartialState.Loading ->
            currentState.copy(isGenerating = partialState.isGenerating)

        is PartialState.CheckingPermission ->
            currentState.copy(isCheckingPermission = partialState.isChecking)

        is PartialState.NotAllowed ->
            currentState.copy(
                isCheckingPermission = false,
                isNotAllowed = true,
                notAllowedMessage = partialState.message
            )

        is PartialState.ChatAllowedReceived ->
            currentState.copy(
                isCheckingPermission = false,
                isNotAllowed = false,
                chatToken = partialState.chatToken,
                sessionId = partialState.sessionId
            )

        is PartialState.PendingReceived ->
            currentState.copy(currentRequestId = partialState.requestId)

        is PartialState.ProcessingStateUpdated ->
            currentState.copy(processingState = partialState.state)

        is PartialState.NewChatItems -> {
            if (partialState.items.isEmpty()) {
                // Reset chat list
                currentState.copy(chatItems = emptyList())
            } else {
                currentState.copy(chatItems = currentState.chatItems + partialState.items)
            }
        }

        is PartialState.UpdateChatItem -> {
            currentState.copy(
                chatItems = currentState.chatItems.map { 
                    if (it.id == partialState.item.id) partialState.item else it 
                }
            )
        }

        is PartialState.SessionUpdated ->
            currentState.copy(
                sessionId = partialState.sessionId,
                lastEntity = partialState.lastEntity
            )

        is PartialState.InputModeChanged ->
            currentState.copy(inputMode = partialState.mode)

        is PartialState.GenerationCancelled ->
            currentState.copy(isGenerating = false, currentRequestId = null)

        is PartialState.Error ->
            currentState.copy(isGenerating = false)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
