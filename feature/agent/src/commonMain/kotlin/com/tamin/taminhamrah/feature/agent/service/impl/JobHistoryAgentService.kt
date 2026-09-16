package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.dateRange
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.history.HistoryJobInfoItemDN
import com.tamin.taminhamrah.useCases.history.GetHistoryJobInfosUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_job_history
import taminx.core.core_ui.agent_error_job_history
import taminx.core.core_ui.agent_label_job_title
import taminx.core.core_ui.agent_label_start_date
import taminx.core.core_ui.agent_label_workshop

/**
 * Job titles held by the insured — عناوین شغلی — from `history-services/historyjobinfos`, ported
 * from the native `JobHistoryAllUseCase` / `JobHistoryLastUseCase`.
 *
 * - `history_job_infos`: titles that started on or after the filter's `YYYYMM`; when none match,
 *   the latest title is shown instead.
 * - `history_job_infos_last`: only the latest title.
 */
class JobHistoryAgentService(
    private val getHistoryJobInfosUseCase: GetHistoryJobInfosUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.HISTORY_JOB_INFOS,
        AgentActionKey.HISTORY_JOB_INFOS_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val list = getHistoryJobInfosUseCase().firstOrNull()?.list.orEmpty()
        val latest = list.lastOrNull()

        val shown = when {
            latest == null -> emptyList()
            params.requestedKey == AgentActionKey.HISTORY_JOB_INFOS_LAST -> listOf(latest)
            else -> list.startingFrom(params.dateRange().start?.yearMonth).ifEmpty { listOf(latest) }
        }

        val markdown = agentMarkdown {
            heading(params.message)
            if (shown.isEmpty()) {
                paragraph(strings.get(Res.string.agent_empty_job_history))
            } else {
                table(
                    listOf(
                        strings.get(Res.string.agent_label_job_title),
                        strings.get(Res.string.agent_label_workshop),
                        strings.get(Res.string.agent_label_start_date),
                    ),
                    shown.map { listOf(it.jobDesc, it.rwshName, it.startDate.toYearMonthText()) },
                )
            }
        }
        AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_job_history), e)
    }

    /** Titles whose `YYYYMM…` start is at or after [yearMonth]; all of them when there is no filter. */
    private fun List<HistoryJobInfoItemDN>.startingFrom(yearMonth: Int?): List<HistoryJobInfoItemDN> {
        if (yearMonth == null) return this
        return filter { item -> item.startDate?.take(6)?.toIntOrNull()?.let { it >= yearMonth } == true }
    }

    /** `140203xx` → `1402/03`, as the native card showed it. */
    private fun String?.toYearMonthText(): String? =
        this?.takeIf { it.length >= 6 }?.let { "${it.take(4)}/${it.substring(4, 6)}" }
}
