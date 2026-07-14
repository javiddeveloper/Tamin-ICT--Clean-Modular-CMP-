package com.tamin.taminhamrah.feature.treatment.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.TreatmentUiState.PartialState
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDependantUnderEighteenUseCase
import com.tamin.taminhamrah.useCases.treatment.GetDeservedTreatmentUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TreatmentViewModel(
    private val tokenStoreManager: TokenStoreManager,
    private val getDeservedTreatmentUseCase: GetDeservedTreatmentUseCase,
    private val getDependantUnderEighteenUseCase: GetDependantUnderEighteenUseCase,
    private val identityInfoUseCase: IdentityInfoUseCase
) : BaseViewModel<TreatmentUiState, PartialState, TreatmentEvent, TreatmentIntent>(
    initialState = TreatmentUiState()
) {

    override fun handleIntent(intent: TreatmentIntent): Flow<PartialState> {
        return when (intent) {
            is TreatmentIntent.InitTreatmentFlow -> initTreatmentFlow()
            is TreatmentIntent.SelectPatient -> flow { emit(PartialState.PatientSelected(intent.nationalCode, intent.fullName)) }
        }
    }

    private fun initTreatmentFlow(): Flow<PartialState> = flow {
        emit(PartialState.Reset)
        emit(PartialState.Loading(true))
        var nationalCode = tokenStoreManager.getUserId() ?: ""
        if (nationalCode.isEmpty()) {
            try {
                val identity = identityInfoUseCase().first()
                nationalCode = identity.nationalId ?: ""
                if (nationalCode.isNotEmpty()) {
                    tokenStoreManager.saveUserId(nationalCode)
                }
            } catch (e: Exception) {
                // Ignore and proceed
            }
        }

        if (nationalCode.isEmpty()) {
            emit(PartialState.Error("اطلاعات کاربری یافت نشد."))
            return@flow
        }

        emit(PartialState.MainUserNationalCodeLoaded(nationalCode))
        emit(PartialState.PatientSelected(nationalCode, "کاربر اصلی"))

        var mainUserFullName = "کاربر اصلی"
        // Load Deserved Status
        try {
            getDeservedTreatmentUseCase(nationalCode).collect { list ->
                val presentationList = list.toPresentation()
                presentationList.firstOrNull()?.let {
                    mainUserFullName = it.fullName
                }
                emit(PartialState.DeservedLoaded(presentationList))
                emit(PartialState.PatientSelected(nationalCode, mainUserFullName))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }

        // Load Dependants under 18 (supplementary; ignore failures)
        try {
            getDependantUnderEighteenUseCase(nationalCode).collect { list ->
                emit(PartialState.DependantsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            // Ignore dependant load failures; they are non-critical.
        }
    }

    override fun reduceState(
        currentState: TreatmentUiState,
        partialState: PartialState
    ): TreatmentUiState = when (partialState) {
        is PartialState.Reset -> TreatmentUiState()
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.DeservedLoaded -> currentState.copy(isLoading = false, deservedList = partialState.list)
        is PartialState.DependantsLoaded -> currentState.copy(isLoading = false, dependantList = partialState.list)

        is PartialState.PatientSelected -> currentState.copy(
            selectedNationalCode = partialState.nationalCode,
            selectedPatientName = partialState.fullName,
            error = null
        )
        is PartialState.MainUserNationalCodeLoaded -> currentState.copy(
            mainUserNationalCode = partialState.nationalCode
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
