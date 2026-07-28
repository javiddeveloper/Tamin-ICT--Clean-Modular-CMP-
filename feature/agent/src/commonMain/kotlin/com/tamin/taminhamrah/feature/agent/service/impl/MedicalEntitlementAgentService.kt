package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.orDash
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDeservedTreatmentUseCase
import kotlinx.coroutines.flow.firstOrNull

/**
 * Medical entitlement / booklet status — استحقاق درمان (دفترچه).
 *
 * Ported from old_Android's `MedicalEntitlementUseCase`. The national code is taken
 * from the user's identity info since the endpoint is keyed by it.
 */
class MedicalEntitlementAgentService(
    private val getDeservedTreatmentUseCase: GetDeservedTreatmentUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.BOOKLET)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        return try {
            val nationalCode = identityInfoUseCase().firstOrNull()?.nationalId
            if (nationalCode.isNullOrBlank()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text("کد ملی شما در دسترس نیست."))
                    }
                )
            }

            val list = getDeservedTreatmentUseCase(nationalCode).firstOrNull().orEmpty()
            if (list.isEmpty()) {
                return AgentServiceResult.Success(
                    params.buildBubbles {
                        add(ChatBubbleContent.Text(params.message ?: "اطلاعات استحقاق درمان یافت نشد."))
                    }
                )
            }

            val rows = mutableListOf<Pair<String, String>>()
            list.forEachIndexed { index, item ->
                rows.add("نام و نام خانوادگی" to
                    "${item.firstName.orDash()} ${item.lastName.orDash()}")
                rows.add("کد ملی" to (item.nationalId ?: item.natCode).orDash())
                rows.add("نسبت" to item.dependenceType.orDash())
                rows.add("نوع بیمه" to item.insuranceType.orDash())
                rows.add("نام شعبه" to item.brhName.orDash())
                rows.add("استان" to item.provinceName.orDash())
                rows.add("کارگاه" to item.regWorkshopName.orDash())
                rows.add("فرانشیز" to item.feranshiz.orDash())
                item.lastBookletDate?.takeIf { it.isNotBlank() }
                    ?.let { rows.add("تاریخ اعتبار دفترچه" to it) }
                item.message?.takeIf { it.isNotBlank() }
                    ?.let { rows.add("وضعیت" to it) }
                if (index < list.lastIndex) rows.add(ROW_SEPARATOR to "")
            }

            AgentServiceResult.Success(
                params.buildBubbles {
                    add(
                        ChatBubbleContent.KeyValue(
                            title = params.message?.takeIf { it.isNotBlank() } ?: "استحقاق درمان",
                            items = rows
                        )
                    )
                }
            )
        } catch (e: Exception) {
            AgentServiceResult.Error("خطا در دریافت استحقاق درمان: ${e.message}", e)
        }
    }

    private companion object {
        const val ROW_SEPARATOR = "----------------"
    }
}
