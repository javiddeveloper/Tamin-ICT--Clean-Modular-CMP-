package com.tamin.taminhamrah.data.entity.ai

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tamin.taminhamrah.data.repository.ai.model.AiHistoryCategory
import com.tamin.taminhamrah.utils.extentions.randomUUID


@Entity(tableName = "aicategory")
data class AiHistoryCategoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val date: Long,
    val userNationalCode: String,
    val lastMessageDate: Long = date,
    val messageCount: Int = 0
)


fun AiHistoryCategoryEntity.toDomain() = AiHistoryCategory(
    id = id,
    title = title,
    date = date,
    userNationalCode = this.userNationalCode,
    lastMessageDate = lastMessageDate,
    messageCount = messageCount,
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = AiHistoryCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"]), Index(value = ["timestamp"])]
)
data class ChatMessageEntity(
    @PrimaryKey val id: String = randomUUID(),
    val categoryId: String,
    val messageType: String,
    val jsonData: String,
    val isError: Boolean = false,
    val timestamp: Long,
    val messageOrder: Int = 0
)


data class CategoryWithLastMessage(
    val id: String,
    val title: String,
    val date: Long,
    val lastMessageDate: Long,
    val messageCount: Int,
    val userNationalCode: String,
    val lastMessage: String? // JSON string of last message
)



fun List<AiHistoryCategoryEntity>.toDomain(): List<AiHistoryCategory> {
    return this.map { entity ->
        entity.toDomain()
    }
}
