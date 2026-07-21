package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param

sealed interface PromptData {
    data class Prompt(
        val prompt: String?,
    ) : PromptData

}