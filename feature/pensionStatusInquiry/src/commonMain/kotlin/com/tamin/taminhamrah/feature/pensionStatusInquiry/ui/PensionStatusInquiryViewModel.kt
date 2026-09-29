package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryEvent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryIntent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryUiState
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryUiState.PartialState
import com.tamin.taminhamrah.mapper.certificate.toUiModelList
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.SendRequestInquirePensionCertificateUseCase
import com.tamin.taminhamrah.useCases.user.GetRecipientsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PensionStatusInquiryViewModel(
    private val getPensionInquiryUseCase: GetPensionInquiryUseCase,
    private val sendRequestInquirePensionCertificateUseCase: SendRequestInquirePensionCertificateUseCase,
    private val getRecipientsUseCase: GetRecipientsUseCase,
) : BaseViewModel<PensionStatusInquiryUiState, PartialState, PensionStatusInquiryEvent, PensionStatusInquiryIntent>(
    initialState = PensionStatusInquiryUiState()
) {

    override fun handleIntent(intent: PensionStatusInquiryIntent): Flow<PartialState> =
        when (intent) {
            PensionStatusInquiryIntent.Load,
            PensionStatusInquiryIntent.OnRetry -> handleLoad()
            PensionStatusInquiryIntent.OnBackClicked -> flow {
                sendEvent(PensionStatusInquiryEvent.NavigateBack)
            }
            PensionStatusInquiryIntent.DismissError -> flow { emit(PartialState.DismissError) }
            PensionStatusInquiryIntent.DismissSuccess -> flow { emit(PartialState.DismissSuccess) }
            PensionStatusInquiryIntent.OnSendCertificateClicked -> flow {
                emit(PartialState.CertificateSheetVisible(true))
            }
            PensionStatusInquiryIntent.DismissCertificateSheet -> flow {
                emit(PartialState.CertificateSheetVisible(false))
            }
            PensionStatusInquiryIntent.OnSelectRecipientClicked -> handleSelectRecipient()
            PensionStatusInquiryIntent.DismissRecipientsSheet -> flow {
                emit(PartialState.RecipientsSheetVisible(false))
            }
            is PensionStatusInquiryIntent.OnRecipientSearchChanged -> flow {
                emit(PartialState.RecipientSearchChanged(intent.query))
            }
            is PensionStatusInquiryIntent.OnRecipientSelected -> flow {
                emit(PartialState.RecipientSelected(intent.recipient))
            }
            is PensionStatusInquiryIntent.OnBranchNameChanged -> flow {
                emit(PartialState.BranchNameChanged(intent.name))
            }
            PensionStatusInquiryIntent.OnIssueCertificateClicked -> handleSendCertificate()
        }

    private fun handleLoad(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPensionInquiryUseCase().collect { list ->
                emit(PartialState.PensionListLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        }
    }

    private fun handleSelectRecipient(): Flow<PartialState> = flow {
        emit(PartialState.RecipientsSheetVisible(true))
        if (uiState.value.recipients.isNotEmpty()) return@flow
        emit(PartialState.LoadingRecipients(true))
        getRecipientsUseCase()
            .catch {
                emit(PartialState.LoadingRecipients(false))
                emit(PartialState.RecipientsSheetVisible(false))
                sendEvent(PensionStatusInquiryEvent.ShowToast(it.toSingleLineMessage()))
            }
            .collect { emit(PartialState.RecipientsLoaded(it.toUiModelList())) }
    }

    private fun handleSendCertificate(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.isSendingCertificate) return@flow
        val recipient = state.selectedRecipient ?: run {
            emit(PartialState.RecipientMissing)
            return@flow
        }

        emit(PartialState.SendingCertificate(true))
        val filters = listOf(
            ApiFilterDN(
                FilterProperty.TARGET,
                buildCertificateTarget(recipient.name, state.branchName),
                FilterOperator.EQ,
            ),
        )
        sendRequestInquirePensionCertificateUseCase(filters)
            .catch {
                emit(PartialState.SendingCertificate(false))
                sendEvent(PensionStatusInquiryEvent.ShowToast(it.toSingleLineMessage()))
            }
            .collect { result -> emit(PartialState.SendSuccess(result.message)) }
    }

    override fun reduceState(
        currentState: PensionStatusInquiryUiState,
        partialState: PartialState,
    ): PensionStatusInquiryUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = if (partialState.isLoading) null else currentState.error,
        )
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PensionListLoaded -> currentState.copy(
            isLoading = false,
            error = null,
            pensionList = partialState.list,
        )
        is PartialState.SendingCertificate -> currentState.copy(isSendingCertificate = partialState.isSending)
        is PartialState.SendSuccess -> currentState.copy(
            isSendingCertificate = false,
            successMessage = partialState.message,
            showCertificateSheet = false,
            selectedRecipient = null,
            branchName = "",
        )
        PartialState.DismissSuccess -> currentState.copy(successMessage = null)
        PartialState.DismissError -> currentState.copy(error = null)
        is PartialState.CertificateSheetVisible -> if (partialState.visible) {
            currentState.copy(showCertificateSheet = true)
        } else {
            currentState.copy(
                showCertificateSheet = false,
                selectedRecipient = null,
                branchName = "",
                showRecipientError = false,
            )
        }
        is PartialState.RecipientsSheetVisible -> currentState.copy(
            showRecipientsSheet = partialState.visible,
            recipientSearchQuery = "",
        )
        is PartialState.LoadingRecipients -> currentState.copy(isLoadingRecipients = partialState.isLoading)
        is PartialState.RecipientsLoaded -> currentState.copy(
            recipients = partialState.list,
            isLoadingRecipients = false,
        )
        is PartialState.RecipientSearchChanged -> currentState.copy(recipientSearchQuery = partialState.query)
        is PartialState.RecipientSelected -> currentState.copy(
            selectedRecipient = partialState.recipient,
            showRecipientsSheet = false,
            recipientSearchQuery = "",
            showRecipientError = false,
        )
        is PartialState.BranchNameChanged -> currentState.copy(branchName = partialState.name)
        PartialState.RecipientMissing -> currentState.copy(showRecipientError = true)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

/** Same `target` text the legacy app sent: "<recipient> شعبه <branch>", branch optional. */
internal fun buildCertificateTarget(recipientName: String, branchName: String): String {
    val branch = branchName.trim()
    if (branch.isEmpty()) return recipientName
    val branchWord = if (branch.contains("شعبه")) "" else " شعبه "
    return "$recipientName$branchWord $branch"
}
