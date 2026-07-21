package com.tamin.taminhamrah.data.remote.models.ai.agent

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.ui.aiAgent.domain.AgentResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.AiEntity
import com.tamin.taminhamrah.ui.aiAgent.domain.ItemType
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum

data class AgentResponseDTO(
    @SerializedName("lastEntity")
    var lastEntity: String? = null,
    @SerializedName("sessionId", alternate = ["session_id"])
    var sessionId: String? = null,
    @SerializedName("entities")
    var entities: List<Entity?>? = null
)

data class Entity(
    @SerializedName("key")
    val key: String?,
    @SerializedName("item_type")
    val itemType: String?,
    @SerializedName("data")
    val data: List<AgentResponseData?>?,
    @SerializedName("message_id")
    val messageId: String?,
    @SerializedName("payload")
    val payload: Payload?,
    @SerializedName("step_number")
    val stepNumber: Int?,
    @SerializedName("message")
    val message: String?,
)

data class Payload(
    @SerializedName("filter")
    val filter: List<String?>?,
    @SerializedName("item_type")
    val itemType: Int?,
)

class Sort

fun AgentResponseDTO.toDomain(/*message: String? = null*/): AgentResponse {
    val mappedEntities = this.entities?.mapNotNull { entityDto ->
        entityDto?.key?.let { key ->
            AiEntity(
                action = ServiceNameEnum.fromString(key),
                itemType = ItemType.fromString(entityDto.itemType),
                data = entityDto.data?.filterNotNull(),
                payload = entityDto.payload?.toMap(),
                message = entityDto.message,
            )
        }
    } ?: emptyList()

    return AgentResponse(
        lastEntity = this.lastEntity,
        sessionId = sessionId,
        entities = mappedEntities,
//        message = message
    )
}

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
