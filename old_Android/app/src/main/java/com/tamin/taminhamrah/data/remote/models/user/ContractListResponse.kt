package com.tamin.taminhamrah.data.remote.models.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.ListDataModel
import com.tamin.taminhamrah.utils.ConvertDate
import com.tamin.taminhamrah.utils.Utility
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class ContractListResponse : ListDataModel<ContractItem>()

@Parcelize
 class ContractItem(
    @SerializedName("adultLetterDate")
    val adultLetterDate:@RawValue Any? = null,
    @SerializedName("adultLetterNumber")
    val adultLetterNumber:@RawValue Any? = null,
    @SerializedName("age")
    val age: String? = null,
    @SerializedName("branchCode")
    val branchCode: String? = null,
    @SerializedName("brchCodeNew")
    val brchCodeNew: String? = null,
    @SerializedName("cancelDate")
    val cancelDate: Long? = null,
    @SerializedName("cancelUID")
    val cancelUID: String? = null,
    @SerializedName("canceldesc")
    val canceldesc: String? = null,
    @SerializedName("cityCode")
    val cityCode: String? = null,
    @SerializedName("cntDrmn")
    val cntDrmn: String? = null,
    @SerializedName("cntFreeJobCode")
    val cntFreeJobCode: String? = null,
    @SerializedName("cntIncPayDate3t4")
    val cntIncPayDate3t4:@RawValue Any? = null,
    @SerializedName("cntMedicalFlag")
    val cntMedicalFlag:@RawValue Any? = null,
    @SerializedName("comment")
    val comment:@RawValue Any? = null,
    @SerializedName("commissionStatus")
    val commissionStatus: String? = null,
    @SerializedName("confirmDate")
    val confirmDate: Long? = null,
    @SerializedName("confirmUID")
    val confirmUID:@RawValue Any? = null,
    @SerializedName("contractDate")
    val contractDate: Long? = null,
    @SerializedName("contractNumber")
    val contractNumber: Int? = null,
    @SerializedName("contractStatus")
    val contractStatus: String? = null,
    @SerializedName("contractStatusObject")
    val contractStatusObject: ContractStatusObject? = null,
    @SerializedName("creatDate")
    val creatDate: Long? = null,
    @SerializedName("createDate")
    val createDate: Long? = null,
    @SerializedName("createUID")
    val createUID: String? = null,
    @SerializedName("eligibilityStatus")
    val eligibilityStatus: String? = null,
    @SerializedName("freeJob")
    val freeJob: FreeJob? = null,
    @SerializedName("guid")
    val guid:@RawValue Any? = null,
    @SerializedName("guidName")
    val guidName:@RawValue Any? = null,
    @SerializedName("history")
    val history: String? = null,
    @SerializedName("insuranceId")
    val insuranceId: String? = null,
    @SerializedName("isStudent")
    val isStudent:@RawValue Any? = null,
    @SerializedName("medicalExemptionStatus")
    val medicalExemptionStatus: String? = null,
    @SerializedName("militaryServiceLicense")
    val militaryServiceLicense:@RawValue Any? = null,
    @SerializedName("mobileNumber")
    val mobileNumber: String? = null,
    @SerializedName("natinoalCode")
    val natinoalCode: String? = null,
    @SerializedName("physicalStatus")
    val physicalStatus: String? = null,
    @SerializedName("premiumRate")
    val premiumRate: PremiumRate? = null,
    @SerializedName("premiumRateCode")
    val premiumRateCode: String? = null,
    @SerializedName("premiumType")
    val premiumType: PremiumType? = null,
    @SerializedName("premiumTypeCode")
    val premiumTypeCode: String? = null,
    @SerializedName("provinceCode")
    val provinceCode: String? = null,
    @SerializedName("provinceName")
    val provinceName:@RawValue Any? = null,
    @SerializedName("refCode")
    val refCode:@RawValue Any? = null,
    @SerializedName("salary")
    val salary: Long? = null,
    @SerializedName("startDate")
    val startDate: Long? = null,
    @SerializedName("statusDate")
    val statusDate: Long? = null,
    @SerializedName("wage")
    val wage: Int? = null
):Parcelable{
     fun getPersianDate(timeStamp: Long?): String {
         return timeStamp?.let { ConvertDate.convertTimestampToPersianDate(timeStamp) } ?: "null"
     }
     fun getSupportCondition() = if (cntDrmn=="2") "حمایت درمان ندارد" else "حمایت درمان دارد"

     fun getSalaryWithSeparator(): String {
         var salarySep = "0 ریال"
         salary?.let {
             salarySep = Utility.getRialWithSeparator(salary)
         }
         return salarySep
     }
 }
@Parcelize
data class ContractStatusObject(
    @SerializedName("selfIsuContStatDesc")
    val selfIsuContStatDesc: String? = null,
    @SerializedName("selfIsuContStatDode")
    val selfIsuContStatCode: Int? = null
):Parcelable

@Parcelize
data class PremiumType(
    @SerializedName("insuranceDescription")
    val insuranceDescription: String? = null,
    @SerializedName("insuranceKind")
    val insuranceKind: String? = null,
    @SerializedName("insuranceTypeCode")
    val insuranceTypeCode: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("statusDate")
    val statusDate: Long? = null
):Parcelable

@Parcelize
data class PremiumRate(
    @SerializedName("govermentPercent")
    val govermentPercent: String? = null,
    @SerializedName("insurDpercent")
    val insurDpercent: String? = null,
    @SerializedName("payrespitelOne")
    val payrespitelOne: String? = null,
    @SerializedName("payrespitelTwo")
    val payrespitelTwo: String? = null,
    @SerializedName("selfIsuTypeCode")
    val selfIsuTypeCode: String? = null,
    @SerializedName("spcLowDayWage")
    val spcLowDayWage: String? = null,
    @SerializedName("spcrateCode")
    val spcrateCode: String? = null,
    @SerializedName("spcrateDescription")
    val spcrateDescription: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("statusStDate")
    val statusStDate: String? = null,
    @SerializedName("treatmentPercap")
    val treatmentPercap: String? = null
):Parcelable

@Parcelize
data class FreeJob(
    @SerializedName("discrioption")
    val discrioption: String? = null,
    @SerializedName("endDate")
    val endDate: String? = null,
    @SerializedName("fixRank")
    val fixRank: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("iscoCode")
    val iscoCode: @RawValue Any? = null,
    @SerializedName("jobCode")
    val jobCode: String? = null,
    @SerializedName("startDate")
    val startDate: String? = null,
    @SerializedName("status")
    val status: String? = null
):Parcelable

