package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Design-system building blocks shared across the app. Everything here takes primitives
 * and lambdas only, so each piece previews and snapshot-tests without a ViewModel.
 */

private val PRIMARY_BUTTON_HEIGHT = 52.dp

/** The navy cast under the primary button. Public so a caller can tint its own shadow to match. */
val PrimaryButtonShadow = Color(0x47173D7E)

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
    fontWeight: FontWeight = FontWeight.Medium,
    verticalPadding: Dp = 5.dp,
) {
    Row(
        modifier = modifier
            .background(containerColor, CircleShape)
            .padding(horizontal = Spacing.md, vertical = verticalPadding),
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
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = fontWeight),
            color = contentColor
        )
    }
}

/**
 * How much room a [StatTile] takes.
 *
 * One property rather than a pair of booleans: `dense` and `inline` would spell four states of
 * which only these three mean anything.
 */
enum class StatTileStyle {
    /** Caption over the figure. What a tile standing on its own uses. */
    Standard,

    /** The same stack, tighter — for a row of tiles sitting inside a list item. */
    Dense,

    /** Caption beside the figure, which is what actually halves the height. */
    Inline,
}

/** A small caption over — or beside — an emphasized figure, on a tinted rounded background. */
@Composable
fun StatTile(
    label: String,
    /** `null` while the figure is still being fetched: the tile shimmers instead of reading zero. */
    amount: String?,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    labelColor: Color = contentColor,
    style: StatTileStyle = StatTileStyle.Standard,
    /**
     * The opaque color the tint is composited over.
     *
     * [containerColor] is a low-alpha tint in the dark theme (16%), so painting it straight onto a
     * card let the card's own gradient read through and the tile looked washed out. Laying it over
     * a solid surface first keeps exactly the intended hue while making the tile itself opaque --
     * the glass stays *around* the tiles, not inside them.
     */
    baseColor: Color = LocalTaminColors.current.bgSurface,
) {
    val compact = style != StatTileStyle.Standard
    val inline = style == StatTileStyle.Inline
    val shape = RoundedCornerShape(if (compact) CornerRadius.md else CornerRadius.lg)
    val container = modifier
        .background(baseColor, shape)
        .background(containerColor, shape)
        .padding(
            horizontal = Spacing.md,
            // Inline is the only style read as a standalone chip rather than part of a group,
            // and it sat at 11sp -- a size an older reader has to work at. Roomier on purpose.
            vertical = when {
                inline -> Spacing.sm
                compact -> Spacing.xs
                else -> Spacing.md
            },
        )

    val labelText: @Composable () -> Unit = {
        Text(
            text = label,
            style = when {
                inline -> MaterialTheme.typography.labelMedium
                compact -> MaterialTheme.typography.labelSmall
                else -> MaterialTheme.typography.labelMedium
            },
            color = labelColor,
            textAlign = TextAlign.Center,
        )
    }
    val amountText: @Composable () -> Unit = {
        if (amount == null) {
            ShimmerBlock(
                modifier = Modifier
                    .padding(vertical = Spacing.xxs)
                    .width(ShimmerSize.valueWidth)
                    .height(ShimmerSize.valueHeight),
            )
        } else {
            NumericText(
                text = amount,
                style = when {
                    inline -> MaterialTheme.typography.titleSmall
                    compact -> MaterialTheme.typography.labelMedium
                    else -> MaterialTheme.typography.titleMedium
                },
                color = contentColor,
            )
        }
    }

    if (style == StatTileStyle.Inline) {
        Row(
            modifier = container,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
        ) {
            labelText()
            amountText()
        }
    } else {
        Column(
            modifier = container,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                if (style == StatTileStyle.Dense) Spacing.xxs else Spacing.xs,
            ),
        ) {
            labelText()
            amountText()
        }
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
    /**
     * Makes the row copy this to the clipboard when tapped, and shows a copy glyph beside the
     * value to say so.
     *
     * Separate from [value] because the two differ: a code is displayed in Persian digits and has
     * to be copied in ASCII ones, or what gets pasted matches nothing.
     *
     * The whole row is the target, not the glyph — the glyph is 16dp and a poor thing to aim at.
     */
    copyValue: String? = null,
) {
    val copy = copyValue?.let { rememberCopyAction(it) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (copy != null) Modifier.clickable(onClick = copy) else Modifier)
            .padding(vertical = verticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = LocalTaminColors.current.textMuted,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            // First child, so under the app's right-to-left layout the glyph sits to the *right*
            // of the value it copies rather than drifting off to the far edge.
            if (copyValue != null) {
                CopyIconButton(value = copyValue, label = label, interactive = false)
            }
            when {
                // Number and unit are separate children so the unit stays physically left of the
                // digits: in the RTL row the number is the right child, the unit the left one.
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
    /**
     * Puts the icon ahead of the label instead of after it — the leading edge, so it reads on the
     * right in a right-to-left layout. Defaults to the trailing position every existing caller has.
     */
    iconAtStart: Boolean = false,
) {
    val iconContent: @Composable () -> Unit = {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.medium),
            )
        }
    }

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
        if (iconAtStart) iconContent()
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = Color.White)
        if (!iconAtStart) iconContent()
    }
}

