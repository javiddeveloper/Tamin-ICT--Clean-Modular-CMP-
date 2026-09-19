package com.tamin.taminhamrah.feature.workshops.ui.contractRows.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_tab_with_agreement
import taminx.core.core_ui.contract_rows_tab_with_agreement_hint
import taminx.core.core_ui.contract_rows_tab_without_agreement
import taminx.core.core_ui.contract_rows_tab_without_agreement_hint

/**
 * Which of the two ردیف پیمان services the list is reading.
 *
 * Declaration order is the order the tabs sit in. Label, hint and the endpoint behind them are
 * columns of this one table, so a tab cannot end up labeled for one service while fetching the
 * other — which is exactly what the two near-duplicate screens in the old app allowed.
 */
enum class ContractRowTab(val label: StringResource, val hint: StringResource) {
    /** `get-employer-agreement-by-workshop-id-and-branch-code` — seven fields per row. */
    WITH_AGREEMENT(
        label = Res.string.contract_rows_tab_with_agreement,
        hint = Res.string.contract_rows_tab_with_agreement_hint,
    ),

    /** `contract-employer-workshop-info-with-workshop-and-branch-code` — four fields per row. */
    WITHOUT_AGREEMENT(
        label = Res.string.contract_rows_tab_without_agreement,
        hint = Res.string.contract_rows_tab_without_agreement_hint,
    ),
}

/** Which workshop the visible page was fetched for. */
@Immutable
data class ContractRowFilter(
    val workshopId: String,
    val branchCode: String,
)

/**
 * State of ردیف‌های پیمان.
 *
 * [draftWorkshopId] / [draftBranchCode] are what the picker sheet is editing; [applied] is what the
 * visible page was fetched with. Keeping them apart is what lets the sheet be dismissed without
 * silently changing the list, and lets the chip above the list say which workshop is in force.
 *
 * [applied] is null until a workshop has been chosen — that is a different empty state from "this
 * workshop has no rows", and the two are worded differently, so it cannot collapse to a blank code.
 */
@Immutable
data class ContractRowsUiState(
    val tab: ContractRowTab = ContractRowTab.WITH_AGREEMENT,
    val list: PagedListState<ContractRowPR> = PagedListState(),
    val draftWorkshopId: String = "",
    val draftBranchCode: String = "",
    val applied: ContractRowFilter? = null,
    val isPickerOpen: Boolean = false,
    /**
     * Whether each code has been submitted blank.
     *
     * Flags rather than messages: resolving a `StringResource` inside the ViewModel is unreliable
     * under the unit-test runtime, so the UI turns each into the copy at the field it names.
     *
     * Both codes are required. کد شعبه looks optional — the service accepts an agreement list
     * without it elsewhere — but on *these two* endpoints it is a path segment: submitting blank
     * produced `…/get-employer-agreement-by-workshop-id-and-branch-code/6318210573/` and the
     * service answered **404**, verified against the live backend.
     */
    val showWorkshopIdError: Boolean = false,
    val showBranchCodeError: Boolean = false,
    /**
     * Set when the list was moved to the other tab because the chosen one held nothing.
     *
     * A workshop is in exactly one of the two categories, so making the user discover that by
     * hand — which is what the empty state's «دستهٔ دیگر … را بررسی کنید» asks — is work the screen
     * can do itself. Cleared as soon as the user picks a tab deliberately.
     */
    val didAutoSwitchTab: Boolean = false,
    /**
     * کارگاه‌های شما — the quick-pick rows, fetched the first time the sheet opens and paged in as
     * its list is scrolled. A list that silently stops at one-page reads as complete.
     */
    val myWorkshops: PagedListState<WorkshopPR> = PagedListState(),
) {
    sealed interface PartialState {
        data class TabChanged(val tab: ContractRowTab) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        /**
         * [tab] is the tab the request was made *for*, and null for the pipeline's own catch-all.
         *
         * `BaseViewModel` runs intents through `flatMapMerge`, so two tab loads can be in flight at
         * once and the slower one can land last. Carrying the tab lets the reducer drop a result
         * that no longer belongs to the tab on screen, instead of painting one service's rows under
         * the other's heading.
         */
        data class Error(
            val message: String?,
            val tab: ContractRowTab? = null,
        ) : PartialState

        data class Loaded(
            val list: PagedListState<ContractRowPR>,
            val tab: ContractRowTab,
        ) : PartialState
        data class DraftChanged(
            val workshopId: String? = null,
            val branchCode: String? = null,
        ) : PartialState

        data class Applied(val filter: ContractRowFilter) : PartialState
        data object Cleared : PartialState
        data class PickerOpenChanged(val isOpen: Boolean) : PartialState
        data class WorkshopIdErrorChanged(val isVisible: Boolean) : PartialState
        data class BranchCodeErrorChanged(val isVisible: Boolean) : PartialState
        data class AutoSwitchedTab(val tab: ContractRowTab) : PartialState
        data object MyWorkshopsLoadingMore : PartialState
        data class MyWorkshopsLoaded(val workshops: PagedListState<WorkshopPR>) : PartialState
    }
}

sealed interface ContractRowsIntent {
    /**
     * Carries the identity the route was opened with.
     *
     * Blank on the services-grid entry, where no workshop is known yet and the picker opens
     * instead; filled on the drill-down from جزئیات کارگاه, where the list loads straight away.
     */
    data class Open(val workshopId: String, val branchCode: String) : ContractRowsIntent

    data class TabSelected(val tab: ContractRowTab) : ContractRowsIntent
    data object LoadMore : ContractRowsIntent

    /** Re-runs the applied filter after a failure, without reopening the picker. */
    data object Retry : ContractRowsIntent
    data class PickerOpenChanged(val isOpen: Boolean) : ContractRowsIntent
    data class DraftWorkshopIdChanged(val value: String) : ContractRowsIntent
    data class DraftBranchCodeChanged(val value: String) : ContractRowsIntent
    data class QuickPicked(val workshopId: String, val branchCode: String) : ContractRowsIntent

    /** کارگاه‌های شما was scrolled to its end. */
    data object LoadMoreMyWorkshops : ContractRowsIntent
    data object ApplyPicker : ContractRowsIntent
    data object ClearPicker : ContractRowsIntent
}

/** Nothing leaves this screen: it navigates nowhere and every row is inert. */
sealed interface ContractRowsEvent
