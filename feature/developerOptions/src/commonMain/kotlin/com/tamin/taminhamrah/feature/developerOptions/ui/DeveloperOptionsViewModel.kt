package com.tamin.taminhamrah.feature.developerOptions.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.BaseUrlItemUi
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.DeveloperOptionsEvent
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.DeveloperOptionsIntent
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.DeveloperOptionsUiState
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.DeveloperOptionsUiState.PartialState
import com.tamin.taminhamrah.feature.developerOptions.ui.model.BaseUrlPresets
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class DeveloperOptionsViewModel(
    private val developerOptionsRepository: DeveloperOptionsRepository
) : BaseViewModel<DeveloperOptionsUiState, PartialState, DeveloperOptionsEvent, DeveloperOptionsIntent>(
    initialState = DeveloperOptionsUiState()
) {

    init {
        viewModelScope.launch {
            developerOptionsRepository.observeOverrides().collect { overrides ->
                sendIntent(DeveloperOptionsIntent.OnOverridesUpdated(overrides))
            }
        }
    }

    override fun handleIntent(intent: DeveloperOptionsIntent): Flow<PartialState> {
        return when (intent) {
            is DeveloperOptionsIntent.OnBackClicked -> {
                sendEvent(DeveloperOptionsEvent.NavigateBack)
                kotlinx.coroutines.flow.emptyFlow()
            }

            is DeveloperOptionsIntent.OnItemClicked -> flow {
                emit(PartialState.SetEditingKey(intent.key))
            }

            is DeveloperOptionsIntent.OnDialogDismissed -> flow {
                emit(PartialState.SetEditingKey(null))
            }

            is DeveloperOptionsIntent.OnOverridesUpdated -> flow {
                emit(PartialState.SetItems(buildItems(intent.overrides)))
            }

            is DeveloperOptionsIntent.OnUrlConfirmed -> flow {
                developerOptionsRepository.setOverride(intent.key, intent.url)
                emit(PartialState.SetEditingKey(null))
            }

            is DeveloperOptionsIntent.OnResetClicked -> flow {
                developerOptionsRepository.clearOverride(intent.key)
                emit(PartialState.SetEditingKey(null))
            }
        }
    }

    override fun reduceState(
        currentState: DeveloperOptionsUiState,
        partialState: PartialState
    ): DeveloperOptionsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.SetItems -> currentState.copy(items = partialState.items)
        is PartialState.SetEditingKey -> currentState.copy(editingKey = partialState.key)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private fun buildItems(overrides: Map<BaseUrlKey, String>): List<BaseUrlItemUi> =
        BaseUrlKey.entries.map { key ->
            BaseUrlItemUi(
                key = key,
                currentUrl = overrides[key] ?: key.defaultValue,
                isOverridden = overrides.containsKey(key),
                requiresRestart = BaseUrlPresets.requiresRestart.getValue(key)
            )
        }
}
