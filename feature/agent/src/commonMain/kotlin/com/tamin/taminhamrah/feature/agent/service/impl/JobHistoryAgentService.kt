package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.orDash
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.useCases.common.GetJobTitleUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Job titles held by the insured — عناوین شغلی.
 *
 * Ported from old_Android's `JobHistoryAllUseCase` / `JobHistoryLastUseCase`
 * (both backed by `getTitlesJob`).
 */
class JobHistoryAgentService(
    private val getJobTitleUseCase: GetJobTitleUseCase
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.HISTORY_JOB_INFOS,
        AgentActionKey.HISTORY_JOB_INFOS_LAST
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val list = getJobTitleUseCase(emptyList()).firstOrNull()?.list.orEmpty()

            if (list.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text(params.message ?: "عنوان شغلی برای شما ثبت نشده است."))
                    }
                )
            }

            val records = if (params.isLastVariant()) listOf(list.first()) else list

            val rows = mutableListOf<Pair<String, String>>()
            records.forEachIndexed { index, item ->
                rows.add("عنوان شغلی" to item.jobDescription.orDash())
                rows.add("کد شغل" to item.jobCode.orDash())
                rows.add("وضعیت" to item.status.orDash())
                rows.add("تاریخ وضعیت" to item.statusDate.orDash())
                if (index < records.lastIndex) rows.add(ROW_SEPARATOR to "")
            }

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(
                        ChatBubbleContent.KeyValue(
                            title = params.message?.takeIf { it.isNotBlank() } ?: "سوابق شغلی",
                            items = rows.toKeyValueRows()
                        )
                    )
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت سوابق شغلی: ${e.message}", e)
        }
    }

    private companion object {
        const val ROW_SEPARATOR = "----------------"
    }
}
