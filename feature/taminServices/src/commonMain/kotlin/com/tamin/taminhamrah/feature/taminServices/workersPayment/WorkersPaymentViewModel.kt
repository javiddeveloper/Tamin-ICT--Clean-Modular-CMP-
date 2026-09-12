package com.tamin.taminhamrah.feature.taminServices.workersPayment

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentEvent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentIntent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentUiState
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentUiState.PartialState
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.toPR
import com.tamin.taminhamrah.model.payment.PaymentRequestDN
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workersPayment.GetWorkersPaymentInfoUseCase
import com.tamin.taminhamrah.useCases.workersPayment.PayWorkersDebitUseCase
import com.tamin.taminhamrah.util.NetworkConstants
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.workers_payment_url_missing

import androidx.lifecycle.SavedStateHandle

class WorkersPaymentViewModel(
    private val getWorkersPaymentInfoUseCase: GetWorkersPaymentInfoUseCase,
    private val payWorkersDebitUseCase: PayWorkersDebitUseCase,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<WorkersPaymentUiState, PartialState, WorkersPaymentEvent, WorkersPaymentIntent>(
    initialState = WorkersPaymentUiState(),
) {

    private var isPaymentInFlight: Boolean
        get() = savedStateHandle["is_payment_in_flight"] ?: false
        set(value) {
            savedStateHandle["is_payment_in_flight"] = value
        }

    private var hasLoadedSuccessfully: Boolean
        get() = savedStateHandle["has_loaded_successfully"] ?: false
        set(value) {
            savedStateHandle["has_loaded_successfully"] = value
        }

    init {
        sendIntent(WorkersPaymentIntent.LoadPaymentInfo)
    }

    override fun handleIntent(intent: WorkersPaymentIntent): Flow<PartialState> = when (intent) {
        is WorkersPaymentIntent.LoadPaymentInfo -> loadPaymentInfo(force = false)
        is WorkersPaymentIntent.Retry -> loadPaymentInfo(force = true)
        is WorkersPaymentIntent.OnResumed -> handleOnResumed()
        is WorkersPaymentIntent.OpenPaymentScreen -> flow { emit(PartialState.PaymentScreenOpened(intent.item)) }
        is WorkersPaymentIntent.ClosePaymentScreen -> flow { emit(PartialState.PaymentScreenClosed) }
        is WorkersPaymentIntent.PayItem -> payItem(intent.item)
    }

    private fun handleOnResumed(): Flow<PartialState> {
        if (isPaymentInFlight) {
            isPaymentInFlight = false
            return flow {
                emit(PartialState.PaymentScreenClosed)
                emitAll(loadPaymentInfo(force = true))
            }
        }
        return emptyFlow()
    }

    private fun loadPaymentInfo(force: Boolean = false): Flow<PartialState> {
        if (!force && hasLoadedSuccessfully) return emptyFlow()
        if (uiState.value.isLoading) return emptyFlow()
        return flow {
            try {
                val result = getWorkersPaymentInfoUseCase()
                hasLoadedSuccessfully = true
                emit(
                    PartialState.ListLoaded(
                        totalAmount = result.totalAmount,
                        totalPenalty = result.totalPenalty,
                        totalPremium = result.totalPremium,
                        items = result.list.map { it.toPR() }.toImmutableList(),
                    ),
                )
            } catch (e: Exception) {
                val message = e.toSingleLineMessage()
                emit(PartialState.Error(message))
                sendEvent(WorkersPaymentEvent.ShowToast(message))
            }
        }.onStart {
            emit(PartialState.ClearError)
            emit(PartialState.Loading(true))
        }.onCompletion {
            emit(PartialState.Loading(false))
        }
    }

    private fun payItem(item: WorkersPaymentInfoPR): Flow<PartialState> = flow {
        emit(PartialState.ProcessingPayment(true))
        try {
            val params = WorkersPayDebitParamsDN(
                amount = item.totalPayable,
                dates = listOf(item.fromDatePersian),
                fromToDate = listOf(item.fromDateToDate),
                redirectUrl = NetworkConstants.PAYMENT_RETURN_URI,
            )
            val result = payWorkersDebitUseCase(params)
            val ticket = result.ticket
            if (ticket.isNullOrBlank()) {
                emit(PartialState.ProcessingPayment(false))
                sendEvent(WorkersPaymentEvent.ShowToast(getString(Res.string.workers_payment_url_missing)))
                return@flow
            }
            isPaymentInFlight = true
            emit(PartialState.ProcessingPayment(false))
            sendEvent(
                WorkersPaymentEvent.NavigateToPayment(
                    PaymentRequestDN(
                        ticket = ticket,
                        verifierKey = PaymentVerifierKey.CONSTRUCTION_WORKERS,
                        verifierReference = result.paymentInfo.orEmpty(),
                    ),
                ),
            )
        } catch (e: Exception) {
            emit(PartialState.ProcessingPayment(false))
            sendEvent(WorkersPaymentEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: WorkersPaymentUiState,
        partialState: PartialState,
    ): WorkersPaymentUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.ClearError -> currentState.copy(errorMessage = null)
        is PartialState.ListLoaded -> currentState.copy(
            isLoading = false,
            errorMessage = null,
            totalAmount = partialState.totalAmount,
            totalPenalty = partialState.totalPenalty,
            totalPremium = partialState.totalPremium,
            items = partialState.items,
            selectedPaymentItem = null,
        )

        is PartialState.ProcessingPayment -> currentState.copy(isProcessingPayment = partialState.inProgress)

        is PartialState.PaymentScreenOpened -> currentState.copy(
            selectedPaymentItem = partialState.item,
            errorMessage = null,
        )

        is PartialState.PaymentScreenClosed -> currentState.copy(
            selectedPaymentItem = null,
            errorMessage = null,
        )

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            errorMessage = partialState.message.ifBlank { null },
        )
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(WorkersPaymentEvent.ShowToast(message))
        return PartialState.Loading(false)
    }
}

