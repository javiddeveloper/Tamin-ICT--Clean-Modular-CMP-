package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Pensioner edict — حکم مستمری.
 *
 * Ported from old_Android's `HokmUseCase` / `HokmLastUseCase`: resolve the pensioner id,
 * then fetch the edict and render its header plus the server-provided detail rows.
 */
class EdictAgentService(
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getEdictPensionerUseCase: GetEdictPensionerUseCase
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.HOKM,
        AgentActionKey.HOKM_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val pensionerId = getPensionerIdUseCase().firstOrNull()
                ?.firstOrNull()?.pensionerId

            if (pensionerId.isNullOrBlank()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text("شما مستمری‌بگیر نیستید یا اطلاعات حکم شما یافت نشد."))
                    }
                )
            }

            val query = ApiQueryParamDN(
                filters = listOf(
                    ApiFilterDN(
                        property = FilterProperty.PENSIONER_ID,
                        value = pensionerId,
                        operator = FilterOperator.EQUAL
                    )
                )
            )
            val edict = getEdictPensionerUseCase(query).firstOrNull()

            if (edict == null) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text(params.message ?: "حکم مستمری یافت نشد."))
                    }
                )
            }

            val rows = mutableListOf<Pair<String, String>>()
            val info = edict.edictInfo

            rows.add("عنوان حکم" to edict.title.orDash())
            val issuedAt = listOfNotNull(edict.edictYear, edict.edictMonth)
                .joinToString("/").takeIf { it.isNotBlank() }
            rows.add("تاریخ حکم" to issuedAt.orDash())
            rows.add("نام و نام خانوادگی" to
                listOfNotNull(info?.firstName, info?.lastName ?: edict.lastName)
                    .joinToString(" ").takeIf { it.isNotBlank() }.orDash())
            rows.add("کد ملی" to info?.nationalCode.orDash())
            rows.add("شماره بیمه" to edict.insuranceId.orDash())
            rows.add("نام شعبه" to edict.branchName.orDash())
            info?.pensionStartDate?.takeIf { it.isNotBlank() }
                ?.let { rows.add("تاریخ برقراری" to it) }
            info?.pensionBeforeIncrease?.takeIf { it.isNotBlank() }
                ?.let { rows.add("مستمری قبل از افزایش" to it) }
            info?.pensionAfterIncrease?.takeIf { it.isNotBlank() }
                ?.let { rows.add("مستمری بعد از افزایش" to it) }
            info?.edictDescription?.takeIf { it.isNotBlank() }
                ?.let { rows.add("شرح حکم" to it) }

            // Server-driven detail rows (already localized field descriptions).
            edict.detail.orEmpty()
                .filter { !it.fieldDesc.isNullOrBlank() }
                .forEach { rows.add(it.fieldDesc!! to it.fieldValue.orDash()) }

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(
                        ChatBubbleContent.KeyValue(
                            title = params.message?.takeIf { it.isNotBlank() } ?: "حکم مستمری",
                            items = rows.toKeyValueRows()
                        )
                    )
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت حکم مستمری: ${e.message}", e)
        }
    }
}
