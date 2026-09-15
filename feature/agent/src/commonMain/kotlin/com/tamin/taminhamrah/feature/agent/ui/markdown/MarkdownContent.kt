package com.tamin.taminhamrah.feature.agent.ui.markdown

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.deeplink.DeepLinkParser
import com.tamin.taminhamrah.deeplink.DeepLinkSource
import com.tamin.taminhamrah.deeplink.ParsedDeepLink
import com.tamin.taminhamrah.feature.agent.markdown.InlineRun
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownBlock
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownInlineParser
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownLink
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownParser
import com.tamin.taminhamrah.feature.agent.markdown.MathExpr
import com.tamin.taminhamrah.feature.agent.markdown.MathParser
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.TableRow
import com.tamin.taminhamrah.feature.agent.ui.bubble.TableBubble
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_markdown_formula
import taminx.core.core_ui.agent_markdown_table

/**
 * Draws an assistant markdown answer, block by block.
 *
 * While [isAnimating], blocks appear one after another so the card grows with its content; a
 * message already seen shows at once. [onLinkClick] receives the raw link of a tapped button —
 * the caller routes it (prompt links back into the chat, the rest through the deep link gate).
 */
@Composable
fun MarkdownContent(
    text: String,
    isAnimating: Boolean,
    contentColor: Color,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onRequestScroll: () -> Unit = {},
    onAnimationFinished: () -> Unit = {},
) {
    val blocks = remember(text) { MarkdownParser.parse(text) }
    var revealed by remember(text) { mutableIntStateOf(if (isAnimating) 0 else blocks.size) }

    LaunchedEffect(text) {
        while (revealed < blocks.size) {
            revealed++
            onRequestScroll()
            delay(BLOCK_REVEAL_DELAY_MS)
        }
        onAnimationFinished()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            blocks.forEachIndexed { index, block ->
                AnimatedVisibility(
                    visible = index < revealed,
                    enter = fadeIn(tween(BLOCK_FADE_MS)) + expandVertically(tween(BLOCK_FADE_MS)),
                ) {
                    MarkdownBlockView(block = block, contentColor = contentColor, onLinkClick = onLinkClick)
                }
            }
        }
    }
}

@Composable
private fun MarkdownBlockView(
    block: MarkdownBlock,
    contentColor: Color,
    onLinkClick: (String) -> Unit,
) {
    val body = MaterialTheme.typography.bodyMedium.copy(color = contentColor, lineHeight = 22.sp)
    when (block) {
        is MarkdownBlock.Heading -> Text(
            text = block.text.toStyledText(),
            style = when (block.level) {
                1 -> MaterialTheme.typography.titleLarge
                2 -> MaterialTheme.typography.titleMedium
                else -> MaterialTheme.typography.titleSmall
            }.copy(color = contentColor, fontWeight = FontWeight.Bold),
        )

        is MarkdownBlock.Paragraph -> Text(text = block.text.toStyledText(), style = body)

        is MarkdownBlock.ListItem -> Row(modifier = Modifier.padding(start = (block.level * 14).dp)) {
            val marker = if (block.ordered) "${block.number}.".toPersianDigits() else "•"
            Text(text = marker, style = body, modifier = Modifier.padding(end = 6.dp))
            Text(text = block.text.toStyledText(), style = body)
        }

        is MarkdownBlock.Quote -> Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
            )
            Text(
                text = block.text.toStyledText(),
                style = body.copy(color = LocalTaminColors.current.textSecondary),
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        is MarkdownBlock.CodeBlock -> Text(
            text = block.code,
            style = body,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                .padding(8.dp),
        )

        MarkdownBlock.Rule -> HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 4.dp),
        )

        is MarkdownBlock.Table -> MarkdownSection(
            title = stringResource(Res.string.agent_markdown_table),
            copyText = block.toPlainText(),
        ) {
            // Cells keep their inline markdown. A cell that is just a link is a button, like the
            // links outside tables; a link inside other text is tapped in place.
            val linkColor = MaterialTheme.colorScheme.primary
            val bodyStyle = MaterialTheme.typography.bodySmall.copy(color = LocalTaminColors.current.textPrimary)
            TableBubble(
                content = ChatBubbleContent.Table(
                    columns = block.header,
                    rows = block.rows.map { row -> TableRow(row) },
                ),
                renderCell = { cell -> cell.toStyledText(linkColor, onLinkClick) },
                cellContent = { cell ->
                    val link = remember(cell) { MarkdownParser.loneLink(cell) }
                    if (link != null) {
                        TableLinkButton(link = link, onLinkClick = onLinkClick)
                    } else {
                        Text(text = cell.toStyledText(linkColor, onLinkClick), style = bodyStyle, maxLines = TABLE_CELL_MAX_LINES)
                    }
                },
            )
        }

        is MarkdownBlock.Formula -> MarkdownSection(
            title = stringResource(Res.string.agent_markdown_formula),
            copyText = block.raw,
        ) {
            FormulaRow(formula = block, color = contentColor)
        }

        is MarkdownBlock.Actions -> ActionButtons(links = block.links, onLinkClick = onLinkClick)
    }
}

/** A titled card with a copy button, used for tables and formulas. */
@Composable
private fun MarkdownSection(
    title: String,
    copyText: String,
    content: @Composable () -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textMuted,
                modifier = Modifier.weight(1f),
            )
            CopyIconButton(value = copyText, label = title)
        }
        content()
    }
}

/**
 * `label = expression` on one line. A formula with Persian words reads right to left, one made only
 * of numbers and symbols left to right (`2/π = …`). A formula wider than the card scrolls sideways
 * rather than breaking or shrinking.
 */
