package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.dateRange
import com.tamin.taminhamrah.feature.agent.service.impl.prescription.PrescriptionQuery
import com.tamin.taminhamrah.feature.agent.service.impl.prescription.rows
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import kotlinx.coroutines.CancellationException
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_tracking_code
import taminx.core.core_ui.agent_error_national_code
import taminx.core.core_ui.agent_error_tracking_code

/**
 * Prescription tracking codes — کد پیگیری — ported from the native `TrackingCodeUseCase` /
 * `LastTrackingCodeUseCase`.
 *
 * Dates from the assistant are Jalali `YYYYMMDD` and are turned into the epoch-millis window the
 * endpoint expects (the earlier port passed them through raw, which the service cannot read).
 * `last_tracking_code` keeps only the newest prescription.
 */
class TrackingAgentService(
    getElectronicPrescriptionListUseCase: GetElectronicPrescriptionListUseCase,
    private val getCurrentUserNationalCodeUseCase: GetCurrentUserNationalCodeUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    private val query = PrescriptionQuery(getElectronicPrescriptionListUseCase)

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.TRACKING_CODE,
        AgentActionKey.LAST_TRACKING_CODE
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val nationalCode = getCurrentUserNationalCodeUseCase()
        if (nationalCode == null) {
            AgentServiceResult.Error(strings.get(Res.string.agent_error_national_code))
        } else {
            val list = query.inRange(nationalCode, params.dateRange())
            val shown = if (params.requestedKey == AgentActionKey.LAST_TRACKING_CODE) {
                listOfNotNull(list.maxByOrNull { it.prescDate?.toLongOrNull() ?: 0L })
            } else {
                list
            }
            val markdown = agentMarkdown {
                heading(params.message)
                if (shown.isEmpty()) paragraph(strings.get(Res.string.agent_empty_tracking_code))
                shown.forEach { prescription ->
                    fields(prescription.rows(strings))
                    rule()
                }
            }
            AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_tracking_code), e)
    }
}
