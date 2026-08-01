package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.AgentDestination
import com.tamin.taminhamrah.model.agent.AgentActionKey

/**
 * Entry-point services that hand the user off to a full screen instead of
 * answering inline — old_Android modelled these as `ServiceData.Clickable`
 * with a local deep link.
 *
 * Only actions whose destination screen actually exists in this app are mapped
 * (see [AgentDestination]); the host maps the destination id to a real route.
 *
 * These are deliberately *not* the generative-form flows: the multi-step form
 * services (wedding present, funeral allowance, occurrence report, education
 * inquiry, dependent cancellation, edit phone/bank) still need dedicated
 * handlers and are intentionally left unregistered.
 */
class DeepLinkAgentService : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = DESTINATIONS.keys.toList()

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val target = DESTINATIONS[params.requestedKey] ?: return AgentServiceResult.NoHandler

        return AgentServiceResult.Success(
            params.buildBubbles {
                val description = params.message?.takeIf { it.isNotBlank() } ?: target.description
                add(ChatBubbleContent.Text(description))
                add(
                    ChatBubbleContent.DeepLink(
                        title = target.actionText,
                        destination = target.destination
                    )
                )
            }
        )
    }

    /** A screen the assistant can send the user to. */
    private data class Target(
        val destination: String,
        val actionText: String,
        val description: String
    )

    private companion object {
        val DESTINATIONS: Map<AgentActionKey, Target> = mapOf(
            AgentActionKey.DISABILITY_PENSION to Target(
                destination = AgentDestination.DISABILITY_PENSION,
                actionText = "برقراری مستمری از کارافتادگی",
                description = "برای ثبت درخواست برقراری مستمری از کارافتادگی، روی دکمه زیر بزنید."
            ),
            AgentActionKey.DEFFERED_INSTALLMENT_CERTIFICATE to Target(
                destination = AgentDestination.DEFERRED_INSTALLMENT,
                actionText = "گواهی اقساط معوق",
                description = "برای دریافت گواهی اقساط معوق، روی دکمه زیر بزنید."
            ),
            AgentActionKey.REGISTER_CONTRACT to Target(
                destination = AgentDestination.CONTRACTS,
                actionText = "انعقاد قرارداد بیمه",
                description = "برای انعقاد قرارداد بیمه، روی دکمه زیر بزنید."
            ),
            AgentActionKey.COMPLETE_INFO_OF_REAL_WORKSHOP to Target(
                destination = AgentDestination.WORKSHOPS,
                actionText = "اطلاعات کارگاه",
                description = "برای مشاهده و تکمیل اطلاعات کارگاه، روی دکمه زیر بزنید."
            ),
            AgentActionKey.PATIENT_HISTORY to Target(
                destination = AgentDestination.PRESCRIPTION,
                actionText = "نسخه الکترونیک",
                description = "برای مشاهده نسخه‌های الکترونیک خود، روی دکمه زیر بزنید."
            ),
            AgentActionKey.PATIENT_HISTORY_LAST to Target(
                destination = AgentDestination.PRESCRIPTION,
                actionText = "آخرین نسخه الکترونیک",
                description = "برای مشاهده آخرین نسخه الکترونیک خود، روی دکمه زیر بزنید."
            )
        )
    }
}
