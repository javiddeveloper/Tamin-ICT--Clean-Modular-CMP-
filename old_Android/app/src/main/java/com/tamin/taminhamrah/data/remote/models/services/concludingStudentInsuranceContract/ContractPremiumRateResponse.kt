package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


class ContractPremiumRateResponse (val data:ContractPremiumRateModel?= null):BaseResponseNew()

data class ContractPremiumRateModel(
    val highPremium: Long? = null,
    val history: Int? = null,
    val lowPremium: Long? = null,
    val paymentTabayi: Int? = null
)