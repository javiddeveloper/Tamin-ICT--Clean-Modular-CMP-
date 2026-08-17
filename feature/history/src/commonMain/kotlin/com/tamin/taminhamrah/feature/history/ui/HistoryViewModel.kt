package com.tamin.taminhamrah.feature.history.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryEvent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState.PartialState
import com.tamin.taminhamrah.feature.history.ui.model.careerTotal
import com.tamin.taminhamrah.feature.history.ui.model.mergeByYear
import com.tamin.taminhamrah.mapper.history.toPresentation
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
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
) : BaseViewModel<HistoryUiState, PartialState, HistoryEvent, HistoryIntent>(
    initialState = HistoryUiState()
) {

    override fun handleIntent(intent: HistoryIntent): Flow<PartialState> = when (intent) {
        is HistoryIntent.Load -> load()

        is HistoryIntent.SelectYear -> flow { emit(PartialState.YearSelected(intent.year)) }

        is HistoryIntent.DismissYearDetail -> flow { emit(PartialState.YearSelected(null)) }
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
        // BaseViewModel merges intents rather than switching between them, so a second tap on
        // «تلاش دوباره» while the first is still in flight would run two loads at once and let the
        // slower one write last. One at a time, and the retry button simply does nothing until the
        // current attempt finishes.
        if (uiState.value.isLoading) return@flow

        // The list already on screen stays there while this runs, so a retry never blanks the page.
        emit(PartialState.Loading(true))
        try {
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
                    )
                )
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
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
            error = null,
        )

        is PartialState.YearSelected -> currentState.copy(selectedYear = partialState.year)

        is PartialState.Error ->
            currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
