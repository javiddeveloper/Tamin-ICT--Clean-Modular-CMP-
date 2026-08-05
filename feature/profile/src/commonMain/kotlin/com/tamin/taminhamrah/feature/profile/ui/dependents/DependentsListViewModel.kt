package com.tamin.taminhamrah.feature.profile.ui.dependents

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListEvent
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListIntent
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListState
import com.tamin.taminhamrah.feature.profile.ui.dependents.contract.DependentsListState.PartialState
import com.tamin.taminhamrah.mapper.subdominant.toPresentation
import com.tamin.taminhamrah.useCases.user.mockUseCases.MockSubdominantUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class DependentsListViewModel(
    private val subdominantUseCase: MockSubdominantUseCase,
) : BaseViewModel<DependentsListState, PartialState, DependentsListEvent, DependentsListIntent>(
    initialState = DependentsListState()
) {

    override fun handleIntent(intent: DependentsListIntent): Flow<PartialState> {
        return when (intent) {
            is DependentsListIntent.InitData,
            is DependentsListIntent.OnRefreshClicked -> loadDependentsList()
            is DependentsListIntent.OnAddDependentClicked -> flow {
                sendEvent(DependentsListEvent.NavigateToAddDependentWizard)
            }
        }
    }

    private fun loadDependentsList(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        subdominantUseCase()
            .map { subdominantDN ->
                val prList = subdominantDN.toPresentation().list
                PartialState.DependentsLoaded(prList) as PartialState
            }
            .catch { emit(PartialState.Error(it.message ?: "خطا در دریافت لیست افراد تبعی")) }
            .collect { emit(it) }
    }

    override fun reduceState(
        currentState: DependentsListState,
        partialState: PartialState
    ): DependentsListState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.DependentsLoaded -> currentState.copy(
            isLoading = false,
            dependentsList = partialState.dependents,
            error = null
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
