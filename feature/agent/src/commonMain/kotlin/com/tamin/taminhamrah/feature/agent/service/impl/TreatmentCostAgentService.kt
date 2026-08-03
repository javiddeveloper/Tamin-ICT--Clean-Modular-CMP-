package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.formatAmount
import com.tamin.taminhamrah.feature.agent.service.base.orDash
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.treatment.TreatmentCostDN
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Displays treatment cost certificates — گواهی هزینه درمان (tcr_price_certificate).
 *
 * Ported from old_Android's TreatmentCostUseCase.
 */
class TreatmentCostAgentService(
    private val getTreatmentCostsUseCase: GetTreatmentCostsUseCase,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.TREATMENT_COST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val list = getTreatmentCostsUseCase().firstOrNull().orEmpty()

            if (list.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles { add(ChatBubbleContent.Text("گواهی هزینه درمانی یافت نشد.")) }
                )
            }

            val rows = mutableListOf<Pair<String, String>>()
            list.forEachIndexed { index, item ->
                rows.addAll(item.toRows())
                if (index < list.lastIndex) rows.add(ROW_SEP to "")
            }

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(ChatBubbleContent.KeyValue(
                        title = params.message?.takeIf { it.isNotBlank() } ?: "گواهی هزینه درمان",
                        items = rows.toKeyValueRows()
                    ))
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت گواهی هزینه درمان: ${e.message}", e)
        }
    }

    private fun TreatmentCostDN.toRows(): List<Pair<String, String>> = buildList {
        nameFamil?.takeIf { it.isNotBlank() }?.let  { add("نام بیمار" to it) }
        datePaz?.takeIf { it.isNotBlank() }?.let     { add("تاریخ پذیرش" to it) }
        serviceDate?.takeIf { it.isNotBlank() }?.let { add("تاریخ خدمت" to it) }
        healthcenterName?.takeIf { it.isNotBlank() }?.let { add("مرکز درمانی" to it) }
        statusDesc?.takeIf { it.isNotBlank() }?.let  { add("وضعیت" to it) }
        payStatusDesc?.takeIf { it.isNotBlank() }?.let { add("وضعیت پرداخت" to it) }
        payPrice?.toLongOrNull()?.let { add("مبلغ پرداخت" to "${it.formatAmount()} ریال") }
        payService?.toLongOrNull()?.let { add("هزینه خدمت" to "${it.formatAmount()} ریال") }
        payOtherService?.toLongOrNull()?.let { add("سایر هزینه‌ها" to "${it.formatAmount()} ریال") }
        rahgiriCode?.takeIf { it.isNotBlank() }?.let { add("کد رهگیری" to it) }
        estimatePayDate?.takeIf { it.isNotBlank() }?.let { add("تاریخ پرداخت تخمینی" to it) }
        returnReason?.takeIf { it.isNotBlank() }?.let { add("علت رد" to it) }
    }

    private companion object { const val ROW_SEP = "────────────────" }
}
