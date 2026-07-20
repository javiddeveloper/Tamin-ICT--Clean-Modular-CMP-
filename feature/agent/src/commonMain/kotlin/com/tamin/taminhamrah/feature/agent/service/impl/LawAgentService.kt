package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class LawItemDTO(
    val item_type: String? = null,
    val name: String? = null,
    val reference: String? = null,
    val content: String? = null
)

/**
 * Dedicated handler for the "Law Search" service in the chatbot.
 *
 * Supports two data formats from the backend:
 *  1. rawData: Array of LawItemDTO (item_type = "law_item") — real backend format
 *  2. payload: Object with "description" field — simple/test format
 *
 * When both are absent, returns a ServiceError bubble.
 */
class LawAgentService(
    private val json: Json
) : AgentServiceUseCase {

    override val actionKey = AgentActionKey.LAW

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val rawData = params.rawData
        val payload = params.payload

        // No data at all → error bubble
        if (rawData == null && payload == null) {
            return AgentServiceResult.Success(
                bubbles = listOf(ChatBubbleContent.ServiceError("اطلاعات قانون دریافت نشد."))
            )
        }

        val bubbles = mutableListOf<ChatBubbleContent>()

        // Prepend the AI message if present
        if (!params.message.isNullOrBlank()) {
            bubbles.add(ChatBubbleContent.Text(params.message))
        }

        // ── Strategy 1: rawData as a list of LawItemDTO (real backend) ──────────
        if (rawData != null) {
            val laws = try {
                json.decodeFromJsonElement<List<LawItemDTO>>(rawData)
            } catch (e: Exception) {
                emptyList()
            }

            val lawItems = laws.filter { it.item_type == "law_item" }
            if (lawItems.isNotEmpty()) {
                val sb = StringBuilder()
                lawItems.forEachIndexed { index, law ->
                    val lawName = law.name?.replace(")", "")?.trim() ?: "${index + 1}"
                    sb.append("- **").append(lawName).append("**: ").append(law.content ?: "").append("\n")
                }
                bubbles.add(ChatBubbleContent.Text(sb.toString().trimEnd()))
                return AgentServiceResult.Success(bubbles)
            }
        }

        // ── Strategy 2: payload as { "description": "..." } ──────────────────────
        if (payload != null) {
            val description = try {
                payload.jsonObject["description"]?.jsonPrimitive?.content
            } catch (e: Exception) {
                null
            }

            if (!description.isNullOrBlank()) {
                bubbles.add(ChatBubbleContent.Text(description))
                return AgentServiceResult.Success(bubbles)
            }
        }

        // Nothing useful found
        if (bubbles.isEmpty()) {
            return AgentServiceResult.Success(
                bubbles = listOf(ChatBubbleContent.ServiceError("اطلاعات قانون دریافت نشد."))
            )
        }

        return AgentServiceResult.Success(bubbles)
    }
}
