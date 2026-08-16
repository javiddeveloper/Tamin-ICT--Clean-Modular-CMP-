package com.tamin.taminhamrah.model.userRequest

data class UserRequestDetailsDN(
    val deferredInstallment: DeferredInstallmentDetailDN? = null,
    val illDay: IllDayDetailDN? = null,
    val article16: Article16DetailDN? = null,
    val followUpObjection: FollowUpObjectionDetailDN? = null,
)

data class DeferredInstallmentDetailDN(
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

data class IllDayDetailDN(
    val startDate: String? = null,
    val endDate: String? = null,
    val employerName: String? = null,
    val amount: Long? = null,
)

data class Article16DetailDN(
    val meetingDate: String? = null,
    val result: String? = null,
)

data class FollowUpObjectionDetailDN(
    val objectionDate: String? = null,
    val reason: String? = null,
)
