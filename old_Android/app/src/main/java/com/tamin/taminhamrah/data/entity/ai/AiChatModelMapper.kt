package com.tamin.taminhamrah.data.entity.ai

import com.google.gson.Gson
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiErrorModel
import com.tamin.taminhamrah.data.repository.ai.model.TypingAnimatable
import com.tamin.taminhamrah.utils.extentions.randomUUID
import javax.inject.Inject

class AiChatModelMapper @Inject constructor(
    private val gson: Gson
) {
    fun toEntity(model: AiChatModel, categoryId: String): ChatMessageEntity {
        return ChatMessageEntity(
            id = if (model is TypingAnimatable) model.id else randomUUID(),
            categoryId = categoryId,
            messageType = model::class.java.simpleName,
            jsonData = gson.toJson(model, AiChatModel::class.java),
            isError = model.isError,
            timestamp = System.currentTimeMillis(),
            messageOrder = 0
        )
    }


    fun fromEntity(entity: ChatMessageEntity): AiChatModel? {
        return try {
            gson.fromJson(entity.jsonData, AiChatModel::class.java)
                .apply { isError = entity.isError }
        } catch (e: Exception) {
            null
        }
    }

    fun fromEntityOrPlaceholder(entity: ChatMessageEntity): AiChatModel {
        return fromEntity(entity) ?: AiErrorModel(message = "خطا در پردازش پیام")
    }

    fun fromEntityList(entities: List<ChatMessageEntity>): List<AiChatModel> {
        return entities.mapNotNull { fromEntity(it) }
    }
}
