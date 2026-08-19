package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.model.DurationChipPR
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
import com.tamin.taminhamrah.feature.history.ui.model.YearChipPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipSelectedBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipSelectedEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipSelectedStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipSelectedText
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipText
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipTextDisabled
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroCaption
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroChipBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroChipBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroGrid
import com.tamin.taminhamrah.ui.theme.TaminHistoryOrbBase
import com.tamin.taminhamrah.ui.theme.TaminHistoryOrbBody
import com.tamin.taminhamrah.ui.theme.TaminHistoryOrbGlow
import com.tamin.taminhamrah.ui.theme.TaminHistoryOrbHighlight
import kotlinx.collections.immutable.ImmutableList
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.tamin.taminhamrah.ui.theme.Duration

/**
 * The page's dark head: who is looking, how long they were insured, and which years they can pick.
 *
 * Its own composable rather than a lambda in the screen, so the year list underneath never
 * recomposes when a chip is tapped.
 */
@Composable
fun HistoryHero(
    title: String,
    scope: HistoryScope,
    yearChips: ImmutableList<YearChipPR>,
    allChipLabel: String,
    orbDays: String,
    orbDaysLabel: String,
    caption: String,
    durations: ImmutableList<DurationChipPR>,
    onScopeChange: (HistoryScope) -> Unit,
    navigationIcon: @Composable () -> Unit,
    action: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Both themes' heads live in the theme: navy running down in light, the design's teal→blue on
    // the diagonal in dark.
    val heroBrush = LocalTaminColors.current.heroBrush

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = HistoryDimens.heroCorner, bottomEnd = HistoryDimens.heroCorner))
            .background(heroBrush)
            .heroGrid()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HistoryDimens.heroPaddingH)
                .padding(top = HistoryDimens.heroPaddingTop, bottom = HistoryDimens.heroPaddingBottom),
            // The design's own row gap. A uniform theme spacing left the head visibly taller than
            // the mock, most obviously with one chip and a zero orb.
            verticalArrangement = Arrangement.spacedBy(HistoryDimens.heroRowGap),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                navigationIcon()
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
                action()
            }

            YearChipStrip(
                scope = scope,
                chips = yearChips,
                allLabel = allChipLabel,
                onScopeChange = onScopeChange,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DayOrb(days = orbDays, label = orbDaysLabel)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Text(
                        text = caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = TaminHistoryHeroCaption,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        durations.forEach { chip -> DurationChip(chip) }
                    }
                }
            }
        }
    }
}

/**
 * The faint grid ruled over the hero.
 *
 * Drawn rather than composed: it is one repeating pattern of hairlines, and a grid of Boxes for it
 * would be a hundred layout nodes that never change.
 */
private fun Modifier.heroGrid(): Modifier = drawBehind {
    val step = HistoryDimens.gridStep.toPx()
    val stroke = HistoryDimens.gridStroke.toPx()
    var x = 0f
    while (x < size.width) {
        drawRect(TaminHistoryHeroGrid, Offset(x, 0f), Size(stroke, size.height))
        x += step
    }
    var y = 0f
    while (y < size.height) {
        drawRect(TaminHistoryHeroGrid, Offset(0f, y), Size(size.width, stroke))
        y += step
    }
}

/**
 * Every year in the span, newest first, with «همه» pinned before them.
 *
 * Lazy, because a long career is forty chips and only six are ever on screen. A year the service
 * reported nothing for is drawn dimmed and does not answer a tap — the design shows the gaps rather
 * than hiding them, and the note under the chart counts them.
 */
@Composable
private fun YearChipStrip(
    scope: HistoryScope,
    chips: ImmutableList<YearChipPR>,
    allLabel: String,
    onScopeChange: (HistoryScope) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HistoryDimens.chipGap),
    ) {
        item(key = HistoryConstants.ALL_CHIP_KEY) {
            HeroChip(
                label = allLabel,
                selected = scope is HistoryScope.All,
                enabled = true,
                numeric = false,
                onClick = { onScopeChange(HistoryScope.All) },
            )
        }
        items(chips, key = { it.year }) { chip ->
            HeroChip(
                label = chip.label,
                selected = scope is HistoryScope.Year && scope.year == chip.year,
                enabled = chip.hasHistory,
                numeric = true,
                onClick = { onScopeChange(HistoryScope.Year(chip.year)) },
            )
        }
    }
}

