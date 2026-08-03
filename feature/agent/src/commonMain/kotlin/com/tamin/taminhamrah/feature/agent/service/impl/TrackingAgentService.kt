package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.filterValue
import com.tamin.taminhamrah.feature.agent.service.base.formatAmount
import com.tamin.taminhamrah.feature.agent.service.base.orDash
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.useCases.treatment.GetElectronicPrescriptionListUseCase
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Displays electronic prescription tracking codes — کد پیگیری.
 *
 * Ported from old_Android's TrackingCodeUseCase / LastTrackingCodeUseCase.
 * Uses [GetElectronicPrescriptionListUseCase] with a default 7-day window if no
 * date filters are provided by the AI.
 */
class TrackingAgentService(
    private val getElectronicPrescriptionListUseCase: GetElectronicPrescriptionListUseCase,
    private val getCurrentUserNationalCodeUseCase: GetCurrentUserNationalCodeUseCase,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.TRACKING_CODE,
        AgentActionKey.LAST_TRACKING_CODE
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val nationalCode = getCurrentUserNationalCodeUseCase() ?: return AgentServiceResult.Error("کد ملی یافت نشد.")

            // Default window: last 7 days
            val nowMs = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            val sevenDaysMs = 7L * 24 * 60 * 60 * 1000

            val startDate = params.filterValue("startDate") ?: (nowMs - sevenDaysMs).toString()
            val endDate   = params.filterValue("endDate")   ?: nowMs.toString()

            val list = getElectronicPrescriptionListUseCase(
                requestTypeId       = "1",
                nationalCode        = nationalCode,
                patientNationalCode = "0",
                startDate           = startDate,
                endDate             = endDate
            ).firstOrNull().orEmpty()

            if (list.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles { add(ChatBubbleContent.Text("متاسفانه کد پیگیری یافت نشد.")) }
                )
            }

            // LAST variant: only the most recent item
            val records = if (params.requestedKey == AgentActionKey.LAST_TRACKING_CODE) {
                listOf(list.maxByOrNull { it.prescDate?.toLongOrNull() ?: 0L } ?: list.first())
            } else {
                list
            }

            val rows = mutableListOf<Pair<String, String>>()
            records.forEachIndexed { index, item ->
                rows.addAll(item.toRows())
                if (index < records.lastIndex) rows.add(ROW_SEP to "")
            }

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(ChatBubbleContent.KeyValue(
                        title = params.message?.takeIf { it.isNotBlank() } ?: "کد پیگیری",
                        items = rows.toKeyValueRows()
                    ))
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت کد پیگیری: ${e.message}", e)
        }
    }

    private fun ElectronicPrescriptionDN.toRows(): List<Pair<String, String>> = buildList {
        trackingCode?.let { add("کد پیگیری" to it.toString()) }
        prescDate?.let { add("تاریخ نسخه" to it) }
        prescName?.let { if (it.isNotBlank()) add("نوع نسخه" to it) }
        docName?.let { if (it.isNotBlank()) add("پزشک" to it) }
        specDesc?.let { if (it.isNotBlank()) add("تخصص" to it) }
        patientName?.let { if (it.isNotBlank()) add("بیمار" to it) }
        location?.let { if (it.isNotBlank()) add("محل ارائه" to it) }
    }

    private companion object { const val ROW_SEP = "────────────────" }
}
