package com.tamin.taminhamrah.feature.treatment.ui.components

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
// The platform local, deliberately: TaminHamrahTheme declares a same-named local of
// its own that no Compose layout reads, so importing the theme's one would no-op.
import androidx.compose.ui.platform.LocalLayoutDirection

/**
 * Stateless building blocks shared by the treatment hub and the medical-records
 * timeline. Everything here takes primitives and lambdas only, so each piece can be
 * previewed and snapshot-tested without a ViewModel.
 */

/**
 * Numeric text. Amounts, national IDs and tracking codes are always laid out
 * left-to-right, matching the `dir="ltr"` the design puts on every number even inside
 * the otherwise right-to-left page.
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
 * Rounded status badge — "تکمیل نشده", a record's category, a lab result's range.
 * Colors are passed in as a container/content pair so callers can pick the
 * green/blue/orange sets straight off [com.tamin.taminhamrah.ui.theme.TaminColors].
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
            .padding(horizontal = Spacing.md, vertical = 5.dp),
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
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
        )
    }
}

/**
 * Money tile used by the cost summary card and the timeline totals bar: a small
 * caption over an emphasized amount, on a tinted rounded background.
 */
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

/** Muted caption above a group of cards — "دسترسی سریع", or a timeline date group. */
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

/**
 * Square-ish gradient container holding a single icon — the leading glyph on the
 * quick-access, navigation and category cards.
 */
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
 * The teal hero at the top of every treatment sub-screen: gradient background with a
 * rounded bottom edge, a centred title, an optional back affordance and trailing
 * action, plus a [content] slot for the filter row the timeline puts underneath.
 */
@Composable
fun TreatmentHeader(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = LocalTaminColors.current.medicalGradient,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.sheet,
                    bottomEnd = CornerRadius.sheet,
                ),
            )
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeaderSlot { navigationIcon?.invoke() }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            HeaderSlot { action?.invoke() }
        }
        content()
    }
}

/** Fixed-width end cap so the header title stays optically centred with 0, 1 or 2 actions. */
@Composable
private fun HeaderSlot(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

/**
 * The white card that treatment content sits on: surface fill, hairline border and the
 * design's card radius. Every card in the flow repeats this chain, so it lives here once.
 */
@Composable
fun Modifier.treatmentSurface(cornerRadius: Dp = CornerRadius.card): Modifier {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(cornerRadius)
    return clip(shape)
        .background(colors.bgSurface)
        .border(1.dp, colors.border, shape)
}

/** Small muted caption above a block of body text — "دستور مصرف", "تشخیص", "یادداشت پزشک". */
@Composable
fun LabeledBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
        )
    }
}

/**
 * Label-on-one-side, value-on-the-other row used by the record summary and the cost
 * breakdown. [numeric] routes the value through [NumericText] so codes and amounts stay
 * left-to-right.
 */
@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = LocalTaminColors.current.textPrimary,
    valueStyle: TextStyle = MaterialTheme.typography.titleSmall,
    numeric: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = LocalTaminColors.current.textMuted,
        )
        if (numeric) {
            NumericText(text = value, style = valueStyle, color = valueColor)
        } else {
            Text(text = value, style = valueStyle, color = valueColor)
        }
    }
}

/** Hairline rule separating rows inside a card. */
@Composable
fun TreatmentDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(LocalTaminColors.current.divider),
    )
}

/** Full-width gradient call to action — the download button, the apply-search button. */
@Composable
fun TreatmentPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(CornerRadius.iconTile))
            .background(LocalTaminColors.current.medicalGradient)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.medium),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
        )
    }
}

/**
 * Translucent bar pinned to the bottom of a treatment screen, holding either the cost
 * totals or the screen's primary action.
 */
@Composable
fun TreatmentBottomBar(
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
fun TreatmentEmptyState(
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
