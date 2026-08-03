package com.tamin.taminhamrah.dataSource.agent

import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.CancelResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import com.tamin.taminhamrah.model.agent.CancelDataDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDataDTO
import com.tamin.taminhamrah.model.agent.PollingDataDTO
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

internal class AgentRemoteDataSourceFakeImpl(
    private val json: Json
) : AgentRemoteDataSource {

    override suspend fun checkChatAllowed(): ChatAllowedDTO {
        delay(500)
        return ChatAllowedDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = ChatAllowedDataDTO(
                canStartChat = true,
                chatToken = "fake-token-123",
                errorMessage = null
            )
        )
    }

    override suspend fun sendServicePrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO {
        delay(800)
        return PollingResponseDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = PollingDataDTO(
                id = "fake-request-id",
                eta = 2,
                status = "PENDING",
                message = null,
                result = null
            )
        )
    }

    override suspend fun sendLawPrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO {
        return sendServicePrompt(request, voiceBytes, voiceFileName)
    }

    override suspend fun trackRequest(requestId: String): PollingResponseDTO {
        delay(1000)
        // Fixtures, pick one:
        //   FAKE_AGENT_SHOWCASE_RESPONSE — one of every bubble type, arriving one by one
        //   FAKE_AGENT_RESPONSE          — every supported action key
        //   FAKE_AGENT_ONE_RESPONSE      — short 4-entity smoke test
        val fakeData = json.decodeFromString<PollingDataDTO>(FAKE_AGENT_ONE_RESPONSE)
        return PollingResponseDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = fakeData
        )
    }

    override suspend fun cancelRequest(requestId: String): CancelResponseDTO {
        delay(500)
        return CancelResponseDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = CancelDataDTO(
                success = true,
                message = "Cancelled"
            )
        )
    }
}
