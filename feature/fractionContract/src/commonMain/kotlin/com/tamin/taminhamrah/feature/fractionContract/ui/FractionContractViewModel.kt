package com.tamin.taminhamrah.feature.fractionContract.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractEvent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractIntent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState.PartialState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractStep
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.fractionEligibilityGateError
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.mapper.fractionContract.toPresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.contracts.SaveContactUseCase
import com.tamin.taminhamrah.useCases.fractionContract.CheckFractionAgeAndHistoryUseCase
import com.tamin.taminhamrah.useCases.fractionContract.MakeFractionContractUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.ValidationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.fraction_contract_error_active_fraction
import taminx.core.core_ui.fraction_contract_error_eligibility_unavailable
import taminx.core.core_ui.fraction_contract_error_not_primary_insured
import taminx.core.core_ui.fraction_contract_error_under_18

class FractionContractViewModel(
    private val getRegistrationInfoUseCase: GetRegistrationInfoUseCase,
    private val checkFractionAgeAndHistoryUseCase: CheckFractionAgeAndHistoryUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val saveContactUseCase: SaveContactUseCase,
    private val makeFractionContractUseCase: MakeFractionContractUseCase,
) : BaseViewModel<FractionContractState, PartialState, FractionContractEvent, FractionContractIntent>(
    initialState = FractionContractState(),
) {

    override fun handleIntent(intent: FractionContractIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(FractionContractEvent.ShowToast(e.toSingleLineMessage()))
            emit(createErrorState(e.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: FractionContractIntent): Flow<PartialState> = flow {
        when (intent) {
            FractionContractIntent.InitData -> loadInitialData()
            FractionContractIntent.OnBackClicked -> handleBack()
            FractionContractIntent.OnGuideClicked -> emit(PartialState.GuideDialogVisibility(true))
            FractionContractIntent.DismissGuideDialog -> emit(PartialState.GuideDialogVisibility(false))
            FractionContractIntent.DismissBlockingError -> sendEvent(FractionContractEvent.NavigateBack)
            FractionContractIntent.ConfirmContactSaved -> {
                emit(PartialState.ContactSavedDialogVisibility(false))
                emit(PartialState.StepChanged(FractionContractStep.Submit))
            }
            FractionContractIntent.OnNextStepClicked -> goNext()
            FractionContractIntent.OnPreviousStepClicked -> goPrevious()
            is FractionContractIntent.SetRulesConfirmed ->
                emit(PartialState.RulesConfirmedChanged(intent.confirmed))
            is FractionContractIntent.UpdateUserInfo ->
                emit(PartialState.UserInfoChanged(intent.userInfo))
            is FractionContractIntent.SetFinalConfirmed ->
                emit(PartialState.FinalConfirmedChanged(intent.confirmed))
            FractionContractIntent.SubmitContract -> submitContract()
        }
    }

    private suspend fun FlowCollector<PartialState>.loadInitialData() {
        emit(PartialState.Loading(true))
        val registrationInfo = getRegistrationInfoUseCase().first().toPresentation()
        val eligibility = checkFractionAgeAndHistoryUseCase().first()?.toPresentation()
        val userInfo = UserInfoFormPR.fromRegistration(registrationInfo).copy(
            cityCode = eligibility?.cityCode.orEmpty(),
            cityName = eligibility?.city.orEmpty(),
            zipCode = ValidationUtils.validatePostcode(registrationInfo.zipCode.digitsOnly()),
            phoneNumber = ValidationUtils.validateLandline(registrationInfo.phoneNumber.digitsOnly()),
        )
        val (jy, jm, jd) = PersianDateFormatter.today()
        emit(
            PartialState.DataLoaded(
                registrationInfo = registrationInfo,
                eligibility = eligibility,
                userInfo = userInfo,
                startDateLabel = PersianDateFormatter.format(jy, jm, jd),
                savedCityName = userInfo.cityName,
            ),
        )

        val gateError = resolveEligibilityGateError(eligibility)
        if (gateError != null) {
            emit(PartialState.BlockingError(gateError))
            return
        }

        // Legacy fraction city picker uses the national list (no province filter).
        loadCities(preferredCityName = eligibility?.city)
    }

    private suspend fun resolveEligibilityGateError(
        eligibility: FractionEligibilityPR?,
    ): String? = fractionEligibilityGateError(
        eligibility = eligibility,
        notPrimaryInsuredMessage = getString(Res.string.fraction_contract_error_not_primary_insured),
        under18Message = getString(Res.string.fraction_contract_error_under_18),
        activeFractionMessage = getString(Res.string.fraction_contract_error_active_fraction),
        unavailableMessage = getString(Res.string.fraction_contract_error_eligibility_unavailable),
    )

    private suspend fun FlowCollector<PartialState>.loadCities(
        preferredCityName: String?,
    ) {
        emit(PartialState.CitiesLoading(true))
        try {
            val cities = identityInfoUseCase
                .getCities()
                .first()
                .toCityPresentation()
            emit(PartialState.CitiesLoaded(cities))
            matchPreferredCity(cities, preferredCityName)?.let { matched ->
                val current = uiState.value.userInfo
                if (current.cityCode.isBlank()) {
                    emit(
                        PartialState.UserInfoChanged(
                            current.copy(
                                cityCode = matched.cityCode,
                                cityName = matched.cityName,
                            ),
                        ),
                    )
                    if (uiState.value.savedCityName.isBlank()) {
                        emit(PartialState.SavedCityNameChanged(matched.cityName))
                    }
                }
            }
        } finally {
            emit(PartialState.CitiesLoading(false))
        }
    }

    private fun matchPreferredCity(
        cities: List<CityPR>,
        preferredCityName: String?,
    ): CityPR? {
        val name = preferredCityName?.trim().orEmpty()
        if (name.isBlank()) return null
        return cities.firstOrNull { it.cityName == name }
            ?: cities.firstOrNull { it.cityName.contains(name) || name.contains(it.cityName) }
    }

    private suspend fun FlowCollector<PartialState>.handleBack() {
        val step = uiState.value.currentStep
        if (step == FractionContractStep.Eligibility) {
            sendEvent(FractionContractEvent.NavigateBack)
        } else {
            goPrevious()
        }
    }

    private suspend fun FlowCollector<PartialState>.goNext() {
        val state = uiState.value
        if (state.blockingErrorMessage != null) return
        when (state.currentStep) {
            FractionContractStep.Eligibility -> if (!state.isEligible) return
            FractionContractStep.Terms -> if (!state.isRulesConfirmed) return
            FractionContractStep.UserInfo -> {
                if (!state.isUserInfoComplete || state.isSavingContact) return
                if (hasContactChanged()) {
                    saveContactThenAdvance()
                    return
                }
            }
            FractionContractStep.Submit -> return
        }
        val next = when (state.currentStep) {
            FractionContractStep.Eligibility -> FractionContractStep.Terms
            FractionContractStep.Terms -> FractionContractStep.UserInfo
            FractionContractStep.UserInfo -> FractionContractStep.Submit
            FractionContractStep.Submit -> return
        }
        emit(PartialState.StepChanged(next))
    }

    private fun hasContactChanged(): Boolean {
        val userInfo = uiState.value.userInfo
        val info = uiState.value.registrationInfo ?: return true
        // Legacy changedAddressInfo also treats city rename as a change.
        return userInfo.address != info.address ||
            userInfo.zipCode.digitsOnly() != info.zipCode.digitsOnly() ||
            userInfo.phoneNumber.digitsOnly() != info.phoneNumber.digitsOnly() ||
            userInfo.cityName != uiState.value.savedCityName
    }

    private suspend fun FlowCollector<PartialState>.saveContactThenAdvance() {
        emit(PartialState.SavingContact(true))
        try {
            saveContactUseCase(buildSaveContactParams()).first()
            val userInfo = uiState.value.userInfo
            val currentInfo = uiState.value.registrationInfo ?: return
            emit(
                PartialState.ContactSaved(
                    registrationInfo = currentInfo.copy(
                        address = userInfo.address,
                        zipCode = userInfo.zipCode.digitsOnly(),
                        phoneNumber = userInfo.phoneNumber.digitsOnly(),
                        mobileNumber = userInfo.mobileNumber.digitsOnly(),
                        hasMobile = userInfo.showMobile,
                    ),
                    savedCityName = userInfo.cityName,
                ),
            )
            emit(PartialState.ContactSavedDialogVisibility(true))
        } finally {
            emit(PartialState.SavingContact(false))
        }
    }

    private fun buildSaveContactParams(): SaveContactRequestDN {
        val userInfo = uiState.value.userInfo
        return SaveContactRequestDN(
            address = userInfo.address,
            mobile = userInfo.mobileNumber.digitsOnly(),
            ssn = uiState.value.registrationInfo?.nationalId.orEmpty(),
            phoneNumber = userInfo.phoneNumber.digitsOnly(),
            zipCode = userInfo.zipCode.digitsOnly(),
        )
    }

    private suspend fun FlowCollector<PartialState>.submitContract() {
        val state = uiState.value
        if (state.currentStep != FractionContractStep.Submit) return
        if (!state.isFinalConfirmed || state.isSubmitting || state.submittedContract != null) return

        emit(PartialState.Submitting(true))
        try {
            val result = makeFractionContractUseCase(
                FractionContractState.MAKE_CONTRACT_PREMIUM_BODY,
            ).first().toPresentation()
            emit(PartialState.ContractSubmitted(result))
            sendEvent(
                FractionContractEvent.ShowSubmitSuccess(
                    contractNumber = result.contractNumber,
                    contractDate = PersianDateFormatter.formatTimestamp(result.contractDate),
                ),
            )
        } finally {
            emit(PartialState.Submitting(false))
        }
    }

    private suspend fun FlowCollector<PartialState>.goPrevious() {
        if (uiState.value.submittedContract != null) return
        val previous = when (uiState.value.currentStep) {
            FractionContractStep.Eligibility -> return
            FractionContractStep.Terms -> FractionContractStep.Eligibility
            FractionContractStep.UserInfo -> FractionContractStep.Terms
            FractionContractStep.Submit -> FractionContractStep.UserInfo
        }
        emit(PartialState.StepChanged(previous))
    }

    override fun reduceState(
        currentState: FractionContractState,
        partialState: PartialState,
    ): FractionContractState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.DataLoaded -> currentState.copy(
            isLoading = false,
            registrationInfo = partialState.registrationInfo,
            eligibility = partialState.eligibility,
            userInfo = partialState.userInfo,
            startDateLabel = partialState.startDateLabel,
            savedCityName = partialState.savedCityName,
            error = null,
        )
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.RulesConfirmedChanged -> currentState.copy(isRulesConfirmed = partialState.confirmed)
        is PartialState.UserInfoChanged -> currentState.copy(userInfo = partialState.userInfo)
        is PartialState.CitiesLoading -> currentState.copy(isCitiesLoading = partialState.isLoading)
        is PartialState.CitiesLoaded -> currentState.copy(cities = partialState.cities)
        is PartialState.SavingContact -> currentState.copy(isSavingContact = partialState.isSaving)
        is PartialState.ContactSaved -> currentState.copy(
            registrationInfo = partialState.registrationInfo,
            savedCityName = partialState.savedCityName,
        )
        is PartialState.SavedCityNameChanged -> currentState.copy(savedCityName = partialState.savedCityName)
        is PartialState.ContactSavedDialogVisibility ->
            currentState.copy(showContactSavedDialog = partialState.visible)
        is PartialState.FinalConfirmedChanged -> currentState.copy(isFinalConfirmed = partialState.confirmed)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.ContractSubmitted -> currentState.copy(submittedContract = partialState.result)
        is PartialState.GuideDialogVisibility -> currentState.copy(showGuideDialog = partialState.visible)
        is PartialState.BlockingError -> currentState.copy(
            isLoading = false,
            blockingErrorMessage = partialState.message,
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isSavingContact = false,
            isSubmitting = false,
            error = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
