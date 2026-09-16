package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.deeplink.DeepLinkKey
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.appLink
import com.tamin.taminhamrah.model.agent.AgentActionKey

/**
 * Keys that hand the user to a screen of the app instead of answering in the chat — the native
 * entry-point use cases (disability pension, wedding present, illness compensation, …).
 *
 * The answer is the server's title and one button. The button's label is the menu's name for the
 * service, and the link goes through the deep link gate, so a disabled service cannot be entered
 * from here either. Keys whose screen does not exist in this app are not listed, so the dispatcher
 * shows the server's text without a dead button.
 */
class DeepLinkAgentService(
    private val featureManager: FeatureManager,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = DESTINATIONS.keys.toList()

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val destination = DESTINATIONS[params.requestedKey] ?: return AgentServiceResult.NoHandler
        val label = featureManager.getFeatureTitle(destination.flag) ?: params.message
        val markdown = agentMarkdown {
            heading(params.message)
            label?.let { links(listOf(it to appLink(destination.key))) }
        }
        return AgentServiceResult.Success(listOfNotNull(markdown.takeIf { it.isNotBlank() }?.let(ChatBubbleContent::Markdown)))
    }

    private companion object {
        val DESTINATIONS: Map<AgentActionKey, DeepLinkKey> = mapOf(
            AgentActionKey.DISABILITY_PENSION to DeepLinkKey.DISABILITY_PENSION,
            AgentActionKey.DEFFERED_INSTALLMENT_CERTIFICATE to DeepLinkKey.DEFERRED_INSTALLMENT_CERTIFICATE,
            AgentActionKey.REGISTER_CONTRACT to DeepLinkKey.CONTRACT_LIST,
            AgentActionKey.COMPLETE_INFO_OF_REAL_WORKSHOP to DeepLinkKey.COMPLETE_WORKSHOP_INFO,
            AgentActionKey.WEDDING_PRESENT to DeepLinkKey.WEDDING_PRESENT,
            AgentActionKey.FUNERAL_ALLOWANCE_GET to DeepLinkKey.REQUEST_FUNERAL_GRANT,
            AgentActionKey.PREGNANCY_PAY to DeepLinkKey.PREGNANCY_PAY,
            AgentActionKey.SHORT_TERM_ORTHOSIS to DeepLinkKey.OROTEZ_PROTEZ,
            AgentActionKey.OCCURRENCE_REPORT to DeepLinkKey.OCCURRENCE_REPORT,
            AgentActionKey.EXTEND_EDUCATION to DeepLinkKey.INQUIRY_EDUCATION,
            AgentActionKey.WORKER_PAYMENT to DeepLinkKey.WORKERS_PAYMENT_INFO,
            AgentActionKey.PENSION_SURVIVOR to DeepLinkKey.PENSION_SURVIVOR,
            // Illness: calculating the compensation and requesting it each have their own screen.
            AgentActionKey.CALCULATE_ILLNESS to DeepLinkKey.CALCULATE_WAGE_ILL_DAYS,
            AgentActionKey.DASTMOZD_INFOS_CALCILLNESS_PENSIONER to DeepLinkKey.CALCULATE_WAGE_ILL_DAYS,
            AgentActionKey.ILLNESS_COMPENSATION to DeepLinkKey.REQUEST_PAYMENT_ILL_DAYS,
            AgentActionKey.REPILLNESS to DeepLinkKey.REQUEST_PAYMENT_ILL_DAYS,
            AgentActionKey.REPILLNESS_LAST to DeepLinkKey.REQUEST_PAYMENT_ILL_DAYS,
            AgentActionKey.CALCILLNESS_REP to DeepLinkKey.REQUEST_PAYMENT_ILL_DAYS,
            AgentActionKey.CALCILLNESS_REP_LAST to DeepLinkKey.REQUEST_PAYMENT_ILL_DAYS,
        )
    }
}
