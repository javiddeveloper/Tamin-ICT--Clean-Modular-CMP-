package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.dateRange
import com.tamin.taminhamrah.feature.agent.service.base.getFilters
import com.tamin.taminhamrah.feature.agent.service.impl.wage.inYears
import com.tamin.taminhamrah.feature.agent.service.impl.wage.lastPaidYear
import com.tamin.taminhamrah.feature.agent.service.impl.wage.paidMonths
import com.tamin.taminhamrah.feature.agent.service.impl.wage.year
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_last_payment
import taminx.core.core_ui.agent_empty_wage_average
import taminx.core.core_ui.agent_empty_wage_in_range
import taminx.core.core_ui.agent_error_wage_calculation
import taminx.core.core_ui.agent_label_average_wage
import taminx.core.core_ui.agent_label_average_wage_in_range
import taminx.core.core_ui.agent_label_branch
import taminx.core.core_ui.agent_label_days
import taminx.core.core_ui.agent_label_history_type
import taminx.core.core_ui.agent_label_history_year
import taminx.core.core_ui.agent_label_last_payment_month
import taminx.core.core_ui.agent_label_month
import taminx.core.core_ui.agent_label_wage
import taminx.core.core_ui.agent_label_workshop
import taminx.core.core_ui.agent_value_days
import taminx.core.core_ui.agent_value_rial
import kotlin.math.ceil

/**
 * Summaries over the wage history, ported from the native `AverageWageUseCase`,
 * `AverageWagePerDateUseCase` and `LastPayUseCase`.
 *
 * - `average_dastmozd_infos` with an `averageSalary:N` filter: walks paid months from the newest
 *   back until N×365 days are covered and divides their wages by N×12 — the pension calculator's
 *   average. Without that filter it lists the paid months of the years in the date range.
 * - `average_dastmozd_infos_per_date`: wages ÷ days worked inside the month-bounded range, × 30.
 * - `dastmozdinfos_last_pay`: the newest paid month of the newest paid year.
 */
class AverageWageAgentService(
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.AVERAGE_DASTMOZD_INFOS,
        AgentActionKey.AVERAGE_DASTMOZD_INFOS_PER_DATE,
        AgentActionKey.DASTMOZD_INFOS_LAST_PAY
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val records = getDastmozdInfosUseCase().list.orEmpty()
        val markdown = when (params.requestedKey) {
            AgentActionKey.DASTMOZD_INFOS_LAST_PAY -> lastPay(params, records)
            AgentActionKey.AVERAGE_DASTMOZD_INFOS_PER_DATE -> averagePerDate(params, records)
            else -> average(params, records)
        }
        AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_wage_calculation), e)
    }

    private suspend fun average(params: AgentServiceParams, records: List<DastmozdInfoItemDN>): String {
        val years = params.getFilters()[AVERAGE_SALARY_FILTER]?.trim()?.toIntOrNull()?.takeIf { it > 0 }
        if (years == null) return yearsInRange(params, records)

        val targetDays = years * DAYS_PER_YEAR
        var coveredDays = 0
        var totalWages = 0L
        records.sortedByDescending { it.year ?: 0 }
            .flatMap { record -> record.paidMonths().sortedByDescending { it.month } }
            .forEach { month ->
                if (coveredDays >= targetDays) return@forEach
                coveredDays += month.days
                totalWages += month.wage
            }
        if (coveredDays == 0) return emptyAnswer(params, Res.string.agent_empty_wage_average)

        val average = ceil(totalWages.toDouble() / (years * MONTHS_PER_YEAR)).toLong()
        return agentMarkdown {
            heading(params.message)
            fields(listOf(strings.get(Res.string.agent_label_average_wage) to rial(average)))
        }
    }

    /** The native service's fallback without `averageSalary`: the paid months of each year in range. */
    private suspend fun yearsInRange(params: AgentServiceParams, records: List<DastmozdInfoItemDN>): String {
        val years = records.inYears(params.dateRange())
            .sortedBy { it.year }
            .mapNotNull { record -> record.paidMonths().takeIf { it.isNotEmpty() }?.let { record to it } }
        if (years.isEmpty()) return emptyAnswer(params, Res.string.agent_empty_wage_average)

        val columns = listOf(
            strings.get(Res.string.agent_label_month),
            strings.get(Res.string.agent_label_days),
            strings.get(Res.string.agent_label_wage),
        )
        return agentMarkdown {
            heading(params.message)
            years.forEach { (record, months) ->
                fields(recordFields(record))
                table(columns, months.map { listOf(monthName(it.month), it.days.toString(), rial(it.wage)) })
                rule()
            }
        }
    }

    private suspend fun averagePerDate(params: AgentServiceParams, records: List<DastmozdInfoItemDN>): String {
        val range = params.dateRange()
        var totalWages = 0L
        var totalDays = 0
        records.inYears(range).forEach { record ->
            val year = record.year ?: return@forEach
            record.paidMonths(range.monthsOf(year)).forEach { month ->
                totalWages += month.wage
                totalDays += month.days
            }
        }
        if (totalDays == 0) return emptyAnswer(params, Res.string.agent_empty_wage_in_range)

        val average = ceil(totalWages.toDouble() / totalDays * DAYS_PER_MONTH).toLong()
        return agentMarkdown {
            heading(params.message)
            fields(listOf(strings.get(Res.string.agent_label_average_wage_in_range) to rial(average)))
        }
    }

    private suspend fun lastPay(params: AgentServiceParams, records: List<DastmozdInfoItemDN>): String {
        val record = records.lastPaidYear() ?: return emptyAnswer(params, Res.string.agent_empty_last_payment)
        val month = record.paidMonths().last()
        return agentMarkdown {
            heading(params.message)
            fields(
                listOf(
                    strings.get(Res.string.agent_label_workshop) to record.rwshname,
                    strings.get(Res.string.agent_label_history_type) to record.historytypedesc,
                    strings.get(Res.string.agent_label_branch) to record.brhname,
                    strings.get(Res.string.agent_label_last_payment_month) to "${monthName(month.month)} ${record.hisyear.orEmpty()}",
                    strings.get(Res.string.agent_label_days) to strings.get(Res.string.agent_value_days, month.days),
                    strings.get(Res.string.agent_label_wage) to rial(month.wage),
                )
            )
        }
    }

    private suspend fun recordFields(record: DastmozdInfoItemDN): List<Pair<String, String?>> = listOf(
        strings.get(Res.string.agent_label_history_year) to record.hisyear,
        strings.get(Res.string.agent_label_workshop) to record.rwshname,
        strings.get(Res.string.agent_label_history_type) to record.historytypedesc,
        strings.get(Res.string.agent_label_branch) to record.brhname,
    )

    private suspend fun emptyAnswer(params: AgentServiceParams, message: StringResource): String = agentMarkdown {
        heading(params.message)
        paragraph(strings.get(message))
    }

    private suspend fun rial(amount: Long): String = strings.get(Res.string.agent_value_rial, amount.toPriceFormat())

    private fun monthName(month: Int): String = PersianDateFormatter.monthNames.getOrElse(month - 1) { month.toString() }

    private companion object {
        const val AVERAGE_SALARY_FILTER = "averageSalary"
        const val DAYS_PER_YEAR = 365
        const val DAYS_PER_MONTH = 30
        const val MONTHS_PER_YEAR = 12
    }
}
