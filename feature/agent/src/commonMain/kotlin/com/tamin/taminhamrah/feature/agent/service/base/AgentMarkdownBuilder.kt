package com.tamin.taminhamrah.feature.agent.service.base

/**
 * Builds a markdown answer in the same shape the server renders, so a client-side service and a
 * server-rendered reply look alike in the chat.
 *
 * Only structure is added — a heading, label/value fields, tables, links. Values coming from
 * services are escaped so a stray `*` or `|` in a name cannot turn into formatting.
 */
class AgentMarkdownBuilder {

    private val blocks = mutableListOf<String>()

    val isEmpty: Boolean get() = blocks.isEmpty()

    /** The server's title for this answer. Blank titles are skipped. */
    fun heading(text: String?) {
        text?.trim()?.takeIf { it.isNotEmpty() }?.let { blocks += "### ${it.escapeInline()}" }
    }

    /** A section title inside the answer, e.g. «پرداخت‌ها» above its rows. */
    fun subheading(text: String?) {
        text?.trim()?.takeIf { it.isNotEmpty() }?.let { blocks += "#### ${it.escapeInline()}" }
    }

    fun paragraph(text: String?) {
        text?.trim()?.takeIf { it.isNotEmpty() }?.let { blocks += it.escapeInline() }
    }

    /** One `- **label:** value` line per pair. */
    fun fields(rows: List<Pair<String, String?>>) {
        if (rows.isEmpty()) return
        blocks += rows.joinToString("\n") { (label, value) ->
            "- **${label.escapeInline()}:** ${value.orDash().escapeInline()}"
        }
    }

    fun table(columns: List<String>, rows: List<List<String?>>) {
        if (columns.isEmpty() || rows.isEmpty()) return
        val header = columns.joinToString(" | ", prefix = "| ", postfix = " |") { it.escapeCell() }
        val divider = columns.joinToString("|", prefix = "|", postfix = "|") { "---" }
        val body = rows.joinToString("\n") { row ->
            List(columns.size) { index -> row.getOrNull(index).orDash().escapeCell() }
                .joinToString(" | ", prefix = "| ", postfix = " |")
        }
        blocks += "$header\n$divider\n$body"
    }

    /** Buttons: each pair is a label and a link the deep link gate understands. */
    fun links(links: List<Pair<String, String>>) {
        if (links.isEmpty()) return
        blocks += links.joinToString("\n") { (label, url) -> "- [${label.escapeLinkLabel()}](${url.trim()})" }
    }

    fun rule() {
        if (blocks.isNotEmpty() && blocks.last() != RULE) blocks += RULE
    }

    fun build(): String = blocks.dropLastWhile { it == RULE }.joinToString("\n\n")

    private companion object {
        const val RULE = "---"
        const val DASH = "-"
        val INLINE_SPECIAL = setOf('\\', '*', '_', '~', '`', '[', ']')

        fun String?.orDash(): String = this?.trim()?.takeIf { it.isNotEmpty() } ?: DASH

        fun String.escapeInline(): String = buildString {
            val source = this@escapeInline
            source.forEachIndexed { index, c ->
                // An underscore inside a word (`file_name`) is never emphasis, so it stays as is.
                val inWord = c == '_' &&
                    source.getOrNull(index - 1)?.isLetterOrDigit() == true &&
                    source.getOrNull(index + 1)?.isLetterOrDigit() == true
                if (c in INLINE_SPECIAL && !inWord) append('\\')
                append(c)
            }
        }.replace("\n", " ")

        fun String.escapeCell(): String = replace("|", "\\|").replace("\n", " ")

        fun String.escapeLinkLabel(): String = replace("]", ")").replace("[", "(").replace("\n", " ")
    }
}

/** Builds an agent markdown answer. */
inline fun agentMarkdown(build: AgentMarkdownBuilder.() -> Unit): String = AgentMarkdownBuilder().apply(build).build()

/** A `@key` link for a destination in the app, gated by its feature flag when tapped. */
fun appLink(key: String): String = "@$key"

/** A link that sends [text] as the user's next prompt. */
fun promptLink(text: String): String = "agent://prompt?text=" + text.encodeQueryValue()

private fun String.encodeQueryValue(): String = buildString {
    this@encodeQueryValue.encodeToByteArray().forEach { byte ->
        val c = byte.toInt().toChar()
        if (byte >= 0 && (c.isLetterOrDigit() || c in "-_.~")) {
            append(c)
        } else {
            append('%')
            append(((byte.toInt() and 0xFF) shr 4).toString(16).uppercase())
            append((byte.toInt() and 0x0F).toString(16).uppercase())
        }
    }
}
