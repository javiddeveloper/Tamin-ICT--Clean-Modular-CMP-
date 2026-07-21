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

data class SelfDeclarationUiState(
    val currentStep: SelfDeclarationStep = SelfDeclarationStep.GATE,
    val isLoading: Boolean = false,
    val error: String? = null,

    // User Identity Info (Read-only) (Step 1)
    val patientName: String = "علی",
    val patientFamily: String = "محمدی",
    val patientFather: String = "حسین",
    val patientGender: String = "مرد",
    val patientBirthDate: String = "۱۳۶۸/۰۵/۱۲",
    val insuranceNumber: String = "۰۰۲۳۴۵۶۷۸۹",
    val insuranceType: String = "اجباری (کارگری)",
    val lastVisitDate: String = "۱۴۰۴/۰۲/۱۸",

    // Step 2: Personal Info
    val maritalStatus: String = "",
    val job: String = "",
    val citizenship: String = "ایرانی",
    val nationality: String = "ایرانی",

    // Step 3: Contact Info
    val mobile: String = "09123456789",
    val email: String = "",
    val province: String = "",
    val city: String = "",
    val address: String = "",
    val postcode: String = "",
    val landline: String = "",
    val postalCode: String = "",

    // Step 4: Emergency Contact
    val emergencyName: String = "",
    val emergencyFamily: String = "",
    val emergencyRelation: String = "",
    val emergencyMobile: String = "",

    // Step 5: Physical stats
    val height: Int = 170,
    val weight: Int = 70,

    // Step 6: Health Questions (Diseases)
    val hasHighBloodSugar: Boolean? = null,
    val hasHighBloodPressure: Boolean? = null,
    val hasHighCholesterol: Boolean? = null,
    val hasChronicDisease: Boolean? = null,
    val chronicDiseases: Set<Int> = emptySet(),
    val hasMentalIllness: Boolean? = null,
    val mentalIllnesses: Set<Int> = emptySet(),
    val hasCancer: Boolean? = null,
    val cancers: Set<Int> = emptySet(),

    // Step 7: Family Health History
    val familyHighCholesterol: Boolean? = null,
    val familyHighBloodPressure: Boolean? = null,
    val familyHighBloodSugar: Boolean? = null,
    val familyHasCancer: Boolean? = null,
    val familyCancers: Set<Int> = emptySet(),

    // Step 8: Blood Group
    val selectedBloodGroupLetter: String? = null,
    val selectedBloodGroupRh: String? = null,
    val isBloodGroupUnknown: Boolean = false,

    // Step 9: Lifestyle
    val isSmoking: Boolean? = null,
    val smokingPattern: String? = null,
    val isDrinking: Boolean? = null,
    val drinkingPattern: String? = null,
    val isExercising: Boolean? = null,
    val exerciseFrequency: String? = null,
    val hasAddiction: Boolean? = null,

    // Step 10: Drug Allergies
    val allergies: List<PatientDrugAllergyMock> = emptyList()
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class StepChanged(val step: SelfDeclarationStep) : PartialState
        data class StateUpdated(val transform: SelfDeclarationUiState.() -> SelfDeclarationUiState) : PartialState
    }
}

sealed interface SelfDeclarationIntent {
    data class ChangeStep(val step: SelfDeclarationStep) : SelfDeclarationIntent
    data class UpdateState(val transform: SelfDeclarationUiState.() -> SelfDeclarationUiState) : SelfDeclarationIntent
}

sealed interface SelfDeclarationEvent {
    data object NavigateBack : SelfDeclarationEvent
}
