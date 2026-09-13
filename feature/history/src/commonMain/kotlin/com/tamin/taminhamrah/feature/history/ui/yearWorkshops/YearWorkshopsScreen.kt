package com.tamin.taminhamrah.feature.history.ui.yearWorkshops

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.HistoryToggleOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.history.ui.HistoryConstants
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.HistoryViewModel
import com.tamin.taminhamrah.feature.history.ui.components.NoWorkshops
import com.tamin.taminhamrah.feature.history.ui.components.SeasonCard
import com.tamin.taminhamrah.feature.history.ui.components.WorkshopCard
import com.tamin.taminhamrah.feature.history.ui.model.YearDetailPR
import com.tamin.taminhamrah.feature.history.ui.model.detailWith
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.collapseHeightAway
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroCaption
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroChipBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroChipBorder
import com.tamin.taminhamrah.ui.theme.TaminHistoryHeroGlowCore
import com.tamin.taminhamrah.ui.theme.TaminHistoryPartialYearBg
import com.tamin.taminhamrah.ui.theme.TaminHistoryPartialYearText
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.action_back
import taminx.core.core_ui.history_months_breakdown
import taminx.core.core_ui.history_workshops_caption
import taminx.core.core_ui.history_workshops_title
import taminx.feature.history.history_combined_no_workshop
import taminx.feature.history.history_combined_wage_unavailable
import taminx.feature.history.history_combined_year_days
import taminx.feature.history.history_scheme_construction
import taminx.feature.history.history_scheme_optional
import taminx.feature.history.history_year_full
import taminx.feature.history.history_year_incomplete
import taminx.core.core_ui.Res as CoreRes
import taminx.feature.history.Res as HistoryRes

/**
 * «کارگاه‌های سال» — one year's employers, each with its branch, its workshop number and its
 * month-by-month wages, and the year's own twelve months grouped by season underneath.
 *
 * A destination rather than a sheet, which is what the design makes it and what the rest of the app
 * does with a drill-down: system back leaves it, the year it is showing is in the route, and the
 * page behind it keeps the scroll position and the chart the person left.
 */