@Composable
fun TaminOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    iconModifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(CornerRadius.iconTile),
    height: Dp = PRIMARY_BUTTON_HEIGHT,
    borderWidth: Dp = 1.dp,
    borderColor: Color = LocalTaminColors.current.border,
    containerColor: Color = Color.Transparent,
    contentColor: Color = LocalTaminColors.current.textPrimary,
    disabledBorderColor: Color = LocalTaminColors.current.border.copy(alpha = 0.5f),
    disabledContainerColor: Color = Color.Transparent,
    disabledContentColor: Color = LocalTaminColors.current.textMuted,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val currentBorderColor = if (enabled) borderColor else disabledBorderColor
    val currentContainerColor = if (enabled) containerColor else disabledContainerColor
    val currentContentColor = if (enabled) contentColor else disabledContentColor

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(currentContainerColor)
            .border(borderWidth, currentBorderColor, shape)
            .clickable(
                enabled = enabled,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            Spacing.sm,
            Alignment.CenterHorizontally,
        ),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = currentContentColor,
                modifier = Modifier.size(IconSize.medium).then(iconModifier),
            )
        }

        Text(
            text = text,
            style = textStyle,
            color = currentContentColor
        )
    }
}

enum class IconPosition {
    Start,
    End,
}

@Composable
fun TaminFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    painter: Painter? = null,
    iconPosition: IconPosition = IconPosition.Start,
    shape: Shape = RoundedCornerShape(CornerRadius.iconTile),
    height: Dp = PRIMARY_BUTTON_HEIGHT,
    background: Brush = LocalTaminColors.current.heroGradient,
    disabledBackgroundColor: Color = LocalTaminColors.current.border,
    contentColor: Color = Color.White,
    disabledContentColor: Color = LocalTaminColors.current.textMuted,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    /** Defaults to the navy cast the primary button drops; teal buttons pass their own. */
    shadowColor: Color = PrimaryButtonShadow,
) {
    val showIconBeforeText =
        (LocalLayoutDirection.current == LayoutDirection.Ltr && iconPosition == IconPosition.Start) ||
            (LocalLayoutDirection.current == LayoutDirection.Rtl && iconPosition == IconPosition.End)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = 22.dp,
                shape = shape,
                spotColor = shadowColor,
                ambientColor = shadowColor,
            )
            .clip(shape)
            .background(
                if (enabled) {
                    background
                } else {
                    Brush.linearGradient(
                        listOf(
                            disabledBackgroundColor,
                            disabledBackgroundColor,
                        )
                    )
                }
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            Spacing.sm,
            Alignment.CenterHorizontally,
        ),
    ) {

        if (showIconBeforeText) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) contentColor else disabledContentColor,
                    modifier = Modifier.size(IconSize.medium),
                )
            } else if (painter != null) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = if (enabled) contentColor else disabledContentColor,
                    modifier = Modifier.size(IconSize.medium),
                )
            }
        }

        Text(
            text = text,
            style = textStyle,
            color = if (enabled) contentColor else disabledContentColor,
        )

        if (!showIconBeforeText) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) contentColor else disabledContentColor,
                    modifier = Modifier.size(IconSize.medium),
                )
            } else if (painter != null) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = if (enabled) contentColor else disabledContentColor,
                    modifier = Modifier.size(IconSize.medium),
                )
            }
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

/**
 * A decorative circle with a radial gradient that can be placed anywhere without
 * affecting the layout of other elements.
 */
@Composable
fun DecorativeBackgroundCircle(
    size: Dp,
    xOffset: Dp,
    yOffset: Dp,
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.10f),
) {
    Box(
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(0, 0) {
                    placeable.placeRelative(0, 0)
                }
            }
            .size(size)
            .offset(x = xOffset, y = yOffset)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color,
                        Color.Transparent
                    )
                )
            )
    )
}
