package com.tamin.taminhamrah.feature.history.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.history.ui.model.CareerTotalPR
import com.tamin.taminhamrah.feature.history.ui.model.HistoryMetric
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
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
    /**
     * Everything, or one year — what the orb counts and the chart plots.
     *
     * Kept in the state rather than remembered in the composable: it decides what is loaded into
     * every part of the page at once, and a screen that recreates it on rotation would drop the
     * year the person had chosen.
     */
    val scope: HistoryScope = HistoryScope.All,
    /**
     * Which series the chart plots — the شاخص chips above it.
     *
     * In the state rather than remembered in the composable for the same reason [scope] is: it
     * decides what the whole card draws, and rotating the phone must not quietly put the person
     * back on a series they did not choose.
     */
    val metric: HistoryMetric = HistoryMetric.BOTH,
    /**
     * Whether the bars are broken out per employer rather than added together.
     *
     * Mutually exclusive with [selectedSource] by construction: splitting *shows* every employer,
     * so a filter down to one of them would be answering the same question twice. Turning it on
     * clears the filter.
     */
    val splitBySource: Boolean = false,
    /** The month whose wages are open under the chart, in year scope only. */
    val selectedMonth: Int? = null,
    /** Which employer the month bars are filtered to, or null for all of them together. */
    val selectedSource: Int? = null,
    /** «انتخاب سال و ماه» is open. */
    val yearPickerOpen: Boolean = false,
    /**
     * What has been typed into the picker's year search, in ASCII digits?
     *
     * The field shows Persian digits and the years are matched as ASCII, so the conversion happens
     * once on the way in rather than on every year of a long career on the way out.
     */
    val yearQuery: String = "",
    /**
     * What the picker has staged, which is not yet what the page shows.
     *
     * The design lets a person land on a year, look at its months, and change their mind before
     * anything happens — so the pick is held here and only becomes [scope] and [selectedMonth] when
     * they confirm. Applying on each tap would reload the chart under them three times on the way
     * to the month they wanted.
     */
    val pickerYear: String? = null,
    val pickerMonth: Int? = null,
    /** «ارسال سابقه به موسسات» is waiting to be confirmed. */
    val showSendConfirm: Boolean = false,
    /** The send is in flight; the confirm button says so and cannot be pressed twice. */
    val isSending: Boolean = false,
    /** The success dialog is up. */
    val showSendSuccess: Boolean = false,
    /**
     * The server's own confirmation text, when it sent any.
     *
     * Null means it confirmed without wording of its own, and the screen falls back to
     * `history_send_success_default` — it does *not* mean the dialog is hidden; that is
     * [showSendSuccess]. A service answering with an empty string used to raise a dialog with
     * no text in it.
     */
    val sendSuccessMessage: String? = null,
    /**
     * The signed-in person's national number, from the record the access check already fetched.
     *
     * Only used to name a downloaded file, which is why a missing one is not a problem: the report
     * is still the right report, it just saves under a plainer name.
     */
    val nationalId: String? = null,
    /**
     * Which services actually returned rows.
     *
     * Each report is generated by one of them, and a service with nothing to say still produces a
     * valid, blank PDF — so these decide which reports can be offered, not [years], which may have
     * been folded from the wage rows when the merged service was empty.
     */
    val hasCombinedRecords: Boolean = false,
    val hasWageRecords: Boolean = false,
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
            val hasCombinedRecords: Boolean,
            val hasWageRecords: Boolean,
        ) : PartialState


        data class ScopeChanged(val scope: HistoryScope) : PartialState

        data class MonthSelected(val month: Int?) : PartialState

        data class SourceSelected(val source: Int?) : PartialState

        data class MetricSelected(val metric: HistoryMetric) : PartialState

        /** Opening seeds the staged pick from what is on screen; closing leaves it untouched. */
        data class YearPickerVisible(
            val visible: Boolean,
            val year: String? = null,
            val month: Int? = null,
        ) : PartialState

        data class YearQueryChanged(val query: String) : PartialState

        /** A year staged in the picker. Choosing one always drops the month staged under the last. */
        data class PickerYearStaged(val year: String) : PartialState

        data class PickerMonthStaged(val month: Int?) : PartialState

        data class SplitChanged(val split: Boolean) : PartialState

        data class SendConfirmVisible(val visible: Boolean) : PartialState

        data class Sending(val isSending: Boolean) : PartialState

        /** Sent. [message] is the service's own wording, or null if it sent none. */
        data class SendSucceeded(val message: String?) : PartialState

        data object SendSuccessDismissed : PartialState

        data class IdentityLoaded(val nationalId: String?) : PartialState

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

    /** Switch the page between all years and one of them. */
    data class SelectScope(val scope: HistoryScope) : HistoryIntent

    /** Open a month's wages under the chart; the same month again closes it. */
    data class SelectMonth(val month: Int) : HistoryIntent

    /** Filter the month bars to one employer, or null for all of them. */
    data class SelectSource(val source: Int?) : HistoryIntent

    /** Switch the chart between دستمزد, روزهای کار and both together. */
    data class SelectMetric(val metric: HistoryMetric) : HistoryIntent

    /** Break the bars out per employer, or add them back together. */
    data object ToggleSplit : HistoryIntent

    // ── «انتخاب سال و ماه» ───────────────────────────────────────────────────────
    data object OpenYearPicker : HistoryIntent

    data object DismissYearPicker : HistoryIntent

    data class YearQueryChanged(val query: String) : HistoryIntent

    data class PickerYearSelected(val year: String) : HistoryIntent

    /** Null is «کل سال» — the whole year rather than one month of it. */
    data class PickerMonthSelected(val month: Int?) : HistoryIntent

    /** Commits the staged pick to the page and closes the sheet. */
    data object ApplyYearPicker : HistoryIntent

    data object AskSendNotice : HistoryIntent

    data object DismissSendConfirm : HistoryIntent

    /** Actually send it — only ever reached from the confirmation. */
    data object ConfirmSendNotice : HistoryIntent

    data object DismissSendSuccess : HistoryIntent

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
