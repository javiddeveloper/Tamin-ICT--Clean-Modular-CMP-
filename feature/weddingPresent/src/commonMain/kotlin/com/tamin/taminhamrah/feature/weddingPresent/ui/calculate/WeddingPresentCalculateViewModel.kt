package com.tamin.taminhamrah.feature.weddingPresent.ui.calculate

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.weddingPresent.ui.calculate.WeddingPresentCalculateUiState.PartialState
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.useCases.weddingPresent.CalculateMarriageAllowanceUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.wedding_present_calc_error_no_amount
import taminx.core.core_ui.wedding_present_calc_error_select_date

class WeddingPresentCalculateViewModel(
    private val calculateMarriageAllowanceUseCase: CalculateMarriageAllowanceUseCase,
) : BaseViewModel<
    WeddingPresentCalculateUiState,
    PartialState,
    WeddingPresentCalculateEvent,
    WeddingPresentCalculateIntent,
>(
    initialState = WeddingPresentCalculateUiState(),
) {
    override fun handleIntent(intent: WeddingPresentCalculateIntent): Flow<PartialState> = flow {
        when (intent) {
            WeddingPresentCalculateIntent.OpenDatePicker ->
                emit(PartialState.ShowDatePicker(true))
            WeddingPresentCalculateIntent.DismissDatePicker ->
                emit(PartialState.ShowDatePicker(false))
            is WeddingPresentCalculateIntent.MarriageDatePicked -> {
                emit(PartialState.ClearResult)
                emit(PartialState.DateError(null))
                emit(
                    PartialState.MarriageDateSelected(
                        millis = intent.millis,
                        label = intent.label,
                    ),
                )
                emit(PartialState.ShowDatePicker(false))
            }
            WeddingPresentCalculateIntent.Calculate -> calculate()
            WeddingPresentCalculateIntent.Back ->
                sendEvent(WeddingPresentCalculateEvent.NavigateBack)
        }
    }.catch { error ->
        sendEvent(WeddingPresentCalculateEvent.ShowToast(error.toSingleLineMessage()))
        emit(createErrorState(error.toSingleLineMessage()))
    }

    private suspend fun FlowCollector<PartialState>.calculate() {
        val state = uiState.value
        val millis = state.marriageDateMillis
        if (millis == null) {
            emit(PartialState.DateError(getString(Res.string.wedding_present_calc_error_select_date)))
            return
        }
        emit(PartialState.Calculating(true))
        calculateMarriageAllowanceUseCase(millis.toString())
            .catch { error ->
                emit(PartialState.Calculating(false))
                sendEvent(WeddingPresentCalculateEvent.ShowToast(error.toSingleLineMessage()))
            }
            .collect { lines ->
                emit(PartialState.Calculating(false))
                val averageSalaryRaw = lines.getOrNull(0).orEmpty()
                val payableRaw = lines.getOrNull(1).orEmpty()
                if (payableRaw.isBlank()) {
                    val message = averageSalaryRaw.trim().ifBlank {
                        getString(Res.string.wedding_present_calc_error_no_amount)
                    }
                    sendEvent(WeddingPresentCalculateEvent.ShowToast(message))
                    return@collect
                }
                emit(
                    PartialState.ResultLoaded(
                        WeddingPresentCalcResultUi(
                            marriageDateLabel = state.marriageDateLabel,
                            averageSalaryLabel = averageSalaryRaw.toRialAmount(),
                            payableAmountLabel = payableRaw.toRialAmount(),
                        ),
                    ),
                )
            }
    }

    override fun reduceState(
        currentState: WeddingPresentCalculateUiState,
        partialState: PartialState,
    ): WeddingPresentCalculateUiState = when (partialState) {
        is PartialState.MarriageDateSelected -> currentState.copy(
            marriageDateMillis = partialState.millis,
            marriageDateLabel = partialState.label,
        )
        is PartialState.ShowDatePicker -> currentState.copy(showDatePicker = partialState.show)
        is PartialState.DateError -> currentState.copy(marriageDateError = partialState.message)
        is PartialState.Calculating -> currentState.copy(isCalculating = partialState.isCalculating)
        is PartialState.ResultLoaded -> currentState.copy(result = partialState.result)
        PartialState.ClearResult -> currentState.copy(result = null)
        is PartialState.Error -> currentState.copy(isCalculating = false)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
