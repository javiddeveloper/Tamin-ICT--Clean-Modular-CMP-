package com.tamin.taminhamrah.feature.fractionContract.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractEvent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractIntent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState.PartialState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractStep
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.mapper.fractionContract.toPresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.contracts.SaveContactUseCase
import com.tamin.taminhamrah.useCases.fractionContract.CheckFractionAgeAndHistoryUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.util.ValidationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FractionContractViewModel(
    private val getRegistrationInfoUseCase: GetRegistrationInfoUseCase,
    private val checkFractionAgeAndHistoryUseCase: CheckFractionAgeAndHistoryUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val saveContactUseCase: SaveContactUseCase,
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
            FractionContractIntent.OnNextStepClicked -> goNext()
            FractionContractIntent.OnPreviousStepClicked -> goPrevious()
            is FractionContractIntent.SetRulesConfirmed ->
                emit(PartialState.RulesConfirmedChanged(intent.confirmed))
            is FractionContractIntent.UpdateUserInfo ->
                emit(PartialState.UserInfoChanged(intent.userInfo))
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
        emit(
            PartialState.DataLoaded(
                registrationInfo = registrationInfo,
                eligibility = eligibility,
                userInfo = userInfo,
            ),
        )
        loadCities(
            preferredCityName = eligibility?.city,
            provinceCode = eligibility?.provinceCode,
        )
    }

    private suspend fun FlowCollector<PartialState>.loadCities(
        preferredCityName: String?,
        provinceCode: String?,
    ) {
        emit(PartialState.CitiesLoading(true))
        try {
            val cities = identityInfoUseCase
                .getCities(provinceCode = provinceCode?.takeIf { it.isNotBlank() })
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
        return userInfo.address != info.address ||
            userInfo.zipCode.digitsOnly() != info.zipCode.digitsOnly() ||
            userInfo.phoneNumber.digitsOnly() != info.phoneNumber.digitsOnly()
    }

    private suspend fun FlowCollector<PartialState>.saveContactThenAdvance() {
        emit(PartialState.SavingContact(true))
        try {
            saveContactUseCase(buildSaveContactParams()).first()
            emit(PartialState.StepChanged(FractionContractStep.Submit))
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

    private suspend fun FlowCollector<PartialState>.goPrevious() {
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
            error = null,
        )
        is PartialState.StepChanged -> currentState.copy(currentStep = partialState.step)
        is PartialState.RulesConfirmedChanged -> currentState.copy(isRulesConfirmed = partialState.confirmed)
        is PartialState.UserInfoChanged -> currentState.copy(userInfo = partialState.userInfo)
        is PartialState.CitiesLoading -> currentState.copy(isCitiesLoading = partialState.isLoading)
        is PartialState.CitiesLoaded -> currentState.copy(cities = partialState.cities)
        is PartialState.SavingContact -> currentState.copy(isSavingContact = partialState.isSaving)
        is PartialState.GuideDialogVisibility -> currentState.copy(showGuideDialog = partialState.visible)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
