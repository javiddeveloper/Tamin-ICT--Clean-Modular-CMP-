package com.tamin.taminhamrah.feature.contracts.ui.paymentHistory

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.contracts.ui.paymentHistory.contract.ContractPaymentHistoryEvent
import com.tamin.taminhamrah.feature.contracts.ui.paymentHistory.contract.ContractPaymentHistoryIntent
import com.tamin.taminhamrah.feature.contracts.ui.paymentHistory.contract.ContractPaymentHistoryUiState
import com.tamin.taminhamrah.feature.contracts.ui.paymentHistory.contract.ContractPaymentHistoryUiState.PartialState
import com.tamin.taminhamrah.mapper.contracts.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.useCases.contracts.GetContractPaymentHistoryUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ContractPaymentHistoryViewModel(
    private val getContractPaymentHistoryUseCase: GetContractPaymentHistoryUseCase,
) : BaseViewModel<ContractPaymentHistoryUiState, PartialState, ContractPaymentHistoryEvent, ContractPaymentHistoryIntent>(
    initialState = ContractPaymentHistoryUiState(),
) {

    private var contractNumber: String = ""

    override fun handleIntent(intent: ContractPaymentHistoryIntent): Flow<PartialState> =
        when (intent) {
            is ContractPaymentHistoryIntent.Load -> {
                contractNumber = intent.contractNumber
                loadHistory(seed = intent)
            }

            ContractPaymentHistoryIntent.Retry -> loadHistory(seed = null)

            ContractPaymentHistoryIntent.OnBackClicked -> {
                sendEvent(ContractPaymentHistoryEvent.NavigateBack)
                emptyFlow()
            }
        }

    private fun loadHistory(seed: ContractPaymentHistoryIntent.Load?): Flow<PartialState> = flow {
        seed?.let {
            emit(PartialState.HeaderSeeded(it.contractNumber, it.insuranceType))
        }
        emit(PartialState.Loading(true))
        try {
            val items = getContractPaymentHistoryUseCase(contractNumber).first()
                .toPresentation()
                .toImmutableList()
            val successfulTotal = items
                .filter { it.isPaid }
                .sumOf { it.amountPayment.toLongOrNull() ?: 0L }
            emit(
                PartialState.Loaded(
                    items = items,
                    successfulTotalLabel = successfulTotal.toString().toRialAmount(fallback = ""),
                ),
            )
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    override fun reduceState(
        currentState: ContractPaymentHistoryUiState,
        partialState: PartialState,
    ): ContractPaymentHistoryUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            contractNumber = partialState.contractNumber,
            insuranceType = partialState.insuranceType,
        )

        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.Error -> currentState.copy(error = partialState.message)

        is PartialState.Loaded -> currentState.copy(
            items = partialState.items,
            successfulTotalLabel = partialState.successfulTotalLabel,
            error = null,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
