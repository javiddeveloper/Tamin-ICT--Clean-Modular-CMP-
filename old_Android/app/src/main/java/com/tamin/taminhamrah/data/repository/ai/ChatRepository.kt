package com.tamin.taminhamrah.data.repository.ai

import com.tamin.taminhamrah.data.entity.ai.AiChatMessageEntity
import com.tamin.taminhamrah.data.entity.ai.MessageStatus
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(sessionId: String): Flow<List<AiChatMessageEntity>>
    suspend fun insertMessage(message: AiChatMessageEntity): Long
    suspend fun getMessageById(id: Long): AiChatMessageEntity?
    suspend fun updateMessage(message: AiChatMessageEntity)
    suspend fun updateMessageStatus(id: Long, status: MessageStatus)
    suspend fun deleteSessionMessages(sessionId: String)
    suspend fun deletePendingAiMessages(sessionId: String)
    suspend fun getLastSessionId(): String?
}
