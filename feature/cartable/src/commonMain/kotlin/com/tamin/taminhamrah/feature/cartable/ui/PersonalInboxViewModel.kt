package com.tamin.taminhamrah.feature.cartable.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxEvent
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxIntent
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxUiState
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxUiState.PartialState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PersonalInboxViewModel : BaseViewModel<PersonalInboxUiState, PartialState, PersonalInboxEvent, PersonalInboxIntent>(
    initialState = PersonalInboxUiState()
) {

    override fun handleIntent(intent: PersonalInboxIntent): Flow<PartialState> {
        return when (intent) {
            is PersonalInboxIntent.LoadInbox -> handleLoadInbox()
        }
    }

    private fun handleLoadInbox(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        emit(PartialState.Loading(false))
    }

    override fun reduceState(
        currentState: PersonalInboxUiState,
        partialState: PartialState
    ): PersonalInboxUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
