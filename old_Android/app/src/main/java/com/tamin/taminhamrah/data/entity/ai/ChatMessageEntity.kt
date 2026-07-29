package com.tamin.taminhamrah.data.entity.ai

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.tamin.taminhamrah.data.entity.ai.convertor.ServiceResponseConverter
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse

@Entity(tableName = "ai_chat_messages")
@TypeConverters(ServiceResponseConverter::class)
data class AiChatMessageEntity (
    @PrimaryKey(autoGenerate = true)
    val id : Long =0,
    val message : String?,
    val voicePath: String? = null,
    val serviceResponses: List<ServiceResponse>? = null,
    val sender: MessageSender,
    val status: MessageStatus,
    val sessionId: String,
    val lastEntity: String?,
    val timeStamp: Long = System.currentTimeMillis()

)
enum class MessageSender {
    USER,
    AI
}

enum class MessageStatus { SENDING, SUCCESS, FAILED}
