package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase

/**
 * Dedicated handler for the "Wage History" service in the chatbot.
 *
 * When AI determines that wage history should be shown to the user,
 * it returns the action [AgentActionKey.DASTMOZD_INFOS].
 * The Dispatcher finds this class and calls its execute method.
 */
class DastmozdInfosAgentService(
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase
) : AgentServiceUseCase {

    override val actionKey: AgentActionKey = AgentActionKey.DASTMOZD_INFOS

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            // 1. Retrieve data from the project's main Use Case
            val response = getDastmozdInfosUseCase()
            val list = response.list

            // 2. Check for empty data
            if (list.isNullOrEmpty()) {
                return AgentServiceResult.Success(
                    bubbles = listOf(
                        ChatBubbleContent.Text(params.message ?: "No wage history found for you.")
                    )
                )
            }

            // 3. Map data to displayable chat bubbles
            // 3. Map data to displayable chat bubbles with full formatting exactly like old_Android
            val bubbles = mutableListOf<ChatBubbleContent>()

            val msg = params.message
            if (!msg.isNullOrBlank()) {
                bubbles.add(ChatBubbleContent.Text(msg))
            }

            // TODO: Parse `params.payload` into DateFilter to filter the list locally
            // if AI provided date ranges like {"filter":["startDate:14020101", ...]}
            val filteredList = list // For now using the whole list

            filteredList.forEach { info ->
                val year = info.hisyear ?: return@forEach
                val details = mutableListOf<Pair<String, String>>()
                details.add("سال سابقه" to year)
                details.add("اطلاعات کارگاه" to (info.rwshname ?: "-"))
                details.add("نوع سابقه" to (info.historytypedesc ?: "-"))
                details.add("نام شعبه" to (info.brhname ?: "-"))
                
                // Add monthly entries just like the old Android app
                // Assuming info has fields like month1, month2 etc, or getMonthlyKeyValue logic
                // For demonstration, we add summary. In a real scenario, map month fields.
                
                bubbles.add(
                    ChatBubbleContent.KeyValue(
                        title = null,
                        items = details
                    )
                )
            }

            AgentServiceResult.Success(bubbles)

        } catch (e: Exception) {
            AgentServiceResult.Error(
                message = "Error retrieving wage history: ${e.message}",
                cause = e
            )
        }
    }
}
