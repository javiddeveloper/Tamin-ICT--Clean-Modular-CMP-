package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.paymentList

import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.Utility

class PaymentCalculationDetailListResponse : ListDataModel<CalculationModel>()
/*
0: "1401"
1: "01"
2: "02"
3: "حق بيمه"
4: 1393250
5: 334380*/

data class CalculationModel(

    val year: String? = null,
    val month: String? = null,
    val day: String? = null,
    val description: String? = null,
    val wage: Double? = null,
    val amount: Double? = null
) {
    fun getPriceWithSeparator(amount: Double?): String {
        var data = "0 ریال"
        amount?.let {
            data = Utility.getRialWithSeparator(it.toLong())
        }
        return data
    }
}


