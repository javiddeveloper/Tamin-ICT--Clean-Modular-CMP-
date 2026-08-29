package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.identity.toPresentation
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.useCases.user.GetIdentityInfoUseCase
import com.tamin.taminhamrah.useCases.workshops.GetLegalRepresentativeWorkshopsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class LegalRepresentativeWorkshopsViewModel(
    private val getLegalRepresentativeWorkshopsUseCase: GetLegalRepresentativeWorkshopsUseCase,
    private val getIdentityInfoUseCase: GetIdentityInfoUseCase,
) : BaseViewModel<
    LegalRepresentativeWorkshopsUiState,
    LegalRepresentativeWorkshopsUiState.PartialState,
    LegalRepresentativeWorkshopsEvent,
    LegalRepresentativeWorkshopsIntent
    >(initialState = LegalRepresentativeWorkshopsUiState()) {

    override fun handleIntent(
        intent: LegalRepresentativeWorkshopsIntent
    ): Flow<LegalRepresentativeWorkshopsUiState.PartialState> = when (intent) {
        is LegalRepresentativeWorkshopsIntent.Load -> merge(loadWorkshops(), loadIdentity())
    }

    private fun loadWorkshops(): Flow<LegalRepresentativeWorkshopsUiState.PartialState> = flow {
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

    // The identity card's name is decorative here — the workshop list is the primary content
    // this screen exists for — so a failure to fetch it is swallowed rather than surfaced as
    // a full-screen error.
    private fun loadIdentity(): Flow<LegalRepresentativeWorkshopsUiState.PartialState> =
        getIdentityInfoUseCase()
            .map { LegalRepresentativeWorkshopsUiState.PartialState.NameLoaded(it.toPresentation().fullName) }
            .catch { }

    override fun reduceState(
        currentState: LegalRepresentativeWorkshopsUiState,
        partialState: LegalRepresentativeWorkshopsUiState.PartialState
    ): LegalRepresentativeWorkshopsUiState = when (partialState) {
        is LegalRepresentativeWorkshopsUiState.PartialState.Loading ->
            currentState.copy(isLoading = partialState.isLoading, error = null)

        is LegalRepresentativeWorkshopsUiState.PartialState.Loaded ->
            currentState.copy(isLoading = false, workshops = partialState.workshops)

        is LegalRepresentativeWorkshopsUiState.PartialState.NameLoaded ->
            currentState.copy(fullName = partialState.fullName)

        is LegalRepresentativeWorkshopsUiState.PartialState.Error ->
            currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): LegalRepresentativeWorkshopsUiState.PartialState =
        LegalRepresentativeWorkshopsUiState.PartialState.Error(message)
}
