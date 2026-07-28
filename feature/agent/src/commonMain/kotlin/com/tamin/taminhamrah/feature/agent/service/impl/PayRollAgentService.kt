package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.filterValue
import com.tamin.taminhamrah.feature.agent.service.base.formatAmount
import com.tamin.taminhamrah.feature.agent.service.base.orDash
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Pensioner payslips — فیش حقوقی.
 *
 * Ported from old_Android's `FishUseCase` / `FishLastUseCase`: both first resolve the
 * pensioner id, then fetch the payroll list filtered by that id.
 */
class PayRollAgentService(
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getPensionerPayRollUseCase: GetPensionerPayRollUseCase
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.FISH,
        AgentActionKey.FISH_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val pensionerId = getPensionerIdUseCase().firstOrNull()
                ?.firstOrNull()?.pensionerId

            if (pensionerId.isNullOrBlank()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text("شما مستمری‌بگیر نیستید یا اطلاعات مستمری شما یافت نشد."))
                    }
                )
            }

            val filters = listOf(
                ApiFilterDN(
                    property = FilterProperty.PENSIONER_ID,
                    value = pensionerId,
                    operator = FilterOperator.EQUAL
                )
            )
            val payRolls = getPensionerPayRollUseCase(filters).firstOrNull().orEmpty()

            if (payRolls.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text(params.message ?: "فیش حقوقی یافت نشد."))
                    }
                )
            }

            // Optional year narrowing when the AI extracted one from the user's question.
            val year = params.filterValue("year") ?: params.filterValue("hisYear")
            val filtered = if (year != null) {
                payRolls.filter { it.hisYear == year }.ifEmpty { payRolls }
            } else payRolls

            val records = if (params.isLastVariant()) listOf(filtered.first()) else filtered

            val rows = mutableListOf<Pair<String, String>>()
            records.forEachIndexed { index, item ->
                rows.addAll(item.toRows())
                if (index < records.lastIndex) rows.add(ROW_SEPARATOR to "")
            }

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(
                        ChatBubbleContent.KeyValue(
                            title = params.message?.takeIf { it.isNotBlank() } ?: "فیش حقوقی",
                            items = rows.toKeyValueRows()
                        )
                    )
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت فیش حقوقی: ${e.message}", e)
        }
    }

    private fun PayRollDN.toRows(): List<Pair<String, String>> = buildList {
        val period = listOfNotNull(hisYear, hisMon).joinToString("/").takeIf { it.isNotBlank() }
        add("دوره" to period.orDash())
        add("شرح" to tprDesc.orDash())
        add("مبلغ کل" to sumAmount.formatAmount())
        add("خالص پرداختی" to sumPay.formatAmount())
        textNumber?.takeIf { it.isNotBlank() }?.let { add("شماره متن" to it) }
    }

    private companion object {
        const val ROW_SEPARATOR = "----------------"
    }
}
