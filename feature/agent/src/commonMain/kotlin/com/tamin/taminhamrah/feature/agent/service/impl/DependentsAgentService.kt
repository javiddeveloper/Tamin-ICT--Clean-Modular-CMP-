package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Lists the insured person's dependents (افراد تحت تکفل).
 *
 * Ported from old_Android's `DependentsUseCase`, including its "children only"
 * narrowing when the user's question mentions فرزند and the AI sent no explicit filter.
 */
class DependentsAgentService(
    private val subdominantUseCase: SubdominantUseCase
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.GET_DEPENDENT)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val all = subdominantUseCase().firstOrNull()?.list.orEmpty()

            if (all.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text("فرد تحت تکفلی برای شما ثبت نشده است."))
                    }
                )
            }

            val filtered = applyRelationFilter(all, params.message)
            if (filtered.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text("موردی با این مشخصات در افراد تحت تکفل یافت نشد."))
                    }
                )
            }

            val rows = mutableListOf<Pair<String, String>>()
            filtered.forEachIndexed { index, item ->
                rows.add("نام و نام خانوادگی" to "${item.firstName.orDash()} ${item.lastName.orDash()}")
                rows.add("نسبت" to item.relationDescription.orDash())
                rows.add("کد ملی" to item.nationalCode.orDash())
                rows.add("نام پدر" to item.fatherName.orDash())
                rows.add("وضعیت" to item.status.orDash())
                if (index < filtered.lastIndex) rows.add(ROW_SEPARATOR to "")
            }

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(
                        ChatBubbleContent.KeyValue(
                            title = params.message?.takeIf { it.isNotBlank() } ?: "افراد تحت تکفل",
                            items = rows.toKeyValueRows()
                        )
                    )
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت افراد تحت تکفل: ${e.message}", e)
        }
    }

    /** Narrows to children when the user explicitly asked about فرزند (legacy behaviour). */
    private fun applyRelationFilter(
        items: List<SubdominantItemDN>,
        message: String?
    ): List<SubdominantItemDN> {
        if (message?.contains("فرزند") != true) return items
        val matches = items.filter { item ->
            CHILD_RELATIONS.any { item.relationDescription?.contains(it) == true }
        }
        return matches.ifEmpty { items }
    }

    private companion object {
        const val ROW_SEPARATOR = "----------------"
        val CHILD_RELATIONS = setOf("فرزند", "پسر", "دختر")
    }
}
