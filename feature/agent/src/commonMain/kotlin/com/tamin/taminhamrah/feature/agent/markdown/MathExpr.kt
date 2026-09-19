package com.tamin.taminhamrah.feature.agent.markdown

/**
 * A formula as a tree, laid out left to right.
 *
 * Every parenthesis in the source survives as a [Group] — including one that wraps a fraction's
 * numerator or denominator. The renderer draws each group's brackets at its content's height.
 */
sealed interface MathExpr {
    data class Atom(val text: String) : MathExpr
    data class Operator(val symbol: String) : MathExpr
    data class Group(val content: MathExpr) : MathExpr
    data class Fraction(val numerator: MathExpr, val denominator: MathExpr) : MathExpr
    data class Power(val base: MathExpr, val exponent: MathExpr) : MathExpr
    data class Subscript(val base: MathExpr, val subscript: MathExpr) : MathExpr
    data class Row(val children: List<MathExpr>) : MathExpr
}

/**
 * Parses a formula expression into a [MathExpr].
 *
 * Grammar, tightest first:
 * - primary: an atom (number, word, symbol run) or a `( … )` group
 * - power: `primary ^ primary`, or a primary followed by superscript digits (`2²`)
 * - subscript: `primary _ primary`, e.g. `(a/b)_۲ سال آخر` — averaged over the last two years
 * - fraction: `power / power / …`, left associative — `1/2²` is one over two squared
 * - row: fractions joined by operators or simply side by side (`(a)(b)`)
 *
 * Atoms separated only by spaces merge into one, so a space-grouped number (`136 249 479`) or a
 * multi-word label stays a single unit. Empty brackets `()` hold nothing and are dropped — the
 * assistant has sent `… / () ۳۰`, meaning `… / ۳۰`. Never throws: an unmatched `)` becomes a plain
 * atom and a group left open at the end is closed.
 */
object MathParser {

    private val OPERATORS = setOf('+', '−', '-', '×', '÷', '·', '*', '=', '≤', '≥', '±', '<', '>', '≈', '≠')
    private const val SUPERSCRIPTS = "⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻⁽⁾ⁿⁱ"
    private const val PLAIN_OF_SUPERSCRIPTS = "0123456789+-()ni"

    fun parse(source: String): MathExpr {
        val tokens = tokenize(source).withoutEmptyGroups()
        val cursor = Cursor(tokens)
        val row = parseRow(cursor, insideGroup = false)
        return row.flatten()
    }

    fun isOperatorChar(c: Char): Boolean = c in OPERATORS || c == '/' || c == '^' || c == '_' || c in SUPERSCRIPTS

    /** Whether the formula has words in a right-to-left script, so it reads right to left. */
    fun isRightToLeft(expr: MathExpr): Boolean = when (expr) {
        is MathExpr.Atom -> expr.text.any { it.isRtlLetter() }
        is MathExpr.Operator -> false
        is MathExpr.Group -> isRightToLeft(expr.content)
        is MathExpr.Fraction -> isRightToLeft(expr.numerator) || isRightToLeft(expr.denominator)
        is MathExpr.Power -> isRightToLeft(expr.base) || isRightToLeft(expr.exponent)
        is MathExpr.Subscript -> isRightToLeft(expr.base) || isRightToLeft(expr.subscript)
        is MathExpr.Row -> expr.children.any(::isRightToLeft)
    }

    /** Arabic-script letters; Persian and Arabic digits are not, a number alone stays left to right. */
    private fun Char.isRtlLetter(): Boolean =
        isLetter() && code.let { it in 0x0600..0x06FF || it in 0x0750..0x077F || it in 0xFB50..0xFDFF || it in 0xFE70..0xFEFF }

    // ── Tokens ─────────────────────────────────────────────────────────────

    private sealed interface Token {
        data class Atom(val text: String) : Token
        data class Op(val symbol: String) : Token
        data object Open : Token
        data object Close : Token
        data object Slash : Token
        data object Caret : Token
        data object Underscore : Token
        data class Superscript(val text: String) : Token
    }

    private class Cursor(val tokens: List<Token>) {
        var index = 0
        fun peek(): Token? = tokens.getOrNull(index)
        fun next(): Token? = tokens.getOrNull(index++)
    }

