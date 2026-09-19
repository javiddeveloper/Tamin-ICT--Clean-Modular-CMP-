package com.tamin.taminhamrah.feature.agent.ui.markdown

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.agent.markdown.MathExpr
import com.tamin.taminhamrah.util.toPersianDigits
import kotlin.math.roundToInt

/**
 * Lays out a [MathExpr] as mathematics: stacked fractions, raised exponents, lowered subscripts and
 * brackets drawn at the height of what they enclose. Follows the layout direction: a Persian
 * formula is composed right to left, and each bracket takes the shape of the side it stands on.
 *
 * @param atomMaxWidth the widest a word or phrase may be before it wraps onto another line, so a
 *   formula made of long Persian phrases still fits a phone screen
 */
@Composable
fun MathView(expr: MathExpr, style: TextStyle, modifier: Modifier = Modifier, atomMaxWidth: Dp = Dp.Infinity) {
    CompositionLocalProvider(LocalAtomMaxWidth provides atomMaxWidth) {
        MathNode(expr, style, modifier)
    }
}

private val LocalAtomMaxWidth = staticCompositionLocalOf { Dp.Infinity }

@Composable
private fun MathNode(expr: MathExpr, style: TextStyle, modifier: Modifier = Modifier) {
    when (expr) {
        is MathExpr.Atom -> Text(
            text = expr.text.toPersianDigits(),
            style = style.copy(textAlign = TextAlign.Center),
            modifier = modifier.widthIn(max = LocalAtomMaxWidth.current),
        )

        is MathExpr.Operator -> Text(
            text = expr.symbol.displaySymbol(),
            style = style,
            modifier = modifier.padding(horizontal = 4.dp),
        )

        is MathExpr.Row -> MathAxisRow(modifier = modifier) {
            expr.children.forEach { MathNode(it, style) }
        }

        is MathExpr.Group -> BracketedGroup(style = style, modifier = modifier) {
            MathNode(expr.content, style)
        }

        is MathExpr.Fraction -> {
            val inner = style.copy(fontSize = style.fontSize.scaled(FRACTION_SCALE))
            FractionLayout(color = style.color, modifier = modifier) {
                MathNode(expr.numerator, inner)
                MathNode(expr.denominator, inner)
            }
        }

        is MathExpr.Power -> Row(modifier = modifier, verticalAlignment = Alignment.Top) {
            val exponentSize = style.fontSize.scaled(EXPONENT_SCALE)
            MathNode(
                expr.base,
                style,
                Modifier.padding(top = (exponentSize.valueOrDefault() * EXPONENT_RAISE).dp),
            )
            MathNode(expr.exponent, style.copy(fontSize = exponentSize))
        }

        // A qualifier such as «۲ سال آخر» reads next to what it qualifies, level with its middle:
        // dropped below a tall bracket it looked detached from the formula.
        is MathExpr.Subscript -> MathAxisRow(modifier = modifier) {
            MathNode(expr.base, style)
            MathNode(expr.subscript, style.copy(fontSize = style.fontSize.scaled(EXPONENT_SCALE)), Modifier.padding(horizontal = 2.dp))
        }
    }
}

/**
 * The height of a formula's fraction bar within a laid-out piece of mathematics. A [FractionLayout]
 * sets it at its bar; a [MathAxisRow] lines its children up on it, so `label =`, operators and the
 * terms beside a fraction sit level with the bar, not with the middle of a tall numerator. A piece
 * without a fraction has its axis at half its height.
 */
val MathAxis = HorizontalAlignmentLine(merger = { old, new -> maxOf(old, new) })

/** Side by side, each child placed so its [MathAxis] meets the others'; reports that axis itself. */
@Composable
fun MathAxisRow(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        var remaining = constraints.maxWidth
        val placeables = measurables.map { measurable ->
            val maxWidth = if (constraints.hasBoundedWidth) remaining.coerceAtLeast(0) else Constraints.Infinity
            measurable.measure(Constraints(maxWidth = maxWidth)).also {
                if (constraints.hasBoundedWidth) remaining -= it.width
            }
        }
        val axes = placeables.map { it.mathAxis() }
        val above = axes.maxOrNull() ?: 0
        val below = placeables.indices.maxOfOrNull { placeables[it].height - axes[it] } ?: 0
        val width = placeables.sumOf { it.width }

        layout(width, above + below, mapOf(MathAxis to above)) {
            var x = 0
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x, above - axes[index])
                x += placeable.width
            }
        }
    }
}

private fun Placeable.mathAxis(): Int = this[MathAxis].takeIf { it != AlignmentLine.Unspecified } ?: (height / 2)

