package com.tamin.taminhamrah.model.requestPaymentForIllDays

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class IllDaysInsuredMainInfoPR(
    val risuid: String = "",
    val nationalCode: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val mobileNumber: String = "",
    val genderCode: String = "",
    val branchCode: String = "",
    val branchName: String = "",
    val bankAccount: String = "",
    val bankName: String = "",
    val insuranceTypeDesc: String = "",
    val insuranceStatusDesc: String = "",
    val serviceDateTimeStamp: Long? = null,
    val branchWorkshops: List<IllDaysBranchWorkshopPR> = emptyList(),
)

@Immutable
@Serializable
data class IllDaysBranchWorkshopPR(
    val id: String = "",
    val label: String = "",
    val branchCode: String = "",
    val branchName: String = "",
    val workshopCode: String = "",
    val workshopName: String = "",
)
