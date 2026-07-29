package com.tamin.taminhamrah.data.remote.models.ai.agent

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.ui.aiAgent.domain.AgentLawResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.LawsEntity

data class AgentLawsDTO(
    @SerializedName("lastEntity") var lastEntity: String? = null,
    @SerializedName("session_id") var sessionId: String? = null,
    @SerializedName("entities") var entities: List<LawEntity?>? = null
)

data class LawEntity(
    @SerializedName("key") val key: String?,
    @SerializedName("item_type") val itemType: String?,
    @SerializedName("move_new_session") val moveNewSession: Boolean?,
    @SerializedName("data") val data: List<AgentLawDto>?,
    @SerializedName("failed_message") val failedMessage: String?,
    @SerializedName("has_data") val hasData: Boolean?,
    @SerializedName("message_id") val messageId: String?,
    @SerializedName("payload") val payload: Payload?,
    @SerializedName("step_number") val stepNumber: Int?,
    @SerializedName("success_message") val successMessage: String?
)


fun AgentLawsDTO.toDomain() = AgentLawResponse(
    lastEntity = lastEntity,
    sessionId = sessionId,
    entities = entities?.map { it?.toDomain() },
)

fun LawEntity.toDomain() = LawsEntity(
    key = key,
    itemType = itemType,
    moveNewSession = moveNewSession,
    data = data?.map { it.toDomain() },
    failedMessage = failedMessage,
    hasData = hasData,
    messageId = messageId,
    payload = payload,
    stepNumber = stepNumber,
    successMessage = successMessage,
)


private fun Payload.toMap(): Map<String, Any?> {
    val processedFilter = this.filter?.mapNotNull { filterItem ->
        if (filterItem == null) return@mapNotNull null

        val parts = filterItem.split(":", limit = 2)
        if (parts.size != 2) return@mapNotNull filterItem // Return as-is if malformed

        val key = parts[0]
        var value = parts[1]

        if (value.startsWith("@")) {
            val placeholder = value.substring(1)
            value = when (placeholder) {
                "USER_ID" -> ""
                "nationalID" -> ""
                else -> value // If placeholder is unknown, keep it as-is
            }
        }
        "$key:$value"
    }

    // Return the map with the newly processed filter list
    return mapOf(
        "filter" to processedFilter,
//        "itemType" to this.itemType,
    )
}
