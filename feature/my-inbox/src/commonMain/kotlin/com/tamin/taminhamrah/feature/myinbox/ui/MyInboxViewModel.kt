package com.tamin.taminhamrah.feature.myinbox.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxEvent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxEvent.*
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxIntent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState.PartialState
import com.tamin.taminhamrah.mapper.inbox.toPresentation
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxItemsUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxSizeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class MyInboxViewModel(
    private val getPersonalInboxItemsUseCase: GetPersonalInboxItemsUseCase,
    private val getPersonalInboxSizeUseCase: GetPersonalInboxSizeUseCase,
) : BaseViewModel<MyInboxUiState, PartialState, MyInboxEvent, MyInboxIntent>(
    initialState = MyInboxUiState()
) {

    override fun handleIntent(intent: MyInboxIntent): Flow<PartialState> {
        return when (intent) {
            is MyInboxIntent.LoadInbox -> handleLoadInbox()
            is MyInboxIntent.OnBackClicked -> {
                sendEvent(MyInboxEvent.NavigateBack)
                emptyFlow()
            }

            is MyInboxIntent.OnCopyClicked -> {
                sendEvent(CopyToClipboard(intent.id))
                emptyFlow()
            }

            is MyInboxIntent.OnItemActionClicked -> TODO()
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
            emit(PartialState.Error(e.message ?: "Unknown Error"))
        }
    }

    private fun loadInboxSize(): Flow<PartialState> = flow {
        try {
            getPersonalInboxSizeUseCase().collect { size ->
                emit(PartialState.SizeLoaded(size.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun reduceState(
        currentState: MyInboxUiState,
        partialState: PartialState
    ): MyInboxUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
        is PartialState.ItemsLoaded -> currentState.copy(
            isLoading = false,
            items = partialState.items
        )
        is PartialState.SizeLoaded -> currentState.copy(
            size = partialState.size
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
