package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.LegalRepresentativeWorkshopPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class LegalRepresentativeWorkshopsUiState(
    val isLoading: Boolean = false,
    val workshops: ImmutableList<LegalRepresentativeWorkshopPR> = persistentListOf(),
    val fullName: String? = null,
    val error: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Loaded(val workshops: ImmutableList<LegalRepresentativeWorkshopPR>) : PartialState
        data class NameLoaded(val fullName: String) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface LegalRepresentativeWorkshopsIntent {
    data object Load : LegalRepresentativeWorkshopsIntent
}

sealed interface LegalRepresentativeWorkshopsEvent
