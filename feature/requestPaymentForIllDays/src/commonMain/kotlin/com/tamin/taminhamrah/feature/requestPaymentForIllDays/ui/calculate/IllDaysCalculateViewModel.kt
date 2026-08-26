package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate.IllDaysCalculateUiState.PartialState
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.CalcIllnessAmountUseCase
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.ill_days_calc_error_no_amount
import taminx.core.core_ui.ill_days_calc_marital_married_caption
import taminx.core.core_ui.ill_days_calc_marital_single_caption
import taminx.core.core_ui.ill_days_calc_rate_title_married
import taminx.core.core_ui.ill_days_calc_rate_title_single
import taminx.core.core_ui.ill_days_calc_rest_days_value
import taminx.core.core_ui.ill_days_error_end_before_start
import kotlin.math.max

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
                emit(PartialState.ClearResult)
                emit(
                    PartialState.StartDateSelected(
                        millis = intent.millis,
                        label = intent.label,
                        dayCount = computeDayCount(intent.millis, uiState.value.endDateMillis),
                    )
                )
                emit(PartialState.DatePickerChanged(IllDaysDatePicker.None))
            }
            is IllDaysCalculateIntent.EndDatePicked -> {
                emit(PartialState.ClearResult)
                emit(
                    PartialState.EndDateSelected(
                        millis = intent.millis,
                        label = intent.label,
                        dayCount = computeDayCount(uiState.value.startDateMillis, intent.millis),
                    )
                )
                emit(PartialState.DatePickerChanged(IllDaysDatePicker.None))
            }
            is IllDaysCalculateIntent.SelectMarital -> {
                emit(PartialState.ClearResult)
                emit(PartialState.MaritalSelected(intent.status))
            }
            IllDaysCalculateIntent.Calculate -> calculate()
            IllDaysCalculateIntent.Back -> sendEvent(IllDaysCalculateEvent.NavigateBack)
        }
    }.catch { error ->
        sendEvent(IllDaysCalculateEvent.ShowToast(error.toSingleLineMessage()))
        emit(createErrorState(error.toSingleLineMessage()))
    }

    private suspend fun FlowCollector<PartialState>.calculate() {
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
            val averageWageRaw = lines.getOrNull(0).orEmpty()
            val payableRaw = lines.getOrNull(1).orEmpty()
            if (payableRaw.isBlank()) {
                val message = averageWageRaw.ifBlank {
                    getString(Res.string.ill_days_calc_error_no_amount)
                }
                sendEvent(IllDaysCalculateEvent.ShowToast(message))
                return@collect
            }
            val dayCount = state.dayCount ?: computeDayCount(start, end) ?: 1
            emit(
                PartialState.ResultLoaded(
                    IllDaysCalcResultUi(
                        averageWageLabel = averageWageRaw.toRialAmount(),
                        payableAmountLabel = payableRaw.toRialAmount(),
                        restDaysLabel = getString(
                            Res.string.ill_days_calc_rest_days_value,
                            dayCount.toString().toPersianDigits(),
                        ),
                        rateTitle = when (marital) {
                            IllDaysMaritalStatus.Single ->
                                getString(Res.string.ill_days_calc_rate_title_single)
                            IllDaysMaritalStatus.Married ->
                                getString(Res.string.ill_days_calc_rate_title_married)
                        },
                        rateCaption = when (marital) {
                            IllDaysMaritalStatus.Single ->
                                getString(Res.string.ill_days_calc_marital_single_caption)
                            IllDaysMaritalStatus.Married ->
                                getString(Res.string.ill_days_calc_marital_married_caption)
                        },
                    )
                )
            )
        }
    }

    override fun reduceState(
        currentState: IllDaysCalculateUiState,
        partialState: PartialState,
    ): IllDaysCalculateUiState = when (partialState) {
        is PartialState.StartDateSelected -> currentState.copy(
            startDateMillis = partialState.millis,
            startDateLabel = partialState.label,
            dayCount = partialState.dayCount,
        )
        is PartialState.EndDateSelected -> currentState.copy(
            endDateMillis = partialState.millis,
            endDateLabel = partialState.label,
            dayCount = partialState.dayCount,
        )
        is PartialState.MaritalSelected -> currentState.copy(maritalStatus = partialState.status)
        is PartialState.DatePickerChanged -> currentState.copy(activeDatePicker = partialState.picker)
        is PartialState.Calculating -> currentState.copy(isCalculating = partialState.isCalculating)
        is PartialState.ResultLoaded -> currentState.copy(result = partialState.result)
        PartialState.ClearResult -> currentState.copy(result = null)
        is PartialState.Error -> currentState.copy(isCalculating = false)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private fun computeDayCount(startMillis: Long?, endMillis: Long?): Int? {
        if (startMillis == null || endMillis == null || endMillis < startMillis) return null
        val days = ((endMillis - startMillis) / MILLIS_PER_DAY).toInt() + 1
        return max(days, 1)
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
