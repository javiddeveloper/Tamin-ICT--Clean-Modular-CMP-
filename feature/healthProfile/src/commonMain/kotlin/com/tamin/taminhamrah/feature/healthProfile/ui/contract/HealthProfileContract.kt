package com.tamin.taminhamrah.feature.healthProfile.ui.contract

import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientDrugAllergyMock
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientGeneralMock
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientSelfDeclarativeMock

data class HealthProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val generalInfo: PatientGeneralMock? = null,
    val lifestyleInfo: PatientSelfDeclarativeMock? = null,
    val drugAllergies: List<PatientDrugAllergyMock> = emptyList(),
    val selfDeclaration: SelfDeclarationUiState = SelfDeclarationUiState()
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class GeneralLoaded(val info: PatientGeneralMock) : PartialState
        data class LifestyleLoaded(val info: PatientSelfDeclarativeMock) : PartialState
        data class AllergiesLoaded(val list: List<PatientDrugAllergyMock>) : PartialState

        data class StepChanged(val step: SelfDeclarationStep) : PartialState
        data class IdentityUpdated(val identity: IdentityStepState) : PartialState
        data class PersonalUpdated(val personal: PersonalStepState) : PartialState
        data class ContactUpdated(val contact: ContactStepState) : PartialState
        data class EmergencyUpdated(val emergency: EmergencyStepState) : PartialState
        data class PhysicalUpdated(val physical: PhysicalStepState) : PartialState
        data class DiseasesUpdated(val diseases: DiseasesStepState) : PartialState
        data class FamilyUpdated(val family: FamilyStepState) : PartialState
        data class BloodGroupUpdated(val bloodGroup: BloodGroupStepState) : PartialState
        data class LifestyleUpdated(val lifestyle: LifestyleStepState) : PartialState
        data class AllergyUpdated(val allergy: AllergyStepState) : PartialState
    }
}

sealed interface HealthProfileIntent {
    data class LoadHealthProfile(val nationalCode: String? = null) : HealthProfileIntent
}

sealed interface HealthProfileEvent {
    data object NavigateBack : HealthProfileEvent
    data class ShowToast(val message: String) : HealthProfileEvent
}
