package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroCaption
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroChipBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroChipBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroGrid
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroMid
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryOrbBase
import com.tamin.taminhamrah.ui.theme.TaminHistoryOrbBody
import com.tamin.taminhamrah.ui.theme.TaminHistoryOrbGlow
import com.tamin.taminhamrah.ui.theme.TaminHistoryOrbHighlight
import kotlinx.collections.immutable.ImmutableList

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
    val heroBrush = remember {
        Brush.verticalGradient(
            0f to TaminHistoryHeroTop,
            HeroMidStop to TaminHistoryHeroMid,
            1f to TaminHistoryHeroBottom,
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = HeroCorner, bottomEnd = HeroCorner))
            .background(heroBrush)
            .heroGrid()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroPaddingH)
                .padding(top = HeroPaddingTop, bottom = HeroPaddingBottom),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
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
    val step = GridStep.toPx()
    val stroke = GridStroke.toPx()
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
        horizontalArrangement = Arrangement.spacedBy(ChipGap),
    ) {
        item(key = ALL_CHIP_KEY) {
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

private const val ALL_CHIP_KEY = "all"

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
    val textColor = when {
        selected -> TaminHistoryChipSelectedText
        enabled -> TaminHistoryChipText
        else -> TaminHistoryChipTextDisabled
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(ChipCorner))
            .then(
                if (selected) {
                    Modifier.background(selectedBrush)
                } else {
                    Modifier.background(TaminHistoryChipBg)
                },
            )
            .border(
                width = ChipBorderWidth,
                color = if (selected) TaminHistoryChipSelectedBorder else TaminHistoryChipBorder,
                shape = RoundedCornerShape(ChipCorner),
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = ChipPaddingH, vertical = ChipPaddingV),
        contentAlignment = Alignment.Center,
    ) {
        val style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        if (numeric) {
            NumericText(text = label, style = style, color = textColor)
        } else {
            Text(text = label, style = style, color = textColor, maxLines = 1)
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
        modifier = Modifier.size(OrbSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(HaloSize)
                .blur(HaloBlur)
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
    digits > 5 -> 22.sp
    digits > 4 -> 24.sp
    else -> 27.sp
}

@Composable
private fun DurationChip(chip: DurationChipPR) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(PillCorner))
            .background(TaminHistoryHeroChipBg)
            .border(
                ChipBorderWidth,
                TaminHistoryHeroChipBorder,
                RoundedCornerShape(PillCorner),
            )
            .padding(horizontal = Spacing.sm, vertical = DurationChipPaddingV),
        horizontalArrangement = Arrangement.spacedBy(DurationChipGap),
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

private val HeroCorner = 34.dp
private val HeroPaddingH = 18.dp
private val HeroPaddingTop = 8.dp
private val HeroPaddingBottom = 28.dp
private const val HeroMidStop = 0.58f
private val GridStep = 34.dp
private val GridStroke = 1.dp
private val ChipGap = 5.dp
private val ChipCorner = 13.dp
private val ChipPaddingH = 11.dp
private val ChipPaddingV = 8.dp
private val ChipBorderWidth = 1.dp
private val OrbSize = 104.dp
private val HaloSize = 150.dp
private val HaloBlur = 5.dp
private val PillCorner = 100.dp
private val DurationChipPaddingV = 6.dp
private val DurationChipGap = 5.dp
