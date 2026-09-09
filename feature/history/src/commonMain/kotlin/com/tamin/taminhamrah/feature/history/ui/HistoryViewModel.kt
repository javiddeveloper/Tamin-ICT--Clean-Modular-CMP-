package com.tamin.taminhamrah.feature.history.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState.PartialState
import com.tamin.taminhamrah.feature.history.ui.model.HistoryScope
import com.tamin.taminhamrah.feature.history.ui.model.canHaveInsuranceHistory
import com.tamin.taminhamrah.feature.history.ui.model.careerTotal
import com.tamin.taminhamrah.feature.history.ui.model.careerTotalFromDays
import com.tamin.taminhamrah.feature.history.ui.model.mergeByYear
import com.tamin.taminhamrah.feature.history.ui.model.yearsFromWages
import com.tamin.taminhamrah.mapper.history.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.UserRoleDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.useCases.history.DownloadHistoryReportUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetUserRoleUseCase
import com.tamin.taminhamrah.useCases.history.SendHistoryNoticeUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import taminx.feature.history.Res
import taminx.feature.history.history_combined_wage_unavailable
import taminx.feature.history.history_report_empty
import taminx.feature.history.history_report_no_history

class HistoryViewModel(
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase,
    private val getUserInfosUseCase: GetUserInfosUseCase,
    private val sendHistoryNoticeUseCase: SendHistoryNoticeUseCase,
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

        is HistoryIntent.SelectMetric -> flow { emit(PartialState.MetricSelected(intent.metric)) }

        HistoryIntent.ToggleSplit ->
            flow { emit(PartialState.SplitChanged(!uiState.value.splitBySource)) }

        /*
         * Opening seeds the pick from what the page is already showing, so the sheet lands on the
         * year in front of the person rather than on nothing. From «همه» there is no year to seed
         * with, which is also the state the picker's «یک سال انتخاب کنید» button describes.
         */
        HistoryIntent.OpenYearPicker -> flow {
            val state = uiState.value
            val scope = state.scope as? HistoryScope.Year
            emit(
                PartialState.YearPickerVisible(
                    visible = true,
                    year = scope?.year,
                    month = scope?.let { state.selectedMonth },
                )
            )
            emit(PartialState.YearQueryChanged(""))
        }

        HistoryIntent.DismissYearPicker -> flow {
            emit(PartialState.YearPickerVisible(visible = false))
        }

        // Persian digits are what the field shows; the years are ASCII, so the search converts once
        // here rather than per year on every keystroke.
        is HistoryIntent.YearQueryChanged -> flow {
            emit(PartialState.YearQueryChanged(intent.query.digitsOnly()))
        }

        is HistoryIntent.PickerYearSelected -> flow {
            emit(PartialState.PickerYearStaged(intent.year))
        }

        is HistoryIntent.PickerMonthSelected -> flow {
            emit(PartialState.PickerMonthStaged(intent.month))
        }

        /*
         * The one place the staged pick becomes the page.
         *
         * Emits the same partials a chip tap does, so a year reached through the sheet and a year
         * reached from the strip leave the page in exactly the same state — including the employer
         * filter, which belonged to the year being left.
         */
        HistoryIntent.ApplyYearPicker -> flow {
            val state = uiState.value
            val year = state.pickerYear ?: return@flow
            emit(PartialState.ScopeChanged(HistoryScope.Year(year)))
            emit(PartialState.MonthSelected(state.pickerMonth))
            emit(PartialState.SourceSelected(null))
            emit(PartialState.YearPickerVisible(visible = false))
        }

        is HistoryIntent.AskSendNotice -> flow { emit(PartialState.SendConfirmVisible(true)) }

        is HistoryIntent.DismissSendConfirm -> flow { emit(PartialState.SendConfirmVisible(false)) }

        is HistoryIntent.DismissSendSuccess -> flow { emit(PartialState.SendSucceeded(null)) }

        /*
         * The «اعلام» the previous app sent from this very screen — `sendeblagh`, not the three-flag
         * `sendinstitution` of the standalone service.
         *
         * Guarded against a second tap while the first is in flight: this posts something, and a
         * double send is not a cosmetic problem.
         */
        is HistoryIntent.ConfirmSendNotice -> flow {
            if (uiState.value.isSending) return@flow
            emit(PartialState.Sending(true))
            try {
                val message = sendHistoryNoticeUseCase()
                emit(PartialState.SendConfirmVisible(false))
                emit(PartialState.SendSucceeded(message ?: DEFAULT_SEND_SUCCESS))
            } catch (e: Exception) {
                emit(PartialState.SendConfirmVisible(false))
                emit(PartialState.Error(e.toSingleLineMessage()))
            } finally {
                emit(PartialState.Sending(false))
            }
        }

        /*
         * A report for someone with no insured years is a valid PDF with nothing on it. The service
         * will happily produce one, so the refusal has to be here: no years, no file, and say why
         * rather than open a viewer onto a blank page.
         */
        is HistoryIntent.ShowReportMenu -> flow {
            if (uiState.value.years.isEmpty()) {
                sendEvent(HistoryEvent.ShowToast(Res.string.history_report_no_history))
            } else {
                emit(PartialState.ReportMenuVisible(true))
            }
        }

        is HistoryIntent.DismissReportMenu -> flow { emit(PartialState.ReportMenuVisible(false)) }

        /*
         * Each report comes from one service, and a service with no rows still returns a valid PDF
         * with nothing on it — which is what reached the user as a blank page. Refuse the report
         * whose source is empty rather than download a document with nothing in it.
         */
        is HistoryIntent.SelectReport -> flow {
            val state = uiState.value
            val available = when (intent.type) {
                HistoryCertificateType.COMBINED -> state.hasCombinedRecords
                HistoryCertificateType.WAGES -> state.hasWageRecords
                // «کلیه سوابق» is generated from the year report, which is populated whenever any
                // of the person's history exists at all.
                HistoryCertificateType.ALL -> state.years.isNotEmpty()
            }
            if (!available) {
                emit(PartialState.ReportMenuVisible(false))
                sendEvent(HistoryEvent.ShowToast(Res.string.history_report_empty))
                return@flow
            }

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
            // Who this is, before asking for anything. Neither a مستمری‌بگیر nor a کارفرما has
            // insured years, and the history endpoints reject them rather than answering empty —
            // which reaches the user as a connection error, because an unparseable rejection is
            // indistinguishable from a dead network by the time it gets to the screen.
            //
            // Both lookups at once: they are independent, and together they cost one round trip.
            // Neither failing is a refusal — the gate exists to explain the service, so an outage
            // on it must not lock out someone who can use the page.
            val (role, userInfo) = coroutineScope {
                val roleCall = async { orNull { getUserRoleUseCase() } }
                val infoCall = async { orNull { getUserInfosUseCase() } }
                (roleCall.await() ?: UserRoleDN.UNKNOWN) to infoCall.await()
            }
            if (!canHaveInsuranceHistory(role, userInfo)) {
                emit(PartialState.AccessDenied)
                return@flow
            }
            // Kept from the record already in hand — it names the files this page downloads.
            emit(PartialState.IdentityLoaded(userInfo?.nationalID))

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
                // failure from also complaining that the workshops are missing.
                val history = years.await().list?.toPresentation().orEmpty()
                val wageRows = wages.await()

                // Said out loud rather than swallowed: the years are all there, but every sheet
                // opened from them will be missing its workshops.
                if (wageRows == null) {
                    sendEvent(HistoryEvent.ShowToast(Res.string.history_combined_wage_unavailable))
                }

                // The merged service is the source of truth when it answers at all; when it comes
                // back empty for someone the wage service does report, the years are folded from
                // those rows instead of showing a person with history an empty page.
                val wageRowList = wageRows?.list?.toPresentation().orEmpty()
                val mergedYears = history.mergeByYear()
                val resolvedYears = mergedYears.ifEmpty { wageRowList.yearsFromWages() }

                emit(
                    PartialState.HistoryLoaded(
                        years = resolvedYears,
                        careerTotal = if (mergedYears.isEmpty()) {
                            resolvedYears.careerTotalFromDays()
                        } else {
                            history.careerTotal()
                        },
                        wageByYear = wageRowList.groupByYear(),
                        wagesUnavailable = wageRows == null,
                        hasCombinedRecords = mergedYears.isNotEmpty(),
                        hasWageRecords = wageRowList.isNotEmpty(),
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

    /**
     * The call's result, or null when it failed.
     *
     * Not `runCatching`: it swallows CancellationException too, and a lookup torn down with its
     * scope would then read as an answer of "no".
     */
    private inline fun <T> orNull(call: () -> T): T? = try {
        call()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
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

        is PartialState.HistoryLoaded -> run {
            println("TOTALDBG years=${partialState.years.size} total=${partialState.careerTotal} merged=${partialState.hasCombinedRecords} wage=${partialState.hasWageRecords}")
            currentState
        }.copy(
            isLoading = false,
            hasLoadedOnce = true,
            years = partialState.years,
            careerTotal = partialState.careerTotal,
            wageByYear = partialState.wageByYear,
            wagesUnavailable = partialState.wagesUnavailable,
            hasCombinedRecords = partialState.hasCombinedRecords,
            hasWageRecords = partialState.hasWageRecords,
            // A reload can return a different set of years; a scope pointing at one that is gone
            // would leave the page counting a year it can no longer draw.
            scope = partialState.scope(currentState.scope),
            selectedMonth = null,
            selectedSource = null,
            error = null,
        )


        is PartialState.ScopeChanged -> currentState.copy(scope = partialState.scope)

        is PartialState.MonthSelected -> currentState.copy(selectedMonth = partialState.month)

        is PartialState.SourceSelected -> currentState.copy(selectedSource = partialState.source)

        is PartialState.MetricSelected -> currentState.copy(metric = partialState.metric)

        is PartialState.YearPickerVisible -> currentState.copy(
            yearPickerOpen = partialState.visible,
            // Only an opening carries a seed; closing leaves the staged pick alone so the sheet
            // does not visibly empty itself on the way out.
            pickerYear = if (partialState.visible) partialState.year else currentState.pickerYear,
            pickerMonth = if (partialState.visible) partialState.month else currentState.pickerMonth,
        )

        is PartialState.YearQueryChanged -> currentState.copy(yearQuery = partialState.query)

        // A different year invalidates the month staged under the last one — ماه ۵ of ۱۴۰۲ is not
        // ماه ۵ of ۱۴۰۳, and the month list the person was choosing from has just been replaced.
        is PartialState.PickerYearStaged -> currentState.copy(
            pickerYear = partialState.year,
            pickerMonth = null,
        )

        is PartialState.PickerMonthStaged -> currentState.copy(pickerMonth = partialState.month)

        // Splitting shows every employer at once, so a filter down to one of them is the
        // same question asked twice — it is cleared rather than left to contradict the bars.
        is PartialState.SplitChanged -> currentState.copy(
            splitBySource = partialState.split,
            selectedSource = if (partialState.split) null else currentState.selectedSource,
        )

        is PartialState.SendConfirmVisible ->
            currentState.copy(showSendConfirm = partialState.visible)

        is PartialState.Sending -> currentState.copy(isSending = partialState.isSending)

        is PartialState.SendSucceeded ->
            currentState.copy(sendSuccessMessage = partialState.message)

        is PartialState.IdentityLoaded ->
            currentState.copy(nationalId = partialState.nationalId)

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

    private companion object {
        /** Used only when the service confirms without wording of its own. */
        const val DEFAULT_SEND_SUCCESS = "درخواست شما با موفقیت ثبت شد."
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
