package com.tamin.taminhamrah.ui.aiAgent.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.data.entity.MenuModel
import com.tamin.taminhamrah.data.entity.ai.AiChatMessageEntity
import com.tamin.taminhamrah.data.entity.ai.MessageSender
import com.tamin.taminhamrah.data.entity.ai.MessageStatus
import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentRequest
import com.tamin.taminhamrah.data.repository.CommonRepository
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.ChatRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.AgentClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentGroupButtonModel
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import com.tamin.taminhamrah.data.repository.ai.model.AiTextModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel
import com.tamin.taminhamrah.ui.aiAgent.ActionDispatcher
import com.tamin.taminhamrah.ui.aiAgent.domain.AiEntity
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AiHistoryRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.CheckChatAllowedUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.SendLawPromptUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.SendPromptUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.SendVoiceServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.UpdateCategoryUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.GetServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.result.AgentResult
import com.tamin.taminhamrah.ui.aiAgent.mapper.toAiChatModels
import com.tamin.taminhamrah.ui.aiAgent.mapper.toChatMessages
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIEvent
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIEvent.BackPressed
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIEvent.ClickableClick
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIEvent.NavigateToEdit
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIEvent.NavigateToHistory
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIEvent.ShowMessage
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIIntent
import com.tamin.taminhamrah.ui.aiAgent.ui.AgentContracts.AIState
import com.tamin.taminhamrah.ui.aiAgent.ui.PromptType.Voice.Deleted
import com.tamin.taminhamrah.ui.aiAgent.ui.PromptType.Voice.Paused
import com.tamin.taminhamrah.ui.aiAgent.ui.PromptType.Voice.Playing
import com.tamin.taminhamrah.ui.aiAgent.ui.PromptType.Voice.Recording
import com.tamin.taminhamrah.ui.aiAgent.ui.PromptType.Voice.Reset
import com.tamin.taminhamrah.ui.aiAgent.ui.VoiceListState.ListProgressUpdating
import com.tamin.taminhamrah.ui.aiAgent.ui.VoiceListState.ListVoiceReset
import com.tamin.taminhamrah.ui.aiAgent.ui.VoiceListState.PauseListVoice
import com.tamin.taminhamrah.ui.aiAgent.ui.VoiceListState.PlayListVoice
import com.tamin.taminhamrah.ui.aiAgent.ui.VoiceListState.VoiceListIdle
import com.tamin.taminhamrah.ui.base.BaseViewModelMVI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.ArrayDeque
import java.util.UUID
import javax.inject.Inject

/** ── Three independent flags, each with a single clear responsibility ──────

isWaitingForResponse  → TRUE from the moment we call sendPrompt/sendVoice
until handlePromptResult fires (success OR error).
Prevents the queue from declaring "all done" while
the network round-trip is still in progress.

isActionProcessing    → TRUE while actionDispatcher.dispatch() is running.
Prevents a second service call from starting before
the first one finishes.

isLoading (in state)  → Pure UI flag that shows/hides the spinner.
NOT used for any queue-flow decisions.

 **/
