package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.useCases.calculateWagePension.CalculateWagePensionUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import kotlinx.coroutines.CancellationException
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_wage_average
import taminx.core.core_ui.agent_error_wage_calculation
import taminx.core.core_ui.agent_label_average_wage_two_years
import taminx.core.core_ui.agent_label_eligible_pension
import taminx.core.core_ui.agent_value_rial

/**
 * Eligible pension amount — مبلغ استحقاقی مستمری — for `eligible_amount_pension`.
 *
 * The native assistant computed this with its own copy of the pension calculator. Here it uses the
 * app's [CalculateWagePensionUseCase], the same one the «نحوه محاسبه مبلغ مستمری» screen shows,
 * so the chat and the screen can never disagree.
 */
class EligiblePensionAgentService(
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase,
    private val calculateWagePensionUseCase: CalculateWagePensionUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.ELIGIBLE_AMOUNT_PENSION)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val wages = getDastmozdInfosUseCase()
        val markdown = agentMarkdown {
            heading(params.message)
            if (wages.list.isNullOrEmpty()) {
                paragraph(strings.get(Res.string.agent_empty_wage_average))
            } else {
                val result = calculateWagePensionUseCase(getTalfighInfosUseCase(), wages)
                fields(
                    listOf(
                        strings.get(Res.string.agent_label_average_wage_two_years) to rial(result.averageSalaryLastTwoYears),
                        strings.get(Res.string.agent_label_eligible_pension) to rial(result.eligibleAmountPension),
                    )
                )
            }
        }
        AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_wage_calculation), e)
    }

    private suspend fun rial(amount: Long): String = strings.get(Res.string.agent_value_rial, amount.toPriceFormat())
}
