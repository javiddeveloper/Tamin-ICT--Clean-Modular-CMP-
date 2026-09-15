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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerFilterBar
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerSearchSheet
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerSearchSheetContent
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.buildAssignerFilterText
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractFilter
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractTab
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsEvent
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
import com.tamin.taminhamrah.model.workshop.AssignerPartyPR
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.StatColumn
import com.tamin.taminhamrah.ui.components.StatDivider
import com.tamin.taminhamrah.ui.components.StatRowCard
import com.tamin.taminhamrah.ui.components.TaminSegmentedTabs
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.info
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_action_bases
import taminx.core.core_ui.assigner_action_certificate
import taminx.core.core_ui.assigner_action_detail
import taminx.core.core_ui.assigner_action_settlement
import taminx.core.core_ui.assigner_certificate_issued
import taminx.core.core_ui.assigner_certificate_not_found
import taminx.core.core_ui.assigner_contract_date
import taminx.core.core_ui.assigner_contracts_title
import taminx.core.core_ui.assigner_count_label
import taminx.core.core_ui.assigner_empty_active_title
import taminx.core.core_ui.assigner_empty_finished_title
import taminx.core.core_ui.assigner_empty_not_found_body
import taminx.core.core_ui.assigner_empty_not_found_title
import taminx.core.core_ui.assigner_empty_tab_body
import taminx.core.core_ui.assigner_filter_branch
import taminx.core.core_ui.assigner_filter_row
import taminx.core.core_ui.assigner_filter_workshop
import taminx.core.core_ui.assigner_me_label
import taminx.core.core_ui.assigner_select_workshop
import taminx.core.core_ui.ic_tamin_assigner_contracts
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.workshop_code

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
    onRequestSettlement: (AssignerContractPR) -> Unit,
    modifier: Modifier = Modifier,
    workshopId: String = "",
    branchCode: String = "",
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(AssignerContractsIntent.Open(workshopId, branchCode))
    }
    HandleAssignerContractsEvents(events = viewModel.events)

    AssignerContractsContent(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        onOpenDetail = onOpenDetail,
        onOpenBases = onOpenBases,
        onRequestSettlement = onRequestSettlement,
        modifier = modifier,
    )
}

/** «گواهی صادرشده» answers with a toast: the list has no place to print a certificate. */
@Composable
private fun HandleAssignerContractsEvents(events: Flow<AssignerContractsEvent>) {
    val toaster = LocalToaster.current
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is AssignerContractsEvent.CertificateFound -> toaster.success(
                    getString(
                        Res.string.assigner_certificate_issued,
                        event.certificate.number,
                        event.certificate.date,
                    )
                )

                AssignerContractsEvent.CertificateNotFound ->
                    toaster.info(getString(Res.string.assigner_certificate_not_found))

                is AssignerContractsEvent.ShowServerMessage -> toaster.error(event.message)
            }
        }
    }
}

