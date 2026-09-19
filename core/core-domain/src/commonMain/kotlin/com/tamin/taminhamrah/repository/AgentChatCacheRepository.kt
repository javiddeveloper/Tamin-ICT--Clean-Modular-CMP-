package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.agent.AgentCachedMessageDN
import com.tamin.taminhamrah.model.agent.AgentSessionDN
import com.tamin.taminhamrah.model.agent.CachedStatus
import kotlinx.coroutines.flow.Flow

/**
 * Local persistence for assistant conversations, so a chat survives leaving the screen
 * or restarting the app. Ported from old_Android's ChatRepository + AiHistoryRepository.
 */
interface AgentChatCacheRepository {

    // ── Sessions ──
    suspend fun createSession(session: AgentSessionDN)
    suspend fun getSession(sessionId: String): AgentSessionDN?
    /** Most recent conversation for the user, used to resume on open. */
    suspend fun getLastSession(userNationalCode: String): AgentSessionDN?
    fun observeSessions(userNationalCode: String): Flow<List<AgentSessionDN>>
    /** One-shot list for the history sheet, newest first. */
    suspend fun getSessions(userNationalCode: String): List<AgentSessionDN>
    suspend fun deleteSession(sessionId: String)
    suspend fun updateSessionTitle(sessionId: String, title: String)
    suspend fun updateSessionContext(sessionId: String, lastEntity: String?, state: String?, history: String?)

    // ── Messages ──
    suspend fun addMessage(message: AgentCachedMessageDN)
    fun observeMessages(sessionId: String): Flow<List<AgentCachedMessageDN>>
    suspend fun getMessages(sessionId: String): List<AgentCachedMessageDN>
    suspend fun countMessages(sessionId: String): Int
    suspend fun updateMessageStatus(messageId: String, status: CachedStatus)
    suspend fun deleteMessages(sessionId: String)
    /** Removes agent replies after the user's last message so a retry does not duplicate. */
    suspend fun deletePendingAgentMessages(sessionId: String)
    /** Next stable ordering index within a session. */
    suspend fun nextMessageOrder(sessionId: String): Int
}
