package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetCertificatePaymentSheetPdfUseCase
import com.tamin.taminhamrah.useCases.constructionInsurance.GetPaymentSheetConstructionInfoUseCase
import com.tamin.taminhamrah.useCases.constructionInsurance.IssuancePaymentSheetUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PaymentSheetViewModel(
    private val getPaymentSheetConstructionInfoUseCase: GetPaymentSheetConstructionInfoUseCase,
    private val getCertificatePaymentSheetPdfUseCase: GetCertificatePaymentSheetPdfUseCase,
    private val issuancePaymentSheetUseCase: IssuancePaymentSheetUseCase,
) : BaseViewModel<PaymentSheetUiState, PartialState, PaymentSheetEvent, PaymentSheetIntent>(
    initialState = PaymentSheetUiState()
) {

    private var debitNumber: String = ""
    private var branchCode: String = ""
    private var hasLoaded = false

    override fun handleIntent(intent: PaymentSheetIntent): Flow<PartialState> =
        when (intent) {
            is PaymentSheetIntent.Load -> {
                if (hasLoaded) {
                    emptyFlow()
                } else {
                    hasLoaded = true
                    debitNumber = intent.debitNumber
                    branchCode = intent.branchCode
                    loadPaymentSheets(seed = intent)
                }
            }

            PaymentSheetIntent.Retry -> loadPaymentSheets(seed = null)

            PaymentSheetIntent.DownloadCertificate -> downloadCertificate()

            PaymentSheetIntent.DismissPdfViewer -> flow {
                emit(PartialState.PdfViewerVisibility(false))
                emit(PartialState.PdfLoaded(null))
                emit(PartialState.PdfFailed(false))
            }

            PaymentSheetIntent.IssuePaymentSheet -> issuePaymentSheet()

            PaymentSheetIntent.OnBackClicked -> {
                sendEvent(PaymentSheetEvent.NavigateBack)
                emptyFlow()
            }
        }

    private fun loadPaymentSheets(seed: PaymentSheetIntent.Load?): Flow<PartialState> = flow {
        seed?.let { emit(PartialState.HeaderSeeded(it.debitNumber, it.branchCode)) }
        if (debitNumber.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(PartialState.Loading(true))
        emitAll(
            getPaymentSheetConstructionInfoUseCase(debitNumber)
                .map { list -> PartialState.Loaded(list.map { it.toPR() }.toImmutableList()) as PartialState }
                .catch { e ->
                    val message = e.toSingleLineMessage()
                    sendEvent(PaymentSheetEvent.ShowError(message))
                    emit(PartialState.Error(message))
                }
        )
        emit(PartialState.Loading(false))
    }

    /** «گواهی پرداخت حق بیمه» — mirrors `IssuanceAndManagementPaymentSheetFragment.btnPaymentCertificate`. */
    private fun downloadCertificate(): Flow<PartialState> = flow {
        emit(PartialState.PdfViewerVisibility(true))
        emit(PartialState.PdfFailed(false))
        emit(PartialState.PdfLoading(true))
        try {
            val pdf = getCertificatePaymentSheetPdfUseCase(debitNumber, branchCode).first().toPresentation()
            if (pdf.pdf?.pdf != null) {
                emit(PartialState.PdfLoaded(pdf))
            } else {
                emit(PartialState.PdfFailed(true))
            }
        } catch (e: Exception) {
            sendEvent(PaymentSheetEvent.ShowError(e.toSingleLineMessage()))
            emit(PartialState.PdfFailed(true))
        } finally {
            emit(PartialState.PdfLoading(false))
        }
    }

    /**
     * «صدور برگه پرداخت». The old UI gates this behind a confirm dialog
     * (`DialogManagerMessageOfRequest`) before sending the intent — that confirmation is a UI
     * concern, so this fires the request immediately once the intent arrives.
     */
    private fun issuePaymentSheet(): Flow<PartialState> = flow {
        emit(PartialState.IssuanceFailed(false))
        emit(PartialState.IssuanceLoading(true))
        try {
            val message = issuancePaymentSheetUseCase(debitNumber).first()
            emit(PartialState.IssuanceSucceeded(message))
        } catch (e: Exception) {
            sendEvent(PaymentSheetEvent.ShowError(e.toSingleLineMessage()))
            emit(PartialState.IssuanceFailed(true))
        } finally {
            emit(PartialState.IssuanceLoading(false))
        }
    }

    override fun reduceState(
        currentState: PaymentSheetUiState,
        partialState: PartialState,
    ): PaymentSheetUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            debitNumber = partialState.debitNumber,
            branchCode = partialState.branchCode,
        )

        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)

        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            items = partialState.items,
            error = null,
        )

        is PartialState.PdfViewerVisibility -> currentState.copy(
            showPdfViewer = partialState.visible,
            pdfDownload = if (partialState.visible) currentState.pdfDownload else null,
            pdfDownloadFailed = if (partialState.visible) currentState.pdfDownloadFailed else false,
        )

        is PartialState.PdfLoading -> currentState.copy(isPdfLoading = partialState.loading)
        is PartialState.PdfLoaded -> currentState.copy(pdfDownload = partialState.pdf, pdfDownloadFailed = false)
        is PartialState.PdfFailed -> currentState.copy(pdfDownloadFailed = partialState.failed)

        is PartialState.IssuanceLoading -> currentState.copy(isIssuing = partialState.loading)
        is PartialState.IssuanceSucceeded -> currentState.copy(
            issuanceMessage = partialState.message,
            issuanceFailed = false,
        )
        is PartialState.IssuanceFailed -> currentState.copy(issuanceFailed = partialState.failed)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
