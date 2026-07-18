package com.tamin.taminhamrah.feature.healthProfile.ui.contract

import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientGeneralMock
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientSelfDeclarativeMock
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientDrugAllergyMock

data class HealthProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val generalInfo: PatientGeneralMock? = null,
    val lifestyleInfo: PatientSelfDeclarativeMock? = null,
    val drugAllergies: List<PatientDrugAllergyMock> = emptyList()
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class GeneralLoaded(val info: PatientGeneralMock) : PartialState
        data class LifestyleLoaded(val info: PatientSelfDeclarativeMock) : PartialState
        data class AllergiesLoaded(val list: List<PatientDrugAllergyMock>) : PartialState
    }
}

sealed interface HealthProfileIntent {
    data object LoadHealthProfile : HealthProfileIntent
}

sealed interface HealthProfileEvent {
    data object NavigateBack : HealthProfileEvent
}
