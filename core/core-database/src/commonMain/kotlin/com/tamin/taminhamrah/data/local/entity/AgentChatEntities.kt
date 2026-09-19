package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One assistant conversation. Ported from old_Android's `aicategory` table so a chat
 * history UI can be built on top later; for now it lets the app resume the last chat.
 */
@Entity(tableName = "agent_sessions")
data class AgentSessionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    /** Scopes sessions per signed-in user, exactly like the legacy table did. */
    val userNationalCode: String,
    val createdAt: Long,
    val lastMessageAt: Long,
    val messageCount: Int = 0,
    /** Server conversation context carried into the next request. */
    val lastEntity: String? = null,
    /** The server's conversation state and history, as the JSON it sent. */
    val agentState: String? = null,
    val agentHistory: String? = null,
)

/**
 * A single chat bubble. Ported from old_Android's `ai_chat_messages`.
 *
 * The bubble payload is stored opaquely as [contentType] + [contentJson]: the database
 * module must not depend on the agent feature, and the feature's `ChatBubbleContent`
 * carries `Any` fields that cannot be serialized. `ChatBubbleCodec` in the feature owns
 * the mapping.
 */
@Entity(
    tableName = "agent_messages",
    foreignKeys = [
        ForeignKey(
            entity = AgentSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"]), Index(value = ["timestamp"])]
)
data class AgentMessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    /** "USER" or "AGENT" — stored as text to keep the schema converter-free. */
    val sender: String,
    /** "SENDING", "SUCCESS" or "FAILED". */
    val status: String,
    val contentType: String,
    val contentJson: String,
    val voicePath: String? = null,
    val timestamp: Long,
    val messageOrder: Int = 0
)