@HiltViewModel
class AgentViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val actionDispatcher: ActionDispatcher,
    private val sendPromptUseCase: SendPromptUseCase,
    private val sendLawPromptUseCase: SendLawPromptUseCase,
    private val sendVoiceServiceUseCase: SendVoiceServiceUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val getServiceUseCase: GetServiceUseCase,
    private val checkChatAllowedUseCase: CheckChatAllowedUseCase,
    private val aiHistoryRepository: AiHistoryRepository,
    private val commonRepository: CommonRepository,
    private val serviceRepository: ServiceRepository
) : BaseViewModelMVI<AIIntent, AIState, AIEvent>() {

    private var job: Job? = null
    private var sessionId: String = UUID.randomUUID().toString()
    private var lastEntity: String = ""
    private val pendingActions = ArrayDeque<AiEntity>()
    private val pendingItems = ArrayDeque<AiChatModel>()
    private var isWaitingForResponse = false
    private var isActionProcessing = false
    private var isLawRequest = false
    private var promptType: PromptType = PromptType.Text
    private val defaultSessionTitle = "چت جدید"
    private var isSessionTitlePending = true
    private val completedTypingIds = mutableSetOf<String>()
    private var currentlyTypingItemId: String? = null
    private var messagesJob: Job? = null

    init {
        viewModelScope.launch {
            val lastSessionId = withContext(Dispatchers.IO) { chatRepository.getLastSessionId() }
            if (lastSessionId != null) {
                cleanupEmptySession(lastSessionId)
            }
            startNewSession()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Message observation
    // ─────────────────────────────────────────────────────────────────────────

    private fun observeMessages(id: String) {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch(Dispatchers.Main) {
            chatRepository.getMessages(id)
                .buffer(Channel.UNLIMITED)
                .collect { entities ->
                    if (entities.isNotEmpty()) {
                        entities.lastOrNull()?.lastEntity?.let { lastEntity = it }
                    }
                    val allDbModels = withContext(Dispatchers.Default) {
                        entities.flatMap { it.toAiChatModels() }
                    }
                    val currentItemsState = state.value.chatItems

                    if (currentItemsState.isEmpty()) {
                        handleInitialLoad(allDbModels)
                    } else {
                        handleIncrementalUpdate(allDbModels, currentItemsState)
                    }
                }
        }
    }

    private suspend fun startNewSession() {
        sessionId = createNewSessionId()
        isSessionTitlePending = true
        resetSessionState()
        addWelcomeMessage()
        observeMessages(sessionId)

    }

    private suspend fun cleanupEmptySession(id: String) {
        val category = try {
            aiHistoryRepository.getCategory(id)
        } catch (e: Exception) {
            null
        }
        if (category != null && category.messageCount == 0) {
            aiHistoryRepository.deleteCategory(id)
            withContext(Dispatchers.IO) {
                chatRepository.deleteSessionMessages(id)
            }
        }
    }

    private suspend fun loadSession(id: String) {
        sessionId = id
        resetSessionState()
        observeMessages(sessionId)
        isSessionTitlePending = false
        val category = try {
            aiHistoryRepository.getCategory(id)
        } catch (e: Exception) {
            null
        }
        if (category != null && category.title == defaultSessionTitle) {
            isSessionTitlePending = true
        }
        setState { copy(isLoading = false , chatType = PromptType.Text) }
    }

    private suspend fun createNewSessionId(): String {
        return withContext(Dispatchers.IO) {
            val nationalCode = commonRepository.getUserInfo().nationalCode
            if (nationalCode.isNullOrBlank() || nationalCode == "0") {
                UUID.randomUUID().toString()
            } else {
                aiHistoryRepository.createNewCategory(defaultSessionTitle, nationalCode)
            }
        }
    }

    private fun resetSessionState() {
        lastEntity = ""
        completedTypingIds.clear()
        currentlyTypingItemId = null
        pendingItems.clear()
        pendingActions.clear()
        isActionProcessing = false
        isWaitingForResponse = false
        job?.cancel()
        setState {
            copy(
                chatItems = emptyList(),
                isLoading = false,
                voiceListState = VoiceListIdle,
                filePath = null
            )
        }
    }

    private fun handleInitialLoad(allDbModels: List<AiChatModel>) {
        val historyModels = allDbModels.map { model ->
            if (model is TypingAnimatable) {
                model.isTypingComplete = true
                model.shouldStartTyping = false
                completedTypingIds.add(model.id)
            }
            model
        }
        if (historyModels.isNotEmpty()) {
            setState { copy(chatItems = historyModels) }
        }
    }

    private fun handleIncrementalUpdate(
        allDbModels: List<AiChatModel>,
        currentItemsState: List<AiChatModel>
    ) {
        val currentIds = currentItemsState.map { it.id }.toSet()
        val dbModelsMap = allDbModels.associateBy { it.id }

        val updatedList = currentItemsState.map { currentItem ->
            if (currentItem.id == currentlyTypingItemId) return@map currentItem

            val dbModel = dbModelsMap[currentItem.id] ?: return@map currentItem
            var finalModel = dbModel
            if (dbModel is TypingAnimatable && currentItem is TypingAnimatable) {
                dbModel.isTypingComplete = currentItem.isTypingComplete
                dbModel.shouldStartTyping = currentItem.shouldStartTyping
                dbModel.skipTyping = currentItem.skipTyping
            }
            if (dbModel is VoiceModel && currentItem is VoiceModel) {
                dbModel.isPlaying = currentItem.isPlaying
                dbModel.playerProgress = currentItem.playerProgress
            }
            if (dbModel is AiGenerativeModel && currentItem is AiGenerativeModel) {
                dbModel.isExpanded = currentItem.isExpanded
                if (dbModel.schema.currentStep == currentItem.schema.currentStep) {
                    finalModel = dbModel.copy(
                        schema = dbModel.schema.copy(
                            isLoading = currentItem.schema.isLoading,
                            errorMessage = currentItem.schema.errorMessage ?: dbModel.schema.errorMessage,
                            message = currentItem.schema.message ?: dbModel.schema.message
                        )
                    )
                }
            }
            finalModel
        }.toMutableList()

        val pendingIds = pendingItems.map { it.id }.toSet()
        val newModels = allDbModels.filter { model ->
            model.id != currentlyTypingItemId && !currentIds.contains(model.id) && !pendingIds.contains(model.id)
        }

        if (updatedList != currentItemsState) {
            setState { copy(chatItems = updatedList) }
        }

        if (newModels.isNotEmpty()) {
            newModels.forEach { model ->
                if (model is TypingAnimatable && shouldSkipTyping(model)) {
                    model.skipTyping = true
                }
                pendingItems.add(model)
            }
            tryAdvanceItemQueue()
        }
    }

    private fun shouldSkipTyping(model: AiChatModel): Boolean {
        return when {
            model is AiTextModel && model.isUserMessage -> true
            else -> false
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Item (typing-animation) queue
    // ─────────────────────────────────────────────────────────────────────────

    private fun tryAdvanceItemQueue() {
        if (currentlyTypingItemId != null) return

        if (pendingItems.isEmpty()) {
            tryAdvanceActionQueue()
            return
        }

        val nextItem = pendingItems.removeFirst()

        if (nextItem is TypingAnimatable && !nextItem.skipTyping) {
            nextItem.shouldStartTyping = true
            nextItem.isTypingComplete = false
            currentlyTypingItemId = nextItem.id
        } else if (nextItem is TypingAnimatable) {
            nextItem.isTypingComplete = true
            nextItem.shouldStartTyping = false
        }

        val currentList = state.value.chatItems.toMutableList()
        currentList.add(nextItem)
        setState { copy(chatItems = currentList) }

        if (nextItem !is TypingAnimatable || nextItem.isTypingComplete) {
            tryAdvanceItemQueue()
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Action (service-dispatch) queue
    // ─────────────────────────────────────────────────────────────────────────

    private fun tryAdvanceActionQueue() {
        if (isActionProcessing) return
        if (isWaitingForResponse) return

        if (pendingActions.isEmpty()) {
            if (pendingItems.isEmpty() && currentlyTypingItemId == null) {
                Timber.tag("AgentViewModel").d("tryAdvanceActionQueue")
                handleStopChatMessage()
            }
            return
        }

        val entity = pendingActions.removeFirst()
        if (entity.action == null) {
            viewModelScope.launch(Dispatchers.Main) { tryAdvanceActionQueue() }
            return
        }

        isActionProcessing = true
        job = viewModelScope.launch {
            val params = ServiceParams(entity.action, entity.payload, entity.data, entity.message)
            val result = actionDispatcher.dispatch(entity.action, params)
            handleDispatchResult(result)
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Network result handlers
    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun sendPrompt(params: AgentRequest) {
        val result = if (isLawRequest) {
            sendLawPromptUseCase(params)
        } else {
            sendPromptUseCase(params)
        }
        handlePromptResult(result)
    }

    private suspend fun sendVoice(params: AgentRequest, voicePath: String) {
        val result = sendVoiceServiceUseCase(voicePath = voicePath, params = params)
        handlePromptResult(result)
    }

    private suspend fun handlePromptResult(result: AgentResult?) {
        when (result) {
            is AgentResult.Success -> {
                // Update lastEntity from server but keep our local sessionId for database consistency
                result.data.lastEntity?.let { this@AgentViewModel.lastEntity = it }

                // Insert top-level message if present and meaningful
                if (!result.data.message.isNullOrBlank() && result.data.message.lowercase() != "ok") {
                    chatRepository.insertMessage(
                        AiChatMessageEntity(
                            message = result.data.message,
                            sender = MessageSender.AI,
                            status = MessageStatus.SUCCESS,
                            sessionId = sessionId,
                            lastEntity = lastEntity
                        )
                    )
                }

                result.data.entities.forEach { entity ->
                    // ONLY save message here if it has NO action. 
                    // If it HAS an action, it will be saved via handleDispatchResult to avoid duplicates.
                    if (entity.action == null && !entity.message.isNullOrBlank()) {
                        chatRepository.insertMessage(
                            AiChatMessageEntity(
                                message = entity.message,
                                sender = MessageSender.AI,
                                status = MessageStatus.SUCCESS,
                                sessionId = sessionId,
                                lastEntity = lastEntity
                            )
                        )
                    }
                }

                pendingActions.addAll(result.data.entities)
                updateSessionAfterAiMessage()
                withContext(Dispatchers.Main) {
                    isWaitingForResponse = false
                    tryAdvanceActionQueue()
                }
            }

            is AgentResult.Error -> {
                if (result.errorMessage.contains("StandaloneCoroutine was cancelled", ignoreCase = true) ||
                    result.errorMessage.contains("Job was cancelled", ignoreCase = true)) {
                    isWaitingForResponse = false
                    return
                }
                setState { copy(isLoading = false, chatType = PromptType.StopGenerating) }
                chatRepository.insertMessage(
                    AiChatMessageEntity(
                        message = result.errorMessage,
                        sender = MessageSender.AI,
                        status = MessageStatus.FAILED,
                        sessionId = sessionId,
                        lastEntity = lastEntity
                    )
                )
                updateSessionAfterAiMessage()
                isWaitingForResponse = false
            }

            else -> {
                withContext(Dispatchers.Main) {
                    Timber.tag("AgentViewModel").d("handlePromptResult")
                    handleStopChatMessage()
                }
            }
        }
    }

    private suspend fun handleDispatchResult(result: ServiceResult?) {
        when (result) {
            is ServiceResult.Success -> {
                chatRepository.insertMessage(
                    AiChatMessageEntity(
                        message = null,
                        serviceResponses = result.data,
                        sender = MessageSender.AI,
                        status = MessageStatus.SUCCESS,
                        sessionId = sessionId,
                        lastEntity = lastEntity
                    )
                )
                updateSessionAfterAiMessage()
                setState { copy(isLoading = false) }
                withContext(Dispatchers.Main) {
                    tryAdvanceItemQueue()
                    isActionProcessing = false
                }
            }

            is ServiceResult.Error -> {
                if (result.errorMessage.contains("StandaloneCoroutine was cancelled", ignoreCase = true) ||
                    result.errorMessage.contains("Job was cancelled", ignoreCase = true)) {
                    setState { copy(isLoading = false) }
                    isActionProcessing = false
                    return
                }
                pendingActions.clear()
                chatRepository.insertMessage(
                    AiChatMessageEntity(
                        message = result.errorMessage,
                        sender = MessageSender.AI,
                        status = MessageStatus.FAILED,
                        sessionId = sessionId,
                        lastEntity = lastEntity
                    )
                )
                updateSessionAfterAiMessage()
                setState { copy(isLoading = false) }
                isActionProcessing = false
            }

            else -> {
                setState { copy(isLoading = false) }
                withContext(Dispatchers.Main) {
                    tryAdvanceItemQueue()
                    isActionProcessing = false
                }
            }
        }
    }

    private fun handleStopChatMessage(forceCancel: Boolean = false) {

        Timber.tag("AgentViewModel").d("handleStopChatMessage")
        isActionProcessing = false
        isWaitingForResponse = false
        currentlyTypingItemId = null
        if (forceCancel || !state.value.isLoading) {
            job?.cancel()
        }
        pendingActions.clear()
        pendingItems.clear()

        if (forceCancel) {
            viewModelScope.launch(Dispatchers.IO) {
                chatRepository.deletePendingAiMessages(sessionId)
            }
        }

        state.value.chatItems.forEach { message ->
            if (message is TypingAnimatable && message.shouldStartTyping && !message.isTypingComplete) {
                message.isTypingComplete = true
                message.shouldStartTyping = false
            }
        }

        setState {
            copy(
                chatItems = chatItems,
                chatType = PromptType.StopGenerating,
                isLoading = false
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Intent handling
    // ─────────────────────────────────────────────────────────────────────────

    override fun createInitialState(): AIState = AIState()

    override suspend fun handleIntent(intent: AIIntent) {
        when (intent) {

            is AIIntent.SendPrompt.MessageType -> {
                chatRepository.insertMessage(
                    AiChatMessageEntity(
                        message = intent.message,
                        sender = MessageSender.USER,
                        status = MessageStatus.SUCCESS,
                        sessionId = sessionId,
                        lastEntity = lastEntity
                    )
                )
                updateSessionAfterUserMessage(intent.message)
                isWaitingForResponse = true
                setState { copy(chatType = PromptType.Generating, isLoading = true) }
                job?.cancel()
                job = viewModelScope.launch {
                    sendPrompt(createSendPromptParams(intent.message))
                }
            }

            is AIIntent.SendPrompt.VoiceType -> {
                state.value.filePath?.let { voice ->
                    chatRepository.insertMessage(
                        AiChatMessageEntity(
                            message = "پیام صوتی",
                            voicePath = voice,
                            sender = MessageSender.USER,
                            status = MessageStatus.SUCCESS,
                            sessionId = sessionId,
                            lastEntity = lastEntity
                        )
                    )
                    updateSessionAfterUserMessage("پیام صوتی")
                    isWaitingForResponse = true
                    setState { copy(chatType = PromptType.Generating, isLoading = true) }
                    currentlyTypingItemId = null
                    job?.cancel()
                    job = viewModelScope.launch {
                        sendVoice(createSendPromptParams(""), voice)
                    }
                }
            }

            is AIIntent.FormAction -> {
                handleFormAction(intent.actionId, intent.data, intent.position, intent.messageId)
            }

            is AIIntent.UploadFormDocument -> {
                handleUploadFormDocument(intent.body, intent.fieldId, intent.docTypeId, intent.position, intent.messageId)
            }

            is AIIntent.UpdateFormData -> {
                handleUpdateFormData(intent.data, intent.position, intent.messageId)
            }

            is AIIntent.SetMessageType -> {
                promptType = intent.type
                setState { copy(chatType = promptType) }
            }

            is AIIntent.ToggleExpand ->
                handleToggleExpand(intent.position, intent.isExpanded)

            is AIIntent.OnTypingComplete -> {
                if (intent.item.id == currentlyTypingItemId) {
                    currentlyTypingItemId = null
                    markTypingComplete(intent.item.id)
                    setState { copy(chatItems = state.value.chatItems) }
                    tryAdvanceItemQueue()
                } else {
                    markTypingComplete(intent.item.id)
                }
            }

            is AIIntent.UpdateRequestState -> isLawRequest = intent.isLawRequest

            is AIIntent.UpdateCategoryItem -> handleUpdateCategory(intent.item)

            is AIIntent.SetCategoryItem -> sendEvent(NavigateToEdit(intent.item))

            is AIIntent.OnRetryClick -> handleRetryClick()

            is AIIntent.OnMicClick -> {
                val items = state.value.chatItems.toMutableList()
                val idx = items.indexOfFirst { it is VoiceModel && it.isPlaying }
                if (idx != -1) {
                    val cur = items[idx] as VoiceModel
                    items[idx] = cur.copy(isPlaying = false, playerProgress = -1.0f)
                }
                setState {
                    copy(
                        chatItems = items,
                        chatType = Recording,
                        filePath = intent.filePath,
                        voiceListState = ListVoiceReset
                    )
                }
            }

            is AIIntent.OnDeletedVoice -> handleDeleteVoice()

            is AIIntent.OnPlayVoice -> {
                if (state.value.voiceListState is ListProgressUpdating ||
                    state.value.voiceListState is PlayListVoice
                ) {
                    val items = state.value.chatItems.toMutableList()
                    val idx = items.indexOfFirst { it is VoiceModel && it.isPlaying }
                    if (idx != -1) {
                        val cur = items[idx] as VoiceModel
                        items[idx] = cur.copy(isPlaying = false, playerProgress = -1.0f)
                        setState { copy(chatItems = items, voiceListState = ListVoiceReset) }
                    }
                }
                setState { copy(chatType = Playing, voiceListState = VoiceListIdle) }
            }

            is AIIntent.ResetVoice -> setState { copy(chatType = Reset) }

            is AIIntent.OnPauseVoiceInList -> {
                val items = state.value.chatItems.toMutableList()
                val oldItem = items.getOrNull(intent.itemPosition) as? VoiceModel
                if (oldItem != null) {
                    val newItem = oldItem.copy(isPlaying = false, playerProgress = -1.0f)
                    items[intent.itemPosition] = newItem
                    setState {
                        copy(
                            chatItems = items,
                            voiceListState = PauseListVoice(newItem, intent.itemPosition)
                        )
                    }
                }
            }

            is AIIntent.OnPlayVoiceInList -> {
                try {
                    if (state.value.chatType == Recording) return
                    if (state.value.chatType == Playing) setState { copy(chatType = Reset) }

                    val items = state.value.chatItems.toMutableList()
                    val curIdx = items.indexOfFirst { it is VoiceModel && it.isPlaying }
                    if (curIdx != -1) {
                        val cur = items[curIdx] as VoiceModel
                        items[curIdx] = cur.copy(isPlaying = false, playerProgress = -1.0f)
                    }
                    val targetItem = items.getOrNull(intent.itemPosition) as? VoiceModel
                    targetItem?.let {
                        val newItem = it.copy(isPlaying = true)
                        items[intent.itemPosition] = newItem
                        setState {
                            copy(
                                chatItems = items,
                                voiceListState = PlayListVoice(newItem, intent.itemPosition)
                            )
                        }
                    }
                } catch (_: Exception) {
                }
            }

            is AIIntent.VoiceListProgressUpdated -> {
                val items = state.value.chatItems.toMutableList()
                val oldItem = items.getOrNull(intent.itemPosition) as? VoiceModel
                if (oldItem != null) {
                    val newItem = oldItem.copy(playerProgress = intent.playingPosition.toFloat())
                    items[intent.itemPosition] = newItem
                    setState {
                        copy(
                            chatItems = items,
                            voiceListState = ListProgressUpdating(newItem, intent.itemPosition)
                        )
                    }
                }
            }

            is AIIntent.ResetListVoice -> {
                val items = state.value.chatItems.toMutableList()
                val oldItem = items.getOrNull(intent.itemPosition) as? VoiceModel
                if (oldItem != null) {
                    val newItem = oldItem.copy(isPlaying = false)
                    items[intent.itemPosition] = newItem
                    setState { copy(chatItems = items, voiceListState = ListVoiceReset) }
                }
            }

            is AIIntent.OnPauseVoice -> setState { copy(chatType = Paused) }

            is AIIntent.StartNewChat -> {
                cleanupEmptySession(sessionId)
                startNewSession()
            }

            is AIIntent.LoadSession -> {
                loadSession(intent.sessionId)
            }

            is AIIntent.OnClickableClick -> {
                when {
                    intent.itemAction is AgentActionContent.EditMobile -> {
                        if (!isWaitingForResponse) {
                            val position = state.value.chatItems.indexOfLast {
                                (it is AgentClickableModel && it.actionContent is AgentActionContent.EditMobile) ||
                                        (it is AgentGroupButtonModel && it.prompts.any { p -> p.action is AgentActionContent.EditMobile })
                            }
                            executeServiceAction(
                                ServiceNameEnum.EDIT_PHONE_NUMBER_GET,
                                emptyMap(),
                                position
                            )
                        }
                    }

                    intent.itemAction is AgentActionContent.CancelDependent -> {
                        if (!isWaitingForResponse) {
                            val position = state.value.chatItems.indexOfLast {
                                (it is AgentClickableModel && it.actionContent is AgentActionContent.CancelDependent) ||
                                        (it is AgentGroupButtonModel && it.prompts.any { p -> p.action is AgentActionContent.CancelDependent })
                            }
                            executeServiceAction(
                                ServiceNameEnum.DEPENDENT_CANCELLATION_GET,
                                emptyMap(),
                                position
                            )
                        }
                    }

                    intent.itemAction is AgentActionContent.AddAccountNumber -> {
                        if (!isWaitingForResponse) {
                            val position = state.value.chatItems.indexOfLast {
                                (it is AgentClickableModel && it.actionContent is AgentActionContent.AddAccountNumber) ||
                                        (it is AgentGroupButtonModel && it.prompts.any { p -> p.action is AgentActionContent.AddAccountNumber })
                            }
                            executeServiceAction(
                                ServiceNameEnum.EDIT_BANK_ACCOUNT_GET,
                                emptyMap(),
                                position
                            )
                        }
                    }

                    intent.itemAction is AgentActionContent.WeddingPresent -> {
                        if (!isWaitingForResponse) {
                            val position = state.value.chatItems.indexOfLast {
                                (it is AgentClickableModel && it.actionContent is AgentActionContent.WeddingPresent) ||
                                        (it is AgentGroupButtonModel && it.prompts.any { p -> p.action is AgentActionContent.WeddingPresent })
                            }
                            executeServiceAction(
                                ServiceNameEnum.WEDDING_PRESENT_GET,
                                emptyMap(),
                                position
                            )
                        }
                    }

                    intent.itemAction is AgentActionContent.InquiryEducation -> {
                        if (!isWaitingForResponse) {
                            val position = state.value.chatItems.indexOfLast {
                                (it is AgentClickableModel && it.actionContent is AgentActionContent.InquiryEducation) ||
                                        (it is AgentGroupButtonModel && it.prompts.any { p -> p.action is AgentActionContent.InquiryEducation })
                            }
                            executeServiceAction(
                                ServiceNameEnum.EXTEND_EDUCATION_GET,
                                emptyMap(),
                                position
                            )
                        }
                    }

                    intent.itemAction is AgentActionContent.OccurrenceReportGet -> {
                        if (!isWaitingForResponse) {
                            val position = state.value.chatItems.indexOfLast {
                                (it is AgentClickableModel && it.actionContent is AgentActionContent.OccurrenceReportGet) ||
                                        (it is AgentGroupButtonModel && it.prompts.any { p -> p.action is AgentActionContent.OccurrenceReportGet })
                            }
                            executeServiceAction(
                                ServiceNameEnum.OCCURRENCE_REPORT_GET,
                                emptyMap(),
                                position
                            )
                        }
                    }

                    else -> sendEvent(ClickableClick(intent.itemAction))
                }
            }

            is AIIntent.OnBackPress -> sendEvent(BackPressed)

            is AIIntent.OpenBottomSheetHistory ->
                sendEvent(NavigateToHistory(isLawRequest))

            is AIIntent.StopMessageText -> {
                Timber.tag("AgentViewModel").d("AIIntent.StopMessageText")
                handleStopChatMessage(true)
            }

            is AIIntent.StopMessageVoice ->
                setState { copy(chatType = PromptType.Voice.Stopped) }

            AIIntent.VoiceIdle ->
                setState { copy(chatType = PromptType.Voice.Idle) }

            AIIntent.VoiceListIdle ->
                setState { copy(voiceListState = VoiceListIdle) }

            AIIntent.SkipTypingAnimation -> {
                currentlyTypingItemId = null

                val updatedChatItems = state.value.chatItems.toMutableList()
                var stateChanged = false
                updatedChatItems.lastOrNull()?.let { lastItem ->
                    if (lastItem is TypingAnimatable && !lastItem.isTypingComplete) {
                        lastItem.isTypingComplete = true
                        lastItem.shouldStartTyping = false
                        lastItem.skipTyping = true
                        stateChanged = true
                    }
                }
                if (stateChanged) setState { copy(chatItems = updatedChatItems) }

                when {
                    pendingItems.isNotEmpty() -> tryAdvanceItemQueue()
                    pendingActions.isNotEmpty() -> tryAdvanceActionQueue()
                    state.value.chatType == PromptType.Generating && !isWaitingForResponse -> {
                        Timber.tag("AgentViewModel").d("PromptType.Generating && !isWaitingForResponse")
                        handleStopChatMessage()
                    }
                }
            }

            is AIIntent.DisplayReportMode -> {
                handleShowReportAction(intent.mode, intent.title )
            }
        }
    }

    private fun handleShowReportAction(mode: String, title: String? ) {
        val payload = mutableMapOf<String, Any?>()
        payload["VIEW_MODE"] = mode
        handleShowReportService(payload, title)
    }

    private fun handleFormAction(actionId: String, data: Map<String, Any?>, position: Int, messageId: String?) {
        val serviceEnum = ServiceNameEnum.fromString(actionId)
        if (serviceEnum != null && serviceEnum != ServiceNameEnum.UN_AVAILABLE_SERVICE) {
            executeServiceAction(serviceEnum, data, position, messageId)
        }
    }

    private fun executeServiceAction(
        serviceEnum: ServiceNameEnum,
        payload: Map<String, Any?>,
        position: Int,
        messageId: String? = null
    ) {
        if (isWaitingForResponse && position == -1 && messageId == null) return

        isWaitingForResponse = true
        setState { copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val service = getServiceUseCase(serviceEnum)
                val params = ServiceParams(
                    serviceName = serviceEnum,
                    payload = payload
                )
                val result = service?.execute(params)
                val chatMessages = result?.toChatMessages()

                if (!chatMessages.isNullOrEmpty()) {
                    val currentList = state.value.chatItems.toMutableList()
                    val targetIndex = resolveTargetIndex(currentList, position, messageId)
                    if (currentList.isNotEmpty() && targetIndex >= 0 && targetIndex < currentList.size) {
                        val previousItem = currentList[targetIndex]
                        val previousTypingItem = previousItem as? TypingAnimatable
                        val updatedItem = chatMessages[0]
                        if (updatedItem is TypingAnimatable && previousTypingItem != null) {
                            updatedItem.id = previousTypingItem.id
                            updatedItem.isTypingComplete = previousTypingItem.isTypingComplete
                            updatedItem.shouldStartTyping = previousTypingItem.shouldStartTyping
                            updatedItem.skipTyping = previousTypingItem.skipTyping
                        }
                        currentList[targetIndex] = updatedItem
                        setState { copy(chatItems = currentList) }
                        if (result is ServiceResult.Success && result.data.isNotEmpty()) {
                            persistUpdatedServiceResponse(previousTypingItem?.id, result.data[0])
                        }
                    } else {
                        withContext(Dispatchers.IO) {
                            val responses = (result as? ServiceResult.Success)?.data
                            chatRepository.insertMessage(
                                AiChatMessageEntity(
                                    serviceResponses = responses,
                                    sender = MessageSender.AI,
                                    status = MessageStatus.SUCCESS,
                                    sessionId = sessionId,
                                    lastEntity = lastEntity,
                                    message = null,
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
            } finally {
                isWaitingForResponse = false
                setState { copy(isLoading = false) }
            }
        }
    }

    /**
     * Uploads a document picked for a FILE_UPLOAD form field, then merges the returned
     * GUID into the form item's payload so the (re-rendered) form shows the new file and
     * the submit use case can include it. The form item at [position] is updated in place.
     */
    private fun handleUploadFormDocument(
        body: okhttp3.MultipartBody.Part,
        fieldId: String,
        docTypeId: String?,
        position: Int,
        messageId: String?
    ) {
        val list = state.value.chatItems
        val targetIndex = resolveTargetIndex(list, position, messageId)
        updateGenerativeItemLoading(targetIndex, true)
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    serviceRepository.uploadOccurrenceImage(body)
                }
                if (response.isSuccess && !response.guid.isNullOrEmpty()) {
                    mergeUploadedGuid(targetIndex, fieldId, docTypeId, response.guid)
                } else {
                    updateGenerativeItemLoading(targetIndex, false)
                }
            } catch (e: Exception) {
                updateGenerativeItemLoading(targetIndex, false)
            }
        }
    }

    private fun updateGenerativeItemLoading(targetIndex: Int, isLoading: Boolean) {
        val list = state.value.chatItems.toMutableList()
        val item = list.getOrNull(targetIndex) as? AiGenerativeModel ?: return
        list[targetIndex] = item.copy(schema = item.schema.copy(isLoading = isLoading))
        setState { copy(chatItems = list) }
    }

    private fun mergeUploadedGuid(targetIndex: Int, fieldId: String, docTypeId: String?, guid: String) {
        val list = state.value.chatItems.toMutableList()
        val item = list.getOrNull(targetIndex) as? AiGenerativeModel ?: return
        // Encode each document as "<docTypeId>:<guid>" so the submit mapper can recover
        // the user-selected document type; falls back to a bare guid when no type was chosen.
        val entry = if (docTypeId.isNullOrBlank()) guid else "$docTypeId:$guid"
        val existing = item.data?.get(fieldId).orEmpty()
        val merged = if (existing.isBlank()) entry else "$existing,$entry"
        val newData = (item.data ?: emptyMap()).toMutableMap()
        newData[fieldId] = merged
        val updatedSchema = item.schema.copy(isLoading = false)
        val updatedItem = item.copy(
            data = newData,
            schema = updatedSchema
        )
        list[targetIndex] = updatedItem
        setState { copy(chatItems = list) }

        viewModelScope.launch {
            try {
                val serviceResponse = com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse(
                    action = ServiceNameEnum.fromString(updatedItem.schema.key),
                    title = updatedItem.schema.steps.firstOrNull { it.index == updatedItem.schema.currentStep }?.title,
                    data = com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData.GenerativeForm(
                        schema = updatedSchema,
                        payload = newData.mapKeys { it.key }
                    )
                )
                persistUpdatedServiceResponse(updatedItem.id, serviceResponse)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    private fun handleUpdateFormData(data: Map<String, Any?>, position: Int, messageId: String?) {
        val list = state.value.chatItems.toMutableList()
        val targetIndex = resolveTargetIndex(list, position, messageId)
        val item = list.getOrNull(targetIndex) as? AiGenerativeModel ?: return
        val convertedData = data.mapKeys { it.key as String? }.mapValues { it.value?.toString() }
        val updatedSchema = item.schema.copy(isLoading = item.schema.isLoading)
        val updatedItem = item.copy(
            data = convertedData,
            schema = updatedSchema
        )
        list[targetIndex] = updatedItem
        setState { copy(chatItems = list) }

        viewModelScope.launch {
            try {
                val serviceResponse = com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse(
                    action = ServiceNameEnum.fromString(updatedItem.schema.key),
                    title = updatedItem.schema.steps.firstOrNull { it.index == updatedItem.schema.currentStep }?.title,
                    data = com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData.GenerativeForm(
                        schema = updatedSchema,
                        payload = convertedData
                    )
                )
                persistUpdatedServiceResponse(updatedItem.id, serviceResponse)
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun getDocumentTypes(onResult: (List<MenuModel>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    serviceRepository.getDocumentType(null)
                }
                val data = response.data
                val list = data?.list
                if (response.isSuccess && list != null) {
                    val menuModels = list.map {
                        MenuModel(
                            id = it.docTypeId,
                            title = it.docDesc
                        )
                    }
                    onResult(menuModels)
                } else {
                    onResult(emptyList())
                }
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    private fun resolveTargetIndex(list: List<AiChatModel>, position: Int, messageId: String?): Int {
        if (!messageId.isNullOrBlank()) {
            val idx = list.indexOfFirst { it.id == messageId }
            if (idx != -1) return idx
        }
        if (position >= 0 && position < list.size) {
            return position
        }
        return -1
    }

    private suspend fun persistUpdatedServiceResponse(
        typingItemId: String?,
        updatedResponse: com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
    ) {
        val idParts = typingItemId?.split("_") ?: return
        val entityId = idParts.getOrNull(0)?.toLongOrNull() ?: return
        val responseIndex = idParts.getOrNull(1)?.toIntOrNull() ?: return

        val entity = chatRepository.getMessageById(entityId) ?: return
        val existingResponses = entity.serviceResponses ?: return
        if (responseIndex !in existingResponses.indices) return

        val mutableResponses = existingResponses.toMutableList()
        mutableResponses[responseIndex] = updatedResponse
        chatRepository.updateMessage(entity.copy(serviceResponses = mutableResponses))
    }

    private suspend fun processNextItem() {
        if (currentlyTypingItemId != null) return

        if (pendingItems.isEmpty()) {
            processNextAction()
            return
        }

        val nextItem = pendingItems.removeFirst()

        if (nextItem is TypingAnimatable) {
            if (!nextItem.skipTyping) {
                nextItem.shouldStartTyping = true
                nextItem.isTypingComplete = false
                currentlyTypingItemId = nextItem.id
            } else {
                nextItem.isTypingComplete = true
                nextItem.shouldStartTyping = false
            }
        }

        setState { copy(chatItems = this.chatItems + nextItem) }

        if (nextItem !is TypingAnimatable || nextItem.isTypingComplete) {
            delay(50)
            processNextItem()
        }
    }

    private fun processNextAction() {
        if (isActionProcessing || pendingItems.isNotEmpty()) {
            return
        }

        if (pendingActions.isEmpty()) {
            Timber.tag("AgentViewModel").d("processNextAction")
            handleStopChatMessage()
            return
        }

        val entity = pendingActions.removeFirst()
        if (entity.action == null) {
            processNextAction()
            return
        }
        setState { copy(chatType = PromptType.Generating) }
        isActionProcessing = true
        job = viewModelScope.launch {
            setState { copy(isLoading = true) }
            val params = ServiceParams(
                entity.action,
                entity.payload,
                entity.data,
                entity.message
            )
            handleAction(entity.action, params)
        }
    }

    private suspend fun handleAction(serviceName: ServiceNameEnum?, params: ServiceParams) {
        val result = actionDispatcher.dispatch(serviceName, params)
        handleDispatchResult(result)
    }

    private fun handleShowReportService(payload: Map<String, Any?>, title: String?) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val serviceEnum = ServiceNameEnum.DASTMOZD_INFOS_SUM_TOTAL
            val service = getServiceUseCase(serviceEnum)
            val params = ServiceParams(
                serviceName = serviceEnum,
                payload = payload,
                message = title
            )
            val result = service?.execute(params)
            val chatMessages = result?.toChatMessages()
            setState { copy(isLoading = false) }
            if (!chatMessages.isNullOrEmpty()) {
                pendingItems.addAll(chatMessages)
                tryAdvanceItemQueue()
            }
        }
    }

    private fun addWelcomeMessage() {
        viewModelScope.launch {
            setState { copy(isLoading = true , chatType = PromptType.Generating) }
            try {
                val chatAllowedResult = checkChatAllowedUseCase()
                Timber.tag("AgentViewModel").d(chatAllowedResult.toString())
                val isVoiceEnabled = chatAllowedResult.getOrNull()?.canSendVoice ?: true
                setState { copy(isVoiceEnabled = isVoiceEnabled) }

                val welcomeText = """
                    ${commonRepository.getUserInfo().fullName} با سلام!
                    با چت بات میتونی خدمات مختلف اپ را سریع و راحت پیدا و استفاده کنی، بدون اینکه به صفحات مختلف بری.
                """.trimIndent()

                withContext(Dispatchers.IO) {
                    chatRepository.insertMessage(
                        AiChatMessageEntity(
                            message = welcomeText,
                            sender = MessageSender.AI,
                            status = MessageStatus.SUCCESS,
                            sessionId = sessionId,
                            lastEntity = null
                        )
                    )

                    if (chatAllowedResult.isFailure) {
                        val errorMessage = chatAllowedResult.exceptionOrNull()?.message
                        if (!errorMessage.isNullOrBlank()) {
                            chatRepository.insertMessage(
                                AiChatMessageEntity(
                                    message = errorMessage,
                                    sender = MessageSender.AI,
                                    status = MessageStatus.FAILED,
                                    sessionId = sessionId,
                                    lastEntity = null
                                )
                            )
                        }
                    }
                }
            } finally {
                setState { copy(isLoading = false , chatType = PromptType.Text) }
            }
        }
    }


    private fun createSendPromptParams(prompt: String) = AgentRequest(
        prompt = prompt,
        sessionId = sessionId,
        lastEntity = lastEntity
    )

    private fun markTypingComplete(id: String) {
        completedTypingIds.add(id)
        state.value.chatItems.find { it is TypingAnimatable && it.id == id }
            ?.let { item ->
                if (item is TypingAnimatable) {
                    item.isTypingComplete = true
                    item.shouldStartTyping = false
                }
            }
    }

    private fun handleToggleExpand(position: Int, isExpanded: Boolean) {
        val currentList = state.value.chatItems.toMutableList()
        if (position in currentList.indices) {
            val item = currentList[position]
            if (item is AiGenerativeModel) {
                currentList[position] = item.copy(isExpanded = isExpanded)
                setState { copy(chatItems = currentList) }
            }
        }
    }

    private suspend fun handleEditResult(result: ServiceResult?, position: Int) {
        if (result !is ServiceResult.Success || result.data.isEmpty()) return

        val currentList = state.value.chatItems
        if (currentList.isNotEmpty() && position in currentList.indices) {
            val currentItem = currentList[position]
            val idParts = (currentItem as? TypingAnimatable)?.id?.split("_")
            val entityId = idParts?.getOrNull(0)?.toLongOrNull()
            val responseIndex = idParts?.getOrNull(1)?.toIntOrNull() ?: 0

            if (entityId != null) {
                val entity = chatRepository.getMessageById(entityId)
                if (entity != null) {
                    val mutableResponses =
                        entity.serviceResponses?.toMutableList() ?: mutableListOf()
                    if (responseIndex in mutableResponses.indices) {
                        mutableResponses[responseIndex] = result.data[0]
                        chatRepository.updateMessage(entity.copy(serviceResponses = mutableResponses))
                    }
                }
            }
        } else {
            chatRepository.insertMessage(
                AiChatMessageEntity(
                    serviceResponses = result.data,
                    sender = MessageSender.AI,
                    status = MessageStatus.SUCCESS,
                    sessionId = sessionId,
                    lastEntity = lastEntity,
                    message = null,
                )
            )
        }
    }

    private suspend fun handleUpdateCategory(name: AiHistoryCategory) {
        updateCategoryUseCase.execute(name)
        sendEvent(ShowMessage("تغییرات با موفقیت ذخیره شد"))
    }

    private fun handleRetryClick() {
        val lastUserMessage = state.value.chatItems.lastOrNull { message ->
            when (message) {
                is AiTextModel -> message.isUserMessage
                is VoiceModel -> true
                else -> false
            }
        }
        when (lastUserMessage) {
            is AiTextModel -> {
                isWaitingForResponse = true
                setState {
                    copy(
                        chatItems = this.chatItems,
                        chatType = PromptType.Generating,
                        isLoading = true
                    )
                }
                currentlyTypingItemId = null
                job?.cancel()
                job = viewModelScope.launch {
                    sendPrompt(createSendPromptParams(lastUserMessage.message))
                }
            }

            is VoiceModel -> { /* handle voice retry if needed */
            }
        }
    }

    private fun handleDeleteVoice() {
        setState { copy(chatType = Deleted) }
    }

    private suspend fun updateSessionAfterUserMessage(message: String) {
        updateSessionStats(message)
    }

    private suspend fun updateSessionAfterAiMessage() {
        updateSessionStats(null)
    }

    private suspend fun updateSessionStats(newTitle: String?) {
        val now = System.currentTimeMillis()
        val category = try {
            aiHistoryRepository.getCategory(sessionId)
        } catch (e: Exception) {
            null
        }
        if (category == null) return
        var updated = category.copy(
            lastMessageDate = now,
            messageCount = category.messageCount + 1
        )
        val trimmedTitle = newTitle?.trim()
        if (isSessionTitlePending && !trimmedTitle.isNullOrBlank()) {
            updated = updated.copy(title = trimmedTitle)
            isSessionTitlePending = false
        }
        updateCategoryUseCase.execute(updated)
    }
}