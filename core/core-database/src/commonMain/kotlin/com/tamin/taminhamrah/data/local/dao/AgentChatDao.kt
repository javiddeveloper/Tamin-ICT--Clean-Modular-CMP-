package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tamin.taminhamrah.data.local.entity.AgentMessageEntity
import com.tamin.taminhamrah.data.local.entity.AgentSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Persistence for assistant conversations — the new-architecture equivalent of
 * old_Android's `AgentChatCategoryDao` + `AgentChatMessageDao`, merged into one DAO.
 */
@Dao
interface AgentChatDao {

    // ── Sessions ──────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: AgentSessionEntity)

    @Query("SELECT * FROM agent_sessions WHERE id = :sessionId")
    suspend fun getSession(sessionId: String): AgentSessionEntity?

    @Query(
        """
        SELECT * FROM agent_sessions
        WHERE userNationalCode = :userNationalCode
        ORDER BY lastMessageAt DESC
        """
    )
    fun observeSessions(userNationalCode: String): Flow<List<AgentSessionEntity>>

    /** One-shot list for the history sheet, newest conversation first. */
    @Query(
        """
        SELECT * FROM agent_sessions
        WHERE userNationalCode = :userNationalCode
        ORDER BY lastMessageAt DESC
        """
    )
    suspend fun getSessions(userNationalCode: String): List<AgentSessionEntity>

    /** Most recently active session for this user — used to prune it when unused. */
    @Query(
        """
        SELECT * FROM agent_sessions
        WHERE userNationalCode = :userNationalCode
        ORDER BY lastMessageAt DESC
        LIMIT 1
        """
    )
    suspend fun getLastSession(userNationalCode: String): AgentSessionEntity?

    @Query("DELETE FROM agent_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("UPDATE agent_sessions SET title = :title WHERE id = :sessionId")
    suspend fun updateSessionTitle(sessionId: String, title: String)

    @Query(
        """
        UPDATE agent_sessions
        SET lastEntity = :lastEntity, agentState = :agentState, agentHistory = :agentHistory
        WHERE id = :sessionId
        """
    )
    suspend fun updateSessionContext(
        sessionId: String,
        lastEntity: String?,
        agentState: String?,
        agentHistory: String?,
    )

    /** Bumps recency and the message counter after a message lands. */
    @Query(
        """
        UPDATE agent_sessions
        SET lastMessageAt = :lastMessageAt,
            messageCount = messageCount + 1
        WHERE id = :sessionId
        """
    )
    suspend fun touchSession(sessionId: String, lastMessageAt: Long)

    // ── Messages ──────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AgentMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<AgentMessageEntity>)

    @Query(
        """
        SELECT * FROM agent_messages
        WHERE sessionId = :sessionId
        ORDER BY messageOrder ASC, timestamp ASC
        """
    )
    fun observeMessages(sessionId: String): Flow<List<AgentMessageEntity>>

    @Query(
        """
        SELECT * FROM agent_messages
        WHERE sessionId = :sessionId
        ORDER BY messageOrder ASC, timestamp ASC
        """
    )
    suspend fun getMessages(sessionId: String): List<AgentMessageEntity>

    @Query("SELECT COUNT(*) FROM agent_messages WHERE sessionId = :sessionId")
    suspend fun countMessages(sessionId: String): Int

    @Query("UPDATE agent_messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: String)

    @Query("DELETE FROM agent_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesBySession(sessionId: String)

    /**
     * Drops everything the agent produced after the user's last message so a retry does
     * not duplicate the answer. Mirrors old_Android's `deleteMessagesAfterLastUserMessage`.
     */
    @Query(
        """
        DELETE FROM agent_messages
        WHERE sessionId = :sessionId
          AND sender = :agentSender
          AND messageOrder > COALESCE(
              (SELECT MAX(messageOrder) FROM agent_messages
               WHERE sessionId = :sessionId AND sender = :userSender),
              -1
          )
        """
    )
    suspend fun deleteMessagesAfterLastUserMessage(
        sessionId: String,
        agentSender: String = "AGENT",
        userSender: String = "USER"
    )

    /** Next order index for a session, so ordering is stable within the same millisecond. */
    @Query("SELECT COALESCE(MAX(messageOrder), -1) + 1 FROM agent_messages WHERE sessionId = :sessionId")
    suspend fun nextMessageOrder(sessionId: String): Int
}
