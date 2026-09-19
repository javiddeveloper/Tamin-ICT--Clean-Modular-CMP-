package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.json.JsonArray
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_not_understood

/**
 * Conversational answers — `general_response` and `message` — ported from the native
 * `GeneralResponseUseCase` / `MessageUseCase`. The server's text is markdown and is shown as sent;
 * the native "{name} عزیز" greeting is not added.
 */
class GeneralResponseAgentService(
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.GENERAL_RESPONSE,
        AgentActionKey.MESSAGE
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val text = params.message?.takeIf { it.isNotBlank() }
        // Without text the entity may still carry buttons or prompts; the dispatcher adds those.
        if (text == null && (params.rawData as? JsonArray)?.isNotEmpty() == true) {
            return AgentServiceResult.Success(emptyList())
        }
        return AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(text ?: strings.get(Res.string.agent_not_understood))))
    }
}
