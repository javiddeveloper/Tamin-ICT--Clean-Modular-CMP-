package com.tamin.taminhamrah.model.pension

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PensionInquiryPR(
    val branchCode: String,
    val insuranceNumber: String,
    val pensionerRisUid: String,
    val pensionerType: String,
    val paymentDate: String,
    val pensionerBaseDate: String,
    val fullName: String,
    val statusDesc: String,
    val isActive: Boolean,
    val sexDesc: String,
    val branchName: String,
    val pensionEndDate: String,
    val nationalId: String,
    val paymentAmount: String
)
