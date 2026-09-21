package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
import com.tamin.taminhamrah.feature.history.ui.model.YearChipPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.collapsingBottomPadding
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipSelectedBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipSelectedText
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipShimmer
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipText
import com.tamin.taminhamrah.ui.theme.TaminHistoryChipTextDisabled
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroChipBg
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.Res as CoreRes

/**
 * The redesigned hero header for «کلیه سوابق».
 *
 * Features:
 * - Curved gradient background (`#173D7E` -> `#1B4790` -> `#1F4FA3`) with rounded bottom corners.
 * - Top app bar with translucent navigation back button (36dp rounded square), center title, and search & download action buttons.
 * - 4-item year switcher: «همه», up to 2 recent years, and a «More» dropdown chip.
 */
@Composable
fun HistoryHero(
    title: String,
    scope: HistoryScope,
    yearChips: ImmutableList<YearChipPR>,
    allChipLabel: String,
    onScopeChange: (HistoryScope) -> Unit,
    onSearchClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onMoreClick: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    /** Read only inside the layout phase — see [HistoryTopArea]. */
    collapseProgress: () -> Float = { 0f },
    /** The career has not answered yet, so the year strip stands in for itself. */
    loading: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val heroBrush = colors.heroBrush
    val heroShape = remember {
        RoundedCornerShape(bottomStart = HistoryDimens.heroCorner, bottomEnd = HistoryDimens.heroCorner)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(heroShape)
            .background(heroBrush)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // The band under the chip strip closes as the card folds, so the head tightens
                // around the summary bar and lifts it with it. Left at its open depth, the bar
                // stays parked on the hero's bottom edge under a band of empty blue.
                .collapsingBottomPadding(
                    progress = collapseProgress,
                    expanded = HistoryDimens.heroPaddingBottom,
                    collapsed = HistoryDimens.heroPaddingBottomFolded,
                )
                .padding(horizontal = HistoryDimens.heroPaddingH)
                .padding(top = HistoryDimens.heroPaddingTop),
            verticalArrangement = Arrangement.spacedBy(HistoryDimens.heroRowGap),
        ) {
            // App bar row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Back button (right in RTL)
                HeroActionButton(
                    icon = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(CoreRes.string.action_back),
                    onClick = onBackClicked,
                )

                // Center screen title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = Color.White,
                )

                // Actions: Search + Download (left in RTL)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeroActionButton(
                        icon = Icons.Filled.Search,
                        contentDescription = "جستجوی سال",
                        onClick = onSearchClick,
                    )
                    HeroActionButton(
                        icon = vectorResource(CoreRes.drawable.ic_tamin_download),
                        contentDescription = "دانلود فایل",
                        onClick = onDownloadClick,
                    )
                }
            }

            // Year chips row (4 slots)
            YearSwitchRow(
                scope = scope,
                chips = yearChips,
                allLabel = allChipLabel,
                onScopeChange = onScopeChange,
                onMoreClick = onMoreClick,
                loading = loading,
            )
        }
    }
}

/**
 * Floating top area of the «کلیه سوابق» screen.
 *
 * Hosts the curved gradient [HistoryHero] and the collapsible [HistoryDurationCard]
 * riding up into the header as [collapseProgress] runs 0 → 1, matching the
 * treatment hub collapsing header interaction.
 */
@Composable
fun HistoryTopArea(
    title: String,
    scope: HistoryScope,
    yearChips: ImmutableList<YearChipPR>,
    allChipLabel: String,
    onScopeChange: (HistoryScope) -> Unit,
    onSearchClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onMoreClick: () -> Unit,
    onBackClicked: () -> Unit,
    durationCardModel: DurationCardPR,
    onStepOlder: () -> Unit,
    onStepNewer: () -> Unit,
    collapseProgress: () -> Float,
    modifier: Modifier = Modifier,
    hasYears: Boolean = true,
    /** The career has not answered yet, so the year strip stands in for itself. */
    loading: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HistoryHero(
            title = title,
            scope = scope,
            yearChips = yearChips,
            allChipLabel = allChipLabel,
            onScopeChange = onScopeChange,
            onSearchClick = onSearchClick,
            onDownloadClick = onDownloadClick,
            onMoreClick = onMoreClick,
            onBackClicked = onBackClicked,
            collapseProgress = collapseProgress,
            loading = loading,
        )

        if (hasYears) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    // Folds in place: the card keeps its slot under the chip strip and only gives
                    // up its own height, settling onto the hero's edge rather than climbing over
                    // the strip and onto the app-bar row.
                    .rideUpIntoHeader(
                        progress = collapseProgress,
                        expandedOverlap = HistoryDimens.durationCardOverlap,
                        collapsedOverlap = HistoryDimens.durationCardCollapsedOverlap,
                    ),
            ) {
                HistoryDurationCard(
                    model = durationCardModel,
                    onStepOlder = onStepOlder,
                    onStepNewer = onStepNewer,
                    collapseProgress = collapseProgress,
                )
            }
        }
    }
}

