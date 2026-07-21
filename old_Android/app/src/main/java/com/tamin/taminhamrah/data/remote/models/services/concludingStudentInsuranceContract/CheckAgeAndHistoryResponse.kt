package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class CheckAgeAndHistoryResponse(val data: CheckAgeAndHistoryModel? = null) : BaseResponseNew()
/*
Because in the response received from eservice, the value of the province's name and code has been moved, we forcibly moved the values - date 02/17/1402
*/
data class CheckAgeAndHistoryModel(
    val deadDate: Any? = null,
    val newAge: String? = null,
    val city: String? = null,
    @SerializedName("provinceCode")
    val provinceName: String? = null,
    @SerializedName("provinceName")
    val provinceCode: String? = null,
    val contract: Contract? = null,
    val insuranceIdState: Boolean? = null,
    val chkRelolap: String? = null,
    val eligibilityStatus: Int? = null,
    @SerializedName("organizationId")
    val organizationAddress: String? = null,
    val previousPayment: Boolean? = null,
    val medicals: Any? = null,
    val chkRelolapMessage: String? = null,
    val age: String? = null,
    val checkContractStatus: Int? = null,
    val errorHistory: Int? = null,
    val history: Int? = null,
    val otherContract: Any? = null,
    val premiumRateCode: Int? = null,
    val growth: Boolean? = null,
    val isPaid: Boolean? = null,
    val protector: Any? = null,
    val isInsurance:Boolean = false,
    val checkFractionMonthStatus :String? = null

    )

data class Protector(
    val contractNumber: String? = null,
    val proCode: Long? = 0,
    val premiumTypeCode: String? = null,
    val fullName: String? = null,
    val nid: String? = null,
    val protectorLetterNo: String? = null,
    val protectorLetterDate: Long? = 0,
    val guid: String? = null,
    val createUID: String? = null,
    val createDate: Long? = 0,
    val confirmDate: String? = null,
    val guidName: String? = null

)

data class Contract(
    val adultLetterDate: Any? = null,
    val adultLetterNumber: Any? = null,
    val age: String? = null,
    val branchCode: String? = null,
    val brchCodeNew: String? = null,
    val cancelDate: Any? = null,
    val cancelUID: Any? = null,
    val canceldesc: Any? = null,
    val cityCode: String? = null,
    val cntDrmn: String? = null,
    val cntFreeJobCode: String? = null,
    val cntIncPayDate3t4: Any? = null,
    val cntMedicalFlag: Any? = null,
    val comment: Any? = null,
    val commissionStatus: String? = null,
    val confirmDate: Any? = null,
    val confirmUID: Any? = null,
    val contractDate: Long? = null,
    val contractNumber: String? = null,
    val contractStatus: String? = null,
    val contractStatusObject: ContractStatusObject? = null,
    val creatDate: Long? = null,
    val createDate: Long? = null,
    val createUID: String? = null,
    val eligibilityStatus: String? = null,
    val freeJob: FreeJob? = null,
    val guid: String? = null,
    val guidName: String? = null,
    val history: String? = null,
    val insuranceId: String? = null,
    val isStudent: Any? = null,
    val medicalExemptionStatus: String? = null,
    val militaryServiceLicense: Any? = null,
    val mobileNumber: String? = null,
    val natinoalCode: String? = null,
    val physicalStatus: String? = null,
    val premiumRate: PremiumRate? = null,
    val premiumRateCode: String? = null,
    val premiumType: PremiumType? = null,
    val premiumTypeCode: String? = null,
    val provinceCode: String? = null,
    val provinceName: Any? = null,
    val refCode: Any? = null,
    val salary: Int? = null,
    val startDate: Long? = null,
    val statusDate: Long? = null,
    val wage: Int? = null,
)

data class ContractStatusObject(
    val selfIsuContStatDesc: String? = null,
    @SerializedName("selfIsuContStatDode")
    val selfIsuContStatCode: Int? = null,
)

data class FreeJob(
    val discrioption: String? = null,
    val endDate: String? = null,
    val fixRank: String? = null,
    val id: Int? = null,
    val iscoCode: Any? = null,
    val jobCode: String? = null,
    val startDate: String? = null,
    val status: String? = null,
)

data class PremiumRate(
    val govermentPercent: String? = null,
    val insurDpercent: String? = null,
    val payrespitelOne: String? = null,
    val payrespitelTwo: String? = null,
    val selfIsuTypeCode: String? = null,
    val spcLowDayWage: String? = null,
    val spcrateCode: String? = null,
    val spcrateDescription: String? = null,
    val status: String? = null,
    val statusStDate: String? = null,
    val treatmentPercap: String? = null,
)

data class PremiumType(
    val insuranceDescription: String? = null,
    val insuranceKind: String? = null,
    val insuranceTypeCode: String? = null,
    val status: String? = null,
    val statusDate: Long? = null,
)