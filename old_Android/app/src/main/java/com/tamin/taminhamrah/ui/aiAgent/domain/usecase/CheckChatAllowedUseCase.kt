package com.tamin.taminhamrah.ui.aiAgent.domain.usecase

import com.tamin.taminhamrah.data.remote.models.ai.agent.ChatAllowedData
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AgentRepository
import javax.inject.Inject

class CheckChatAllowedUseCase @Inject constructor(
    private val repository: AgentRepository
) {
    suspend operator fun invoke(): Result<ChatAllowedData?> {
        val result = repository.checkChatAllowed()
        result.onSuccess { data ->
            data?.let { repository.saveChatAllowedData(it) }
        }
        return result
    }
}
