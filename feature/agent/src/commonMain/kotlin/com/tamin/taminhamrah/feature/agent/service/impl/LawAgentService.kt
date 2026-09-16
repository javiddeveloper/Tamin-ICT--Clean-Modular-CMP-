package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentItemType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_law

/** One law returned for the `law` key. Unknown fields are ignored. */
@Serializable
data class LawItemDTO(
    @SerialName("item_type") val itemType: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("reference") val reference: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("url") val url: String? = null,
)

/**
 * Laws and regulations — قوانین — returned by the assistant itself, ported from the native
 * `LawUseCase`. Each `law_item` becomes a titled section; a `payload.description` is used when the
 * server sends plain text instead.
 */
class LawAgentService(
    private val json: Json,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys = listOf(AgentActionKey.LAW)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val laws = params.rawData
            ?.let { runCatching { json.decodeFromJsonElement<List<LawItemDTO>>(it) }.getOrNull() }
            .orEmpty()
            .filter { AgentItemType.fromWireName(it.itemType) == AgentItemType.LAW_ITEM }
        val description = ((params.payload as? JsonObject)?.get(DESCRIPTION) as? JsonPrimitive)?.content

        val markdown = agentMarkdown {
            heading(params.message)
            when {
                laws.isNotEmpty() -> laws.forEach { law ->
                    subheading(law.name)
                    paragraph(law.content)
                    law.reference?.let { paragraph(it) }
                    rule()
                }
                !description.isNullOrBlank() -> paragraph(description)
                else -> paragraph(strings.get(Res.string.agent_empty_law))
            }
        }
        return AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    }

    private companion object {
        const val DESCRIPTION = "description"
    }
}
