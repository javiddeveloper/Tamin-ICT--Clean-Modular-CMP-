package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentStrings
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDeservedTreatmentUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_empty_medical_entitlement
import taminx.core.core_ui.agent_error_medical_entitlement
import taminx.core.core_ui.agent_error_national_code
import taminx.core.core_ui.agent_label_booklet_validity
import taminx.core.core_ui.agent_label_branch
import taminx.core.core_ui.agent_label_franchise
import taminx.core.core_ui.agent_label_full_name
import taminx.core.core_ui.agent_label_insurance_type
import taminx.core.core_ui.agent_label_national_code
import taminx.core.core_ui.agent_label_province
import taminx.core.core_ui.agent_label_relation
import taminx.core.core_ui.agent_label_status
import taminx.core.core_ui.agent_label_workshop

/** Treatment entitlement — استحقاق درمان — ported from the native `MedicalEntitlementUseCase`. */
class MedicalEntitlementAgentService(
    private val getDeservedTreatmentUseCase: GetDeservedTreatmentUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val strings: AgentStrings,
) : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.BOOKLET)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult = try {
        val nationalCode = identityInfoUseCase().firstOrNull()?.nationalId
        if (nationalCode.isNullOrBlank()) {
            AgentServiceResult.Error(strings.get(Res.string.agent_error_national_code))
        } else {
            val list = getDeservedTreatmentUseCase(nationalCode).firstOrNull().orEmpty()
            val markdown = agentMarkdown {
                heading(params.message)
                if (list.isEmpty()) paragraph(strings.get(Res.string.agent_empty_medical_entitlement))
                list.forEach { item ->
                    fields(
                        listOf(
                            strings.get(Res.string.agent_label_full_name) to listOfNotNull(item.firstName, item.lastName).joinToString(" "),
                            strings.get(Res.string.agent_label_national_code) to (item.nationalId ?: item.natCode),
                            strings.get(Res.string.agent_label_relation) to item.dependenceType,
                            strings.get(Res.string.agent_label_insurance_type) to item.insuranceType,
                            strings.get(Res.string.agent_label_branch) to item.brhName,
                            strings.get(Res.string.agent_label_province) to item.provinceName,
                            strings.get(Res.string.agent_label_workshop) to item.regWorkshopName,
                            strings.get(Res.string.agent_label_franchise) to item.feranshiz,
                            strings.get(Res.string.agent_label_booklet_validity) to item.lastBookletDate,
                            strings.get(Res.string.agent_label_status) to item.message,
                        )
                    )
                    rule()
                }
            }
            AgentServiceResult.Success(listOf(ChatBubbleContent.Markdown(markdown)))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        AgentServiceResult.Error(strings.get(Res.string.agent_error_medical_entitlement), e)
    }
}
