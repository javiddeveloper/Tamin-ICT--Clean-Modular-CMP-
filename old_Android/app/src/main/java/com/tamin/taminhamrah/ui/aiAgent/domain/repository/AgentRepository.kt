package com.tamin.taminhamrah.ui.aiAgent.domain.repository

import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentRequest
import com.tamin.taminhamrah.data.remote.models.ai.agent.ChatAllowedData
import com.tamin.taminhamrah.ui.aiAgent.domain.AgentResponse

interface AgentRepository {
    suspend fun checkChatAllowed(): Result<ChatAllowedData?>
    suspend fun sendLawPrompt(data: AgentRequest): Result<AgentResponse>
    suspend fun searchLawVoiceService(voicePath: String, data: AgentRequest): Result<AgentResponse>
    suspend fun sendPrompt(data: AgentRequest): Result<AgentResponse>
    suspend fun searchVoiceService(voicePath: String, data: AgentRequest): Result<AgentResponse>
    fun saveChatAllowedData(data: ChatAllowedData)
    fun getChatAllowedData(): ChatAllowedData?
    fun isChatAllowed(): Boolean
}
