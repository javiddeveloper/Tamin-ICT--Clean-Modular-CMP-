package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.dateRange
import com.tamin.taminhamrah.feature.agent.service.base.getFilters
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollItemKindDN
import com.tamin.taminhamrah.model.pension.PaymentTypeDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_payroll
import taminx.core.core_ui.agent_error_payroll
import taminx.core.core_ui.agent_label_deductions
import taminx.core.core_ui.agent_label_history_length
import taminx.core.core_ui.agent_label_loans
import taminx.core.core_ui.agent_label_payments
import taminx.core.core_ui.agent_label_payroll_date
import taminx.core.core_ui.agent_not_pensioner_access
import taminx.core.core_ui.agent_value_history_length
import taminx.core.core_ui.agent_value_rial
import kotlin.math.abs

/**
 * Pensioner payslip — فیش حقوقی — ported from the native `FishUseCase` / `FishLastUseCase`.
 *
 * The payslip for one `YYYYMM` is requested with the filter's payment type. `fish_last`, and `fish`
 * without a date, start at the current month and step back one month at a time, up to 12 times,
 * until a payslip exists. `fish` with a date asks for that month only.
 */
class PayRollAgentService(
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getPensionerPayRollUseCase: GetPensionerPayRollUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.FISH,
        AgentActionKey.FISH_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val pensionerId = getPensionerIdUseCase().firstOrNull()?.firstOrNull()?.pensionerId
        val markdown = if (pensionerId.isNullOrBlank()) {
            message(params, strings.get(Res.string.agent_not_pensioner_access))
        } else {
            val found = findPayRoll(params, pensionerId)
            if (found == null) message(params, strings.get(Res.string.agent_empty_payroll)) else render(params, found)
        }
        AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_payroll), e)
    }

    private data class FoundPayRoll(val year: Int, val month: Int, val rows: List<PayRollDN>)

    private suspend fun findPayRoll(params: AgentServiceParams, pensionerId: String): FoundPayRoll? {
        val paymentType = params.getFilters()[PAYMENT_TYPE_FILTER]?.takeIf { it.isNotBlank() } ?: PaymentTypeDN.MONTHLY.code
        val requested = params.dateRange().start?.takeIf { params.requestedKey == AgentActionKey.FISH }
        val (todayYear, todayMonth, _) = PersianDateFormatter.today()

        var year = requested?.year ?: todayYear
        var month = requested?.month ?: todayMonth
        val attempts = if (requested != null) 1 else MAX_MONTHS_BACK
        repeat(attempts) {
            val rows = getPensionerPayRollUseCase(filters(pensionerId, year, month, paymentType)).firstOrNull().orEmpty()
            if (rows.isNotEmpty()) return FoundPayRoll(year, month, rows)
            if (month == 1) {
                month = 12
                year -= 1
            } else {
                month -= 1
            }
        }
        return null
    }

    private suspend fun render(params: AgentServiceParams, found: FoundPayRoll): String {
        val first = found.rows.first()
        val payments = found.rows.ofKind(PayRollItemKindDN.PAYMENT).filter { (it.sumAmount ?: 0L) > 0 }
        val deductions = found.rows.ofKind(PayRollItemKindDN.DEDUCTION)
        val loans = found.rows.ofKind(PayRollItemKindDN.LOAN)

        val paymentRows = payments.map { it.tprDesc.orEmpty() to rial(it.sumAmount ?: 0L) }
        val deductionRows = deductions.map { it.tprDesc.orEmpty() to "-" + rial(abs(it.sumAmount ?: 0L)) }
        val loanRows = loans.map { it.tprDesc.orEmpty() to rial(it.sumAmount ?: 0L) }
        val paymentsTitle = strings.get(Res.string.agent_label_payments)
        val deductionsTitle = strings.get(Res.string.agent_label_deductions)
        val loansTitle = strings.get(Res.string.agent_label_loans)

        return agentMarkdown {
            heading(params.message)
            fields(
                listOf(
                    strings.get(Res.string.agent_label_payroll_date) to "${found.year}/${found.month.toString().padStart(2, '0')}",
                    strings.get(Res.string.agent_label_history_length) to
                        strings.get(Res.string.agent_value_history_length, first.hisYear.orEmpty(), first.hisMon.orEmpty()),
                )
            )
            listOf(paymentsTitle to paymentRows, deductionsTitle to deductionRows, loansTitle to loanRows)
                .filter { (_, rows) -> rows.isNotEmpty() }
                .forEach { (title, rows) ->
                    subheading(title)
                    fields(rows)
                }
        }
    }

    private fun List<PayRollDN>.ofKind(kind: PayRollItemKindDN) = filter { PayRollItemKindDN.fromCode(it.clpType) == kind }

    private suspend fun rial(amount: Long): String = strings.get(Res.string.agent_value_rial, amount.toPriceFormat())

    private fun message(params: AgentServiceParams, text: String): String = agentMarkdown {
        heading(params.message)
        paragraph(text)
    }

    private fun filters(pensionerId: String, year: Int, month: Int, paymentType: String) = listOf(
        ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL),
        ApiFilterDN(FilterProperty.START_DATE, "$year${month.toString().padStart(2, '0')}", FilterOperator.EQUAL),
        ApiFilterDN(FilterProperty.PAYMENT_TYPE, paymentType, FilterOperator.EQUAL),
    )

    private companion object {
        const val PAYMENT_TYPE_FILTER = "paymentType"
        const val MAX_MONTHS_BACK = 12
    }
}
