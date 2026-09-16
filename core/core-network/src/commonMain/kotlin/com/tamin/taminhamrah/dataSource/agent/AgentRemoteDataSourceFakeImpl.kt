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

    /** The prompt of the request being tracked, so the fixture can follow what was asked. */
    private var lastPrompt: String = ""

    override suspend fun checkChatAllowed(): ChatAllowedDTO {
        delay(500)
        return ChatAllowedDTO(
            status = 200,
            family = "SUCCESSFUL",
            reason = "OK",
            data = ChatAllowedDataDTO(
                canStartChat = true,
                chatToken = "fake-token-123",
                errorMessage = null,
                canSendVoice = true
            )
        )
    }

    override suspend fun sendServicePrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO {
        lastPrompt = request.prompt
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
        //   FAKE_AGENT_MARKDOWN_RESPONSE — server-rendered markdown; sent for any prompt that
        //                                  mentions markdown, so both paths stay reachable
        val fixture = if (MARKDOWN_PROMPT_HINTS.any { lastPrompt.contains(it, ignoreCase = true) }) {
            FAKE_AGENT_MARKDOWN_RESPONSE
        } else {
            FAKE_AGENT_ONE_RESPONSE
        }
        val fakeData = json.decodeFromString<PollingDataDTO>(fixture)
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

private val MARKDOWN_PROMPT_HINTS = listOf("markdown", "مارک‌داون", "مارکداون", "مارک داون")
