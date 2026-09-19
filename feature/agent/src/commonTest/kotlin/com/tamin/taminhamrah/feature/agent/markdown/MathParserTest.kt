package com.tamin.taminhamrah.feature.agent.markdown

import com.tamin.taminhamrah.feature.agent.markdown.MathExpr.Atom
import com.tamin.taminhamrah.feature.agent.markdown.MathExpr.Fraction
import com.tamin.taminhamrah.feature.agent.markdown.MathExpr.Group
import com.tamin.taminhamrah.feature.agent.markdown.MathExpr.Operator
import com.tamin.taminhamrah.feature.agent.markdown.MathExpr.Power
import com.tamin.taminhamrah.feature.agent.markdown.MathExpr.Row
import com.tamin.taminhamrah.feature.agent.markdown.MathExpr.Subscript
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class MathParserTest {

    @Test
    fun `wallis product keeps every parenthesis and stacks each fraction`() {
        val factor = { n: String ->
            Group(Row(listOf(Atom("1"), Operator("−"), Fraction(Atom("1"), Power(Atom(n), Atom("2"))))))
        }
        assertEquals(
            Row(
                listOf(
                    Fraction(Atom("2"), Atom("π")),
                    Operator("="),
                    factor("2"),
                    factor("4"),
                    factor("6"),
                    Atom("…"),
                )
            ),
            MathParser.parse("2/π = (1 − 1/2²)(1 − 1/4²)(1 − 1/6²)…"),
        )
    }

    @Test
    fun `parentheses around a numerator and denominator are not dropped`() {
        assertEquals(
            Fraction(
                Group(Row(listOf(Atom("۱۳۶ ۲۴۹ ۴۷۹"), Operator("×"), Atom("۷٪"), Operator("×"), Atom("۲۴")))),
                Group(Atom("۱")),
            ),
            MathParser.parse("(۱۳۶ ۲۴۹ ۴۷۹ × ۷٪ × ۲۴) / (۱)"),
        )
    }

    @Test
    fun `nested fractions keep their groups`() {
        assertEquals(
            Fraction(
                Group(Fraction(Group(Row(listOf(Atom("a"), Operator("+"), Atom("b")))), Group(Atom("c")))),
                Group(Row(listOf(Atom("d"), Operator("−"), Atom("1")))),
            ),
            MathParser.parse("((a + b) / (c)) / (d − 1)"),
        )
    }

    @Test
    fun `fractions are left associative`() {
        assertEquals(Fraction(Fraction(Atom("a"), Atom("b")), Atom("c")), MathParser.parse("a/b/c"))
    }

    @Test
    fun `caret and superscript both make powers, a group exponent keeps its brackets`() {
        assertEquals(
            Row(
                listOf(
                    Power(Atom("x"), Atom("2")),
                    Operator("+"),
                    Power(Atom("y"), Group(Row(listOf(Atom("n"), Operator("+"), Atom("1"))))),
                )
            ),
            MathParser.parse("x^2 + y^(n+1)"),
        )
        assertEquals(Power(Atom("x"), Atom("10")), MathParser.parse("x¹⁰"))
    }

    @Test
    fun `multi word labels and space grouped numbers stay single atoms`() {
        assertEquals(
            Row(listOf(Atom("سنوات بیمه‌پردازی"), Operator("×"), Atom("۱ ۰۰۰ ۰۰۰"))),
            MathParser.parse("سنوات بیمه‌پردازی × ۱ ۰۰۰ ۰۰۰"),
        )
    }

    /** The pension formula exactly as the assistant sent it, with its empty `()` before ۳۰. */
    @Test
    fun `the assistant's pension formula keeps its subscript and loses only the empty brackets`() {
        val parsed = MathParser.parse(
            "( ((معدل نسبت حقوق یا دستمزد مشمول کسور هر سال) / (حداقل حقوق یا دستمزد همان سال))_۲ سال آخر × " +
                "حداقل حقوق یا دستمزد زمان بازنشستگی) / () ۳۰ × سنوات بیمه‌پردازی"
        )
        val average = Subscript(
            Group(Fraction(Group(Atom("معدل نسبت حقوق یا دستمزد مشمول کسور هر سال")), Group(Atom("حداقل حقوق یا دستمزد همان سال")))),
            Atom("۲ سال آخر"),
        )
        assertEquals(
            Row(
                listOf(
                    Fraction(
                        Group(Row(listOf(average, Operator("×"), Atom("حداقل حقوق یا دستمزد زمان بازنشستگی")))),
                        Atom("۳۰"),
                    ),
                    Operator("×"),
                    Atom("سنوات بیمه‌پردازی"),
                )
            ),
            parsed,
        )
        assertTrue(MathParser.isRightToLeft(parsed))
    }

    @Test
    fun `only formulas with words read right to left`() {
        assertFalse(MathParser.isRightToLeft(MathParser.parse("2/π = (1 − 1/2²)(1 − 1/4²)")))
        assertFalse(MathParser.isRightToLeft(MathParser.parse("(۱۳۶ ۲۴۹ ۴۷۹ × ۷٪ × ۲۴) / (۱)")))
        assertTrue(MathParser.isRightToLeft(MathParser.parse("x_سال")))
    }

    @Test
    fun `empty brackets vanish and a lone underscore stays visible`() {
        assertEquals(Row(listOf(Atom("a"), Operator("×"), Atom("b"))), MathParser.parse("a × (()) b"))
        assertEquals(Row(listOf(Atom("a"), Atom("_"))), MathParser.parse("a _"))
    }

    @Test
    fun `malformed input degrades instead of failing`() {
        assertEquals(Row(listOf(Atom("a"), Atom(")"))), MathParser.parse("a )"))
        assertEquals(Group(Row(listOf(Atom("a"), Operator("+"), Atom("b")))), MathParser.parse("(a + b"))
        assertEquals(Row(listOf(Operator("/"), Atom("b"))), MathParser.parse("/ b"))
        assertEquals(Row(listOf(Atom("a"), Operator("/"))), MathParser.parse("a /"))
        assertEquals(Row(emptyList()), MathParser.parse(""))
    }
}