/** Numerator over a bar over denominator, centred; its [MathAxis] is the bar. */
@Composable
private fun FractionLayout(color: Color, modifier: Modifier, content: @Composable () -> Unit) {
    Layout(
        modifier = modifier.padding(horizontal = 2.dp),
        content = {
            content()
            Canvas(modifier = Modifier) {
                drawLine(color, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), strokeWidth = size.height)
            }
        },
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val numerator = measurables[0].measure(loose)
        val denominator = measurables[1].measure(loose)
        val width = maxOf(numerator.width, denominator.width)
        val bar = measurables[2].measure(Constraints.fixed(width, FRACTION_BAR.roundToPx().coerceAtLeast(1)))
        val gap = FRACTION_GAP.roundToPx()
        val barTop = numerator.height + gap
        val height = barTop + bar.height + gap + denominator.height

        layout(width, height, mapOf(MathAxis to barTop + bar.height / 2)) {
            numerator.placeRelative((width - numerator.width) / 2, 0)
            bar.placeRelative(0, barTop)
            denominator.placeRelative((width - denominator.width) / 2, barTop + bar.height + gap)
        }
    }
}

/**
 * `(` content `)` where both brackets are as tall as the content, so a bracket around a fraction
 * reaches from its numerator to its denominator.
 */
@Composable
private fun BracketedGroup(style: TextStyle, modifier: Modifier, content: @Composable () -> Unit) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Layout(
        modifier = modifier.padding(horizontal = GROUP_SPACING),
        content = {
            // The opening bracket stands at the start: on the left it is "(", on the right ")".
            Bracket(leftSide = !rtl, style = style)
            Row(verticalAlignment = Alignment.CenterVertically) { content() }
            Bracket(leftSide = rtl, style = style)
        },
    ) { measurables, constraints ->
        val gap = BRACKET_GAP.roundToPx()
        // Room for both brackets is kept aside first: content measured at the full width would push
        // the brackets past the edge, where the sideways scroll clips them.
        val reserved = 2 * (MAX_BRACKET_WIDTH.roundToPx() + gap)
        val loose = constraints.copy(
            minWidth = 0,
            minHeight = 0,
            maxWidth = if (constraints.hasBoundedWidth) (constraints.maxWidth - reserved).coerceAtLeast(0) else Constraints.Infinity,
        )
        val body = measurables[1].measure(loose)
        val minBracketHeight = (style.fontSize.valueOrDefault() * 1.25f).sp.roundToPx()
        val height = maxOf(body.height, minBracketHeight)
        val bracketWidth = (height * BRACKET_WIDTH_RATIO).roundToInt()
            .coerceIn(MIN_BRACKET_WIDTH.roundToPx(), MAX_BRACKET_WIDTH.roundToPx())
        val open = measurables[0].measure(Constraints.fixed(bracketWidth, height))
        val close = measurables[2].measure(Constraints.fixed(bracketWidth, height))

        layout(open.width + gap + body.width + gap + close.width, height) {
            open.placeRelative(0, 0)
            body.placeRelative(open.width + gap, (height - body.height) / 2)
            close.placeRelative(open.width + gap + body.width + gap, 0)
        }
    }
}

@Composable
private fun Bracket(leftSide: Boolean, style: TextStyle) {
    Canvas(modifier = Modifier) {
        val inset = size.width * 0.25f
        val outer = if (leftSide) size.width - inset * 0.4f else inset * 0.4f
        val bulge = if (leftSide) inset * 0.2f else size.width - inset * 0.2f
        val path = Path().apply {
            moveTo(outer, size.height * 0.04f)
            quadraticTo(bulge - (if (leftSide) inset else -inset), size.height / 2, outer, size.height * 0.96f)
        }
        drawPath(path, style.color, style = Stroke(width = BRACKET_STROKE.toPx()))
    }
}

private fun String.displaySymbol(): String = when (this) {
    "-" -> "−"
    "*" -> "×"
    else -> this
}

private fun TextUnit.valueOrDefault(): Float = if (this == TextUnit.Unspecified) DEFAULT_FONT_SP else value

private fun TextUnit.scaled(factor: Float): TextUnit =
    (valueOrDefault() * factor).coerceAtLeast(MIN_FONT_SP).sp

private const val DEFAULT_FONT_SP = 15f
private const val MIN_FONT_SP = 10f
private const val FRACTION_SCALE = 0.88f
private const val EXPONENT_SCALE = 0.7f
private const val EXPONENT_RAISE = 0.45f
private const val BRACKET_WIDTH_RATIO = 0.18f
private val MIN_BRACKET_WIDTH = 5.dp
private val MAX_BRACKET_WIDTH = 12.dp
private val BRACKET_STROKE = 1.3.dp
/** Between a bracket and what it encloses. */
private val BRACKET_GAP = 3.dp
/** Around a whole group, so neighbouring brackets `)(` or nested `((` do not touch. */
private val GROUP_SPACING = 2.dp
private val FRACTION_BAR = 1.dp
private val FRACTION_GAP = 2.dp
