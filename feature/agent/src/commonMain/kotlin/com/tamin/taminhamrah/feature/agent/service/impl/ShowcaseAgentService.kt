package com.tamin.taminhamrah.feature.agent.service.impl

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.ChartKind
import com.tamin.taminhamrah.feature.agent.service.base.ChartSeries
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.KeyValueRow
import com.tamin.taminhamrah.feature.agent.service.base.TableRow
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull

/**
 * Builds a bubble from whatever the response describes, one type per entity.
 *
 * Everything it renders — titles, body text, image URLs, table rows, chart values —
 * comes from the entity payload, exactly as it would from a real backend. Nothing is
 * hardcoded here, so changing the demo means editing the fixture, not this class.
 *
 * The payload shape is one object per bubble, keyed by `type`:
 * ```json
 * { "type": "rich_text", "title": "…", "text": "…", "footnote": "…" }
 * { "type": "image",     "image": "https://…", "caption": "…" }
 * { "type": "table",     "title": "…", "columns": ["…"], "rows": [["…","…"]] }
 * { "type": "chart",     "title": "…", "kind": "bar", "labels": [], "values": [] }
 * ```
 */
class ShowcaseAgentService : AgentServiceUseCase {

    override val supportedKeys: List<AgentActionKey> = listOf(AgentActionKey.SHOWCASE)

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        val payload = params.payload as? JsonObject
            ?: return AgentServiceResult.Success(
                listOf(ChatBubbleContent.Text(params.message.orEmpty()))
            )

        val bubble = when (payload.string("type")) {
            "rich_text" -> ChatBubbleContent.RichText(
                header = payload.string("title").orEmpty(),
                body = payload.string("text").orEmpty(),
                footnote = payload.string("footnote")
            )

            "text" -> ChatBubbleContent.Text(
                payload.string("text") ?: params.message.orEmpty()
            )

            "key_value" -> ChatBubbleContent.KeyValue(
                title = payload.string("title"),
                items = payload.objects("items").map {
                    KeyValueRow(
                        key = it.string("key").orEmpty(),
                        value = it.string("value").orEmpty()
                    )
                }
            )

            "table" -> ChatBubbleContent.Table(
                title = payload.string("title"),
                columns = payload.strings("columns"),
                rows = payload.arrays("rows").map { row ->
                    TableRow(cells = row.map { (it as? JsonPrimitive)?.content.orEmpty() })
                }
            )

            "chart" -> ChatBubbleContent.Chart(
                title = payload.string("title"),
                kind = when (payload.string("kind")) {
                    "line" -> ChartKind.LINE
                    "pie" -> ChartKind.PIE
                    else -> ChartKind.BAR
                },
                labels = payload.strings("labels"),
                series = listOf(
                    ChartSeries(
                        name = payload.string("series"),
                        values = payload.doubles("values")
                    )
                ),
                valueUnit = payload.string("unit")
            )

            "image" -> ChatBubbleContent.Image(
                source = payload.string("image").orEmpty(),
                caption = payload.string("caption")
            )

            "video" -> ChatBubbleContent.Video(
                source = payload.string("video").orEmpty(),
                thumbnailUrl = payload.string("thumbnail"),
                durationMs = payload.string("duration")?.toLongOrNull(),
                caption = payload.string("caption")
            )

            "voice" -> ChatBubbleContent.Voice(
                source = payload.string("audio").orEmpty(),
                durationMs = payload.string("duration")?.toLongOrNull(),
                amplitudes = payload.doubles("waveform").map { it.toInt() },
                caption = payload.string("caption")
            )

            "deep_link" -> ChatBubbleContent.DeepLink(
                title = payload.string("title").orEmpty(),
                destination = payload.string("destination").orEmpty()
            )

            "web_link" -> ChatBubbleContent.WebLink(
                title = payload.string("title").orEmpty(),
                url = payload.string("url").orEmpty()
            )

            "processing" -> ChatBubbleContent.ProcessingSteps(
                steps = payload.strings("steps"),
                currentActiveIndex = payload.string("active")?.toIntOrNull() ?: 0,
                isCompleted = payload.string("completed")?.toBooleanStrictOrNull() ?: false
            )

            // `retryable: false` is the shape a disabled service ends in — a note, not a retry.
            "error" -> {
                val retryable = payload.string("retryable")?.toBooleanStrictOrNull() ?: true
                ChatBubbleContent.ServiceError(
                    message = payload.string("text").orEmpty(),
                    canRetryPrompt = retryable,
                    actionKey = AgentActionKey.SHOWCASE.takeIf { retryable }
                )
            }

            // A service that fails while running: the dispatcher turns the throw into an error
            // bubble whose retry re-runs this service with the same payload.
            "throw" -> throw IllegalStateException(payload.string("text") ?: "showcase service failure")

            "suggestions" -> ChatBubbleContent.SuggestedPrompts(payload.strings("prompts"))

            else -> ChatBubbleContent.Text(params.message.orEmpty())
        }

        return AgentServiceResult.Success(listOf(bubble))
    }
}

// ─── Payload readers ──────────────────────────────────────────────────────────
// Small helpers so the builder above reads as a straight mapping. Anything missing or
// of the wrong shape comes back empty rather than throwing, since fixture and backend
// payloads both evolve.

private fun JsonObject.string(key: String): String? =
    (this[key] as? JsonPrimitive)?.content?.takeIf { it.isNotBlank() }

private fun JsonObject.strings(key: String): List<String> =
    (this[key] as? JsonArray).orEmpty().mapNotNull { (it as? JsonPrimitive)?.content }

private fun JsonObject.doubles(key: String): List<Double> =
    (this[key] as? JsonArray).orEmpty().mapNotNull { (it as? JsonPrimitive)?.doubleOrNull }

private fun JsonObject.objects(key: String): List<JsonObject> =
    (this[key] as? JsonArray).orEmpty().mapNotNull { it as? JsonObject }

private fun JsonObject.arrays(key: String): List<JsonArray> =
    (this[key] as? JsonArray).orEmpty().mapNotNull { it as? JsonArray }

private fun JsonArray?.orEmpty(): List<kotlinx.serialization.json.JsonElement> =
    this ?: emptyList()
