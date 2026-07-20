package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.repository.AgentRepository
import kotlinx.coroutines.flow.Flow

/**
 * ارسال پرامپت متنی به سرور AI و دریافت Flow از نتایج
 *
 * استفاده:
 * ```kotlin
 * sendAgentPromptUseCase(AgentRequest(prompt = "سوابق بیمه‌ام چقدره؟"))
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
