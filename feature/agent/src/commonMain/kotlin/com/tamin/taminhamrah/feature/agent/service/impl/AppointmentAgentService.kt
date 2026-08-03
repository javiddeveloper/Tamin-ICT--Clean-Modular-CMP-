package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.KeyValueRow
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Handles appointment data returned directly by the AI — نوبت‌دهی.
 *
 * Ported from old_Android's AppointmentUseCase. The AI pre-fills the appointment
 * list in [AgentServiceParams.rawData]; this service only needs to parse and
 * display it.  When the AI returns no data, a friendly empty-state message is shown.
 */
class AppointmentAgentService : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.APPOINTMENT
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val items = parseAppointments(params)

            if (items.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles { add(ChatBubbleContent.Text("متاسفانه نوبتی یافت نشد.")) }
                )
            }

            // Each appointment becomes its own KeyValue bubble, separated visually.
            val bubbles = mutableListOf<ChatBubbleContent>()
            params.message?.takeIf { it.isNotBlank() }?.let { bubbles.add(ChatBubbleContent.Text(it)) }

            items.forEachIndexed { idx, appt ->
                bubbles.add(ChatBubbleContent.KeyValue(
                    title = "نوبت ${idx + 1}",
                    items = appt.toKeyValueRows()
                ))
            }

            AgentServiceResult.Success(bubbles)
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در نمایش نوبت‌ها: ${e.message}", e)
        }
    }

    /**
     * Tries to extract a list of appointment maps from [AgentServiceParams.rawData].
     * The AI typically sends an array of objects, each with fields like
     * doctorName, date, time, location, status, etc.
     */
    private fun parseAppointments(params: AgentServiceParams): List<List<Pair<String, String>>> {
        val array = params.rawData as? JsonArray ?: return emptyList()
        return array.mapNotNull { element ->
            val obj = element as? JsonObject ?: return@mapNotNull null
            buildList {
                obj["doctorName"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("پزشک" to it) }
                obj["docName"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("پزشک" to it) }
                obj["speciality"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("تخصص" to it) }
                obj["specDesc"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("تخصص" to it) }
                obj["date"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("تاریخ" to it) }
                obj["time"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("ساعت" to it) }
                obj["location"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("مکان" to it) }
                obj["status"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("وضعیت" to it) }
                obj["clinicName"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("کلینیک" to it) }
                obj["type"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }?.let { add("نوع" to it) }
            }.ifEmpty { null }
        }
    }
}
