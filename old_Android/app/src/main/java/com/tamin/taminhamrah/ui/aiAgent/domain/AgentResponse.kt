package com.tamin.taminhamrah.ui.aiAgent.domain

import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentResponseData
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum

data class AgentResponse(
    val sessionId: String?,
    val lastEntity: String?,
    val entities: List<AiEntity>,
    val message: String? = null
)

data class AiEntity(
    val action: ServiceNameEnum?,
    val itemType: ItemType?,
    val data: List<AgentResponseData>?,
    val payload: Map<String, Any?>?,
    val message: String?,
)
