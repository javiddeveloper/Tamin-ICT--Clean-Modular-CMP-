package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.AddressError
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState.PartialState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.LandlinePhoneError
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.addDependent.RefreshDependentsUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
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
    private val getUserAgeUseCase: GetUserAgeUseCase,
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
            DisabilityPensionIntent.PreviousStepClicked -> handlePreviousStepClicked()
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
            DisabilityPensionIntent.ToggleIdentityDetails -> {
                emit(PartialState.IdentityDetailsExpandedChanged(!uiState.value.isIdentityDetailsExpanded))
            }
            is DisabilityPensionIntent.LandlinePhoneChanged -> {
                emit(PartialState.LandlinePhoneChanged(intent.value, null))
            }
            is DisabilityPensionIntent.AddressChanged -> {
                emit(PartialState.AddressChanged(intent.value, null))
            }
            is DisabilityPensionIntent.IdentityConfirmedChanged -> {
                emit(PartialState.IdentityConfirmedChanged(intent.accepted))
                if (intent.accepted) {
                    emit(PartialState.IdentityConfirmationErrorChanged(false))
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
                    emit(PartialState.StepChanged(DisabilityPensionStep.IdentityContact))
                } else {
                    emit(PartialState.DependentsConfirmationErrorChanged(true))
                }
            }
            DisabilityPensionStep.IdentityContact -> handleIdentityContactNextStep()
        }
    }

    private suspend fun FlowCollector<PartialState>.handleIdentityContactNextStep() {
        val state = uiState.value
        val phoneError = validateLandlinePhone(state.landlinePhone)
        val addressError = validateAddress(state.address)
        emit(PartialState.LandlinePhoneChanged(state.landlinePhone, phoneError))
        emit(PartialState.AddressChanged(state.address, addressError))

        if (phoneError != null || addressError != null) return

        if (state.isIdentityConfirmed) {
            emit(PartialState.IdentityConfirmationErrorChanged(false))
            // TODO(EM-2619): navigate to step 4 once its design is delivered.
        } else {
            emit(PartialState.IdentityConfirmationErrorChanged(true))
        }
    }

    private suspend fun FlowCollector<PartialState>.handlePreviousStepClicked() {
        when (uiState.value.currentStep) {
            DisabilityPensionStep.Dependents -> emit(PartialState.StepChanged(DisabilityPensionStep.Terms))
            DisabilityPensionStep.IdentityContact -> emit(PartialState.StepChanged(DisabilityPensionStep.Dependents))
            DisabilityPensionStep.Terms -> Unit
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
        is PartialState.IdentityLoaded -> currentState.copy(identityInfo = partialState.info)
        is PartialState.IdentityAgeLoaded -> currentState.copy(identityAgeYears = partialState.years)
        is PartialState.IdentityDetailsExpandedChanged -> currentState.copy(
            isIdentityDetailsExpanded = partialState.expanded,
        )
        is PartialState.LandlinePhoneChanged -> currentState.copy(
            landlinePhone = partialState.value,
            landlinePhoneError = partialState.error,
        )
        is PartialState.AddressChanged -> currentState.copy(
            address = partialState.value,
            addressError = partialState.error,
        )
        is PartialState.IdentityConfirmedChanged -> currentState.copy(isIdentityConfirmed = partialState.accepted)
        is PartialState.IdentityConfirmationErrorChanged -> currentState.copy(
            showIdentityConfirmationError = partialState.show,
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
            emit(PartialState.IdentityLoaded(info.toPresentation()))
            emit(PartialState.IdentityAgeLoaded(loadAgeYears(personal?.dateOfBirth)))
        }
    }

    private suspend fun loadAgeYears(birthDate: Long?): String {
        if (birthDate == null) return ""
        var years = ""
        getUserAgeUseCase(
            listOf(
                ApiFilterDN(
                    property = FilterProperty.BIRTH_DATE,
                    value = birthDate.toString(),
                    operator = FilterOperator.EQUAL,
                ),
            ),
        ).collect { age ->
            years = age.age?.split(",")?.getOrNull(0)?.trim().orEmpty()
        }
        return years
    }

    private suspend fun FlowCollector<PartialState>.loadDependents() {
        emit(PartialState.DependentsLoading(true))
        getDisabilityDependentInfoUseCase(emptyList()).collect { dependents ->
            emit(PartialState.DependentsLoaded(dependents.toPresentation().toImmutableList()))
        }
    }

    private fun validateLandlinePhone(value: String): LandlinePhoneError? = when {
        value.isBlank() -> LandlinePhoneError.Blank
        !value.startsWith("0") -> LandlinePhoneError.InvalidPrefix
        value.length != LANDLINE_PHONE_LENGTH -> LandlinePhoneError.InvalidLength
        else -> null
    }

    private fun validateAddress(value: String): AddressError? = when {
        value.isBlank() -> AddressError.Blank
        value.length < MIN_ADDRESS_LENGTH -> AddressError.TooShort
        INVALID_ADDRESS_CHARACTERS.containsMatchIn(value) -> AddressError.InvalidCharacters
        else -> null
    }

    private companion object {
        const val MALE_TITLE = "آقای"
        const val FEMALE_TITLE = "خانم"
        const val LANDLINE_PHONE_LENGTH = 11
        const val MIN_ADDRESS_LENGTH = 10
        val INVALID_ADDRESS_CHARACTERS = Regex("[a-zA-Z$&+:;=?@#|/'<>.^*()%!\\\\]")
    }
}
