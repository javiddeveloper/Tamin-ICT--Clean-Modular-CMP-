package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerActionSheet
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerActionSheetContent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerFilterBar
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerSearchSheet
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerSearchSheetContent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.buildAssignerFilterText
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractFilter
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsEvent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsUiState
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.components.ContractRowCard
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_contract_date
import taminx.core.core_ui.assigner_contracts_subtitle
import taminx.core.core_ui.assigner_contracts_title
import taminx.core.core_ui.assigner_empty_no_search_body
import taminx.core.core_ui.assigner_empty_no_search_title
import taminx.core.core_ui.assigner_empty_not_found_body
import taminx.core.core_ui.assigner_empty_not_found_title
import taminx.core.core_ui.assigner_filter_branch
import taminx.core.core_ui.assigner_filter_row
import taminx.core.core_ui.assigner_filter_workshop
import taminx.core.core_ui.assigner_search_workshop
import taminx.core.core_ui.ic_tamin_assigner_contracts
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search

/**
 * واگذارندگان — the پیمان‌ها the signed-in employer assigned out.
 *
 * Reached two ways: from the services grid, where no workshop is known and the search sheet opens
 * first, and from جزئیات کارگاه, where the route carries the identity and the list loads at once.
 *
 * The list is gated on a search. With no کد کارگاه applied nothing is fetched and the empty state
 * asks for one — a different sentence from the one shown after a search that found nothing, and
 * the two are deliberately worded apart.
 */
@Composable
fun AssignerContractsRoute(
    viewModel: AssignerContractsViewModel,
    onBack: () -> Unit,
    onOpenDetail: () -> Unit,
    onOpenBases: () -> Unit,
    modifier: Modifier = Modifier,
    workshopId: String = "",
    branchCode: String = "",
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(AssignerContractsIntent.Open(workshopId, branchCode))
    }

    HandleAssignerContractsEvents(viewModel.events)

    AssignerContractsScreen(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        onOpenDetail = onOpenDetail,
        onOpenBases = onOpenBases,
        modifier = modifier,
    )
}

/**
 * The one effect the flow raises.
 *
 * Every screen in the flow mounts this, because a document can fail on جزئیات مبنا and a پیمان can
 * refuse its bases on the list; the toaster is the same either way, so whichever screen is
 * composed when the message arrives shows it.
 */
@Composable
fun HandleAssignerContractsEvents(events: Flow<AssignerContractsEvent>) {
    val toaster = LocalToaster.current
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is AssignerContractsEvent.ShowMessage -> toaster.error(getString(event.message))
            }
        }
    }
}

