package com.tamin.taminhamrah.model.fractionContract

data class FractionEligibilityDN(
    val newAge: String? = null,
    val city: String? = null,
    val provinceName: String? = null,
    val provinceCode: String? = null,
    val organizationAddress: String? = null,
    val eligibilityStatus: Int? = null,
    val history: Int? = null,
    val isInsurance: Boolean = false,
    val checkFractionMonthStatus: String? = null,
    val insuranceId: String? = null,
    val branchCode: String? = null,
    val cityCode: String? = null,
    val contractProvinceCode: String? = null,
    val premiumTypeCode: String? = null,
    val insuranceTypeCode: String? = null,
    val contractNumber: String? = null,
)

data class FractionContractResultDN(
    val contractNumber: Long? = null,
    val contractDate: Long? = null,
)
