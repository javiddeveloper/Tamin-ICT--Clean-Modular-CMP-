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
    /**
     * The service could not be reached. Distinct from [isNotAllowed]: the chat stays
     * open on the cached conversation, only sending is disabled.
     */
    val isOffline: Boolean = false,
    /** Message explaining why the user is not allowed */
    val notAllowedMessage: String? = null,
    /** List of chat items */
    val chatItems: List<ChatItem> = emptyList(),
    /** Extension Card state (section 7 of agent.md) */
    val processingState: AgentProcessingState? = null,
    /** Current request ID — used for cancellation */
    val currentRequestId: String? = null,
    /** Last entity — used for next message context */
    val lastEntity: String? = null,
    /** The server's conversation state and history, replayed with the next prompt. */
    val conversationState: String? = null,
    val conversationHistory: String? = null,
    /** Whether the server lets this user send voice prompts. */
    val canSendVoice: Boolean = false,
    /** Current input mode */
    val inputMode: InputMode = InputMode.Text,
    /** Live recording state — non-null while the mic is recording. */
    val voiceRecording: VoiceRecordingState? = null,
    /** Pending recording awaiting send — non-null while previewing before send. */
    val voicePreview: VoicePreviewState? = null,
    /** Saved conversations shown in the history sheet, newest first. */
    val sessions: List<com.tamin.taminhamrah.model.agent.AgentSessionDN> = emptyList(),
    /** Whether the conversation-history bottom sheet is open. */
    val isHistoryVisible: Boolean = false,
    /** Local id of the conversation currently on screen. */
    val activeSessionId: String? = null,
    /** Id of the chat voice bubble currently playing (null if none). */
    val playingVoiceId: String? = null,
    /** Playback position (ms) of the currently playing chat voice bubble. */
    val voicePlaybackPositionMs: Int = 0,
    /** Total length (ms) of that clip, for the pinned player's progress. */
    val voicePlaybackDurationMs: Int = 0,
    /** Whether that clip is actually sounding, as opposed to paused mid-way. */
    val isVoicePlaying: Boolean = false,
    /** Logged-in user's first name, for the empty-state greeting. Null while unresolved. */
    val userFirstName: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isGenerating: Boolean) : PartialState
        data class CheckingPermission(val isChecking: Boolean) : PartialState
        data class NotAllowed(val message: String?) : PartialState
        data class OfflineChanged(val isOffline: Boolean) : PartialState
        data class SessionsLoaded(
            val sessions: List<com.tamin.taminhamrah.model.agent.AgentSessionDN>
        ) : PartialState
        data class HistoryVisibilityChanged(val isVisible: Boolean) : PartialState
        data class ActiveSessionChanged(val sessionId: String?) : PartialState
        /** Replaces the whole conversation, e.g. when opening one from history. */
        data class ChatItemsReplaced(val items: List<ChatItem>) : PartialState
        data class ChatAllowedReceived(val canSendVoice: Boolean) : PartialState
        data class PendingReceived(val requestId: String, val etaSeconds: Int) : PartialState
        data class ProcessingStateUpdated(val state: AgentProcessingState?) : PartialState
        data class NewChatItems(val items: List<ChatItem>) : PartialState
        data class UpdateChatItem(val item: ChatItem) : PartialState
        data class SessionUpdated(
            val lastEntity: String?,
            val state: String? = null,
            val history: String? = null,
        ) : PartialState
        data class InputModeChanged(val mode: InputMode) : PartialState
        object GenerationCancelled : PartialState
        data class Error(val message: String?) : PartialState
        data class VoiceRecordingUpdated(val state: VoiceRecordingState?) : PartialState
        data class VoicePreviewUpdated(val state: VoicePreviewState?) : PartialState
        data class VoicePlaybackUpdated(
            val itemId: String?,
            val positionMs: Int,
            val durationMs: Int = 0,
            val isPlaying: Boolean = false
        ) : PartialState
        data class IdentityLoaded(val firstName: String?) : PartialState
    }
}

enum class InputMode { Text, Voice }

