package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
data class LawItemDTO(
    val item_type: String? = null,
    val name: String? = null,
    val reference: String? = null,
    val content: String? = null
)

/**
 * Dedicated handler for the "Law Search" service in the chatbot.
 * Takes the raw data which is an array of law items, and formats them beautifully using markdown.
 */
class LawAgentService(
    private val json: Json
) : AgentServiceUseCase {
    override val actionKey = AgentActionKey.LAW

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val data = params.rawData
            if (data == null) {
                return AgentServiceResult.Success(listOf(ChatBubbleContent.Text(params.message ?: "قانونی یافت نشد.")))
            }

            val laws = try {
                json.decodeFromJsonElement<List<LawItemDTO>>(data)
            } catch (e: Exception) {
                emptyList()
            }
            
            val lawItems = laws.filter { it.item_type == "law_item" }
            if (lawItems.isEmpty()) {
                return AgentServiceResult.Success(listOf(ChatBubbleContent.Text(params.message ?: "قانونی یافت نشد.")))
            }

            val sb = StringBuilder()
            if (!params.message.isNullOrBlank()) {
                sb.append("**").append(params.message).append("**\n\n")
            }
            
            lawItems.forEachIndexed { index, law ->
                val lawName = law.name?.replace(")", "")?.trim() ?: "${index + 1}"
                sb.append("- **").append(lawName).append("**: ").append(law.content ?: "").append("\n")
            }

            AgentServiceResult.Success(
                bubbles = listOf(ChatBubbleContent.Text(sb.toString().trimEnd()))
            )
        } catch (e: Exception) {
            AgentServiceResult.Error(
                message = "خطا در پردازش قوانین: ${e.message}",
                cause = e
            )
        }
    }
}
