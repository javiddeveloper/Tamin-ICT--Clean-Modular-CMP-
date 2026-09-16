package com.tamin.taminhamrah.feature.agent.markdown

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MarkdownParserTest {

    @Test
    fun `headings, paragraphs, rule, quote and code are separate blocks`() {
        val blocks = MarkdownParser.parse(
            "### عنوان\n\nخط اول\nخط دوم\n\n---\n\n> نقل قول\n> ادامه\n\n```\nکد\n```"
        )
        assertEquals(
            listOf(
                MarkdownBlock.Heading(3, "عنوان"),
                MarkdownBlock.Paragraph("خط اول\nخط دوم"),
                MarkdownBlock.Rule,
                MarkdownBlock.Quote("نقل قول\nادامه"),
                MarkdownBlock.CodeBlock("کد"),
            ),
            blocks,
        )
    }

    @Test
    fun `list items keep order, numbering and nesting`() {
        val blocks = MarkdownParser.parse("- الف\n  - ب\n- ج\n\n3. سه\n4. چهار")
        assertEquals(
            listOf(
                MarkdownBlock.ListItem(ordered = false, number = 0, level = 0, text = "الف"),
                MarkdownBlock.ListItem(ordered = false, number = 0, level = 1, text = "ب"),
                MarkdownBlock.ListItem(ordered = false, number = 0, level = 0, text = "ج"),
                MarkdownBlock.ListItem(ordered = true, number = 3, level = 0, text = "سه"),
                MarkdownBlock.ListItem(ordered = true, number = 4, level = 0, text = "چهار"),
            ),
            blocks,
        )
    }

    @Test
    fun `table rows are padded or trimmed to the header and escaped pipes stay in cells`() {
        val table = assertIs<MarkdownBlock.Table>(
            MarkdownParser.parse("| a | b | c |\n|---|:-:|---|\n| 1 | 2 |\n| x \\| y | 5 | 6 | 7 |").single()
        )
        assertEquals(listOf("a", "b", "c"), table.header)
        assertEquals(listOf(listOf("1", "2", ""), listOf("x | y", "5", "6")), table.rows)
    }

    @Test
    fun `a list of only links becomes one button group`() {
        val blocks = MarkdownParser.parse("- [هدیه](@wedding_present)\n- [فیش](mytamin://feature/pensioner_pay_roll)")
        assertEquals(
            listOf(
                MarkdownBlock.Actions(
                    listOf(
                        MarkdownLink("هدیه", "@wedding_present"),
                        MarkdownLink("فیش", "mytamin://feature/pensioner_pay_roll"),
                    )
                )
            ),
            blocks,
        )
    }

    @Test
    fun `links in prose leave their label behind and become buttons after the text`() {
        val blocks = MarkdownParser.parse("برای دیدن [اینجا](@laws) را بزنید.")
        assertEquals(
            listOf(
                MarkdownBlock.Paragraph("برای دیدن اینجا را بزنید."),
                MarkdownBlock.Actions(listOf(MarkdownLink("اینجا", "@laws"))),
            ),
            blocks,
        )
    }

    @Test
    fun `explicit math block keeps the whole line as the expression`() {
        val formula = assertIs<MarkdownBlock.Formula>(
            MarkdownParser.parse("\$\$\n2/π = (1 − 1/2²)(1 − 1/4²)…\n\$\$").single()
        )
        assertNull(formula.label)
        assertEquals("2/π = (1 − 1/2²)(1 − 1/4²)…", formula.raw)
        val row = assertIs<MathExpr.Row>(formula.expression)
        assertEquals(2, row.children.count { it is MathExpr.Group })
    }

    @Test
    fun `single line math block and math fence are formulas`() {
        assertIs<MarkdownBlock.Formula>(MarkdownParser.parse("\$\$x^2 + 1\$\$").single())
        val fence = MarkdownParser.parse("```math\na/b\nc^2\n```")
        assertEquals(2, fence.size)
        assertTrue(fence.all { it is MarkdownBlock.Formula })
    }

    @Test
    fun `a label equals expression line inside prose is lifted out as a formula`() {
        val blocks = MarkdownParser.parse("متن معمولی\nقسط ماهیانه = (۲۲۸ ۸۹۹ ۱۲۵) / (۱۲)\nادامه‌ی متن")
        assertEquals(3, blocks.size)
        assertEquals(MarkdownBlock.Paragraph("متن معمولی"), blocks[0])
        val formula = assertIs<MarkdownBlock.Formula>(blocks[1])
        assertEquals("قسط ماهیانه", formula.label)
        assertEquals(MarkdownBlock.Paragraph("ادامه‌ی متن"), blocks[2])
    }

    @Test
    fun `sentences with an equals sign are not formulas`() {
        assertNull(MarkdownParser.formulaLine("جمله‌ی عادی با علامت = که فرمول نیست."))
        assertNull(MarkdownParser.formulaLine("نتیجه = بدون عملگر"))
        assertNull(MarkdownParser.formulaLine("= ۲ × ۳"))
        assertIs<MarkdownBlock.Formula>(MarkdownParser.formulaLine("**نرخ** = ۰.۰۷ × ۲۴"))
    }

    @Test
    fun `html is never rendered - br breaks lines and other tags are removed`() {
        val blocks = MarkdownParser.parse("اول<br/>دوم <script>alert(1)</script><b>پررنگ</b>")
        assertEquals(listOf(MarkdownBlock.Paragraph("اول\nدوم alert(1)پررنگ")), blocks)
    }

    @Test
    fun `empty input yields no blocks`() {
        assertEquals(emptyList(), MarkdownParser.parse("  \n\n "))
    }
}
