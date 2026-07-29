package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class FinalConfirmResponse (val data :FinalConfirmModel?=null): BaseResponseNew()
data class FinalConfirmModel(
    val contractNumber: Long? = null,
    val contractDate: Long? = null,
)