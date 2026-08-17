package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryStep
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsEvent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsIntent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsUiState
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsUiState.PartialState
import com.tamin.taminhamrah.mapper.history.toPresentation
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.history.SendToInstitutionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SendHistoryToInstitutionsViewModel(
    private val getUserInfosUseCase: GetUserInfosUseCase,
    private val sendToInstitutionUseCase: SendToInstitutionUseCase
) : BaseViewModel<SendHistoryToInstitutionsUiState, PartialState, SendHistoryToInstitutionsEvent, SendHistoryToInstitutionsIntent>(
    initialState = SendHistoryToInstitutionsUiState()
) {

    override fun handleIntent(intent: SendHistoryToInstitutionsIntent): Flow<PartialState> {
        return when (intent) {
            is SendHistoryToInstitutionsIntent.LoadUserInfo -> handleLoadUserInfo()
            is SendHistoryToInstitutionsIntent.ConfirmTypeSelection -> handleConfirmTypeSelection(intent.type1, intent.type2, intent.type3)
            is SendHistoryToInstitutionsIntent.GoToNextStep -> handleGoToNextStep()
            is SendHistoryToInstitutionsIntent.GoToPreviousStep -> handleGoToPreviousStep()
            is SendHistoryToInstitutionsIntent.SendToInstitution -> handleSendToInstitution()
        }
    }

    private fun handleLoadUserInfo(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        val userInfo = getUserInfosUseCase()
        emit(PartialState.UserInfoLoaded(userInfo.toPresentation()))
    }

    private fun handleConfirmTypeSelection(type1: Boolean, type2: Boolean, type3: Boolean): Flow<PartialState> = flow {
        emit(PartialState.SetTypes(type1, type2, type3))
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
        sendToInstitutionUseCase(state.isType1Selected, state.isType2Selected, state.isType3Selected)
        emit(PartialState.Loading(false))
        sendEvent(SendHistoryToInstitutionsEvent.DisplaySuccessModal)
    }

    override fun reduceState(
        currentState: SendHistoryToInstitutionsUiState,
        partialState: PartialState
    ): SendHistoryToInstitutionsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.UserInfoLoaded -> currentState.copy(isLoading = false, userInfo = partialState.userInfo)
        is PartialState.SetTypes -> currentState.copy(
            isType1Selected = partialState.type1,
            isType2Selected = partialState.type2,
            isType3Selected = partialState.type3
        )
        is PartialState.GoToNextStep -> currentState.copy(currentStep = SendHistoryStep.Review)
        is PartialState.GoToPreviousStep -> currentState.copy(currentStep = SendHistoryStep.SelectType)
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(SendHistoryToInstitutionsEvent.ShowToast(message))
        return PartialState.Loading(false)
    }
}
