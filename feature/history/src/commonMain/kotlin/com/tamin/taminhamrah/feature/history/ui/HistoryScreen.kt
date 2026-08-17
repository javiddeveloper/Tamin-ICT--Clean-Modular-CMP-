package com.tamin.taminhamrah.feature.history.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.history.ui.components.CareerTotalCard
import com.tamin.taminhamrah.feature.history.ui.components.NoWorkshops
import com.tamin.taminhamrah.feature.history.ui.components.YearDetailSheet
import com.tamin.taminhamrah.feature.history.ui.components.YearHistoryCard
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.feature.history.ui.model.CareerTotalPR
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.ShimmerRows
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.motion.ScrollMotionState
import com.tamin.taminhamrah.ui.motion.motionFade
import com.tamin.taminhamrah.ui.motion.motionParallax
import com.tamin.taminhamrah.ui.motion.motionScale
import com.tamin.taminhamrah.ui.motion.rememberScrollMotionState
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_back
import taminx.feature.history.Res as HistoryRes
import taminx.feature.history.history_combined_empty
import taminx.feature.history.history_combined_empty_title
import taminx.feature.history.history_combined_list_header
import taminx.feature.history.history_combined_list_hint
import taminx.feature.history.history_combined_subtitle
import taminx.feature.history.history_combined_title
import androidx.compose.ui.unit.lerp as dpLerp

private val HeaderIconOffset = (-30).dp
private val MaxMotionDistance = 120.dp
private val CollapsedHeaderSpacing = 25.dp
private val DecorCircleSize = 190.dp
private val DecorCircleX = 450.dp
private val DecorCircleY = (-150).dp

/** What a year card measures, so the skeleton stands in for one without the list jumping. */
private val YearCardHeight = 76.dp

@Composable
fun HistoryScreen(
    onBackClicked: () -> Unit = {},
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val motionState = rememberScrollMotionState(maxMotionDistance = MaxMotionDistance)
    val toaster = LocalToaster.current

    LaunchedEffect(Unit) { viewModel.sendIntent(HistoryIntent.Load) }

    LaunchedEffect(motionState, lazyListState) {
        motionState.observeLazyListState(lazyListState)
    }

    HandleHistoryEvents(events = viewModel.events, toaster = toaster)

    HistoryContent(
        uiState = uiState,
        lazyListState = lazyListState,
        motionState = motionState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
    )
}

@Composable
fun HandleHistoryEvents(
    events: Flow<HistoryEvent>,
    toaster: ToasterState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            // Resolved here, where a composition exists to resolve it in.
            is HistoryEvent.ShowToast -> toaster.error(getString(event.message))
        }
    }
}

