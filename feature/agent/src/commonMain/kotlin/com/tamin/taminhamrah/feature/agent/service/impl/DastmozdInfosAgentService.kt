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

            var filteredList = list

            // 4. Extract filters from payload
            val payload = params.payload
            var startYear: Int? = null
            var endYear: Int? = null

            if (payload != null && payload is kotlinx.serialization.json.JsonObject) {
                val filterArray = payload["filter"] as? kotlinx.serialization.json.JsonArray
                filterArray?.forEach { element ->
                    val filterStr = element.run { if (this is kotlinx.serialization.json.JsonPrimitive) this.content else "" }
                    if (filterStr.startsWith("startDate:")) {
                        val date = filterStr.removePrefix("startDate:")
                        if (date.length >= 4) startYear = date.substring(0, 4).toIntOrNull()
                    }
                    if (filterStr.startsWith("endDate:")) {
                        val date = filterStr.removePrefix("endDate:")
                        if (date.length >= 4) endYear = date.substring(0, 4).toIntOrNull()
                    }
                }
            }

            // 5. Apply filters
            if (startYear != null || endYear != null) {
                filteredList = list.filter { info ->
                    val year = info.hisyear?.toIntOrNull() ?: return@filter true
                    when {
                        startYear != null && endYear != null -> year in startYear..endYear
                        startYear != null -> year >= startYear
                        endYear != null -> year <= endYear
                        else -> true
                    }
                }
            }

            if (filteredList.isEmpty()) {
                return AgentServiceResult.Success(
                    bubbles = listOf(
                        ChatBubbleContent.Text(msg ?: "رکوردی در این بازه تاریخی یافت نشد.")
                    )
                )
            }

            // 6. Map to Bubbles
            if (!msg.isNullOrBlank()) {
                bubbles.add(ChatBubbleContent.Text(msg))
            }

            filteredList.forEach { info ->
                val year = info.hisyear ?: return@forEach
                val details = mutableListOf<Pair<String, String>>()
                details.add("سال سابقه" to year)
                details.add("نام کارگاه" to (info.rwshname ?: "-"))
                details.add("نوع سابقه" to (info.historytypedesc ?: "-"))
                details.add("نام شعبه" to (info.brhname ?: "-"))

                // Map monthly wage details
                info.wageDetails.forEach { detail ->
                    val month = detail.month ?: return@forEach
                    val amount = detail.wage ?: "-"
                    details.add("مبلغ دستمزد $month" to amount)
                }

                bubbles.add(
                    ChatBubbleContent.KeyValue(
                        title = "اطلاعات دستمزد سال $year",
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