/** Live microphone recording state driving the recorder waveform + 20s countdown. */
data class VoiceRecordingState(
    /** Rolling window of recent amplitudes (0..32767) for the live waveform. */
    val amplitudes: List<Int> = emptyList(),
    /** Elapsed recording time in milliseconds. */
    val elapsedMs: Long = 0L,
    /** Whether the last 5 seconds are reached — the waveform turns red. */
    val isNearLimit: Boolean = false
)

/** A finished recording awaiting preview / delete / send. */
data class VoicePreviewState(
    val filePath: String,
    val durationMs: Int = 0,
    val positionMs: Int = 0,
    val isPlaying: Boolean = false,
    /** Amplitude samples captured during recording, reused for the preview waveform. */
    val amplitudes: List<Int> = emptyList()
)

/** Maximum voice recording length, mirroring old_Android (20s), and the red threshold. */
const val VOICE_MAX_DURATION_MS = 20_000L
const val VOICE_NEAR_LIMIT_MS = 5_000L
/** How often the recorder samples amplitude / advances the elapsed timer. */
const val VOICE_AMPLITUDE_INTERVAL_MS = 80L

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
 * @param isEntering Whether this bubble just landed in the live session (as opposed to
 *   being loaded from history) and should play its slide/fade entrance once.
 */
data class ChatItem(
    val id: String,
    val sender: ChatSender,
    val content: ChatBubbleContent,
    val isTypingAnimating: Boolean = false,
    val isEntering: Boolean = false,
    /**
     * Follow-up suggestions belonging to this reply. They render inside the same bubble
     * rather than as their own chat row, so one answer stays one item in the list.
     */
    val suggestedPrompts: List<String> = emptyList()
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

    /**
     * A bubble finished its reveal animation. The ViewModel is the authority on this so
     * the animation does not restart when the list recycles the row while scrolling.
     */
    data class OnTypingFinished(val itemId: String) : AgentIntent

    /**
     * A bubble finished its entrance animation. Same reasoning as [OnTypingFinished]: the
     * ViewModel owns it so the slide/fade does not replay when the row is recycled.
     */
    data class OnEnterAnimationFinished(val itemId: String) : AgentIntent

    /** Execute a service action triggered by user click (e.g., suggested prompts) */
    data class ExecuteServiceAction(
        val actionKey: AgentActionKey,
        val payload: kotlinx.serialization.json.JsonElement? = null
    ) : AgentIntent

    // ── Interaction ──
    /** Share text content */
    data class ShareContent(val text: String) : AgentIntent

    // ── Conversation history ──
    /** Opens the history sheet and refreshes the saved conversation list. */
    object OpenChatHistory : AgentIntent
    object CloseChatHistory : AgentIntent
    /** Opens a saved conversation and replaces what is on screen. */
    data class LoadChatSession(val sessionId: String) : AgentIntent
    data class DeleteChatSession(val sessionId: String) : AgentIntent
    data class RenameChatSession(val sessionId: String, val title: String) : AgentIntent

    // ── Voice ──
    /** Begin microphone recording (permission is checked by the UI first). */
    object StartVoiceRecording : AgentIntent
    /** Stop recording and move to the preview state. */
    object StopVoiceRecording : AgentIntent
    /** Discard the pending recording without sending. */
    object DeleteVoiceRecording : AgentIntent
    /** Upload and send the pending recording as a voice message. */
    object SendVoiceRecording : AgentIntent
    /** Play/pause the pending recording in the preview bar. */
    object TogglePreviewPlayback : AgentIntent
    /** Seek within the pending recording preview. */
    data class SeekPreview(val ms: Int) : AgentIntent
    /** Play/pause a voice bubble already in the chat list. */
    data class ToggleVoicePlayback(val itemId: String, val filePath: String) : AgentIntent
    /** Stops voice playback entirely and dismisses the pinned player. */
    object StopVoicePlayback : AgentIntent

    /** Seek within a playing chat voice bubble. */
    data class SeekVoicePlayback(val itemId: String, val ms: Int) : AgentIntent
}

// ─── Events ───────────────────────────────────────────────────────────────────

sealed interface AgentEvent {
    data class ShowError(val message: String) : AgentEvent
    data class NavigateToDeepLink(val destination: String) : AgentEvent
    data class NavigateToWebView(val url: String) : AgentEvent
    data class ShareText(val text: String) : AgentEvent
    object ScrollToBottom : AgentEvent
}
