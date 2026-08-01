package com.tamin.taminhamrah.feature.agent.cache

import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Persists a [ChatBubbleContent] as the opaque `(contentType, contentJson)` pair the
 * database stores, and reads it back.
 *
 * Because the bubble hierarchy is a `@Serializable` sealed interface, this needs no
 * per-type mapping: kotlinx.serialization writes a `type` discriminator from each
 * class's `@SerialName`. **A new bubble type works here automatically** — the only
 * requirement is that its serial name never changes once released, since old rows
 * carry it.
 *
 * [contentTypeOf] is stored alongside the JSON purely so rows stay greppable and
 * queryable; decoding relies on the discriminator inside the payload.
 */
object ChatBubbleCodec {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        // Newer app versions may add bubble types this build has never heard of; a
        // stored row must not blow up the whole conversation when it is reopened.
        classDiscriminator = "type"
    }

    /** @return the stored pair, or null when the bubble cannot be serialized. */
    fun encode(content: ChatBubbleContent): Pair<String, String>? = runCatching {
        val element = json.encodeToJsonElement(ChatBubbleContent.serializer(), content)
        discriminatorOf(element) to element.toString()
    }.getOrNull()

    /** @return the bubble, or null when the row is unknown, corrupt, or from a newer build. */
    fun decode(contentType: String, contentJson: String): ChatBubbleContent? = runCatching {
        json.decodeFromString(ChatBubbleContent.serializer(), contentJson)
    }.getOrNull()

    /** The stable serial name recorded for [content], used as the row's `contentType`. */
    fun contentTypeOf(content: ChatBubbleContent): String = runCatching {
        discriminatorOf(json.encodeToJsonElement(ChatBubbleContent.serializer(), content))
    }.getOrDefault(UNKNOWN_TYPE)

    /** Reads the `type` discriminator kotlinx.serialization wrote from the `@SerialName`. */
    private fun discriminatorOf(element: JsonElement): String =
        (element as? JsonObject)?.get("type")?.jsonPrimitive?.contentOrNull ?: UNKNOWN_TYPE

    private const val UNKNOWN_TYPE = "unknown"
}
