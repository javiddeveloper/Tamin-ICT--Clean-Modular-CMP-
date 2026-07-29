package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList.detailPayment

import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class DetailPaymentListResponse : ListDataModel<DetailPaymentListModel>()

data class DetailPaymentListModel(
    val year: String? = null,
    val month: String? = null,
    val day: String? = null,
    val wage: Double? = null,
    val debtAmount: Double? = null,
    val descDebtAmount: String? = null,
) {

    fun getPriceWithSeparator(amount: Double?): String {
        var data = "0"
        amount?.let {
            data = Utility.getRialWithSeparator(it.toLong())
        }
        return data
    }

}
