package com.tamin.taminhamrah.feature.agent.ui.contract

import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.model.agent.AgentActionKey

// ─── UI State ─────────────────────────────────────────────────────────────────

/**
 * Overall UI State for the Agent screen.
 */
data class AgentUiState(
    /** Currently receiving a response from AI */
    val isGenerating: Boolean = false,
    /** Initial permission check in progress */
    val isCheckingPermission: Boolean = true,
    /** User is not allowed to use the assistant */
    val isNotAllowed: Boolean = false,
    /** Message explaining why the user is not allowed */
    val notAllowedMessage: String? = null,
    /** List of chat items */
    val chatItems: List<ChatItem> = emptyList(),
    /** Extension Card state (section 7 of agent.md) */
    val processingState: AgentProcessingState? = null,
    /** Current request ID — used for cancellation */
    val currentRequestId: String? = null,
    /** Session ID */
    val sessionId: String? = null,
    /** Last entity — used for next message context */
    val lastEntity: String? = null,
    /** Chat token */
    val chatToken: String? = null,
    /** Current input mode */
    val inputMode: InputMode = InputMode.Text
) {
    sealed interface PartialState {
        data class Loading(val isGenerating: Boolean) : PartialState
        data class CheckingPermission(val isChecking: Boolean) : PartialState
        data class NotAllowed(val message: String?) : PartialState
        data class ChatAllowedReceived(
            val chatToken: String?,
            val sessionId: String? = null
        ) : PartialState
        data class PendingReceived(val requestId: String, val etaSeconds: Int) : PartialState
        data class ProcessingStateUpdated(val state: AgentProcessingState?) : PartialState
        data class NewChatItems(val items: List<ChatItem>) : PartialState
        data class UpdateChatItem(val item: ChatItem) : PartialState
        data class SessionUpdated(
            val sessionId: String?,
            val lastEntity: String?
        ) : PartialState
        data class InputModeChanged(val mode: InputMode) : PartialState
        object GenerationCancelled : PartialState
        data class Error(val message: String?) : PartialState
    }
}

enum class InputMode { Text, Voice }

/**
 * Extension Card State — Represents AI processing steps (section 7 of agent.md)
 *
 * @param steps Names of the steps in execution order
 * @param currentActiveIndex Index of the currently executing step
 * @param isCompleted Whether all steps have finished
 */
data class AgentProcessingState(
    val steps: List<String>,
    val currentActiveIndex: Int = 0,
    val isCompleted: Boolean = false
)

/**
 * An item in the chat list.
 *
 * @param id Unique identifier
 * @param sender The sender of the message
 * @param content The content of the bubble
 * @param isTypingAnimating Whether the typing animation is active
 */
data class ChatItem(
    val id: String,
    val sender: ChatSender,
    val content: ChatBubbleContent,
    val isTypingAnimating: Boolean = false
)

enum class ChatSender { User, Agent }

// ─── Intents ──────────────────────────────────────────────────────────────────

sealed interface AgentIntent {
    /** Initial check — is the user allowed to use the assistant */
    object CheckPermission : AgentIntent

    /** Send a text prompt */
    data class SendTextPrompt(val message: String) : AgentIntent

    /** Cancel the current request */
    object CancelGeneration : AgentIntent

    /** Start a new session */
    object StartNewSession : AgentIntent

    /** Retry the last failed user prompt */
    object OnRetryClick : AgentIntent

    /** Change the input mode */
    data class ChangeInputMode(val mode: InputMode) : AgentIntent

    /** Execute a service action triggered by user click (e.g., suggested prompts) */
    data class ExecuteServiceAction(
        val actionKey: AgentActionKey,
        val payload: kotlinx.serialization.json.JsonElement? = null
    ) : AgentIntent
}

// ─── Events ───────────────────────────────────────────────────────────────────

sealed interface AgentEvent {
    data class ShowError(val message: String) : AgentEvent
    data class NavigateToDeepLink(val destination: String) : AgentEvent
    data class NavigateToWebView(val url: String) : AgentEvent
    object ScrollToBottom : AgentEvent
}
