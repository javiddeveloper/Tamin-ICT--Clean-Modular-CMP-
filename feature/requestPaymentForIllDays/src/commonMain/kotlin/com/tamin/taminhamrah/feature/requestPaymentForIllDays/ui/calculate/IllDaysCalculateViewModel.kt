package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate.IllDaysCalculateUiState.PartialState
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.CalcIllnessAmountUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.ill_days_error_end_before_start

class IllDaysCalculateViewModel(
    private val calcIllnessAmountUseCase: CalcIllnessAmountUseCase,
) : BaseViewModel<IllDaysCalculateUiState, PartialState, IllDaysCalculateEvent, IllDaysCalculateIntent>(
    initialState = IllDaysCalculateUiState(),
) {
    override fun handleIntent(intent: IllDaysCalculateIntent): Flow<PartialState> = flow {
        when (intent) {
            IllDaysCalculateIntent.OpenStartDatePicker ->
                emit(PartialState.DatePickerChanged(IllDaysDatePicker.Start))
            IllDaysCalculateIntent.OpenEndDatePicker ->
                emit(PartialState.DatePickerChanged(IllDaysDatePicker.End))
            IllDaysCalculateIntent.DismissDatePicker ->
                emit(PartialState.DatePickerChanged(IllDaysDatePicker.None))
            is IllDaysCalculateIntent.StartDatePicked -> {
                emit(PartialState.StartDateSelected(intent.millis, intent.label))
                emit(PartialState.DatePickerChanged(IllDaysDatePicker.None))
            }
            is IllDaysCalculateIntent.EndDatePicked -> {
                emit(PartialState.EndDateSelected(intent.millis, intent.label))
                emit(PartialState.DatePickerChanged(IllDaysDatePicker.None))
            }
            is IllDaysCalculateIntent.SelectMarital ->
                emit(PartialState.MaritalSelected(intent.status))
            IllDaysCalculateIntent.Calculate -> calculate()
            IllDaysCalculateIntent.DismissResult -> emit(PartialState.DismissResult)
            IllDaysCalculateIntent.Back -> sendEvent(IllDaysCalculateEvent.NavigateBack)
        }
    }.catch { error ->
        sendEvent(IllDaysCalculateEvent.ShowToast(error.toSingleLineMessage()))
        emit(createErrorState(error.toSingleLineMessage()))
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.calculate() {
        val state = uiState.value
        val start = state.startDateMillis
        val end = state.endDateMillis
        val marital = state.maritalStatus
        if (start == null || end == null || marital == null) return
        if (end < start) {
            sendEvent(IllDaysCalculateEvent.ShowToast(getString(Res.string.ill_days_error_end_before_start)))
            return
        }
        emit(PartialState.Calculating(true))
        calcIllnessAmountUseCase(
            startDateTimeStamp = start.toString(),
            endDateTimeStamp = end.toString(),
            maritalStatus = marital.apiValue,
        ).catch { error ->
            emit(PartialState.Calculating(false))
            sendEvent(IllDaysCalculateEvent.ShowToast(error.toSingleLineMessage()))
        }.collect { lines ->
            emit(PartialState.Calculating(false))
            emit(PartialState.ResultLoaded(lines))
        }
    }

    override fun reduceState(
        currentState: IllDaysCalculateUiState,
        partialState: PartialState,
    ): IllDaysCalculateUiState = when (partialState) {
        is PartialState.StartDateSelected -> currentState.copy(
            startDateMillis = partialState.millis,
            startDateLabel = partialState.label,
        )
        is PartialState.EndDateSelected -> currentState.copy(
            endDateMillis = partialState.millis,
            endDateLabel = partialState.label,
        )
        is PartialState.MaritalSelected -> currentState.copy(maritalStatus = partialState.status)
        is PartialState.DatePickerChanged -> currentState.copy(activeDatePicker = partialState.picker)
        is PartialState.Calculating -> currentState.copy(isCalculating = partialState.isCalculating)
        is PartialState.ResultLoaded -> currentState.copy(
            resultLines = partialState.lines,
            showResultDialog = true,
        )
        PartialState.DismissResult -> currentState.copy(
            showResultDialog = false,
            resultLines = emptyList(),
        )
        is PartialState.Error -> currentState.copy(isCalculating = false)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
