package com.tamin.taminhamrah.feature.workshops.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchDialog
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopStatsCard
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopStats
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.detail.WorkshopDetailScreen
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.sheets.WorkshopFilterSheet
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.article_sixteen_no_debt_body
import taminx.core.core_ui.article_sixteen_no_debt_title
import taminx.core.core_ui.btn_understood
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.ic_tamin_workshop
import taminx.core.core_ui.workshop_search
import taminx.core.core_ui.workshops_header_subtitle
import taminx.core.core_ui.workshops_title

/**
 * کارگاه‌های کارفرما — the launcher for every workshop service.
 */
@Composable
fun WorkshopsRoute(
    onBack: () -> Unit,
    onOpenAction: (WorkshopsEvent.Navigate) -> Unit,
    viewModel: WorkshopsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HandleWorkshopsEvents(
        events = viewModel.events,
        onOpenAction = onOpenAction,
    )

    WorkshopsScreen(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
    )
}

@Composable
fun WorkshopsScreen(
    state: WorkshopsUiState,
    onIntent: (WorkshopsIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // جزئیات کارگاه is the same destination in the design's own model: picking a workshop
    // swaps the page, and back returns to the list. Everything it draws already traveled
    // with the workshop, so it costs no request and needs no route of its own.
    state.detailFor?.let { workshop ->
        BackHandler { onIntent(WorkshopsIntent.DetailDismissed) }
        Box(modifier = modifier) {
            WorkshopDetailScreen(
                workshop = workshop,
                actions = state.availableActions,
                onBack = { onIntent(WorkshopsIntent.DetailDismissed) },
                onAction = { action -> onIntent(WorkshopsIntent.ActionSelected(action, workshop)) },
            )
            // رسیدگی به بدهی ماده ۱۶ asks for the workshop's debts before it opens.
            if (state.isCheckingDebts) LoadingStateOverlay()
        }
        if (state.isNoDebtDialogOpen) {
            TaminConfirmationDialog(
                title = stringResource(Res.string.article_sixteen_no_debt_title),
                description = stringResource(Res.string.article_sixteen_no_debt_body),
                icon = Icons.Outlined.Info,
                confirmButton = {
                    TaminFilledButton(
                        background = LocalTaminColors.current.buttonGradient,
                        text = stringResource(Res.string.btn_understood),
                        onClick = { onIntent(WorkshopsIntent.NoDebtDialogDismissed) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                dismissButton = {},
                onDismissRequest = { onIntent(WorkshopsIntent.NoDebtDialogDismissed) },
            )
        }
        return
    }

    val workshops = state.workshops
    val stats = state.stats
    val isSearchOpen = state.isSearchOpen
    val hasActiveFilter = state.hasActiveFilter
    val onSearchClick = { onIntent(WorkshopsIntent.SearchOpenChanged(!isSearchOpen)) }

    // The list's own drag folds the ring icon and subtitle away, snapping on release; the stats
    // strip is never wrapped in a topArea behavior, so it stays pinned, unchanged, under the slim
    // bar. Same shape as ObjectionStatusScreen — see docs/vault/TopArea-System.md.
    val topArea = rememberMeasuredTopAreaState { topAreaState ->
        WorkshopsTopArea(
            stats = stats,
            onBack = onBack,
            onSearchClick = onSearchClick,
            topAreaState = topAreaState,
        )
    }
    val listState = rememberLazyListState()

    // Overlaid rather than a Column, so the list passes underneath the header as it scrolls.
    Box(modifier = modifier.fillMaxSize()) {
        WorkshopListScaffold(
            emptyIcon = vectorResource(Res.drawable.ic_tamin_workshop),
            state = state.list,
            listState = listState,
            modifier = Modifier.fillMaxSize().driveTopArea(topArea, listState),
            contentPadding = topAreaContentPadding(state = topArea, rest = WorkshopDimens.listContentPadding),
            onLoadMore = { onIntent(WorkshopsIntent.LoadMore) },
            onRetry = { onIntent(WorkshopsIntent.Load) },
            key = { "${it.workshopId}_${it.branchCode}" },
            header = {
                WorkshopSectionHeader(
                    count = workshops.size,
                    isFilterActive = hasActiveFilter,
                    onFilterClick = { onIntent(WorkshopsIntent.FilterSheetOpenChanged(true)) },
                )
            },
        ) { workshop, itemModifier ->
            WorkshopCard(
                workshop = workshop,
                onOpenDetails = { onIntent(WorkshopsIntent.DetailRequested(workshop)) },
                modifier = itemModifier,
            )
        }

        WorkshopsTopArea(
            stats = stats,
            onBack = onBack,
            onSearchClick = onSearchClick,
            topAreaState = topArea,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )
    }

    if (isSearchOpen) {
        WorkshopSearchDialog(
            onDismiss = { onIntent(WorkshopsIntent.SearchOpenChanged(false)) },
        ) {
            WorkshopSearchPanel(
                workshopId = state.workshopIdInput,
                branchCode = state.branchCodeInput,
                onWorkshopIdChange = { onIntent(WorkshopsIntent.WorkshopIdChanged(it)) },
                onBranchCodeChange = { onIntent(WorkshopsIntent.BranchCodeChanged(it)) },
                onSearch = { onIntent(WorkshopsIntent.ApplySearch) },
                onClear = { onIntent(WorkshopsIntent.ClearSearch) },
            )
        }
    }

    if (state.isFilterSheetOpen) {
        WorkshopFilterSheet(
            selected = state.statusFilter,
            onDismiss = { onIntent(WorkshopsIntent.FilterSheetOpenChanged(false)) },
            onSelect = { status -> onIntent(WorkshopsIntent.StatusFilterChanged(status)) },
        )
    }
}

/**
 * The list's floating top area: the gradient bar, whose ring icon and subtitle fold away, and the
 * stats strip riding [WorkshopDimens.statsCardOverlap] up into it, pinned there at every fold.
 */
@Composable
private fun WorkshopsTopArea(
    stats: WorkshopStats?,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val headerGradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }
    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = stringResource(Res.string.workshops_title),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                    bordered = true,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    contentDescription = stringResource(Res.string.workshop_search),
                    onClick = onSearchClick,
                    bordered = true,
                )
            },
            background = headerGradient,
            bottomPadding = WorkshopDimens.headerBottomPadding,
        ) {
            // Hidden together with its top gap, so the folded bar closes up under the title row.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .topAreaHide(topAreaState)
                    .padding(top = Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                // Static while this is one of rememberMeasuredTopAreaState's off-screen probes.
                AnimatedRingHeaderIcon(
                    icon = vectorResource(Res.drawable.ic_tamin_workshop),
                    animated = !topAreaState.isMeasureProbe,
                )
                Text(
                    text = stringResource(Res.string.workshops_header_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textHeaderSubtitle,
                    textAlign = TextAlign.Center,
                )
            }
        }

        // Rides up into the bar's reserved bottom space and reports a height reduced by the same
        // overlap, so reportTopAreaHeight sees the block's true footprint, not the overlap twice.
        WorkshopStatsCard(
            stats = stats,
            modifier = Modifier
                .padding(horizontal = Spacing.page)
                .rideUpIntoHeader(
                    progress = { 0f },
                    expandedOverlap = WorkshopDimens.statsCardOverlap,
                    collapsedOverlap = WorkshopDimens.statsCardOverlap,
                ),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopsScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopsScreen(
            state = WorkshopsUiState(
                list = PagedListState(
                    items = persistentListOf(
                        WorkshopPR(
                            workshopId = "9900020917749",
                            branchCode = "123",
                            name = "کارگاه کامپیوتر توکلی",
                            branchOfficeName = "شعبه ۱ تهران",
                        )
                    )
                ),
                stats = WorkshopStats(total = 5, active = 4),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