@Composable
fun HistoryContent(
    uiState: HistoryUiState,
    lazyListState: LazyListState,
    motionState: ScrollMotionState,
    onIntent: (HistoryIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val headerProgress = motionState.progress
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
    // Held across the sheet opening and closing, so a year already on screen does not fade in again.
    val staggerState = rememberStaggeredEntranceState(uiState.years.size)

    Scaffold(
        modifier = modifier,
        containerColor = taminColors.bgPage,
        topBar = {
            HistoryHeader(
                headerProgress = headerProgress,
                motionState = motionState,
                background = profileGradientBrush,
                careerTotal = uiState.careerTotal,
                onBackClicked = onBackClicked,
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.page, vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                overscrollEffect = rememberJellyOverscroll(),
            ) {
                if (uiState.years.isNotEmpty()) {
                    item(key = LIST_HEADER_KEY) { YearListHeader() }
                }

                itemsIndexed(uiState.years, key = { _, year -> year.year }) { index, year ->
                    YearHistoryCard(
                        year = year,
                        // The year itself travels to the sheet, never its position in the list.
                        onClick = { onIntent(HistoryIntent.SelectYear(year)) },
                        modifier = Modifier.staggeredItemEntrance(
                            index = index,
                            key = year.year,
                            state = staggerState,
                        ),
                    )
                }

                // Only once a load has returned: an empty list before that means "not known yet",
                // and saying "you have no history" then would be a lie the next frame corrects.
                if (uiState.years.isEmpty() && uiState.hasLoadedOnce && !uiState.isLoading) {
                    item(key = EMPTY_STATE_KEY) {
                        EmptyStateMessage(
                            icon = Icons.Outlined.History,
                            title = stringResource(HistoryRes.string.history_combined_empty_title),
                            subtitle = stringResource(HistoryRes.string.history_combined_empty),
                            showIconTile = true,
                            modifier = Modifier.fillMaxWidth().padding(top = Spacing.xl),
                        )
                    }
                }

                if (uiState.isLoading && uiState.years.isEmpty()) {
                    item(key = SKELETON_KEY) { ShimmerRows(rowHeight = YearCardHeight) }
                }
            }
        }
    }

    // Outside the list: a failure is the only thing worth attending to while it is up, and a
    // dialog cannot live in a LazyColumn item.
    ErrorStateView(
        message = uiState.error,
        onDismiss = onBackClicked,
        onRetry = { onIntent(HistoryIntent.Load) },
    )

    uiState.selectedYear?.let { year ->
        YearDetailSheet(
            year = year,
            workshops = uiState.wageByYear[year.year] ?: NoWorkshops,
            onDismiss = { onIntent(HistoryIntent.DismissYearDetail) },
        )
    }
}

private const val LIST_HEADER_KEY = "header"
private const val EMPTY_STATE_KEY = "empty"
private const val SKELETON_KEY = "skeleton"

/**
 * The page header, built the way the job-titles page builds its own so the two read as one feature.
 *
 * Its own composable rather than a lambda in `Scaffold`: a `topBar` lambda re-runs whenever the page
 * recomposes, and this one carries a gradient, a decorative circle and an animated ring icon.
 */
@Composable
private fun HistoryHeader(
    headerProgress: Float,
    motionState: ScrollMotionState,
    background: Brush,
    careerTotal: CareerTotalPR,
    onBackClicked: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TaminTopAppBar(
                title = "",
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(CoreRes.string.action_back),
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
                background = background,
                bottomPadding = dpLerp(Spacing.xxxl, Spacing.sm, headerProgress),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = DecorCircleSize,
                        xOffset = DecorCircleX,
                        yOffset = DecorCircleY,
                    )

                    Text(
                        text = stringResource(HistoryRes.string.history_combined_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = HeaderIconOffset)
                            .motionFade(motionState, startProgress = 0.2f, endProgress = 0.7f)
                            .motionParallax(motionState, parallaxDistance = 20.dp),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dpLerp(Spacing.lg, Spacing.none, headerProgress))
                            .motionParallax(motionState, parallaxDistance = 45.dp)
                            .motionScale(
                                state = motionState,
                                minScale = 0.8f,
                                maxScale = 1f,
                                transformOrigin = TransformOrigin(0.5f, 0.5f),
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedRingHeaderIcon(icon = Icons.Outlined.History)
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = stringResource(HistoryRes.string.history_combined_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Spacer(
                    modifier = Modifier.height(dpLerp(Spacing.sm, Spacing.none, headerProgress)),
                )
            }
            Spacer(
                modifier = Modifier.height(
                    dpLerp(Spacing.xxxl, CollapsedHeaderSpacing, headerProgress),
                ),
            )
        }

        CareerTotalCard(
            total = careerTotal,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = Spacing.page)
                .coloredShadow(
                    color = taminColors.shadowSubtle,
                    borderRadius = CornerRadius.lg,
                    blurRadius = Elevation.lg,
                    offsetY = Spacing.xs,
                ),
        )
    }
}

@Composable
private fun YearListHeader() {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = stringResource(HistoryRes.string.history_combined_list_header),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = taminColors.textPrimary,
        )
        Text(
            text = stringResource(HistoryRes.string.history_combined_list_hint),
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textMuted,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryScreenPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(
                hasLoadedOnce = true,
                years = PreviewYears,
                careerTotal = CareerTotalPR(years = 12, months = 4, days = 18, totalDays = 4518),
            ),
            lazyListState = rememberLazyListState(),
            motionState = rememberScrollMotionState(maxMotionDistance = MaxMotionDistance),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryScreenEmptyPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(hasLoadedOnce = true),
            lazyListState = rememberLazyListState(),
            motionState = rememberScrollMotionState(maxMotionDistance = MaxMotionDistance),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryScreenLoadingPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(isLoading = true),
            lazyListState = rememberLazyListState(),
            motionState = rememberScrollMotionState(maxMotionDistance = MaxMotionDistance),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryScreenDarkPreview() {
    TaminHamrahTheme(darkTheme = true) {
        HistoryContent(
            uiState = HistoryUiState(
                hasLoadedOnce = true,
                years = PreviewYears,
                careerTotal = CareerTotalPR(years = 12, months = 4, days = 18, totalDays = 4518),
            ),
            lazyListState = rememberLazyListState(),
            motionState = rememberScrollMotionState(maxMotionDistance = MaxMotionDistance),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

/** A full year, a part year, and a year with a gap in the middle. */
private val PreviewYears = persistentListOf(
    YearHistoryPR(
        year = "1402",
        monthDays = persistentListOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29),
        totalDays = 365,
    ),
    YearHistoryPR(
        year = "1401",
        monthDays = persistentListOf(31, 31, 31, 0, 0, 0, 30, 30, 30, 30, 30, 29),
        totalDays = 272,
    ),
    YearHistoryPR(
        year = "1400",
        monthDays = persistentListOf(0, 0, 0, 31, 31, 31, 0, 0, 0, 0, 0, 0),
        totalDays = 93,
    ),
)
