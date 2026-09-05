package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.addDependent.RefreshDependentsUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetDisabilityDependentInfoUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DisabilityPensionViewModel(
    private val getDisabilityPersonalInfoUseCase: GetDisabilityPersonalInfoUseCase,
    private val getDisabilityDependentInfoUseCase: GetDisabilityDependentInfoUseCase,
    private val refreshDependentsUseCase: RefreshDependentsUseCase,
) : BaseViewModel<DisabilityPensionUiState, PartialState, DisabilityPensionEvent, DisabilityPensionIntent>(
    initialState = DisabilityPensionUiState()
) {

    init {
        sendIntent(DisabilityPensionIntent.Init)
    }

    override fun handleIntent(intent: DisabilityPensionIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(DisabilityPensionEvent.ShowToast(e.toSingleLineMessage()))
            emit(createErrorState(e.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: DisabilityPensionIntent): Flow<PartialState> = flow {
        when (intent) {
            DisabilityPensionIntent.Init -> loadApplicantInfo()
            is DisabilityPensionIntent.TermsAcceptedChanged -> {
                emit(PartialState.TermsAcceptedChanged(intent.accepted))
                if (intent.accepted) {
                    emit(PartialState.TermsValidationErrorChanged(false))
                }
            }
            DisabilityPensionIntent.ShowRulesClicked -> emit(PartialState.RulesVisibilityChanged(true))
            DisabilityPensionIntent.DismissRules -> emit(PartialState.RulesVisibilityChanged(false))
            DisabilityPensionIntent.NextStepClicked -> handleNextStepClicked()
            DisabilityPensionIntent.PreviousStepClicked -> {
                emit(PartialState.StepChanged(DisabilityPensionStep.Terms))
            }
            is DisabilityPensionIntent.DependentCardToggled -> {
                emit(PartialState.DependentCardToggled(intent.id))
            }
            is DisabilityPensionIntent.DependentsListConfirmedChanged -> {
                emit(PartialState.DependentsConfirmedChanged(intent.accepted))
                if (intent.accepted) {
                    emit(PartialState.DependentsConfirmationErrorChanged(false))
                }
            }
            DisabilityPensionIntent.AddDependentClicked -> {
                sendEvent(DisabilityPensionEvent.NavigateToAddDependent)
            }
            DisabilityPensionIntent.RefreshDependentsClicked -> {
                emit(PartialState.RefreshConfirmDialogVisibilityChanged(true))
            }
            DisabilityPensionIntent.DismissRefreshConfirm -> {
                emit(PartialState.RefreshConfirmDialogVisibilityChanged(false))
            }
            DisabilityPensionIntent.ConfirmRefreshDependents -> confirmRefreshDependents()
            DisabilityPensionIntent.DependentsResumed -> {
                if (uiState.value.currentStep == DisabilityPensionStep.Dependents) {
                    loadDependents()
                }
            }
        }
    }

    private suspend fun FlowCollector<PartialState>.handleNextStepClicked() {
        when (uiState.value.currentStep) {
            DisabilityPensionStep.Terms -> {
                if (uiState.value.isTermsAccepted) {
                    emit(PartialState.TermsValidationErrorChanged(false))
                    emit(PartialState.StepChanged(DisabilityPensionStep.Dependents))
                    loadDependents()
                } else {
                    emit(PartialState.TermsValidationErrorChanged(true))
                }
            }
            DisabilityPensionStep.Dependents -> {
                if (uiState.value.isDependentsListConfirmed) {
                    emit(PartialState.DependentsConfirmationErrorChanged(false))
                    // TODO(EM-2619): navigate to step 3 once its design is delivered.
                } else {
                    emit(PartialState.DependentsConfirmationErrorChanged(true))
                }
            }
        }
    }

    private suspend fun FlowCollector<PartialState>.confirmRefreshDependents() {
        if (uiState.value.isRefreshingDependents) return
        emit(PartialState.RefreshConfirmDialogVisibilityChanged(false))
        emit(PartialState.RefreshingDependentsChanged(true))
        refreshDependentsUseCase().collect { result ->
            sendEvent(DisabilityPensionEvent.ShowToast(result.message.orEmpty()))
        }
        emit(PartialState.RefreshingDependentsChanged(false))
        loadDependents()
    }

    override fun reduceState(
        currentState: DisabilityPensionUiState,
        partialState: PartialState
    ): DisabilityPensionUiState = when (partialState) {
        is PartialState.ProfileLoading -> currentState.copy(isProfileLoading = partialState.isProfileLoading)
        is PartialState.ApplicantInfoLoaded -> currentState.copy(
            isProfileLoading = false,
            applicantGenderTitle = partialState.genderTitle,
            applicantFullName = partialState.fullName,
        )
        is PartialState.TermsAcceptedChanged -> currentState.copy(isTermsAccepted = partialState.accepted)
        is PartialState.TermsValidationErrorChanged -> currentState.copy(showTermsValidationError = partialState.show)
        is PartialState.RulesVisibilityChanged -> currentState.copy(showRules = partialState.show)
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.DependentsLoading -> currentState.copy(isDependentsLoading = partialState.isLoading)
        is PartialState.DependentsLoaded -> currentState.copy(
            isDependentsLoading = false,
            dependents = partialState.dependents,
        )
        is PartialState.DependentCardToggled -> currentState.copy(
            expandedDependentIds = if (partialState.id in currentState.expandedDependentIds) {
                currentState.expandedDependentIds - partialState.id
            } else {
                currentState.expandedDependentIds + partialState.id
            }.toPersistentSet(),
        )
        is PartialState.DependentsConfirmedChanged -> currentState.copy(isDependentsListConfirmed = partialState.accepted)
        is PartialState.DependentsConfirmationErrorChanged -> currentState.copy(
            showDependentsConfirmationError = partialState.show,
        )
        is PartialState.RefreshConfirmDialogVisibilityChanged -> currentState.copy(
            showRefreshConfirmDialog = partialState.show,
        )
        is PartialState.RefreshingDependentsChanged -> currentState.copy(
            isRefreshingDependents = partialState.isRefreshing,
        )
        is PartialState.Error -> currentState.copy(
            isProfileLoading = false,
            isDependentsLoading = false,
            isRefreshingDependents = false,
            error = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private suspend fun FlowCollector<PartialState>.loadApplicantInfo() {
        emit(PartialState.ProfileLoading(true))
        getDisabilityPersonalInfoUseCase().collect { info ->
            val personal = info.personal
            val fullName = listOfNotNull(personal?.firstName, personal?.lastName)
                .joinToString(" ")
                .ifBlank { "-" }
            val genderTitle = if (personal?.genderDesc?.contains("زن") == true) {
                FEMALE_TITLE
            } else {
                MALE_TITLE
            }
            emit(PartialState.ApplicantInfoLoaded(genderTitle = genderTitle, fullName = fullName))
        }
    }

    private suspend fun FlowCollector<PartialState>.loadDependents() {
        emit(PartialState.DependentsLoading(true))
        getDisabilityDependentInfoUseCase(emptyList()).collect { dependents ->
            emit(PartialState.DependentsLoaded(dependents.toPresentation().toImmutableList()))
        }
    }

    private companion object {
        const val MALE_TITLE = "آقای"
        const val FEMALE_TITLE = "خانم"
    }
}
