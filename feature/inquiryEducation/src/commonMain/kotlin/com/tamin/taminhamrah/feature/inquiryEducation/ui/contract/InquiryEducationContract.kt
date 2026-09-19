package com.tamin.taminhamrah.feature.inquiryEducation.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class InquiryEducationStep {
    Form,
    Success,
    Failure,
}

@Immutable
data class InquiryEducationUiState(
    val step: InquiryEducationStep = InquiryEducationStep.Form,
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val sons: ImmutableList<EducationDependentItemPR> = persistentListOf(),
    val selectedNationalId: String? = null,
    val educationCode: String = "",
    val educationCodeError: String? = null,
    val sonSelectionError: String? = null,
    val studentName: String = "",
    val studentNationalId: String = "",
    val universityName: String = "",
    val inquiryCode: String = "",
    val inquiryDate: String = "",
    val successMessage: String = "",
    val failureMessage: String = "",
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Submitting(val isSubmitting: Boolean) : PartialState
        data class SonsLoaded(
            val sons: ImmutableList<EducationDependentItemPR>,
            val selectedNationalId: String?,
        ) : PartialState
        data class SonSelected(val nationalId: String) : PartialState
        data class EducationCodeChanged(val value: String) : PartialState
        data class FieldErrors(
            val sonSelectionError: String? = null,
            val educationCodeError: String? = null,
        ) : PartialState
        data class SubmitSuccess(
            val studentName: String,
            val studentNationalId: String,
            val universityName: String,
            val inquiryCode: String,
            val inquiryDate: String,
            val successMessage: String,
        ) : PartialState
        data class SubmitFailure(val message: String) : PartialState
        data class ResetToForm(val keepInputs: Boolean) : PartialState
    }
}

sealed interface InquiryEducationIntent {
    data object Load : InquiryEducationIntent
    data class SelectSon(val nationalId: String) : InquiryEducationIntent
    data class EducationCodeChanged(val value: String) : InquiryEducationIntent
    data object Submit : InquiryEducationIntent
    data object Retry : InquiryEducationIntent
    data object AnotherInquiry : InquiryEducationIntent
    data object BackToServices : InquiryEducationIntent
}

sealed interface InquiryEducationEvent {
    data object NavigateBack : InquiryEducationEvent
}
