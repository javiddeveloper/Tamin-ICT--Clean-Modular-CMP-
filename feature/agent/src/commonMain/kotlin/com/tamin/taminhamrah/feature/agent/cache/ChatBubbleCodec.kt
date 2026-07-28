package com.tamin.taminhamrah.feature.agent.cache

import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Converts a [ChatBubbleContent] to and from the opaque `(contentType, contentJson)` pair
 * the database stores.
 *
 * This lives in the agent feature because it is the only module that knows the bubble
 * type; the data layer deliberately treats the payload as a blob. Bubbles carrying `Any`
 * (EmbeddedModel / Chart / DynamicForm) cannot be serialized and are skipped — [encode]
 * returns null for them and the caller simply does not persist that bubble.
 */
object ChatBubbleCodec {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // Type tags are persisted, so treat them as a stable on-disk contract.
    private const val TYPE_TEXT = "text"
    private const val TYPE_KEY_VALUE = "key_value"
    private const val TYPE_SUGGESTED = "suggested_prompts"
    private const val TYPE_VOICE = "voice"
    private const val TYPE_DEEP_LINK = "deep_link"
    private const val TYPE_WEB_LINK = "web_link"
    private const val TYPE_IMAGE = "image"
    private const val TYPE_PROCESSING = "processing_steps"
    private const val TYPE_ERROR = "service_error"

    /** @return the stored pair, or null when this bubble type is not persistable. */
    fun encode(content: ChatBubbleContent): Pair<String, String>? = when (content) {
        is ChatBubbleContent.Text ->
            TYPE_TEXT to json.encodeToString(TextDto(content.message))

        is ChatBubbleContent.KeyValue ->
            TYPE_KEY_VALUE to json.encodeToString(
                KeyValueDto(content.title, content.items.map { ItemDto(it.first, it.second) })
            )

        is ChatBubbleContent.SuggestedPrompts ->
            TYPE_SUGGESTED to json.encodeToString(SuggestedDto(content.prompts))

        is ChatBubbleContent.Voice ->
            TYPE_VOICE to json.encodeToString(
                VoiceDto(content.path, content.durationMs, content.amplitudes)
            )

        is ChatBubbleContent.DeepLink ->
            TYPE_DEEP_LINK to json.encodeToString(LinkDto(content.title, content.destination))

        is ChatBubbleContent.WebLink ->
            TYPE_WEB_LINK to json.encodeToString(LinkDto(content.title, content.url))

        is ChatBubbleContent.Image ->
            TYPE_IMAGE to json.encodeToString(ImageDto(content.url, content.caption))

        is ChatBubbleContent.ProcessingSteps ->
            TYPE_PROCESSING to json.encodeToString(
                ProcessingDto(content.steps, content.currentActiveIndex, content.isCompleted)
            )

        is ChatBubbleContent.ServiceError ->
            TYPE_ERROR to json.encodeToString(
                ErrorDto(content.message, content.canRetryPrompt, content.actionKey?.key)
            )

        // Hold arbitrary domain models — not serializable, so they are not cached.
        is ChatBubbleContent.EmbeddedModel,
        is ChatBubbleContent.Chart,
        is ChatBubbleContent.DynamicForm -> null
    }

    /** @return the bubble, or null when the row is unknown or corrupt. */
    fun decode(contentType: String, contentJson: String): ChatBubbleContent? = runCatching {
        when (contentType) {
            TYPE_TEXT ->
                ChatBubbleContent.Text(json.decodeFromString<TextDto>(contentJson).message)

            TYPE_KEY_VALUE -> json.decodeFromString<KeyValueDto>(contentJson).let { dto ->
                ChatBubbleContent.KeyValue(dto.title, dto.items.map { it.key to it.value })
            }

            TYPE_SUGGESTED ->
                ChatBubbleContent.SuggestedPrompts(json.decodeFromString<SuggestedDto>(contentJson).prompts)

            TYPE_VOICE -> json.decodeFromString<VoiceDto>(contentJson).let { dto ->
                ChatBubbleContent.Voice(dto.path, dto.durationMs, dto.amplitudes)
            }

            TYPE_DEEP_LINK -> json.decodeFromString<LinkDto>(contentJson).let { dto ->
                ChatBubbleContent.DeepLink(dto.title, dto.target)
            }

            TYPE_WEB_LINK -> json.decodeFromString<LinkDto>(contentJson).let { dto ->
                ChatBubbleContent.WebLink(dto.title, dto.target)
            }

            TYPE_IMAGE -> json.decodeFromString<ImageDto>(contentJson).let { dto ->
                ChatBubbleContent.Image(dto.url, dto.caption)
            }

            TYPE_PROCESSING -> json.decodeFromString<ProcessingDto>(contentJson).let { dto ->
                ChatBubbleContent.ProcessingSteps(dto.steps, dto.currentActiveIndex, dto.isCompleted)
            }

            TYPE_ERROR -> json.decodeFromString<ErrorDto>(contentJson).let { dto ->
                ChatBubbleContent.ServiceError(
                    message = dto.message,
                    canRetryPrompt = dto.canRetryPrompt,
                    actionKey = dto.actionKey?.let { AgentActionKey.fromString(it) }
                )
            }

            else -> null
        }
    }.getOrNull()

    // ── Wire formats ──────────────────────────────────────────────────────────

    @Serializable private data class TextDto(val message: String)
    @Serializable private data class ItemDto(val key: String, val value: String)
    @Serializable private data class KeyValueDto(val title: String?, val items: List<ItemDto>)
    @Serializable private data class SuggestedDto(val prompts: List<String>)
    @Serializable private data class VoiceDto(
        val path: String,
        val durationMs: Long?,
        val amplitudes: List<Int> = emptyList()
    )
    @Serializable private data class LinkDto(val title: String, val target: String)
    @Serializable private data class ImageDto(val url: String, val caption: String?)
    @Serializable private data class ProcessingDto(
        val steps: List<String>,
        val currentActiveIndex: Int,
        val isCompleted: Boolean
    )
    @Serializable private data class ErrorDto(
        val message: String,
        val canRetryPrompt: Boolean,
        val actionKey: String?
    )
}
