package com.tamin.taminhamrah.data.local.ai.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tamin.taminhamrah.data.entity.ai.AiChatMessageEntity
import com.tamin.taminhamrah.data.entity.ai.MessageSender
import com.tamin.taminhamrah.data.entity.ai.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<AiChatMessageEntity>)

    @Query("SELECT * FROM ai_chat_messages WHERE sessionId = :sessionId ORDER BY timeStamp ASC")
    fun getMessagesBySessionId(sessionId: String): Flow<List<AiChatMessageEntity>>

    @Query("SELECT * FROM ai_chat_messages WHERE sessionId = :sessionId ORDER BY timeStamp ASC")
    suspend fun getMessagesBySessionIdOneShot(sessionId: String): List<AiChatMessageEntity>

    @Query("DELETE FROM ai_chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesBySessionId(sessionId: String)

    @Query("DELETE FROM ai_chat_messages")
    suspend fun deleteAllMessages()

    @Query("SELECT * FROM ai_chat_messages WHERE id = :id")
    suspend fun getMessageById(id: Long): AiChatMessageEntity?

    @Query("UPDATE ai_chat_messages SET status = :status WHERE id = :id")
    suspend fun updateMessageStatus(id: Long, status: MessageStatus)

    @Query("DELETE FROM ai_chat_messages WHERE sessionId = :sessionId AND sender = :sender AND id > (SELECT MAX(id) FROM ai_chat_messages WHERE sessionId = :sessionId AND sender = :userSender)")
    suspend fun deleteMessagesAfterLastUserMessage(
        sessionId: String,
        sender: MessageSender = MessageSender.AI,
        userSender: MessageSender = MessageSender.USER
    )

    @Query("SELECT sessionId FROM ai_chat_messages ORDER BY timeStamp DESC LIMIT 1")
    suspend fun getLastSessionId(): String?
}
