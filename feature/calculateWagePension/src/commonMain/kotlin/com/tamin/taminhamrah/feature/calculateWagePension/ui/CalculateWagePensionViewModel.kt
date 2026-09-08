package com.tamin.taminhamrah.feature.calculateWagePension.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionEvent
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionIntent
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionUiState
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionUiState.PartialState
import com.tamin.taminhamrah.mapper.calculateWagePension.toPresentation
import com.tamin.taminhamrah.model.calculateWagePension.MULTIPLE_WORKSHOPS_YES
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.calculateWagePension.CalculateMultipleWorkshopsPensionUseCase
import com.tamin.taminhamrah.useCases.calculateWagePension.CalculateWagePensionUseCase
import com.tamin.taminhamrah.useCases.calculateWagePension.CheckMultipleWorkshopsUseCase
import com.tamin.taminhamrah.useCases.calculateWagePension.GetMultipleWorkshopPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CalculateWagePensionViewModel(
    private val getTalfighInfosUseCase: GetTalfighInfosUseCase,
    private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase,
    private val calculateWagePensionUseCase: CalculateWagePensionUseCase,
    private val getMultipleWorkshopPersonalInfoUseCase: GetMultipleWorkshopPersonalInfoUseCase,
    private val checkMultipleWorkshopsUseCase: CheckMultipleWorkshopsUseCase,
    private val calculateMultipleWorkshopsPensionUseCase: CalculateMultipleWorkshopsPensionUseCase,
) : BaseViewModel<CalculateWagePensionUiState, PartialState, CalculateWagePensionEvent, CalculateWagePensionIntent>(
    initialState = CalculateWagePensionUiState()
) {
    init {
        sendIntent(CalculateWagePensionIntent.Load)
    }

    override fun handleIntent(intent: CalculateWagePensionIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(CalculateWagePensionEvent.ShowToast(e.toSingleLineMessage()))
            emit(createErrorState(e.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: CalculateWagePensionIntent): Flow<PartialState> = flow {
        when (intent) {
            CalculateWagePensionIntent.Load,
            CalculateWagePensionIntent.Retry -> loadCalculation()
            is CalculateWagePensionIntent.MultipleWorkshopsToggled -> {
                if (intent.enabled) {
                    enableMultipleWorkshops()
                } else {
                    disableMultipleWorkshops()
                }
            }
            is CalculateWagePensionIntent.ChartYearSelected ->
                emit(PartialState.ChartYearSelected(intent.index))
            CalculateWagePensionIntent.ShowInfo ->
                emit(PartialState.InfoDialogVisibility(true))
            CalculateWagePensionIntent.DismissInfo ->
                emit(PartialState.InfoDialogVisibility(false))
            CalculateWagePensionIntent.DismissMultipleWorkshopsInfo -> {
                emit(PartialState.MultipleWorkshopsInfoDialogVisibility(false))
                emit(PartialState.MultipleWorkshopsEnabled(false))
                restoreBaseAmount()
            }
        }
    }

    private suspend fun FlowCollector<PartialState>.loadCalculation() {
        if (uiState.value.isLoading) return
        emit(PartialState.Loading(true))
        val talfigh = getTalfighInfosUseCase()
        val dastmozd = getDastmozdInfosUseCase()
        val calculation = calculateWagePensionUseCase(talfigh, dastmozd).toPresentation()
        emit(PartialState.CalculationLoaded(calculation))
        emit(PartialState.DisplayedAmountChanged(calculation.eligibleAmountPension))
        emit(PartialState.MultipleWorkshopsEnabled(false))
        emit(PartialState.Loading(false))
    }

    private suspend fun FlowCollector<PartialState>.enableMultipleWorkshops() {
        if (uiState.value.isMultipleWorkshopLoading) return
        emit(PartialState.MultipleWorkshopLoading(true))
        emit(PartialState.MultipleWorkshopsEnabled(true))
        try {
            val personalInfo = getMultipleWorkshopPersonalInfoUseCase().first()
            val check = checkMultipleWorkshopsUseCase(
                branchCode = personalInfo.branchCode,
                insuranceNumber = personalInfo.insuranceNumber,
            ).first()
            if (check.result != MULTIPLE_WORKSHOPS_YES) {
                emit(PartialState.MultipleWorkshopLoading(false))
                emit(PartialState.MultipleWorkshopsInfoDialogVisibility(true))
                return
            }
            val calculated = calculateMultipleWorkshopsPensionUseCase(
                branchCode = personalInfo.branchCode,
                insuranceNumber = personalInfo.insuranceNumber,
            ).first()
            emit(PartialState.DisplayedAmountChanged(calculated.pensionAmount))
            emit(PartialState.MultipleWorkshopLoading(false))
        } catch (e: Exception) {
            emit(PartialState.MultipleWorkshopLoading(false))
            emit(PartialState.MultipleWorkshopsEnabled(false))
            restoreBaseAmount()
            throw e
        }
    }

    private suspend fun FlowCollector<PartialState>.disableMultipleWorkshops() {
        emit(PartialState.MultipleWorkshopsEnabled(false))
        restoreBaseAmount()
    }

    private suspend fun FlowCollector<PartialState>.restoreBaseAmount() {
        val base = uiState.value.calculation?.eligibleAmountPension ?: 0L
        emit(PartialState.DisplayedAmountChanged(base))
    }

    override fun reduceState(
        currentState: CalculateWagePensionUiState,
        partialState: PartialState,
    ): CalculateWagePensionUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.MultipleWorkshopLoading ->
            currentState.copy(isMultipleWorkshopLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isMultipleWorkshopLoading = false,
            error = partialState.message,
        )
        is PartialState.CalculationLoaded -> currentState.copy(
            calculation = partialState.calculation,
            error = null,
        )
        is PartialState.DisplayedAmountChanged ->
            currentState.copy(displayedEligibleAmount = partialState.amount)
        is PartialState.MultipleWorkshopsEnabled ->
            currentState.copy(isMultipleWorkshopsEnabled = partialState.enabled)
        is PartialState.ChartYearSelected ->
            currentState.copy(selectedChartYearIndex = partialState.index)
        is PartialState.InfoDialogVisibility ->
            currentState.copy(showInfoDialog = partialState.visible)
        is PartialState.MultipleWorkshopsInfoDialogVisibility ->
            currentState.copy(showMultipleWorkshopsInfoDialog = partialState.visible)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
