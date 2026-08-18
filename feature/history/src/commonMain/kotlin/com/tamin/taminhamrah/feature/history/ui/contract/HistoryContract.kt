package com.tamin.taminhamrah.feature.history.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.history.ui.model.CareerTotalPR
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.StringResource

/**
 * What «مجموع سوابق» shows.
 *
 * Everything here is already in the shape the screen draws: the years are merged, the career total
 * is normalized and the wage rows are grouped by year. Doing that work in the ViewModel rather than
 * in the composables is what keeps a scroll or a sheet opening from re-folding the whole history.
 */
@Immutable
data class HistoryUiState(
    val isLoading: Boolean = false,
    /** Whether a load has ever returned. An empty list only means "none" once one has. */
    val hasLoadedOnce: Boolean = false,
    val years: ImmutableList<YearHistoryPR> = persistentListOf(),
    val careerTotal: CareerTotalPR = CareerTotalPR(),
    /**
     * The workshops that reported each year, keyed by Jalali year.
     *
     * A map rather than a list to filter per year at open time: the sheet asks for one year, and a
     * year with nothing recorded simply has no entry.
     */
    val wageByYear: ImmutableMap<String, ImmutableList<DastmozdInfoItemPR>> = persistentMapOf(),
    /**
     * The wage call failed, so an empty entry above means "not known" rather than "none".
     *
     * Carried in the state rather than left to the toast that announced it: the toast is gone by
     * the time a sheet is opened, and a sheet that says «ثبت نشده» about data it never received is
     * telling the person something untrue.
     */
    val wagesUnavailable: Boolean = false,
    /**
     * Set when the signed-in person cannot have insurance history at all — a مستمری‌بگیر. Not an
     * [error]: nothing failed and retrying cannot help, so the screen says why and offers only a
     * way out.
     */
    val accessDenied: Boolean = false,
    /** The year whose months are on screen, or null while the list is. */
    val selectedYear: YearHistoryPR? = null,
    /** The download menu is open. */
    val showReportMenu: Boolean = false,
    /** Which report the viewer is showing, or null when it is closed. */
    val selectedReport: HistoryCertificateType? = null,
    /** The fetched PDF, once [selectedReport]'s download has answered. */
    val reportPdf: PdfDownloadPR? = null,
    /** The download came back with nothing, so the viewer should stop waiting for it. */
    val reportDownloadFailed: Boolean = false,
    val error: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState

        data class HistoryLoaded(
            val years: ImmutableList<YearHistoryPR>,
            val careerTotal: CareerTotalPR,
            val wageByYear: ImmutableMap<String, ImmutableList<DastmozdInfoItemPR>>,
            val wagesUnavailable: Boolean,
        ) : PartialState

        data class YearSelected(val year: YearHistoryPR?) : PartialState

        data class ReportMenuVisible(val visible: Boolean) : PartialState

        data class ReportSelected(val type: HistoryCertificateType?) : PartialState

        data class ReportPdfChanged(val pdf: PdfDownloadPR?) : PartialState

        data object ReportDownloadFailed : PartialState

        data object AccessDenied : PartialState

        data class Error(val message: String) : PartialState
    }
}

sealed interface HistoryIntent {
    data object Load : HistoryIntent

    /** Carries the year itself, so the sheet can never be handed a stale list position. */
    data class SelectYear(val year: YearHistoryPR) : HistoryIntent

    data object DismissYearDetail : HistoryIntent

    data object ShowReportMenu : HistoryIntent

    data object DismissReportMenu : HistoryIntent

    /** Open the viewer for one of the three reports. */
    data class SelectReport(val type: HistoryCertificateType) : HistoryIntent

    /** Fetch the bytes for [HistoryUiState.selectedReport]; the viewer asks when it needs them. */
    data object DownloadReport : HistoryIntent

    data object DismissReport : HistoryIntent
}

sealed interface HistoryEvent {
    /**
     * Carries the resource, not the resolved text: reading a string in a ViewModel needs a
     * composition, which is also why it hangs a unit test. The screen resolves it.
     */
    data class ShowToast(val message: StringResource) : HistoryEvent
}
