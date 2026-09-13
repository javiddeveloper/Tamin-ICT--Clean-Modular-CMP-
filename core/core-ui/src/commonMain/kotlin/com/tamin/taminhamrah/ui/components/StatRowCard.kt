package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

private val HairlineWidth = 1.dp
private val DividerHeight = 32.dp

/**
 * One figure with its caption — the unit the header stat cards are built from.
 *
 * [highlight] marks the one figure the page is really about, which is how the design distinguishes
 * "12 years insured" from the months and days beside it.
 */
@Composable
fun StatColumn(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
) {
    val colors = LocalTaminColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = Spacing.xs),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (highlight) colors.blueText else colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
    }
}

/**
 * The card of figures a page hangs under its header — insured years/months/days, workshops, counts.
 *
 * Holds the surface, the hairline border and the dividers between columns so a screen only says
 * *which* figures it has; several already draw this exact card, and each copy was one more place
 * for the radius or the divider to drift.
 *
 * [content] is a [RowScope], so callers place [StatColumn]s with `Modifier.weight(1f)` and put a
 * [StatDivider] between them.
 */
@Composable
fun StatRowCard(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(HairlineWidth, colors.border, RoundedCornerShape(CornerRadius.lg))
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** The hairline between two figures in a [StatRowCard]. */
@Composable
fun StatDivider() {
    Box(
        modifier = Modifier
            .width(HairlineWidth)
            .height(DividerHeight)
            .background(LocalTaminColors.current.divider),
    )
}
