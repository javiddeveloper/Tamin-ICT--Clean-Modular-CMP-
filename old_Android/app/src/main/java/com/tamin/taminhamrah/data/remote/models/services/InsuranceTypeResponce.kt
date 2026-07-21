package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class InsuranceTypeResponce : ListDataModel<InsuranceTypeModel>()
data class InsuranceTypeModel (
    val financialCode: Any? = null,
    val insuranceTypeCode: String? = null,
    val insuranceTypeDesc: String? = null,
    val status: String? = null,
    val statusDate: String? = null,
    val telCode: String? = null
)