    private fun tokenize(source: String): List<Token> {
        val tokens = mutableListOf<Token>()
        val atom = StringBuilder()
        var pendingSpace = false

        fun flushAtom() {
            if (atom.isEmpty()) return
            val text = atom.toString()
            val last = tokens.lastOrNull()
            // Space-separated atoms merge: "136 249 479", "سنوات بیمه‌پردازی".
            if (last is Token.Atom && pendingSpace) {
                tokens[tokens.lastIndex] = Token.Atom(last.text + " " + text)
            } else {
                tokens.add(Token.Atom(text))
            }
            atom.clear()
            pendingSpace = false
        }

        var i = 0
        while (i < source.length) {
            val c = source[i]
            when {
                c.isWhitespace() -> {
                    if (atom.isNotEmpty()) {
                        flushAtom()
                        pendingSpace = true
                    } else if (tokens.lastOrNull() is Token.Atom) {
                        pendingSpace = true
                    }
                }
                c == '(' -> { flushAtom(); pendingSpace = false; tokens.add(Token.Open) }
                c == ')' -> { flushAtom(); pendingSpace = false; tokens.add(Token.Close) }
                c == '/' -> { flushAtom(); pendingSpace = false; tokens.add(Token.Slash) }
                c == '^' -> { flushAtom(); pendingSpace = false; tokens.add(Token.Caret) }
                c == '_' -> { flushAtom(); pendingSpace = false; tokens.add(Token.Underscore) }
                c in SUPERSCRIPTS -> {
                    flushAtom()
                    pendingSpace = false
                    val start = i
                    while (i + 1 < source.length && source[i + 1] in SUPERSCRIPTS) i++
                    val plain = source.substring(start, i + 1)
                        .map { PLAIN_OF_SUPERSCRIPTS[SUPERSCRIPTS.indexOf(it)] }
                        .joinToString("")
                    tokens.add(Token.Superscript(plain))
                }
                c in OPERATORS -> {
                    flushAtom()
                    pendingSpace = false
                    tokens.add(Token.Op(c.toString()))
                }
                else -> atom.append(c)
            }
            i++
        }
        flushAtom()
        return tokens
    }

    /** Removes `()` pairs, innermost first, so `(())` goes too. */
    private fun List<Token>.withoutEmptyGroups(): List<Token> {
        val result = mutableListOf<Token>()
        for (token in this) {
            if (token == Token.Close && result.lastOrNull() == Token.Open) {
                result.removeAt(result.lastIndex)
            } else {
                result.add(token)
            }
        }
        return result
    }

    // ── Grammar ────────────────────────────────────────────────────────────

    private fun parseRow(cursor: Cursor, insideGroup: Boolean): MathExpr.Row {
        val children = mutableListOf<MathExpr>()
        while (true) {
            val token = cursor.peek() ?: break
            when (token) {
                Token.Close -> {
                    if (insideGroup) break
                    // An unmatched closing bracket is shown as written.
                    cursor.next()
                    children.add(MathExpr.Atom(")"))
                }
                is Token.Op -> {
                    cursor.next()
                    children.add(MathExpr.Operator(token.symbol))
                }
                else -> parseFraction(cursor)?.let(children::add) ?: run {
                    // A stray "/", "^" or superscript with nothing before it: keep it visible.
                    when (val stray = cursor.next()) {
                        Token.Slash -> children.add(MathExpr.Operator("/"))
                        Token.Caret -> children.add(MathExpr.Atom("^"))
                        Token.Underscore -> children.add(MathExpr.Atom("_"))
                        is Token.Superscript -> children.add(MathExpr.Atom(stray.text))
                        else -> Unit
                    }
                }
            }
        }
        return MathExpr.Row(children)
    }

    private fun parseFraction(cursor: Cursor): MathExpr? {
        var left = parsePower(cursor) ?: return null
        while (cursor.peek() == Token.Slash) {
            val save = cursor.index
            cursor.next()
            val right = parsePower(cursor)
            if (right == null) {
                cursor.index = save
                break
            }
            left = MathExpr.Fraction(left, right)
        }
        return left
    }

    private fun parsePower(cursor: Cursor): MathExpr? {
        val base = parsePrimary(cursor) ?: return null
        return when (val token = cursor.peek()) {
            is Token.Superscript -> {
                cursor.next()
                MathExpr.Power(base, MathExpr.Atom(token.text))
            }
            Token.Caret -> scriptOf(cursor, base, MathExpr::Power)
            Token.Underscore -> scriptOf(cursor, base, MathExpr::Subscript)
            else -> base
        }
    }

    /** `base ^ x` or `base _ x`; a marker with nothing after it is left for the row to show. */
    private fun scriptOf(cursor: Cursor, base: MathExpr, build: (MathExpr, MathExpr) -> MathExpr): MathExpr {
        val save = cursor.index
        cursor.next()
        val script = parsePrimary(cursor) ?: run {
            cursor.index = save
            return base
        }
        return build(base, script)
    }

    private fun parsePrimary(cursor: Cursor): MathExpr? = when (val token = cursor.peek()) {
        is Token.Atom -> {
            cursor.next()
            MathExpr.Atom(token.text)
        }
        Token.Open -> {
            cursor.next()
            val content = parseRow(cursor, insideGroup = true)
            if (cursor.peek() == Token.Close) cursor.next()
            MathExpr.Group(content.flatten())
        }
        else -> null
    }

    /** A row of one child is that child; nested rows are inlined. */
    private fun MathExpr.flatten(): MathExpr = when (this) {
        is MathExpr.Row -> {
            val flat = children.flatMap { child ->
                val f = child.flatten()
                if (f is MathExpr.Row) f.children else listOf(f)
            }
            flat.singleOrNull() ?: MathExpr.Row(flat)
        }
        else -> this
    }
}
