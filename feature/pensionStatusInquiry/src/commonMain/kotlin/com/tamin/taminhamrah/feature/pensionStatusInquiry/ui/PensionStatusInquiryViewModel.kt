package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryEvent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryIntent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryUiState
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryUiState.PartialState
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.SendRequestInquirePensionCertificateUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_empty_pensioner_id

class PensionStatusInquiryViewModel(
    private val getPensionInquiryUseCase: GetPensionInquiryUseCase,
    private val sendRequestInquirePensionCertificateUseCase: SendRequestInquirePensionCertificateUseCase,
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
            is PensionStatusInquiryIntent.OnSendCertificateClicked -> handleSendCertificate(intent.item)
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

    private fun handleSendCertificate(item: PensionInquiryPR): Flow<PartialState> = flow {
        if (uiState.value.isSendingCertificate) return@flow
        val pensionerId = item.pensionerRisUid.ifBlank { item.insuranceNumber }
        if (pensionerId.isBlank()) {
            sendEvent(PensionStatusInquiryEvent.ShowToast(getString(Res.string.error_empty_pensioner_id)))
            return@flow
        }

        emit(PartialState.SendingCertificate(true))
        try {
            val filters = listOf(
                ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL),
            )
            sendRequestInquirePensionCertificateUseCase(filters).collect { result ->
                emit(PartialState.SendingCertificate(false))
                emit(PartialState.SendSuccess(result.message))
            }
        } catch (e: Exception) {
            emit(PartialState.SendingCertificate(false))
            sendEvent(PensionStatusInquiryEvent.ShowToast(e.toSingleLineMessage()))
        }
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
        )
        PartialState.DismissSuccess -> currentState.copy(successMessage = null)
        PartialState.DismissError -> currentState.copy(error = null)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
