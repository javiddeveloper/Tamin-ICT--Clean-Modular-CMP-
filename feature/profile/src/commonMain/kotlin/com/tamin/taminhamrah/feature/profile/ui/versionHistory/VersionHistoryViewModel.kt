package com.tamin.taminhamrah.feature.profile.ui.versionHistory

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryEvent
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryIntent
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryUiState
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryUiState.PartialState
import com.tamin.taminhamrah.mapper.versionHistory.toPresentation
import com.tamin.taminhamrah.useCases.versionHistory.GetVersionHistoryUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class VersionHistoryViewModel(
    private val getVersionHistoryUseCase: GetVersionHistoryUseCase
) : BaseViewModel<VersionHistoryUiState, PartialState, VersionHistoryEvent, VersionHistoryIntent>(
    initialState = VersionHistoryUiState()
) {

    override fun handleIntent(intent: VersionHistoryIntent): Flow<PartialState> {
        return when (intent) {
            VersionHistoryIntent.LoadVersionHistory -> handleLoadVersionHistory()
            is VersionHistoryIntent.ToggleExpand -> flow { emit(PartialState.ToggleExpand(intent.version)) }
            VersionHistoryIntent.OnBackClicked -> flow { sendEvent(VersionHistoryEvent.NavigateBack) }
        }
    }

    private fun handleLoadVersionHistory(): Flow<PartialState> = flow {
        emit(PartialState.SetLoading(true))
        getVersionHistoryUseCase()
            .map { dnList ->
                val prList = dnList.map { it.toPresentation() }.toImmutableList()
                PartialState.SetItems(prList)
            }
            .catch { emit(PartialState.SetLoading(false)) }
            .collect {
                emit(it)
                emit(PartialState.SetLoading(false))
            }
    }

    override fun reduceState(
        currentState: VersionHistoryUiState,
        partialState: PartialState
    ): VersionHistoryUiState = when (partialState) {
        is PartialState.SetLoading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.SetItems -> currentState.copy(
            isLoading = false,
            items = partialState.items,
            lastUpdatedDate = partialState.items.firstOrNull { it.isLatest }?.releaseDate ?: currentState.lastUpdatedDate
        )
        is PartialState.ToggleExpand -> {
            val updatedItems = currentState.items.map { item ->
                if (item.versionName == partialState.version) {
                    item.copy(isExpanded = !item.isExpanded)
                } else {
                    item
                }
            }.toImmutableList()
            currentState.copy(items = updatedItems)
        }
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.SetLoading(false)
}
