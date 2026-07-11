package com.tamin.taminhamrah.feature.treatment.ui.contract

import com.tamin.taminhamrah.model.health.DrugItemAllergiesPR
import com.tamin.taminhamrah.model.health.PatientGeneralPR
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativePR
import com.tamin.taminhamrah.model.health.PatientHospitalizationsPR
import com.tamin.taminhamrah.model.health.PatientVisitPR
import com.tamin.taminhamrah.model.health.PatientLabPR
import com.tamin.taminhamrah.model.health.PatientImagingPR

data class HealthProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val patientGeneral: PatientGeneralPR? = null,
    val patientSelfDeclarative: PatientSelfDeclarativePR? = null,
    val patientDrugAllergies: List<DrugItemAllergiesPR> = emptyList(),
    val patientHospitalizations: List<PatientHospitalizationsPR> = emptyList(),
    val patientVisits: List<PatientVisitPR> = emptyList(),
    val patientLabs: List<PatientLabPR> = emptyList(),
    val patientImaging: List<PatientImagingPR> = emptyList(),
    val isSelfDeclarativeLoading: Boolean = false,
    val isDrugAllergiesLoading: Boolean = false
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()
        data class PatientGeneralLoaded(val patient: PatientGeneralPR) : PartialState()
        data class PatientSelfDeclarativeLoaded(val declarative: PatientSelfDeclarativePR) : PartialState()
        data class PatientDrugAllergiesLoaded(val list: List<DrugItemAllergiesPR>) : PartialState()
        data class PatientHospitalizationsLoaded(val list: List<PatientHospitalizationsPR>) : PartialState()
        data class PatientVisitsLoaded(val list: List<PatientVisitPR>) : PartialState()
        data class PatientLabsLoaded(val list: List<PatientLabPR>) : PartialState()
        data class PatientImagingLoaded(val list: List<PatientImagingPR>) : PartialState()
        data object PatientSelfDeclarativeLoading : PartialState()
        data object PatientDrugAllergiesLoading : PartialState()
    }
}

sealed class HealthProfileIntent {
    data class LoadProfile(val nationalCode: String) : HealthProfileIntent()
}

sealed class HealthProfileEvent {
    data class ShowToast(val message: String) : HealthProfileEvent()
}
