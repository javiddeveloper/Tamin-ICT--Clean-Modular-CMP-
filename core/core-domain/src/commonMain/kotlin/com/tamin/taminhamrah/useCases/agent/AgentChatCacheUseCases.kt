package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.model.agent.AgentCachedMessageDN
import com.tamin.taminhamrah.model.agent.AgentSessionDN
import com.tamin.taminhamrah.repository.AgentChatCacheRepository
import kotlinx.coroutines.flow.Flow

/**
 * Drops the most recent conversation when it was opened but never used, so entering the
 * assistant repeatedly does not litter the history with empty chats.
 * Mirrors old_Android's `cleanupEmptySession`.
 */
class PruneEmptyAgentSessionUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend operator fun invoke(userNationalCode: String) {
        val last = repository.getLastSession(userNationalCode) ?: return
        if (repository.countMessages(last.id) == 0) {
            repository.deleteSession(last.id)
        }
    }
}

/** Conversations for the history sheet, newest first. */
class GetAgentSessionsUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend operator fun invoke(userNationalCode: String): List<AgentSessionDN> =
        repository.getSessions(userNationalCode)
}

/** A single conversation, used to restore its server context when reopened. */
class GetAgentSessionUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend operator fun invoke(sessionId: String): AgentSessionDN? =
        repository.getSession(sessionId)
}

/** Creates and stores a new conversation. */
class StartAgentSessionUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend operator fun invoke(session: AgentSessionDN) = repository.createSession(session)
}

/** Appends a bubble to a conversation and bumps the session's recency. */
class SaveCachedMessageUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend operator fun invoke(message: AgentCachedMessageDN) = repository.addMessage(message)

    /** Next ordering index, so bubbles keep their sequence within a session. */
    suspend fun nextOrder(sessionId: String): Int = repository.nextMessageOrder(sessionId)
}

/** One-shot read of a stored conversation, used to rebuild the chat on open. */
class GetCachedMessagesUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend operator fun invoke(sessionId: String): List<AgentCachedMessageDN> =
        repository.getMessages(sessionId)
}

/** Live view of a stored conversation. */
class ObserveCachedMessagesUseCase(
    private val repository: AgentChatCacheRepository
) {
    operator fun invoke(sessionId: String): Flow<List<AgentCachedMessageDN>> =
        repository.observeMessages(sessionId)
}

/** Clears a conversation's agent replies before retrying the user's last prompt. */
class DeletePendingAgentMessagesUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend operator fun invoke(sessionId: String) =
        repository.deletePendingAgentMessages(sessionId)
}

/** Deletes a conversation and (via CASCADE) its messages. */
class DeleteAgentSessionUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend operator fun invoke(sessionId: String) = repository.deleteSession(sessionId)
}

/** Keeps the session's title and server context in sync as the chat progresses. */
class UpdateAgentSessionUseCase(
    private val repository: AgentChatCacheRepository
) {
    suspend fun title(sessionId: String, title: String) =
        repository.updateSessionTitle(sessionId, title)

    suspend fun lastEntity(sessionId: String, lastEntity: String?) =
        repository.updateSessionLastEntity(sessionId, lastEntity)
}
