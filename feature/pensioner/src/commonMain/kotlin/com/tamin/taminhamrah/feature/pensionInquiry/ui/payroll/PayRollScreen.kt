package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components.PayRollBreakdownSections
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components.PayRollHeader
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components.PayRollNetSummaryBar
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components.PayRollNoPensionerDialog
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components.PayRollPensionerSheet
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components.PayRollSearchSheet
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components.PayRollSkeletonBodyCards
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components.PayRollSuccessDialog
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollUiState
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PayRollPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.btn_send_to_inbox
import taminx.core.core_ui.ic_email
import taminx.core.core_ui.ic_tamin_download


@Composable
fun PayRollScreen(
    onBack: () -> Unit,
    viewModel: PayRollViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val toaster = LocalToaster.current
    var showPdfViewer by remember { mutableStateOf(false) }

    HandlePayRollEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateBack = onBack,
    )

    PayRollContent(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        onDownloadPdf = { showPdfViewer = true },
    )

    if (state.showSearchSheet) {
        PayRollSearchSheet(
            searchYear = state.searchYear,
            searchMonth = state.searchMonth,
            searchPaymentType = state.searchPaymentType,
            onYearChanged = { viewModel.sendIntent(PayRollIntent.ChangeSearchYear(it)) },
            onMonthChanged = { viewModel.sendIntent(PayRollIntent.ChangeSearchMonth(it)) },
            onPaymentTypeChanged = { viewModel.sendIntent(PayRollIntent.ChangeSearchPaymentType(it)) },
            onApply = { viewModel.sendIntent(PayRollIntent.ApplySearch) },
            onDismiss = { viewModel.sendIntent(PayRollIntent.DismissSearchSheet) },
            onClear = { viewModel.sendIntent(PayRollIntent.ClearDateFilter) },
        )
    }

    if (state.showPensionerSheet) {
        PayRollPensionerSheet(
            pensionerIds = state.pensionerIds.map { it.pensionerId },
            selectedId = state.selectedPensionerId,
            onSelect = { id ->
                viewModel.sendIntent(PayRollIntent.ChangeSelectedPensionerId(id))
                viewModel.sendIntent(PayRollIntent.LoadPayRoll)
            },
            onDismiss = { viewModel.sendIntent(PayRollIntent.DismissPensionerSheet) },
        )
    }

    if (state.showNoPensionerDialog) {
        PayRollNoPensionerDialog(
            onDismiss = { viewModel.sendIntent(PayRollIntent.DismissNoPensionerDialog) },
        )
    }

    if (state.showSendSuccess) {
        PayRollSuccessDialog(
            onDismiss = { viewModel.sendIntent(PayRollIntent.DismissSendSuccess) },
        )
    }

    if (showPdfViewer) {
        TaminPdfViewer(
            fileName = payRollFileName(state),
            pdf = state.payRollPDF,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = { viewModel.sendIntent(PayRollIntent.LoadPayRollPDF) },
            onDismiss = {
                showPdfViewer = false
                viewModel.sendIntent(PayRollIntent.DismissPdfViewer)
            },
        )
    }
}

// ─── Event handler ────────────────────────────────────────────────────────────

@Composable
fun HandlePayRollEvents(
    events: Flow<PayRollEvent>,
    onShowToast: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is PayRollEvent.ShowToast -> onShowToast(event.message)
            is PayRollEvent.NavigateBack -> onNavigateBack()
        }
    }
}

// ─── Stateless content ────────────────────────────────────────────────────────

@Composable
fun PayRollContent(
    state: PayRollUiState,
    onIntent: (PayRollIntent) -> Unit,
    onBack: () -> Unit,
    onDownloadPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val hasData = state.payRollList.isNotEmpty()
    val scrollState = rememberScrollState()
    val collapse = rememberCollapsingHeaderState(120.dp)
    var headerHeightPx by remember { mutableStateOf(0) }

    Box(modifier = modifier.fillMaxSize().background(taminColors.bgPage)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection)
                .verticalScroll(scrollState),
        ) {
            Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            if (!state.hasLoadedOnce && state.isLoading) {
                PayRollSkeletonBodyCards(
                    modifier = Modifier
                        .padding(horizontal = Spacing.lg)
                        .padding(top = Spacing.lg),
                )
            } else if (hasData) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = Spacing.lg)
                        .padding(top = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    PayRollBreakdownSections(items = state.payRollList)
                    PayRollNetSummaryBar(items = state.payRollList)
                    Spacer(
                        modifier = Modifier.height(
                            80.dp + WindowInsets.navigationBars
                                .asPaddingValues()
                                .calculateBottomPadding(),
                        ),
                    )
                }
            }
        }

        PayRollHeader(
            state = state,
            onBack = onBack,
            onIntent = onIntent,
            collapseProgress = collapse.progressProvider,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        )

        if (hasData) {
            PayRollBottomBar(
                onSendToInbox = { onIntent(PayRollIntent.RequestSendToInbox) },
                onDownloadPdf = onDownloadPdf,
                isSending = state.isSendingToInbox,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        if (state.isLoading && state.hasLoadedOnce) {
            LoadingStateOverlay()
        }
    }
}

