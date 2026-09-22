package com.tamin.taminhamrah.feature.agent.ui.markdown

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
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
import com.tamin.taminhamrah.feature.agent.markdown.MathParser
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.TableRow
import com.tamin.taminhamrah.feature.agent.ui.bubble.TableBubble
import com.tamin.taminhamrah.feature.agent.ui.AgentActionButton
import com.tamin.taminhamrah.feature.agent.ui.AgentGlass
import com.tamin.taminhamrah.feature.agent.ui.agentGlassCard
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_markdown_formula
import taminx.core.core_ui.agent_markdown_table

/**
 * Draws an assistant markdown answer, block by block, in the app's design system: prose in the
 * app typography, tables and formulas on the app's surface card, links as the app's blue buttons.
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
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
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

/**
 * One block of the answer. Everything here sits on the fixed-dark `AgentBackground`, so the
 * tiles (quote, code, rule) and markers use the [AgentGlass] palette, never the light/dark
 * theme's surfaces — those are white in light mode and showed up as white blocks in the chat.
 */
@Composable
private fun MarkdownBlockView(
    block: MarkdownBlock,
    contentColor: Color,
    onLinkClick: (String) -> Unit,
) {
    val body = MaterialTheme.typography.bodyMedium.copy(color = contentColor, lineHeight = BODY_LINE_HEIGHT)
    val tileShape = RoundedCornerShape(CornerRadius.md)
    when (block) {
        is MarkdownBlock.Heading -> MarkdownHeading(block, contentColor)

        is MarkdownBlock.Paragraph -> Text(text = block.text.toStyledText(), style = body)

        is MarkdownBlock.ListItem -> MarkdownListItem(block, body)

        // A quote is a subtle glass tile with an accent bar on its leading edge.
        is MarkdownBlock.Quote -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(tileShape)
                .background(AgentGlass.tileFillSubtle)
                .border(AgentGlass.borderWidth, AgentGlass.borderColor, tileShape),
        ) {
            Box(
                modifier = Modifier
                    .width(QUOTE_BAR_WIDTH)
                    .fillMaxHeight()
                    .background(AgentGlass.accent),
            )
            Text(
                text = block.text.toStyledText(),
                style = body.copy(color = AgentGlass.textSecondary),
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            )
        }

        // Code reads left to right, on the same glass card as tables and formulas.
        is MarkdownBlock.CodeBlock -> CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = block.code,
                style = MaterialTheme.typography.bodySmall.copy(color = AgentGlass.textPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .agentGlassCard(tileShape)
                    .horizontalScroll(rememberScrollState())
                    .padding(Spacing.md),
            )
        }

        MarkdownBlock.Rule -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.xs)
                .height(Thickness.border)
                .background(AgentGlass.borderColor),
        )

        is MarkdownBlock.Table -> MarkdownSection(
            title = stringResource(Res.string.agent_markdown_table),
            icon = Icons.Outlined.TableChart,
            copyText = block.toPlainText(),
        ) {
            // Cells keep their inline markdown. A cell that is just a link is a button, like the
            // links outside tables; a link inside other text is tapped in place.
            // On the glass card the cells take the reply's own text color, not the theme's.
            val linkColor = AgentGlass.accent
            val cellStyle = MaterialTheme.typography.bodySmall.copy(color = contentColor)
            TableBubble(
                content = ChatBubbleContent.Table(
                    columns = block.header.map { it.toPersianDigits() },
                    rows = block.rows.map { row -> TableRow(row) },
                ),
                framed = false,
                renderCell = { cell -> cell.styledText(AgentGlass.tileFill, linkColor, onLinkClick) },
                cellContent = { cell ->
                    val link = remember(cell) { MarkdownParser.loneLink(cell) }
                    if (link != null) {
                        LinkButton(link = link, onLinkClick = onLinkClick, compact = true)
                    } else {
                        Text(text = cell.toStyledText(linkColor, onLinkClick), style = cellStyle, maxLines = TABLE_CELL_MAX_LINES)
                    }
                },
            )
        }

        is MarkdownBlock.Formula -> MarkdownSection(
            title = stringResource(Res.string.agent_markdown_formula),
            icon = Icons.Outlined.Functions,
            copyText = block.raw,
        ) {
            FormulaRow(formula = block, color = contentColor)
        }

        is MarkdownBlock.Actions -> ActionButtons(links = block.links, onLinkClick = onLinkClick)
    }
}

/** `#`–`##` read as titles, deeper levels as the app's muted section captions. */
@Composable
private fun MarkdownHeading(block: MarkdownBlock.Heading, contentColor: Color) {
    val style = when (block.level) {
        1 -> MaterialTheme.typography.titleMedium.copy(color = contentColor, fontWeight = FontWeight.Bold)
        2 -> MaterialTheme.typography.titleSmall.copy(color = contentColor, fontWeight = FontWeight.Bold)
        else -> MaterialTheme.typography.labelLarge.copy(color = AgentGlass.textSecondary, fontWeight = FontWeight.Bold)
    }
    Text(
        text = block.text.toStyledText(),
        style = style,
        modifier = Modifier.padding(top = if (block.level <= 2) Spacing.xs else Spacing.none),
    )
}