@Composable
fun AssignerContractsScreen(
    state: AssignerContractsUiState,
    onIntent: (AssignerContractsIntent) -> Unit,
    onBack: () -> Unit,
    onOpenDetail: () -> Unit,
    onOpenBases: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val headerGradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    // Read off state once, so no child is handed the whole thing and recomposed by a field it does
    // not draw — the list must not rebuild because the search sheet opened.
    val list = state.list
    val filter = state.filter
    val actionSheetFor = state.actionSheetFor

    // Rows fade and rise in as they arrive; a new search plays the entrance again, paging further
    // into one does not. Held in a remember because building it inline would hand the scaffold a
    // fresh value every recomposition.
    val entranceKey = remember(filter) {
        "${filter?.workshopId}|${filter?.branchCode}|${filter?.contractRow}"
    }

    Column(modifier = modifier.fillMaxSize()) {
        TaminTopAppBar(
            title = stringResource(Res.string.assigner_contracts_title),
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
                    contentDescription = stringResource(Res.string.assigner_search_workshop),
                    onClick = { onIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true)) },
                )
            },
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedRingHeaderIcon(
                    icon = vectorResource(Res.drawable.ic_tamin_assigner_contracts),
                )
                Text(
                    text = stringResource(Res.string.assigner_contracts_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textHeaderSubtitle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = Spacing.sm),
                )
            }
        }

        // Keeps its place in all four list states, so clearing the search never takes the chip off
        // the screen before the empty state explains why.
        val header: (@Composable () -> Unit)? = filter?.let {
            {
                AssignerFilterBar(
                    filterText = rememberAssignerFilterText(it),
                    onClear = { onIntent(AssignerContractsIntent.ClearSearch) },
                )
            }
        }

        WorkshopListScaffold(
            state = list,
            onLoadMore = { onIntent(AssignerContractsIntent.LoadMore) },
            onRetry = { onIntent(AssignerContractsIntent.Retry) },
            entranceKey = entranceKey,
            // One workshop holds several پیمان and a ردیف repeats across workshops, so neither
            // alone is unique; the scaffold prefixes the index, which is what makes this safe as
            // an identity rather than merely as a hint.
            key = { it.card.workshopId + it.contractRow + it.contractSequence },
            header = header,
            empty = {
                // Two empty states, because they mean different things: nothing has been searched
                // for yet, versus searched and answered with nothing.
                val hasFilter = filter != null
                EmptyStateMessage(
                    icon = vectorResource(Res.drawable.ic_tamin_assigner_contracts),
                    title = stringResource(
                        if (hasFilter) {
                            Res.string.assigner_empty_not_found_title
                        } else {
                            Res.string.assigner_empty_no_search_title
                        }
                    ),
                    subtitle = stringResource(
                        if (hasFilter) {
                            Res.string.assigner_empty_not_found_body
                        } else {
                            Res.string.assigner_empty_no_search_body
                        }
                    ),
                    actionLabel = stringResource(Res.string.assigner_search_workshop),
                    onAction = {
                        onIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))
                    },
                    showIconTile = true,
                )
            },
        ) { contract, itemModifier ->
            // The same card ردیف‌های پیمان draws, tapping opens the action sheet rather than
            // navigating. `dateLabel` differs because the column does: this endpoint sends
            // تاریخ قرارداد where that one sends تاریخ تعهد.
            ContractRowCard(
                row = contract.card,
                showContact = true,
                dateLabel = Res.string.assigner_contract_date,
                onClick = { onIntent(AssignerContractsIntent.ContractTapped(contract)) },
                modifier = itemModifier,
            )
        }
    }

    if (actionSheetFor != null) {
        AssignerActionSheet(
            workshopName = actionSheetFor.card.name,
            rowLabel = actionSheetFor.card.rowLabel,
            canOpenBases = actionSheetFor.canOpenBases,
            onViewDetail = {
                onIntent(AssignerContractsIntent.ActionSheetDismissed)
                onOpenDetail()
            },
            onViewBases = {
                onIntent(AssignerContractsIntent.ActionSheetDismissed)
                onOpenBases()
            },
            onDismiss = { onIntent(AssignerContractsIntent.ActionSheetDismissed) },
        )
    }

    if (state.isSearchOpen) {
        val draft = state.draft
        AssignerSearchSheet(
            workshopId = draft.workshopId,
            branchCode = draft.branchCode,
            contractRow = draft.contractRow,
            showWorkshopIdError = draft.showWorkshopIdError,
            isApplying = list.isLoading,
            canReset = filter != null,
            onWorkshopIdChange = { onIntent(AssignerContractsIntent.DraftWorkshopIdChanged(it)) },
            onBranchCodeChange = { onIntent(AssignerContractsIntent.DraftBranchCodeChanged(it)) },
            onContractRowChange = { onIntent(AssignerContractsIntent.DraftContractRowChanged(it)) },
            onApply = { onIntent(AssignerContractsIntent.ApplySearch) },
            onReset = { onIntent(AssignerContractsIntent.ClearSearch) },
            onDismiss = {
                onIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = false))
            },
        )
    }
}

/**
 * «کد کارگاه X · کد شعبه Y · ردیف Z», with the parts the user left blank dropped.
 *
 * All three labels are resolved unconditionally and the *values* decide what is joined, so the
 * number of composable calls does not change between recompositions. The join itself is a pure
 * function so it can be tested without a resource loader.
 */
@Composable
private fun rememberAssignerFilterText(filter: AssignerContractFilter): String {
    val workshop = stringResource(
        Res.string.assigner_filter_workshop,
        filter.workshopId.toPersianDigits(),
    )
    val branch = stringResource(
        Res.string.assigner_filter_branch,
        filter.branchCode.toPersianDigits(),
    )
    val row = stringResource(Res.string.assigner_filter_row, filter.contractRow.toPersianDigits())
    return remember(filter, workshop, branch, row) {
        buildAssignerFilterText(
            workshop = workshop,
            branch = branch.takeIf { filter.branchCode.isNotBlank() },
            row = row.takeIf { filter.contractRow.isNotBlank() },
        )
    }
}

