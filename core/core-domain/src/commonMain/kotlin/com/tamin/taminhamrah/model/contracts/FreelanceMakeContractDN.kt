package com.tamin.taminhamrah.model.contracts

data class FreelanceMakeContractRequestDN(
    val brchCodeNew: String,
    val cityCode: String,
    val cntDrmn: String,
    val cntFreeJobCode: String,
    val guid: String,
    val guidName: String,
    val premiumRateCode: String,
    val provinceCode: String,
)

data class FreelanceMakeContractParams(
    val monthlyPremium: Long,
    val request: FreelanceMakeContractRequestDN,
)

data class FreelanceContractResultDN(
    val contractNumber: Long?,
    val contractDate: Long?,
)
