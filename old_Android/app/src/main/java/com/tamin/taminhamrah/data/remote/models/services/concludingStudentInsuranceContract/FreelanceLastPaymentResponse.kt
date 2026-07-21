package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.utils.ConvertDate

class FreelanceLastPaymentResponse(val data: FreelanceLastPayment?=null) : BaseResponseNew()

data class FreelanceLastPayment(
    val chekReloLap: String? = null,
    val lastPaymentDate: String? = null,
    val medicalRsltResend: String? = null
){
    fun getLocalDate():String{
        return if(lastPaymentDate==null || lastPaymentDate=="0")
            ""
        else
            ConvertDate.convertTimestampToPersianDate(lastPaymentDate?:"0")
    }
}
