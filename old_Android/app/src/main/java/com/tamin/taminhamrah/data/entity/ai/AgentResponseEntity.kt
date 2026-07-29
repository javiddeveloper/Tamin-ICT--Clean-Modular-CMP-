package com.tamin.taminhamrah.data.entity.ai

import androidx.room.PrimaryKey
import com.tamin.taminhamrah.ui.aiAgent.domain.AgentResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.AiEntity
import com.tamin.taminhamrah.ui.aiAgent.domain.ItemType
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum

data class AgentResponseEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var lastEntity: String? = null,
    var sessionId: String? = null,
    var entities: List<AgentEntity>? = null
)

data class AgentEntity(
    val key: String?,
    val itemType: String?,
    val moveNewSession: Boolean?,
//    val data: List<JsonObject?>?,
    val hasData: Boolean?,
    val messageId: String?,
    val payload: PayloadEntity?,
    val stepNumber: Int?,
    val successMessage: String?
)

data class PayloadEntity(
    val filter: List<String?>?,
    val itemType: Int?,
    val start: Int?
)

fun AgentResponseEntity.toDomain(): AgentResponse {
    val mappedEntities = this.entities?.mapNotNull { entityDto ->
        entityDto.key?.let { key ->
            AiEntity(
                action = ServiceNameEnum.fromString(key),
                itemType = ItemType.fromString(entityDto.itemType),
                data = null,
//                data = entityDto.data?.mapNotNull {
//                    it?.let { jsonObject ->
//                        val type =
//                            object : com.google.gson.reflect.TypeToken<Map<String, Any?>>() {}.type
//                        gson.fromJson<Map<String, Any?>>(jsonObject, type)
//                    }
//                },
                payload = null,
//                payload = entityDto.payload?.toMap(),
                message = entityDto.successMessage,
            )
        }
    } ?: emptyList()

    return AgentResponse(
        lastEntity = lastEntity,
        sessionId = sessionId,
        entities = mappedEntities,
    )
}

fun PayloadEntity.toMap(): Map<String, Any?> {
    val processedFilter = this.filter?.mapNotNull { filterItem ->
        if (filterItem == null) return@mapNotNull null
        val parts = filterItem.split(":", limit = 2)
        if (parts.size != 2) return@mapNotNull filterItem
        val key = parts[0]
        var value = parts[1]

        if (value.startsWith("@")) {
            val placeholder = value.substring(1)
            value = when (placeholder) {
                "USER_ID" -> ""
                "nationalID" -> ""
                else -> value
            }
        }
        "$key:$value"
    }

    return mapOf(
        "filter" to processedFilter,
//        "itemType" to this.itemType,
//        "start" to this.start,
    )
}
