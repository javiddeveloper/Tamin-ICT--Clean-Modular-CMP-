package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate

class OptionalInsuranceLastPaymentResponse(
    @SerializedName(value = "data")
    val lastPayment: Long? = null
) : BaseResponseNew() {
    fun getLocalDate(): String {
        return if (lastPayment == null || lastPayment == 0L)
            ""
        else
            ConvertDate.convertTimestampToPersianDate(lastPayment.toString())
    }
}
