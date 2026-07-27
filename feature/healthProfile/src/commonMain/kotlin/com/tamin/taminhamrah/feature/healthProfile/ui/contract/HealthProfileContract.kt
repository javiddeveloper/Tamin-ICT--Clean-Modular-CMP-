package com.tamin.taminhamrah.feature.healthProfile.ui.contract

import com.tamin.taminhamrah.feature.healthProfile.ui.model.DrugAllergyItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.IllnessGroupPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientGeneralPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientSelfDeclarativePR

// ─────────────────────────────────────────────────────────────────────────────
// Wizard Step Enum
// ─────────────────────────────────────────────────────────────────────────────

enum class SelfDeclarationStep {
    GATE,
    INTRO,
    IDENTITY,   // Step 1
    PERSONAL,   // Step 2
    CONTACT,    // Step 3
    EMERGENCY,  // Step 4
    PHYSICAL,   // Step 5
    DISEASES,   // Step 6
    FAMILY,     // Step 7
    BLOOD,      // Step 8
    LIFESTYLE,  // Step 9
    ALLERGY,    // Step 10
    REVIEW,
    SUCCESS,
    COMPLETED
}

// ─────────────────────────────────────────────────────────────────────────────
// Per-Step State Data Classes
// ─────────────────────────────────────────────────────────────────────────────

// Step 1: Read-only Identity Info (populated from PatientGeneralPR)
data class IdentityStepState(
    val patientName: String = "",
    val patientFamily: String = "",
    val patientFather: String = "",
    val patientGender: String = "",
    val patientBirthDate: String = "",
    val insuranceNumber: String = "",
    val insuranceType: String = "",
    val lastVisitDate: String = ""
)

// Step 2: Personal Info
// maritalStatusId is sent to API; maritalStatusLabel is shown in UI
data class PersonalStepState(
    val maritalStatusId: Int? = null,
    val maritalStatusLabel: String = "",
    val job: String = "",
    val citizenship: String = "",
    val nationality: String = ""
)

// Step 3: Contact Info
// provinceId / cityId are sent to API; labels are shown in dropdowns
data class ContactStepState(
    val mobile: String = "",
    val email: String = "",
    val provinceId: Int? = null,
    val provinceLabel: String = "",
    val cityId: Int? = null,
    val cityLabel: String = "",
    val address: String = "",
    val postcode: String = "",
    val landline: String = ""
)

// Step 4: Emergency Contact Info
data class EmergencyStepState(
    val emergencyName: String = "",
    val emergencyFamily: String = "",
    val emergencyRelation: String = "",
    val emergencyMobile: String = ""
)

// Step 5: Physical Stats
data class PhysicalStepState(
    val height: Int = 0,
    val weight: Int = 0
)

// Step 6: Health Questions (Diseases) — illness IDs come from IllnessGroupPR
data class DiseasesStepState(
    val hasHighBloodSugar: Boolean? = null,
    val hasHighBloodPressure: Boolean? = null,
    val hasHighCholesterol: Boolean? = null,
    val hasChronicDisease: Boolean? = null,
    val chronicDiseaseIds: Set<Int> = emptySet(),
    val hasMentalIllness: Boolean? = null,
    val mentalIllnessIds: Set<Int> = emptySet(),
    val hasCancer: Boolean? = null,
    val cancerIds: Set<Int> = emptySet()
)

// Step 7: Family Health History
data class FamilyStepState(
    val familyHighCholesterol: Boolean? = null,
    val familyHighBloodPressure: Boolean? = null,
    val familyHighBloodSugar: Boolean? = null,
    val familyHasCancer: Boolean? = null,
    val familyCancerIds: Set<Int> = emptySet()
)

// Step 8: Blood Group
// selectedBloodGroupId is sent to API (from bloodGroupOptions); letter is a local display helper if needed
data class BloodGroupStepState(
    val selectedBloodGroupId: Int? = null,
    val selectedBloodGroupLetter: String? = null,

    val selectedBloodGroupRh: String? = null,
    val isBloodGroupUnknown: Boolean = false
)

// Step 9: Lifestyle
// smokingStatusId is from API lookup; patterns are still free local chips for now
data class LifestyleStepState(
    val isSmoking: Boolean? = null,
    val smokingStatusId: Int? = null,
    val smokingPattern: String? = null,

    val hasAddiction: Boolean? = null,
    val substanceStatusId: Int? = null,
    val substancePattern: String? = null,

    val isDrinking: Boolean? = null,
    val drinkingStatusId: Int? = null,
    val drinkingPattern: String? = null,

    val isExercising: Boolean? = null,
    val exerciseStatusId: Int? = null,
    val exerciseFrequency: String? = null
)

// Step 10: Drug Allergies — real PR type, no more mock
data class AllergyStepState(
    val allergies: List<DrugAllergyItemPR> = emptyList()
)

// ─────────────────────────────────────────────────────────────────────────────
// Aggregate Wizard UI State
// ─────────────────────────────────────────────────────────────────────────────

data class SelfDeclarationUiState(
    val currentStep: SelfDeclarationStep = SelfDeclarationStep.GATE,
    val isLoading: Boolean = false,
    val error: String? = null,
    val identity: IdentityStepState = IdentityStepState(),
    val personal: PersonalStepState = PersonalStepState(),
    val contact: ContactStepState = ContactStepState(),
    val emergency: EmergencyStepState = EmergencyStepState(),
    val physical: PhysicalStepState = PhysicalStepState(),
    val diseases: DiseasesStepState = DiseasesStepState(),
    val family: FamilyStepState = FamilyStepState(),
    val bloodGroup: BloodGroupStepState = BloodGroupStepState(),
    val lifestyle: LifestyleStepState = LifestyleStepState(),
    val allergy: AllergyStepState = AllergyStepState()
)

