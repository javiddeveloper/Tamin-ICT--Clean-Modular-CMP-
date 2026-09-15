package com.tamin.taminhamrah.feature.workshops.ui.contractRows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.components.ContractRowCard
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.components.ContractRowFilterBar
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.components.ContractRowPickerSheet
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.components.ContractRowTabs
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowTab
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowsUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_count
import taminx.core.core_ui.contract_rows_empty_none_body
import taminx.core.core_ui.contract_rows_empty_none_title
import taminx.core.core_ui.contract_rows_empty_no_workshop_body
import taminx.core.core_ui.contract_rows_empty_no_workshop_title
import taminx.core.core_ui.contract_rows_filter_workshop
import taminx.core.core_ui.contract_rows_filter_workshop_and_branch
import taminx.core.core_ui.contract_rows_pick_workshop
import taminx.core.core_ui.contract_rows_subtitle
import taminx.core.core_ui.contract_rows_tab_switched
import taminx.core.core_ui.contract_rows_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.ic_tamin_workshop_contract_rows
import taminx.core.core_ui.workshop_search

/**
 * ردیف‌های پیمان — one workshop's contract rows, from either of the two services.
 *
 * Reached two ways, and identical either way: from the services grid, where no workshop is known
 * and the picker opens first, and from جزئیات کارگاه, where the route carries the identity and the
 * list loads straight away.
 *
 * The list is read-only. Nothing on a card responds to a tap, and nothing here gives one a ripple —
 * the old app's two adapters both took an `onItemClickListener` that neither ever called.
 */
@Composable
fun ContractRowsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopId: String = "",
    branchCode: String = "",
    viewModel: ContractRowsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(ContractRowsIntent.Open(workshopId, branchCode))
    }

    ContractRowsContent(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun ContractRowsContent(
    state: ContractRowsUiState,
    onIntent: (ContractRowsIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val headerGradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    // Read off state once, so no child is handed the whole thing and recomposed by a field it
    // does not draw — the tab strip must not rebuild because a page landed.
    val tab = state.tab
    val list = state.list
    val applied = state.applied

    // Rows fade and rise in as they arrive; a new tab or a new workshop plays the entrance again,
    // paging further into one does not. Held in a remember because building it inline would hand
    // the scaffold a fresh value every recomposition.
    val entranceKey = remember(tab, applied) {
        "$tab|${applied?.workshopId}|${applied?.branchCode}"
    }

    Column(modifier = modifier.fillMaxSize()) {
        TaminTopAppBar(
            title = stringResource(Res.string.contract_rows_title),
            background = headerGradient,
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
                    onClick = { onIntent(ContractRowsIntent.PickerOpenChanged(isOpen = true)) },
                )
            },
        ) {
            Text(
                text = stringResource(Res.string.contract_rows_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textHeaderSubtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            )
        }

        // The header keeps its place in all three list states, so switching tab or emptying the
        // list never takes the tabs off the screen.
        val header: @Composable () -> Unit = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                ContractRowTabs(
                    selected = tab,
                    onSelect = { onIntent(ContractRowsIntent.TabSelected(it)) },
                )
                if (applied != null) {
                    ContractRowFilterBar(
                        filterText = applied.branchCode.takeIf { it.isNotBlank() }?.let { branch ->
                            stringResource(
                                Res.string.contract_rows_filter_workshop_and_branch,
                                applied.workshopId.toPersianDigits(),
                                branch.toPersianDigits(),
                            )
                        } ?: stringResource(
                            Res.string.contract_rows_filter_workshop,
                            applied.workshopId.toPersianDigits(),
                        ),
                        // The service's own total, not how much of it has been paged in. The
                        // design's chip counts the whole result too, and a number that climbs
                        // while the user scrolls reads as the first one having been wrong.
                        countText = stringResource(
                            Res.string.contract_rows_count,
                            list.total.toString().toPersianDigits(),
                        ),
                        // Only after the screen moved the user itself; a tab they chose needs no
                        // explanation.
                        notice = stringResource(Res.string.contract_rows_tab_switched)
                            .takeIf { state.didAutoSwitchTab },
                        onChange = {
                            onIntent(ContractRowsIntent.PickerOpenChanged(isOpen = true))
                        },
                        onClear = {
                            onIntent(ContractRowsIntent.ClearPicker)
                        },
                    )
                }
            }
        }

        WorkshopListScaffold(
            state = list,
            onLoadMore = { onIntent(ContractRowsIntent.LoadMore) },
            onRetry = { onIntent(ContractRowsIntent.Retry) },
            entranceKey = entranceKey,
            // Neither service guarantees a unique field: one workshop holds several rows, and the
            // row number repeats across workshops. The scaffold prefixes the index, which is what
            // makes this safe as an identity rather than merely as a hint.
            key = { it.workshopId + it.rowLabel },
            header = header,
            empty = {
                // Two different empty states, because they mean different things: nothing has been
                // asked for yet, versus asked and answered with nothing.
                val hasWorkshop = applied != null
                EmptyStateMessage(
                    icon = vectorResource(Res.drawable.ic_tamin_workshop_contract_rows),
                    title = stringResource(
                        if (hasWorkshop) {
                            Res.string.contract_rows_empty_none_title
                        } else {
                            Res.string.contract_rows_empty_no_workshop_title
                        }
                    ),
                    subtitle = stringResource(
                        if (hasWorkshop) {
                            Res.string.contract_rows_empty_none_body
                        } else {
                            Res.string.contract_rows_empty_no_workshop_body
                        }
                    ),
                    actionLabel = stringResource(Res.string.contract_rows_pick_workshop),
                    onAction = { onIntent(ContractRowsIntent.PickerOpenChanged(isOpen = true)) },
                    showIconTile = true,
                )
            },
        ) { row, itemModifier ->
            ContractRowCard(
                row = row,
                showContact = tab == ContractRowTab.WITH_AGREEMENT,
                modifier = itemModifier,
            )
        }
    }

    if (state.isPickerOpen) {
        ContractRowPickerSheet(
            workshopId = state.draftWorkshopId,
            branchCode = state.draftBranchCode,
            showWorkshopIdError = state.showWorkshopIdError,
            showBranchCodeError = state.showBranchCodeError,
            isApplying = list.isLoading,
            myWorkshops = state.myWorkshops,
            myWorkshopsTotal = state.myWorkshopsTotal,
            canReset = applied != null,
            onWorkshopIdChange = { onIntent(ContractRowsIntent.DraftWorkshopIdChanged(it)) },
            onBranchCodeChange = { onIntent(ContractRowsIntent.DraftBranchCodeChanged(it)) },
            onQuickPick = { id, branch ->
                onIntent(ContractRowsIntent.QuickPicked(id, branch))
            },
            onApply = { onIntent(ContractRowsIntent.ApplyPicker) },
            onReset = { onIntent(ContractRowsIntent.ClearPicker) },
            onDismiss = { onIntent(ContractRowsIntent.PickerOpenChanged(isOpen = false)) },
        )
    }
}

