package com.tamin.taminhamrah.ui.aiAgent.domain

import com.tamin.taminhamrah.data.remote.models.ai.agent.Payload

data class AgentLawResponse(
    val lastEntity: String?,
    val sessionId: String?,
    val entities: List<LawsEntity?>?,

)
data class LawsEntity(
    val key: String?,
    val itemType: String?,
    val moveNewSession: Boolean?,
    val data: List<AgentLaw?>?,
    val failedMessage: String?,
    val hasData: Boolean?,
    val messageId: String?,
    val payload: Payload?,
    val stepNumber: Int?,
    val successMessage: String?
    )

data class AgentLaw(
    val url: String,
    val content: String?,
    val name: String?,
    val reference: String?,
    val score: Double?,
    val sourceFile: String?,
)



