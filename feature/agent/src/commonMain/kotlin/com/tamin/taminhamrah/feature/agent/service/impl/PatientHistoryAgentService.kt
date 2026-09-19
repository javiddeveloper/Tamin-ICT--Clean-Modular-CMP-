package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.deeplink.DeepLinkKey
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.appLink
import com.tamin.taminhamrah.feature.agent.service.base.dateRange
import com.tamin.taminhamrah.feature.agent.service.impl.prescription.PrescriptionQuery
import com.tamin.taminhamrah.feature.agent.service.impl.prescription.rows
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import kotlinx.coroutines.CancellationException
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_action_prescription_detail
import taminx.core.core_ui.agent_empty_prescription
import taminx.core.core_ui.agent_error_national_code
import taminx.core.core_ui.agent_error_prescription

/**
 * The user's electronic prescriptions — نسخ الکترونیک — ported from the native
 * `ElectronicPrescriptionUseCase` / `ElectronicPrescriptionLastUseCase`.
 *
 * The earlier port read the patient's general health record instead, which is a different service
 * with a different meaning. Now:
 * - `patient_history`: up to five prescriptions in the date window, each with a detail link.
 * - `patient_history_last`: the first prescription of the date window, or — without dates — of the
 *   newest month that has any.
 */
class PatientHistoryAgentService(
    getElectronicPrescriptionListUseCase: GetElectronicPrescriptionListUseCase,
    private val getCurrentUserNationalCodeUseCase: GetCurrentUserNationalCodeUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    private val query = PrescriptionQuery(getElectronicPrescriptionListUseCase)

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.PATIENT_HISTORY,
        AgentActionKey.PATIENT_HISTORY_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val nationalCode = getCurrentUserNationalCodeUseCase()
        if (nationalCode == null) {
            AgentServiceResult.Error(strings.get(Res.string.agent_error_national_code))
        } else {
            val range = params.dateRange()
            val shown = if (params.requestedKey == AgentActionKey.PATIENT_HISTORY_LAST) {
                val list = if (range.isEmpty) query.latestByMonth(nationalCode) else query.inRange(nationalCode, range)
                listOfNotNull(list.firstOrNull())
            } else {
                query.inRange(nationalCode, range).take(MAX_SHOWN)
            }
            val detailLabel = strings.get(Res.string.agent_action_prescription_detail)
            val markdown = agentMarkdown {
                heading(params.message)
                if (shown.isEmpty()) paragraph(strings.get(Res.string.agent_empty_prescription))
                shown.forEach { prescription ->
                    subheading(prescription.patientName)
                    fields(prescription.rows(strings))
                    links(listOf(detailLabel to prescription.detailLink(nationalCode)))
                    rule()
                }
            }
            AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_prescription), e)
    }

    /** The native detail deep link's arguments, without its toolbar title and icon. */
    private fun ElectronicPrescriptionDN.detailLink(nationalCode: String): String =
        appLink(DeepLinkKey.PRESCRIPTION_DETAIL.key) + "?" + listOf(
            "ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION" to (noteHeadEprescID?.toString() ?: "0"),
            "ARG_REQUEST_TYPE" to prescType.orEmpty(),
            "PRES_TYPE" to prescType.orEmpty(),
            "ARG_NATIONAL_CODE" to nationalCode,
            "ARG_CHILD_NATIONAL_CODE" to PrescriptionQuery.NO_DEPENDANT,
            "ARG_FLAG_SATA" to flagSata.orEmpty(),
        ).joinToString("&") { (key, value) -> "$key=${value.filter { it.isLetterOrDigit() }}" }

    private companion object {
        const val MAX_SHOWN = 5
    }
}
