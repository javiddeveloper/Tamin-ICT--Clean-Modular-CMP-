package com.tamin.taminhamrah.ui.aiAgent.ui

import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel

object AgentContracts {

    data class AIState(
        val isLoading: Boolean = false,
        val chatType: PromptType = PromptType.Text,
        val voiceListState: VoiceListState = VoiceListState.VoiceListIdle,
        val message: String? = null,
        val chatItems: List<AiChatModel> = emptyList(),
        val filePath: String? = null,
        val isVoiceEnabled: Boolean = true
    )
    sealed class AIIntent {
        data class UpdateRequestState(val isLawRequest: Boolean) : AIIntent()
        data class SetCategoryItem(val item: AiHistoryCategory) : AIIntent()
        data class UpdateCategoryItem(val item: AiHistoryCategory) : AIIntent()
        data class LoadSession(val sessionId: String) : AIIntent()
        object OpenBottomSheetHistory : AIIntent()
        object StartNewChat : AIIntent()
        object OnBackPress : AIIntent()
        object StopMessageText : AIIntent()
        object StopMessageVoice : AIIntent()
        object OnRetryClick : AIIntent()
        data class  OnMicClick(val filePath: String?) : AIIntent()
        data class  OnClickableClick(val itemAction: AgentActionContent) : AIIntent()
        object OnDeletedVoice : AIIntent()
        object OnPlayVoice : AIIntent()
        object OnPauseVoice : AIIntent()
        data class OnPlayVoiceInList(val model: VoiceModel, val itemPosition: Int) : AIIntent()
        data class OnPauseVoiceInList(val model: VoiceModel, val itemPosition: Int) : AIIntent()
        data class VoiceListProgressUpdated(
            val playingPosition: Int,
            val model: VoiceModel,
            val itemPosition: Int,
        ) : AIIntent()
       object VoiceListIdle : AIIntent()
        object ResetVoice : AIIntent()
        object VoiceIdle : AIIntent()
        object SkipTypingAnimation : AIIntent()
        data class ResetListVoice(val model: VoiceModel, val itemPosition: Int) : AIIntent()
        data class OnTypingComplete(val item: TypingAnimatable) : AIIntent()
        data class SetMessageType(val type: PromptType) : AIIntent()
        data class DisplayReportMode(val title: String?, val mode: String) : AIIntent()
        data class ToggleExpand(val position: Int, val isExpanded: Boolean) : AIIntent()
        data class FormAction(
            val actionId: String,
            val data: Map<String, Any?>,
            val position: Int,
            val messageId: String? = null
        ) : AIIntent()
        data class UploadFormDocument(
            val body: okhttp3.MultipartBody.Part,
            val fieldId: String,
            val docTypeId: String?,
            val position: Int,
            val messageId: String? = null
        ) : AIIntent()
        data class UpdateFormData(
            val data: Map<String, Any?>,
            val position: Int,
            val messageId: String? = null
        ) : AIIntent()
        sealed class SendPrompt : AIIntent() {
            data class MessageType(val message: String) : SendPrompt()
            data class VoiceType(val duration: Int) : SendPrompt()
        }
    }

    sealed class AIEvent {
        data class ShowMessage(val message: String) : AIEvent()
        data class NavigateToHistory(val isLawRequest: Boolean) : AIEvent()
        object BackPressed : AIEvent()
        data class ClickableClick(val itemAction: AgentActionContent) : AIEvent()
        data class NavigateToEdit(val item: AiHistoryCategory) : AIEvent()
    }
}
