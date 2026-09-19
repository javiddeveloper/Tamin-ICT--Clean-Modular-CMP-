package com.tamin.taminhamrah.feature.agent.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownInlineParserTest {

    @Test
    fun `bold, italic, strike and code become styled runs`() {
        assertEquals(
            listOf(
                InlineRun("a "),
                InlineRun("b", bold = true),
                InlineRun(" "),
                InlineRun("c", italic = true),
                InlineRun(" "),
                InlineRun("d", strike = true),
                InlineRun(" "),
                InlineRun("e", code = true),
            ),
            MarkdownInlineParser.parse("a **b** *c* ~~d~~ `e`"),
        )
    }

    @Test
    fun `markers nest`() {
        assertEquals(
            listOf(InlineRun("a ", bold = true), InlineRun("b", bold = true, strike = true)),
            MarkdownInlineParser.parse("**a ~~b~~**"),
        )
        assertEquals(listOf(InlineRun("x", bold = true, italic = true)), MarkdownInlineParser.parse("***x***"))
    }

    @Test
    fun `unmatched markers and in-word underscores stay literal`() {
        assertEquals(listOf(InlineRun("۲ * ۳ = ۶")), MarkdownInlineParser.parse("۲ * ۳ = ۶"))
        assertEquals(listOf(InlineRun("file_name_here")), MarkdownInlineParser.parse("file_name_here"))
        assertEquals(listOf(InlineRun("**open")), MarkdownInlineParser.parse("**open"))
    }

    @Test
    fun `grouped numbers are isolated left to right`() {
        assertEquals(
            "مبلغ ⁦۱۳۶ ۲۴۹ ۴۷۹⁩ ریال و 12 روز",
            MarkdownInlineParser.isolateGroupedNumbers("مبلغ ۱۳۶ ۲۴۹ ۴۷۹ ریال و 12 روز"),
        )
    }

    @Test
    fun `a link in a cell becomes a run carrying its url, with query and emphasis kept`() {
        assertEquals(
            listOf(
                InlineRun("نسخه "),
                InlineRun("مشاهده ", link = "@prescription_detail?ARG_NOTE_HEAD=12&ARG_REQUEST_TYPE=(1)"),
                InlineRun("جزئیات", bold = true, link = "@prescription_detail?ARG_NOTE_HEAD=12&ARG_REQUEST_TYPE=(1)"),
            ),
            MarkdownInlineParser.parse("نسخه [مشاهده **جزئیات**](@prescription_detail?ARG_NOTE_HEAD=12&ARG_REQUEST_TYPE=(1))"),
        )
    }

    @Test
    fun `brackets that are not a link stay text`() {
        assertEquals(listOf(InlineRun("[a] (b) [c](d e)")), MarkdownInlineParser.parse("[a] (b) [c](d e)"))
        assertEquals(listOf(InlineRun("[]()")), MarkdownInlineParser.parse("[]()"))
    }
}
