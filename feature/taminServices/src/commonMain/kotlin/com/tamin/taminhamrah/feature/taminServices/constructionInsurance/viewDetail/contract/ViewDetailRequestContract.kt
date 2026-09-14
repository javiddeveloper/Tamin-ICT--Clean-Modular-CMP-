package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * نمایش جزییات درخواست — reached from the کارگاه list row or the عملیات menu's first option
 * ("۱ - نمایش جزییات درخواست"). Reuses `GetConstructionFilesUseCase` filtered down to the one
 * file/request pair, exactly like the old app's `getConstructionFilesWhitOutPaging` — there is no
 * separate detail endpoint on the server.
 */
@Immutable
data class ViewDetailRequestUiState(
    val fileNumber: Long? = null,
    val requestNumber: Long? = null,
    val isLoading: Boolean = false,
    val items: ImmutableList<ConstructionFilePR> = persistentListOf(),
    val error: String? = null,
) {
    val file: ConstructionFilePR? get() = items.firstOrNull()

    sealed interface PartialState {
        data class HeaderSeeded(val fileNumber: Long?, val requestNumber: Long?) : PartialState
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val items: ImmutableList<ConstructionFilePR>) : PartialState
    }
}

sealed interface ViewDetailRequestIntent {
    /** Sent once from the Route with the values carried by [ViewDetailRequestUiState]. */
    data class Load(val fileNumber: Long?, val requestNumber: Long?) : ViewDetailRequestIntent
    data object Retry : ViewDetailRequestIntent
    data object OnBackClicked : ViewDetailRequestIntent
}

sealed interface ViewDetailRequestEvent {
    data object NavigateBack : ViewDetailRequestEvent
}
