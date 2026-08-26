package com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.*
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.IssuanceCertificateUiState.PartialState
import com.tamin.taminhamrah.mapper.certificate.toUiModelList
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.user.GetIdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetRecipientsUseCase
import com.tamin.taminhamrah.useCases.user.GetWageCertificateReportUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class IssuanceCertificateViewModel(
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getRecipientsUseCase: GetRecipientsUseCase,
    private val getWageCertificateReportUseCase: GetWageCertificateReportUseCase,
    private val getIdentityInfoUseCase: GetIdentityInfoUseCase,
) : BaseViewModel<IssuanceCertificateUiState, PartialState, IssuanceCertificateEvent, IssuanceCertificateIntent>(
    initialState = IssuanceCertificateUiState()
) {

    init {
        sendIntent(IssuanceCertificateIntent.Init)
    }

    override fun handleIntent(intent: IssuanceCertificateIntent): Flow<PartialState> =
        when (intent) {
            is IssuanceCertificateIntent.Init -> handleInit()
            is IssuanceCertificateIntent.ShowPensionerSheet -> flow { emit(PartialState.ShowPensionerSheet(true)) }
            is IssuanceCertificateIntent.DismissPensionerSheet -> flow { emit(PartialState.ShowPensionerSheet(false)) }
            is IssuanceCertificateIntent.SelectPensionerId -> flow {
                emit(PartialState.SelectedPensionerIdChanged(intent.id))
                emit(PartialState.ShowPensionerSheet(false))
            }
            is IssuanceCertificateIntent.ShowRecipientsSheet -> handleShowRecipientsSheet()
            is IssuanceCertificateIntent.DismissRecipientsSheet -> flow { emit(PartialState.ShowRecipientsSheet(false)) }
            is IssuanceCertificateIntent.SelectRecipient -> flow {
                emit(PartialState.SelectedRecipientChanged(intent.recipient))
                emit(PartialState.ShowRecipientsSheet(false))
            }
            is IssuanceCertificateIntent.SearchRecipients -> flow { emit(PartialState.SearchQueryChanged(intent.query)) }
            is IssuanceCertificateIntent.ChangeBranchName -> flow { emit(PartialState.BranchNameChanged(intent.name)) }
            is IssuanceCertificateIntent.GoToNextStep -> handleGoToNextStep()
            is IssuanceCertificateIntent.GoToPreviousStep -> flow {
                emit(PartialState.GoToStep(IssuanceCertificateStep.Info))
            }
            is IssuanceCertificateIntent.SubmitRequest -> handleSubmitRequest()
            is IssuanceCertificateIntent.DismissSuccessDialog -> flow {
                emit(PartialState.ShowSuccessDialog(false))
                sendEvent(IssuanceCertificateEvent.NavigateHome)
            }
        }

    private fun handleInit(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        getIdentityInfoUseCase()
            .catch { sendEvent(IssuanceCertificateEvent.ShowToast(it.toSingleLineMessage())) }
            .collect { identity ->
                val fullName = listOfNotNull(identity.firstName, identity.lastName)
                    .joinToString(" ")
                emit(PartialState.FullNameLoaded(fullName))
            }
        getPensionerIdUseCase()
            .catch {
                sendEvent(IssuanceCertificateEvent.ShowToast(it.toSingleLineMessage()))
                emit(PartialState.Loading(false))
            }
            .collect { list ->
                val presentationList = list.toPresentation().toImmutableList()
                emit(PartialState.PensionerIdsLoaded(presentationList))
                emit(PartialState.SelectedPensionerIdChanged(presentationList.firstOrNull()?.pensionerId))
                emit(PartialState.Loading(false))
            }
    }

    private fun handleShowRecipientsSheet(): Flow<PartialState> = flow {
        emit(PartialState.ShowRecipientsSheet(true))
        if (uiState.value.recipients.isEmpty()) {
            emit(PartialState.LoadingRecipients(true))
            getRecipientsUseCase()
                .catch {
                    sendEvent(IssuanceCertificateEvent.ShowToast(it.toSingleLineMessage()))
                    emit(PartialState.LoadingRecipients(false))
                }
                .collect { list ->
                    emit(PartialState.RecipientsLoaded(list.toUiModelList().toImmutableList()))
                    emit(PartialState.LoadingRecipients(false))
                }
        }
    }

    private fun handleGoToNextStep(): Flow<PartialState> = flow {
        val fieldErrors = validateFields()
        if (fieldErrors != null) {
            emit(fieldErrors)
            return@flow
        }
        emit(PartialState.ClearFieldErrors)
        emit(PartialState.GoToStep(IssuanceCertificateStep.Confirm))
    }

    private fun validateFields(): PartialState.ValidationFailed? {
        val state = uiState.value
        val pensionerIdError = if (state.selectedPensionerId.isNullOrBlank()) {
            "شماره مستمری را انتخاب کنید"
        } else null
        val recipientError = if (state.selectedRecipient == null) {
            "گیرنده را انتخاب کنید"
        } else null
        val branchNameError = when {
            state.branchName.isBlank() -> "نام شعبه را وارد کنید"
            INVALID_BRANCH_NAME_CHARS_REGEX.containsMatchIn(state.branchName) ->
                "نام شعبه شامل کاراکتر غیر مجاز است"
            state.branchName.length < 2 && !NUMERIC_REGEX.matches(state.branchName) ->
                "نام شعبه نمی‌تواند کمتر از دو کاراکتر باشد"
            else -> null
        }

        return if (pensionerIdError != null || recipientError != null || branchNameError != null) {
            PartialState.ValidationFailed(pensionerIdError, recipientError, branchNameError)
        } else null
    }

    private fun formatBranchName(rawBranchName: String): String =
        if (rawBranchName.isNotBlank() && !rawBranchName.contains("شعبه")) {
            " شعبه $rawBranchName"
        } else {
            rawBranchName
        }

    private fun handleSubmitRequest(): Flow<PartialState> = flow {
        val fieldErrors = validateFields()
        if (fieldErrors != null) {
            emit(fieldErrors)
            emit(PartialState.GoToStep(IssuanceCertificateStep.Info))
            return@flow
        }

        val state = uiState.value
        val filters = listOf(
            ApiFilterDN(FilterProperty.PENSIONER_ID, state.selectedPensionerId!!, FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.RECIPIENT, state.selectedRecipient!!.code, FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.TARGET, "", FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.BRANCH_NAME, formatBranchName(state.branchName), FilterOperator.EQUAL),
        )

        emit(PartialState.Submitting(true))
        getWageCertificateReportUseCase(filters)
            .catch {
                sendEvent(IssuanceCertificateEvent.ShowToast(it.toSingleLineMessage()))
                emit(PartialState.Submitting(false))
            }
            .collect {
                emit(PartialState.Submitting(false))
                emit(PartialState.ShowSuccessDialog(true))
            }
    }

    override fun reduceState(
        currentState: IssuanceCertificateUiState,
        partialState: PartialState
    ): IssuanceCertificateUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)

        is PartialState.GoToStep -> currentState.copy(currentStep = partialState.step)
        is PartialState.FullNameLoaded -> currentState.copy(fullName = partialState.fullName)

        is PartialState.PensionerIdsLoaded -> currentState.copy(pensionerIds = partialState.list)
        is PartialState.SelectedPensionerIdChanged -> currentState.copy(
            selectedPensionerId = partialState.id,
            pensionerIdError = null
        )
        is PartialState.ShowPensionerSheet -> currentState.copy(showPensionerSheet = partialState.show)

        is PartialState.RecipientsLoaded -> currentState.copy(
            recipients = partialState.list,
            filteredRecipients = partialState.list
        )
        is PartialState.LoadingRecipients -> currentState.copy(isLoadingRecipients = partialState.isLoading)
        is PartialState.SelectedRecipientChanged -> currentState.copy(
            selectedRecipient = partialState.recipient,
            recipientError = null
        )
        is PartialState.ShowRecipientsSheet -> currentState.copy(
            showRecipientsSheet = partialState.show,
            searchQuery = if (!partialState.show) "" else currentState.searchQuery,
            filteredRecipients = if (!partialState.show) currentState.recipients else currentState.filteredRecipients
        )
        is PartialState.SearchQueryChanged -> {
            val filtered: List<RecipientPR> = if (partialState.query.isBlank()) {
                currentState.recipients
            } else {
                currentState.recipients.filter { it.name.contains(partialState.query) }
            }
            currentState.copy(
                searchQuery = partialState.query,
                filteredRecipients = filtered.toImmutableList()
            )
        }

        is PartialState.BranchNameChanged -> currentState.copy(
            branchName = partialState.name,
            branchNameError = null
        )

        is PartialState.ValidationFailed -> currentState.copy(
            pensionerIdError = partialState.pensionerIdError,
            recipientError = partialState.recipientError,
            branchNameError = partialState.branchNameError
        )
        is PartialState.ClearFieldErrors -> currentState.copy(
            pensionerIdError = null,
            recipientError = null,
            branchNameError = null
        )
        is PartialState.ShowSuccessDialog -> currentState.copy(showSuccessDialog = partialState.show)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

    private companion object {
        val INVALID_BRANCH_NAME_CHARS_REGEX = Regex("[a-zA-Z$&+،,:;=\\\\?@#|/'<>.^*()%!-]")
        val NUMERIC_REGEX = Regex("[0-9]+")
    }
}
