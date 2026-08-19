package com.tamin.taminhamrah.model.userRequest

data class UserRequestDetailsDN(
    val deferredInstallment: DeferredInstallmentDetailDN? = null,
    val illDay: IllDayDetailDN? = null,
    val article16: Article16DetailDN? = null,
    val followUpObjection: FollowUpObjectionDetailDN? = null,
    val pregnancy: PregnancyDetailDN? = null,
    val rejectReason: String? = null,
    val documents: List<UserRequestDocumentDN> = emptyList(),
)

/**
 * A single uploaded document attached to a short-term or article-16 request.
 * [guid] is the identifier passed to the upload-image endpoint to download the raw bytes.
 * [documentType] is the server-provided type/label used to resolve a human-readable title.
 */
data class UserRequestDocumentDN(
    val guid: String,
    val documentType: String? = null,
)

data class DeferredInstallmentDetailDN(
    val borrowerName: String? = null,
    val borrowerNationalCode: String? = null,
    val borrowerBirthDate: String? = null,
    val borrowerBirthDateMillis: Long? = null,
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
    val firstName: String? = null,
    val lastName: String? = null,
    val pensionerFirstName: String? = null,
    val pensionerLastName: String? = null,
    val pensionerNationalId: String? = null,
    val pensionerId: String? = null,
)

data class IllDayDetailDN(
    val startDate: String? = null,
    val endDate: String? = null,
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,
    val employerName: String? = null,
    val amount: Long? = null,
    val insuranceNumber: String? = null,
    val insuredFullName: String? = null,
    val mobile: String? = null,
    val bankAccount: String? = null,
    val bankName: String? = null,
    val branchName: String? = null,
    val doctorName: String? = null,
    val doctorId: String? = null,
    val prescriptionDateMillis: Long? = null,
    val relationship: String? = null,
    val identityNumber: String? = null,
    val identityPlace: String? = null,
    val birthDate: String? = null,
    val expirationDate: String? = null,
)

data class Article16DetailDN(
    val meetingDate: String? = null,
    val result: String? = null,
    val defectDesc: String? = null,
)

data class FollowUpObjectionDetailDN(
    val objectionDate: String? = null,
    val reason: String? = null,
    val branchName: String? = null,
    val requestDesc: String? = null,
    val userDesc: String? = null,
    val answerDate: String? = null,
    val answerTypeDesc: String? = null,
    val resultDesc: String? = null,
)

data class PregnancyDetailDN(
    val insuranceNumber: String? = null,
    val insuredFullName: String? = null,
    val mobile: String? = null,
    val bankAccount: String? = null,
    val bankName: String? = null,
    val branchName: String? = null,
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,
    val childbearingDateMillis: Long? = null,
    val doctorName: String? = null,
    val doctorId: String? = null,
    val restDays: String? = null,
    val statusDesc: String? = null,
    val typeDesc: String? = null,
)
