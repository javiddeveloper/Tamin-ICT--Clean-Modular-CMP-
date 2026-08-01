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
 * answering inline.
 *
 * Only actions whose destination screen actually exists in this app are mapped
 * (see [AgentDestination]); the host maps the destination id to a real route.
 *
 * Note: PatientHistoryAgentService now handles PATIENT_HISTORY / PATIENT_HISTORY_LAST
 * with real data fetching, so those keys are no longer mapped here.
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

    private data class Target(
        val destination: String,
        val actionText: String,
        val description: String
    )

    private companion object {
        val DESTINATIONS: Map<AgentActionKey, Target> = mapOf(

            // ── Contracts & Legal ──────────────────────────────────────────
            AgentActionKey.DISABILITY_PENSION to Target(
                destination = AgentDestination.DISABILITY_PENSION,
                actionText  = "برقراری مستمری از کارافتادگی",
                description = "برای ثبت درخواست برقراری مستمری از کارافتادگی، روی دکمه زیر بزنید."
            ),
            AgentActionKey.DEFFERED_INSTALLMENT_CERTIFICATE to Target(
                destination = AgentDestination.DEFERRED_INSTALLMENT,
                actionText  = "گواهی اقساط معوق",
                description = "برای دریافت گواهی اقساط معوق، روی دکمه زیر بزنید."
            ),
            AgentActionKey.REGISTER_CONTRACT to Target(
                destination = AgentDestination.CONTRACTS,
                actionText  = "انعقاد قرارداد بیمه",
                description = "برای انعقاد قرارداد بیمه، روی دکمه زیر بزنید."
            ),
            AgentActionKey.COMPLETE_INFO_OF_REAL_WORKSHOP to Target(
                destination = AgentDestination.WORKSHOPS,
                actionText  = "اطلاعات کارگاه",
                description = "برای مشاهده و تکمیل اطلاعات کارگاه، روی دکمه زیر بزنید."
            ),

            // ── Social Benefits ────────────────────────────────────────────
            AgentActionKey.WEDDING_PRESENT to Target(
                destination = AgentDestination.WEDDING_PRESENT,
                actionText  = "کمک هزینه ازدواج",
                description = "برای دریافت کمک هزینه ازدواج، روی دکمه زیر بزنید."
            ),
            AgentActionKey.FUNERAL_ALLOWANCE_GET to Target(
                destination = AgentDestination.FUNERAL_ALLOWANCE,
                actionText  = "کمک هزینه کفن و دفن",
                description = "برای دریافت کمک هزینه کفن و دفن، روی دکمه زیر بزنید."
            ),
            AgentActionKey.PREGNANCY_PAY to Target(
                destination = AgentDestination.PREGNANCY_PAY,
                actionText  = "غرامت بارداری و زایمان",
                description = "برای دریافت غرامت بارداری و زایمان، روی دکمه زیر بزنید."
            ),
            AgentActionKey.SHORT_TERM_ORTHOSIS to Target(
                destination = AgentDestination.SHORT_TERM_ORTHOSIS,
                actionText  = "ارتز و پرتز کوتاه‌مدت",
                description = "برای درخواست ارتز و پرتز کوتاه‌مدت، روی دکمه زیر بزنید."
            ),
            AgentActionKey.OCCURRENCE_REPORT to Target(
                destination = AgentDestination.OCCURRENCE_REPORT,
                actionText  = "گزارش حادثه ناشی از کار",
                description = "برای ثبت گزارش حادثه ناشی از کار، روی دکمه زیر بزنید."
            ),

            // ── Health ─────────────────────────────────────────────────────
            AgentActionKey.BOOKLET to Target(
                destination = AgentDestination.DESERVED_TREATMENT,
                actionText  = "درمان مستقیم",
                description = "برای مشاهده درمان مستقیم خود، روی دکمه زیر بزنید."
            ),
            AgentActionKey.CONFIRMATION_MEDICAL_AUTHORITIES to Target(
                destination = AgentDestination.MEDICAL_AUTHORITIES,
                actionText  = "تأیید مراجع درمانی",
                description = "برای مشاهده تأییدیه مراجع درمانی، روی دکمه زیر بزنید."
            ),

            // ── Profile Edits ──────────────────────────────────────────────
            AgentActionKey.EDIT_PHONE_NUMBER to Target(
                destination = AgentDestination.EDIT_PHONE,
                actionText  = "ویرایش شماره موبایل",
                description = "برای ویرایش شماره موبایل، روی دکمه زیر بزنید."
            ),
            AgentActionKey.EDIT_BANK_ACCOUNT_NUMBER to Target(
                destination = AgentDestination.EDIT_BANK_ACCOUNT,
                actionText  = "ویرایش شماره حساب بانکی",
                description = "برای ویرایش شماره حساب بانکی، روی دکمه زیر بزنید."
            ),
            AgentActionKey.EXTEND_EDUCATION to Target(
                destination = AgentDestination.EXTEND_EDUCATION,
                actionText  = "استعلام ادامه تحصیل",
                description = "برای ثبت استعلام ادامه تحصیل، روی دکمه زیر بزنید."
            ),

            // ── Workers ────────────────────────────────────────────────────
            AgentActionKey.WORKER_PAYMENT to Target(
                destination = AgentDestination.WORKERS_PAYMENT,
                actionText  = "پرداخت بیمه کارگران",
                description = "برای مشاهده پرداخت بیمه کارگران، روی دکمه زیر بزنید."
            ),

            // ── Inbox ──────────────────────────────────────────────────────
            AgentActionKey.MESSAGE to Target(
                destination = AgentDestination.PERSONAL_INBOX,
                actionText  = "صندوق پیام شخصی",
                description = "برای مشاهده پیام‌های صندوق شخصی، روی دکمه زیر بزنید."
            )
        )
    }
}
