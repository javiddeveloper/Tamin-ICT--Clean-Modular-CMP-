package com.tamin.taminhamrah.data.remote.models.employer.debit

 import com.tamin.taminhamrah.data.remote.models.ListDataModel
 import com.tamin.taminhamrah.utils.Utility

class InstallmentPaymentResponse: ListDataModel<InstallmentPaymentModel>()

data class InstallmentPaymentModel(
    val debitSubCode: String?=null,
    val amount: Long?=null,
    val expireDate: String?=null,
    val paymentDate: String?=null,
    val statusDesc: String?=null
){
    fun getExpireDateWithSeparator() = Utility.getDateSeparator(expireDate)
    fun getPaymentAmountWithSeparator() = Utility.getNumberWithSeparator(amount)
}