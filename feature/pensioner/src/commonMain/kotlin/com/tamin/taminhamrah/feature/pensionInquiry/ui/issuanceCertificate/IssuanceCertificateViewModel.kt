package com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IssuanceCertificateViewModel : BaseViewModel<IssuanceCertificateUiState, IssuanceCertificateUiState.PartialState, IssuanceCertificateEvent, IssuanceCertificateIntent>(
    initialState = IssuanceCertificateUiState()
) {
    override fun handleIntent(intent: IssuanceCertificateIntent): Flow<IssuanceCertificateUiState.PartialState> = flow {
        when (intent) {
            IssuanceCertificateIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: IssuanceCertificateUiState,
        partialState: IssuanceCertificateUiState.PartialState
    ): IssuanceCertificateUiState = when (partialState) {
        is IssuanceCertificateUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is IssuanceCertificateUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): IssuanceCertificateUiState.PartialState =
        IssuanceCertificateUiState.PartialState.Error(message)
}
