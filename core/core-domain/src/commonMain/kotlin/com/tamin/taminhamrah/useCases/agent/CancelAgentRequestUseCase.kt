package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.repository.AgentRepository

/** Tells the assistant to drop a request the user stopped waiting for. */
class CancelAgentRequestUseCase(private val agentRepository: AgentRepository) {
    suspend operator fun invoke(requestId: String): Result<Unit> = agentRepository.cancelRequest(requestId)
}
