package com.tamin.taminhamrah.model.userRequest

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UserRequestDetailsPR(
    val deferredInstallment: DeferredInstallmentDetailPR? = null,
    val illDay: IllDayDetailPR? = null,
    val articleSixteen: ArticleSixteenDetailPR? = null,
    val followUpObjection: FollowUpObjectionDetailPR? = null,
    val pregnancy: PregnancyDetailPR? = null,
    val rejectReason: String? = null,
    val documents: List<UserRequestDocumentPR> = emptyList(),
)

@Immutable
@Serializable
data class UserRequestDocumentPR(
    val guid: String,
    val documentType: String? = null,
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
    val firstName: String? = null,
    val lastName: String? = null,
    val pensionerFirstName: String? = null,
    val pensionerLastName: String? = null,
    val pensionerNationalId: String? = null,
    val pensionerId: String? = null,
) {
    val pensionerFullName: String
        get() = listOfNotNull(pensionerFirstName, pensionerLastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}

@Immutable
@Serializable
data class IllDayDetailPR(
    val startDate: String? = null,
    val endDate: String? = null,
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
    val prescriptionDate: String? = null,
    val relationship: String? = null,
    val identityNumber: String? = null,
    val identityPlace: String? = null,
    val birthDate: String? = null,
    val expirationDate: String? = null,
)

@Immutable
@Serializable
data class ArticleSixteenDetailPR(
    val meetingDate: String? = null,
    val result: String? = null,
    val defectDesc: String? = null,
)

@Immutable
@Serializable
data class FollowUpObjectionDetailPR(
    val objectionDate: String? = null,
    val reason: String? = null,
    val branchName: String? = null,
    val requestDesc: String? = null,
    val userDesc: String? = null,
    val answerDate: String? = null,
    val answerTypeDesc: String? = null,
    val resultDesc: String? = null,
)

@Immutable
@Serializable
data class PregnancyDetailPR(
    val insuranceNumber: String? = null,
    val insuredFullName: String? = null,
    val mobile: String? = null,
    val bankAccount: String? = null,
    val bankName: String? = null,
    val branchName: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val childbearingDate: String? = null,
    val doctorName: String? = null,
    val doctorId: String? = null,
    val restDays: String? = null,
    val statusDesc: String? = null,
    val typeDesc: String? = null,
)
