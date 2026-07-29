package com.tamin.taminhamrah.ui.aiAgent.domain

import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.data.repository.ai.model.PromptModel
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum

data class ServiceResponse  (
    val action: ServiceNameEnum?,
//    val itemType: ItemType?,
    val title: String?,
    val data: ServiceData)



sealed interface ServiceData{
    data class StringMessage(val message: String): ServiceData
    object HeaderMessage: ServiceData

    data class KeyValueMessage(val message:List<KeyValueModel>): ServiceData

    data class Clickable(val message:List<KeyValueModel>,val actionType: AgentActionContent): ServiceData
    data class GroupButton(val prompts:List<PromptModel>, val actionType: AgentActionContent, val content: List<KeyValueModel>? = null): ServiceData
    data class DeeplinkWeb(val link: String): ServiceData
    data class Law(val agentLaw: AgentLaw): ServiceData
    data class GenerativeForm(
        val schema: FormSchema,
        val payload: Map<String?, String?>? = null
    ): ServiceData
}
