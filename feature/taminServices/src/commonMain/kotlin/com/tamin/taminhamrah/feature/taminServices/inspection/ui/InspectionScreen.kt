package com.tamin.taminhamrah.feature.taminServices.inspection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.steps.InspectionRequestScreen
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionFilterChipRow
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionHeader
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionItemCard
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionListSkeleton
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionSearchEmptyState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionSearchSheet
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionEvent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestStep
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionUiState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionSearchValidation
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.Toast
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inspection_count_format
import taminx.core.core_ui.inspection_empty_subtitle
import taminx.core.core_ui.inspection_empty_title
import taminx.core.core_ui.inspection_report_filename_format
import taminx.core.core_ui.inspection_request_button
import taminx.core.core_ui.occurrence_exit_confirmation_confirm
import taminx.core.core_ui.occurrence_exit_confirmation_desc
import taminx.core.core_ui.occurrence_exit_confirmation_dismiss
import taminx.core.core_ui.occurrence_exit_confirmation_title

private val HeaderCollapseDistance = 160.dp

/** Steps with unsaved input that warrant an "are you sure?" before closing the wizard — mirrors occurrence's set (everything but the first step). */
private val STEPS_REQUIRING_EXIT_CONFIRMATION = setOf(
    InspectionRequestStep.WORKSHOP_INFO,
    InspectionRequestStep.REQUEST_DESCRIPTION,
)

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

     if (uiState.showRequestFlow) {
         val onExitRequested = remember(uiState.requestStep) {
             {
                 if (uiState.requestStep in STEPS_REQUIRING_EXIT_CONFIRMATION) {
                     viewModel.sendIntent(InspectionIntent.SetExitConfirmationVisible(true))
                 } else {
                     viewModel.sendIntent(InspectionIntent.CloseRequestFlow)
                 }
             }
         }

         BackHandler(onBack = { viewModel.sendIntent(InspectionIntent.GoToPreviousRequestStep) })

         if (uiState.showExitConfirmation) {
             val taminColors = LocalTaminColors.current
             TaminConfirmationDialog(
                 title = stringResource(Res.string.occurrence_exit_confirmation_title),
                 description = stringResource(Res.string.occurrence_exit_confirmation_desc),
                 confirmButton = {
                     TaminFilledButton(
                         text = stringResource(Res.string.occurrence_exit_confirmation_confirm),
                         onClick = { viewModel.sendIntent(InspectionIntent.SetExitConfirmationVisible(false)) },
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
                             viewModel.sendIntent(InspectionIntent.SetExitConfirmationVisible(false))
                             viewModel.sendIntent(InspectionIntent.CloseRequestFlow)
                         },
                         modifier = Modifier.fillMaxWidth(),
                         height = 50.dp,
                         shape = RoundedCornerShape(14.dp),
                         borderWidth = 0.dp,
                         contentColor = taminColors.textSecondary,
                     )
                 },
                 onDismissRequest = { viewModel.sendIntent(InspectionIntent.SetExitConfirmationVisible(false)) },
                 icon = Icons.AutoMirrored.Outlined.HelpOutline,
             )
         }

         InspectionRequestScreen(
             uiState = uiState,
             onIntent = viewModel::sendIntent,
             onClose = onExitRequested,
         )
     } else {
        InspectionScreen(
            uiState = uiState,
            onIntent = viewModel::sendIntent,
            onBackClicked = onBackClicked,
        )
     }
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
    initialSearchCriteria: InspectionSearchValidation = InspectionSearchValidation(),
) {
    val taminColors = LocalTaminColors.current

    val collapse = rememberCollapsingHeaderState(HeaderCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }
    var viewingInspectionNo by remember { mutableStateOf<String?>(null) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var searchCriteria by remember { mutableStateOf(initialSearchCriteria) }

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

            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        TaminNavy300,
                                        TaminNavy900
                                    )
                                )
                            )
                            .padding(vertical = 2.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = { onIntent(InspectionIntent.OpenRequestFlow()) },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TaminText(text = "+", color = Color.White)
                            Spacer(Modifier.width(4.dp))
                            TaminText(
                                text = stringResource(Res.string.inspection_request_button),
                                color = Color.White
                            )
                        }

                    }
                    Spacer(Modifier.width(Spacing.sm))
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                color = taminColors.bgSurface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = taminColors.border,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp)
                    ) {
                        TaminText(
                            text = uiState.inspections?.size?.toString() ?: "0",
                            color = taminColors.textPrimary
                        )
                        TaminText(
                            text = stringResource(Res.string.inspection_count_format),
                            color = taminColors.textMuted,
                            fontSize = 12.sp
                        )
                    }
                }
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
                            onClear = { searchCriteria = InspectionSearchValidation() },
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
                        onSubmitObjectionClicked = {
                            onIntent(InspectionIntent.OpenRequestFlow(item = item))
                        },
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
            collapseProgress = collapse.progressProvider,
            onBackClicked = onBackClicked,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
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
                searchCriteria = InspectionSearchValidation()
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

private val PreviewNoMatchSearchCriteria = InspectionSearchValidation(inspectionNo = "00000000000")

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
