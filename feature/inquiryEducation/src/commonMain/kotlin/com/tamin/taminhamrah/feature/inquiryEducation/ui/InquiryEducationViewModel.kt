package com.tamin.taminhamrah.feature.inquiryEducation.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationEvent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationIntent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationStep
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState.PartialState
import com.tamin.taminhamrah.mapper.inquiryEducation.toPresentation
import com.tamin.taminhamrah.useCases.inquiryEducation.GetDataForEducationUseCase
import com.tamin.taminhamrah.useCases.inquiryEducation.InquiryEducationCertificateUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.inquiry_education_error_code
import taminx.core.core_ui.inquiry_education_error_select_son
import taminx.core.core_ui.inquiry_education_failure_empty
import taminx.core.core_ui.inquiry_education_success_body

private const val EDUCATION_CODE_LENGTH = 10

class InquiryEducationViewModel(
    private val getDataForEducationUseCase: GetDataForEducationUseCase,
    private val inquiryEducationCertificateUseCase: InquiryEducationCertificateUseCase,
) : BaseViewModel<InquiryEducationUiState, PartialState, InquiryEducationEvent, InquiryEducationIntent>(
    initialState = InquiryEducationUiState(),
) {
    init {
        sendIntent(InquiryEducationIntent.Load)
    }

    override fun handleIntent(intent: InquiryEducationIntent): Flow<PartialState> = flow {
        when (intent) {
            InquiryEducationIntent.Load -> loadSons()
            is InquiryEducationIntent.SelectSon -> {
                emit(PartialState.SonSelected(intent.nationalId))
                emit(
                    PartialState.FieldErrors(
                        sonSelectionError = null,
                        educationCodeError = uiState.value.educationCodeError,
                    ),
                )
            }
            is InquiryEducationIntent.EducationCodeChanged -> {
                val filtered = intent.value.filter { it.isLetterOrDigit() && it.code < 128 }
                    .take(EDUCATION_CODE_LENGTH)
                emit(PartialState.EducationCodeChanged(filtered))
                if (filtered.length == EDUCATION_CODE_LENGTH) {
                    emit(
                        PartialState.FieldErrors(
                            sonSelectionError = uiState.value.sonSelectionError,
                            educationCodeError = null,
                        ),
                    )
                }
            }
            InquiryEducationIntent.Submit -> submit()
            InquiryEducationIntent.Retry -> emit(PartialState.ResetToForm(keepInputs = true))
            InquiryEducationIntent.AnotherInquiry -> emit(PartialState.ResetToForm(keepInputs = false))
            InquiryEducationIntent.BackToServices -> sendEvent(InquiryEducationEvent.NavigateBack)
        }
    }.catch { e ->
        emit(PartialState.Submitting(false))
        emit(PartialState.Loading(false))
        emit(PartialState.SubmitFailure(e.message ?: getString(Res.string.inquiry_education_failure_empty)))
    }

    private suspend fun FlowCollector<PartialState>.loadSons() {
        emit(PartialState.Loading(true))
        try {
            val dn = getDataForEducationUseCase().first()
            val pr = dn.toPresentation()
            val list = pr.list.toImmutableList()
            val autoId = list.singleOrNull()?.nationalId?.takeIf { it.isNotBlank() }
            emit(PartialState.SonsLoaded(sons = list, selectedNationalId = autoId))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emit(PartialState.SonsLoaded(sons = persistentListOf(), selectedNationalId = null))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    private suspend fun FlowCollector<PartialState>.submit() {
        if (uiState.value.isSubmitting) return
        val state = uiState.value
        val sonError = if (state.sons.isNotEmpty() && state.selectedNationalId.isNullOrBlank()) {
            getString(Res.string.inquiry_education_error_select_son)
        } else null
        val codeError = if (!state.educationCode.matches(EDUCATION_CODE_REGEX)) {
            getString(Res.string.inquiry_education_error_code)
        } else null
        if (sonError != null || codeError != null) {
            emit(
                PartialState.FieldErrors(
                    sonSelectionError = sonError,
                    educationCodeError = codeError,
                ),
            )
            return
        }
        val nationalId = state.selectedNationalId ?: return
        emit(PartialState.Submitting(true))
        val result = inquiryEducationCertificateUseCase(
            code = nationalId,
            educationCode = state.educationCode,
        ).first().toPresentation()
        emit(PartialState.Submitting(false))
        if (result.message.isBlank()) {
            emit(PartialState.SubmitFailure(getString(Res.string.inquiry_education_failure_empty)))
            return
        }
        val son = state.sons.first { it.nationalId == nationalId }
        val (jy, jm, jd) = PersianDateFormatter.today()
        val date = "$jy/${jm.toString().padStart(2, '0')}/${jd.toString().padStart(2, '0')}".toPersianDigits()
        val body = getString(
            Res.string.inquiry_education_success_body,
            son.fullName,
            result.message,
        )
        emit(
            PartialState.SubmitSuccess(
                studentName = son.fullName,
                studentNationalId = son.nationalId,
                universityName = result.message,
                inquiryCode = state.educationCode,
                inquiryDate = date,
                successMessage = body,
            ),
        )
    }

    override fun reduceState(
        currentState: InquiryEducationUiState,
        partialState: PartialState,
    ): InquiryEducationUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.SonsLoaded -> currentState.copy(
            sons = partialState.sons,
            selectedNationalId = partialState.selectedNationalId,
        )
        is PartialState.SonSelected -> currentState.copy(selectedNationalId = partialState.nationalId)
        is PartialState.EducationCodeChanged -> currentState.copy(educationCode = partialState.value)
        is PartialState.FieldErrors -> currentState.copy(
            sonSelectionError = partialState.sonSelectionError,
            educationCodeError = partialState.educationCodeError,
        )
        is PartialState.SubmitSuccess -> currentState.copy(
            step = InquiryEducationStep.Success,
            studentName = partialState.studentName,
            studentNationalId = partialState.studentNationalId,
            universityName = partialState.universityName,
            inquiryCode = partialState.inquiryCode,
            inquiryDate = partialState.inquiryDate,
            successMessage = partialState.successMessage,
            failureMessage = "",
        )
        is PartialState.SubmitFailure -> currentState.copy(
            step = InquiryEducationStep.Failure,
            failureMessage = partialState.message,
            isSubmitting = false,
        )
        is PartialState.ResetToForm -> if (partialState.keepInputs) {
            currentState.copy(step = InquiryEducationStep.Form, failureMessage = "")
        } else {
            currentState.copy(
                step = InquiryEducationStep.Form,
                selectedNationalId = currentState.sons.singleOrNull()?.nationalId,
                educationCode = "",
                educationCodeError = null,
                sonSelectionError = null,
                studentName = "",
                studentNationalId = "",
                universityName = "",
                inquiryCode = "",
                inquiryDate = "",
                successMessage = "",
                failureMessage = "",
            )
        }
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.SubmitFailure(message)

    private companion object {
        val EDUCATION_CODE_REGEX = Regex("^[A-Za-z0-9]{10}$")
    }
}
