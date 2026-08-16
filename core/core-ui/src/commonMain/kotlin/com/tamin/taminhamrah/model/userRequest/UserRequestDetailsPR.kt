package com.tamin.taminhamrah.model.userRequest

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UserRequestDetailsPR(
    val deferredInstallment: DeferredInstallmentDetailPR? = null,
    val illDay: IllDayDetailPR? = null,
    val article16: Article16DetailPR? = null,
    val followUpObjection: FollowUpObjectionDetailPR? = null,
)

@Immutable
@Serializable
data class DeferredInstallmentDetailPR(
    val borrowerName: String? = null,
    val borrowerNationalCode: String? = null,
    val borrowerBirthDate: String? = null,
    val bankName: String? = null,
    val branchName: String? = null,
    val guaranteeAmount: Long? = null,
    val installmentCount: Int? = null,
    val installmentAmount: Long? = null,
    val repaymentAmount: Long? = null,
    val borrowerFullName: String? = null,
    val borrowerNationalId: String? = null,
    val bank: String? = null,
    val branch: String? = null,
)

@Immutable
@Serializable
data class IllDayDetailPR(
    val startDate: String? = null,
    val endDate: String? = null,
    val employerName: String? = null,
    val amount: Long? = null,
)

@Immutable
@Serializable
data class Article16DetailPR(
    val meetingDate: String? = null,
    val result: String? = null,
)

@Immutable
@Serializable
data class FollowUpObjectionDetailPR(
    val objectionDate: String? = null,
    val reason: String? = null,
)
