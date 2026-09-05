package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState.PartialState
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DisabilityPensionViewModel(
    private val getDisabilityPersonalInfoUseCase: GetDisabilityPersonalInfoUseCase,
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
            DisabilityPensionIntent.NextStepClicked -> {
                if (uiState.value.isTermsAccepted) {
                    emit(PartialState.TermsValidationErrorChanged(false))
                    // TODO(EM-2619): navigate to step 2 once its design is delivered.
                } else {
                    emit(PartialState.TermsValidationErrorChanged(true))
                }
            }
        }
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
        is PartialState.Error -> currentState.copy(isProfileLoading = false, error = partialState.message)
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

    private companion object {
        const val MALE_TITLE = "آقای"
        const val FEMALE_TITLE = "خانم"
    }
}
