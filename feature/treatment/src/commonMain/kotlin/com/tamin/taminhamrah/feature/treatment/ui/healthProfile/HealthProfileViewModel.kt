package com.tamin.taminhamrah.feature.treatment.ui.healthProfile

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.HealthProfileEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.HealthProfileUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.HealthProfileUiState.PartialState
import com.tamin.taminhamrah.mapper.health.toPresentation
import com.tamin.taminhamrah.useCases.health.GetPatientDrugAllergiesUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientGeneralUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientSelfDeclarativeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HealthProfileViewModel(
    private val getPatientGeneralUseCase: GetPatientGeneralUseCase,
    private val getPatientSelfDeclarativeUseCase: GetPatientSelfDeclarativeUseCase,
    private val getPatientDrugAllergiesUseCase: GetPatientDrugAllergiesUseCase
) : BaseViewModel<HealthProfileUiState, PartialState, HealthProfileEvent, HealthProfileIntent>(
    initialState = HealthProfileUiState()
) {

    override fun handleIntent(intent: HealthProfileIntent): Flow<PartialState> {
        return when (intent) {
            is HealthProfileIntent.LoadProfile -> loadProfile(intent.nationalCode)
        }
    }

    private fun loadProfile(nationalCode: String): Flow<PartialState> = flow {
        emit(PartialState.Reset)
        emit(PartialState.Loading(true))
        try {
            getPatientGeneralUseCase(nationalCode).collect { patient ->
                emit(PartialState.PatientGeneralLoaded(patient.toPresentation()))
                val patientID = patient.ptientID ?: 0
                if (patientID > 0) {
                    emit(PartialState.PatientSelfDeclarativeLoading)
                    try {
                        getPatientSelfDeclarativeUseCase(nationalCode, patientID).collect { declarative ->
                            emit(PartialState.PatientSelfDeclarativeLoaded(declarative.toPresentation()))
                        }
                    } catch (e: Exception) {
                        // Self-declaration is supplementary to the profile; ignore its failure.
                    }
                    emit(PartialState.PatientDrugAllergiesLoading)
                    try {
                        getPatientDrugAllergiesUseCase(nationalCode, patientID).collect { allergies ->
                            emit(PartialState.PatientDrugAllergiesLoaded(allergies.map { it.toPresentation() }))
                        }
                    } catch (e: Exception) {
                        // Allergies are supplementary to the profile; ignore their failure.
                    }
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: HealthProfileUiState,
        partialState: PartialState
    ): HealthProfileUiState = when (partialState) {
        is PartialState.Reset -> HealthProfileUiState()
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PatientGeneralLoaded -> currentState.copy(isLoading = false, patientGeneral = partialState.patient)
        is PartialState.PatientSelfDeclarativeLoaded -> currentState.copy(isSelfDeclarativeLoading = false, patientSelfDeclarative = partialState.declarative)
        is PartialState.PatientDrugAllergiesLoaded -> currentState.copy(isDrugAllergiesLoading = false, patientDrugAllergies = partialState.list)
        is PartialState.PatientSelfDeclarativeLoading -> currentState.copy(isSelfDeclarativeLoading = true)
        is PartialState.PatientDrugAllergiesLoading -> currentState.copy(isDrugAllergiesLoading = true)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
