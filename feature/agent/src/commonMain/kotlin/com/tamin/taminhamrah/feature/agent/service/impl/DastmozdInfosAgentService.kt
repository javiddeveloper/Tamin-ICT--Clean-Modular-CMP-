package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentDateRange
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChartKind
import com.tamin.taminhamrah.feature.agent.service.base.ChartSeries
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.dateRange
import com.tamin.taminhamrah.feature.agent.service.impl.wage.inYears
import com.tamin.taminhamrah.feature.agent.service.impl.wage.paidMonths
import com.tamin.taminhamrah.feature.agent.service.impl.wage.workedDays
import com.tamin.taminhamrah.feature.agent.service.impl.wage.year
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.CancellationException
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_wage_history
import taminx.core.core_ui.agent_error_wage_history
import taminx.core.core_ui.agent_label_branch
import taminx.core.core_ui.agent_label_days
import taminx.core.core_ui.agent_label_history_type
import taminx.core.core_ui.agent_label_history_year
import taminx.core.core_ui.agent_label_month
import taminx.core.core_ui.agent_label_total_days
import taminx.core.core_ui.agent_label_total_history_days
import taminx.core.core_ui.agent_label_wage
import taminx.core.core_ui.agent_label_workshop
import taminx.core.core_ui.agent_label_year
import taminx.core.core_ui.agent_value_days
import taminx.core.core_ui.agent_unit_day
import taminx.core.core_ui.agent_value_rial

/**
 * The wage-history answers — سوابق و دستمزد — ported from the native `DastmozdInfos*` use cases.
 *
 * | Key          | Answer |
 * |--------------|--------|
 * | `dastmozd_infos`, `_salary` | every paid month in the date range, year by year; the first and last year are trimmed to the range's months |
 * | `_last`      | only the latest paid month in the range |
 * | `_per_year`  | one row per year with the days worked |
 * | `_sum_total` | days worked per year inside the range, the grand total, and a chart of days per year |
 *
 * Output is markdown: the server's title, then the data. Nothing else is added.
 */
