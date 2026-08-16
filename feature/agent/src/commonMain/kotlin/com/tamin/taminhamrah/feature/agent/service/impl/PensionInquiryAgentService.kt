package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.formatAmount
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Pension inquiry results (استعلام مستمری).
 *
 * Ported from old_Android's `PensionInquiryAllUseCase` / `PensionInquireLastUseCase` —
 * the "_LAST" variant returns only the most recent record.
 */
class PensionInquiryAgentService(
    private val getPensionInquiryUseCase: GetPensionInquiryUseCase
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.PENSION_INQUIRY_ALL,
        AgentActionKey.PENSION_INQUIRY_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val list = getPensionInquiryUseCase().firstOrNull().orEmpty()

            if (list.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text(params.message ?: "اطلاعات مستمری یافت نشد."))
                    }
                )
            }

            // The dispatcher passes the concrete key, so "last" narrows to a single record.
            val isLastOnly = params.isLastVariant()
            val records = if (isLastOnly) listOf(list.first()) else list

            val rows = mutableListOf<Pair<String, String>>()
            records.forEachIndexed { index, item ->
                rows.addAll(item.toRows())
                if (index < records.lastIndex) rows.add(ROW_SEPARATOR to "")
            }

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(
                        ChatBubbleContent.KeyValue(
                            title = params.message?.takeIf { it.isNotBlank() } ?: "اطلاعات مستمری",
                            items = rows.toKeyValueRows()
                        )
                    )
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت اطلاعات مستمری: ${e.message}", e)
        }
    }

    private fun PensionInquiryDN.toRows(): List<Pair<String, String>> = listOf(
        "نام و نام خانوادگی" to fullName.orDash(),
        "نوع مستمری‌بگیر" to pensionerType.orDash(),
        "شماره بیمه" to insuranceNumber.orDash(),
        "کد ملی" to nationalId.orDash(),
        "نام شعبه" to branchName.orDash(),
        "وضعیت" to statusDesc.orDash(),
        "تاریخ برقراری" to pensionerBaseDate.orDash(),
        "تاریخ پرداخت" to paymentDate.orDash(),
        "مبلغ پرداختی" to paymentAmount.formatAmount()
    )

    private companion object {
        const val ROW_SEPARATOR = "----------------"
    }
}

/** True when the AI asked for the "last" variant of a paired action key. */
internal fun AgentServiceParams.isLastVariant(): Boolean =
    requestedKey?.key?.endsWith("_last") == true
