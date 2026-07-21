package com.tamin.taminhamrah.data.repository.ai

import com.tamin.taminhamrah.data.entity.ai.AiChatMessageEntity
import com.tamin.taminhamrah.data.entity.ai.MessageStatus
import com.tamin.taminhamrah.data.local.ai.dao.AgentChatMessageDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val dao: AgentChatMessageDao
) : ChatRepository {

    override fun getMessages(sessionId: String): Flow<List<AiChatMessageEntity>> {
        return dao.getMessagesBySessionId(sessionId)
    }

    override suspend fun insertMessage(message: AiChatMessageEntity): Long {
        return dao.insertMessage(message)
    }

    override suspend fun getMessageById(id: Long): AiChatMessageEntity? {
        return dao.getMessageById(id)
    }

    override suspend fun updateMessage(message: AiChatMessageEntity) {
        dao.insertMessage(message) // Insert with REPLACE strategy acts as update
    }

    override suspend fun updateMessageStatus(id: Long, status: MessageStatus) {
        dao.updateMessageStatus(id, status)
    }

    override suspend fun deleteSessionMessages(sessionId: String) {
        dao.deleteMessagesBySessionId(sessionId)
    }

    override suspend fun deletePendingAiMessages(sessionId: String) {
        dao.deleteMessagesAfterLastUserMessage(sessionId)
    }

    override suspend fun getLastSessionId(): String? {
        return dao.getLastSessionId()
    }
}
