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
import com.tamin.taminhamrah.mapper.history.labelRes
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.BarChartItem
import com.tamin.taminhamrah.ui.components.TaminBarChart
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheet
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetConfig
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetItem
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
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_tamin_download
import taminx.feature.history.Res as HistoryRes
import taminx.feature.history.history_combined_empty
import taminx.feature.history.history_combined_empty_title
import taminx.feature.history.history_combined_list_header
import taminx.feature.history.history_combined_list_hint
import taminx.feature.history.history_combined_not_insured
import taminx.feature.history.history_combined_subtitle
import taminx.feature.history.history_combined_chart_title
import taminx.feature.history.history_combined_title
import taminx.feature.history.history_report_action
import taminx.feature.history.history_report_menu_title
import androidx.compose.ui.unit.lerp as dpLerp
import androidx.compose.foundation.layout.Row
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialYearBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialYearTop

private val HeaderIconOffset = (-30).dp
private val MaxMotionDistance = 120.dp
private val CollapsedHeaderSpacing = 25.dp
private val DecorCircleSize = 190.dp
private val DecorCircleX = 450.dp
private val DecorCircleY = (-150).dp

/** What a year card measures, so the skeleton stands in for one without the list jumping. */
private val YearCardHeight = 76.dp

/**
 * [onBackClicked] deliberately has no default: it is what «بستن» on a failed load and the app bar's
 * chevron both lead to, and a defaulted no-op here is a screen the user cannot leave.
 */
@Composable
fun HistoryScreen(
    onBackClicked: () -> Unit,
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
                motionState = motionState,
                background = profileGradientBrush,
                careerTotal = uiState.careerTotal,
                onBackClicked = onBackClicked,
                onDownloadClicked = { onIntent(HistoryIntent.ShowReportMenu) },
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
                    item(key = CHART_KEY) {
                        YearChart(
                            years = uiState.years,
                            // The bar reports the year it drew, never its index, so the sheet
                            // cannot be opened on a different year than the one tapped.
                            onYearClick = { year -> onIntent(HistoryIntent.SelectYear(year)) },
                        )
                    }

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
    //
    // A refusal gets no «تلاش دوباره». Nothing failed and nothing will change on a second attempt —
    // this person simply has no insured years — so the only button is the way out.
    if (uiState.accessDenied) {
        ErrorStateView(
            message = stringResource(HistoryRes.string.history_combined_not_insured),
            onDismiss = onBackClicked,
        )
    } else {
        ErrorStateView(
            message = uiState.error,
            onDismiss = onBackClicked,
            onRetry = { onIntent(HistoryIntent.Load) },
        )
    }

    uiState.selectedYear?.let { year ->
        YearDetailSheet(
            year = year,
            workshops = uiState.wageByYear[year.year] ?: NoWorkshops,
            wagesUnavailable = uiState.wagesUnavailable,
            onDismiss = { onIntent(HistoryIntent.DismissYearDetail) },
        )
    }

    if (uiState.showReportMenu) {
        ReportMenu(
            onSelect = { onIntent(HistoryIntent.SelectReport(it)) },
            onDismiss = { onIntent(HistoryIntent.DismissReportMenu) },
        )
    }

    // The same viewer every downloaded document in the app opens in: it renders a copy already on
    // the device without asking, saves the one it fetches, and refuses a body that is not a PDF.
    uiState.selectedReport?.let { report ->
        TaminPdfViewer(
            fileName = report.fileName(),
            pdf = uiState.reportPdf,
            downloadFailed = uiState.reportDownloadFailed,
            onRequestDownload = { onIntent(HistoryIntent.DownloadReport) },
            onDismiss = { onIntent(HistoryIntent.DismissReport) },
            title = stringResource(report.labelRes()),
        )
    }
}

private const val CHART_KEY = "chart"
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
    motionState: ScrollMotionState,
    background: Brush,
    careerTotal: CareerTotalPR,
    onBackClicked: () -> Unit,
    onDownloadClicked: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    // Read here and nowhere higher. `progress` changes on every frame of a scroll, so a read in
    // `HistoryContent` would recompose the chart and the whole year list along with the header.
    val headerProgress = motionState.progress

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
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(CoreRes.drawable.ic_tamin_download),
                        contentDescription = stringResource(HistoryRes.string.history_report_action),
                        onClick = onDownloadClicked,
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

/**
 * The three «سوابق» reports, in the order the previous app listed them.
 *
 * Built on the shared bottom sheet rather than a menu of its own: single-select is what it already
 * does, and the labels come from the one table both this screen and اعلام سابقه read.
 */
