package com.tamin.taminhamrah.model.constructionInsurance

import com.tamin.taminhamrah.ui.orDash
import com.tamin.taminhamrah.util.toFormattedDate
import taminx.core.core_ui.Res
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_debit_start_date
import taminx.core.core_ui.label_debit_end_date
import taminx.core.core_ui.label_debit_status
import taminx.core.core_ui.label_debit_step
import taminx.core.core_ui.workshop_number

/** بدهی‌های تقسیط‌شده — one flat per-installment debit detail row for one debit letter. */
data class InstallmentDebitListPR(
    val workshopId: String? = null,
    val debitNumber: String? = null,
    val debitStepDescription: String? = null,
    val debitStatusDescription: String? = null,
    val debitStartDate: String? = null,
    val debitEndDate: String? = null,
    val remainingAmount: Long? = null,
) {
    fun getDetailInstallmentDebitList(): List<KeyValueModel> = listOf(
        KeyValueModel(keyResId = Res.string.workshop_number, value = workshopId.orDash()),
        KeyValueModel(keyResId = Res.string.label_debit_step, value = debitStepDescription.orDash(), numeric = false),
        KeyValueModel(keyResId = Res.string.label_debit_number, value = debitNumber.orDash()),
        KeyValueModel(keyResId = Res.string.label_debit_start_date, value = debitStartDate?.toFormattedDate().orDash()),
        KeyValueModel(keyResId = Res.string.label_debit_end_date, value = debitEndDate?.toFormattedDate().orDash()),
        KeyValueModel(
            keyResId = Res.string.label_debit_status,
            value = debitStatusDescription.orDash(),
            numeric = false,
        ),
    )
}