@Composable
private fun HeroChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    numeric: Boolean,
    onClick: () -> Unit,
) {
    val selectedBrush = remember {
        Brush.linearGradient(listOf(TaminHistoryChipSelectedStart, TaminHistoryChipSelectedEnd))
    }
    val shape = remember { RoundedCornerShape(HistoryDimens.chipCorner) }

    /*
     * Every part of the transition is held as State and read inside `drawBehind` or a color
     * lambda, never during composition.
     *
     * These chips are the most-tapped thing on the page: a selection that recomposed the strip
     * would recompose every chip in it, and the strip is as long as the person's career.
     */
    val selection = animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(Duration.fast),
        label = "chipSelection",
    )
    val border = animateColorAsState(
        targetValue = if (selected) {
            TaminHistoryChipSelectedBorder
        } else {
            TaminHistoryChipBorder
        },
        animationSpec = tween(Duration.fast),
        label = "chipBorder",
    )
    val textColor = animateColorAsState(
        targetValue = when {
            selected -> TaminHistoryChipSelectedText
            enabled -> TaminHistoryChipText
            else -> TaminHistoryChipTextDisabled
        },
        animationSpec = tween(Duration.fast),
        label = "chipText",
    )

    Box(
        modifier = Modifier
            .clip(shape)
            .drawBehind {
                // The unselected glass is always there; the selected fill washes over it.
                drawRect(TaminHistoryChipBg)
                drawRect(brush = selectedBrush, alpha = selection.value)
            }
            .border(HistoryDimens.hairline, border.value, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(
                horizontal = HistoryDimens.chipPaddingH,
                vertical = HistoryDimens.chipPaddingV,
            ),
        contentAlignment = Alignment.Center,
    ) {
        val style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        if (numeric) {
            NumericText(text = label, style = style, color = textColor.value)
        } else {
            Text(text = label, style = style, color = textColor.value, maxLines = 1)
        }
    }
}

/** The day count, as the design's lit sphere. */
@Composable
private fun DayOrb(days: String, label: String) {
    val orbBrush = remember {
        Brush.radialGradient(
            colors = listOf(TaminHistoryOrbHighlight, TaminHistoryOrbBody, TaminHistoryOrbBase),
            center = Offset.Unspecified,
        )
    }
    val haloBrush = remember {
        Brush.radialGradient(listOf(TaminHistoryOrbGlow, Color.Transparent))
    }

    Box(
        modifier = Modifier.size(HistoryDimens.orbSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(HistoryDimens.haloSize)
                .blur(HistoryDimens.haloBlur)
                .background(haloBrush, CircleShape),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(orbBrush),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NumericText(
                    text = days,
                    // Shrinks as the number grows, so a five-digit career still fits the sphere.
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = orbFontSize(days.length),
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.White,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TaminHistoryHeroCaption,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun orbFontSize(digits: Int) = when {
    digits > 5 -> HistoryDimens.orbTextSmall
    digits > 4 -> HistoryDimens.orbTextMedium
    else -> HistoryDimens.orbTextLarge
}

@Composable
private fun DurationChip(chip: DurationChipPR) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(HistoryDimens.pillCorner))
            .background(TaminHistoryHeroChipBg)
            .border(
                HistoryDimens.hairline,
                TaminHistoryHeroChipBorder,
                RoundedCornerShape(HistoryDimens.pillCorner),
            )
            .padding(horizontal = Spacing.sm, vertical = HistoryDimens.durationChipPaddingV),
        horizontalArrangement = Arrangement.spacedBy(HistoryDimens.durationChipGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NumericText(
            text = chip.value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
        Text(
            text = chip.label,
            style = MaterialTheme.typography.labelSmall,
            color = TaminHistoryHeroCaption,
        )
    }
}

