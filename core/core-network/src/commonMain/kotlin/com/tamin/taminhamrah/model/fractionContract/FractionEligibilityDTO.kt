package com.tamin.taminhamrah.model.fractionContract

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FractionEligibilityDTO(
    @SerialName("deadDate") val deadDate: String? = null,
    @SerialName("newAge") val newAge: String? = null,
    @SerialName("city") val city: String? = null,
    /** Wire field `provinceCode` holds the province *name* (legacy swap). */
    @SerialName("provinceCode") val provinceName: String? = null,
    /** Wire field `provinceName` holds the province *code* (legacy swap). */
    @SerialName("provinceName") val provinceCode: String? = null,
    @SerialName("contract") val contract: FractionEligibilityContractDTO? = null,
    @SerialName("insuranceIdState") val insuranceIdState: Boolean? = null,
    @SerialName("chkRelolap") val chkRelolap: String? = null,
    @SerialName("eligibilityStatus") val eligibilityStatus: Int? = null,
    @SerialName("organizationId") val organizationAddress: String? = null,
    @SerialName("previousPayment") val previousPayment: Boolean? = null,
    @SerialName("chkRelolapMessage") val chkRelolapMessage: String? = null,
    @SerialName("age") val age: String? = null,
    @SerialName("checkContractStatus") val checkContractStatus: Int? = null,
    @SerialName("errorHistory") val errorHistory: Int? = null,
    @SerialName("history") val history: Int? = null,
    @SerialName("premiumRateCode") val premiumRateCode: Int? = null,
    @SerialName("growth") val growth: Boolean? = null,
    @SerialName("isPaid") val isPaid: Boolean? = null,
    @SerialName("isInsurance") val isInsurance: Boolean? = null,
    @SerialName("checkFractionMonthStatus") val checkFractionMonthStatus: String? = null,
)

@Serializable
data class FractionEligibilityContractDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("cityCode") val cityCode: String? = null,
    @SerialName("contractNumber") val contractNumber: String? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("premiumType") val premiumType: FractionPremiumTypeDTO? = null,
    @SerialName("premiumTypeCode") val premiumTypeCode: String? = null,
    @SerialName("provinceCode") val provinceCode: String? = null,
    @SerialName("contractStatusObject") val contractStatusObject: FractionContractStatusDTO? = null,
)

@Serializable
data class FractionPremiumTypeDTO(
    @SerialName("insuranceDescription") val insuranceDescription: String? = null,
    @SerialName("insuranceKind") val insuranceKind: String? = null,
    @SerialName("insuranceTypeCode") val insuranceTypeCode: String? = null,
    @SerialName("status") val status: String? = null,
)

@Serializable
data class FractionContractStatusDTO(
    @SerialName("selfIsuContStatDesc") val selfIsuContStatDesc: String? = null,
    @SerialName("selfIsuContStatDode") val selfIsuContStatCode: Int? = null,
)