@Composable
fun AssignerContractsContent(
    state: AssignerContractsUiState,
    onIntent: (AssignerContractsIntent) -> Unit,
    onBack: () -> Unit,
    onOpenDetail: (AssignerContractPR) -> Unit,
    onOpenBases: (AssignerContractPR) -> Unit,
    onRequestSettlement: (AssignerContractPR) -> Unit,
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
    val assigner = remember(list.items) { list.items.soleAssigner() }

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
                    contentDescription = stringResource(Res.string.assigner_select_workshop),
                    onClick = { onIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true)) },
                )
            },
        ) {
            AnimatedRingHeaderIcon(
                icon = vectorResource(Res.drawable.ic_tamin_assigner_contracts),
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            )
        }

        // Keeps its place in every list state — skeleton, empty, failed — so switching tab or
        // clearing the search never takes the tabs off the screen while the rows below settle.
        val count = list.items.size
        val header: @Composable () -> Unit = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                if (assigner != null) AssignerIdentityCard(assigner = assigner)
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
                // A search can be checked; a tab with nothing in it can only be switched away from.
                val title = stringResource(
                    when {
                        filter != null -> Res.string.assigner_empty_not_found_title
                        tab == AssignerContractTab.ACTIVE -> Res.string.assigner_empty_active_title
                        else -> Res.string.assigner_empty_finished_title
                    }
                )
                val body = stringResource(
                    if (filter != null) Res.string.assigner_empty_not_found_body else Res.string.assigner_empty_tab_body
                )
                EmptyStateMessage(
                    icon = vectorResource(Res.drawable.ic_tamin_assigner_contracts),
                    title = title,
                    subtitle = body,
                    actionLabel = stringResource(Res.string.assigner_select_workshop),
                    onAction = {
                        onIntent(AssignerContractsIntent.SearchOpenChanged(isOpen = true))
                    },
                    showIconTile = true,
                )
            },
        ) { contract, itemModifier ->
            // The same card ردیف‌های پیمان draws, with the design's three actions in one row.
            // `dateLabel` differs because the column does: this endpoint sends تاریخ قرارداد where
            // that one sends تاریخ تعهد.
            ContractRowCard(
                row = contract.card,
                showContact = true,
                dateLabel = Res.string.assigner_contract_date,
                modifier = itemModifier,
                buttons = {
                    WorkshopCardButton(
                        text = stringResource(Res.string.assigner_action_detail),
                        tone = WorkshopCardButtonTone.OUTLINE,
                        onClick = { onOpenDetail(contract) },
                    )
                    WorkshopCardButton(
                        text = stringResource(Res.string.assigner_action_bases),
                        // A پیمان missing any of the four keys cannot address its own bases, so the
                        // button is plainly unavailable rather than opening another contract's records.
                        tone = if (contract.canOpenBases) {
                            WorkshopCardButtonTone.OUTLINE
                        } else {
                            WorkshopCardButtonTone.DISABLED
                        },
                        onClick = { onOpenBases(contract) },
                    )
                    if (contract.isFinished) {
                        // A finished پیمان takes no new request; it answers with the certificate it
                        // was settled under.
                        WorkshopCardButton(
                            text = stringResource(Res.string.assigner_action_certificate),
                            tone = WorkshopCardButtonTone.SUCCESS,
                            onClick = {
                                onIntent(AssignerContractsIntent.CertificateRequested(contract, announce = true))
                            },
                        )
                    } else {
                        WorkshopCardButton(
                            text = stringResource(Res.string.assigner_action_settlement),
                            // The request is filed under an id built from the same four keys, so a
                            // row missing one offers it disabled rather than filing under the wrong id.
                            tone = if (contract.canRequestSettlement) {
                                WorkshopCardButtonTone.PRIMARY
                            } else {
                                WorkshopCardButtonTone.DISABLED
                            },
                            onClick = { onRequestSettlement(contract) },
                        )
                    }
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
 * «کارگاه واگذارنده» and its code — the employer's own workshop, read off the rows themselves.
 *
 * Every row carries both sides of its پیمان, so this is real data rather than a profile lookup. It is
 * drawn only while every row names the same واگذارنده: across several, one name would be a claim
 * about the others.
 */
@Composable
private fun AssignerIdentityCard(assigner: AssignerPartyPR, modifier: Modifier = Modifier) {
    StatRowCard(modifier = modifier) {
        StatColumn(
            value = assigner.workshopName,
            label = stringResource(Res.string.assigner_me_label),
            modifier = Modifier.weight(1f),
        )
        StatDivider()
        StatColumn(
            value = assigner.workshopCode,
            label = stringResource(Res.string.workshop_code),
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * جاری / خاتمه‌یافته, and how many پیمان the list holds.
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

/** The one واگذارنده every row names, or null when they name several or none. */
private fun List<AssignerContractPR>.soleAssigner(): AssignerPartyPR? =
    map { it.assigner }
        .distinctBy { it.workshopCode }
        .singleOrNull()
        ?.takeIf { it.workshopName.isNotBlank() }

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

private val PreviewAssigner = AssignerPartyDN(
    workshopId = "0968210170",
    workshopName = "آموزشگاه کامپیوتر توکلی",
)

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
        branchCode = "0210",
        contractNumber = "44122",
        contractDate = "14010210",
        contractEndDate = "15000101",
        contractSubject = "خدمات نظافت و پشتیبانی",
        assigner = PreviewAssigner,
        employer = AssignerPartyDN(
            workshopId = "9028212822",
            workshopName = "دبستان کارن ۲ مجتبی غلامیان",
            address = "بجنورد، خیابان طالقانی، کوچهٔ ۱۲، پلاک ۴",
            branchCode = "0210",
        ),
    ),
    AssignerContractDN(
        contractRow = "3",
        // No sequence — the bases action is offered disabled.
        contractSequence = "",
        branchCode = "0210",
        contractNumber = "45200",
        contractDate = "14030120",
        contractEndDate = "14030601",
        contractSubject = "پیمان با کارکرد ارزی",
        assigner = PreviewAssigner,
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
        onRequestSettlement = {},
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
        onRequestSettlement = {},
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
        onRequestSettlement = {},
    )
}

/** No search and nothing held — the tab's own empty wording. */
@PreviewRtlTheme
@Composable
private fun AssignerContractsNoneHeldPreview() = PreviewRtlThemeContent {
    AssignerContractsContent(
        state = AssignerContractsUiState(hasApplied = true),
        onIntent = {},
        onBack = {},
        onOpenDetail = {},
        onOpenBases = {},
        onRequestSettlement = {},
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
        onRequestSettlement = {},
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
        onRequestSettlement = {},
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
        onRequestSettlement = {},
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
