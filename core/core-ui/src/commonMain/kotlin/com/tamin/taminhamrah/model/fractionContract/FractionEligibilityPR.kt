package com.tamin.taminhamrah.model.fractionContract

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class FractionEligibilityPR(
    val newAge: String = "",
    val city: String = "",
    val provinceName: String = "",
    val provinceCode: String = "",
    val organizationAddress: String = "",
    val eligibilityStatus: Int = -1,
    val history: Int = 0,
    val isInsurance: Boolean = false,
    val checkFractionMonthStatus: String = "",
    val insuranceId: String = "",
    val branchCode: String = "",
    val cityCode: String = "",
    val contractProvinceCode: String = "",
    val premiumTypeCode: String = "",
    val insuranceTypeCode: String = "",
    val contractNumber: String = "",
    /** Display string built from province + city + organization address. */
    val branchAddress: String = "",
)

@Immutable
@Serializable
data class FractionContractResultPR(
    val contractNumber: String = "",
    val contractDate: Long = 0L,
)
