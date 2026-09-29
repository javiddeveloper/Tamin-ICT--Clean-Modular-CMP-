package com.tamin.taminhamrah.feature.history.ui.jobinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.history.ui.jobinfo.components.JobInfoCard
import com.tamin.taminhamrah.feature.history.ui.jobinfo.components.JobInfoStatsCard
import com.tamin.taminhamrah.model.history.HistoryJobInfoItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.system.copyToClipboard
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.straddlePreviousSibling
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.feature.history.Res as HistoryRes
import taminx.feature.history.history_job_info_empty_state
import taminx.feature.history.history_job_info_list_header
import taminx.feature.history.history_job_info_subtitle
import taminx.feature.history.history_job_info_title

/** How far [JobInfoStatsCard] rides up into the header's reserved bottom space. */
private val StatsCardOverlap = Spacing.xxxl

@Composable
fun HistoryJobInfoScreen(
    onBackClicked: () -> Unit,
    viewModel: HistoryJobInfoViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    HistoryJobInfoEvents(
        events = viewModel.events,
        toaster = toaster
    )

    HistoryJobInfoContent(
        uiState = uiState,
        onBackClicked = onBackClicked
    )
}

@Composable
fun HistoryJobInfoEvents(
    events: Flow<HistoryJobInfoEvent>,
    toaster: ToasterState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is HistoryJobInfoEvent.ShowToast -> {
                toaster.error(event.message)
            }
        }
    }
}

@Composable
fun HistoryJobInfoContent(
    uiState: HistoryJobInfoUiState,
    onBackClicked: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    val workshopCount = uiState.jobInfos.map { it.rwshId }.distinct().size
    val firstEmploymentYear = uiState.jobInfos
        .mapNotNull { it.startDate.takeIf { s -> s.length >= 4 }?.take(4) }
        .minOrNull() ?: ""
    val jobTitleCount = uiState.jobInfos.size

    // Folds the header's ring icon/subtitle from the list's own drag, snapping on release --
    // same TopArea pattern as ActiveRelationHeader / CalculateWagePensionHeader. JobInfoStatsCard
    // is never wrapped in a topArea behavior, so it stays fully shown and rides up to straddle
    // the header's seam regardless of fold progress. See docs/vault/TopArea-System.md.
    val topArea = rememberMeasuredTopAreaState { topAreaState ->
        HistoryJobInfoTopArea(
            workshopCount = workshopCount,
            firstEmploymentYear = firstEmploymentYear,
            jobTitleCount = jobTitleCount,
            topAreaState = topAreaState,
            onBackClicked = onBackClicked,
        )
    }
    val lazyListState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .driveTopArea(topArea, lazyListState),
            contentPadding = topAreaContentPadding(
                state = topArea,
                rest = PaddingValues(horizontal = Spacing.page, vertical = Spacing.md),
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            // The same give the rest of the app scrolls with.
            overscrollEffect = rememberJellyOverscroll(),
        ) {
            if (uiState.jobInfos.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(HistoryRes.string.history_job_info_list_header),
                        style = MaterialTheme.typography.titleMedium,
                        color = taminColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.md)
                    )
                }
            }

            if (uiState.jobInfos.isEmpty() && !uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.xxl),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(HistoryRes.string.history_job_info_empty_state),
                            color = taminColors.textMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            items(uiState.jobInfos, key = { it.id }) { jobInfo ->
                JobInfoCard(
                    jobInfo = jobInfo,
                    onCopy = { text -> copyToClipboard(text) }
                )
            }

            item {
                Spacer(Modifier.height(Spacing.xxl))
            }
        }

        // The floating top area sits on top so the list passes underneath it as it scrolls away.
        HistoryJobInfoTopArea(
            workshopCount = workshopCount,
            firstEmploymentYear = firstEmploymentYear,
            jobTitleCount = jobTitleCount,
            topAreaState = topArea,
            onBackClicked = onBackClicked,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )

        if (uiState.isLoading) {
            LoadingStateOverlay()
        }
    }
}

/**
 * The screen's floating top area: the folding gradient hero (back button, ring icon, subtitle)
 * plus [JobInfoStatsCard], which stays fully shown and pinned beneath it regardless of scroll --
 * riding up by [StatsCardOverlap] to straddle the header's seam, same shape as
 * `CalculateWagePensionScreen`'s `CalculateWagePensionTopArea`.
 */
@Composable
private fun HistoryJobInfoTopArea(
    workshopCount: Int,
    firstEmploymentYear: String,
    jobTitleCount: Int,
    topAreaState: TopAreaState,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = stringResource(HistoryRes.string.history_job_info_title),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(CoreRes.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(CoreRes.string.action_back),
                    onClick = onBackClicked,
                    bordered = true
                )
            },
            background = profileGradientBrush,
            // Extra blue space below the bar's own content for the stats card to ride up into --
            // same idea as CalculateWagePensionHeader's bottomPadding.
            bottomPadding = Spacing.lg + StatsCardOverlap,
        ) {
            // Only the expanded-state furniture (ring icon + subtitle) folds away; the bar's own
            // title row stays put, same split as ActiveRelationHeader.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .topAreaHide(topAreaState),
            ) {
                DecorativeBackgroundCircle(
                    size = 190.dp,
                    xOffset = 450.dp,
                    yOffset = (-150).dp
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AnimatedRingHeaderIcon(
                        icon = Icons.Outlined.Work,
                        animated = !topAreaState.isMeasureProbe,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(HistoryRes.string.history_job_info_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        JobInfoStatsCard(
            workshopCount = workshopCount,
            firstEmploymentYear = firstEmploymentYear,
            jobTitleCount = jobTitleCount,
            // Rides up into the header's reserved bottom space rather than sitting right after
            // it, so the card visually straddles the header's seam whether the header is
            // expanded or folded.
            modifier = Modifier
                .straddlePreviousSibling(StatsCardOverlap)
                .padding(horizontal = Spacing.page)
                .coloredShadow(
                    color = taminColors.shadowSubtle,
                    borderRadius = CornerRadius.lg,
                    blurRadius = Elevation.lg,
                    offsetY = Spacing.xs
                )
        )
        Spacer(Modifier.height(Spacing.lg))
    }
}


@PreviewRtlTheme
@Composable
private fun HistoryJobInfoScreenPreview() {
    PreviewRtlThemeContent {
        HistoryJobInfoContent(
            uiState = HistoryJobInfoUiState(
                jobInfos = listOf(
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 1,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0081631829",
                        rwshName = "شرکت صنایع دما بخار مشهد",
                        brhcode = "6400",
                        id = 2,
                        jobDesc = "کارمند اداری ۱",
                        startDate = "139810",
                        rwshId = "6393610019"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 3,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 4,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 5,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 6,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 7,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 8,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                )
            ),
            onBackClicked = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryJobInfoScreenPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        HistoryJobInfoContent(
            uiState = HistoryJobInfoUiState(
                jobInfos = listOf(
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 1,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0081631829",
                        rwshName = "شرکت صنایع دما بخار مشهد",
                        brhcode = "6400",
                        id = 2,
                        jobDesc = "کارمند اداری ۱",
                        startDate = "139810",
                        rwshId = "6393610019"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 3,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 4,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 5,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 6,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 7,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                    HistoryJobInfoItemPR(
                        risuid = "0016318941",
                        rwshName = "درمانگاه دندانپزشکی دکتر محمدجعفری جبلی",
                        brhcode = "6400",
                        id = 8,
                        jobDesc = "کارفرما",
                        startDate = "140009",
                        rwshId = "9028222442"
                    ),
                )
            ),
            onBackClicked = {}
        )
    }
}
