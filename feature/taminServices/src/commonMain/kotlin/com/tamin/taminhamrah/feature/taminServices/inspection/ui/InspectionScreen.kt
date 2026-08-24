package com.tamin.taminhamrah.feature.taminServices.inspection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionFilterChipRow
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionHeader
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionItemCard
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionListSkeleton
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionSearchEmptyState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionSearchSheet
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionEvent
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionUiState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionSearchCriteria
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.Toast
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.info
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inspection_coming_soon_toast
import taminx.core.core_ui.inspection_empty_subtitle
import taminx.core.core_ui.inspection_empty_title
import taminx.core.core_ui.inspection_report_filename_format

private val HeaderCollapseDistance = 160.dp

@Composable
fun InspectionRoute(
    viewModel: InspectionViewModel,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    InspectionEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateBack = onBackClicked,
    )

    viewModel.events.collectWithLifecycleAware { event ->

    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(InspectionIntent.LoadInspections())
    }

    InspectionScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
    )
}

@Composable
fun InspectionEvents(
    events: Flow<InspectionEvent>,
    onShowToast: (String) -> Toast,
    onNavigateBack: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            InspectionEvent.NavigateBack -> onNavigateBack()
            is InspectionEvent.ShowToast -> onShowToast(event.message)
        }
    }
}

@Composable
internal fun InspectionScreen(
    uiState: InspectionUiState,
    onIntent: (InspectionIntent) -> Unit,
    onBackClicked: () -> Unit,
    // Only ever non-default from a preview, to render the "no results for this filter" branch
    // without exercising the search sheet.
    initialSearchCriteria: InspectionSearchCriteria = InspectionSearchCriteria(),
) {
    val taminColors = LocalTaminColors.current
    val toaster = LocalToaster.current
    val comingSoonMessage = stringResource(Res.string.inspection_coming_soon_toast)

    val collapse = rememberCollapsingHeaderState(HeaderCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }
    var viewingInspectionNo by remember { mutableStateOf<String?>(null) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var searchCriteria by remember { mutableStateOf(initialSearchCriteria) }

    // Purely local filtering over whatever the API last returned — clearing the criteria (via the
    // filter chip's close button or the sheet's own clear button) falls straight back to
    // uiState.inspections without any re-fetch.
    val visibleInspections = remember(uiState.inspections, searchCriteria) {
        if (searchCriteria.isEmpty) {
            uiState.inspections
        } else {
            uiState.inspections.filter { searchCriteria.matches(it) }
        }
    }
    val staggerState = rememberStaggeredEntranceState(key = visibleInspections.size)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            }

            if (uiState.isLoading && uiState.inspections.isEmpty()) {
                item {
                    InspectionListSkeleton()
                }
            } else if (uiState.inspections.isEmpty()) {
                item {
                    EmptyStateMessage(
                        icon = Icons.Outlined.Assignment,
                        title = stringResource(Res.string.inspection_empty_title),
                        subtitle = stringResource(Res.string.inspection_empty_subtitle),
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = Spacing.xlg),
                    )
                }
            } else {
                if (!searchCriteria.isEmpty) {
                    item {
                        InspectionFilterChipRow(
                            criteria = searchCriteria,
                            onClear = { searchCriteria = InspectionSearchCriteria() },
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                        )
                    }
                }

                if (visibleInspections.isEmpty()) {
                    item {
                        InspectionSearchEmptyState(
                            modifier = Modifier.padding(horizontal = Spacing.lg),
                        )
                    }
                }

                itemsIndexed(
                    visibleInspections,
                    key = { _, item -> item.inspectionNo }) { index, item ->
                    InspectionItemCard(
                        item = item,
                        onSubmitObjectionClicked = { toaster.info(comingSoonMessage) },
                        onDownloadReportClicked = { inspectionNo ->
                            viewingInspectionNo = inspectionNo
                            onIntent(InspectionIntent.DownloadReportPdf(inspectionNo))
                        },
                        modifier = Modifier
                            .padding(horizontal = Spacing.lg)
                            .staggeredItemEntrance(
                                index = index,
                                key = item.inspectionNo,
                                state = staggerState
                            ),
                    )
                }
            }
        }

        InspectionHeader(
//            count = uiState.inspections.size,
            collapseProgress = collapse.progressProvider,
            onBackClicked = onBackClicked,
//            onRequestInspectionClicked = { toaster.info(comingSoonMessage) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
            onIntent = {},
            onSearchClicked = { showSearchSheet = true },
        )

        if (uiState.isLoading && uiState.inspections.isNotEmpty()) {
            LoadingStateOverlay()
        }
    }

    if (viewingInspectionNo != null) {
        val inspectionNo = viewingInspectionNo.orEmpty()
        TaminPdfViewer(
            fileName = stringResource(Res.string.inspection_report_filename_format, inspectionNo),
            pdf = uiState.viewerPdf,
            downloadFailed = uiState.viewerDownloadFailed,
            onRequestDownload = { onIntent(InspectionIntent.DownloadReportPdf(inspectionNo)) },
            onDismiss = {
                viewingInspectionNo = null
                onIntent(InspectionIntent.DismissPdfViewer)
            },
        )
    }

    if (showSearchSheet) {
        InspectionSearchSheet(
            initial = searchCriteria,
            onDismiss = { showSearchSheet = false },
            onApply = { criteria ->
                searchCriteria = criteria
                showSearchSheet = false
            },
            onClear = {
                searchCriteria = InspectionSearchCriteria()
                showSearchSheet = false
            },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewInspectionScreenLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            InspectionScreen(
                uiState = InspectionUiState(inspections = PreviewMockInspections),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewInspectionScreenDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            InspectionScreen(
                uiState = InspectionUiState(inspections = PreviewMockInspections),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewInspectionScreenEmpty() {
    PreviewRtlThemeContent {
        AppToastHost {
            InspectionScreen(
                uiState = InspectionUiState(inspections = emptyList()),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewInspectionScreenSearchEmptyLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            InspectionScreen(
                uiState = InspectionUiState(inspections = PreviewMockInspections),
                onIntent = {},
                onBackClicked = {},
                initialSearchCriteria = PreviewNoMatchSearchCriteria,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewInspectionScreenSearchEmptyDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            InspectionScreen(
                uiState = InspectionUiState(inspections = PreviewMockInspections),
                onIntent = {},
                onBackClicked = {},
                initialSearchCriteria = PreviewNoMatchSearchCriteria,
            )
        }
    }
}

private val PreviewNoMatchSearchCriteria = InspectionSearchCriteria(inspectionNo = "00000000000")

private val PreviewMockInspections = listOf(
    InspectionPerformedPR(
        activityDesc = "اجرای پروژه‌های ساختمانی و تأسیسات",
        branchCode = "0117",
        branchdesc = "پنج تهران",
        inspectionDate = 1743280800000L,
        inspectionNo = "01631894",
        insuranceNo = "01631894",
        objectable = "1",
        relationType = "کارگر پیمانی",
        workshopName = "شرکت پیمانکاری ساخت و ابنیهٔ کاوه",
        workshopNo = "0117742260",
        nationalCode = "",
    ),
    InspectionPerformedPR(
        activityDesc = "تولید محصولات فلزی",
        branchCode = "0924",
        branchdesc = "دو تهران",
        inspectionDate = 1683059400000L,
        inspectionNo = "0202031500",
        insuranceNo = "0202031500",
        objectable = "0",
        relationType = "کارفرما",
        workshopName = "مجتمع فولاد نگین شرق",
        workshopNo = "0924447115",
        nationalCode = "",
    ),
)
