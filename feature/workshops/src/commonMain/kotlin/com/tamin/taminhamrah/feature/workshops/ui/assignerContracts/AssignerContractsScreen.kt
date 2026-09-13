package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerFilterBar
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerSearchSheet
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerSearchSheetContent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.buildAssignerFilterText
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractFilter
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractTab
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsUiState
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.components.ContractRowCard
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.StatColumn
import com.tamin.taminhamrah.ui.components.TaminSegmentedTabs
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_bases_title
import taminx.core.core_ui.assigner_contract_date
import taminx.core.core_ui.assigner_contract_detail_title
import taminx.core.core_ui.assigner_contracts_subtitle
import taminx.core.core_ui.assigner_contracts_title
import taminx.core.core_ui.assigner_count_label
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
 * Opens straight onto every one of them, split into جاری and خاتمه‌یافته; a search narrows the list
 * rather than being the way into it. From جزئیات کارگاه the route carries the workshop, and the list
 * opens already narrowed to it.
 */
@Composable
fun AssignerContractsScreen(
    viewModel: AssignerContractsViewModel,
    onBack: () -> Unit,
    onOpenDetail: (AssignerContractPR) -> Unit,
    onOpenBases: (AssignerContractPR) -> Unit,
    modifier: Modifier = Modifier,
    workshopId: String = "",
    branchCode: String = "",
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(AssignerContractsIntent.Open(workshopId, branchCode))
    }

    AssignerContractsContent(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        onOpenDetail = onOpenDetail,
        onOpenBases = onOpenBases,
        modifier = modifier,
    )
}

@Composable
fun AssignerContractsContent(
    state: AssignerContractsUiState,
    onIntent: (AssignerContractsIntent) -> Unit,
    onBack: () -> Unit,
    onOpenDetail: (AssignerContractPR) -> Unit,
    onOpenBases: (AssignerContractPR) -> Unit,
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
    val tab = state.tab

    // Narrowed on exactly the two inputs it is built from, so opening the sheet or typing in it
    // never re-filters the rows.
    val visible = remember(list, tab) { list.inTab(tab) }

    // Rows fade and rise in as they arrive; a new search or a new tab plays the entrance again,
    // later pages landing in one does not. Held in a remember because building it inline would
    // hand the scaffold a fresh value every recomposition.
    val entranceKey = remember(filter, tab) {
        "${filter?.workshopId}|${filter?.branchCode}|${filter?.contractRow}|$tab"
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

        // Keeps its place in every list state — skeleton, empty, failed — so switching tab or
        // clearing the search never takes the tabs off the screen while the rows below settle.
        val count = visible.items.size
        val header: @Composable () -> Unit = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                AssignerTabsRow(
                    selected = tab,
                    count = count,
                    onSelect = { onIntent(AssignerContractsIntent.TabSelected(it)) },
                )
                if (filter != null) {
                    AssignerFilterBar(
                        filterText = rememberAssignerFilterText(filter),
                        onClear = { onIntent(AssignerContractsIntent.ClearSearch) },
                    )
                }
            }
        }

        WorkshopListScaffold(
            state = visible,
            // Every page is fetched up front (AssignerContractsViewModel.loadAll), so the scroll
            // position has nothing left to ask for.
            onLoadMore = {},
            onRetry = { onIntent(AssignerContractsIntent.Retry) },
            entranceKey = entranceKey,
            // One workshop holds several پیمان and a ردیف repeats across workshops, so neither
            // alone is unique; the scaffold prefixes the index, which is what makes this safe as
            // an identity rather than merely as a hint. Delimited because the entrance animation
            // reads this key unprefixed, and "12"+""+"3" would otherwise equal "1"+"2"+"3".
            key = { "${it.card.workshopId}_${it.contractRow}_${it.contractSequence}" },
            header = header,
            empty = {
                // Only a search can be widened, so the line telling the user to check theirs shows
                // only while one is applied. Resolved either way, so the number of composable
                // calls does not change with it.
                val body = stringResource(Res.string.assigner_empty_not_found_body)
                EmptyStateMessage(
                    icon = vectorResource(Res.drawable.ic_tamin_assigner_contracts),
                    title = stringResource(Res.string.assigner_empty_not_found_title),
                    subtitle = body.takeIf { filter != null },
                    actionLabel = stringResource(Res.string.assigner_search_workshop),
                    onAction = {
                        onIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))
                    },
                    showIconTile = true,
                )
            },
        ) { contract, itemModifier ->
            // The same card ردیف‌های پیمان draws, with its two destinations offered on the card
            // itself rather than behind a sheet — one tap instead of three, and the same shape
            // every other کارگاه card here uses. `dateLabel` differs because the column does:
            // this endpoint sends تاریخ قرارداد where that one sends تاریخ تعهد.
            ContractRowCard(
                row = contract.card,
                showContact = true,
                dateLabel = Res.string.assigner_contract_date,
                modifier = itemModifier,
                buttons = {
                    WorkshopCardButton(
                        text = stringResource(Res.string.assigner_contract_detail_title),
                        tone = WorkshopCardButtonTone.PRIMARY,
                        onClick = { onOpenDetail(contract) },
                    )
                    WorkshopCardButton(
                        text = stringResource(Res.string.assigner_bases_title),
                        // A پیمان missing any of the four keys cannot address its own bases; the
                        // button is plainly unavailable and the line below says why, rather than
                        // opening a screen onto another contract's records.
                        tone = if (contract.canOpenBases) {
                            WorkshopCardButtonTone.OUTLINE
                        } else {
                            WorkshopCardButtonTone.DISABLED
                        },
                        onClick = { onOpenBases(contract) },
                    )
                },
            )
        }
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
            myWorkshops = state.myWorkshops,
            myWorkshopsTotal = state.myWorkshopsTotal,
            onWorkshopIdChange = { onIntent(AssignerContractsIntent.DraftWorkshopIdChanged(it)) },
            onBranchCodeChange = { onIntent(AssignerContractsIntent.DraftBranchCodeChanged(it)) },
            onContractRowChange = { onIntent(AssignerContractsIntent.DraftContractRowChanged(it)) },
            onQuickPick = { id, branch ->
                onIntent(AssignerContractsIntent.QuickPicked(id, branch))
            },
            onApply = { onIntent(AssignerContractsIntent.ApplySearch) },
            onReset = { onIntent(AssignerContractsIntent.ClearSearch) },
            onDismiss = {
                onIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = false))
            },
        )
    }
}

