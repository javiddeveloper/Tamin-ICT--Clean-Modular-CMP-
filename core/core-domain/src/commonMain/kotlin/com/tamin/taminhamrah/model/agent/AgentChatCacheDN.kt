package com.tamin.taminhamrah.model.agent

/** Who produced a cached chat bubble. */
enum class CachedSender { USER, AGENT }

/** Delivery state of a cached message, used to recover from interrupted sends. */
enum class CachedStatus { SENDING, SUCCESS, FAILED }

/**
 * A stored assistant conversation.
 *
 * @param lastEntity server conversation context, replayed into the next request so a
 *   resumed chat keeps its thread.
 */
data class AgentSessionDN(
    val id: String,
    val title: String,
    val userNationalCode: String,
    val createdAt: Long,
    val lastMessageAt: Long,
    val messageCount: Int = 0,
    val lastEntity: String? = null
)

/**
 * A stored chat bubble.
 *
 * [contentType] / [contentJson] hold the bubble opaquely — only the agent feature knows
 * how to encode and decode them, which keeps the bubble type out of the data layer.
 */
data class AgentCachedMessageDN(
    val id: String,
    val sessionId: String,
    val sender: CachedSender,
    val status: CachedStatus,
    val contentType: String,
    val contentJson: String,
    val voicePath: String? = null,
    val timestamp: Long,
    val messageOrder: Int = 0
)