// ------------------------------------------------------------------------------- previews

/**
 * The rows a preview draws.
 *
 * Shaped like the real mapper's output — Persian digits, separated Jalali dates, contact columns
 * left blank because this endpoint sends none — so a preview cannot keep looking right after the
 * formatting changes underneath it.
 */
private val PreviewContracts = persistentListOf(
    AssignerContractPR(
        card = ContractRowPR(
            workshopId = "9028212822",
            branchCode = "0210",
            name = "دبستان کارن ۲ مجتبی غلامیان",
            rowLabel = "۱",
            workshopCodeLabel = "۹۰۲۸۲۱۲۸۲۲",
            commitmentDate = "۱۴۰۱/۰۲/۱۰",
            address = "بجنورد، خیابان طالقانی، کوچهٔ ۱۲، پلاک ۴",
        ),
        contractRow = "1",
        contractSequence = "1",
        contractNumber = "۴۴۱۲۲",
        contractDate = "۱۴۰۱/۰۲/۱۰",
        contractSubject = "خدمات نظافت و پشتیبانی",
    ),
    AssignerContractPR(
        card = ContractRowPR(
            workshopId = "9007441260",
            branchCode = "0210",
            name = "شرکت راه‌سازی البرز شرق",
            rowLabel = "۳",
            workshopCodeLabel = "۹۰۰۷۴۴۱۲۶۰",
            commitmentDate = "۱۴۰۳/۰۱/۲۰",
        ),
        contractRow = "3",
        // No sequence — the bases action is offered disabled, with its reason.
        contractSequence = "",
        contractNumber = "۴۵۲۰۰",
        contractDate = "۱۴۰۳/۰۱/۲۰",
        contractSubject = "پیمان با کارکرد ارزی",
    ),
)

private val PreviewFilledState = AssignerContractsUiState(
    filter = AssignerContractFilter(workshopId = "9028212822", branchCode = "0210"),
    list = PagedListState(items = PreviewContracts, total = 2),
)

@PreviewRtlTheme
@Composable
private fun AssignerContractsFilledPreview() = PreviewRtlThemeContent {
    AssignerContractsScreen(
        state = PreviewFilledState,
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

/** Nothing searched for yet — the wording that asks for a workshop rather than reporting none. */
@PreviewRtlTheme
@Composable
private fun AssignerContractsNoSearchPreview() = PreviewRtlThemeContent {
    AssignerContractsScreen(
        state = AssignerContractsUiState(),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

/** Searched, and the service answered with nothing — the other wording. */
@PreviewRtlTheme
@Composable
private fun AssignerContractsEmptyPreview() = PreviewRtlThemeContent {
    AssignerContractsScreen(
        state = PreviewFilledState.copy(list = PagedListState()),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

@PreviewRtlTheme
@Composable
private fun AssignerContractsLoadingPreview() = PreviewRtlThemeContent {
    AssignerContractsScreen(
        state = PreviewFilledState.copy(list = PagedListState(isLoading = true)),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

/** The request failed — not the same as an empty list, and it offers «تلاش مجدد». */
@PreviewRtlTheme
@Composable
private fun AssignerContractsFailedPreview() = PreviewRtlThemeContent {
    AssignerContractsScreen(
        state = PreviewFilledState.copy(
            list = PagedListState(error = "ارتباط با سرویس برقرار نشد."),
        ),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

/** The search sheet, submitted blank. */
@PreviewRtlTheme
@Composable
private fun AssignerSearchSheetPreview() = PreviewRtlThemeContent {
    AssignerSearchSheetContent(
            workshopId = "",
            branchCode = "۱۲",
            contractRow = "",
            showWorkshopIdError = true,
            isApplying = false,
            canReset = true,
            onWorkshopIdChange = {},
            onBranchCodeChange = {},
            onContractRowChange = {},
            onApply = {},
            onReset = {},
        )
}

/** The action sheet, on a پیمان whose bases cannot be addressed. */
@PreviewRtlTheme
@Composable
private fun AssignerActionSheetPreview() = PreviewRtlThemeContent {
    AssignerActionSheetContent(
            workshopName = "شرکت راه‌سازی البرز شرق",
            rowLabel = "۳",
            canOpenBases = false,
            onViewDetail = {},
            onViewBases = {},
        )
}