@Composable
fun YearWorkshopsScreen(
    year: String,
    viewModel: HistoryViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Named here so the domain layer never learns what this screen calls a scheme with no workshop
    // of its own.
    val optionalScheme = stringResource(HistoryRes.string.history_scheme_optional)
    val constructionScheme = stringResource(HistoryRes.string.history_scheme_construction)

    val detail = remember(year, uiState.years, uiState.wageByYear, optionalScheme, constructionScheme) {
        uiState.years.firstOrNull { it.year == year }?.detailWith(
            rows = uiState.wageByYear[year] ?: NoWorkshops,
            optionalSchemeName = optionalScheme,
            constructionSchemeName = constructionScheme,
        )
    }

    YearWorkshopsContent(
        year = year,
        detail = detail,
        wagesUnavailable = uiState.wagesUnavailable,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

/** Stateless, so the whole page is previewable and testable without a ViewModel. */
@Composable
fun YearWorkshopsContent(
    year: String,
    detail: YearDetailPR?,
    wagesUnavailable: Boolean,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    // The head stays put and folds under the drag rather than scrolling away with the list — the
    // same state, spacer and floating-header arrangement every other page in the app is built from.
    val collapse = rememberCollapsingHeaderState(HistoryDimens.workshopsHeroCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }

    Scaffold(modifier = modifier, containerColor = colors.bgPage) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(collapse.nestedScrollConnection),
                contentPadding = PaddingValues(bottom = Spacing.xxl),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                // The same give the rest of the app scrolls with.
                overscrollEffect = rememberJellyOverscroll(),
            ) {
                // Stands in for the floating head, which is measured rather than fixed.
                item(key = HistoryConstants.HERO_KEY) {
                    Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
                }

                when {
                    // The wage call failed, so «ثبت نشده» would be a claim about data that never came.
                    wagesUnavailable -> item(key = HistoryConstants.WAGES_UNAVAILABLE_KEY) {
                        YearWorkshopsNote(stringResource(HistoryRes.string.history_combined_wage_unavailable))
                    }

                    detail == null || detail.workshops.isEmpty() ->
                        item(key = HistoryConstants.NO_WORKSHOP_KEY) {
                            YearWorkshopsNote(stringResource(HistoryRes.string.history_combined_no_workshop))
                        }

                    else -> items(detail.workshops, key = { it.id }) { workshop ->
                        Box(modifier = Modifier.padding(horizontal = HistoryDimens.sidePadding)) {
                            WorkshopCard(workshop = workshop)
                        }
                    }
                }

                if (detail != null) {
                    item(key = HistoryConstants.SEASONS_TITLE_KEY) {
                        Text(
                            text = stringResource(CoreRes.string.history_months_breakdown),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary,
                            modifier = Modifier
                                .padding(horizontal = HistoryDimens.sidePadding)
                                .padding(top = Spacing.sm),
                        )
                    }

                    // Two cards per row — the design's grid, without nesting a grid inside a list.
                    items(HistoryConstants.SEASONS / 2, key = { "season_row_$it" }) { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = HistoryDimens.sidePadding),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        ) {
                            SeasonCard(detail = detail, season = row * 2, modifier = Modifier.weight(1f))
                            SeasonCard(detail = detail, season = row * 2 + 1, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Floats over the list so the content passes underneath it as it scrolls away.
            YearWorkshopsHero(
                year = year,
                totalDays = detail?.totalDays ?: 0,
                onBackClicked = onBackClicked,
                collapseProgress = collapse.progressProvider,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .onSizeChanged { headerHeightPx = it.height },
            )
        }
    }
}

/**
 * The screen's own dark head.
 *
 * Its own rather than [com.tamin.taminhamrah.feature.history.ui.components.HistoryHero]: that one
 * carries an orb, a chip strip and a duration row, none of which this page has. What the two share
 * is the palette, which they take from the same tokens.
 */
@Composable
private fun YearWorkshopsHero(
    year: String,
    totalDays: Int,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    /** Read only inside the layout phase, so a frame of the fold costs no recomposition. */
    collapseProgress: () -> Float = { 0f },
) {
    val colors = LocalTaminColors.current
    val heroBrush = colors.heroBrush
    val complete = totalDays >= HistoryConstants.FULL_YEAR_DAYS

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = HistoryDimens.heroCorner,
                    bottomEnd = HistoryDimens.heroCorner,
                ),
            )
            .background(heroBrush)
            .heroGlow()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HistoryDimens.heroPaddingH)
                .padding(
                    top = HistoryDimens.heroPaddingTop,
                    bottom = HistoryDimens.heroPaddingBottomCollapsed,
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminTopAppBarButton(
                    icon = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(CoreRes.string.action_back),
                    onClick = onBackClicked,
                    bordered = true,
                )
                Text(
                    text = stringResource(
                        CoreRes.string.history_workshops_title,
                        year.toPersianDigits(),
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                // Balances the back button so the title sits centred, which is what the design
                // draws — an empty box of the same size, not a second control.
                Spacer(modifier = Modifier.size(HistoryDimens.heroButtonSize))
            }

            // The caption and its two pills belong to the open state: they give their height back
            // as the head folds, so the title row lands as a slim bar rather than over a gap.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // The gap above it is the row's own padding rather than the column's spacing,
                    // so it goes with the row instead of leaving a band the fold cannot close.
                    .collapseHeightAway(collapseProgress)
                    .padding(top = HistoryDimens.heroRowGap),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(CoreRes.string.history_workshops_caption),
                    style = MaterialTheme.typography.labelSmall,
                    color = TaminHistoryHeroCaption,
                    modifier = Modifier.weight(1f),
                )
                HeroPill(
                    text = stringResource(
                        HistoryRes.string.history_combined_year_days,
                        totalDays.toString().toPersianDigits(),
                    ),
                    textColor = Color.White,
                    background = TaminHistoryHeroChipBg,
                    borderColor = TaminHistoryHeroChipBorder,
                )
                HeroPill(
                    text = stringResource(
                        if (complete) {
                            HistoryRes.string.history_year_full
                        } else {
                            HistoryRes.string.history_year_incomplete
                        },
                    ),
                    textColor = if (complete) colors.blueText else TaminHistoryPartialYearText,
                    background = if (complete) colors.blueBg else TaminHistoryPartialYearBg,
                    borderColor = Color.Transparent,
                )
            }
        }
    }
}

/**
 * The hero's corner bloom.
 *
 * Drawn rather than composed: it is one radial wash that never changes, and a Box holding it would
 * be a layout node that exists only to be painted.
 */
private fun Modifier.heroGlow(): Modifier = drawBehind {
    val radius = HistoryDimens.heroGlowSize.toPx()
    // Off the physical top-left corner, which is where the design puts it — `left:-50px`, not a
    // start-relative offset, so it stays on the left on an RTL page.
    val centre = Offset(-radius * GLOW_INSET, -radius * GLOW_INSET)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(TaminHistoryHeroGlowCore, Color.Transparent),
            center = centre,
            radius = radius,
        ),
        radius = radius,
        center = centre,
    )
}

@Composable
private fun HeroPill(
    text: String,
    textColor: Color,
    background: Color,
    borderColor: Color,
) {
    val shape = RoundedCornerShape(HistoryDimens.pillCorner)
    Box(
        modifier = Modifier
            .clip(shape)
            .background(background)
            .then(
                if (borderColor == Color.Transparent) {
                    Modifier
                } else {
                    Modifier.border(HistoryDimens.hairline, borderColor, shape)
                },
            )
            .padding(horizontal = Spacing.sm, vertical = HistoryDimens.pillPaddingV),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
        )
    }
}

/** Why there is nothing to list, said in the page rather than left blank. */
@Composable
private fun YearWorkshopsNote(message: String) {
    EmptyStateMessage(
        icon = Icons.Outlined.HistoryToggleOff,
        title = message,
        showIconTile = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HistoryDimens.sidePadding)
            .padding(top = Spacing.xl),
    )
}

/** How far the bloom's center sits outside the hero's corner, as a fraction of its radius. */
private const val GLOW_INSET = 0.35f