// ------------------------------------------------------------------------------- previews

/**
 * The rows a preview draws.
 *
 * Built through the real mapper's own output shape — Persian digits, separated Jalali dates, the
 * em dash for what the service omitted — so a preview cannot keep looking right after the
 * formatting changes underneath it.
 */
private val PreviewRows = persistentListOf(
    ContractRowPR(
        workshopId = "9028212822",
        branchCode = "0210",
        name = "دبستان کارن ۲ مجتبی غلامیان",
        rowLabel = "۱",
        workshopCodeLabel = "۹۰۲۸۲۱۲۸۲۲",
        commitmentDate = "۱۴۰۱/۰۲/۱۰",
        mobile = "۰۹۱۴۳۰۱۸۳۷۲",
        email = "karan.school@mail.com",
        address = "بجنورد، خیابان طالقانی، کوچهٔ ۱۲، پلاک ۴",
    ),
    ContractRowPR(
        workshopId = "9014778315",
        branchCode = "0210",
        name = "آموزشگاه علمی آزاد کارن",
        rowLabel = "۳",
        workshopCodeLabel = "۹۰۱۴۷۷۸۳۱۵",
        commitmentDate = "۱۴۰۲/۰۸/۲۴",
        mobile = "—",
        email = "—",
    ),
)

private val PreviewFilledState = ContractRowsUiState(
    applied = com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract.ContractRowFilter(
        workshopId = "9028212822",
        branchCode = "0210",
    ),
    list = PagedListState(items = PreviewRows.toImmutableList()),
)

@PreviewRtlTheme
@Composable
private fun ContractRowsFilledPreview() = PreviewRtlThemeContent {
    ContractRowsContent(state = PreviewFilledState, onIntent = {}, onBack = {})
}

@PreviewRtlTheme
@Composable
private fun ContractRowsBasicTabPreview() = PreviewRtlThemeContent {
    ContractRowsContent(
        state = PreviewFilledState.copy(tab = ContractRowTab.WITHOUT_AGREEMENT),
        onIntent = {},
        onBack = {},
    )
}

/** Nothing chosen yet — the wording that asks for a workshop rather than reporting none found. */
@PreviewRtlTheme
@Composable
private fun ContractRowsNoWorkshopPreview() = PreviewRtlThemeContent {
    ContractRowsContent(state = ContractRowsUiState(), onIntent = {}, onBack = {})
}

/** Chosen, and the service answered with nothing — the other wording. */
@PreviewRtlTheme
@Composable
private fun ContractRowsEmptyPreview() = PreviewRtlThemeContent {
    ContractRowsContent(
        state = PreviewFilledState.copy(list = PagedListState()),
        onIntent = {},
        onBack = {},
    )
}

@PreviewRtlTheme
@Composable
private fun ContractRowsLoadingPreview() = PreviewRtlThemeContent {
    ContractRowsContent(
        state = PreviewFilledState.copy(list = PagedListState(isLoading = true)),
        onIntent = {},
        onBack = {},
    )
}
