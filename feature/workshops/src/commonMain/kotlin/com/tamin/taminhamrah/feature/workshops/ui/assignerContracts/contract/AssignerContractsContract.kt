package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.ComputationalBasePR
import org.jetbrains.compose.resources.StringResource

/** What the visible page of واگذارندگان was fetched with. */
@Immutable
data class AssignerContractFilter(
    val workshopId: String,
    val branchCode: String = "",
    val contractRow: String = "",
)

/** What the search sheet is editing, before «جست‌وجوی کارگاه» applies it. */
@Immutable
data class AssignerSearchDraft(
    val workshopId: String = "",
    val branchCode: String = "",
    val contractRow: String = "",
    /**
     * Whether the sheet has been submitted with a blank کد کارگاه.
     *
     * A flag rather than a message: resolving a `StringResource` inside a ViewModel is unreliable
     * under the unit-test runtime, so the sheet turns this into the copy at the field it names.
     */
    val showWorkshopIdError: Boolean = false,
)

/**
 * The four keys مبانی محاسباتی is addressed with, as جزئیات مبنا's route carries them.
 *
 * A value rather than four parameters so the "are these the ones already loaded?" check is one
 * comparison — the screen re-runs its `LaunchedEffect` on every recomposition of the nav entry.
 */
@Immutable
data class ComputationalBaseKeys(
    val workshopId: String,
    val branchCode: String,
    val contractRow: String,
    val contractSequence: String,
)

/** A document the user has opened, and whichever of the two payloads its kind needs. */
@Immutable
data class DocumentPreview(
    val documentId: String,
    val title: StringResource,
    val kind: BaseDocumentKind,
    /** Base64, for [BaseDocumentKind.IMAGE]. Blank while the fetch is still in flight. */
    val imageData: String = "",
    /** Bytes, for [BaseDocumentKind.PDF]. Null while the fetch is still in flight. */
    val pdf: PdfDownloadPR? = null,
    val didFail: Boolean = false,
)

/**
 * State of واگذارندگان, across all four of its destinations.
 *
 * One state for four screens because they are one flow: the list response already holds everything
 * جزئیات پیمان draws, so drilling in costs no request — exactly what the old app's parcelable did.
 * All four destinations resolve the same graph-scoped ViewModel, and each reads only its own slice.
 *
 * The slices are separate `@Immutable` values on purpose: a page landing in [bases] must not
 * invalidate the search draft, and typing in the draft must not invalidate the list.
 *
 * Nothing here drives navigation. [selected] and [selectedBase] are set when the *row* is tapped,
 * a step before the screen that reads them is opened, so no destination can compose before its own
 * data is in state — which is what an ordering between a state emission and a navigation call
 * could never guarantee.
 */
