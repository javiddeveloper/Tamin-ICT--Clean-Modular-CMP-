package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryStep
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsEvent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsIntent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsUiState
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsUiState.PartialState
import com.tamin.taminhamrah.mapper.history.toPresentation
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.history.SendToInstitutionUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class SendHistoryToInstitutionsViewModel(
    private val getUserInfosUseCase: GetUserInfosUseCase,
    private val sendToInstitutionUseCase: SendToInstitutionUseCase,
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
) : BaseViewModel<SendHistoryToInstitutionsUiState, PartialState, SendHistoryToInstitutionsEvent, SendHistoryToInstitutionsIntent>(
    initialState = SendHistoryToInstitutionsUiState()
) {

    override fun handleIntent(intent: SendHistoryToInstitutionsIntent): Flow<PartialState> {
        return when (intent) {
            is SendHistoryToInstitutionsIntent.LoadUserInfo -> handleLoadUserInfo()
            is SendHistoryToInstitutionsIntent.ConfirmTypeSelection -> handleConfirmTypeSelection(intent.selectedTypes)
            is SendHistoryToInstitutionsIntent.GoToNextStep -> handleGoToNextStep()
            is SendHistoryToInstitutionsIntent.GoToPreviousStep -> handleGoToPreviousStep()
            is SendHistoryToInstitutionsIntent.SendToInstitution -> handleSendToInstitution()
        }
    }

    private fun handleLoadUserInfo(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val pensionerIds = getPensionerIdUseCase().first()
            if (pensionerIds.isNotEmpty()) {
                val msg = "کاربر گرامی؛ شما بازنشسته یا مستمری‌بگیر هستید؛ به همین دلیل مجوز دسترسی به این سرویس را ندارید."
                emit(PartialState.Loading(false))
                sendEvent(SendHistoryToInstitutionsEvent.DisplayAccessDeniedModal(msg))
                return@flow
            }
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            sendEvent(SendHistoryToInstitutionsEvent.ShowToast(e.toSingleLineMessage()))
            sendEvent(SendHistoryToInstitutionsEvent.NavigateBack)
            return@flow
        }
        val userInfo = getUserInfosUseCase()
        if (userInfo.insuranceNumber.isNullOrEmpty()) {
            val msg = "اطلاعات بیمه‌ای برای این حساب یافت نشد. لطفاً با شعبه تأمین اجتماعی خود تماس بگیرید."
            emit(PartialState.Loading(false))
            sendEvent(SendHistoryToInstitutionsEvent.DisplayAccessDeniedModal(msg))
            return@flow
        }
        emit(PartialState.UserInfoLoaded(userInfo.toPresentation()))
    }

    private fun handleConfirmTypeSelection(selectedTypes: Set<HistoryCertificateType>): Flow<PartialState> = flow {
        emit(PartialState.SetTypes(selectedTypes))
    }

    private fun handleGoToNextStep(): Flow<PartialState> = flow {
        emit(PartialState.GoToNextStep)
    }

    private fun handleGoToPreviousStep(): Flow<PartialState> = flow {
        emit(PartialState.GoToPreviousStep)
    }

    private fun handleSendToInstitution(): Flow<PartialState> = flow {
        val state = uiState.value
        emit(PartialState.Loading(true))
        sendToInstitutionUseCase(state.selectedTypes)
        emit(PartialState.Loading(false))
        sendEvent(SendHistoryToInstitutionsEvent.DisplaySuccessModal)
    }

    override fun reduceState(
        currentState: SendHistoryToInstitutionsUiState,
        partialState: PartialState
    ): SendHistoryToInstitutionsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.UserInfoLoaded -> currentState.copy(isLoading = false, userInfo = partialState.userInfo)
        is PartialState.SetTypes -> currentState.copy(selectedTypes = partialState.selectedTypes)
        is PartialState.GoToNextStep -> currentState.copy(currentStep = SendHistoryStep.Review)
        is PartialState.GoToPreviousStep -> currentState.copy(currentStep = SendHistoryStep.SelectType)
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(SendHistoryToInstitutionsEvent.ShowToast(message))
        return PartialState.Loading(false)
    }
}
