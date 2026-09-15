package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.BaseDocumentKind
import com.tamin.taminhamrah.model.workshop.BaseDocumentPR
import com.tamin.taminhamrah.model.workshop.ComputationalBasePR
import com.tamin.taminhamrah.model.workshop.SettlementCertificatePR
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_tab_active
import taminx.core.core_ui.assigner_tab_finished

/**
 * The two halves the list is split into, in the order the tabs sit.
 *
 * The label and the membership rule are columns of this one table, so a tab cannot end up titled
 * for one half while drawing the other.
 */
enum class AssignerContractTab(val label: StringResource) {
    ACTIVE(Res.string.assigner_tab_active),
    FINISHED(Res.string.assigner_tab_finished);

    fun includes(contract: AssignerContractPR): Boolean = contract.isFinished == (this == FINISHED)

    companion object {
        /** Hoisted, so the tab strip is handed the same list instance on every recomposition. */
        val all: ImmutableList<AssignerContractTab> = entries.toImmutableList()
    }
}

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

/**
 * A document that could not be fetched, and the service's own reason.
 *
 * [message] is null only when the service failed without saying anything — the row then falls back
 * to the generic line, exactly as `WorkshopListScaffold` does for a failed list. Keyed by document
 * so the failure sits on the row that caused it rather than on the whole screen.
 *
 * [isMissing] separates "this document is not there" from "the fetch did not get through". Only the
 * first is permanent: the row is badged unavailable and stops responding, because tapping it again
 * can only produce the same 404. Everything else stays tappable, because tapping it *is* the retry.
 */
