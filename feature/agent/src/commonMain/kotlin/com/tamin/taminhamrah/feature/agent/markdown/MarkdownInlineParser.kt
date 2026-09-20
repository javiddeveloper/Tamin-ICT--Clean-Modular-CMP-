package com.tamin.taminhamrah.feature.agent.markdown

/** A stretch of text with one combination of inline styles; [link] is set for a link's label. */
data class InlineRun(
    val text: String,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val strike: Boolean = false,
    val code: Boolean = false,
    val link: String? = null,
)

/**
 * Turns inline markdown into [InlineRun]s: `***both***`, `**bold**` / `__bold__`, `*italic*` /
 * `_italic_`, `~~strike~~`, `` `code` `` and `[label](url)`. Markers can nest (`**a ~~b~~**`). An unmatched marker
 * is kept as literal text, `_` inside a word (`file_name`) is never a marker, and a backslash makes
 * the character after it literal (`\*`).
 *
 * Links reach this parser only where they stay in the text — table cells. Elsewhere
 * [MarkdownParser] has already turned them into buttons. A link's url has no spaces and may hold
 * balanced parentheses; the label keeps its own emphasis.
 *
 * Number runs grouped by spaces (`136 249 479`) are wrapped in a left-to-right isolate so the
 * surrounding right-to-left text cannot reverse the order of their groups.
 */
object MarkdownInlineParser {

    private val ESCAPABLE = setOf('\\', '*', '_', '~', '`', '[', ']', '|', '#')
    private const val LTR_ISOLATE_START = '⁦'
    private const val LTR_ISOLATE_END = '⁩'
    private val GROUPED_NUMBER = Regex("""[0-9۰-۹٠-٩]{1,3}(?:[  ][0-9۰-۹٠-٩]{3})+""")

    fun parse(text: String): List<InlineRun> {
        val runs = mutableListOf<InlineRun>()
        parseInto(text, InlineRun(""), runs)
        return runs.mergeAdjacent()
    }

    /** Wraps every space-grouped number in [text] in an LTR isolate. */
    fun isolateGroupedNumbers(text: String): String =
        GROUPED_NUMBER.replace(text) { "$LTR_ISOLATE_START${it.value}$LTR_ISOLATE_END" }

    private fun parseInto(text: String, style: InlineRun, out: MutableList<InlineRun>) {
        val plain = StringBuilder()
        fun flush() {
            if (plain.isNotEmpty()) {
                out.add(style.copy(text = plain.toString()))
                plain.clear()
            }
        }

        var i = 0
        while (i < text.length) {
            val c = text[i]

            // A backslash makes the next marker character literal: `\*` is a star, not emphasis.
            if (c == '\\' && text.getOrNull(i + 1)?.let { it in ESCAPABLE } == true) {
                plain.append(text[i + 1])
                i += 2
                continue
            }

            if (c == '`') {
                val end = text.indexOf('`', i + 1)
                if (end > i + 1) {
                    flush()
                    out.add(style.copy(text = text.substring(i + 1, end), code = true))
                    i = end + 1
                    continue
                }
            }

            if (c == '[') {
                val link = linkAt(text, i)
                if (link != null) {
                    flush()
                    parseInto(link.label, style.copy(link = link.url), out)
                    i = link.end
                    continue
                }
            }

            val marker = markerAt(text, i)
            if (marker != null) {
                val close = findClose(text, i + marker.length, marker)
                if (close != null) {
                    flush()
                    val inner = text.substring(i + marker.length, close)
                    parseInto(inner, style.apply(marker), out)
                    i = close + marker.length
                    continue
                }
            }

            plain.append(c)
            i++
        }
        flush()
    }

    private class LinkMatch(val label: String, val url: String, val end: Int)

    /** `[label](url)` starting at [start], or null when the brackets do not form a link. */
    private fun linkAt(text: String, start: Int): LinkMatch? {
        val labelEnd = text.indexOf("](", start + 1).takeIf { it > start + 1 } ?: return null
        if (text.substring(start + 1, labelEnd).contains('[')) return null
        var depth = 1
        var i = labelEnd + 2
        while (i < text.length) {
            when (text[i]) {
                '(' -> depth++
                ')' -> if (--depth == 0) break
            }
            if (text[i].isWhitespace()) return null
            i++
        }
        if (depth != 0 || i == labelEnd + 2) return null
        return LinkMatch(label = text.substring(start + 1, labelEnd), url = text.substring(labelEnd + 2, i), end = i + 1)
    }

    private fun markerAt(text: String, index: Int): String? {
        val candidates = listOf("***", "___", "**", "__", "~~", "*", "_")
        val marker = candidates.firstOrNull { text.startsWith(it, index) } ?: return null
        if (marker[0] == '_' && text.getOrNull(index - 1)?.isLetterOrDigit() == true) return null
        // An opening marker must be followed by content, not a space.
        val next = text.getOrNull(index + marker.length) ?: return null
        if (next.isWhitespace()) return null
        return marker
    }

    private fun findClose(text: String, from: Int, marker: String): Int? {
        var index = text.indexOf(marker, from)
        while (index >= 0) {
            val before = text.getOrNull(index - 1)
            val after = text.getOrNull(index + marker.length)
            val closesWord = before != null && !before.isWhitespace()
            // "**a**b" is fine, but for "_" the closing marker must not sit inside a word.
            val insideWord = marker[0] == '_' && after?.isLetterOrDigit() == true
            // Do not let "*" close on the first half of a "**".
            val partOfLonger = marker.length == 1 && after == marker[0]
            if (index > from && closesWord && !insideWord && !partOfLonger) return index
            index = text.indexOf(marker, index + 1)
        }
        return null
    }

    private fun InlineRun.apply(marker: String): InlineRun = when (marker) {
        "***", "___" -> copy(bold = true, italic = true)
        "**", "__" -> copy(bold = true)
        "~~" -> copy(strike = true)
        else -> copy(italic = true)
    }

    private fun List<InlineRun>.mergeAdjacent(): List<InlineRun> {
        val merged = mutableListOf<InlineRun>()
        forEach { run ->
            val last = merged.lastOrNull()
            if (last != null && last.copy(text = "") == run.copy(text = "")) {
                merged[merged.lastIndex] = last.copy(text = last.text + run.text)
            } else if (run.text.isNotEmpty()) {
                merged.add(run)
            }
        }
        return merged
    }
}
