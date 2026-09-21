package com.tamin.taminhamrah.feature.agent.markdown

/**
 * One block of an assistant markdown answer. Blocks are drawn and revealed one at a time.
 *
 * Text-bearing blocks keep their inline markdown (`**bold**`, `` `code` ``…) as a string; it is
 * turned into styled runs by [MarkdownInlineParser] when drawn.
 */
sealed interface MarkdownBlock {

    data class Heading(val level: Int, val text: String) : MarkdownBlock

    data class Paragraph(val text: String) : MarkdownBlock

    /** [number] is the written ordinal for an ordered item; [level] is the nesting depth from 0. */
    data class ListItem(
        val ordered: Boolean,
        val number: Int,
        val level: Int,
        val text: String,
    ) : MarkdownBlock

    data class Quote(val text: String) : MarkdownBlock

    data class CodeBlock(val code: String) : MarkdownBlock

    data object Rule : MarkdownBlock

    /** A pipe table. Every row is padded or trimmed to the header's column count. */
    data class Table(val header: List<String>, val rows: List<List<String>>) : MarkdownBlock

    /**
     * A formula. [label] is the prose name on the left of `=` for a `label = expression` line and
     * null for an explicit math block, where the whole line — `=` included — is the expression.
     * [raw] is the source line, used for copying.
     */
    data class Formula(val label: String?, val expression: MathExpr, val raw: String) : MarkdownBlock

    /** Links lifted out of the text; each becomes a button. */
    data class Actions(val links: List<MarkdownLink>) : MarkdownBlock
}

data class MarkdownLink(val label: String, val url: String)
