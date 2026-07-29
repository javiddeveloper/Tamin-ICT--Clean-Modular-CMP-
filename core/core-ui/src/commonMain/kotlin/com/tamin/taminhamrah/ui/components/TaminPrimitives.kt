package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Design-system building blocks shared across the app. Everything here takes primitives
 * and lambdas only, so each piece previews and snapshot-tests without a ViewModel.
 */

private val PILL_VERTICAL_PADDING = 5.dp
private val PRIMARY_BUTTON_HEIGHT = 52.dp

/**
 * A gradient sweeping along the reading direction — right to left under a right-to-left
 * layout, left to right otherwise.
 *
 * [Brush.horizontalGradient] always runs left to right in pixel space, so the stops are
 * reversed under RTL to land the first color on the start edge.
 */
@Composable
fun startToEndGradient(colors: List<Color>): Brush {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    return Brush.horizontalGradient(if (isRtl) colors.reversed() else colors)
}

/**
 * Numeric text. Amounts, national IDs and tracking codes are always laid out
 * left-to-right, matching the `dir="ltr"` the design puts on every number even inside an
 * otherwise right-to-left page.
 */
@Composable
fun NumericText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Text(text = text, style = style, color = color, modifier = modifier)
    }
}

/**
 * Rounded status badge — a validity marker, a record's category, a lab result's range.
 * Colors are passed as a container/content pair so callers can pick the green/blue/orange
 * sets straight off [com.tamin.taminhamrah.ui.theme.TaminColors].
 */
@Composable
fun StatusPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .background(containerColor, CircleShape)
            .padding(horizontal = Spacing.md, vertical = PILL_VERTICAL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(IconSize.small),
            )
        }
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = contentColor)
    }
}

/** A small caption over an emphasized figure, on a tinted rounded background. */
@Composable
fun StatTile(
    label: String,
    amount: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    labelColor: Color = contentColor,
) {
    Column(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(CornerRadius.lg))
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
            textAlign = TextAlign.Center,
        )
        NumericText(
            text = amount,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
        )
    }
}

/** Muted caption above a group of cards. */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalTaminColors.current.textMuted,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        modifier = modifier,
    )
}

/** Square-ish gradient container holding a single icon — a card's leading glyph. */
@Composable
fun IconTile(
    icon: ImageVector,
    tint: Color,
    background: Brush,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    cornerRadius: Dp = CornerRadius.iconTile,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(background, RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size / 2),
        )
    }
}

/**
 * The white card content sits on: surface fill, hairline border and the design's card
 * radius. Extracted because every card in the app repeats the same chain.
 */
@Composable
fun Modifier.taminSurface(cornerRadius: Dp = CornerRadius.card): Modifier {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(cornerRadius)
    return clip(shape)
        .background(colors.bgSurface)
        .border(1.dp, colors.border, shape)
}

/** Small muted caption above a block of body text. */
@Composable
fun LabeledBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
    }
}

/**
 * Label on one side, value on the other. [numeric] routes the value through [NumericText]
 * so codes and amounts stay left-to-right.
 */
@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = LocalTaminColors.current.textPrimary,
    valueStyle: TextStyle = MaterialTheme.typography.titleSmall,
    numeric: Boolean = true,
    /** A unit (e.g. "ریال") drawn to the left of the numeric [value], regardless of RTL. */
    unit: String? = null,
    /** Row height, for callers whose cards breathe more than the default. */
    verticalPadding: Dp = Spacing.xs,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = LocalTaminColors.current.textMuted,
        )
        when {
            // Number and unit are separate children so the unit stays physically left of the digits:
            // in the RTL row the number is the right child, the unit the left one.
            unit != null -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                NumericText(text = value, style = valueStyle, color = valueColor)
                Text(text = unit, style = valueStyle, color = valueColor)
            }

            numeric -> NumericText(text = value, style = valueStyle, color = valueColor)
            else -> Text(text = value, style = valueStyle, color = valueColor)
        }
    }
}

/** Hairline rule separating rows inside a card. */
@Composable
fun TaminDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(LocalTaminColors.current.divider),
    )
}

/** Full-width gradient call to action. */
@Composable
fun TaminPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    background: Brush = taminTopAppBarGradient(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(PRIMARY_BUTTON_HEIGHT)
            .clip(RoundedCornerShape(CornerRadius.iconTile))
            .background(background)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = Color.White)
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.medium),
            )
        }
    }
}

/** Translucent bar pinned to the bottom of a screen, holding totals or a primary action. */
@Composable
fun TaminBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalTaminColors.current.glassSolid)
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        content = content,
    )
}

/** Centred muted message for an empty list or a search that matched nothing. */
@Composable
fun TaminEmptyState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.xxl),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalTaminColors.current.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}
