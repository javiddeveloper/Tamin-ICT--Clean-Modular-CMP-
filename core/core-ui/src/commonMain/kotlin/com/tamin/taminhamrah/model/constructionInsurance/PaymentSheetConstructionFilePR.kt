package com.tamin.taminhamrah.model.constructionInsurance

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.branch
import taminx.core.core_ui.file_number
import taminx.core.core_ui.label_calculated_amount
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_payment_dead_line
import taminx.core.core_ui.label_registration_date
import taminx.core.core_ui.label_request_number
import taminx.core.core_ui.pregnancy_pay_estimate_result_rial_unit
import taminx.core.core_ui.workshop_number

/** صدور و مدیریت برگه پرداخت — one already-issued payment sheet row. */
data class PaymentSheetConstructionFilePR(
    val orderNumber: String? = null,
    val paymentCode: String? = null,
    val paymentSheetAmount: Long? = null,
    val status: String? = null,
    val paymentDate: String? = null,
    val buildingRequest: BuildingRequestSummaryPR? = null,
)

data class BuildingRequestSummaryPR(
    val debitNumber: String? = null,
    val fileNumber: Long? = null,
    val requestNumber: Long? = null,
    val requestDate: String? = null,
    val totalPayment: Long? = null,
    val paymentDeadLine: String? = null,
    val workshopInfo: WorkshopIdInfoPR? = null,
) {
    /** The expandable request-summary block legacy shows above the payment-sheet list. */
    @Composable
    fun getRequestInfo(): List<KeyValueModel> {
        val rialUnit = stringResource(Res.string.pregnancy_pay_estimate_result_rial_unit)
        return listOf(
            KeyValueModel(
                keyResId = Res.string.label_calculated_amount,
                value = (totalPayment ?: 0L).toPriceFormat(),
                unit = rialUnit,
                textColor = EnumTextColor.AMBER,
            ),
            KeyValueModel(
                keyResId = Res.string.label_payment_dead_line,
                value = paymentDeadLine?.toFormattedDate().orDash(),
                textColor = EnumTextColor.RED,
            ),
            KeyValueModel(keyResId = Res.string.file_number, value = (fileNumber ?: 0).toString(), textColor = EnumTextColor.GREEN),
            KeyValueModel(keyResId = Res.string.label_request_number, value = (requestNumber ?: 0).toString()),
            KeyValueModel(
                keyResId = Res.string.label_registration_date,
                value = workshopInfo?.workshopRegisterDate?.toFormattedDate().orDash(),
            ),
            KeyValueModel(keyResId = Res.string.workshop_number, value = workshopInfo?.workshopId ?: "-"),
            KeyValueModel(keyResId = Res.string.branch, value = workshopInfo?.brhCode ?: "-"),
            KeyValueModel(keyResId = Res.string.label_debit_number, value = debitNumber ?: "-"),
        )
    }
}
