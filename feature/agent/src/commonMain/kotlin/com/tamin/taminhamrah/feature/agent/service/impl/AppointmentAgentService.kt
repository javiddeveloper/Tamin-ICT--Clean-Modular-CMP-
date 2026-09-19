package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_appointment
import taminx.core.core_ui.agent_label_clinic
import taminx.core.core_ui.agent_label_date
import taminx.core.core_ui.agent_label_doctor
import taminx.core.core_ui.agent_label_location
import taminx.core.core_ui.agent_label_specialty
import taminx.core.core_ui.agent_label_status
import taminx.core.core_ui.agent_label_time
import taminx.core.core_ui.agent_label_type

/**
 * Appointments the assistant found itself — نوبت‌دهی. The items arrive in the entity's `data`;
 * this only lays them out.
 */
class AppointmentAgentService(
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.APPOINTMENT)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val appointments = (params.rawData as? JsonArray).orEmpty().mapNotNull { it as? JsonObject }
        val rows = appointments.map { item ->
            FIELDS.mapNotNull { (keys, label) ->
                keys.firstNotNullOfOrNull { key -> (item[key] as? JsonPrimitive)?.content?.takeIf { it.isNotBlank() } }
                    ?.let { strings.get(label) to it }
            }
        }.filter { it.isNotEmpty() }

        val markdown = agentMarkdown {
            heading(params.message)
            if (rows.isEmpty()) paragraph(strings.get(Res.string.agent_empty_appointment))
            rows.forEach {
                fields(it)
                rule()
            }
        }
        return AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    }

    private companion object {
        /** Field names the assistant may use for each label, first match wins. */
        val FIELDS: List<Pair<List<String>, StringResource>> = listOf(
            listOf("doctorName", "docName", "NAME") to Res.string.agent_label_doctor,
            listOf("speciality", "specDesc", "PROFICIENCY") to Res.string.agent_label_specialty,
            listOf("date") to Res.string.agent_label_date,
            listOf("time") to Res.string.agent_label_time,
            listOf("location", "ADDRESS", "CITY") to Res.string.agent_label_location,
            listOf("clinicName", "CENTER") to Res.string.agent_label_clinic,
            listOf("status") to Res.string.agent_label_status,
            listOf("type", "TITLE") to Res.string.agent_label_type,
        )
    }
}
