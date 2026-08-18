package com.tamin.taminhamrah.feature.workshops.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopStatsCard
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.feature.workshops.ui.sheets.WorkshopActionsSheet
import com.tamin.taminhamrah.feature.workshops.ui.sheets.WorkshopFilterSheet
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_search
import taminx.core.core_ui.workshops_header_subtitle
import taminx.core.core_ui.workshops_title

/**
 * کارگاه‌های کارفرما — the launcher for every workshop service.
 *
 * The route collects state and turns events into navigation; [WorkshopsScreen] is stateless, so a
 * preview can exercise every state without a ViewModel.
 */
@Composable
fun WorkshopsRoute(
    onBack: () -> Unit,
    onOpenAction: (WorkshopAction, String, String, String) -> Unit,
    viewModel: WorkshopsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HandleWorkshopsEvents(events = viewModel.events, onOpenAction = onOpenAction)

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
    val colors = LocalTaminColors.current
    // Hoisted out of the item lambdas below: reading these from `state` inside a row would capture
    // the whole state, and every workshop would recompose whenever any unrelated field changed.
    val workshops = state.workshops
    val stats = state.stats
    val isSearchOpen = state.isSearchOpen
    val hasActiveFilter = state.hasActiveFilter

    Column(modifier = modifier.fillMaxSize()) {
        TaminTopAppBar(
            title = stringResource(Res.string.workshops_title),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    onClick = onBack,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.workshop_search),
                    onClick = { onIntent(WorkshopsIntent.SearchOpenChanged(!isSearchOpen)) },
                )
            },
            bottomPadding = HeaderBottomPadding,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                AnimatedRingHeaderIcon(icon = Icons.Default.Search)
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
                    // Climbs into the gradient above it, the way the design overlaps the two.
                    // A negative padding throws; this borrows the space during layout instead, and
                    // gives back exactly as much below as it takes above.
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
                onOpenDetails = { onIntent(WorkshopsIntent.ActionsRequested(workshop)) },
            )
        }
    }

    state.actionsFor?.let { workshop ->
        WorkshopActionsSheet(
            workshop = workshop,
            onDismiss = { onIntent(WorkshopsIntent.ActionsDismissed) },
            onAction = { action -> onIntent(WorkshopsIntent.ActionSelected(action, workshop)) },
        )
    }

    if (state.isFilterSheetOpen) {
        WorkshopFilterSheet(
            selected = state.statusFilter,
            onDismiss = { onIntent(WorkshopsIntent.FilterSheetOpenChanged(false)) },
            onSelect = { status -> onIntent(WorkshopsIntent.StatusFilterChanged(status)) },
        )
    }
}

/** How far the stats card climbs into the gradient header. */
private val StatsCardOverlap = 40.dp

/** Deep enough for the ring icon, the subtitle, and the card that overlaps them. */
private val HeaderBottomPadding = 64.dp
