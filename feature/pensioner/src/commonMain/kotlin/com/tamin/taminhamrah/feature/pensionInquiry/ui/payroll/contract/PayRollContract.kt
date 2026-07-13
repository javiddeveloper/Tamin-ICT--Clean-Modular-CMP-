package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PayRollPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR

@Immutable
data class PayRollUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pensionerIds: List<PensionIdPR> = emptyList(),
    val selectedPensionerId: String? = null,
    val startDate: String = "",
    val paymentType: String = "",
    val payRollList: List<PayRollPR> = emptyList(),
    val payRollPDF: PdfDownloadPR? = null,
    val showPdfDialog: Boolean = false,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class PensionerIdsLoaded(val list: List<PensionIdPR>) : PartialState
        data class SelectedPensionerIdChanged(val id: String) : PartialState
        data class StartDateChanged(val date: String) : PartialState
        data class PaymentTypeChanged(val type: String) : PartialState
        data class PayRollLoaded(val payRoll: List<PayRollPR>) : PartialState
        data class PayRollPDFLoaded(val pdf: PdfDownloadPR) : PartialState
        data class TogglePdfDialog(val show: Boolean) : PartialState
    }
}

sealed interface PayRollIntent {
    data object LoadPensionerIds : PayRollIntent
    data class ChangeSelectedPensionerId(val id: String) : PayRollIntent
    data class ChangeStartDate(val date: String) : PayRollIntent
    data class ChangePaymentType(val type: String) : PayRollIntent
    data object LoadPayRoll : PayRollIntent
    data object LoadPayRollPDF : PayRollIntent
    data class TogglePdfDialog(val show: Boolean) : PayRollIntent
}

sealed interface PayRollEvent {
    data class ShowToast(val message: String) : PayRollEvent
}
