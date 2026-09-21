package com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestStep
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionListSkeleton
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.InfoBanner
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionEvent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionIntent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionUiState
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.components.WorkshopInspectionHeader
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.components.WorkshopInspectionItemCard
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.components.WorkshopInspectionSearchSheet
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.steps.WorkshopInspectionRequestScreen
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.Toast
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.paging.OnLoadMore
import com.tamin.taminhamrah.ui.paging.PagingFooter
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_exit_confirmation_confirm
import taminx.core.core_ui.occurrence_exit_confirmation_desc
import taminx.core.core_ui.occurrence_exit_confirmation_dismiss
import taminx.core.core_ui.occurrence_exit_confirmation_title
import taminx.core.core_ui.workshop_inspection_empty_subtitle
import taminx.core.core_ui.workshop_inspection_empty_title
import taminx.core.core_ui.workshop_inspection_note
import taminx.core.core_ui.workshop_inspection_report_filename_format
import taminx.core.core_ui.inspection_search_empty_title
import taminx.core.core_ui.workshop_inspection_search_empty_subtitle

/** Steps with unsaved input that warrant an "are you sure?" before closing the wizard. */
private val STEPS_REQUIRING_EXIT_CONFIRMATION = setOf(
    InspectionRequestStep.WORKSHOP_INFO,
    InspectionRequestStep.REQUEST_DESCRIPTION,
)

private const val PAGING_FOOTER_KEY = "workshop_inspection_paging_footer"

@Composable
fun WorkshopInspectionRoute(
    viewModel: WorkshopInspectionViewModel,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    WorkshopInspectionEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateBack = onBackClicked,
    )

    if (uiState.showRequestFlow) {
        val onExitRequested = remember(uiState.requestStep) {
            {
                if (uiState.requestStep in STEPS_REQUIRING_EXIT_CONFIRMATION) {
                    viewModel.sendIntent(WorkshopInspectionIntent.SetExitConfirmationVisible(true))
                } else {
                    viewModel.sendIntent(WorkshopInspectionIntent.CloseRequestFlow)
                }
            }
        }

        BackHandler(onBack = { viewModel.sendIntent(WorkshopInspectionIntent.GoToPreviousRequestStep) })

        if (uiState.showExitConfirmation) {
            val taminColors = LocalTaminColors.current
            TaminConfirmationDialog(
                title = stringResource(Res.string.occurrence_exit_confirmation_title),
                description = stringResource(Res.string.occurrence_exit_confirmation_desc),
                confirmButton = {
                    TaminFilledButton(
                        text = stringResource(Res.string.occurrence_exit_confirmation_confirm),
                        onClick = { viewModel.sendIntent(WorkshopInspectionIntent.SetExitConfirmationVisible(false)) },
                        modifier = Modifier.fillMaxWidth(),
                        height = 50.dp,
                        shape = RoundedCornerShape(14.dp),
                        icon = Icons.Default.Check,
                    )
                },
                dismissButton = {
                    TaminOutlinedButton(
                        text = stringResource(Res.string.occurrence_exit_confirmation_dismiss),
                        onClick = {
                            viewModel.sendIntent(WorkshopInspectionIntent.SetExitConfirmationVisible(false))
                            viewModel.sendIntent(WorkshopInspectionIntent.CloseRequestFlow)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        height = 50.dp,
                        shape = RoundedCornerShape(14.dp),
                        borderWidth = 0.dp,
                        contentColor = taminColors.textSecondary,
                    )
                },
                onDismissRequest = { viewModel.sendIntent(WorkshopInspectionIntent.SetExitConfirmationVisible(false)) },
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
            )
        }

        WorkshopInspectionRequestScreen(
            uiState = uiState,
            onIntent = viewModel::sendIntent,
            onClose = onExitRequested,
        )
    } else {
        WorkshopInspectionScreen(
            uiState = uiState,
            onIntent = viewModel::sendIntent,
            onBackClicked = onBackClicked,
        )
    }
}

