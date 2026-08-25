package com.tamin.taminhamrah.feature.workshops.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopStatsCard
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopStats
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.detail.WorkshopDetailScreen
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.feature.workshops.ui.sheets.WorkshopFilterSheet
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
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
    onOpenAction: (WorkshopAction, String, String, String) -> Unit,
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
        WorkshopDetailScreen(
            workshop = workshop,
            onBack = { onIntent(WorkshopsIntent.DetailDismissed) },
            onAction = { action -> onIntent(WorkshopsIntent.ActionSelected(action, workshop)) },
            modifier = modifier,
        )
        return
    }

    val colors = LocalTaminColors.current
    val headerGradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    val workshops = state.workshops
    val stats = state.stats
    val isSearchOpen = state.isSearchOpen
    val hasActiveFilter = state.hasActiveFilter

    Column(modifier = modifier.fillMaxSize()) {
        TaminTopAppBar(
            title = stringResource(Res.string.workshops_title),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    contentDescription = stringResource(Res.string.workshop_search),
                    onClick = { onIntent(WorkshopsIntent.SearchOpenChanged(!isSearchOpen)) },
                )
            },
            background = headerGradient,
            bottomPadding = HeaderBottomPadding,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_tamin_workshop))
                Text(
                    text = stringResource(Res.string.workshops_header_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textHeaderSubtitle,
                    textAlign = TextAlign.Center,
                )
            }
        }

        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { onIntent(WorkshopsIntent.LoadMore) },
            contentPadding = PaddingValues(
                start = Spacing.page,
                end = Spacing.page,
                bottom = Spacing.page,
            ),
            key = { it.workshopId + it.branchCode },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                    WorkshopStatsCard(
                        stats = stats,
                        modifier = Modifier.rideUpIntoHeader(
                            progress = { 0f },
                            expandedOverlap = StatsCardOverlap,
                            collapsedOverlap = StatsCardOverlap,
                        ),
                    )
                    if (isSearchOpen) {
                        WorkshopSearchPanel(
                            workshopId = state.workshopIdInput,
                            branchCode = state.branchCodeInput,
                            onWorkshopIdChange = { onIntent(WorkshopsIntent.WorkshopIdChanged(it)) },
                            onBranchCodeChange = { onIntent(WorkshopsIntent.BranchCodeChanged(it)) },
                            onSearch = { onIntent(WorkshopsIntent.ApplySearch) },
                            onClear = { onIntent(WorkshopsIntent.ClearSearch) },
                        )
                    }
                    WorkshopSectionHeader(
                        count = workshops.size,
                        isFilterActive = hasActiveFilter,
                        onFilterClick = { onIntent(WorkshopsIntent.FilterSheetOpenChanged(true)) },
                    )
                }
            },
        ) { workshop ->
            WorkshopCard(
                workshop = workshop,
                onOpenDetails = { onIntent(WorkshopsIntent.DetailRequested(workshop)) },
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

/** The design lifts the stats card 42px into the header (`margin:-42px 0 15px`). */
private val StatsCardOverlap = 42.dp
private val HeaderBottomPadding = 64.dp

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
