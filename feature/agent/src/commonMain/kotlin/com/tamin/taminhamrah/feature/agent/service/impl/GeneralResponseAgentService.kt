package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.model.agent.AgentActionKey

/**
 * Handles conversational (non-data) responses from the AI.
 *
 * Ported from old_Android's `GeneralResponseUseCase` + `MessageUseCase`: both simply
 * render the model's message text and surface any follow-up prompt suggestions.
 */
class GeneralResponseAgentService : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.GENERAL_RESPONSE,
        AgentActionKey.MESSAGE
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val bubbles = params.buildBubbles {
            val message = params.message
            if (!message.isNullOrBlank()) {
                add(ChatBubbleContent.Text(message))
            }
        }

        if (bubbles.isEmpty()) {
            return AgentServiceResult.Success(
                listOf(ChatBubbleContent.Text("متوجه نشدم. لطفاً درخواستتان را واضح‌تر بفرمایید."))
            )
        }
        return AgentServiceResult.Success(bubbles)
    }
}