@Composable
fun WorkshopInspectionEvents(
    events: Flow<WorkshopInspectionEvent>,
    onShowToast: (String) -> Toast,
    onNavigateBack: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            WorkshopInspectionEvent.NavigateBack -> onNavigateBack()
            is WorkshopInspectionEvent.ShowToast -> onShowToast(event.message)
        }
    }
}

@Composable
internal fun WorkshopInspectionScreen(
    uiState: WorkshopInspectionUiState,
    onIntent: (WorkshopInspectionIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    val topArea = rememberMeasuredTopAreaState { state ->
        WorkshopInspectionHeader(
            count = uiState.inspections.size,
            topAreaState = state,
            onBackClicked = onBackClicked,
            onSearchClicked = { onIntent(WorkshopInspectionIntent.OpenSearchSheet) },
        )
    }
    val listState = rememberLazyListState()

    var viewingInspectionNo by remember { mutableStateOf<String?>(null) }

    val filtered = uiState.filteredInspections
    val staggerState = rememberStaggeredEntranceState(key = filtered.size)

    listState.OnLoadMore(
        enabled = !uiState.inspectionsEndReached &&
            uiState.inspectionsPagingError == null &&
            uiState.inspections.isNotEmpty(),
    ) {
        onIntent(WorkshopInspectionIntent.LoadNextInspections)
    }

    // A filter only matches what's been paged in so far (see WorkshopInspectionFilter's kdoc) —
    // if it comes up empty while more pages remain, keep loading automatically so "no results" is
    // only shown once the whole list has actually been searched, not just the pages scrolled past.
    LaunchedEffect(
        uiState.appliedFilter,
        filtered.isEmpty(),
        uiState.inspectionsEndReached,
        uiState.isLoadingNextInspections,
        uiState.inspectionsPagingError,
    ) {
        if (uiState.appliedFilter != null &&
            filtered.isEmpty() &&
            !uiState.inspectionsEndReached &&
            !uiState.isLoadingNextInspections &&
            uiState.inspectionsPagingError == null
        ) {
            onIntent(WorkshopInspectionIntent.LoadNextInspections)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            state = listState,
            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier
                .fillMaxSize()
                .driveTopArea(topArea, listState),
            contentPadding = topAreaContentPadding(
                state = topArea,
                rest = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + Spacing.lg,
                )
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                BannerCard(
                    type = BannerType.Info,
                    message = stringResource(Res.string.workshop_inspection_note),
                    modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                )
            }

            when {
                uiState.isLoadingInspections && uiState.inspections.isEmpty() -> item {
                    InspectionListSkeleton()
                }

                uiState.inspections.isEmpty() && uiState.inspectionsPagingError != null -> item {
                    PagingFooter(
                        isLoadingNextPage = false,
                        error = uiState.inspectionsPagingError,
                        onRetry = { onIntent(WorkshopInspectionIntent.RetryNextInspections) },
                        modifier = Modifier.padding(horizontal = Spacing.lg),
                    )
                }

                filtered.isEmpty() && uiState.appliedFilter != null && !uiState.inspectionsEndReached -> item {
                    // Still paging through the rest of the list looking for a match — see the
                    // LaunchedEffect above. Only once inspectionsEndReached is true have we
                    // actually searched everything, and the "not found" state below applies.
                    InspectionListSkeleton()
                }

                filtered.isEmpty() && uiState.appliedFilter != null -> item {
                    EmptyStateMessage(
                        icon = Icons.Outlined.Assignment,
                        title = stringResource(Res.string.inspection_search_empty_title),
                        subtitle = stringResource(Res.string.workshop_inspection_search_empty_subtitle),
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = Spacing.xlg),
                    )
                }

                uiState.inspections.isEmpty() -> item {
                    EmptyStateMessage(
                        icon = Icons.Outlined.Assignment,
                        title = stringResource(Res.string.workshop_inspection_empty_title),
                        subtitle = stringResource(Res.string.workshop_inspection_empty_subtitle),
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = Spacing.xlg),
                    )
                }

                else -> {
                    itemsIndexed(
                        filtered,
                        key = { _, item -> item.inspectionNo }) { index, item ->
                        WorkshopInspectionItemCard(
                            item = item,
                            onSubmitObjectionClicked = {
                                onIntent(WorkshopInspectionIntent.OpenRequestFlow(item = item))
                            },
                            onDownloadReportClicked = { inspectionNo ->
                                viewingInspectionNo = inspectionNo
                                onIntent(WorkshopInspectionIntent.DownloadReportPdf(inspectionNo))
                            },
                            modifier = Modifier
                                .padding(horizontal = Spacing.lg)
                        )
                    }

                    if (uiState.appliedFilter == null) {
                        item(key = PAGING_FOOTER_KEY) {
                            PagingFooter(
                                isLoadingNextPage = uiState.isLoadingNextInspections,
                                error = uiState.inspectionsPagingError,
                                onRetry = { onIntent(WorkshopInspectionIntent.RetryNextInspections) },
                            )
                        }
                    }
                }
            }
        }

        WorkshopInspectionHeader(
            count = uiState.inspections.size,
            topAreaState = topArea,
            onBackClicked = onBackClicked,
            onSearchClicked = { onIntent(WorkshopInspectionIntent.OpenSearchSheet) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )

        if (uiState.isLoading && uiState.inspections.isNotEmpty()) {
            LoadingStateOverlay()
        }
    }

    if (uiState.isSearchSheetOpen) {
        WorkshopInspectionSearchSheet(
            workshopCodeQuery = uiState.workshopCodeQuery,
            onWorkshopCodeQueryChange = { onIntent(WorkshopInspectionIntent.UpdateWorkshopCodeQuery(it)) },
            inspectionIdQuery = uiState.inspectionIdQuery,
            onInspectionIdQueryChange = { onIntent(WorkshopInspectionIntent.UpdateInspectionIdQuery(it)) },
            onApply = { onIntent(WorkshopInspectionIntent.ApplySearch) },
            onClear = { onIntent(WorkshopInspectionIntent.ClearSearch) },
            onDismiss = { onIntent(WorkshopInspectionIntent.CloseSearchSheet) },
        )
    }

    if (viewingInspectionNo != null) {
        val inspectionNo = viewingInspectionNo.orEmpty()
        TaminPdfViewer(
            fileName = stringResource(Res.string.workshop_inspection_report_filename_format, inspectionNo),
            pdf = uiState.viewerPdf,
            downloadFailed = uiState.viewerDownloadFailed,
            onRequestDownload = { onIntent(WorkshopInspectionIntent.DownloadReportPdf(inspectionNo)) },
            onDismiss = {
                viewingInspectionNo = null
                onIntent(WorkshopInspectionIntent.DismissPdfViewer)
            },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewWorkshopInspectionScreenLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            WorkshopInspectionScreen(
                uiState = WorkshopInspectionUiState(inspections = PreviewMockInspections),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewWorkshopInspectionScreenDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            WorkshopInspectionScreen(
                uiState = WorkshopInspectionUiState(inspections = PreviewMockInspections),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewWorkshopInspectionScreenEmpty() {
    PreviewRtlThemeContent {
        AppToastHost {
            WorkshopInspectionScreen(
                uiState = WorkshopInspectionUiState(inspections = persistentListOf()),
                onIntent = {},
                onBackClicked = {},
            )
        }
    }
}

private val PreviewMockInspections = persistentListOf(
    InspectionPerformedPR(
        activityDesc = "دبستان غیردولتی",
        branchCode = "0210",
        branchdesc = "یک بجنورد",
        inspectionDate = 1743280800000L,
        inspectionNo = "6310020000706",
        insuranceNo = "",
        objectable = "1",
        relationType = "کارفرما",
        workshopName = "دبستان کارن ۲ مجتبی غلامیان",
        workshopNo = "9028212822",
        nationalCode = "0681895705",
    ),
    InspectionPerformedPR(
        activityDesc = "اجرای پروژه‌های راه و ابنیه",
        branchCode = "0210",
        branchdesc = "دو بجنورد",
        inspectionDate = 1683059400000L,
        inspectionNo = "6310020000287",
        insuranceNo = "0016318941",
        objectable = "0",
        relationType = "کارفرما",
        workshopName = "شرکت راه‌سازی البرز شرق",
        workshopNo = "9007441260",
        nationalCode = "0681895705",
    ),
)
