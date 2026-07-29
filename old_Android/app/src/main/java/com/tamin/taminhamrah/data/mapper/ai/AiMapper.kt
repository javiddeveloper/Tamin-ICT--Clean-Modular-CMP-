package com.tamin.taminhamrah.data.mapper.ai

import com.google.gson.Gson
import com.tamin.taminhamrah.data.entity.ai.AiHistoryCategoryEntity
import com.tamin.taminhamrah.data.entity.ai.CategoryWithLastMessage
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory

fun AiHistoryCategory.toEntity() = AiHistoryCategoryEntity(
    id = id,
    title = title,
    userNationalCode = this.userNationalCode,
    date = lastMessageDate,
    lastMessageDate = lastMessageDate,
    messageCount = messageCount
)

fun CategoryWithLastMessage.toDomain(gson: Gson): AiHistoryCategory {
    val lastMessageModel = try {
        if (!lastMessage.isNullOrEmpty()) {
            gson.fromJson(lastMessage, AiChatModel::class.java)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }

    return AiHistoryCategory(
        id = id,
        title = title,
        userNationalCode = userNationalCode,
        date = date,
        lastMessageDate = lastMessageDate,
        messageCount = messageCount,
    )
}
