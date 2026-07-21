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

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.DASTMOZD_INFOS,
        AgentActionKey.DASTMOZD_INFOS_LAST,
        AgentActionKey.DASTMOZD_INFOS_PER_YEAR,
        AgentActionKey.DASTMOZD_INFOS_SALARY,
        AgentActionKey.DASTMOZD_INFOS_SUM_TOTAL
    )

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

            // Remove adding message as a separate Text bubble. We'll use it as KeyValue title.
            val msg = params.message
            
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

            // 6. Combine all data into ONE single KeyValue bubble (like old Android GroupButton)
            val allDetails = mutableListOf<Pair<String, String>>()

            filteredList.forEachIndexed { index, info ->
                val year = info.hisyear ?: return@forEachIndexed
                allDetails.add("سال سابقه" to year)
                allDetails.add("نام کارگاه" to (info.rwshname ?: "-"))
                allDetails.add("نوع سابقه" to (info.historytypedesc ?: "-"))
                allDetails.add("نام شعبه" to (info.brhname ?: "-"))

                // Map monthly wage details
                info.wageDetails.forEach { detail ->
                    val month = detail.month ?: return@forEach
                    val amount = detail.wage ?: "-"
                    allDetails.add("مبلغ دستمزد $month" to amount)
                }

                if (index < filteredList.lastIndex) {
                    allDetails.add("----------------" to "")
                }
            }

            bubbles.add(
                ChatBubbleContent.KeyValue(
                    title = msg?.takeIf { it.isNotBlank() } ?: "اطلاعات دستمزد",
                    items = allDetails
                )
            )

            AgentServiceResult.Success(bubbles)

        } catch (e: Exception) {
            AgentServiceResult.Error(
                message = "Error retrieving wage history: ${e.message}",
                cause = e
            )
        }
    }
}