/** A bullet is a small accent dot, a number the accent blue; nesting indents by [Spacing.lg]. */
@Composable
private fun MarkdownListItem(block: MarkdownBlock.ListItem, body: TextStyle) {
    Row(
        modifier = Modifier.padding(start = Spacing.lg * block.level),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier.heightIn(min = BODY_LINE_HEIGHT_DP),
            contentAlignment = Alignment.Center,
        ) {
            if (block.ordered) {
                Text(
                    text = "${block.number}.".toPersianDigits(),
                    style = body.copy(color = AgentGlass.accent, fontWeight = FontWeight.Bold),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(BULLET_SIZE)
                        .background(if (block.level == 0) AgentGlass.accent else AgentGlass.textSecondary, CircleShape),
                )
            }
        }
        Text(text = block.text.toStyledText(), style = body)
    }
}

/** A table or formula on the app's surface card, under an icon, a caption and a copy button. */
@Composable
private fun MarkdownSection(
    title: String,
    icon: ImageVector,
    copyText: String,
    content: @Composable () -> Unit,
) {
    // The same glass card as the screen's top/input bars, not the theme's opaque surface:
    // the reply sits on the fixed-dark AgentBackground, so the card and everything on it
    // use the AgentGlass palette rather than the light/dark TaminColors.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .agentGlassCard(RoundedCornerShape(CornerRadius.xl))
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.badge)
                    .background(AgentGlass.tileFill, RoundedCornerShape(CornerRadius.avatarTile)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = AgentGlass.iconTint, modifier = Modifier.size(IconSize.small))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = AgentGlass.textSecondary,
                modifier = Modifier.weight(1f),
            )
            CopyIconButton(value = copyText, label = title, tint = AgentGlass.textSecondary)
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
    val mathStyle = style.copy(color = AgentGlass.formula)
    val direction = if (MathParser.isRightToLeft(formula.expression)) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val atomMaxWidth = maxWidth * ATOM_WIDTH_FRACTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = Spacing.xs),
            ) {
                // Label, `=` and the expression's terms line up on the main fraction bar.
                MathAxisRow {
                    formula.label?.let { label ->
                        Text(text = label.toStyledText(), style = style.copy(fontWeight = FontWeight.Bold))
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
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        links.forEach { link -> LinkButton(link = link, onLinkClick = onLinkClick, compact = false) }
    }
}

/**
 * A link as the reply's gradient action button. [compact] is the table-cell size. Only a link the
 * app can act on is tappable; the feature flag is checked on tap.
 */
@Composable
private fun LinkButton(link: MarkdownLink, onLinkClick: (String) -> Unit, compact: Boolean) {
    val isActionable = remember(link.url) {
        DeepLinkParser.parse(link.url, DeepLinkSource.AGENT) != ParsedDeepLink.Invalid
    }
    AgentActionButton(
        label = link.label,
        onClick = { onLinkClick(link.url) },
        enabled = isActionable,
        compact = compact,
    )
}

/**
 * Inline markdown as styled text, with Persian digits outside code and grouped numbers isolated.
 *
 * A link the app can act on becomes a tappable span handed to [onLinkClick] — the same route as a
 * link button, so prompt links go back into the chat and every other link through the feature-flag
 * gate on tap. A link the app cannot read shows only its label.
 */
@Composable
private fun String.toStyledText(
    linkColor: Color = Color.Unspecified,
    onLinkClick: ((String) -> Unit)? = null,
): AnnotatedString {
    // Inline code sits on a glass tile, like every other boxed thing in a reply.
    val codeBackground = AgentGlass.tileFill
    return remember(this, linkColor, onLinkClick, codeBackground) {
        styledText(codeBackground, linkColor, onLinkClick)
    }
}

private fun String.styledText(
    codeBackground: Color,
    linkColor: Color,
    onLinkClick: ((String) -> Unit)?,
): AnnotatedString = buildAnnotatedString {
    MarkdownInlineParser.parse(this@styledText).forEach { run: InlineRun ->
        val text = if (run.code) run.text else MarkdownInlineParser.isolateGroupedNumbers(run.text.toPersianDigits())
        val style = SpanStyle(
            fontWeight = if (run.bold) FontWeight.Bold else null,
            fontStyle = if (run.italic) FontStyle.Italic else null,
            textDecoration = if (run.strike) TextDecoration.LineThrough else null,
            background = if (run.code) codeBackground else Color.Unspecified,
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
private const val BLOCK_FADE_MS = 220
private const val TABLE_CELL_MAX_LINES = 4
private val BODY_LINE_HEIGHT = 22.sp
/** [BODY_LINE_HEIGHT] as a size, so a list marker centres on the first line of its item. */
private val BODY_LINE_HEIGHT_DP = 22.dp
private val BULLET_SIZE = 6.dp
private val QUOTE_BAR_WIDTH = 3.dp
private val FORMULA_FONT_SIZE = 14.sp
private val FORMULA_LINE_HEIGHT = 20.sp
/** A phrase in a formula wraps past this share of the card, so two phrases and their brackets fit side by side. */
private const val ATOM_WIDTH_FRACTION = 0.3f
