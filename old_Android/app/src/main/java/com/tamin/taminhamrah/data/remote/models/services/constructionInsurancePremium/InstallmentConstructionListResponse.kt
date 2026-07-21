package com.tamin.taminhamrah.data.remote.models.services.constructionInsurancePremium

import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.entity.EnumTextColor
import com.tamin.taminhamrah.data.entity.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class InstallmentConstructionListResponse : ListDataModel<InstallmentConstructionListModel>()

data class InstallmentConstructionListModel (
    val workshopId: String? = null,
    val debitNumber: String? = null,
    val debitSubCode: String? = null,
    val dtnAmount: Long? = null,
    val lastPaymentSheetAmount: Long? = null,
    val dtnExpireDate: String? = null,
    val lastPaymentSheetDescription: String? = null,
    val paymentDate: String? = null){

    fun getDetailInstallment() = listOf(
        KeyValueModel(_keyStringResId = R.string.label_payment_status , _value = (lastPaymentSheetDescription?: "_"), _textColor = EnumTextColor.BLUE),
        KeyValueModel(_keyStringResId = R.string.workshop_number , _value = (workshopId?: "_").toString()),
        KeyValueModel(_keyStringResId = R.string.label_debit_number , _value = debitNumber?: "_"),
        KeyValueModel(_keyStringResId = R.string.amount_paid , _value = (Utility.getRialWithSeparator(lastPaymentSheetAmount)), _textColor = if (lastPaymentSheetAmount!=null) EnumTextColor.GREEN else EnumTextColor.NORMAL),
        KeyValueModel(_keyStringResId = R.string.debit_number , _value = debitSubCode?: "_"),
        KeyValueModel(_keyStringResId = R.string.label_payment_date , _value = (Utility.getDateSeparator(paymentDate)), _textColor = if (paymentDate!=null) EnumTextColor.GREEN else EnumTextColor.NORMAL))


}


