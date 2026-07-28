package com.tamin.taminhamrah.feature.agent.service.base

import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * What a chat bubble holds — the answer shape, with no UI detail attached.
 *
 * Organised into groups so a new answer type has an obvious home and shared behaviour
 * can be expressed against a whole family:
 *
 * | Group          | For                                              |
 * |----------------|--------------------------------------------------|
 * | [Textual]      | prose and tabular readouts                        |
 * | [Media]        | image / voice / video attachments                 |
 * | [DataView]     | charts and other rendered data                    |
 * | [Interactive]  | things the user can act on                        |
 * | [SystemNote]   | pipeline feedback and errors                      |
 *
 * The whole hierarchy is `@Serializable`, which is what lets `ChatBubbleCodec` persist
 * any bubble without per-type wiring. **Adding a new answer type means: add the class
 * here with a stable [SerialName], then add its renderer.** Nothing else.
 *
 * [SerialName] values are written to the database, so treat them as a stored contract:
 * rename a class freely, but never change its serial name.
 */
@Serializable
sealed interface ChatBubbleContent {

    // ── Textual ──────────────────────────────────────────────────────────────

    /** Prose answers. */
    @Serializable
    sealed interface Textual : ChatBubbleContent

    /** A plain paragraph. Supports the assistant's markdown subset. */
    @Serializable
    @SerialName("text")
    data class Text(val message: String) : Textual

    /**
     * A titled block: a short header line above the body text — the shape most service
     * answers take ("سابقه شما", then the explanation).
     */
    @Serializable
    @SerialName("rich_text")
    data class RichText(
        val header: String,
        val body: String,
        /** Optional smaller line under the body, e.g. a source or disclaimer. */
        val footnote: String? = null
    ) : Textual

    /** A labelled readout, e.g. wage history rows. */
    @Serializable
    @SerialName("key_value")
    data class KeyValue(
        val title: String?,
        val items: List<KeyValueRow>
    ) : Textual

    // ── Media ────────────────────────────────────────────────────────────────

    /** Attachments rendered with their own player/viewer chrome. */
    @Serializable
    sealed interface Media : ChatBubbleContent {
        /** Remote URL, or a local file path for something the user just recorded. */
        val source: String
        val caption: String?
    }

    @Serializable
    @SerialName("image")
    data class Image(
        override val source: String,
        override val caption: String? = null,
        val width: Int? = null,
        val height: Int? = null
    ) : Media

    @Serializable
    @SerialName("voice")
    data class Voice(
        override val source: String,
        val durationMs: Long? = null,
        /** Amplitudes captured while recording, used to draw the waveform. */
        val amplitudes: List<Int> = emptyList(),
        override val caption: String? = null
    ) : Media

    @Serializable
    @SerialName("video")
    data class Video(
        override val source: String,
        val thumbnailUrl: String? = null,
        val durationMs: Long? = null,
        override val caption: String? = null
    ) : Media

    // ── Data views ───────────────────────────────────────────────────────────

    /** Rendered data rather than prose. */
    @Serializable
    sealed interface DataView : ChatBubbleContent

    /**
     * A chart. Kept as plain data — labels plus one or more series — so it persists and
     * survives being reopened from history, instead of pointing at a live domain model.
     */
    @Serializable
    @SerialName("chart")
    data class Chart(
        val title: String? = null,
        val kind: ChartKind = ChartKind.BAR,
        /** X-axis labels, shared by every series. */
        val labels: List<String> = emptyList(),
        val series: List<ChartSeries> = emptyList(),
        /** Unit shown next to values, e.g. "ریال". */
        val valueUnit: String? = null
    ) : DataView

    /**
     * A tabular readout. Unlike [KeyValue] this has real columns, so the renderer can
     * lay it out responsively — sharing the width when the columns fit, and scrolling
     * horizontally when they do not.
     */
    @Serializable
    @SerialName("table")
    data class Table(
        val title: String? = null,
        val columns: List<String>,
        val rows: List<TableRow>
    ) : DataView

    // ── Interactive ──────────────────────────────────────────────────────────

    /** Bubbles the user can act on. */
    @Serializable
    sealed interface Interactive : ChatBubbleContent

    /** Follow-up prompts; usually folded into the reply they belong to. */
    @Serializable
    @SerialName("suggested_prompts")
    data class SuggestedPrompts(val prompts: List<String>) : Interactive

    /** Hands off to a screen in this app; [destination] is an `AgentDestination` id. */
    @Serializable
    @SerialName("deep_link")
    data class DeepLink(val title: String, val destination: String) : Interactive

    @Serializable
    @SerialName("web_link")
    data class WebLink(val title: String, val url: String) : Interactive

    /**
     * A server-described form (SDUI). The schema stays as raw JSON so the form renderer
     * can evolve without changing this type.
     */
    @Serializable
    @SerialName("dynamic_form")
    data class DynamicForm(
        val formKey: String,
        val schema: JsonElement
    ) : Interactive

    // ── System notes ─────────────────────────────────────────────────────────

    /** Pipeline feedback rather than an answer. */
    @Serializable
    sealed interface SystemNote : ChatBubbleContent

    /** The assistant's step tracker, shown inline while it works. */
    @Serializable
    @SerialName("processing_steps")
    data class ProcessingSteps(
        val steps: List<String>,
        val currentActiveIndex: Int,
        val isCompleted: Boolean
    ) : SystemNote

    @Serializable
    @SerialName("service_error")
    data class ServiceError(
        val message: String,
        val canRetryPrompt: Boolean = false,
        val actionKey: AgentActionKey? = null,
        val payload: JsonElement? = null
    ) : SystemNote
}

/** One row of a [ChatBubbleContent.KeyValue] readout. */
@Serializable
data class KeyValueRow(val key: String, val value: String)

/** One row of a [ChatBubbleContent.Table]; cells line up with the table's columns. */
@Serializable
data class TableRow(val cells: List<String>)

/** One plotted series of a [ChatBubbleContent.Chart]. */
@Serializable
data class ChartSeries(
    val name: String? = null,
    val values: List<Double>
)

/** How a [ChatBubbleContent.Chart] should be drawn. */
@Serializable
enum class ChartKind {
    @SerialName("bar") BAR,
    @SerialName("line") LINE,
    @SerialName("pie") PIE
}

/** Rows as plain pairs, for call sites that read them that way. */
val ChatBubbleContent.KeyValue.pairs: List<Pair<String, String>>
    get() = items.map { it.key to it.value }

/** Builds readout rows from the label→value pairs services naturally accumulate. */
fun List<Pair<String, String>>.toKeyValueRows(): List<KeyValueRow> =
    map { KeyValueRow(it.first, it.second) }