// ─────────────────────────────────────────────────────────────────────────────
// Root UI State
// Lookup lists are loaded once on init and held here so all screens can read
// them without fetching again.
// ─────────────────────────────────────────────────────────────────────────────

data class HealthProfileUiState(
    val isLoading: Boolean = false,
    val isProvincesLoading: Boolean = false,
    val isCitiesLoading: Boolean = false,
    val error: String? = null,

    // ── Patient data (loaded from API) ────────────────────────────────────────
    val generalInfo: PatientGeneralPR? = null,
    val lifestyleInfo: PatientSelfDeclarativePR? = null,
    val drugAllergies: List<DrugAllergyItemPR> = emptyList(),

    // ── Lookup / dropdown lists (loaded from API on init) ─────────────────────
    val maritalStatusOptions: List<LookupItemPR> = emptyList(),   // Step 2
    val provinceOptions: List<LookupItemPR> = emptyList(),        // Step 3
    val cityOptions: List<LookupItemPR> = emptyList(),            // Step 3 (filtered by selected province)
    val bloodGroupOptions: List<LookupItemPR> = emptyList(),      // Step 8
    val smokingStatusOptions: List<LookupItemPR> = emptyList(),   // Step 9
    val actFrequencyOptions: List<LookupItemPR> = emptyList(),    // Step 9 (Addiction, Alcohol, Exercise)
    val illnessGroups: List<IllnessGroupPR> = emptyList(),        // Steps 6 + 7
    val drugOptions: List<LookupItemPR> = emptyList(),            // Step 10 dialog picker

    // ── Wizard state ──────────────────────────────────────────────────────────
    val selfDeclaration: SelfDeclarationUiState = SelfDeclarationUiState()
) {
    sealed interface PartialState {
        // ── Loading / Error ───────────────────────────────────────────────────
        data class Loading(val isLoading: Boolean) : PartialState
        data class ProvincesLoading(val isLoading: Boolean) : PartialState
        data class CitiesLoading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState

        // ── Remote data loaded ────────────────────────────────────────────────
        data class GeneralLoaded(val info: PatientGeneralPR) : PartialState
        data class LifestyleLoaded(val info: PatientSelfDeclarativePR) : PartialState
        data class AllergiesLoaded(val list: List<DrugAllergyItemPR>) : PartialState

        // ── Lookup lists loaded ───────────────────────────────────────────────
        data class MaritalStatusLoaded(val options: List<LookupItemPR>) : PartialState
        data class ProvincesLoaded(val options: List<LookupItemPR>) : PartialState
        data class CitiesLoaded(val options: List<LookupItemPR>) : PartialState
        data class BloodGroupsLoaded(val options: List<LookupItemPR>) : PartialState
        data class SmokingStatusLoaded(val options: List<LookupItemPR>) : PartialState
        data class ActFrequenciesLoaded(val options: List<LookupItemPR>) : PartialState
        data class IllnessGroupsLoaded(val groups: List<IllnessGroupPR>) : PartialState
        data class DrugsLoaded(val options: List<LookupItemPR>) : PartialState

        // ── Step navigation ───────────────────────────────────────────────────
        data class StepChanged(val step: SelfDeclarationStep) : PartialState

        // ── Per-step field updates ────────────────────────────────────────────
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

// ─────────────────────────────────────────────────────────────────────────────
// Intents
// ─────────────────────────────────────────────────────────────────────────────

sealed interface HealthProfileIntent {
    // Top-level
    data class LoadHealthProfile(val nationalCode: String? = null) : HealthProfileIntent

    // Step 3: triggered when province changes to reload cities
    data class LoadCitiesForProvince(val provinceId: Int) : HealthProfileIntent

    // Self-declaration wizard
    data class ChangeStep(val step: SelfDeclarationStep) : HealthProfileIntent
    data class UpdateIdentity(val identity: IdentityStepState) : HealthProfileIntent
    data class UpdatePersonal(val personal: PersonalStepState) : HealthProfileIntent
    data class UpdateContact(val contact: ContactStepState) : HealthProfileIntent
    data class UpdateEmergency(val emergency: EmergencyStepState) : HealthProfileIntent
    data class UpdatePhysical(val physical: PhysicalStepState) : HealthProfileIntent
    data class UpdateDiseases(val diseases: DiseasesStepState) : HealthProfileIntent
    data class UpdateFamily(val family: FamilyStepState) : HealthProfileIntent
    data class UpdateBloodGroup(val bloodGroup: BloodGroupStepState) : HealthProfileIntent
    data class UpdateLifestyle(val lifestyle: LifestyleStepState) : HealthProfileIntent
    data class UpdateAllergy(val allergy: AllergyStepState) : HealthProfileIntent
    data object SubmitDeclaration : HealthProfileIntent
}

// ─────────────────────────────────────────────────────────────────────────────
// Events
// ─────────────────────────────────────────────────────────────────────────────

sealed interface HealthProfileEvent {
    data object NavigateBack : HealthProfileEvent
    data class ShowToast(val message: String) : HealthProfileEvent
}
