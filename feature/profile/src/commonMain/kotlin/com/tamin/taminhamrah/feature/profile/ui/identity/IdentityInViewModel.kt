package com.tamin.taminhamrah.feature.profile.ui.identity

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInEvent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInIntent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInUiState
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInUiState.PartialState
import com.tamin.taminhamrah.mapper.identity.toPresentation
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class IdentityInViewModel(
    private val identityInfoUseCase: IdentityInfoUseCase
) : BaseViewModel<IdentityInUiState, PartialState, IdentityInEvent, IdentityInIntent>(
    initialState = IdentityInUiState()
) {

    override fun handleIntent(intent: IdentityInIntent): Flow<PartialState> {
        return when (intent) {
            is IdentityInIntent.LoadIdentity -> loadIdentity()
            is IdentityInIntent.OnBackClicked -> flow { sendEvent(IdentityInEvent.NavigateBack) }
        }
    }

    private fun loadIdentity(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        identityInfoUseCase()
            .map { it.toPresentation() }
            .map { PartialState.IdentityLoaded(it) as PartialState }
            .catch { emit(PartialState.Error(it.toSingleLineMessage())) }
            .collect { emit(it) }
    }

    override fun reduceState(
        currentState: IdentityInUiState,
        partialState: PartialState
    ): IdentityInUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.IdentityLoaded -> currentState.copy(
            isLoading = false,
            identityInfo = partialState.info,
            error = null
        )
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