@Composable
private fun FormulaRow(formula: MarkdownBlock.Formula, color: Color) {
    // The app's font, like the prose around it; a bare TextStyle would fall back to the system font.
    val style = MaterialTheme.typography.bodyMedium.copy(color = color, fontSize = FORMULA_FONT_SIZE, lineHeight = FORMULA_LINE_HEIGHT)
    val mathStyle = style.copy(color = MaterialTheme.colorScheme.primary)
    val direction = if (MathParser.isRightToLeft(formula.expression)) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val atomMaxWidth = maxWidth * ATOM_WIDTH_FRACTION
            Box(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                // Label, `=` and the expression's terms line up on the main fraction bar.
                MathAxisRow {
                    formula.label?.let { label ->
                        Text(text = label.toStyledText(), style = style.copy(fontWeight = FontWeight.Medium))
                        Text(text = " = ", style = style)
                    }
                    MathView(expr = formula.expression, style = mathStyle, atomMaxWidth = atomMaxWidth)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActionButtons(links: List<MarkdownLink>, onLinkClick: (String) -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        links.forEach { link ->
            // Only a link the app can act on is tappable; the feature flag is checked on tap.
            val isActionable = remember(link.url) {
                DeepLinkParser.parse(link.url, DeepLinkSource.AGENT) != ParsedDeepLink.Invalid
            }
            OutlinedButton(
                onClick = { onLinkClick(link.url) },
                enabled = isActionable,
                modifier = Modifier.alpha(if (isActionable) 1f else DISABLED_LINK_ALPHA),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
            ) {
                Text(text = link.label.toPersianDigits(), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/** A link that fills a table cell, drawn as a compact button; disabled when the app cannot open it. */
@Composable
private fun TableLinkButton(link: MarkdownLink, onLinkClick: (String) -> Unit) {
    val isActionable = remember(link.url) {
        DeepLinkParser.parse(link.url, DeepLinkSource.AGENT) != ParsedDeepLink.Invalid
    }
    // Sized by its label, with room around it: a fixed-height button squeezed a two-line label
    // against its border. A label too long for the cell wraps to a second line inside the padding.
    val primary = MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(CornerRadius.md)
    Box(
        modifier = Modifier
            .alpha(if (isActionable) 1f else DISABLED_LINK_ALPHA)
            .heightIn(min = TABLE_BUTTON_MIN_HEIGHT)
            .clip(shape)
            .background(primary.copy(alpha = TABLE_BUTTON_FILL_ALPHA))
            .border(1.dp, primary.copy(alpha = TABLE_BUTTON_BORDER_ALPHA), shape)
            .clickable(enabled = isActionable) { onLinkClick(link.url) }
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = link.label.toPersianDigits(),
            style = MaterialTheme.typography.labelSmall.copy(lineHeight = TABLE_BUTTON_LINE_HEIGHT),
            color = primary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Inline markdown as styled text, with Persian digits outside code and grouped numbers isolated.
 *
 * A link the app can act on becomes a tappable span handed to [onLinkClick] — the same route as a
 * link button, so prompt links go back into the chat and every other link through the feature-flag
 * gate on tap. A link the app cannot read shows only its label.
 */
private fun String.toStyledText(
    linkColor: Color = Color.Unspecified,
    onLinkClick: ((String) -> Unit)? = null,
): AnnotatedString = buildAnnotatedString {
    MarkdownInlineParser.parse(this@toStyledText).forEach { run: InlineRun ->
        val text = if (run.code) run.text else MarkdownInlineParser.isolateGroupedNumbers(run.text.toPersianDigits())
        val style = SpanStyle(
            fontWeight = if (run.bold) FontWeight.Bold else null,
            fontStyle = if (run.italic) FontStyle.Italic else null,
            textDecoration = if (run.strike) TextDecoration.LineThrough else null,
            background = if (run.code) Color.Gray.copy(alpha = 0.12f) else Color.Unspecified,
        )
        val url = run.link?.takeIf { onLinkClick != null && DeepLinkParser.parse(it, DeepLinkSource.AGENT) != ParsedDeepLink.Invalid }
        if (url == null) {
            withStyle(style) { append(text) }
        } else {
            val linkStyle = style.merge(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline))
            withLink(LinkAnnotation.Clickable(tag = url, styles = TextLinkStyles(style = linkStyle)) { onLinkClick?.invoke(url) }) {
                append(text)
            }
        }
    }
}

private fun MarkdownBlock.Table.toPlainText(): String =
    (listOf(header) + rows).joinToString("\n") { row -> row.joinToString("\t") { cell -> MarkdownInlineParser.parse(cell).joinToString("") { it.text } } }

private const val BLOCK_REVEAL_DELAY_MS = 90L
private const val TABLE_CELL_MAX_LINES = 4
private val TABLE_BUTTON_MIN_HEIGHT = 32.dp
private val TABLE_BUTTON_LINE_HEIGHT = 16.sp
private const val TABLE_BUTTON_FILL_ALPHA = 0.06f
private const val TABLE_BUTTON_BORDER_ALPHA = 0.45f
private const val BLOCK_FADE_MS = 220
private const val DISABLED_LINK_ALPHA = 0.5f
private val FORMULA_FONT_SIZE = 14.sp
private val FORMULA_LINE_HEIGHT = 20.sp
/** A phrase in a formula wraps past this share of the card, so two phrases and their brackets fit side by side. */
private const val ATOM_WIDTH_FRACTION = 0.3f
