package com.tamin.taminhamrah.ui.aiAgent.domain.usecase

import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentRequest
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AgentRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.result.AgentResult
import javax.inject.Inject

class SendVoiceServiceUseCase @Inject constructor(
    private val aiRepository: AgentRepository
) {
    suspend operator fun invoke(voicePath: String, params: AgentRequest): AgentResult {
        return try {
            params.chatToken = aiRepository.getChatAllowedData()?.chatToken ?: ""
            val sendPromptResult = aiRepository.searchVoiceService(voicePath, params)

            sendPromptResult.fold(
                onSuccess = {
                    AgentResult.Success(it)
                },
                onFailure = { throwable ->
                    AgentResult.Error(errorMessage = throwable.message ?: "خطا در ارسال درخواست!")
                }
            )
        } catch (e: Exception) {
            AgentResult.Error(errorMessage = e.message ?: "خطا در ارسال درخواست!")
        }
    }
}
