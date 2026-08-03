package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.AgentDestination
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.model.agent.AgentActionKey

/**
 * Handles illness-related queries — غرامت دستمزد بیماری.
 *
 * Ported from old_Android's IllnessCompensationUseCase / WageCompensationUseCase /
 * RepIllnessUseCase. The calculation endpoint (calculateWageIllDay) and the short-term
 * illness list (getViewShorttermRequestList) do not yet have KMP use-case equivalents,
 * so each variant returns a contextual message and a deep link to the relevant screen
 * for now. This service serves as a placeholder until those KMP use cases are built.
 */
class IllnessAgentService : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(
        AgentActionKey.CALCULATE_ILLNESS,
        AgentActionKey.CALCILLNESS_REP,
        AgentActionKey.CALCILLNESS_REP_LAST,
        AgentActionKey.REPILLNESS,
        AgentActionKey.REPILLNESS_LAST,
        AgentActionKey.DASTMOZD_INFOS_CALCILLNESS_PENSIONER
    )

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val (description, destination, actionText) = when (params.requestedKey) {
            AgentActionKey.CALCULATE_ILLNESS,
            AgentActionKey.DASTMOZD_INFOS_CALCILLNESS_PENSIONER ->
                Triple(
                    params.message?.takeIf { it.isNotBlank() }
                        ?: "برای محاسبه غرامت دستمزد بیماری، تاریخ شروع و پایان بیماری را در بخش مربوطه وارد کنید.",
                    AgentDestination.ILLNESS_COMPENSATION,
                    "محاسبه غرامت دستمزد"
                )

            AgentActionKey.REPILLNESS,
            AgentActionKey.REPILLNESS_LAST,
            AgentActionKey.CALCILLNESS_REP,
            AgentActionKey.CALCILLNESS_REP_LAST ->
                Triple(
                    params.message?.takeIf { it.isNotBlank() }
                        ?: "برای مشاهده گزارش غرامت دستمزد بیماری خود، روی دکمه زیر بزنید.",
                    AgentDestination.ILLNESS_REPORT,
                    "گزارش غرامت دستمزد"
                )

            else -> return AgentServiceResult.NoHandler
        }

        return AgentServiceResult.Success(
            params.buildBubbles {
                add(ChatBubbleContent.Text(description))
                add(ChatBubbleContent.DeepLink(title = actionText, destination = destination))
            }
        )
    }
}
