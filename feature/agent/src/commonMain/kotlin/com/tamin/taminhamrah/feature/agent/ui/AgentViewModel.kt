package com.tamin.taminhamrah.feature.agent.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.agent.audio.MediaPlaybackCoordinator
import com.tamin.taminhamrah.feature.agent.audio.VoicePlayer
import com.tamin.taminhamrah.feature.agent.audio.VoiceRecorder
import com.tamin.taminhamrah.feature.agent.audio.deleteFile
import com.tamin.taminhamrah.feature.agent.audio.readFileBytes
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownParser
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
import com.tamin.taminhamrah.feature.agent.ui.contract.VoicePreviewState
import com.tamin.taminhamrah.feature.agent.ui.contract.VoiceRecordingState
import com.tamin.taminhamrah.feature.agent.ui.contract.VOICE_AMPLITUDE_INTERVAL_MS
import com.tamin.taminhamrah.feature.agent.ui.contract.VOICE_MAX_DURATION_MS
import com.tamin.taminhamrah.feature.agent.ui.contract.VOICE_NEAR_LIMIT_MS
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.agent.toFeatureFlag
import com.tamin.taminhamrah.model.agent.toProcessingStepTitle
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.feature.agent.cache.ChatBubbleCodec
import com.tamin.taminhamrah.model.agent.AgentCachedMessageDN
import com.tamin.taminhamrah.model.agent.AgentSessionDN
import com.tamin.taminhamrah.model.agent.CachedSender
import com.tamin.taminhamrah.model.agent.CachedStatus
import com.tamin.taminhamrah.useCases.agent.CancelAgentRequestUseCase
import com.tamin.taminhamrah.useCases.agent.CheckChatAllowedUseCase
import com.tamin.taminhamrah.useCases.agent.DeletePendingAgentMessagesUseCase
import com.tamin.taminhamrah.useCases.agent.GetCachedMessagesUseCase
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.agent.PruneEmptyAgentSessionUseCase
import com.tamin.taminhamrah.useCases.agent.GetAgentSessionsUseCase
import com.tamin.taminhamrah.useCases.agent.GetAgentSessionUseCase
import com.tamin.taminhamrah.useCases.agent.DeleteAgentSessionUseCase
import com.tamin.taminhamrah.useCases.agent.SaveCachedMessageUseCase
import com.tamin.taminhamrah.useCases.agent.SendAgentPromptUseCase
import com.tamin.taminhamrah.useCases.agent.StartAgentSessionUseCase
import com.tamin.taminhamrah.useCases.agent.UpdateAgentSessionUseCase
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_offline_error
import taminx.core.core_ui.agent_request_failed
import taminx.core.core_ui.agent_service_unavailable
import taminx.core.core_ui.deep_link_feature_unavailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transformWhile
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
    private val cancelAgentRequestUseCase: CancelAgentRequestUseCase,
    private val actionDispatcher: AgentActionDispatcher,
    private val featureManager: FeatureManager,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer,
    private val playbackCoordinator: MediaPlaybackCoordinator,
    private val getCurrentUserNationalCodeUseCase: GetCurrentUserNationalCodeUseCase,
    private val pruneEmptyAgentSessionUseCase: PruneEmptyAgentSessionUseCase,
    private val getAgentSessionsUseCase: GetAgentSessionsUseCase,
    private val getAgentSessionUseCase: GetAgentSessionUseCase,
    private val deleteAgentSessionUseCase: DeleteAgentSessionUseCase,
    private val startAgentSessionUseCase: StartAgentSessionUseCase,
    private val saveCachedMessageUseCase: SaveCachedMessageUseCase,
    private val getCachedMessagesUseCase: GetCachedMessagesUseCase,
    private val deletePendingAgentMessagesUseCase: DeletePendingAgentMessagesUseCase,
    private val updateAgentSessionUseCase: UpdateAgentSessionUseCase
) : BaseViewModel<AgentUiState, PartialState, AgentEvent, AgentIntent>(
    initialState = AgentUiState()
) {

    /** Session context shared across pipeline steps */
    private val sessionContext = AgentSessionContext()

    /** Local conversation id used for cache rows — distinct from the server session id. */
    private var cacheSessionId: String? = null

    /**
     * Sent as `sessionId` when there is no cached conversation (identity unknown), so the server
     * still sees one id per conversation. Replaced whenever a new conversation starts.
     */
    private var uncachedSessionId: String = newSessionId()

    /** National code scoping cached conversations; resolved lazily once per VM. */
    private var cachedNationalCode: String? = null

    /** The coroutine collecting the prompt in flight, so cancelling really stops it. */
    private var generationJob: Job? = null

    /** Bubbles whose reveal animation already played, so recycling never replays it. */
    private val completedTypingIds = mutableSetOf<String>()

    init {
        // A video (or another clip) taking the audio must silence whatever voice is
        // playing, otherwise the two talk over each other.
        viewModelScope.launch {
            playbackCoordinator.activeOwner.collect { owner ->
                val isVoiceOwner = owner != null &&
                    (owner.startsWith("voice:"))
                if (!isVoiceOwner && voicePlayer.isPlaying.value) {
                    voicePlayer.playPause()
                }
            }
        }
    }

    /** Flips to false to signal the recording loop to finish (from StopVoiceRecording). */
    private val isRecordingActive = MutableStateFlow(false)

    /** Path currently loaded in [voicePlayer] — preview or a chat bubble. */
    private var loadedAudioPath: String? = null

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
        is AgentIntent.OnTypingFinished        -> handleTypingFinished(intent.itemId)
        is AgentIntent.OpenChatHistory         -> handleOpenHistory()
        is AgentIntent.CloseChatHistory        -> flow {
            emit(PartialState.HistoryVisibilityChanged(false))
        }
        is AgentIntent.LoadChatSession         -> handleLoadSession(intent.sessionId)
        is AgentIntent.DeleteChatSession       -> handleDeleteSession(intent.sessionId)
        is AgentIntent.RenameChatSession       -> handleRenameSession(intent.sessionId, intent.title)
        is AgentIntent.StartVoiceRecording     -> handleStartVoiceRecording()
        is AgentIntent.StopVoiceRecording      -> flow { isRecordingActive.value = false }
        is AgentIntent.DeleteVoiceRecording    -> handleDeleteVoiceRecording()
        is AgentIntent.SendVoiceRecording      -> handleSendVoiceRecording()
        is AgentIntent.TogglePreviewPlayback   -> handleTogglePreviewPlayback()
        is AgentIntent.SeekPreview             -> flow {
            voicePlayer.seekTo(intent.ms)
            uiState.value.voicePreview?.let { emit(PartialState.VoicePreviewUpdated(it.copy(positionMs = intent.ms))) }
        }
        is AgentIntent.ToggleVoicePlayback     -> handleToggleVoicePlayback(intent.itemId, intent.filePath)
        is AgentIntent.StopVoicePlayback       -> flow {
            voicePlayer.stop()
            loadedAudioPath = null
            uiState.value.playingVoiceId?.let {
                playbackCoordinator.release(MediaPlaybackCoordinator.voiceOwner(it))
            }
            emit(PartialState.VoicePlaybackUpdated(itemId = null, positionMs = 0))
        }
        is AgentIntent.SeekVoicePlayback       -> flow {
            voicePlayer.seekTo(intent.ms)
            emit(
                PartialState.VoicePlaybackUpdated(
                    itemId = intent.itemId,
                    positionMs = intent.ms,
                    durationMs = uiState.value.voicePlaybackDurationMs,
                    isPlaying = uiState.value.isVoicePlaying
                )
            )
        }
        is AgentIntent.ShareContent -> flow {
            sendEvent(AgentEvent.ShareText(intent.text))
        }
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
                ?: getString(Res.string.agent_service_unavailable)
            emit(PartialState.NotAllowed(message))
            emit(PartialState.CheckingPermission(false))
            return@flow
        }

        // ── Step 2: Check user-level permission via API ───────────────────────
        val result = checkChatAllowedUseCase()
        result.fold(
            onSuccess = { data ->
                emit(PartialState.OfflineChanged(false))
                if (data.canStartChat) {
                    emit(PartialState.ChatAllowedReceived(canSendVoice = data.canSendVoice))
                } else {
                    // A real "no" from the server — this one does block the screen. The use case
                    // has cached the refusal, which also hides the assistant's entry point, and a
                    // refused user gets no conversation row.
                    emit(PartialState.NotAllowed(data.errorMessage))
                    emit(PartialState.CheckingPermission(false))
                    return@flow
                }
            },
            onFailure = {
                // Unreachable service is not the same as being denied: keep the chat open
                // on the cached conversation and only block sending.
                emit(PartialState.OfflineChanged(true))
            }
        )
        emit(PartialState.CheckingPermission(false))

        // ── Step 3: Open on an empty conversation ─────────────────────────────
        // Earlier chats are reachable from the history sheet, not auto-restored.
        if (uiState.value.activeSessionId == null) {
            startEmptySession().forEach { emit(it) }
        }
    }

    private fun handleSendPrompt(
        message: String,
        isRetry: Boolean = false,
        voiceBytes: ByteArray? = null,
        voiceFileName: String? = null,
        addUserBubble: Boolean = true
    ): Flow<PartialState> = flow {
        val currentState = uiState.value
        if (currentState.isGenerating) return@flow
        if (currentState.isOffline) {
            sendEvent(AgentEvent.ShowError(getString(Res.string.agent_offline_error)))
            return@flow
        }

        emit(PartialState.Loading(true))
        generationJob = currentCoroutineContext()[Job]

        if (!isRetry && addUserBubble) {
            // Append the user's message to the chat
            val userItem = ChatItem(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.User,
                content = ChatBubbleContent.Text(message)
            )
            emit(PartialState.NewChatItems(listOf(userItem)))
            cacheBubble(userItem)
            sendEvent(AgentEvent.ScrollToBottom)
        }

        // Like the native app, the server's memory of the chat is keyed on the local conversation
        // id, which is sent from the very first prompt; the id in the answer is not used.
        val request = AgentRequest(
            prompt = message,
            sessionId = cacheSessionId ?: uncachedSessionId,
            lastEntity = currentState.lastEntity,
            state = currentState.conversationState,
            history = currentState.conversationHistory,
            voiceBytes = voiceBytes,
            voiceFileName = voiceFileName
        )

        sendAgentPromptUseCase(request).collect { pollingState ->
            when (pollingState) {
                is AgentPollingState.Pending -> {
                    // Update the Extension Card with the ETA information
                    emit(PartialState.PendingReceived(
                        requestId = pollingState.requestId,
                        etaSeconds = pollingState.etaSeconds
                    ))
                    
                    val steps = listOf(
                        "درحال بررسی درخواست...",
                        "درحال ارسال درخواست (${pollingState.attempt}/${pollingState.maxAttempts})"
                    )
                    
                    emit(PartialState.ProcessingStateUpdated(
                        AgentProcessingState(
                            steps = steps,
                            currentActiveIndex = 1,
                            isCompleted = false
                        )
                    ))
                }

                is AgentPollingState.Done -> {
                    val response = pollingState.response
                    // An answer without context keeps the previous one (native app behaviour).
                    val context = PartialState.SessionUpdated(
                        lastEntity = response.lastEntity ?: currentState.lastEntity,
                        state = response.state ?: currentState.conversationState,
                        history = response.history ?: currentState.conversationHistory,
                    )
                    emit(context)
                    // Persist the context so a resumed conversation keeps its thread.
                    cacheSessionId?.let { id ->
                        runCatching {
                            updateAgentSessionUseCase.context(id, context.lastEntity, context.state, context.history)
                        }
                    }

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
                    allResultItems.foldSuggestionsIntoReplies().forEach { item ->
                        emit(PartialState.NewChatItems(listOf(item)))
                        cacheBubble(item)
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
                            is ChatBubbleContent.Markdown -> {
                                MarkdownParser.parse(content.text).size * MARKDOWN_BLOCK_REVEAL_MS + 300L
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
                    val message = pollingState.message ?: getString(Res.string.agent_request_failed)
                    val errorItem = ChatItem(
                        id = UUID.randomUUID().toString(),
                        sender = ChatSender.Agent,
                        content = ChatBubbleContent.ServiceError(message, canRetryPrompt = true)
                    )
                    emit(PartialState.NewChatItems(listOf(errorItem)))
                    sendEvent(AgentEvent.ShowError(message))
                }
                
                is AgentPollingState.Cancelled -> {
                    emit(PartialState.GenerationCancelled)
                    emit(PartialState.ProcessingStateUpdated(null))
                }
            }
        }

        emit(PartialState.Loading(false))
        generationJob = null
    }

    /**
     * Stops the prompt in flight for real: the polling coroutine is cancelled, so no late answer
     * lands in the chat, and the server is told to drop the request.
     */
    private fun handleCancelGeneration(): Flow<PartialState> = flow {
        val requestId = uiState.value.currentRequestId
        generationJob?.cancel()
        generationJob = null
        emit(PartialState.GenerationCancelled)
        emit(PartialState.ProcessingStateUpdated(null))
        emit(PartialState.Loading(false))
        requestId?.let { runCatching { cancelAgentRequestUseCase(it) } }
    }

    private fun handleRetryClick(): Flow<PartialState> {
        // A failed permission check left the chat offline; check again before resending.
        if (uiState.value.isOffline) return handleCheckPermission()
        val lastUserMessage = uiState.value.chatItems.lastOrNull { it.sender == ChatSender.User }
        val textMessage = (lastUserMessage?.content as? ChatBubbleContent.Text)?.message
        if (!textMessage.isNullOrBlank()) {
            return flow {
                // Drop the failed answer from the cache first, so retrying does not
                // leave a duplicate reply behind (old_Android: deletePendingAiMessages).
                cacheSessionId?.let { id ->
                    runCatching { deletePendingAgentMessagesUseCase(id) }
                }
                emitAll(handleSendPrompt(textMessage, isRetry = true))
            }
        }
        return flow { }
    }

    private fun handleStartNewSession(): Flow<PartialState> = flow {
        sessionContext.clear()
        completedTypingIds.clear()
        uncachedSessionId = newSessionId()
        emit(PartialState.SessionUpdated(lastEntity = null))
        emit(PartialState.ChatItemsReplaced(emptyList()))
        emit(PartialState.HistoryVisibilityChanged(false))
        // Open a brand-new conversation; the previous one stays in history if it was used.
        resolveNationalCode()?.let { code ->
            runCatching { pruneEmptyAgentSessionUseCase(code) }
            emit(PartialState.ActiveSessionChanged(startFreshSession(code)))
        }
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
            }.foldSuggestionsIntoReplies()
            emit(PartialState.NewChatItems(items))
            sendEvent(AgentEvent.ScrollToBottom)
        }
        emit(PartialState.Loading(false))
    }

    /**
     * Marks a bubble's reveal animation as done.
     *
     * The flag lives in state (not in the composable) because a LazyColumn disposes rows
     * that scroll out of view; composable-local "already animated" state is lost on
     * recycle and the typewriter would replay on old messages. Ported from old_Android's
     * `completedTypingIds` / `markTypingComplete`.
     */
    private fun handleTypingFinished(itemId: String): Flow<PartialState> = flow {
        if (!completedTypingIds.add(itemId)) return@flow
        val item = uiState.value.chatItems.firstOrNull { it.id == itemId } ?: return@flow
        if (!item.isTypingAnimating) return@flow
        emit(PartialState.UpdateChatItem(item.copy(isTypingAnimating = false)))
    }

    // ─── Conversation cache ───────────────────────────────────────────────────

    /**
     * Opens the assistant on an empty conversation, like old_Android did on startup.
     *
     * Earlier conversations are not auto-restored — they stay reachable from the history
     * sheet. A previous session that was opened but never used is pruned first so the
     * history does not fill up with empty chats.
     */
    private suspend fun startEmptySession(): List<PartialState> {
        val nationalCode = resolveNationalCode() ?: return emptyList()
        runCatching { pruneEmptyAgentSessionUseCase(nationalCode) }
        val id = startFreshSession(nationalCode)
        return listOf(PartialState.ActiveSessionChanged(id))
    }

    /** Creates a new conversation row and makes it the active one. @return its id. */
    private suspend fun startFreshSession(nationalCode: String): String {
        val id = newSessionId()
        val now = currentTimeMillis()
        cacheSessionId = id
        runCatching {
            startAgentSessionUseCase(
                AgentSessionDN(
                    id = id,
                    title = DEFAULT_SESSION_TITLE,
                    userNationalCode = nationalCode,
                    createdAt = now,
                    lastMessageAt = now
                )
            )
        }
        return id
    }

    // ─── History sheet ────────────────────────────────────────────────────────

    private fun handleOpenHistory(): Flow<PartialState> = flow {
        emit(PartialState.HistoryVisibilityChanged(true))
        emitAll(loadSessions())
    }

    /** Re-reads the saved conversation list; used on open and after delete/rename. */
    private fun loadSessions(): Flow<PartialState> = flow {
        val nationalCode = resolveNationalCode() ?: return@flow
        val sessions = runCatching { getAgentSessionsUseCase(nationalCode) }
            .getOrDefault(emptyList())
            // Hide the empty chat the user is sitting in — it is not history yet.
            .filter { it.messageCount > 0 || it.id != cacheSessionId }
        emit(PartialState.SessionsLoaded(sessions))
    }

    /**
     * Opens a saved conversation. Bubbles are rebuilt with `isTypingAnimating = false`
     * so old content does not replay the typewriter.
     */
    private fun handleLoadSession(sessionId: String): Flow<PartialState> = flow {
        val cached = runCatching { getCachedMessagesUseCase(sessionId) }.getOrDefault(emptyList())
        val items = cached.mapNotNull { row ->
            val content = ChatBubbleCodec.decode(row.contentType, row.contentJson)
                ?: return@mapNotNull null
            ChatItem(
                id = row.id,
                sender = if (row.sender == CachedSender.USER) ChatSender.User else ChatSender.Agent,
                content = content,
                isTypingAnimating = false
            )
        }.foldSuggestionsIntoReplies()

        // Prune the empty chat we are leaving behind, then switch over.
        resolveNationalCode()?.let { runCatching { pruneEmptyAgentSessionUseCase(it) } }
        cacheSessionId = sessionId
        sessionContext.clear()
        completedTypingIds.clear()

        val session = runCatching { getAgentSessionUseCase(sessionId) }.getOrNull()
        emit(PartialState.ChatItemsReplaced(items))
        // Restore the server conversation context so the reopened chat keeps its thread.
        emit(PartialState.SessionUpdated(session?.lastEntity, session?.state, session?.history))
        emit(PartialState.ActiveSessionChanged(sessionId))
        emit(PartialState.HistoryVisibilityChanged(false))
        sendEvent(AgentEvent.ScrollToBottom)
    }

    private fun handleDeleteSession(sessionId: String): Flow<PartialState> = flow {
        runCatching { deleteAgentSessionUseCase(sessionId) }
        // Deleting the conversation on screen leaves the user on a fresh empty one.
        if (sessionId == cacheSessionId) {
            emit(PartialState.ChatItemsReplaced(emptyList()))
            uncachedSessionId = newSessionId()
            emit(PartialState.SessionUpdated(lastEntity = null))
            resolveNationalCode()?.let { code ->
                emit(PartialState.ActiveSessionChanged(startFreshSession(code)))
            }
        }
        emitAll(loadSessions())
    }

    private fun handleRenameSession(sessionId: String, title: String): Flow<PartialState> = flow {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return@flow
        runCatching {
            updateAgentSessionUseCase.title(sessionId, trimmed.take(SESSION_TITLE_MAX_LENGTH))
        }
        emitAll(loadSessions())
    }

    /** Persists one bubble. Silently skips types the codec cannot serialize. */
    private suspend fun cacheBubble(
        item: ChatItem,
        status: CachedStatus = CachedStatus.SUCCESS
    ) {
        val sessionId = cacheSessionId ?: return
        val (type, payload) = ChatBubbleCodec.encode(item.content) ?: return
        runCatching {
            val order = saveCachedMessageUseCase.nextOrder(sessionId)
            saveCachedMessageUseCase(
                AgentCachedMessageDN(
                    id = item.id,
                    sessionId = sessionId,
                    sender = if (item.sender == ChatSender.User) CachedSender.USER else CachedSender.AGENT,
                    status = status,
                    contentType = type,
                    contentJson = payload,
                    voicePath = (item.content as? ChatBubbleContent.Voice)?.source,
                    timestamp = currentTimeMillis(),
                    messageOrder = order
                )
            )
            // The first user message names the conversation, like old_Android did.
            if (item.sender == ChatSender.User) {
                (item.content as? ChatBubbleContent.Text)?.message
                    ?.takeIf { it.isNotBlank() }
                    ?.let { title ->
                        updateAgentSessionUseCase.title(sessionId, title.take(SESSION_TITLE_MAX_LENGTH))
                    }
            }
        }
    }

    /** The native app's conversation id shape, which the server has always received. */
    private fun newSessionId() = "category_${UUID.randomUUID()}"

    private suspend fun resolveNationalCode(): String? {
        cachedNationalCode?.let { return it }
        val code = runCatching { getCurrentUserNationalCodeUseCase() }.getOrNull()
        return code?.takeIf { it.isNotBlank() }?.also { cachedNationalCode = it }
    }

    // ─── Voice ────────────────────────────────────────────────────────────────

    /** Records with a live amplitude/timer stream, auto-stopping at [VOICE_MAX_DURATION_MS]. */
    private fun handleStartVoiceRecording(): Flow<PartialState> = flow {
        if (uiState.value.isGenerating || isRecordingActive.value) return@flow
        voicePlayer.stop()
        val path = voiceRecorder.newRecordingPath()
        voiceRecorder.start(path)
        isRecordingActive.value = true

        val amps = mutableListOf<Int>()
        var elapsed = 0L
        while (isRecordingActive.value && elapsed < VOICE_MAX_DURATION_MS) {
            amps.add(voiceRecorder.amplitude.value)
            emit(
                PartialState.VoiceRecordingUpdated(
                    VoiceRecordingState(
                        amplitudes = amps.toList(),
                        elapsedMs = elapsed,
                        isNearLimit = elapsed >= VOICE_MAX_DURATION_MS - VOICE_NEAR_LIMIT_MS
                    )
                )
            )
            kotlinx.coroutines.delay(VOICE_AMPLITUDE_INTERVAL_MS)
            elapsed += VOICE_AMPLITUDE_INTERVAL_MS
        }

        voiceRecorder.stop()
        isRecordingActive.value = false
        val durationMs = elapsed.coerceAtMost(VOICE_MAX_DURATION_MS).toInt()
        loadedAudioPath = null
        emit(PartialState.VoiceRecordingUpdated(null))
        emit(
            PartialState.VoicePreviewUpdated(
                VoicePreviewState(filePath = path, durationMs = durationMs, amplitudes = amps.toList())
            )
        )
    }

    private fun handleDeleteVoiceRecording(): Flow<PartialState> = flow {
        isRecordingActive.value = false
        voicePlayer.stop()
        uiState.value.voicePreview?.filePath?.let { deleteFile(it) }
        loadedAudioPath = null
        emit(PartialState.VoiceRecordingUpdated(null))
        emit(PartialState.VoicePreviewUpdated(null))
    }

    private fun handleSendVoiceRecording(): Flow<PartialState> = flow {
        val preview = uiState.value.voicePreview ?: return@flow
        voicePlayer.stop()
        loadedAudioPath = null

        val userItem = ChatItem(
            id = UUID.randomUUID().toString(),
            sender = ChatSender.User,
            content = ChatBubbleContent.Voice(
                source = preview.filePath,
                durationMs = preview.durationMs.toLong(),
                amplitudes = preview.amplitudes
            )
        )
        emit(PartialState.VoicePreviewUpdated(null))
        emit(PartialState.NewChatItems(listOf(userItem)))
        cacheBubble(userItem)
        sendEvent(AgentEvent.ScrollToBottom)

        val bytes = readFileBytes(preview.filePath)
        val fileName = preview.filePath.substringAfterLast('/')
        emitAll(
            handleSendPrompt(
                message = "",
                voiceBytes = bytes,
                voiceFileName = fileName,
                addUserBubble = false
            )
        )
    }

    private fun handleTogglePreviewPlayback(): Flow<PartialState> {
        val preview = uiState.value.voicePreview ?: return emptyFlow()
        // Currently playing → pause; the running stream below observes it and stops.
        if (voicePlayer.isPlaying.value) {
            voicePlayer.playPause()
            playbackCoordinator.release(MediaPlaybackCoordinator.PREVIEW_OWNER)
            return emptyFlow()
        }
        return flow {
            startPlayback(preview.filePath, MediaPlaybackCoordinator.PREVIEW_OWNER)
            emitAll(
                streamPlayback { playing, pos, dur ->
                    uiState.value.voicePreview?.let {
                        PartialState.VoicePreviewUpdated(
                            it.copy(
                                isPlaying = playing,
                                positionMs = pos,
                                durationMs = if (dur > 0) dur else it.durationMs
                            )
                        )
                    }
                }
            )
        }
    }

    private fun handleToggleVoicePlayback(itemId: String, filePath: String): Flow<PartialState> {
        val owner = MediaPlaybackCoordinator.voiceOwner(itemId)
        if (uiState.value.playingVoiceId == itemId && voicePlayer.isPlaying.value) {
            voicePlayer.playPause()
            playbackCoordinator.release(owner)
            return emptyFlow()
        }
        return flow {
            startPlayback(filePath, owner)
            emitAll(
                streamPlayback { playing, pos, dur ->
                    // Keep the id while paused so the pinned player stays on screen and
                    // can be resumed; it clears when playback actually finishes.
                    val stillLoaded = playing || pos > 0
                    PartialState.VoicePlaybackUpdated(
                        itemId = if (stillLoaded) itemId else null,
                        positionMs = pos,
                        durationMs = dur,
                        isPlaying = playing
                    )
                }
            )
        }
    }

    /** Loads (if needed) and starts playback of [filePath] on the shared player. */
    private fun startPlayback(filePath: String, owner: String) {
        // Silences any video that is currently playing.
        playbackCoordinator.claim(owner)
        if (loadedAudioPath != filePath) {
            loadedAudioPath = filePath
            voicePlayer.load(
                filePath = filePath,
                onReady = { voicePlayer.playPause() },
                onError = { message ->
                    // Playback failures were silent, so a clip that would not open just
                    // looked like a dead button.
                    loadedAudioPath = null
                    playbackCoordinator.release(owner)
                    sendEvent(AgentEvent.ShowError(message))
                }
            )
        } else {
            voicePlayer.playPause()
        }
    }

    /**
     * Streams the player's state through [map] until playback has started and then
     * stopped (pause or completion), guaranteeing the flow terminates — one active
     * streamer at a time.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun streamPlayback(
        map: (playing: Boolean, positionMs: Int, durationMs: Int) -> PartialState?
    ): Flow<PartialState> {
        var started = false
        return combine(
            voicePlayer.isPlaying,
            voicePlayer.positionMs,
            voicePlayer.durationMs
        ) { playing, pos, dur -> Triple(playing, pos, dur) }
            .transformWhile { (playing, pos, dur) ->
                if (playing) started = true
                map(playing, pos, dur)?.let { emit(it) }
                !started || playing
            }
    }

    /**
     * Folds standalone suggestion bubbles into the reply they belong to.
     *
     * The dispatcher appends `SuggestedPrompts` as its own bubble, which would otherwise
     * become a separate chat row with its own timestamp and action footer. Attaching them
     * to the preceding agent item keeps one answer as one item. A suggestion arriving with
     * no reply before it is kept as its own item so nothing is silently dropped.
     */
    private fun List<ChatItem>.foldSuggestionsIntoReplies(): List<ChatItem> {
        val folded = mutableListOf<ChatItem>()
        forEach { item ->
            val prompts = (item.content as? ChatBubbleContent.SuggestedPrompts)?.prompts
            val previous = folded.lastOrNull()
            if (prompts != null && previous != null && previous.sender == ChatSender.Agent) {
                folded[folded.lastIndex] = previous.copy(
                    suggestedPrompts = previous.suggestedPrompts + prompts
                )
            } else {
                folded.add(item)
            }
        }
        return folded
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private suspend fun dispatchEntity(entity: AiEntityDN): List<ChatBubbleContent> {
        return when (val result = actionDispatcher.dispatch(entity, sessionContext)) {
            is AgentServiceResult.Success -> result.bubbles

            is AgentServiceResult.FeatureDisabled ->
                listOf(ChatBubbleContent.ServiceError(result.message ?: getString(Res.string.deep_link_feature_unavailable)))

            is AgentServiceResult.NoHandler -> emptyList()

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
                canSendVoice = partialState.canSendVoice,
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
                lastEntity = partialState.lastEntity,
                conversationState = partialState.state,
                conversationHistory = partialState.history,
            )

        is PartialState.InputModeChanged ->
            currentState.copy(inputMode = partialState.mode)

        is PartialState.GenerationCancelled ->
            currentState.copy(isGenerating = false, currentRequestId = null)

        is PartialState.Error ->
            currentState.copy(isGenerating = false)

        is PartialState.VoiceRecordingUpdated ->
            currentState.copy(voiceRecording = partialState.state)

        is PartialState.VoicePreviewUpdated ->
            currentState.copy(voicePreview = partialState.state)

        is PartialState.OfflineChanged ->
            currentState.copy(isOffline = partialState.isOffline)

        is PartialState.SessionsLoaded ->
            currentState.copy(sessions = partialState.sessions)

        is PartialState.HistoryVisibilityChanged ->
            currentState.copy(isHistoryVisible = partialState.isVisible)

        is PartialState.ActiveSessionChanged ->
            currentState.copy(activeSessionId = partialState.sessionId)

        is PartialState.ChatItemsReplaced ->
            currentState.copy(chatItems = partialState.items)

        is PartialState.VoicePlaybackUpdated ->
            currentState.copy(
                playingVoiceId = partialState.itemId,
                voicePlaybackPositionMs = partialState.positionMs,
                voicePlaybackDurationMs = partialState.durationMs,
                isVoicePlaying = partialState.isPlaying
            )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

    override fun onCleared() {
        isRecordingActive.value = false
        voiceRecorder.stop()
        voicePlayer.release()
        super.onCleared()
    }

    private companion object {
        /** Roughly how long each markdown block takes to appear; see MarkdownContent. */
        const val MARKDOWN_BLOCK_REVEAL_MS = 110L
        const val DEFAULT_SESSION_TITLE = "گفتگوی جدید"
        const val SESSION_TITLE_MAX_LENGTH = 60
    }
}

/** Wall-clock millis; kotlinx-datetime keeps this multiplatform. */
private fun currentTimeMillis(): Long =
    kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
