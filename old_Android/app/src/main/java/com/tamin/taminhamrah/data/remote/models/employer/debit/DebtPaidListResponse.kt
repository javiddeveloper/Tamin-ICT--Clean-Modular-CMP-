package com.tamin.taminhamrah.data.remote.models.employer.debit

import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class DebtPaidListResponse : ListDataModel<DebtPaidListModel>()

data class DebtPaidListModel(
    val debitInstallment: DebtInstallmentModel,
    val debitSubCode: String,
    val debitNumber: String,
    val amount: Long,
    val paymentDate: String,
    val expireDate: String,
){
    fun getKeyValueModel() = arrayListOf(
        KeyValueModel(
            "شماره قسط",
             debitSubCode,
            _textColor = EnumTextColor.BLUE,
            _isValueBold = true
        ),
        KeyValueModel(
            "مبلغ قسط",
            Utility.getRialWithSeparator(amount),
            _textColor = EnumTextColor.AMBER,
            _isValueBold = true
        ),
        KeyValueModel(
            "سررسید پرداخت",
            Utility.getDateSeparator(expireDate),
            _textColor = if (expireDate.toLong()<paymentDate.toLong() )EnumTextColor.RED else EnumTextColor.BLUE,
            _isValueBold = true
        ),
        KeyValueModel("تاریخ پرداخت", Utility.getDateSeparator(paymentDate),_textColor = EnumTextColor.BLUE)
    )
}

data class DebtInstallmentModel(
    val debitNumber: String,
    val workshopId: String,
    val branchCode: String,
)

