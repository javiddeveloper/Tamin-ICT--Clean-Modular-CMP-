package com.tamin.taminhamrah.feature.agent.markdown

/**
 * Splits an assistant markdown answer into [MarkdownBlock]s.
 *
 * Supported: ATX headings, paragraphs, bullet and ordered lists (nesting by indentation), block
 * quotes, fenced code, thematic breaks, pipe tables, formulas and links.
 *
 * - **Formulas** come from an explicit `$$ … $$` block or a ```` ```math ```` fence (one formula
 *   per line), or from a prose line shaped like `label = expression` that contains a math operator
 *   and no sentence punctuation.
 * - **Links** never stay inline: each `[label](url)` becomes a button in an [MarkdownBlock.Actions]
 *   block after the text it came from, and its label stays in the sentence as plain text. A list
 *   made only of links becomes one button group.
 * - **Safety**: the text is model-generated, so HTML is never rendered — `<br>` becomes a line
 *   break and every other tag is removed. Anything unrecognised is shown as plain text.
 */
object MarkdownParser {

    private val HEADING = Regex("""^(#{1,6})\s+(.*)$""")
    private val LIST_ITEM = Regex("""^(\s*)([-*+•]|(\d+)[.)])\s+(.*)$""")
    private val RULE = Regex("""^\s*([-*_])(\s*\1){2,}\s*$""")
    private val TABLE_DELIMITER = Regex("""^\s*\|?\s*:?-+:?\s*(\|\s*:?-+:?\s*)*\|?\s*$""")
    private val LINK = Regex("""\[([^\]]+)]\(([^)\s]+)\)""")
    private val LONE_LINK = Regex("""^\[([^\]]+)]\(([^)\s]+)\)$""")
    private val BR_TAG = Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE)
    private val HTML_TAG = Regex("""</?[a-zA-Z][^>]*>""")
    private val UNESCAPED_PIPE = Regex("""(?<!\\)\|""")
    private val SENTENCE_PUNCTUATION = setOf('.', '!', '؟', '?', '،', ':')

    private const val FENCE = "```"
    private const val MATH_BLOCK = "\$\$"
    private const val MATH_FENCE_INFO = "math"

    /** The link when [text] is nothing but one `[label](url)`, e.g. a table cell; otherwise null. */
    fun loneLink(text: String): MarkdownLink? =
        LONE_LINK.matchEntire(text.trim())?.let { MarkdownLink(it.groupValues[1], it.groupValues[2]) }

    /** Whether [text] holds a `[label](url)` link, i.e. needs this parser to be tappable. */
    fun containsLink(text: String): Boolean = LINK.containsMatchIn(text)

    fun parse(markdown: String): List<MarkdownBlock> {
        val lines = sanitize(markdown).split('\n')
        val blocks = mutableListOf<MarkdownBlock>()
        var i = 0

        while (i < lines.size) {
            val line = lines[i]
            val trimmed = line.trim()

            when {
                trimmed.isEmpty() -> i++

                trimmed.startsWith(FENCE) -> {
                    val info = trimmed.removePrefix(FENCE).trim()
                    val body = mutableListOf<String>()
                    i++
                    while (i < lines.size && !lines[i].trim().startsWith(FENCE)) body.add(lines[i++])
                    i++ // closing fence, if any
                    if (info.equals(MATH_FENCE_INFO, ignoreCase = true)) {
                        body.mathBlocks().forEach(blocks::add)
                    } else {
                        blocks.add(MarkdownBlock.CodeBlock(body.joinToString("\n").trimEnd()))
                    }
                }

                trimmed.startsWith(MATH_BLOCK) -> {
                    val inline = trimmed.removePrefix(MATH_BLOCK)
                    if (inline.endsWith(MATH_BLOCK) && inline.length > MATH_BLOCK.length) {
                        listOf(inline.removeSuffix(MATH_BLOCK)).mathBlocks().forEach(blocks::add)
                        i++
                    } else {
                        val body = mutableListOf(inline)
                        i++
                        while (i < lines.size && !lines[i].trim().endsWith(MATH_BLOCK)) body.add(lines[i++])
                        if (i < lines.size) body.add(lines[i].trim().removeSuffix(MATH_BLOCK))
                        i++
                        body.mathBlocks().forEach(blocks::add)
                    }
                }

                isTableStart(lines, i) -> {
                    val header = parseCells(line)
                    val rows = mutableListOf<List<String>>()
                    i += 2
                    while (i < lines.size && isTableRow(lines[i])) {
                        rows.add(parseCells(lines[i]).padTo(header.size))
                        i++
                    }
                    blocks.add(MarkdownBlock.Table(header, rows))
                }

                RULE.matches(line) -> {
                    blocks.add(MarkdownBlock.Rule)
                    i++
                }

                HEADING.matches(trimmed) -> {
                    val match = HEADING.matchEntire(trimmed)!!
                    val level = match.groupValues[1].length
                    blocks.addWithLinks(match.groupValues[2].trim().trimEnd('#').trim()) { MarkdownBlock.Heading(level, it) }
                    i++
                }

                trimmed.startsWith(">") -> {
                    val quote = mutableListOf<String>()
                    while (i < lines.size && lines[i].trim().startsWith(">")) {
                        quote.add(lines[i].trim().removePrefix(">").trim())
                        i++
                    }
                    blocks.addWithLinks(quote.joinToString("\n")) { MarkdownBlock.Quote(it) }
                }

                LIST_ITEM.matches(line) -> {
                    val items = mutableListOf<MatchResult>()
                    while (i < lines.size && LIST_ITEM.matches(lines[i])) {
                        items.add(LIST_ITEM.matchEntire(lines[i])!!)
                        i++
                    }
                    addList(blocks, items)
                }

                else -> {
                    val paragraph = mutableListOf<String>()
                    while (i < lines.size && lines[i].isNotBlank() && !startsBlock(lines, i)) {
                        paragraph.add(lines[i].trim())
                        i++
                    }
                    addParagraph(blocks, paragraph)
                }
            }
        }
        return blocks
    }

    // ── Paragraphs, lists, links ───────────────────────────────────────────

    private fun addParagraph(blocks: MutableList<MarkdownBlock>, lines: List<String>) {
        // A formula line is its own block even inside a paragraph run.
        val prose = mutableListOf<String>()
        fun flushProse() {
            if (prose.isEmpty()) return
            blocks.addWithLinks(prose.joinToString("\n")) { MarkdownBlock.Paragraph(it) }
            prose.clear()
        }
        lines.forEach { line ->
            val formula = formulaLine(line)
            if (formula != null) {
                flushProse()
                blocks.add(formula)
            } else {
                prose.add(line)
            }
        }
        flushProse()
    }

    private fun addList(blocks: MutableList<MarkdownBlock>, items: List<MatchResult>) {
        val contents = items.map { it.groupValues[4].trim() }
        val loneLinks = contents.map { LONE_LINK.matchEntire(it) }
        if (loneLinks.all { it != null }) {
            blocks.add(MarkdownBlock.Actions(loneLinks.map { MarkdownLink(it!!.groupValues[1], it.groupValues[2]) }))
            return
        }
        items.forEach { item ->
            val indent = item.groupValues[1].replace("\t", "    ").length
            val ordinal = item.groupValues[3].toIntOrNull()
            val text = item.groupValues[4].trim()
            val lone = LONE_LINK.matchEntire(text)
            if (lone != null) {
                blocks.add(MarkdownBlock.Actions(listOf(MarkdownLink(lone.groupValues[1], lone.groupValues[2]))))
            } else {
                blocks.addWithLinks(text) {
                    MarkdownBlock.ListItem(
                        ordered = ordinal != null,
                        number = ordinal ?: 0,
                        level = indent / 2,
                        text = it,
                    )
                }
            }
        }
    }

    /** Adds the block built from [text] with its links replaced by their labels, then its buttons. */
    private inline fun MutableList<MarkdownBlock>.addWithLinks(text: String, build: (String) -> MarkdownBlock) {
        val links = LINK.findAll(text).map { MarkdownLink(it.groupValues[1], it.groupValues[2]) }.toList()
        if (links.isEmpty()) {
            add(build(text))
            return
        }
        val withoutLinks = LINK.replace(text) { it.groupValues[1] }
        if (withoutLinks.isNotBlank()) add(build(withoutLinks))
        add(MarkdownBlock.Actions(links))
    }

    // ── Formulas ───────────────────────────────────────────────────────────

    private fun List<String>.mathBlocks(): List<MarkdownBlock> =
        map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { MarkdownBlock.Formula(label = null, expression = MathParser.parse(it), raw = it) }

    /** A prose line shaped like `label = expression`, or null. */
    internal fun formulaLine(rawLine: String): MarkdownBlock.Formula? {
        val line = rawLine.replace("**", "").replace("__", "").trim()
        if (line.isEmpty() || line.first() in setOf('#', '-', '>', '|', '*')) return null
        if (LINK.containsMatchIn(line)) return null

        val eq = line.indexOf('=')
        if (eq <= 0 || eq >= line.lastIndex) return null
        val label = line.substring(0, eq).trim()
        val expression = line.substring(eq + 1).trim()
        if (label.isEmpty() || expression.isEmpty()) return null
        // Sentences end with punctuation; formulas do not. A decimal point between digits is fine.
        if (line.withIndex().any { (index, c) -> c in SENTENCE_PUNCTUATION && !isDecimalPoint(line, index) }) return null
        if (expression.none { MathParser.isOperatorChar(it) }) return null

        return MarkdownBlock.Formula(label = label, expression = MathParser.parse(expression), raw = line)
    }

    private fun isDecimalPoint(text: String, index: Int): Boolean =
        text[index] == '.' && text.getOrNull(index - 1)?.isDigit() == true && text.getOrNull(index + 1)?.isDigit() == true

    // ── Tables ─────────────────────────────────────────────────────────────

    private fun isTableRow(line: String): Boolean = line.trim().startsWith("|")

    private fun isTableStart(lines: List<String>, index: Int): Boolean {
        val next = lines.getOrNull(index + 1) ?: return false
        return isTableRow(lines[index]) && next.contains('-') && TABLE_DELIMITER.matches(next)
    }

    private fun parseCells(line: String): List<String> {
        var content = line.trim().removePrefix("|")
        if (content.endsWith("|") && !content.endsWith("\\|")) content = content.dropLast(1)
        return UNESCAPED_PIPE.split(content).map { it.trim().replace("\\|", "|") }
    }

    private fun List<String>.padTo(size: Int): List<String> = when {
        this.size == size -> this
        this.size > size -> take(size)
        else -> this + List(size - this.size) { "" }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private fun startsBlock(lines: List<String>, index: Int): Boolean {
        val line = lines[index]
        val trimmed = line.trim()
        return trimmed.startsWith(FENCE) || trimmed.startsWith(MATH_BLOCK) || trimmed.startsWith(">") ||
            HEADING.matches(trimmed) || RULE.matches(line) || LIST_ITEM.matches(line) || isTableStart(lines, index)
    }

    private fun sanitize(markdown: String): String =
        markdown
            .replace("\r\n", "\n")
            .replace(BR_TAG, "\n")
            .replace(HTML_TAG, "")
}
