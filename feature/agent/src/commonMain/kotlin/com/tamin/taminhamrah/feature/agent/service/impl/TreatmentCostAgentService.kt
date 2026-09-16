package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.treatment.TreatmentCostDN
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.useCases.treatment.GetTreatmentCostsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_treatment_costs
import taminx.core.core_ui.agent_error_treatment_costs
import taminx.core.core_ui.agent_label_admission_date
import taminx.core.core_ui.agent_label_estimated_pay_date
import taminx.core.core_ui.agent_label_health_center
import taminx.core.core_ui.agent_label_other_costs
import taminx.core.core_ui.agent_label_patient_name
import taminx.core.core_ui.agent_label_pay_status
import taminx.core.core_ui.agent_label_payment_amount
import taminx.core.core_ui.agent_label_rejection_reason
import taminx.core.core_ui.agent_label_service_cost
import taminx.core.core_ui.agent_label_service_date
import taminx.core.core_ui.agent_label_status
import taminx.core.core_ui.agent_label_tracking_code
import taminx.core.core_ui.agent_value_rial

/**
 * Miscellaneous treatment cost claims — خسارت متفرقه — ported from the native
 * `TreatmentCostsUseCase`, which asked for the ten most recent claims.
 */
class TreatmentCostAgentService(
    private val getTreatmentCostsUseCase: GetTreatmentCostsUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.TREATMENT_COST)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val list = getTreatmentCostsUseCase().firstOrNull().orEmpty().take(MAX_CLAIMS)
        val markdown = agentMarkdown {
            heading(params.message)
            if (list.isEmpty()) paragraph(strings.get(Res.string.agent_empty_treatment_costs))
            list.forEach { claim ->
                fields(rows(claim))
                rule()
            }
        }
        AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_treatment_costs), e)
    }

    private suspend fun rows(claim: TreatmentCostDN): List<Pair<String, String?>> = listOf(
        strings.get(Res.string.agent_label_patient_name) to claim.nameFamil,
        strings.get(Res.string.agent_label_admission_date) to claim.datePaz,
        strings.get(Res.string.agent_label_service_date) to claim.serviceDate,
        strings.get(Res.string.agent_label_health_center) to claim.healthcenterName,
        strings.get(Res.string.agent_label_status) to claim.statusDesc,
        strings.get(Res.string.agent_label_pay_status) to claim.payStatusDesc,
        strings.get(Res.string.agent_label_payment_amount) to rial(claim.payPrice),
        strings.get(Res.string.agent_label_service_cost) to rial(claim.payService),
        strings.get(Res.string.agent_label_other_costs) to rial(claim.payOtherService),
        strings.get(Res.string.agent_label_tracking_code) to claim.rahgiriCode,
        strings.get(Res.string.agent_label_estimated_pay_date) to claim.estimatePayDate,
        strings.get(Res.string.agent_label_rejection_reason) to claim.returnReason,
    ).filter { !it.second.isNullOrBlank() }

    private suspend fun rial(amount: String?): String? =
        amount?.toLongOrNull()?.let { strings.get(Res.string.agent_value_rial, it.toPriceFormat()) }

    private companion object {
        const val MAX_CLAIMS = 10
    }
}
