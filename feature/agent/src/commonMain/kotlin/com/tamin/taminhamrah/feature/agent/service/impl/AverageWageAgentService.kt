package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.formatAmount
import com.tamin.taminhamrah.feature.agent.service.base.getFilters
import com.tamin.taminhamrah.feature.agent.service.base.orDash
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase

/**
 * Aggregate views over the wage history — میانگین دستمزد و آخرین پرداخت.
 *
 * Ported from old_Android's `AverageWageUseCase`, `AverageWagePerDateUseCase` and
 * `LastPayUseCase`, all of which read the same wage-history source and then
 * summarise it rather than listing every row (that is [DastmozdInfosAgentService]).
 */
class AverageWageAgentService(
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.AVERAGE_DASTMOZD_INFOS,
        AgentActionKey.AVERAGE_DASTMOZD_INFOS_PER_DATE,
        AgentActionKey.DASTMOZD_INFOS_LAST_PAY
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val list = getDastmozdInfosUseCase().list.orEmpty()
            if (list.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text(params.message ?: "سابقه دستمزدی برای شما یافت نشد."))
                    }
                )
            }

            return when (params.requestedKey) {
                AgentActionKey.DASTMOZD_INFOS_LAST_PAY -> lastPay(params, list)
                else -> average(params, list)
            }
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در محاسبه دستمزد: ${e.message}", e)
        }
    }

    /** Latest recorded monthly wage (most recent year, last month with a value). */
    private fun lastPay(
        params: AgentServiceParams,
        list: List<DastmozdInfoItemDN>
    ): AgentServiceResult {
        val latest = list.maxByOrNull { it.hisyear?.toIntOrNull() ?: Int.MIN_VALUE }
        val lastDetail = latest?.wageDetails?.lastOrNull { !it.wage.isNullOrBlank() }

        if (latest == null || lastDetail == null) {
            return AgentServiceResult.Success(
                params.buildBubbles {
                    add(ChatBubbleContent.Text("آخرین دستمزد ثبت‌شده‌ای یافت نشد."))
                }
            )
        }

        val rows = listOf(
            "سال" to latest.hisyear.orDash(),
            "ماه" to lastDetail.month.orDash(),
            "مبلغ دستمزد" to lastDetail.wage?.toLongOrNull().formatAmount(),
            "نام کارگاه" to latest.rwshname.orDash(),
            "نام شعبه" to latest.brhname.orDash()
        )
        return AgentServiceResult.Success(
            params.buildBubbles {
                add(
                    ChatBubbleContent.KeyValue(
                        title = params.message?.takeIf { it.isNotBlank() } ?: "آخرین دستمزد",
                        items = rows.toKeyValueRows()
                    )
                )
            }
        )
    }

    /** Average monthly wage, optionally restricted to a year range chosen by the AI. */
    private fun average(
        params: AgentServiceParams,
        list: List<DastmozdInfoItemDN>
    ): AgentServiceResult {
        val filters = params.getFilters()
        val startYear = filters["startDate"]?.take(YEAR_LENGTH)?.toIntOrNull()
            ?: filters["startYear"]?.toIntOrNull()
        val endYear = filters["endDate"]?.take(YEAR_LENGTH)?.toIntOrNull()
            ?: filters["endYear"]?.toIntOrNull()

        val scoped = list.filter { item ->
            val year = item.hisyear?.toIntOrNull() ?: return@filter true
            (startYear == null || year >= startYear) && (endYear == null || year <= endYear)
        }

        val amounts = scoped.flatMap { it.wageDetails }.mapNotNull { it.wage?.toLongOrNull() }
            .filter { it > 0 }

        if (amounts.isEmpty()) {
            return AgentServiceResult.Success(
                params.buildBubbles {
                    add(ChatBubbleContent.Text("دستمزدی در این بازه برای محاسبه میانگین یافت نشد."))
                }
            )
        }

        val average = amounts.sum() / amounts.size
        val rows = buildList {
            val range = listOfNotNull(startYear, endYear)
            if (range.isNotEmpty()) {
                add("بازه" to listOfNotNull(startYear, endYear).joinToString(" تا "))
            }
            add("میانگین دستمزد ماهانه" to average.formatAmount())
            add("بیشترین دستمزد" to amounts.max().formatAmount())
            add("کمترین دستمزد" to amounts.min().formatAmount())
            add("تعداد ماه‌های محاسبه‌شده" to amounts.size.toString())
        }

        return AgentServiceResult.Success(
            params.buildBubbles {
                add(
                    ChatBubbleContent.KeyValue(
                        title = params.message?.takeIf { it.isNotBlank() } ?: "میانگین دستمزد",
                        items = rows.toKeyValueRows()
                    )
                )
            }
        )
    }

    private companion object {
        const val YEAR_LENGTH = 4
    }
}