/**
 * جاری / خاتمه‌یافته, and how many پیمان the chosen one holds.
 *
 * The tabs are the first child, so on the RTL page they sit rightmost with the count tile at the
 * far end, as the design places them. The tile takes the strip's height rather than its own, so
 * the two read as one row.
 */
@Composable
private fun AssignerTabsRow(
    selected: AssignerContractTab,
    count: Int,
    onSelect: (AssignerContractTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        TaminSegmentedTabs(
            options = AssignerContractTab.all,
            selected = selected,
            onSelect = onSelect,
            label = { stringResource(it.label) },
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .taminSurface(CornerRadius.xl)
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.Center,
        ) {
            StatColumn(
                value = count.toString().toPersianDigits(),
                label = stringResource(Res.string.assigner_count_label),
            )
        }
    }
}

/**
 * The list narrowed to one tab, with its loading flags kept honest for what is left.
 *
 * Every page is fetched up front, so a tab can only be empty mid-load because its rows are on a page
 * that has not landed yet: it shimmers rather than claiming there is nothing, and the footer shimmer
 * shows only under rows that exist.
 */
private fun PagedListState<AssignerContractPR>.inTab(
    tab: AssignerContractTab,
): PagedListState<AssignerContractPR> {
    val rows = items.filter(tab::includes).toImmutableList()
    return copy(
        items = rows,
        isLoading = isLoading || (rows.isEmpty() && isLoadingMore),
        isLoadingMore = isLoadingMore && rows.isNotEmpty(),
        hasMore = false,
    )
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
 * The rows a preview draws, run through the real mapper.
 *
 * Built from domain models rather than pre-formatted cards, so the Persian digits, the separated
 * dates and the tab each پیمان lands in all come from the code the app runs — a preview cannot keep
 * looking right after any of them breaks. One end date is far in the future, the other long past.
 */
private val PreviewContracts = persistentListOf(
    AssignerContractDN(
        contractRow = "1",
        contractSequence = "1",
        contractNumber = "44122",
        contractDate = "14010210",
        contractEndDate = "15000101",
        contractSubject = "خدمات نظافت و پشتیبانی",
        employer = AssignerPartyDN(
            workshopId = "9028212822",
            workshopName = "دبستان کارن ۲ مجتبی غلامیان",
            address = "بجنورد، خیابان طالقانی، کوچهٔ ۱۲، پلاک ۴",
            branchCode = "0210",
        ),
    ),
    AssignerContractDN(
        contractRow = "3",
        // No sequence — the bases action is offered disabled, with its reason.
        contractSequence = "",
        contractNumber = "45200",
        contractDate = "14030120",
        contractEndDate = "14030601",
        contractSubject = "پیمان با کارکرد ارزی",
        employer = AssignerPartyDN(
            workshopId = "9007441260",
            workshopName = "شرکت راه‌سازی البرز شرق",
            branchCode = "0210",
        ),
    ),
).map { it.toPresentation() }.toImmutableList()

private val PreviewFilledState = AssignerContractsUiState(
    hasApplied = true,
    list = PagedListState(items = PreviewContracts, total = 2),
)

@PreviewRtlTheme
@Composable
private fun AssignerContractsActivePreview() = PreviewRtlThemeContent {
    AssignerContractsContent(
        state = PreviewFilledState,
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

@PreviewRtlTheme
@Composable
private fun AssignerContractsFinishedPreview() = PreviewRtlThemeContent {
    AssignerContractsContent(
        state = PreviewFilledState.copy(tab = AssignerContractTab.FINISHED),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

/** A search applied on top of the full list — the chip under the tabs, and «حذف» beside it. */
@PreviewRtlTheme
@Composable
private fun AssignerContractsSearchedPreview() = PreviewRtlThemeContent {
    AssignerContractsContent(
        state = PreviewFilledState.copy(
            filter = AssignerContractFilter(workshopId = "9028212822", branchCode = "0210"),
        ),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

/** No search and nothing held — the empty state without a filter to tell the user to check. */
@PreviewRtlTheme
@Composable
private fun AssignerContractsNoneHeldPreview() = PreviewRtlThemeContent {
    AssignerContractsContent(
        state = AssignerContractsUiState(hasApplied = true),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

/** Searched, and the service answered with nothing — the wording that asks to check the search. */
@PreviewRtlTheme
@Composable
private fun AssignerContractsEmptySearchPreview() = PreviewRtlThemeContent {
    AssignerContractsContent(
        state = PreviewFilledState.copy(
            filter = AssignerContractFilter(workshopId = "9028212822"),
            list = PagedListState(),
        ),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
    )
}

@PreviewRtlTheme
@Composable
private fun AssignerContractsLoadingPreview() = PreviewRtlThemeContent {
    AssignerContractsContent(
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
    AssignerContractsContent(
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
        myWorkshops = PreviewWorkshops,
        myWorkshopsTotal = 8,
        onWorkshopIdChange = {},
        onBranchCodeChange = {},
        onContractRowChange = {},
        onQuickPick = { _, _ -> },
        onApply = {},
        onReset = {},
    )
}

/** کارگاه‌های شما as the sheet offers them — two of the employer's eight, so the shortfall shows. */
private val PreviewWorkshops = persistentListOf(
    WorkshopPR(
        workshopId = "9028212822",
        branchCode = "6310",
        name = "دبستان کارن ۲ مجتبی غلامیان",
        codeLabel = "۹۰۲۸۲۱۲۸۲۲",
    ),
    WorkshopPR(
        workshopId = "6318210573",
        branchCode = "6310",
        name = "دبستان غیر دولتی کارن",
        codeLabel = "۶۳۱۸۲۱۰۵۷۳",
    ),
)
