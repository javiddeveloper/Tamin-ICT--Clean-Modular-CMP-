package com.tamin.taminhamrah.dataSource.agent

import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.CancelResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import com.tamin.taminhamrah.model.agent.PollingDataDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO

interface AgentRemoteDataSource {
    suspend fun checkChatAllowed(): ChatAllowedDTO
    suspend fun sendServicePrompt(request: AgentRequestDTO): PollingResponseDTO
    suspend fun sendLawPrompt(request: AgentRequestDTO): PollingResponseDTO
    suspend fun trackRequest(requestId: String): PollingResponseDTO
    suspend fun cancelRequest(requestId: String): CancelResponseDTO
}
