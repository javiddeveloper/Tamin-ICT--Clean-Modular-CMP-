package com.tamin.taminhamrah.dataSource.agent

import com.tamin.taminhamrah.model.agent.AgentMockMode
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.CancelResponseDTO
import com.tamin.taminhamrah.model.agent.ChatAllowedDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository

/**
 * Sends each assistant call either to the real backend or to [AgentRemoteDataSourceFakeImpl],
 * according to the agent mock mode chosen in Developer Options — the assistant's counterpart of
 * `PaymentGatewayRemoteDataSourceSelector`.
 *
 * The mode is read per call, so switching it takes effect on the next check or prompt rather
 * than after a restart. [DeveloperOptionsRepository.getAgentMockMode] returns
 * [AgentMockMode.DISABLED] in release builds whatever is stored, so a released app never reaches
 * the fake.
 */
internal class AgentRemoteDataSourceSelector(
    private val real: AgentRemoteDataSource,
    private val fake: AgentRemoteDataSource,
    private val developerOptionsRepository: DeveloperOptionsRepository,
) : AgentRemoteDataSource {

    private val current: AgentRemoteDataSource
        get() = when (developerOptionsRepository.getAgentMockMode()) {
            AgentMockMode.DISABLED -> real
            AgentMockMode.RESPONSES,
            AgentMockMode.NO_VOICE,
            AgentMockMode.ACCESS_DENIED,
            AgentMockMode.OFFLINE -> fake
        }

    override suspend fun checkChatAllowed(): ChatAllowedDTO = current.checkChatAllowed()

    override suspend fun sendServicePrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO = current.sendServicePrompt(request, voiceBytes, voiceFileName)

    override suspend fun sendLawPrompt(
        request: AgentRequestDTO,
        voiceBytes: ByteArray?,
        voiceFileName: String?
    ): PollingResponseDTO = current.sendLawPrompt(request, voiceBytes, voiceFileName)

    override suspend fun trackRequest(requestId: String): PollingResponseDTO = current.trackRequest(requestId)

    override suspend fun cancelRequest(requestId: String): CancelResponseDTO = current.cancelRequest(requestId)
}