/** 4-item switcher row: «همه», recent years, and «More» chip. */
@Composable
private fun YearSwitchRow(
    scope: HistoryScope,
    chips: ImmutableList<YearChipPR>,
    allLabel: String,
    onScopeChange: (HistoryScope) -> Unit,
    onMoreClick: () -> Unit,
    loading: Boolean,
) {
    // The strip's own skeleton. Until the career arrives there are no years to offer, and the row
    // used to draw «همه» alone stretched across all four slots — which then snapped back to a
    // quarter of the width the moment the load answered.
    if (loading) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(HistoryDimens.yearChipGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(HistoryConstants.YEAR_CHIP_SLOTS) {
                ShimmerBlock(
                    modifier = Modifier.weight(1f).height(HistoryDimens.yearChipHeight),
                    cornerRadius = HistoryDimens.yearChipCorner,
                    colorBase = TaminHistoryChipBg,
                    colorHighlight = TaminHistoryChipShimmer,
                )
            }
        }
        return
    }

    val isAll = scope is HistoryScope.All
    val hasMore = chips.size > 3
    val railYears = remember(chips, hasMore) {
        chips.take(if (hasMore) 2 else 3)
    }
    val olderYears = remember(chips, railYears) {
        chips.drop(railYears.size)
    }
    val scopeYear = (scope as? HistoryScope.Year)?.year
    val scopeIsOlder = remember(scopeYear, olderYears) {
        scopeYear != null && olderYears.any { it.year == scopeYear }
    }

    val moreLabel = remember(scopeIsOlder, scopeYear, olderYears) {
        when {
            scopeIsOlder && scopeYear != null -> chips.firstOrNull { it.year == scopeYear }?.label ?: scopeYear
            olderYears.isNotEmpty() -> "${olderYears.last().label}–${olderYears.first().label}"
            else -> ""
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(HistoryDimens.yearChipGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // «همه»
        Box(modifier = Modifier.weight(1f)) {
            HeroChip(
                label = allLabel,
                selected = isAll,
                enabled = true,
                numeric = false,
                onClick = { onScopeChange(HistoryScope.All) },
            )
        }

        // Rail years
        railYears.forEach { chip ->
            Box(modifier = Modifier.weight(1f)) {
                HeroChip(
                    label = chip.label,
                    selected = scope is HistoryScope.Year && scope.year == chip.year,
                    enabled = chip.hasHistory,
                    numeric = true,
                    onClick = { onScopeChange(HistoryScope.Year(chip.year)) },
                )
            }
        }

        // More chip (if career spans more years)
        if (hasMore) {
            Box(modifier = Modifier.weight(1f)) {
                HeroDropdownChip(
                    label = moreLabel,
                    selected = scopeIsOlder,
                    onClick = onMoreClick,
                )
            }
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
    val shape = remember { RoundedCornerShape(HistoryDimens.yearChipCorner) }
    val bg = if (selected) Color.White else TaminHistoryChipBg
    val border = if (selected) TaminHistoryChipSelectedBorder else TaminHistoryChipBorder
    val textColor = when {
        selected -> TaminHistoryChipSelectedText
        enabled -> TaminHistoryChipText
        else -> TaminHistoryChipTextDisabled
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HistoryDimens.yearChipHeight)
            .clip(shape)
            .background(bg)
            .border(HistoryDimens.hairline, border, shape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (numeric) {
            NumericText(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = textColor,
            )
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = textColor,
            )
        }
    }
}

@Composable
private fun HeroDropdownChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = remember { RoundedCornerShape(HistoryDimens.yearChipCorner) }
    val bg = if (selected) Color.White else TaminHistoryChipBg
    val border = if (selected) TaminHistoryChipSelectedBorder else TaminHistoryChipBorder
    val textColor = if (selected) TaminHistoryChipSelectedText else TaminHistoryChipText

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HistoryDimens.yearChipHeight)
            .clip(shape)
            .background(bg)
            .border(HistoryDimens.hairline, border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NumericText(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
            ),
            color = textColor,
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(13.dp),
        )
    }
}

@Composable
fun HeroActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = remember { RoundedCornerShape(HistoryDimens.heroActionCorner) }
    Box(
        modifier = modifier
            .size(HistoryDimens.heroActionSize)
            .clip(shape)
            .background(TaminHistoryHeroChipBg)
            .border(HistoryDimens.hairline, TaminHistoryChipBorder, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(HistoryDimens.heroActionIconSize),
        )
    }
}
