package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.util.isValidIranianNationalCode
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeRequestDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetLegalRepresentativeWorkshopContractsUseCase
import com.tamin.taminhamrah.useCases.workshops.RequestLegalRepresentativeTicketUseCase
import com.tamin.taminhamrah.useCases.workshops.SubmitLegalRepresentativeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class AddLegalRepresentativeViewModel(
    private val requestLegalRepresentativeTicketUseCase: RequestLegalRepresentativeTicketUseCase,
    private val submitLegalRepresentativeUseCase: SubmitLegalRepresentativeUseCase,
    private val getLegalRepresentativeWorkshopContractsUseCase: GetLegalRepresentativeWorkshopContractsUseCase,
) : BaseViewModel<
    AddLegalRepresentativeUiState,
    AddLegalRepresentativeUiState.PartialState,
    AddLegalRepresentativeEvent,
    AddLegalRepresentativeIntent
    >(initialState = AddLegalRepresentativeUiState()) {

    override fun handleIntent(
        intent: AddLegalRepresentativeIntent
    ): Flow<AddLegalRepresentativeUiState.PartialState> = when (intent) {
        is AddLegalRepresentativeIntent.Init -> merge(
            flow {
                emit(
                    AddLegalRepresentativeUiState.PartialState.Init(
                        workshopId = intent.workshopId,
                        branchCode = intent.branchCode,
                        isEditMode = intent.isEditMode,
                        nationalCode = intent.nationalCode,
                        hasElectronicNotification = intent.hasElectronicNotification,
                        hasInternetList = intent.hasInternetList,
                        hasInsuredRegistration = intent.hasInsuredRegistration,
                        isSpecialWorkshop = intent.special,
                    )
                )
            },
            if (intent.special) loadContracts(intent.workshopId, intent.branchCode) else flow { }
        )

        is AddLegalRepresentativeIntent.NationalCodeChanged -> flow {
            val digitsOnly = intent.value.filter { it.isDigit() }.take(10)
            val error = if (digitsOnly.length == 10 && !isValidIranianNationalCode(digitsOnly)) {
                NATIONAL_CODE_ERROR
            } else {
                null
            }
            emit(AddLegalRepresentativeUiState.PartialState.NationalCodeChanged(digitsOnly, error))
        }

        is AddLegalRepresentativeIntent.ElectronicNotificationChanged -> flow {
            emit(AddLegalRepresentativeUiState.PartialState.ElectronicNotificationChanged(intent.value))
        }

        is AddLegalRepresentativeIntent.InternetListChanged -> flow {
            emit(AddLegalRepresentativeUiState.PartialState.InternetListChanged(intent.value))
        }

        is AddLegalRepresentativeIntent.InsuredRegistrationChanged -> flow {
            emit(AddLegalRepresentativeUiState.PartialState.InsuredRegistrationChanged(intent.value))
        }

        is AddLegalRepresentativeIntent.RequestTicket -> flow {
            val state = uiState.value
            if (state.isRequestingTicket) return@flow
            if (!state.canRequestTicket) {
                emit(AddLegalRepresentativeUiState.PartialState.RequestTicketValidationFailed(NATIONAL_CODE_REQUIRED_ERROR))
                return@flow
            }
            emit(AddLegalRepresentativeUiState.PartialState.RequestingTicket)
            try {
                requestLegalRepresentativeTicketUseCase(state.nationalCode)
                emit(AddLegalRepresentativeUiState.PartialState.TicketRequested)
            } catch (e: Exception) {
                emit(AddLegalRepresentativeUiState.PartialState.RequestTicketFailed(e.toSingleLineMessage()))
            }
        }

        is AddLegalRepresentativeIntent.OtpChanged -> flow {
            emit(AddLegalRepresentativeUiState.PartialState.OtpChanged(intent.value))
        }

        is AddLegalRepresentativeIntent.OpenContractPicker -> flow {
            emit(AddLegalRepresentativeUiState.PartialState.ContractPickerOpened)
        }

        is AddLegalRepresentativeIntent.DismissContractPicker -> flow {
            emit(AddLegalRepresentativeUiState.PartialState.ContractPickerDismissed)
        }

        is AddLegalRepresentativeIntent.ToggleContractRow -> flow {
            val current = uiState.value.selectedContractRows
            val updated = if (current.contains(intent.contractRow)) {
                current - intent.contractRow
            } else {
                current + intent.contractRow
            }
            emit(AddLegalRepresentativeUiState.PartialState.ContractRowToggled(updated))
        }

        is AddLegalRepresentativeIntent.Submit -> flow {
            val state = uiState.value
            if (state.isSubmitting || !state.canSubmit) return@flow
            emit(AddLegalRepresentativeUiState.PartialState.Submitting)
            try {
                submitLegalRepresentativeUseCase(
                    ticket = state.otpCode,
                    request = LegalRepresentativeRequestDN(
                        nationalCode = state.nationalCode,
                        workshopId = state.workshopId,
                        branchCode = state.branchCode,
                        special = state.isSpecialWorkshop,
                        hasElectronicNotification = state.hasElectronicNotification,
                        hasInternetList = state.hasInternetList,
                        hasInsuredRegistration = state.hasInsuredRegistration,
                        contractRows = state.selectedContractRows,
                    )
                )
                emit(AddLegalRepresentativeUiState.PartialState.Submitted)
            } catch (e: Exception) {
                emit(AddLegalRepresentativeUiState.PartialState.SubmitFailed(e.toSingleLineMessage()))
            }
        }
    }

    private fun loadContracts(
        workshopId: String,
        branchCode: String
    ): Flow<AddLegalRepresentativeUiState.PartialState> = flow {
        emit(AddLegalRepresentativeUiState.PartialState.LoadingContracts)
        try {
            val result = getLegalRepresentativeWorkshopContractsUseCase(workshopId, branchCode).first()
            emit(
                AddLegalRepresentativeUiState.PartialState.ContractsLoaded(
                    result?.list?.map { it.toPresentation() } ?: emptyList()
                )
            )
        } catch (e: Exception) {
            emit(AddLegalRepresentativeUiState.PartialState.ContractsLoadFailed(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: AddLegalRepresentativeUiState,
        partialState: AddLegalRepresentativeUiState.PartialState
    ): AddLegalRepresentativeUiState = when (partialState) {
        is AddLegalRepresentativeUiState.PartialState.Init -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
            isEditMode = partialState.isEditMode,
            nationalCode = partialState.nationalCode,
            hasElectronicNotification = partialState.hasElectronicNotification,
            hasInternetList = partialState.hasInternetList,
            hasInsuredRegistration = partialState.hasInsuredRegistration,
            isSpecialWorkshop = partialState.isSpecialWorkshop,
        )

        is AddLegalRepresentativeUiState.PartialState.NationalCodeChanged -> currentState.copy(
            nationalCode = partialState.value,
            nationalCodeError = partialState.error,
        )

        is AddLegalRepresentativeUiState.PartialState.ElectronicNotificationChanged ->
            currentState.copy(hasElectronicNotification = partialState.value)

        is AddLegalRepresentativeUiState.PartialState.InternetListChanged ->
            currentState.copy(hasInternetList = partialState.value)

        is AddLegalRepresentativeUiState.PartialState.InsuredRegistrationChanged ->
            currentState.copy(hasInsuredRegistration = partialState.value)

        is AddLegalRepresentativeUiState.PartialState.RequestingTicket ->
            currentState.copy(isRequestingTicket = true, error = null)

        is AddLegalRepresentativeUiState.PartialState.TicketRequested ->
            currentState.copy(isRequestingTicket = false, isTicketRequested = true)

        is AddLegalRepresentativeUiState.PartialState.RequestTicketFailed ->
            currentState.copy(isRequestingTicket = false, error = partialState.message)

        is AddLegalRepresentativeUiState.PartialState.RequestTicketValidationFailed ->
            currentState.copy(nationalCodeError = partialState.message)

        is AddLegalRepresentativeUiState.PartialState.OtpChanged ->
            currentState.copy(otpCode = partialState.value, otpError = null)

        is AddLegalRepresentativeUiState.PartialState.LoadingContracts ->
            currentState.copy(isLoadingContracts = true)

        is AddLegalRepresentativeUiState.PartialState.ContractsLoaded ->
            currentState.copy(isLoadingContracts = false, availableContracts = partialState.contracts)

        is AddLegalRepresentativeUiState.PartialState.ContractsLoadFailed ->
            currentState.copy(isLoadingContracts = false, error = partialState.message)

        is AddLegalRepresentativeUiState.PartialState.ContractPickerOpened ->
            currentState.copy(isContractPickerOpen = true)

        is AddLegalRepresentativeUiState.PartialState.ContractPickerDismissed ->
            currentState.copy(isContractPickerOpen = false)

        is AddLegalRepresentativeUiState.PartialState.ContractRowToggled ->
            currentState.copy(selectedContractRows = partialState.selectedContractRows)

        is AddLegalRepresentativeUiState.PartialState.Submitting ->
            currentState.copy(isSubmitting = true, error = null)

        is AddLegalRepresentativeUiState.PartialState.Submitted ->
            currentState.copy(isSubmitting = false, isSuccess = true)

        is AddLegalRepresentativeUiState.PartialState.SubmitFailed ->
            currentState.copy(isSubmitting = false, error = partialState.message)
    }

    override fun createErrorState(message: String): AddLegalRepresentativeUiState.PartialState =
        AddLegalRepresentativeUiState.PartialState.SubmitFailed(message)

    private companion object {
        const val NATIONAL_CODE_ERROR = "کد ملی معتبر نیست."
        const val NATIONAL_CODE_REQUIRED_ERROR = "ابتدا کد ملی ۱۰ رقمی و معتبر نماینده را وارد کنید."
    }
}
