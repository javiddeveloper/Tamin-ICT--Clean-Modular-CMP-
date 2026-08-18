package com.tamin.taminhamrah.feature.history.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState.PartialState
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
import com.tamin.taminhamrah.feature.history.ui.model.canHaveInsuranceHistory
import com.tamin.taminhamrah.feature.history.ui.model.careerTotal
import com.tamin.taminhamrah.feature.history.ui.model.mergeByYear
import com.tamin.taminhamrah.mapper.history.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.history.DownloadHistoryReportUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetUserRoleUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import taminx.feature.history.Res
import taminx.feature.history.history_combined_wage_unavailable

class HistoryViewModel(
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase,
    private val downloadHistoryReportUseCase: DownloadHistoryReportUseCase,
) : BaseViewModel<HistoryUiState, PartialState, HistoryEvent, HistoryIntent>(
    initialState = HistoryUiState()
) {

    /**
     * Whether a load is already running.
     *
     * Held here rather than read off `uiState`: `BaseViewModel` reduces partial states through a
     * channel, so `isLoading` only becomes true a hop after it is emitted, and two quick taps on
     * «تلاش دوباره» would both find it still false. This is set before the first suspension.
     */
    private var loadInFlight = false

    override fun handleIntent(intent: HistoryIntent): Flow<PartialState> = when (intent) {
        is HistoryIntent.Load -> load()

        is HistoryIntent.SelectYear -> flow { emit(PartialState.YearSelected(intent.year)) }

        is HistoryIntent.DismissYearDetail -> flow { emit(PartialState.YearSelected(null)) }

        // Changing scope clears what was open inside the old one: a month index and a source
        // position mean nothing in another year, and carrying them over would show the wrong wages.
        is HistoryIntent.SelectScope -> flow {
            emit(PartialState.ScopeChanged(intent.scope))
            emit(PartialState.MonthSelected(null))
            emit(PartialState.SourceSelected(null))
        }

        // Tapping the open month closes it, which is how the design's chart toggles.
        is HistoryIntent.SelectMonth -> flow {
            val next = intent.month.takeIf { it != uiState.value.selectedMonth }
            emit(PartialState.MonthSelected(next))
        }

        is HistoryIntent.SelectSource -> flow { emit(PartialState.SourceSelected(intent.source)) }

        is HistoryIntent.ShowReportMenu -> flow { emit(PartialState.ReportMenuVisible(true)) }

        is HistoryIntent.DismissReportMenu -> flow { emit(PartialState.ReportMenuVisible(false)) }

        is HistoryIntent.SelectReport -> flow {
            emit(PartialState.ReportMenuVisible(false))
            // The bytes of whatever was open before must not be handed to the next viewer.
            emit(PartialState.ReportPdfChanged(null))
            emit(PartialState.ReportSelected(intent.type))
        }

        is HistoryIntent.DownloadReport -> downloadReport()

        is HistoryIntent.DismissReport -> flow {
            emit(PartialState.ReportSelected(null))
            emit(PartialState.ReportPdfChanged(null))
        }
    }

    /**
     * Both lists at once, because neither waits on the other.
     *
     * Only the years are load-bearing: they are the page. The wage rows fill the per-year sheet, so
     * their failure costs one sheet's contents and is reported rather than emptying the whole
     * screen — the previous app aborted on it, which is listed as a defect in the spec, not a rule
     * to carry over.
     */
    private fun load(): Flow<PartialState> = flow {
        if (loadInFlight) return@flow
        loadInFlight = true

        // The list already on screen stays there while this runs, so a retry never blanks the page.
        emit(PartialState.Loading(true))
        try {
            // Who this is, before asking for anything. A مستمری‌بگیر has no insured years and both
            // history endpoints answer 500 for them, so the previous app decided this up front from
            // `login-services/logininfo` rather than showing the server's error — and so does this.
            //
            // A failed role check is not a refusal: the gate only exists to explain the service, so
            // an outage on it must not lock out someone who can use the page.
            val role = try {
                getUserRoleUseCase()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                UserRoleDN.UNKNOWN
            }
            if (!role.canHaveInsuranceHistory()) {
                emit(PartialState.AccessDenied)
                return@flow
            }

            coroutineScope {
                val years = async { getTalfighInfosUseCase() }
                // Not `runCatching`: it catches CancellationException too, so a wage call torn down
                // with its scope would report itself as a wage failure.
                val wages = async {
                    try {
                        getDastmozdInfosUseCase()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        null
                    }
                }

                // The years are awaited first on purpose. They are the page, so if they failed this
                // throws here and the wage result is never inspected — which is what stops a page
                // failure from also complaining that the workshops are missing. Only a load that
                // got its years reaches the warning below.
                val history = years.await().list?.toPresentation().orEmpty()
                val wageRows = wages.await()

                // Said out loud rather than swallowed: the years are all there, but every sheet
                // opened from them will be missing its workshops, and a person looking for a
                // workshop deserves to know it failed rather than read the blank as "none".
                if (wageRows == null) {
                    sendEvent(HistoryEvent.ShowToast(Res.string.history_combined_wage_unavailable))
                }

                emit(
                    PartialState.HistoryLoaded(
                        years = history.mergeByYear(),
                        careerTotal = history.careerTotal(),
                        wageByYear = wageRows?.list?.toPresentation()?.groupByYear()
                            ?: persistentMapOf(),
                        wagesUnavailable = wageRows == null,
                    )
                )
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            loadInFlight = false
        }
    }

    /**
     * The bytes for the report the viewer is showing.
     *
     * Driven by the viewer rather than by the menu tap: `TaminPdfViewer` renders a copy already on
     * the device without asking, so a report downloaded once is never fetched again.
     */
    private fun downloadReport(): Flow<PartialState> = flow {
        val type = uiState.value.selectedReport ?: return@flow
        try {
            downloadHistoryReportUseCase(type).collect { pdf ->
                emit(PartialState.ReportPdfChanged(pdf.toPresentation()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // The viewer says so itself; a dialog on top of an open viewer would only bury it.
            emit(PartialState.ReportDownloadFailed)
        }
    }

    /** The scope to keep after a load: the one already chosen, unless its year did not come back. */
    private fun PartialState.HistoryLoaded.scope(current: HistoryScope): HistoryScope =
        if (current is HistoryScope.Year && years.none { it.year == current.year }) {
            HistoryScope.All
        } else {
            current
        }

    /** Grouped once here so opening a sheet is a lookup rather than a scan of every wage row. */
    private fun List<DastmozdInfoItemPR>.groupByYear():
        ImmutableMap<String, ImmutableList<DastmozdInfoItemPR>> =
        groupBy { it.hisyear }
            .mapValues { (_, rows) -> rows.toImmutableList() }
            .toImmutableMap()

    override fun reduceState(
        currentState: HistoryUiState,
        partialState: PartialState,
    ): HistoryUiState = when (partialState) {
        is PartialState.Loading ->
            currentState.copy(isLoading = partialState.isLoading, error = null)

        is PartialState.HistoryLoaded -> currentState.copy(
            isLoading = false,
            hasLoadedOnce = true,
            years = partialState.years,
            careerTotal = partialState.careerTotal,
            wageByYear = partialState.wageByYear,
            wagesUnavailable = partialState.wagesUnavailable,
            // A reload can return a different set of years; a scope pointing at one that is gone
            // would leave the page counting a year it can no longer draw.
            scope = partialState.scope(currentState.scope),
            selectedMonth = null,
            selectedSource = null,
            error = null,
        )

        is PartialState.YearSelected -> currentState.copy(selectedYear = partialState.year)

        is PartialState.ScopeChanged -> currentState.copy(scope = partialState.scope)

        is PartialState.MonthSelected -> currentState.copy(selectedMonth = partialState.month)

        is PartialState.SourceSelected -> currentState.copy(selectedSource = partialState.source)

        is PartialState.ReportMenuVisible ->
            currentState.copy(showReportMenu = partialState.visible)

        is PartialState.ReportSelected -> currentState.copy(
            selectedReport = partialState.type,
            reportDownloadFailed = false,
        )

        is PartialState.ReportPdfChanged -> currentState.copy(reportPdf = partialState.pdf)

        is PartialState.ReportDownloadFailed -> currentState.copy(reportDownloadFailed = true)

        is PartialState.AccessDenied ->
            currentState.copy(isLoading = false, accessDenied = true, error = null)

        is PartialState.Error ->
            currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
