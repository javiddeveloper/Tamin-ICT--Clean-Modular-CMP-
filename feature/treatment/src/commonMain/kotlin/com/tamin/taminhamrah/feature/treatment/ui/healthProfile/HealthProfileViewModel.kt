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
import com.tamin.taminhamrah.useCases.health.GetPatientHospitalizationsUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientVisitsUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientLabsUseCase
import com.tamin.taminhamrah.useCases.health.GetPatientImagingUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class HealthProfileViewModel(
    private val getPatientGeneralUseCase: GetPatientGeneralUseCase,
    private val getPatientSelfDeclarativeUseCase: GetPatientSelfDeclarativeUseCase,
    private val getPatientDrugAllergiesUseCase: GetPatientDrugAllergiesUseCase,
    private val getPatientHospitalizationsUseCase: GetPatientHospitalizationsUseCase,
    private val getPatientVisitsUseCase: GetPatientVisitsUseCase,
    private val getPatientLabsUseCase: GetPatientLabsUseCase,
    private val getPatientImagingUseCase: GetPatientImagingUseCase
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
                    emitAll(loadSupplementary(nationalCode, patientID))
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    /**
     * Loads all supplementary health-record lists for a patient. Each is optional: a failure
     * on one record type is swallowed so it never blocks the rest of the profile.
     */
    private fun loadSupplementary(nationalCode: String, patientID: Int): Flow<PartialState> = flow {
        emit(PartialState.PatientSelfDeclarativeLoading)
        supplementary { getPatientSelfDeclarativeUseCase(nationalCode, patientID).collect { emit(PartialState.PatientSelfDeclarativeLoaded(it.toPresentation())) } }
        emit(PartialState.PatientDrugAllergiesLoading)
        supplementary { getPatientDrugAllergiesUseCase(nationalCode, patientID).collect { list -> emit(PartialState.PatientDrugAllergiesLoaded(list.map { it.toPresentation() })) } }
        supplementary { getPatientHospitalizationsUseCase(nationalCode, patientID).collect { list -> emit(PartialState.PatientHospitalizationsLoaded(list.map { it.toPresentation() })) } }
        supplementary { getPatientVisitsUseCase(nationalCode, patientID).collect { list -> emit(PartialState.PatientVisitsLoaded(list.map { it.toPresentation() })) } }
        supplementary { getPatientLabsUseCase(nationalCode, patientID).collect { list -> emit(PartialState.PatientLabsLoaded(list.map { it.toPresentation() })) } }
        supplementary { getPatientImagingUseCase(nationalCode, patientID).collect { list -> emit(PartialState.PatientImagingLoaded(list.map { it.toPresentation() })) } }
    }

    private inline fun supplementary(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            // Supplementary health record; a failure here must not block the rest of the profile.
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
        is PartialState.PatientHospitalizationsLoaded -> currentState.copy(patientHospitalizations = partialState.list)
        is PartialState.PatientVisitsLoaded -> currentState.copy(patientVisits = partialState.list)
        is PartialState.PatientLabsLoaded -> currentState.copy(patientLabs = partialState.list)
        is PartialState.PatientImagingLoaded -> currentState.copy(patientImaging = partialState.list)
        is PartialState.PatientSelfDeclarativeLoading -> currentState.copy(isSelfDeclarativeLoading = true)
        is PartialState.PatientDrugAllergiesLoading -> currentState.copy(isDrugAllergiesLoading = true)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
