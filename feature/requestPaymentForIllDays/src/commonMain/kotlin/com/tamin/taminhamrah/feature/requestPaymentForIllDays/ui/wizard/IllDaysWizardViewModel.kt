package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard.IllDaysWizardUiState.PartialState
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.requestPaymentForIllDays.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetCovidResultUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetIllDaysInsuredMainInfoUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.ill_days_error_covid_dates_missing
import taminx.core.core_ui.ill_days_wizard_next_steps_soon
import kotlin.math.max

class IllDaysWizardViewModel(
    private val getIllDaysInsuredMainInfoUseCase: GetIllDaysInsuredMainInfoUseCase,
    private val getCitiesUseCase: GetCitiesUseCase,
    private val getCovidResultUseCase: GetCovidResultUseCase,
) : BaseViewModel<IllDaysWizardUiState, PartialState, IllDaysWizardEvent, IllDaysWizardIntent>(
    initialState = IllDaysWizardUiState(),
) {
    init {
        sendIntent(IllDaysWizardIntent.Load)
    }

    override fun handleIntent(intent: IllDaysWizardIntent): Flow<PartialState> = flow {
        when (intent) {
            IllDaysWizardIntent.Load,
            IllDaysWizardIntent.Retry -> loadInitial()
            IllDaysWizardIntent.OpenBranchPicker ->
                emit(PartialState.PickerChanged(IllDaysWizardPicker.Branch))
            IllDaysWizardIntent.OpenCityPicker -> openCityPicker()
            IllDaysWizardIntent.DismissPicker ->
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            is IllDaysWizardIntent.BranchPicked -> {
                emit(PartialState.BranchSelected(intent.branch))
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            }
            is IllDaysWizardIntent.CityPicked -> {
                emit(PartialState.CitySelected(intent.city))
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            }
            IllDaysWizardIntent.NextStep -> handleNext()
            IllDaysWizardIntent.PreviousStep -> handlePrevious()
            is IllDaysWizardIntent.CovidChanged -> handleCovidToggle(intent.enabled)
            IllDaysWizardIntent.OpenStartDatePicker -> {
                if (!uiState.value.isCovid) {
                    emit(PartialState.PickerChanged(IllDaysWizardPicker.StartDate))
                }
            }
            IllDaysWizardIntent.OpenEndDatePicker -> {
                if (!uiState.value.isCovid) {
                    emit(PartialState.PickerChanged(IllDaysWizardPicker.EndDate))
                }
            }
            is IllDaysWizardIntent.StartDatePicked -> {
                val end = uiState.value.endDateMillis
                emit(
                    PartialState.RestDatesSet(
                        startMillis = intent.millis,
                        startLabel = intent.label,
                        endMillis = end,
                        endLabel = uiState.value.endDateLabel,
                        dayCount = computeDayCount(intent.millis, end),
                    )
                )
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            }
            is IllDaysWizardIntent.EndDatePicked -> {
                val start = uiState.value.startDateMillis
                emit(
                    PartialState.RestDatesSet(
                        startMillis = start,
                        startLabel = uiState.value.startDateLabel,
                        endMillis = intent.millis,
                        endLabel = intent.label,
                        dayCount = computeDayCount(start, intent.millis),
                    )
                )
                emit(PartialState.PickerChanged(IllDaysWizardPicker.None))
            }
            IllDaysWizardIntent.OpenCalculate -> sendEvent(IllDaysWizardEvent.NavigateToCalculate)
            IllDaysWizardIntent.Back -> {
                when (uiState.value.currentStep) {
                    IllDaysWizardStep.BranchCity -> sendEvent(IllDaysWizardEvent.NavigateBack)
                    IllDaysWizardStep.RestDays ->
                        emit(PartialState.StepChanged(IllDaysWizardStep.BranchCity))
                }
            }
        }
    }.catch { error ->
        sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
        emit(createErrorState(error.toSingleLineMessage()))
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.loadInitial() {
        emit(PartialState.Loading(true))
        try {
            val info = getIllDaysInsuredMainInfoUseCase().first()?.toPresentation()
            val branches = info?.branchWorkshops.orEmpty().toImmutableList()
            val selected = branches.firstOrNull()
            emit(PartialState.BranchesLoaded(branches = branches, selected = selected))
            val cities = getCitiesUseCase().first().toCityPresentation().toImmutableList()
            emit(PartialState.CitiesLoaded(cities))
            emit(PartialState.Loading(false))
        } catch (error: Throwable) {
            emit(PartialState.Loading(false))
            emit(PartialState.Error(error.toSingleLineMessage()))
            sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.openCityPicker() {
        if (uiState.value.cityOptions.isEmpty()) {
            emit(PartialState.Loading(true))
            try {
                val cities = getCitiesUseCase().first().toCityPresentation().toImmutableList()
                emit(PartialState.CitiesLoaded(cities))
                emit(PartialState.Loading(false))
            } catch (error: Throwable) {
                emit(PartialState.Loading(false))
                sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
                return
            }
        }
        emit(PartialState.PickerChanged(IllDaysWizardPicker.City))
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.handleNext() {
        when (uiState.value.currentStep) {
            IllDaysWizardStep.BranchCity -> {
                if (!uiState.value.canGoNextFromStep1) return
                emit(PartialState.StepChanged(IllDaysWizardStep.RestDays))
            }
            IllDaysWizardStep.RestDays -> {
                if (!uiState.value.canGoNextFromStep2) return
                // Steps 3–4 arrive in a later phase.
                sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_wizard_next_steps_soon)))
            }
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.handlePrevious() {
        when (uiState.value.currentStep) {
            IllDaysWizardStep.BranchCity -> sendEvent(IllDaysWizardEvent.NavigateBack)
            IllDaysWizardStep.RestDays ->
                emit(PartialState.StepChanged(IllDaysWizardStep.BranchCity))
        }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<PartialState>.handleCovidToggle(
        enabled: Boolean,
    ) {
        emit(PartialState.CovidToggled(enabled))
        if (!enabled) {
            emit(
                PartialState.RestDatesSet(
                    startMillis = null,
                    startLabel = "",
                    endMillis = null,
                    endLabel = "",
                    dayCount = null,
                )
            )
            return
        }
        emit(PartialState.CovidLoading(true))
        try {
            val covid = getCovidResultUseCase().first().toPresentation()
            val startMillis = covid.startDateTimeStamp.toEpochMillisOrNull()
            val endMillis = covid.endDateTimeStamp.toEpochMillisOrNull()
            emit(
                PartialState.RestDatesSet(
                    startMillis = startMillis,
                    startLabel = startMillis?.let { PersianDateFormatter.formatTimestamp(it) }.orEmpty(),
                    endMillis = endMillis,
                    endLabel = endMillis?.let { PersianDateFormatter.formatTimestamp(it) }.orEmpty(),
                    dayCount = computeDayCount(startMillis, endMillis),
                )
            )
            emit(PartialState.CovidLoading(false))
            if (startMillis == null || endMillis == null) {
                sendEvent(IllDaysWizardEvent.ShowToast(getString(Res.string.ill_days_error_covid_dates_missing)))
            }
        } catch (error: Throwable) {
            emit(PartialState.CovidLoading(false))
            emit(PartialState.CovidToggled(false))
            sendEvent(IllDaysWizardEvent.ShowToast(error.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: IllDaysWizardUiState,
        partialState: PartialState,
    ): IllDaysWizardUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            errorMessage = if (partialState.isLoading) null else currentState.errorMessage,
        )
        is PartialState.CovidLoading -> currentState.copy(isCovidLoading = partialState.isLoading)
        is PartialState.BranchesLoaded -> currentState.copy(
            branchOptions = partialState.branches,
            selectedBranch = partialState.selected,
        )
        is PartialState.CitiesLoaded -> currentState.copy(cityOptions = partialState.cities)
        is PartialState.BranchSelected -> currentState.copy(selectedBranch = partialState.branch)
        is PartialState.CitySelected -> currentState.copy(selectedCity = partialState.city)
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.PickerChanged -> currentState.copy(picker = partialState.picker)
        is PartialState.CovidToggled -> currentState.copy(isCovid = partialState.enabled)
        is PartialState.RestDatesSet -> currentState.copy(
            startDateMillis = partialState.startMillis,
            startDateLabel = partialState.startLabel,
            endDateMillis = partialState.endMillis,
            endDateLabel = partialState.endLabel,
            dayCount = partialState.dayCount,
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            errorMessage = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private fun computeDayCount(startMillis: Long?, endMillis: Long?): Int? {
        if (startMillis == null || endMillis == null || endMillis < startMillis) return null
        val days = ((endMillis - startMillis) / MILLIS_PER_DAY).toInt() + 1
        return max(days, 1)
    }

    private fun String.toEpochMillisOrNull(): Long? {
        val value = toLongOrNull() ?: return null
        return if (value < SECONDS_THRESHOLD) value * 1000L else value
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
        const val SECONDS_THRESHOLD = 10_000_000_000L
    }
}
