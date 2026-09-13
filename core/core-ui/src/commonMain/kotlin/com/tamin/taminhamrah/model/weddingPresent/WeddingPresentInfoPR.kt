package com.tamin.taminhamrah.model.weddingPresent

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class WeddingPresentInfoPR(
    val risuid: String = "",
    val nationalCode: String = "",
    val insuranceFirstName: String = "",
    val insuranceLastName: String = "",
    val fullName: String = "",
    val mobileNumber: String = "",
    val insuranceTypeDesc: String = "",
    val insuranceStatusDesc: String = "",
    val bankAccount: String = "",
    val bankName: String = "",
    val branchCode: String = "",
    val branchName: String = "",
    val requestHelpType: String = "",
    val serviceDateTimeStamp: String = "",
)