// ─── Bottom action bar ────────────────────────────────────────────────────────

@Composable
private fun PayRollBottomBar(
    onSendToInbox: () -> Unit,
    onDownloadPdf: () -> Unit,
    isSending: Boolean,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(taminColors.glassSolid)
            .padding(Spacing.lg)
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding(),
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconBox(
            modifier = Modifier.border(
                width = 1.dp,
                shape = RoundedCornerShape(CornerRadius.lg),
                color = taminColors.border,
            ).clickable { onDownloadPdf() },
            painter = painterResource(Res.drawable.ic_tamin_download),
            size = 52.dp,
            backgroundColor = taminColors.bgSurface,
            cornerRadius = CornerRadius.lg,
            colorFilter = ColorFilter.tint(color = taminColors.blueText),
        )
        LoadingButton(
            text = stringResource(Res.string.btn_send_to_inbox),
            onClick = onSendToInbox,
            isLoading = isSending,
            modifier = Modifier.weight(1f),
            icon = vectorResource(Res.drawable.ic_email),
        )
    }
}

/**
 * Names the saved file after the query that produced it, not just the pensioner: two periods are
 * two different payrolls, and the viewer treats one name as one document.
 */
private fun payRollFileName(state: PayRollUiState): String {
    val query = listOf(state.selectedPensionerId.orEmpty(), state.startDate, state.paymentType)
        .joinToString("_") { part -> part.filter { it.isLetterOrDigit() } }
        .trim('_')
        .ifBlank { "document" }
    return "payroll_$query.pdf"
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewPayRollItems = listOf(
    PayRollPR(id = 1, clpType = "1", tprDesc = "مبلغ مستمری", sumAmount = 41008708, sumPay = 40852660, hisYear = "1405", hisMon = "05"),
    PayRollPR(id = 2, clpType = "1", tprDesc = "کمک هزینه عائله‌مندی", sumAmount = 439980),
    PayRollPR(id = 3, clpType = "1", tprDesc = "کمک به تامین معیشت", sumAmount = 385000),
    PayRollPR(id = 4, clpType = "2", tprDesc = "بیمه عمر", sumAmount = -71600),
    PayRollPR(id = 5, clpType = "2", tprDesc = "بیمه درمان تکمیلی", sumAmount = -584000),
)

private val PreviewPayRollUiState = PayRollUiState(
    isLoading = false,
    hasLoadedOnce = true,
    selectedPensionerId = "1003406938",
    startDate = "140505",
    pensionerIds = listOf(PensionIdPR("1003406938"), PensionIdPR("1003406939")),
    payRollList = PreviewPayRollItems,
)

@PreviewRtlTheme
@Composable
private fun PayRollContentPreview() {
    PreviewRtlThemeContent {
        PayRollContent(
            state = PreviewPayRollUiState,
            onIntent = {},
            onBack = {},
            onDownloadPdf = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollContentDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        PayRollContent(
            state = PreviewPayRollUiState,
            onIntent = {},
            onBack = {},
            onDownloadPdf = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollContentLoadingPreview() {
    PreviewRtlThemeContent {
        PayRollContent(
            state = PayRollUiState(isLoading = true, hasLoadedOnce = false),
            onIntent = {},
            onBack = {},
            onDownloadPdf = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollContentEmptyPreview() {
    PreviewRtlThemeContent {
        PayRollContent(
            state = PreviewPayRollUiState.copy(payRollList = emptyList()),
            onIntent = {},
            onBack = {},
            onDownloadPdf = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollBottomBarPreview() {
    PreviewRtlThemeContent {
        PayRollBottomBar(
            onSendToInbox = {},
            onDownloadPdf = {},
            isSending = false,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollBottomBarSendingPreview() {
    PreviewRtlThemeContent {
        PayRollBottomBar(
            onSendToInbox = {},
            onDownloadPdf = {},
            isSending = true,
        )
    }
}
