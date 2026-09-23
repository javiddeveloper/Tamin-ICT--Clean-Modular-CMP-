package com.tamin.taminhamrah.model.constructionInsurance

import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toFormattedDate
import taminx.core.core_ui.Res
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_installment_amount_paid
import taminx.core.core_ui.label_installment_sub_debit_number
import taminx.core.core_ui.label_payment_date
import taminx.core.core_ui.label_payment_status
import taminx.core.core_ui.workshop_number

/**
 * مدیریت اقساط و برگ پرداخت — one individual installment under one debit letter. [dtnExpireDate]/
 * [dtnAmount] (due date + amount payable) are shown in the card's own header, not this expandable
 * body — mirrors the old app's `InstallmentManagementAdapter` (`tvValueDueDate`/`tvValueAmountPayable`
 * outside `getDetailInstallment()`).
 */
data class InstallmentConstructionListPR(
    val workshopId: String? = null,
    val debitNumber: String? = null,
    val debitSubCode: String? = null,
    val dtnAmount: Long? = null,
    val lastPaymentSheetAmount: Long? = null,
    val dtnExpireDate: String? = null,
    val lastPaymentSheetDescription: String? = null,
    val paymentDate: String? = null,
) {
    fun getDetailInstallment(): List<KeyValueModel> = listOf(
        KeyValueModel(
            keyResId = Res.string.label_payment_status,
            value = lastPaymentSheetDescription.orDash(),
            textColor = EnumTextColor.BLUE,
            numeric = false,
        ),
        KeyValueModel(keyResId = Res.string.workshop_number, value = workshopId.orDash()),
        KeyValueModel(keyResId = Res.string.label_debit_number, value = debitNumber.orDash()),
        KeyValueModel(
            keyResId = Res.string.label_installment_amount_paid,
            value = (lastPaymentSheetAmount ?: 0L).toPriceFormat(),
            textColor = if (lastPaymentSheetAmount != null) EnumTextColor.GREEN else EnumTextColor.DEFAULT,
        ),
        KeyValueModel(
            keyResId = Res.string.label_installment_sub_debit_number,
            value = debitSubCode.orDash(),
        ),
        KeyValueModel(
            keyResId = Res.string.label_payment_date,
            value = paymentDate?.toFormattedDate().orDash(),
            textColor = if (paymentDate != null) EnumTextColor.GREEN else EnumTextColor.DEFAULT,
        ),
    )
}