@Immutable
data class AssignerContractsUiState(
    val list: PagedListState<AssignerContractPR> = PagedListState(),
    /** Null until a search has been applied — the screen's two empty states turn on this. */
    val filter: AssignerContractFilter? = null,
    val draft: AssignerSearchDraft = AssignerSearchDraft(),
    val isSearchOpen: Boolean = false,
    /** The row whose action sheet is open. Null when no sheet is up. */
    val actionSheetFor: AssignerContractPR? = null,
    /**
     * The پیمان جزئیات پیمان is showing, set the moment its card is tapped.
     *
     * Held rather than serialized into a route: the detail screen draws both parties and four
     * contract fields, which is more than belongs in a route, and the list already has them. Null
     * only after process death, which the screen states rather than drawing blank cells.
     */
    val selected: AssignerContractPR? = null,
    val bases: PagedListState<ComputationalBasePR> = PagedListState(),
    /** Which پیمان [bases] holds, so returning to a screen already loaded does not refetch. */
    val basesKeys: ComputationalBaseKeys? = null,
    /** The مبنا جزئیات مبنا is showing, set the moment its row is tapped. */
    val selectedBase: ComputationalBasePR? = null,
    /** Which document is being fetched, so its row can show progress and refuse a second tap. */
    val openingDocumentId: String? = null,
    /** The open viewer, image or PDF. Null when none is open. */
    val preview: DocumentPreview? = null,
) {
    sealed interface PartialState {
        // ------------------------------------------------------------------------ the list
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Loaded(val list: PagedListState<AssignerContractPR>) : PartialState
        data class Error(val message: String?) : PartialState
        data class Applied(val filter: AssignerContractFilter) : PartialState
        data object Cleared : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class DraftChanged(
            val workshopId: String? = null,
            val branchCode: String? = null,
            val contractRow: String? = null,
        ) : PartialState

        data class WorkshopIdErrorChanged(val isVisible: Boolean) : PartialState

        // ------------------------------------------------------------------ the drill-downs
        data class ActionSheetChanged(val contract: AssignerContractPR?) : PartialState
        data class Selected(val contract: AssignerContractPR?) : PartialState
        data class BasesLoading(val keys: ComputationalBaseKeys) : PartialState
        data object BasesLoadingMore : PartialState

        /**
         * [keys] is the پیمان the page was fetched *for*.
         *
         * `BaseViewModel` runs intents through `flatMapMerge`, so opening one پیمان's bases, going
         * back and opening another's leaves two requests in flight and the slower one can land
         * last. The reducer drops a page whose keys are not the ones on screen.
         */
        data class BasesLoaded(
            val list: PagedListState<ComputationalBasePR>,
            val keys: ComputationalBaseKeys,
        ) : PartialState

        data class BasesError(
            val message: String?,
            val keys: ComputationalBaseKeys? = null,
        ) : PartialState

        data class BaseSelected(val base: ComputationalBasePR?) : PartialState

        // ---------------------------------------------------------------------- documents
        data class DocumentOpening(val documentId: String?) : PartialState
        data class PreviewChanged(val preview: DocumentPreview?) : PartialState
    }
}

sealed interface AssignerContractsIntent {
    /**
     * Carries the identity the route was opened with.
     *
     * Blank on the services-grid entry, where no workshop is known and the search sheet raises
     * itself; filled on the drill-down from جزئیات کارگاه, where the list loads straight away.
     */
    data class Open(val workshopId: String, val branchCode: String) : AssignerContractsIntent

    data object LoadMore : AssignerContractsIntent

    /** Re-runs the applied search after a failure, without reopening the sheet. */
    data object Retry : AssignerContractsIntent
    data class SearchOpenChanged(val isOpen: Boolean) : AssignerContractsIntent
    data class DraftWorkshopIdChanged(val value: String) : AssignerContractsIntent
    data class DraftBranchCodeChanged(val value: String) : AssignerContractsIntent
    data class DraftContractRowChanged(val value: String) : AssignerContractsIntent
    data object ApplySearch : AssignerContractsIntent
    data object ClearSearch : AssignerContractsIntent

    /**
     * Tapping a card raises the action sheet — it does not navigate.
     *
     * It also records the پیمان as selected, so whichever action the sheet offers next has its
     * screen's data in state before that screen is ever composed.
     */
    data class ContractTapped(val contract: AssignerContractPR) : AssignerContractsIntent
    data object ActionSheetDismissed : AssignerContractsIntent

    /** Opens مبانی محاسباتی for [keys], unless that پیمان is already the one loaded. */
    data class OpenBases(val keys: ComputationalBaseKeys) : AssignerContractsIntent
    data object LoadMoreBases : AssignerContractsIntent
    data object RetryBases : AssignerContractsIntent

    /** Tapping a مبنا records it, a step before جزئیات مبنا is opened. */
    data class BaseTapped(val base: ComputationalBasePR) : AssignerContractsIntent

    data class DocumentTapped(val documentId: String) : AssignerContractsIntent

    /** The PDF viewer's own retry button. */
    data object RetryDocument : AssignerContractsIntent
    data object PreviewDismissed : AssignerContractsIntent
}

/**
 * One-shot effects.
 *
 * Navigation is not one of them: it is the nav graph's, driven by the row that was tapped, so this
 * carries only what has to be *said*. The message is a `StringResource` rather than resolved copy
 * — a ViewModel that resolves one hangs the unit-test runtime.
 */
sealed interface AssignerContractsEvent {
    data class ShowMessage(val message: StringResource) : AssignerContractsEvent
}
