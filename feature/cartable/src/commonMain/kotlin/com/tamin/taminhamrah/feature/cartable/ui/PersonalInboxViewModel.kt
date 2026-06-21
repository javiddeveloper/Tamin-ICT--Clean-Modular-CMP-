package com.tamin.taminhamrah.feature.cartable.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxEvent
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxIntent
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxUiState
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxUiState.PartialState
import com.tamin.taminhamrah.mapper.inbox.toPresentation
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxItemsUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxSizeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class PersonalInboxViewModel(
    private val getPersonalInboxItemsUseCase: GetPersonalInboxItemsUseCase,
    private val getPersonalInboxSizeUseCase: GetPersonalInboxSizeUseCase,
) : BaseViewModel<PersonalInboxUiState, PartialState, PersonalInboxEvent, PersonalInboxIntent>(
    initialState = PersonalInboxUiState()
) {

    override fun handleIntent(intent: PersonalInboxIntent): Flow<PartialState> {
        return when (intent) {
            is PersonalInboxIntent.LoadInbox -> handleLoadInbox()
        }
    }

    private fun handleLoadInbox(): Flow<PartialState> = merge(
        loadInboxItems(),
        loadInboxSize(),
    )

    private fun loadInboxItems(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPersonalInboxItemsUseCase().collect { items ->
                emit(PartialState.ItemsLoaded(items.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun loadInboxSize(): Flow<PartialState> = flow {
        try {
            getPersonalInboxSizeUseCase().collect { size ->
                emit(PartialState.SizeLoaded(size.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: PersonalInboxUiState,
        partialState: PartialState
    ): PersonalInboxUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.ItemsLoaded -> currentState.copy(
            isLoading = false,
            items = partialState.items,
        )
        is PartialState.SizeLoaded -> currentState.copy(size = partialState.size)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
