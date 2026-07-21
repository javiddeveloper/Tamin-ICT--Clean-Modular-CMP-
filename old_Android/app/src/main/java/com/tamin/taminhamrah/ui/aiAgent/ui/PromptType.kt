package com.tamin.taminhamrah.ui.aiAgent.ui

import com.tamin.taminhamrah.data.repository.ai.model.VoiceModel

sealed class PromptType {

    object Text : PromptType()
    object UserTyping : PromptType()
    object Generating : PromptType()
    object StopGenerating : PromptType()
    sealed class Voice : PromptType() {
        object Recording : Voice()
        object Stopped : Voice()
        object Playing : Voice()
        object Paused : Voice()
        object Deleted : Voice()
        object Reset : Voice()
        object Idle : Voice()

    }
}

sealed interface VoiceListState {

    data class PlayListVoice(val model: VoiceModel, val itemPosition: Int) : VoiceListState
    data class PauseListVoice(val model: VoiceModel, val itemPosition: Int) : VoiceListState
    data class ListProgressUpdating(val model: VoiceModel, val itemPosition: Int) : VoiceListState
    object ListVoiceReset : VoiceListState
    object VoiceListIdle : VoiceListState
}