@Composable
private fun ReportMenu(
    onSelect: (HistoryCertificateType) -> Unit,
    onDismiss: () -> Unit,
) {
    val title = stringResource(HistoryRes.string.history_report_menu_title)
    val allLabel = stringResource(ReportTypes[0].labelRes())
    val wagesLabel = stringResource(ReportTypes[1].labelRes())
    val combinedLabel = stringResource(ReportTypes[2].labelRes())

    // Keyed on the resolved strings themselves. Keying on a list built at the call site would key
    // on a new instance every time and rebuild the config on every recomposition.
    val config = remember(title, allLabel, wagesLabel, combinedLabel) {
        TaminBottomSheetConfig(
            title = title,
            singleSelection = true,
            items = listOf(
                TaminBottomSheetItem(id = 0, title = allLabel),
                TaminBottomSheetItem(id = 1, title = wagesLabel),
                TaminBottomSheetItem(id = 2, title = combinedLabel),
            ),
        )
    }

    TaminBottomSheet(
        config = config,
        onDismissRequest = onDismiss,
        onSubmit = { result ->
            // Single-select, so there is at most one; nothing chosen simply closes the sheet.
            val chosen = result.selectedItemIds.firstOrNull()?.let(ReportTypes::getOrNull)
            if (chosen == null) onDismiss() else onSelect(chosen)
        },
    )
}

/** Declared beside the sheet that orders them, so the order is not a coincidence of the enum. */
private val ReportTypes = listOf(
    HistoryCertificateType.ALL,
    HistoryCertificateType.WAGES,
    HistoryCertificateType.COMBINED,
)

/**
 * What the saved file is called on the device.
 *
 * Stable per report, because [TaminPdfViewer] uses the name to decide whether it already has the
 * file — a name with a timestamp in it would download the same report again every time.
 */
private fun HistoryCertificateType.fileName(): String = when (this) {
    HistoryCertificateType.ALL -> "history_all.pdf"
    HistoryCertificateType.WAGES -> "history_wages.pdf"
    HistoryCertificateType.COMBINED -> "history_combined.pdf"
}

/**
 * The years as bars, which is how the previous app opened this page.
 *
 * The list below says the same thing in words; the chart is what makes a gap or a short year
 * visible without reading every row. Tapping a bar opens the same sheet the card does.
 */
@Composable
private fun YearChart(
    years: ImmutableList<YearHistoryPR>,
    onYearClick: (YearHistoryPR) -> Unit,
) {
    val taminColors = LocalTaminColors.current

    // Rebuilt only when the years themselves change: without this the whole series would be
    // reallocated on every recomposition of the page, scrolling included.
    //
    // Most recent first and scaled against a full year rather than against the tallest bar, which
    // is what the design plots: a short year has to look short next to a full one, not merely
    // shorter than the best year this person had.
    val bars = remember(years) {
        years.asReversed().map { year ->
            val full = year.isComplete
            BarChartItem(
                id = year.year,
                label = year.year.toPersianDigits(),
                fraction = year.totalDays / DaysInFullYear,
                fillTop = if (full) TaminHistoryBarFullTop else TaminHistoryBarPartialYearTop,
                fillBottom = if (full) {
                    TaminHistoryBarFullBottom
                } else {
                    TaminHistoryBarPartialYearBottom
                },
                labelColor = taminColors.textTertiary,
            )
        }.toImmutableList()
    }
    val byYear = remember(years) { years.associateBy { it.year } }
    // The design switches to narrow bars and a three-point axis once a career outgrows its labels.
    val dense = bars.size > DenseBarThreshold

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(HistoryRes.string.history_combined_chart_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = taminColors.textPrimary,
        )
        TaminBarChart(
            bars = bars,
            onBarClick = { id -> byYear[id]?.let(onYearClick) },
            dense = dense,
            showLabels = !dense,
        )

        if (dense) {
            YearAxis(
                oldest = bars.last().label,
                middle = bars[bars.size / 2].label,
                newest = bars.first().label,
            )
        }
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

/** Three fixed points instead of a label under every bar, once the bars are too narrow to name. */
@Composable
private fun YearAxis(oldest: String, middle: String, newest: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        NumericText(
            text = oldest,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        NumericText(
            text = middle,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        NumericText(
            text = newest,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
        )
    }
}

/** A full Jalali year in days — what a bar's height is measured against. */
private const val DaysInFullYear = 366f

/** Above this many years the design narrows the bars and drops their labels. */
private const val DenseBarThreshold = 12

@PreviewRtlTheme
@Composable
private fun HistoryScreenErrorPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(
                hasLoadedOnce = true,
                error = "در دریافت اطلاعات مشکلی پیش آمد. لطفاً دوباره تلاش کنید.",
            ),
            lazyListState = rememberLazyListState(),
            motionState = rememberScrollMotionState(maxMotionDistance = MaxMotionDistance),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

/** The one state with no «تلاش دوباره»: nothing failed, so there is nothing to retry. */
@PreviewRtlTheme
@Composable
private fun HistoryScreenAccessDeniedPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(accessDenied = true),
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
