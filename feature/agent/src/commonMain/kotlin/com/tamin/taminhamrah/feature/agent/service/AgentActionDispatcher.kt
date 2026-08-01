package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.agent.PromptItemDTO
import com.tamin.taminhamrah.model.agent.toFeatureFlag
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Central coordinator for the service execution pipeline.
 *
 * Responsibilities:
 * 1. Check [FeatureFlag] before executing any service
 * 2. Intercept and extract global metadata (e.g., suggested prompts)
 * 3. Invoke the appropriate handler from [AgentServiceRegistry]
 * 4. Pass [AgentSessionContext] to share data between steps
 *
 * Based on sections 3 and 8 of the architecture document (agent.md)
 */
class AgentActionDispatcher(
    private val registry: AgentServiceRegistry,
    private val featureManager: FeatureManager,
    private val json: Json
) {

    /**
     * Dispatches an entity from the AI response.
     *
     * Execution flow:
     * 1. Check FeatureFlag ← If disabled: [AgentServiceResult.FeatureDisabled]
     * 2. Extract [prompt_item]s from data (global interceptor)
     * 3. Find handler ← If not found: handle fallback (e.g. general_response)
     * 4. Execute handler ← [AgentServiceResult.Success] or [AgentServiceResult.Error]
     */
    suspend fun dispatch(
        entity: AiEntityDN,
        context: AgentSessionContext
    ): AgentServiceResult {
        // 1. Check FeatureFlag
        val featureFlag = entity.action.toFeatureFlag()
        if (featureFlag != null) {
            val isEnabled = featureManager.isFeatureEnabled(featureFlag)
            if (!isEnabled) {
                // Prefer the server-provided message (entity.message) over a hardcoded one.
                val disabledMessage = entity.message
                    ?: featureManager.getDisabledMessage(featureFlag)
                    ?: "This service is currently unavailable."
                return AgentServiceResult.FeatureDisabled(disabledMessage)
            }
        }

        // 2. Extract Suggested Prompts (Centralized Interceptor)
        val promptsBubble = extractSuggestedPrompts(entity.data)

        // 3. Find the handler
        val handler = registry.get(entity.action)

        if (handler == null) {
            // No handler found (e.g., general_response or unknown actions)
            // Fallback: Show the text message and the appended prompts
            val bubbles = mutableListOf<ChatBubbleContent>()
            val msg = entity.message
            if (!msg.isNullOrBlank()) {
                bubbles.add(ChatBubbleContent.Text(msg))
            }
            if (promptsBubble != null) {
                bubbles.add(promptsBubble)
            }
            return if (bubbles.isNotEmpty()) {
                AgentServiceResult.Success(bubbles)
            } else {
                AgentServiceResult.NoHandler
            }
        }

        // 4. Execute the specific handler
        return try {
            val result = handler.execute(
                AgentServiceParams(
                    payload = entity.payload,
                    rawData = entity.data,
                    message = entity.message,
                    sessionContext = context,
                    requestedKey = entity.action
                )
            )

            // Append global prompts to the handler's result if successful
            if (result is AgentServiceResult.Success && promptsBubble != null) {
                AgentServiceResult.Success(result.bubbles + promptsBubble)
            } else {
                result
            }
        } catch (e: Exception) {
            AgentServiceResult.Error(
                message = e.message ?: "Service execution error",
                cause = e
            )
        }
    }

    /**
     * Attempts to parse `prompt_item`s from the raw data.
     */
    private fun extractSuggestedPrompts(data: JsonElement?): ChatBubbleContent.SuggestedPrompts? {
        if (data == null) return null
        return try {
            val items = json.decodeFromJsonElement<List<PromptItemDTO>>(data)
            val prompts = items.filter { it.itemType == "prompt_item" }.mapNotNull { it.prompt }
            if (prompts.isNotEmpty()) {
                ChatBubbleContent.SuggestedPrompts(prompts)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Dispatches a general message (e.g., general_response / message entity)
     * that does not require a dedicated handler and is rendered directly as a text bubble.
     */
    fun dispatchMessage(message: String?): AgentServiceResult {
        if (message.isNullOrBlank()) return AgentServiceResult.NoHandler
        return AgentServiceResult.Success(
            bubbles = listOf(ChatBubbleContent.Text(message))
        )
    }
}
