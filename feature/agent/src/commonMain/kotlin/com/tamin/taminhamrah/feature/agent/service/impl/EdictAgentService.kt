package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.dateRange
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_edict
import taminx.core.core_ui.agent_error_edict
import taminx.core.core_ui.agent_label_additional_history
import taminx.core.core_ui.agent_label_basis_implementation
import taminx.core.core_ui.agent_label_execution_date
import taminx.core.core_ui.agent_label_original_history
import taminx.core.core_ui.agent_label_pension_start_date
import taminx.core.core_ui.agent_label_total_pension_before_increase
import taminx.core.core_ui.agent_not_pensioner_access
import taminx.core.core_ui.agent_value_rial

/**
 * Pensioner edict — حکم مستمری — ported from the native `HokmUseCase` / `HokmLastUseCase`.
 *
 * `hokm` asks for the edict at the filter's start date, `hokm_last` for the current year's
 * فروردین. An answer with neither edict nor survivor information means the user has no edict for
 * that date. Rows follow the native answer: basis, start date, original and additional history,
 * total before the increase, then each detail row that carries an execution date.
 */
class EdictAgentService(
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getEdictPensionerUseCase: GetEdictPensionerUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.HOKM,
        AgentActionKey.HOKM_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val pensionerId = getPensionerIdUseCase().firstOrNull()?.firstOrNull()?.pensionerId
        val edict = pensionerId?.takeIf { it.isNotBlank() }?.let { id ->
            getEdictPensionerUseCase(ApiQueryParamDN(filters = filters(id, startDate(params)))).firstOrNull()
        }

        val markdown = agentMarkdown {
            heading(params.message)
            when {
                pensionerId.isNullOrBlank() -> paragraph(strings.get(Res.string.agent_not_pensioner_access))
                edict == null || (edict.edictInfo == null && edict.survivorInfo.isNullOrEmpty()) ->
                    paragraph(strings.get(Res.string.agent_empty_edict))
                else -> fields(rows(edict))
            }
        }
        AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_edict), e)
    }

    /** `YYYYMMDD`: the filter's date for `hokm`, the first of فروردین this year otherwise. */
    private fun startDate(params: AgentServiceParams): String {
        val start = params.dateRange().start.takeIf { params.requestedKey == AgentActionKey.HOKM }
        if (start == null) return "${PersianDateFormatter.currentJalaliYear()}0101"
        return "${start.year}${(start.month ?: 1).twoDigits()}${(start.day ?: 1).twoDigits()}"
    }

    private suspend fun rows(edict: EdictPensionerDN): List<Pair<String, String?>> {
        val info = edict.edictInfo
        return buildList {
            add(strings.get(Res.string.agent_label_basis_implementation) to info?.basisImplementation)
            add(strings.get(Res.string.agent_label_pension_start_date) to info?.pensionStartDate)
            add(strings.get(Res.string.agent_label_original_history) to info?.originalHistoryYear)
            add(strings.get(Res.string.agent_label_additional_history) to info?.additionalYear)
            add(strings.get(Res.string.agent_label_total_pension_before_increase) to rial(info?.totalPensionBeforeIncrease))
            edict.detail.orEmpty()
                .filter { it.packageName?.contains(EXECUTION_DATE_MARKER) == true }
                .forEach { detail ->
                    add(strings.get(Res.string.agent_label_execution_date) to detail.packageName?.executionDate())
                    add(detail.fieldDesc.orEmpty() to rial(detail.fieldValue))
                }
        }
    }

    private suspend fun rial(amount: String?): String? =
        amount?.filter { it.isDigit() }?.toLongOrNull()?.let { strings.get(Res.string.agent_value_rial, it.toPriceFormat()) }

    /** `"تاریخ اجرا : 01/01/1403"` → `1403/01/01`, as the native answer reordered it. */
    private fun String.executionDate(): String? {
        val date = substringAfter(':', "").trim().split('/')
        if (date.size != 3 || date.any { part -> part.isBlank() || !part.all(Char::isDigit) }) return null
        val (day, month, year) = date
        return "$year/$month/$day"
    }

    private fun Int.twoDigits(): String = toString().padStart(2, '0')

    private fun filters(pensionerId: String, startDate: String) = listOf(
        ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL),
        ApiFilterDN(FilterProperty.START_DATE, startDate, FilterOperator.EQUAL),
    )

    private companion object {
        const val EXECUTION_DATE_MARKER = "تاریخ اجرا"
    }
}
