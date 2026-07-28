package com.tamin.taminhamrah.feature.healthProfile.ui.contract

import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientDrugAllergyMock

enum class SelfDeclarationStep {
    GATE,
    INTRO,
    IDENTITY,      // Step 1
    PERSONAL,      // Step 2
    CONTACT,       // Step 3
    EMERGENCY,     // Step 4
    PHYSICAL,      // Step 5
    DISEASES,      // Step 6
    FAMILY,        // Step 7
    BLOOD,         // Step 8
    LIFESTYLE,     // Step 9
    ALLERGY,       // Step 10
    REVIEW,        // Review page
    SUCCESS,       // Success page
    COMPLETED      // Static completed profile view
}

// Step 1: Read-only Identity Info
data class IdentityStepState(
    val patientName: String = "علی",
    val patientFamily: String = "محمدی",
    val patientFather: String = "حسین",
    val patientGender: String = "مرد",
    val patientBirthDate: String = "۱۳۶۸/۰۵/۱۲",
    val insuranceNumber: String = "۰۰۲۳۴۵۶۷۸۹",
    val insuranceType: String = "اجباری (کارگری)",
    val lastVisitDate: String = "۱۴۰۴/۰۲/۱۸"
)

// Step 2: Personal Info
data class PersonalStepState(
    val maritalStatus: String = "",
    val job: String = "",
    val citizenship: String = "ایرانی",
    val nationality: String = "ایرانی"
)

// Step 3: Contact Info
data class ContactStepState(
    val mobile: String = "09123456789",
    val email: String = "",
    val province: String = "",
    val city: String = "",
    val address: String = "",
    val postcode: String = "",
    val landline: String = "",
    val postalCode: String = ""
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
    val height: Int = 170,
    val weight: Int = 70
)

// Step 6: Health Questions (Diseases)
data class DiseasesStepState(
    val hasHighBloodSugar: Boolean? = null,
    val hasHighBloodPressure: Boolean? = null,
    val hasHighCholesterol: Boolean? = null,
    val hasChronicDisease: Boolean? = null,
    val chronicDiseases: Set<Int> = emptySet(),
    val hasMentalIllness: Boolean? = null,
    val mentalIllnesses: Set<Int> = emptySet(),
    val hasCancer: Boolean? = null,
    val cancers: Set<Int> = emptySet()
)

// Step 7: Family Health History
data class FamilyStepState(
    val familyHighCholesterol: Boolean? = null,
    val familyHighBloodPressure: Boolean? = null,
    val familyHighBloodSugar: Boolean? = null,
    val familyHasCancer: Boolean? = null,
    val familyCancers: Set<Int> = emptySet()
)

// Step 8: Blood Group
data class BloodGroupStepState(
    val selectedBloodGroupLetter: String? = null,
    val selectedBloodGroupRh: String? = null,
    val isBloodGroupUnknown: Boolean = false
)

// Step 9: Lifestyle
data class LifestyleStepState(
    val isSmoking: Boolean? = null,
    val smokingPattern: String? = null,
    val isDrinking: Boolean? = null,
    val drinkingPattern: String? = null,
    val isExercising: Boolean? = null,
    val exerciseFrequency: String? = null,
    val hasAddiction: Boolean? = null
)

// Step 10: Drug Allergies
data class AllergyStepState(
    val allergies: List<PatientDrugAllergyMock> = emptyList()
)

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
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
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

sealed interface SelfDeclarationIntent : HealthProfileIntent {
    data class ChangeStep(val step: SelfDeclarationStep) : SelfDeclarationIntent
    data class UpdateIdentity(val identity: IdentityStepState) : SelfDeclarationIntent
    data class UpdatePersonal(val personal: PersonalStepState) : SelfDeclarationIntent
    data class UpdateContact(val contact: ContactStepState) : SelfDeclarationIntent
    data class UpdateEmergency(val emergency: EmergencyStepState) : SelfDeclarationIntent
    data class UpdatePhysical(val physical: PhysicalStepState) : SelfDeclarationIntent
    data class UpdateDiseases(val diseases: DiseasesStepState) : SelfDeclarationIntent
    data class UpdateFamily(val family: FamilyStepState) : SelfDeclarationIntent
    data class UpdateBloodGroup(val bloodGroup: BloodGroupStepState) : SelfDeclarationIntent
    data class UpdateLifestyle(val lifestyle: LifestyleStepState) : SelfDeclarationIntent
    data class UpdateAllergy(val allergy: AllergyStepState) : SelfDeclarationIntent
}

sealed interface SelfDeclarationEvent {
    data object NavigateBack : SelfDeclarationEvent
}

