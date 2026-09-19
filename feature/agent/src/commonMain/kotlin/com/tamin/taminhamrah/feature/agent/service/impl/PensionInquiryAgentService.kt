package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PensionerStatusDN
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_pension_inquiry
import taminx.core.core_ui.agent_error_pension_inquiry
import taminx.core.core_ui.agent_label_branch_code
import taminx.core.core_ui.agent_label_edict_type
import taminx.core.core_ui.agent_label_full_name
import taminx.core.core_ui.agent_label_gender
import taminx.core.core_ui.agent_label_insurance_number
import taminx.core.core_ui.agent_label_national_code
import taminx.core.core_ui.agent_label_organization_unit
import taminx.core.core_ui.agent_label_payment_amount
import taminx.core.core_ui.agent_label_payment_date
import taminx.core.core_ui.agent_label_pension_end_date
import taminx.core.core_ui.agent_label_pension_number
import taminx.core.core_ui.agent_label_pension_start_date
import taminx.core.core_ui.agent_label_status
import taminx.core.core_ui.agent_not_pensioner
import taminx.core.core_ui.agent_value_rial

/**
 * Pension inquiry — استعلام وضعیت مستمری — ported from the native `PensionInquiryAllUseCase` /
 * `PensionInquireLastUseCase`.
 *
 * - A first row whose status is [PensionerStatusDN.NOT_PENSIONER] means the user is not a pensioner.
 * - `pension_inquiry_all` shows every row; `pension_inquiry_last` shows the last row's paid amount,
 *   which is the single value the native "last" answer carried.
 */
class PensionInquiryAgentService(
    private val getPensionInquiryUseCase: GetPensionInquiryUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.PENSION_INQUIRY_ALL,
        AgentActionKey.PENSION_INQUIRY_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val list = getPensionInquiryUseCase().firstOrNull().orEmpty()
        val markdown = agentMarkdown {
            heading(params.message)
            when {
                list.isEmpty() -> paragraph(strings.get(Res.string.agent_empty_pension_inquiry))
                PensionerStatusDN.fromCode(list.first().statusDesc) == PensionerStatusDN.NOT_PENSIONER ->
                    paragraph(strings.get(Res.string.agent_not_pensioner))
                params.requestedKey == AgentActionKey.PENSION_INQUIRY_LAST ->
                    fields(listOf(strings.get(Res.string.agent_label_payment_amount) to list.last().amount()))
                else -> list.forEach { item ->
                    fields(item.rows())
                    rule()
                }
            }
        }
        AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_pension_inquiry), e)
    }

    private suspend fun PensionInquiryDN.rows(): List<Pair<String, String?>> = listOf(
        strings.get(Res.string.agent_label_organization_unit) to branchName,
        strings.get(Res.string.agent_label_full_name) to fullName,
        strings.get(Res.string.agent_label_pension_number) to pensionerRisUid,
        strings.get(Res.string.agent_label_insurance_number) to insuranceNumber,
        strings.get(Res.string.agent_label_national_code) to nationalId,
        strings.get(Res.string.agent_label_edict_type) to (pensionerTypeDesc ?: pensionerType),
        strings.get(Res.string.agent_label_payment_date) to paymentDate,
        strings.get(Res.string.agent_label_pension_start_date) to pensionerBaseDate,
        strings.get(Res.string.agent_label_status) to statusDesc,
        strings.get(Res.string.agent_label_branch_code) to branchCode,
        strings.get(Res.string.agent_label_gender) to sexDesc,
        strings.get(Res.string.agent_label_pension_end_date) to pensionEndDate,
        strings.get(Res.string.agent_label_payment_amount) to amount(),
    )

    private suspend fun PensionInquiryDN.amount(): String? =
        paymentAmount?.let { strings.get(Res.string.agent_value_rial, it.toLong().toPriceFormat()) }
}
