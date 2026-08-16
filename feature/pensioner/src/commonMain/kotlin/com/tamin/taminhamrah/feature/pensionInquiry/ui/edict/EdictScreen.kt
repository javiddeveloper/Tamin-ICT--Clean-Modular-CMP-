package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components.EdictComparisonCard
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components.EdictDetailsSection
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components.EdictHeader
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components.EdictPensionerSheet
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components.EdictSearchSheet
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components.EdictSuccessDialog
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components.SurvivorShareCard
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.btn_send_to_inbox
import taminx.core.core_ui.edict_send_success_desc
import taminx.core.core_ui.edict_send_success_title
import taminx.core.core_ui.ic_email
import taminx.core.core_ui.ic_tamin_download

// ─── Entry point ──────────────────────────────────────────────────────────────

@Composable
fun EdictScreen(
    onBack: () -> Unit,
    viewModel: EdictViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val toaster = LocalToaster.current
    var showPdfViewer by remember { mutableStateOf(false) }

    HandleEdictEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
    )

    EdictContent(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        onDownloadPdf = { showPdfViewer = true },
    )

    // ── Overlays rendered outside the content composable so they float over everything ──

    if (state.showSearchSheet) {
        EdictSearchSheet(
            searchYear = state.searchYear,
            searchMonth = state.searchMonth,
            onYearChanged = { viewModel.sendIntent(EdictIntent.ChangeSearchYear(it)) },
            onMonthChanged = { viewModel.sendIntent(EdictIntent.ChangeSearchMonth(it)) },
            onApply = { viewModel.sendIntent(EdictIntent.ApplySearch) },
            onDismiss = { viewModel.sendIntent(EdictIntent.DismissSearchSheet) },
            onClear = { viewModel.sendIntent(EdictIntent.ClearDateFilter) },
        )
    }

    if (state.showPensionerSheet) {
        EdictPensionerSheet(
            pensionerIds = state.pensionerIds.map { it.pensionerId },
            selectedId = state.selectedPensionerId,
            onSelect = { id ->
                viewModel.sendIntent(EdictIntent.ChangeSelectedPensionerId(id))
                viewModel.sendIntent(EdictIntent.LoadEdict)
            },
            onDismiss = { viewModel.sendIntent(EdictIntent.DismissPensionerSheet) },
        )
    }

    if (state.showSendSuccess) {
        EdictSuccessDialog(
            title = stringResource(Res.string.edict_send_success_title),
            description = stringResource(Res.string.edict_send_success_desc),
            onDismiss = { viewModel.sendIntent(EdictIntent.DismissSendSuccess) },
        )
    }

    if (showPdfViewer) {
        TaminPdfViewer(
            fileName = "edict_${state.selectedPensionerId}_${state.startDate}.pdf",
            pdf = state.viewerPdf,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = { viewModel.sendIntent(EdictIntent.DownloadPdf) },
            onDismiss = {
                showPdfViewer = false
                viewModel.sendIntent(EdictIntent.DismissPdfViewer)
            },
        )
    }
}

// ─── Event handler ────────────────────────────────────────────────────────────

@Composable
fun HandleEdictEvents(
    events: Flow<EdictEvent>,
    onShowToast: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is EdictEvent.ShowToast -> onShowToast(event.message)
        }
    }
}

// ─── Stateless content ────────────────────────────────────────────────────────

@Composable
fun EdictContent(
    state: EdictUiState,
    onIntent: (EdictIntent) -> Unit,
    onBack: () -> Unit,
    onDownloadPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val edict = state.edictPensioner
    val scrollState = rememberScrollState()
    val collapse = rememberCollapsingHeaderState(120.dp)
    var headerHeightPx by remember { mutableStateOf(0) }

    Box(modifier = modifier.fillMaxSize().background(taminColors.bgPage)) {
        // Scrollable body: a spacer matching the floating header's measured height,
        // then the secondary cards (comparison, survivors, details).
        // nestedScroll is applied first so the header folds before the body scrolls.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection)
                .verticalScroll(scrollState),
        ) {
            Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            if (edict != null) {
                val staggerKey = state.selectedPensionerId to state.startDate
                val staggerState = rememberStaggeredEntranceState(key = staggerKey)

                Column(
                    modifier = Modifier
                        .padding(horizontal = Spacing.lg)
                        .padding(top = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    EdictComparisonCard(
                        edict,
                        modifier = Modifier.staggeredItemEntrance(
                            index = 0,
                            key = "comparison_$staggerKey",
                            state = staggerState,
                        ),
                    )
                    edict.survivorInfo.forEachIndexed { i, survivor ->
                        SurvivorShareCard(
                            survivor,
                            modifier = Modifier.staggeredItemEntrance(
                                index = i + 1,
                                key = "survivor_${i}_$staggerKey",
                                state = staggerState,
                            ),
                        )
                    }
                    EdictDetailsSection(
                        edict,
                        modifier = Modifier.staggeredItemEntrance(
                            index = edict.survivorInfo.size + 1,
                            key = "details_$staggerKey",
                            state = staggerState,
                        ),
                    )
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

        // Floating header (TaminTopAppBar + EdictMainCard or EdictEmptyCard).
        // Measured via onSizeChanged so the spacer above stays in sync.
        EdictHeader(
            state = state,
            onBack = onBack,
            onIntent = onIntent,
            collapseProgress = collapse.progressProvider,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        )

        if (edict != null) {
            EdictBottomBar(
                onSendToInbox = { onIntent(EdictIntent.RequestSendToInbox) },
                onDownloadPdf = onDownloadPdf,
                isSending = state.isSendingToInbox,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        if (state.isLoading) {
            LoadingStateOverlay()
        }
    }
}

// ─── Bottom action bar ────────────────────────────────────────────────────────

@Composable
private fun EdictBottomBar(
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
                color = taminColors.border
            ).clickable { onDownloadPdf() },
            painter = painterResource(Res.drawable.ic_tamin_download),
            size = 52.dp,
            backgroundColor = taminColors.bgSurface,
            cornerRadius = CornerRadius.lg,
            colorFilter = ColorFilter.tint(color = taminColors.blueText)
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

// ─── Preview ──────────────────────────────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun EdictContentPreview() {
    PreviewRtlThemeContent {
        EdictContent(
            state = EdictUiState(
                selectedPensionerId = "1003406938",
                startDate = "139905",
            ),
            onIntent = {},
            onBack = {},
            onDownloadPdf = {},
        )
    }
}
