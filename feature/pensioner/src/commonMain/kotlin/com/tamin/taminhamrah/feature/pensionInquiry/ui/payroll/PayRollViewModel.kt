package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.*
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollUiState.PartialState
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.pension.PaymentTypeDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollPDFUseCase
import com.tamin.taminhamrah.useCases.pension.SendPayRollToInboxUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.edict_no_pensioner_desc
import taminx.core.core_ui.error_empty_pensioner_id

class PayRollViewModel(
    private val getPensionerPayRollUseCase: GetPensionerPayRollUseCase,
    private val getPensionerPayRollPDFUseCase: GetPensionerPayRollPDFUseCase,
    private val sendPayRollToInboxUseCase: SendPayRollToInboxUseCase,
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
) : BaseViewModel<PayRollUiState, PartialState, PayRollEvent, PayRollIntent>(
    initialState = PayRollUiState(
        searchYear = PersianDateFormatter.currentJalaliYear().toString(),
    )
) {

    init {
        sendIntent(PayRollIntent.LoadPensionerIds)
    }

    override fun handleIntent(intent: PayRollIntent): Flow<PartialState> = flow {
        when (intent) {
            is PayRollIntent.LoadPensionerIds -> {
                emit(PartialState.Loading(true))
                try {
                    getPensionerIdUseCase().collect { list ->
                        val presentationList = list.toPresentation()
                        emit(PartialState.PensionerIdsLoaded(presentationList))
                        if (presentationList.isEmpty()) {
                            emit(PartialState.ShowNoPensionerDialog(true))
                            return@collect
                        }
                        val pensionerId = presentationList.first().pensionerId
                        emit(PartialState.SelectedPensionerIdChanged(pensionerId))

                        val defaultDate = defaultPayRollStartDate()
                        emit(PartialState.StartDateChanged(defaultDate))
                        emit(PartialState.PaymentTypeChanged(PaymentTypeDN.MONTHLY.code))
                        try {
                            val filters = payRollFilters(pensionerId, defaultDate, PaymentTypeDN.MONTHLY.code)
                            getPensionerPayRollUseCase(filters).collect { payRollList ->
                                emit(PartialState.PayRollLoaded(payRollList.map { it.toPresentation() }))
                            }
                        } catch (e: Exception) {
                            val msg = e.toSingleLineMessage()
                            emit(PartialState.Error(msg))
                            sendEvent(PayRollEvent.ShowToast(msg))
                        }
                    }
                } catch (e: Exception) {
                    val msg = e.toSingleLineMessage()
                    emit(PartialState.Error(msg))
                    sendEvent(PayRollEvent.ShowToast(msg))
                }
            }
            is PayRollIntent.ChangeSelectedPensionerId -> {
                emit(PartialState.SelectedPensionerIdChanged(intent.id))
                emit(PartialState.ShowPensionerSheet(false))
            }
            is PayRollIntent.ChangeStartDate -> {
                emit(PartialState.StartDateChanged(intent.date))
            }
            is PayRollIntent.ChangePaymentType -> {
                emit(PartialState.PaymentTypeChanged(intent.type))
            }
            is PayRollIntent.LoadPayRoll -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) {
                    emit(PartialState.Error(getString(Res.string.error_empty_pensioner_id)))
                    return@flow
                }
                emit(PartialState.Loading(true))
                try {
                    val filters = payRollFilters(pensionerId, state.startDate, state.paymentType)
                    getPensionerPayRollUseCase(filters).collect { payRollList ->
                        emit(PartialState.PayRollLoaded(payRollList.map { it.toPresentation() }))
                    }
                } catch (e: Exception) {
                    val msg = e.toSingleLineMessage()
                    emit(PartialState.Error(msg))
                    sendEvent(PayRollEvent.ShowToast(msg))
                }
            }
            is PayRollIntent.RequestSendToInbox -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) return@flow
                emit(PartialState.SendingToInbox(true))
                try {
                    val filters = payRollFilters(pensionerId, state.startDate, state.paymentType)
                    sendPayRollToInboxUseCase(filters).collect {
                        emit(PartialState.SendingToInbox(false))
                        emit(PartialState.ShowSendSuccess(true))
                    }
                } catch (e: Exception) {
                    val msg = e.toSingleLineMessage()
                    emit(PartialState.SendingToInbox(false))
                    emit(PartialState.Error(msg))
                    sendEvent(PayRollEvent.ShowToast(msg))
                }
            }
            is PayRollIntent.DismissSendSuccess -> {
                emit(PartialState.ShowSendSuccess(false))
            }
            is PayRollIntent.LoadPayRollPDF -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) {
                    emit(PartialState.Error(getString(Res.string.error_empty_pensioner_id)))
                    return@flow
                }
                emit(PartialState.Loading(true))
                emit(PartialState.ViewerPdfChanged(null))
                try {
                    val filters = payRollFilters(pensionerId, state.startDate, state.paymentType)
                    getPensionerPayRollPDFUseCase(filters).collect { pdf ->
                        emit(PartialState.ViewerPdfChanged(pdf.toPresentation()))
                    }
                } catch (e: Exception) {
                    val msg = e.toSingleLineMessage()
                    emit(PartialState.Error(msg))
                    emit(PartialState.ViewerDownloadFailed)
                    sendEvent(PayRollEvent.ShowToast(msg))
                }
            }
            is PayRollIntent.DismissPdfViewer -> {
                emit(PartialState.ViewerPdfChanged(null))
            }
            is PayRollIntent.ShowPensionerSheet -> {
                emit(PartialState.ShowPensionerSheet(true))
            }
            is PayRollIntent.DismissPensionerSheet -> {
                emit(PartialState.ShowPensionerSheet(false))
            }
            is PayRollIntent.ShowSearchSheet -> {
                emit(PartialState.ShowSearchSheet(true))
            }
            is PayRollIntent.DismissSearchSheet -> {
                val state = uiState.value
                val committedYear = state.startDate.take(4).ifEmpty {
                    PersianDateFormatter.currentJalaliYear().toString()
                }
                emit(PartialState.SearchYearChanged(committedYear))
                emit(PartialState.ShowSearchSheet(false))
            }
            is PayRollIntent.ChangeSearchYear -> {
                emit(PartialState.SearchYearChanged(intent.year))
            }
            is PayRollIntent.ChangeSearchMonth -> {
                emit(PartialState.SearchMonthChanged(intent.month))
            }
            is PayRollIntent.ChangeSearchPaymentType -> {
                emit(PartialState.SearchPaymentTypeChanged(intent.type))
            }
            is PayRollIntent.ApplySearch -> {
                val state = uiState.value
                val month = state.searchMonth.ifEmpty { "01" }
                val newDate = "${state.searchYear}${month}"
                emit(PartialState.ShowSearchSheet(false))
                emit(PartialState.StartDateChanged(newDate))
                emit(PartialState.PaymentTypeChanged(state.searchPaymentType))
                emit(PartialState.DateFilteredBySearch(true))
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) return@flow
                emit(PartialState.Loading(true))
                try {
                    val filters = payRollFilters(pensionerId, newDate, state.searchPaymentType)
                    getPensionerPayRollUseCase(filters).collect { payRollList ->
                        emit(PartialState.PayRollLoaded(payRollList.map { it.toPresentation() }))
                    }
                } catch (e: Exception) {
                    val msg = e.toSingleLineMessage()
                    emit(PartialState.Error(msg))
                    sendEvent(PayRollEvent.ShowToast(msg))
                }
            }
            is PayRollIntent.ClearDateFilter -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                emit(PartialState.ShowSearchSheet(false))
                emit(PartialState.DateFilteredBySearch(false))
                val defaultDate = defaultPayRollStartDate()
                emit(PartialState.StartDateChanged(defaultDate))
                emit(PartialState.PaymentTypeChanged(PaymentTypeDN.MONTHLY.code))
                emit(PartialState.SearchYearChanged(defaultDate.take(4)))
                emit(PartialState.SearchMonthChanged(defaultDate.drop(4)))
                emit(PartialState.SearchPaymentTypeChanged(PaymentTypeDN.MONTHLY.code))
                if (pensionerId.isNullOrEmpty()) return@flow
                emit(PartialState.Loading(true))
                try {
                    val filters = payRollFilters(pensionerId, defaultDate, PaymentTypeDN.MONTHLY.code)
                    getPensionerPayRollUseCase(filters).collect { payRollList ->
                        emit(PartialState.PayRollLoaded(payRollList.map { it.toPresentation() }))
                    }
                } catch (e: Exception) {
                    val msg = e.toSingleLineMessage()
                    emit(PartialState.Error(msg))
                    sendEvent(PayRollEvent.ShowToast(msg))
                }
            }
            is PayRollIntent.DismissNoPensionerDialog -> {
                emit(PartialState.ShowNoPensionerDialog(false))
                sendEvent(PayRollEvent.NavigateBack)
            }
        }
    }

    override fun reduceState(
        currentState: PayRollUiState,
        partialState: PartialState
    ): PayRollUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.SendingToInbox -> currentState.copy(isSendingToInbox = partialState.isSending)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PensionerIdsLoaded -> currentState.copy(
            // isLoading stays true here: on the happy path LoadPensionerIds immediately chains
            // into the payroll fetch, and PayRollLoaded/Error/ShowNoPensionerDialog are what
            // actually clear it — clearing it here caused the skeleton to flash off mid-chain.
            pensionerIds = partialState.list
        )
        is PartialState.SelectedPensionerIdChanged -> currentState.copy(
            selectedPensionerId = partialState.id
        )
        is PartialState.StartDateChanged -> currentState.copy(
            startDate = partialState.date
        )
        is PartialState.PaymentTypeChanged -> currentState.copy(
            paymentType = partialState.type
        )
        is PartialState.PayRollLoaded -> currentState.copy(
            isLoading = false,
            hasLoadedOnce = true,
            payRollList = partialState.payRoll
        )
        is PartialState.ShowPensionerSheet -> currentState.copy(showPensionerSheet = partialState.show)
        is PartialState.ShowSearchSheet -> currentState.copy(showSearchSheet = partialState.show)
        is PartialState.SearchYearChanged -> currentState.copy(searchYear = partialState.year)
        is PartialState.SearchMonthChanged -> currentState.copy(searchMonth = partialState.month)
        is PartialState.SearchPaymentTypeChanged -> currentState.copy(searchPaymentType = partialState.type)
        is PartialState.DateFilteredBySearch -> currentState.copy(isDateFilteredBySearch = partialState.filtered)
        is PartialState.ShowSendSuccess -> currentState.copy(showSendSuccess = partialState.show)
        is PartialState.ShowNoPensionerDialog -> currentState.copy(isLoading = false, showNoPensionerDialog = partialState.show)
        is PartialState.ViewerPdfChanged -> currentState.copy(
            isLoading = false,
            payRollPDF = partialState.pdf,
            viewerDownloadFailed = false,
        )
        is PartialState.ViewerDownloadFailed -> currentState.copy(
            isLoading = false,
            viewerDownloadFailed = true,
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

    private fun payRollFilters(
        pensionerId: String,
        startDateYYYYMM: String,
        paymentType: String,
    ): List<ApiFilterDN> = listOf(
        ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL),
        ApiFilterDN(FilterProperty.START_DATE, startDateYYYYMM, FilterOperator.EQUAL),
        ApiFilterDN(FilterProperty.PAYMENT_TYPE, paymentType, FilterOperator.EQUAL),
    )

    /** Most recent month with an issued payroll — the current month's isn't out yet, so default
     * one month back, matching [PayRollDateChipsRow]'s excluded-current-month range. */
    private fun defaultPayRollStartDate(): String {
        val (currentYear, currentMonth, _) = PersianDateFormatter.today()
        var year = currentYear
        var month = currentMonth - 1
        if (month <= 0) {
            month += 12
            year -= 1
        }
        return "$year${month.toString().padStart(2, '0')}"
    }
}
