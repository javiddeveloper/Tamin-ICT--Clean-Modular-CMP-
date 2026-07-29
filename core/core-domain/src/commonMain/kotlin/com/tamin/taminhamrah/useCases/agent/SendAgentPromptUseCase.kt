package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.flow.Flow

/**
 * Sends a text prompt to the AI server and receives a Flow of results.
 *
 * Usage:
 * ```kotlin
 * sendAgentPromptUseCase(AgentRequest(prompt = "What is my insurance history?"))
 *     .collect { state ->
 *         when (state) {
 *             is AgentPollingState.Pending -> showLoading(state.etaSeconds)
 *             is AgentPollingState.Done    -> processEntities(state.response.entities)
 *             is AgentPollingState.Failed  -> showError(state.message)
 *             AgentPollingState.Cancelled  -> hideLoading()
 *         }
 *     }
 * ```
 */
class SendAgentPromptUseCase(
    private val agentRepository: AgentRepository
) {
    operator fun invoke(request: AgentRequest): Flow<AgentPollingState> =
        agentRepository.sendPrompt(request)
}
