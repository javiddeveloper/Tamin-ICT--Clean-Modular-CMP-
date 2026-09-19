package com.tamin.taminhamrah.data.repository.agent

import com.tamin.taminhamrah.data.local.dao.AgentChatDao
import com.tamin.taminhamrah.data.local.entity.AgentMessageEntity
import com.tamin.taminhamrah.data.local.entity.AgentSessionEntity
import com.tamin.taminhamrah.model.agent.AgentCachedMessageDN
import com.tamin.taminhamrah.model.agent.AgentSessionDN
import com.tamin.taminhamrah.model.agent.CachedSender
import com.tamin.taminhamrah.model.agent.CachedStatus
import com.tamin.taminhamrah.repository.AgentChatCacheRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed implementation of [AgentChatCacheRepository].
 * Follows the project's existing pattern of a core-data repository over a core-database DAO.
 */
class AgentChatCacheRepositoryImpl(
    private val dao: AgentChatDao
) : AgentChatCacheRepository {

    // ── Sessions ──────────────────────────────────────────────────────────────

    override suspend fun createSession(session: AgentSessionDN) =
        dao.upsertSession(session.toEntity())

    override suspend fun getSession(sessionId: String): AgentSessionDN? =
        dao.getSession(sessionId)?.toDomain()

    override suspend fun getLastSession(userNationalCode: String): AgentSessionDN? =
        dao.getLastSession(userNationalCode)?.toDomain()

    override fun observeSessions(userNationalCode: String): Flow<List<AgentSessionDN>> =
        dao.observeSessions(userNationalCode).map { list -> list.map { it.toDomain() } }

    override suspend fun getSessions(userNationalCode: String): List<AgentSessionDN> =
        dao.getSessions(userNationalCode).map { it.toDomain() }

    override suspend fun deleteSession(sessionId: String) = dao.deleteSession(sessionId)

    override suspend fun updateSessionTitle(sessionId: String, title: String) =
        dao.updateSessionTitle(sessionId, title)

    override suspend fun updateSessionContext(sessionId: String, lastEntity: String?, state: String?, history: String?) =
        dao.updateSessionContext(sessionId, lastEntity, state, history)

    // ── Messages ──────────────────────────────────────────────────────────────

    override suspend fun addMessage(message: AgentCachedMessageDN) {
        dao.insertMessage(message.toEntity())
        // Keep the session list ordered by recency and its counter accurate.
        dao.touchSession(message.sessionId, message.timestamp)
    }

    override fun observeMessages(sessionId: String): Flow<List<AgentCachedMessageDN>> =
        dao.observeMessages(sessionId).map { list -> list.map { it.toDomain() } }

    override suspend fun getMessages(sessionId: String): List<AgentCachedMessageDN> =
        dao.getMessages(sessionId).map { it.toDomain() }

    override suspend fun countMessages(sessionId: String): Int = dao.countMessages(sessionId)

    override suspend fun updateMessageStatus(messageId: String, status: CachedStatus) =
        dao.updateMessageStatus(messageId, status.name)

    override suspend fun deleteMessages(sessionId: String) = dao.deleteMessagesBySession(sessionId)

    override suspend fun deletePendingAgentMessages(sessionId: String) =
        dao.deleteMessagesAfterLastUserMessage(sessionId)

    override suspend fun nextMessageOrder(sessionId: String): Int = dao.nextMessageOrder(sessionId)
}

// ─── Mappers ──────────────────────────────────────────────────────────────────

private fun AgentSessionDN.toEntity() = AgentSessionEntity(
    id = id,
    title = title,
    userNationalCode = userNationalCode,
    createdAt = createdAt,
    lastMessageAt = lastMessageAt,
    messageCount = messageCount,
    lastEntity = lastEntity,
    agentState = state,
    agentHistory = history,
)

private fun AgentSessionEntity.toDomain() = AgentSessionDN(
    id = id,
    title = title,
    userNationalCode = userNationalCode,
    createdAt = createdAt,
    lastMessageAt = lastMessageAt,
    messageCount = messageCount,
    lastEntity = lastEntity,
    state = agentState,
    history = agentHistory,
)

private fun AgentCachedMessageDN.toEntity() = AgentMessageEntity(
    id = id,
    sessionId = sessionId,
    sender = sender.name,
    status = status.name,
    contentType = contentType,
    contentJson = contentJson,
    voicePath = voicePath,
    timestamp = timestamp,
    messageOrder = messageOrder
)

private fun AgentMessageEntity.toDomain() = AgentCachedMessageDN(
    id = id,
    sessionId = sessionId,
    // Unknown values can only come from a hand-edited DB; fall back rather than crash.
    sender = runCatching { CachedSender.valueOf(sender) }.getOrDefault(CachedSender.AGENT),
    status = runCatching { CachedStatus.valueOf(status) }.getOrDefault(CachedStatus.SUCCESS),
    contentType = contentType,
    contentJson = contentJson,
    voicePath = voicePath,
    timestamp = timestamp,
    messageOrder = messageOrder
)
