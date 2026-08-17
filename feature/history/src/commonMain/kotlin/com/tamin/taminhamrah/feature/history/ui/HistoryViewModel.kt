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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
     * their failure costs one sheet's contents and is swallowed here rather than emptying the whole
     * screen — the previous app aborted on it, which is listed as a defect in the spec, not a rule
     * to carry over.
     */
    private fun load(): Flow<PartialState> = flow {
        // The list already on screen stays there while this runs, so a retry never blanks the page.
        emit(PartialState.Loading(true))
        try {
            coroutineScope {
                val years = async { getTalfighInfosUseCase() }
                val wages = async { runCatching { getDastmozdInfosUseCase() }.getOrNull() }

                val history = years.await().list?.toPresentation().orEmpty()
                emit(
                    PartialState.HistoryLoaded(
                        years = history.mergeByYear(),
                        careerTotal = history.careerTotal(),
                        wageByYear = wages.await()?.list?.toPresentation()?.groupByYear()
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
