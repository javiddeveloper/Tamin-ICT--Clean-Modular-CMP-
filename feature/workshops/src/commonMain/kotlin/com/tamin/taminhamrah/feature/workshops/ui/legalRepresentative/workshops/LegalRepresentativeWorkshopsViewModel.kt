package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.useCases.workshops.GetLegalRepresentativeWorkshopsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class LegalRepresentativeWorkshopsViewModel(
    private val getLegalRepresentativeWorkshopsUseCase: GetLegalRepresentativeWorkshopsUseCase
) : BaseViewModel<
    LegalRepresentativeWorkshopsUiState,
    LegalRepresentativeWorkshopsUiState.PartialState,
    LegalRepresentativeWorkshopsEvent,
    LegalRepresentativeWorkshopsIntent
    >(initialState = LegalRepresentativeWorkshopsUiState()) {

    override fun handleIntent(
        intent: LegalRepresentativeWorkshopsIntent
    ): Flow<LegalRepresentativeWorkshopsUiState.PartialState> = when (intent) {
        is LegalRepresentativeWorkshopsIntent.Load -> flow {
            emit(LegalRepresentativeWorkshopsUiState.PartialState.Loading(true))
            try {
                val result = getLegalRepresentativeWorkshopsUseCase().first()
                emit(
                    LegalRepresentativeWorkshopsUiState.PartialState.Loaded(
                        result?.list?.map { it.toPresentation() } ?: emptyList()
                    )
                )
            } catch (e: Exception) {
                emit(LegalRepresentativeWorkshopsUiState.PartialState.Error(e.message))
            }
        }
    }

    override fun reduceState(
        currentState: LegalRepresentativeWorkshopsUiState,
        partialState: LegalRepresentativeWorkshopsUiState.PartialState
    ): LegalRepresentativeWorkshopsUiState = when (partialState) {
        is LegalRepresentativeWorkshopsUiState.PartialState.Loading ->
            currentState.copy(isLoading = partialState.isLoading, error = null)

        is LegalRepresentativeWorkshopsUiState.PartialState.Loaded ->
            currentState.copy(isLoading = false, workshops = partialState.workshops)

        is LegalRepresentativeWorkshopsUiState.PartialState.Error ->
            currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): LegalRepresentativeWorkshopsUiState.PartialState =
        LegalRepresentativeWorkshopsUiState.PartialState.Error(message)
}
