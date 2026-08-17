package com.tamin.taminhamrah.feature.history.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.history.ui.model.CareerTotalPR
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf

/**
 * What «کلیه سوابق» shows.
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
    /** The year whose months are on screen, or null while the list is. */
    val selectedYear: YearHistoryPR? = null,
    val error: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState

        data class HistoryLoaded(
            val years: ImmutableList<YearHistoryPR>,
            val careerTotal: CareerTotalPR,
            val wageByYear: ImmutableMap<String, ImmutableList<DastmozdInfoItemPR>>,
        ) : PartialState

        data class YearSelected(val year: YearHistoryPR?) : PartialState

        data class Error(val message: String) : PartialState
    }
}

sealed interface HistoryIntent {
    data object Load : HistoryIntent

    /** Carries the year itself, so the sheet can never be handed a stale list position. */
    data class SelectYear(val year: YearHistoryPR) : HistoryIntent

    data object DismissYearDetail : HistoryIntent
}

sealed interface HistoryEvent {
    data class ShowToast(val message: String) : HistoryEvent
}
