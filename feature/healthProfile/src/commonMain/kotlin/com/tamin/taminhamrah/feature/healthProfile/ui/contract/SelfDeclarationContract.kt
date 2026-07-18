package com.tamin.taminhamrah.feature.healthProfile.ui.contract

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
    SUCCESS        // Success page
}

data class SelfDeclarationUiState(
    val currentStep: SelfDeclarationStep = SelfDeclarationStep.GATE,
    val isLoading: Boolean = false,
    val error: String? = null,
    
    // User Identity Info (Read-only)
    val patientName: String = "علی",
    val patientFamily: String = "محمدی",
    val patientFather: String = "حسین",
    val patientGender: String = "مرد",
    val patientBirthDate: String = "۱۳۶۸/۰۵/۱۲",
    val insuranceNumber: String = "۰۰۲۳۴۵۶۷۸۹",
    val insuranceType: String = "اجباری (کارگری)",
    val lastVisitDate: String = "۱۴۰۴/۰۲/۱۸"
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class StepChanged(val step: SelfDeclarationStep) : PartialState
    }
}

sealed interface SelfDeclarationIntent {
    data class ChangeStep(val step: SelfDeclarationStep) : SelfDeclarationIntent
}

sealed interface SelfDeclarationEvent {
    data object NavigateBack : SelfDeclarationEvent
}
