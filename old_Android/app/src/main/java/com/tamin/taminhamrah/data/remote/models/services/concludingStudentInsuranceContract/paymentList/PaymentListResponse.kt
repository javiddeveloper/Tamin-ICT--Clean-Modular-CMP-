package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList

import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class PaymentListResponse : ListDataModel<PaymentListModel>()

data class PaymentListModel(
    val nationalId: String? = null,
    val insuranceId: String? = null,
    val debtNumber: String? = null,
    val startTermPayment: String? = null,
    val endTermPayment: String? = null,
    val totalDebt: Double? = null,
    val paymentDeadLine: String? = null,
    val amountPayment: Double? = null,
    val datePayment: String? = null,
    val statusContract: String? = null,
    val statusRecipient: String? = null
){
    fun getPriceWithSeparator(amount : Double?): String {
       var data = "0"
        amount?.let {
            data =Utility.getRialWithSeparator(it.toLong())
        }
        return data
    }


    fun getDateWithSeparator(dateStr : String?): String{
        return if (dateStr.isNullOrBlank())
            "-"
        else Utility.getDateSeparator(dateStr)
    }
}


