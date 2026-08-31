package com.tamin.taminhamrah.feature.workshops.ui.paymentSheets

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.PaymentSheetQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetDebitReasonsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetPaymentSheetsUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** برگ پرداخت‌ها of one workshop. */
class PaymentSheetsViewModel(
    private val getPaymentSheets: GetPaymentSheetsUseCase,
    private val getDebitReasons: GetDebitReasonsUseCase,
) : BaseViewModel<PaymentSheetsUiState, PartialState, PaymentSheetsEvent, PaymentSheetsIntent>(
    initialState = PaymentSheetsUiState()
) {

    override fun handleIntent(intent: PaymentSheetsIntent): Flow<PartialState> = when (intent) {
        is PaymentSheetsIntent.Open -> open(intent)
        PaymentSheetsIntent.Load -> loadPage(page = 0)
        PaymentSheetsIntent.LoadMore -> loadMore()
        is PaymentSheetsIntent.SearchOpenChanged ->
            flow { emit(PartialState.SearchOpenChanged(intent.isOpen)) }

        is PaymentSheetsIntent.DraftChanged -> flow { emit(PartialState.DraftChanged(intent.draft)) }
        PaymentSheetsIntent.ApplyFilters -> applyFilters(uiState.value.draft)
        PaymentSheetsIntent.ClearFilters -> applyFilters(PaymentSheetFilters())
        PaymentSheetsIntent.LoadDebitReasons -> loadDebitReasons()
    }

    /**
     * Re-opening with the identity already in state does not refetch — returning from a row keeps
     * the page and the filters the user left behind.
     */
    private fun open(intent: PaymentSheetsIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode))
        emitAll(loadPage(page = 0, identity = intent.workshopId to intent.branchCode))
    }

    private fun loadPage(
        page: Int,
        filters: PaymentSheetFilters = uiState.value.applied,
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> =
        flow {
            val (workshopId, branchCode) = identity
            if (workshopId.isBlank() || branchCode.isBlank()) {
                emit(PartialState.Error(null))
                return@flow
            }
            emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)

            val result = getPaymentSheets(
                PaymentSheetQuery(
                    workshopId = workshopId,
                    branchCode = branchCode,
                    payIdFrom = filters.payIdFrom.takeIf { it.isNotBlank() },
                    payIdTo = filters.payIdTo.takeIf { it.isNotBlank() },
                    docDateFrom = filters.docDateFrom,
                    docDateTo = filters.docDateTo,
                    debitReasonCode = filters.debitReason?.code,
                    status = filters.type,
                    page = page,
                )
            )
            emit(
                PartialState.Loaded(
                    uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() }
                )
            )
        }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    private fun applyFilters(filters: PaymentSheetFilters): Flow<PartialState> = flow {
        emit(PartialState.DraftChanged(filters))
        emit(PartialState.Applied(filters))
        emit(PartialState.SearchOpenChanged(false))
        emitAll(loadPage(page = 0, filters = filters))
    }

    /** Fetched once: the picker's options do not change while the screen is open. */
    private fun loadDebitReasons(): Flow<PartialState> = flow<PartialState> {
        if (uiState.value.debitReasons.isNotEmpty()) return@flow
        val reasons = getDebitReasons()
        emit(
            PartialState.DebitReasonsLoaded(
                reasons.items.map { it.toPresentation() }.toImmutableList()
            )
        )
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    override fun reduceState(
        currentState: PaymentSheetsUiState,
        partialState: PartialState,
    ): PaymentSheetsUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
        )

        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(list = currentState.list.failed(partialState.message))
        is PartialState.Loaded -> currentState.copy(list = partialState.list)
        is PartialState.DraftChanged -> currentState.copy(draft = partialState.draft)
        is PartialState.Applied -> currentState.copy(applied = partialState.filters)
        is PartialState.SearchOpenChanged -> currentState.copy(isSearchOpen = partialState.isOpen)
        is PartialState.DebitReasonsLoaded -> currentState.copy(debitReasons = partialState.reasons)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
