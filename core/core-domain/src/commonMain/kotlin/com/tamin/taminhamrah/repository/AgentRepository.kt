package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.ChatAllowedDN
import kotlinx.coroutines.flow.Flow

/**
 * Data layer contract for the Agent system.
 *
 * - [sendPrompt]: Sends a prompt and receives a Flow of polling states.
 * - [cancelRequest]: Cancels a currently processing request.
 * - [checkChatAllowed]: Checks if the user is authorized to use the chat.
 */
interface AgentRepository {

    /**
     * Sends a prompt to the AI server and polls until a response is received.
     *
     * The Flow emits:
     * 1. [AgentPollingState.Pending] — Immediately after registering the request.
     * 2. [AgentPollingState.Done] — When processing is complete.
     * 3. [AgentPollingState.Failed] — In case of an error or timeout.
     */
    fun sendPrompt(request: AgentRequest): Flow<AgentPollingState>

    /**
     * Cancels an active polling request.
     */
    suspend fun cancelRequest(requestId: String): Result<Unit>

    /**
     * Checks if the user is authorized to use the chatbot.
     */
    suspend fun checkChatAllowed(): Result<ChatAllowedDN>
}