class DastmozdInfosAgentService(
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.DASTMOZD_INFOS,
        AgentActionKey.DASTMOZD_INFOS_LAST,
        AgentActionKey.DASTMOZD_INFOS_PER_YEAR,
        AgentActionKey.DASTMOZD_INFOS_SALARY,
        AgentActionKey.DASTMOZD_INFOS_SUM_TOTAL
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val range = params.dateRange()
        val records = getDastmozdInfosUseCase().list.orEmpty().inYears(range)
            .sortedBy { it.year }

        val bubbles = when (params.requestedKey) {
            AgentActionKey.DASTMOZD_INFOS_LAST -> lastMonth(params, records, range)
            AgentActionKey.DASTMOZD_INFOS_PER_YEAR -> perYear(params, records)
            AgentActionKey.DASTMOZD_INFOS_SUM_TOTAL -> sumTotal(params, records, range)
            else -> monthly(params, records, range)
        }
        AgentServiceResult.Success(bubbles ?: listOf(empty(params)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_wage_history), e)
    }

    private suspend fun monthly(
        params: AgentServiceParams,
        records: List<DastmozdInfoItemDN>,
        range: AgentDateRange,
    ): List<ChatBubbleContent>? {
        val years = records.mapNotNull { record ->
            val year = record.year ?: return@mapNotNull null
            record.paidMonths(range.monthsOf(year)).takeIf { it.isNotEmpty() }?.let { record to it }
        }
        if (years.isEmpty()) return null

        val columns = listOf(
            strings.get(Res.string.agent_label_month),
            strings.get(Res.string.agent_label_days),
            strings.get(Res.string.agent_label_wage),
        )
        val markdown = agentMarkdown {
            heading(params.message)
            years.forEach { (record, months) ->
                fields(recordFields(record))
                table(columns, months.map { listOf(monthName(it.month), it.days.toString(), rial(it.wage)) })
                rule()
            }
        }
        return listOf(ChatBubbleContent.Markdown(markdown))
    }

    private suspend fun lastMonth(
        params: AgentServiceParams,
        records: List<DastmozdInfoItemDN>,
        range: AgentDateRange,
    ): List<ChatBubbleContent>? {
        val latest = records.sortedByDescending { it.year }.firstNotNullOfOrNull { record ->
            val year = record.year ?: return@firstNotNullOfOrNull null
            record.paidMonths(range.monthsOf(year)).lastOrNull()?.let { record to it }
        } ?: return null

        val (record, month) = latest
        val markdown = agentMarkdown {
            heading(params.message)
            fields(
                recordFields(record) + listOf(
                    strings.get(Res.string.agent_label_month) to monthName(month.month),
                    strings.get(Res.string.agent_label_days) to strings.get(Res.string.agent_value_days, month.days),
                    strings.get(Res.string.agent_label_wage) to rial(month.wage),
                )
            )
        }
        return listOf(ChatBubbleContent.Markdown(markdown))
    }

    private suspend fun perYear(
        params: AgentServiceParams,
        records: List<DastmozdInfoItemDN>,
    ): List<ChatBubbleContent>? {
        if (records.isEmpty()) return null
        val columns = listOf(
            strings.get(Res.string.agent_label_history_year),
            strings.get(Res.string.agent_label_workshop),
            strings.get(Res.string.agent_label_history_type),
            strings.get(Res.string.agent_label_branch),
            strings.get(Res.string.agent_label_total_days),
        )
        val markdown = agentMarkdown {
            heading(params.message)
            table(
                columns,
                records.map { listOf(it.hisyear, it.rwshname, it.historytypedesc, it.brhname, it.workedDays().toString()) },
            )
        }
        return listOf(ChatBubbleContent.Markdown(markdown))
    }

    private suspend fun sumTotal(
        params: AgentServiceParams,
        records: List<DastmozdInfoItemDN>,
        range: AgentDateRange,
    ): List<ChatBubbleContent>? {
        if (records.isEmpty()) return null
        val perRecord = records.map { record -> record to record.workedDays(range.monthsOf(record.year ?: 0)) }
        val total = perRecord.sumOf { it.second }
        val daysLabel = strings.get(Res.string.agent_label_total_days)

        val markdown = agentMarkdown {
            heading(params.message)
            table(
                listOf(strings.get(Res.string.agent_label_year), strings.get(Res.string.agent_label_workshop), daysLabel),
                perRecord.map { (record, days) -> listOf(record.hisyear, record.rwshname, days.toString()) },
            )
            fields(listOf(strings.get(Res.string.agent_label_total_history_days) to strings.get(Res.string.agent_value_days, total)))
        }
        // Days per year, one bar per year: several workshops in the same year add up. A single year
        // is already the total above, and one lone bar carries no comparison, so it gets no chart.
        val byYear = perRecord.groupBy { it.first.hisyear.orEmpty() }.mapValues { (_, rows) -> rows.sumOf { it.second } }
        if (byYear.size < MIN_CHART_YEARS) return listOf(ChatBubbleContent.Markdown(markdown))
        val chart = ChatBubbleContent.Chart(
            title = daysLabel,
            kind = ChartKind.BAR,
            labels = byYear.keys.toList(),
            series = listOf(ChartSeries(name = daysLabel, values = byYear.values.map { it.toDouble() })),
            valueUnit = strings.get(Res.string.agent_unit_day),
        )
        return listOf(ChatBubbleContent.Markdown(markdown), chart)
    }

    private suspend fun recordFields(record: DastmozdInfoItemDN): List<Pair<String, String?>> = listOf(
        strings.get(Res.string.agent_label_history_year) to record.hisyear,
        strings.get(Res.string.agent_label_workshop) to record.rwshname,
        strings.get(Res.string.agent_label_history_type) to record.historytypedesc,
        strings.get(Res.string.agent_label_branch) to record.brhname,
    )

    private suspend fun empty(params: AgentServiceParams) = ChatBubbleContent.Markdown(
        agentMarkdown {
            heading(params.message)
            paragraph(strings.get(Res.string.agent_empty_wage_history))
        }
    )

    private suspend fun rial(amount: Long): String = strings.get(Res.string.agent_value_rial, amount.toPriceFormat())

    private fun monthName(month: Int): String = PersianDateFormatter.monthNames.getOrElse(month - 1) { month.toString() }

    private companion object {
        const val MIN_CHART_YEARS = 2
    }
}
