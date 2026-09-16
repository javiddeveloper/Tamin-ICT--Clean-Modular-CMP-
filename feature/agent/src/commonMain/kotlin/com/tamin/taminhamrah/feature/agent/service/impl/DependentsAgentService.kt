package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.getFilters
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.subdominant.DependentRelationDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_dependents
import taminx.core.core_ui.agent_error_dependents
import taminx.core.core_ui.agent_label_father_name
import taminx.core.core_ui.agent_label_full_name
import taminx.core.core_ui.agent_label_national_code
import taminx.core.core_ui.agent_label_relation
import taminx.core.core_ui.agent_label_status

/**
 * The insured's dependents — افراد تبعی — ported from the native `DependentsUseCase`.
 *
 * - A `tendencyCode` (optionally with `genderCode`) filter keeps only dependents of that relation.
 * - Without that filter, a question about فرزند keeps only children. The question is the server's
 *   own title, so this is the one place a service reads its wording.
 */
class DependentsAgentService(
    private val subdominantUseCase: SubdominantUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.GET_DEPENDENT)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val shown = filter(subdominantUseCase().firstOrNull()?.list.orEmpty(), params)
        val markdown = agentMarkdown {
            heading(params.message)
            if (shown.isEmpty()) paragraph(strings.get(Res.string.agent_empty_dependents))
            shown.forEach { item ->
                fields(
                    listOf(
                        strings.get(Res.string.agent_label_full_name) to
                            listOfNotNull(item.firstName, item.lastName).joinToString(" "),
                        strings.get(Res.string.agent_label_relation) to item.relationDescription,
                        strings.get(Res.string.agent_label_national_code) to item.nationalCode,
                        strings.get(Res.string.agent_label_father_name) to item.fatherName,
                        strings.get(Res.string.agent_label_status) to item.status,
                    )
                )
                rule()
            }
        }
        AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_dependents), e)
    }

    private fun filter(items: List<SubdominantItemDN>, params: AgentServiceParams): List<SubdominantItemDN> {
        val filters = params.getFilters().mapKeys { it.key.trim().lowercase() }
        val tendencyCode = filters[TENDENCY_CODE_FILTER]
        if (tendencyCode != null) {
            val accepted = DependentRelationDN.acceptedBy(tendencyCode, filters[GENDER_CODE_FILTER])
            if (accepted.isEmpty()) return items
            return items.filter { it.relation() in accepted }
        }
        if (params.message?.contains(CHILD_QUESTION_WORD) == true) {
            return items.filter { it.relation()?.isChild == true }
        }
        return items
    }

    private fun SubdominantItemDN.relation() = DependentRelationDN.of(tendencyCode, genderCode)

    private companion object {
        const val TENDENCY_CODE_FILTER = "tendencycode"
        const val GENDER_CODE_FILTER = "gendercode"
        /** Matched against the server's question, not shown to the user. */
        const val CHILD_QUESTION_WORD = "فرزند"
    }
}
