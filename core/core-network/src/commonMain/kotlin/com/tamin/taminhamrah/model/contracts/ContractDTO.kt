package com.tamin.taminhamrah.model.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContractDTO(
    @SerialName("adultLetterDate") val adultLetterDate: String?,
    @SerialName("adultLetterNumber") val adultLetterNumber: String?,
    @SerialName("age") val age: String?,
    @SerialName("branchCode") val branchCode: String?,
    @SerialName("brchCodeNew") val brchCodeNew: String?,
    @SerialName("cancelDate") val cancelDate: Long?,
    @SerialName("cancelUID") val cancelUID: String?,
    @SerialName("canceldesc") val canceldesc: String?,
    @SerialName("cityCode") val cityCode: String?,
    @SerialName("cntDrmn") val cntDrmn: String?,
    @SerialName("cntFreeJobCode") val cntFreeJobCode: String?,
    @SerialName("cntIncPayDate3t4") val cntIncPayDate3t4: String?,
    @SerialName("cntMedicalFlag") val cntMedicalFlag: String?,
    @SerialName("comment") val comment: String?,
    @SerialName("commissionStatus") val commissionStatus: String?,
    @SerialName("confirmDate") val confirmDate: Long?,
    @SerialName("confirmUID") val confirmUID: String?,
    @SerialName("contractDate") val contractDate: Long?,
    @SerialName("contractNumber") val contractNumber: Int?,
    @SerialName("contractStatus") val contractStatus: String?,
    @SerialName("contractStatusObject") val contractStatusObject: ContractStatusObjectDTO?,
    @SerialName("creatDate") val creatDate: Long?,
    @SerialName("createDate") val createDate: Long?,
    @SerialName("createUID") val createUID: String?,
    @SerialName("eligibilityStatus") val eligibilityStatus: String?,
    @SerialName("freeJob") val freeJob: FreeJobDTO?,
    @SerialName("guid") val guid: String?,
    @SerialName("guidName") val guidName: String?,
    @SerialName("history") val history: String?,
    @SerialName("insuranceId") val insuranceId: String?,
    @SerialName("isStudent") val isStudent: String?,
    @SerialName("medicalExemptionStatus") val medicalExemptionStatus: String?,
    @SerialName("militaryServiceLicense") val militaryServiceLicense: String?,
    @SerialName("mobileNumber") val mobileNumber: String?,
    @SerialName("natinoalCode") val natinoalCode: String?,
    @SerialName("physicalStatus") val physicalStatus: String?,
    @SerialName("premiumRate") val premiumRate: PremiumRateDTO?,
    @SerialName("premiumRateCode") val premiumRateCode: String?,
    @SerialName("premiumType") val premiumType: PremiumTypeDTO?,
    @SerialName("premiumTypeCode") val premiumTypeCode: String?,
    @SerialName("provinceCode") val provinceCode: String?,
    @SerialName("provinceName") val provinceName: String?,
    @SerialName("refCode") val refCode: String?,
    @SerialName("salary") val salary: Long?,
    @SerialName("startDate") val startDate: Long?,
    @SerialName("statusDate") val statusDate: Long?,
    @SerialName("wage") val wage: Int?,
)

@Serializable
data class ContractStatusObjectDTO(
    @SerialName("selfIsuContStatDesc") val selfIsuContStatDesc: String?,
    @SerialName("selfIsuContStatDode") val selfIsuContStatCode: Int?,
)

@Serializable
data class PremiumTypeDTO(
    @SerialName("insuranceDescription") val insuranceDescription: String?,
    @SerialName("insuranceKind") val insuranceKind: String?,
    @SerialName("insuranceTypeCode") val insuranceTypeCode: String?,
    @SerialName("status") val status: String?,
    @SerialName("statusDate") val statusDate: Long?,
)

@Serializable
data class PremiumRateDTO(
    @SerialName("govermentPercent") val govermentPercent: String?,
    @SerialName("insurDpercent") val insurDpercent: String?,
    @SerialName("payrespitelOne") val payrespitelOne: String?,
    @SerialName("payrespitelTwo") val payrespitelTwo: String?,
    @SerialName("selfIsuTypeCode") val selfIsuTypeCode: String?,
    @SerialName("spcLowDayWage") val spcLowDayWage: String?,
    @SerialName("spcrateCode") val spcrateCode: String?,
    @SerialName("spcrateDescription") val spcrateDescription: String?,
    @SerialName("status") val status: String?,
    @SerialName("statusStDate") val statusStDate: String?,
    @SerialName("treatmentPercap") val treatmentPercap: String?,
)

@Serializable
data class FreeJobDTO(
    @SerialName("discrioption") val discrioption: String?,
    @SerialName("endDate") val endDate: String?,
    @SerialName("fixRank") val fixRank: String?,
    @SerialName("id") val id: Int?,
    @SerialName("iscoCode") val iscoCode: String?,
    @SerialName("jobCode") val jobCode: String?,
    @SerialName("startDate") val startDate: String?,
    @SerialName("status") val status: String?,
)
