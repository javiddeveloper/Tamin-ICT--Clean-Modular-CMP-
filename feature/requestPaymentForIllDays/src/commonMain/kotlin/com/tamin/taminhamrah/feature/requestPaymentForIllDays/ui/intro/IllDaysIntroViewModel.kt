package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro.IllDaysIntroUiState.PartialState
import com.tamin.taminhamrah.mapper.requestPaymentForIllDays.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetIllDaysInsuredMainInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.ill_days_error_info_not_found
import taminx.core.core_ui.ill_days_error_info_not_loaded

class IllDaysIntroViewModel(
    private val getIllDaysInsuredMainInfoUseCase: GetIllDaysInsuredMainInfoUseCase,
) : BaseViewModel<IllDaysIntroUiState, PartialState, IllDaysIntroEvent, IllDaysIntroIntent>(
    initialState = IllDaysIntroUiState(),
) {
    init {
        sendIntent(IllDaysIntroIntent.Load)
    }

    override fun handleIntent(intent: IllDaysIntroIntent): Flow<PartialState> = flow {
        when (intent) {
            IllDaysIntroIntent.Load,
            IllDaysIntroIntent.Retry -> loadInsuredInfo()
            IllDaysIntroIntent.OpenCalculate -> sendEvent(IllDaysIntroEvent.NavigateToCalculate)
            IllDaysIntroIntent.StartRequest -> {
                if (uiState.value.insuredInfo == null) {
                    sendEvent(IllDaysIntroEvent.ShowToast(getString(Res.string.ill_days_error_info_not_loaded)))
                } else {
                    sendEvent(IllDaysIntroEvent.NavigateToWizard)
                }
            }
            IllDaysIntroIntent.Back -> sendEvent(IllDaysIntroEvent.NavigateBack)
        }
    }.catch { error ->
        sendEvent(IllDaysIntroEvent.ShowToast(error.toSingleLineMessage()))
        emit(createErrorState(error.toSingleLineMessage()))
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.loadInsuredInfo() {
        emit(PartialState.Loading(true))
        getIllDaysInsuredMainInfoUseCase()
            .catch { error ->
                emit(PartialState.Loading(false))
                emit(PartialState.Error(error.toSingleLineMessage()))
                sendEvent(IllDaysIntroEvent.ShowToast(error.toSingleLineMessage()))
            }
            .collect { info ->
                emit(PartialState.Loading(false))
                if (info == null) {
                    emit(PartialState.Error(getString(Res.string.ill_days_error_info_not_found)))
                } else {
                    emit(PartialState.Loaded(info.toPresentation()))
                }
            }
    }

    override fun reduceState(
        currentState: IllDaysIntroUiState,
        partialState: PartialState,
    ): IllDaysIntroUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            errorMessage = if (partialState.isLoading) null else currentState.errorMessage,
        )
        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            insuredInfo = partialState.insuredInfo,
            errorMessage = null,
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            errorMessage = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