@Immutable
data class DocumentFailure(
    val documentId: String,
    val message: String?,
    val isMissing: Boolean = false,
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
 * **No screen here reads a "currently selected" field.** Every drill-down is addressed by its own
 * route and finds what it draws in [list] or [bases] — جزئیات پیمان by ردیف and sequence, مبانی
 * محاسباتی by its four keys, جزئیات مبنا by شمارهٔ سند. That removes the one ordering this design
 * could never guarantee: a state emission landing before the destination that reads it composes.
 */
@Immutable
data class AssignerContractsUiState(
    val list: PagedListState<AssignerContractPR> = PagedListState(),
    /** The search in force. Null is no search — the list then holds every پیمان, not nothing. */
    val filter: AssignerContractFilter? = null,
    /**
     * Whether [filter] has been applied at least once, so re-entering the screen on the same one
     * keeps the rows instead of fetching them again. A flag of its own because null is a real
     * filter now and cannot also mean "not asked yet".
     */
    val hasApplied: Boolean = false,
    /** Which half of [list] is on screen. */
    val tab: AssignerContractTab = AssignerContractTab.ACTIVE,
    val draft: AssignerSearchDraft = AssignerSearchDraft(),
    val isSearchOpen: Boolean = false,
    /** کارگاه‌های شما — the quick-pick rows, fetched the first time the sheet opens. */
    val myWorkshops: ImmutableList<WorkshopPR> = persistentListOf(),
    /**
     * How many workshops the employer actually holds, against how many the quick-pick lists.
     *
     * The sheet asks for one page, so an employer with more than fits never sees the rest. The
     * three fields still reach any workshop by number, so the cap is stated rather than paged
     * away — a silent partial list is the part that misleads.
     */
    val myWorkshopsTotal: Int = 0,
    val bases: PagedListState<ComputationalBasePR> = PagedListState(),
    /** Which پیمان [bases] holds, so returning to a screen already loaded does not refetch. */
    val basesKeys: ComputationalBaseKeys? = null,
    /** Which document is being fetched, so its row can show progress and refuse a second tap. */
    val openingDocumentId: String? = null,
    /** The open viewer, image or PDF. Null when none is open. */
    val preview: DocumentPreview? = null,
    /** The document whose last fetch failed, and why. Cleared when it is tried again. */
    val documentFailure: DocumentFailure? = null,
    /** The last «گواهی مفاصاحساب» asked for, and its answer. One at a time: one پیمان is on screen. */
    val certificate: CertificateLookup? = null,
) {
    sealed interface PartialState {
        // ------------------------------------------------------------------------ the list
        data object Loading : PartialState

        /**
         * [filter] is the search the page was fetched *for*, null for the unfiltered list.
         *
         * `BaseViewModel` runs intents through `flatMapMerge`, and every page of a result is fetched
         * in one loop, so a search applied while another is still paging leaves two loops running.
         * The reducer drops pages that belong to the one no longer applied.
         */
        data class Loaded(
            val list: PagedListState<AssignerContractPR>,
            val filter: AssignerContractFilter?,
        ) : PartialState

        data class Error(val message: String?) : PartialState
        data class Applied(val filter: AssignerContractFilter?) : PartialState
        data object Cleared : PartialState
        data class TabChanged(val tab: AssignerContractTab) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class DraftChanged(
            val workshopId: String? = null,
            val branchCode: String? = null,
            val contractRow: String? = null,
        ) : PartialState

        data class WorkshopIdErrorChanged(val isVisible: Boolean) : PartialState
        data class MyWorkshopsLoaded(
            val workshops: ImmutableList<WorkshopPR>,
            val total: Int,
        ) : PartialState

        // ------------------------------------------------------------------ the drill-downs
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

        // ---------------------------------------------------------------------- documents
        data class DocumentOpening(val documentId: String?) : PartialState
        data class PreviewChanged(val preview: DocumentPreview?) : PartialState
        data class DocumentFailed(val failure: DocumentFailure?) : PartialState

        // ------------------------------------------------------------- گواهی مفاصاحساب
        data class CertificateLoading(val contract: AssignerContractPR) : PartialState

        /** [contract] is the one asked about, so an answer for a پیمان since left is dropped. */
        data class CertificateLoaded(
            val contract: AssignerContractPR,
            val certificate: SettlementCertificatePR?,
            val didFail: Boolean = false,
        ) : PartialState
    }
}

/**
 * The certificate lookup for one پیمان.
 *
 * [certificate] null with [isLoading] and [didFail] both false is an answer — the service holds no
 * certificate for it — and is not asked again; a failed lookup is.
 */
@Immutable
data class CertificateLookup(
    val contract: AssignerContractPR,
    val isLoading: Boolean = false,
    val certificate: SettlementCertificatePR? = null,
    val didFail: Boolean = false,
)

sealed interface AssignerContractsIntent {
    /**
     * Carries the identity the route was opened with.
     *
     * Blank on the services-grid entry, where the list shows every پیمان; filled on the drill-down
     * from جزئیات کارگاه, where it opens narrowed to that workshop.
     */
    data class Open(val workshopId: String, val branchCode: String) : AssignerContractsIntent

    /** Re-runs the applied search — or the unfiltered list — after a failure. */
    data object Retry : AssignerContractsIntent
    data class TabSelected(val tab: AssignerContractTab) : AssignerContractsIntent
    data class SearchOpenChanged(val isOpen: Boolean) : AssignerContractsIntent
    data class DraftWorkshopIdChanged(val value: String) : AssignerContractsIntent
    data class DraftBranchCodeChanged(val value: String) : AssignerContractsIntent
    data class DraftContractRowChanged(val value: String) : AssignerContractsIntent
    /** Picking a کارگاه‌های شما row fills the code and its branch at once. */
    data class QuickPicked(val workshopId: String, val branchCode: String) : AssignerContractsIntent
    data object ApplySearch : AssignerContractsIntent
    data object ClearSearch : AssignerContractsIntent

    /** Opens مبانی محاسباتی for [keys], unless that پیمان is already the one loaded. */
    data class OpenBases(val keys: ComputationalBaseKeys) : AssignerContractsIntent
    data object LoadMoreBases : AssignerContractsIntent
    data object RetryBases : AssignerContractsIntent

    /**
     * Opens one attachment.
     *
     * Carries the whole document rather than an id to look up: the row that was tapped already
     * holds it, and a lookup would have to find it through a "currently selected مبنا" the state
     * would then have to keep in step with navigation.
     */
    data class DocumentTapped(val document: BaseDocumentPR) : AssignerContractsIntent

    /** The PDF viewer's own retry button; the open preview carries everything a refetch needs. */
    data object RetryDocument : AssignerContractsIntent
    data object PreviewDismissed : AssignerContractsIntent

    /**
     * Looks up the certificate a خاتمه‌یافته پیمان was settled under.
     *
     * [announce] is «گواهی صادرشده» on the list, which reports the answer; جزئیات پیمان asks without
     * it and prints the answer in its status line.
     */
    data class CertificateRequested(
        val contract: AssignerContractPR,
        val announce: Boolean,
    ) : AssignerContractsIntent
}

/**
 * What «گواهی صادرشده» found.
 *
 * Navigation is the nav graph's, and a document that would not open is said on its own row. The
 * certificate is the one answer with nowhere on the list to sit, so it is announced. The copy is the
 * screen's: the events carry the values, and the screen resolves the wording.
 */
sealed interface AssignerContractsEvent {
    data class CertificateFound(val certificate: SettlementCertificatePR) : AssignerContractsEvent
    data object CertificateNotFound : AssignerContractsEvent
    data class ShowServerMessage(val message: String) : AssignerContractsEvent
}
