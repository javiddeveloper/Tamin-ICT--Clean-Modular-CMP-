package com.tamin.taminhamrah.model.userRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeferredInstallmentDetailDTO(
    @SerialName("borrowerName") val borrowerName: String? = null,
    @SerialName("borrowerNationalCode") val borrowerNationalCode: String? = null,
    @SerialName("borrowerBirthDate") val borrowerBirthDate: String? = null,
    @SerialName("bankName") val bankName: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("guaranteeAmount") val guaranteeAmount: Long? = null,
    @SerialName("installmentCount") val installmentCount: Int? = null,
    @SerialName("installmentAmount") val installmentAmount: Long? = null,
    @SerialName("repaymentAmount") val repaymentAmount: Long? = null,
    @SerialName("borrowerFullName") val borrowerFullName: String? = null,
    @SerialName("borrowerNationalId") val borrowerNationalId: String? = null,
    @SerialName("bank") val bank: String? = null,
    @SerialName("branch") val branch: String? = null,
)

@Serializable
data class IllDayDetailDTO(
    @SerialName("startDate") val startDate: String? = null,
    @SerialName("endDate") val endDate: String? = null,
    @SerialName("employerName") val employerName: String? = null,
    @SerialName("amount") val amount: Long? = null,
)

@Serializable
data class Article16DetailDTO(
    @SerialName("meetingDate") val meetingDate: String? = null,
    @SerialName("result") val result: String? = null,
)

@Serializable
data class FollowUpObjectionDetailDTO(
    @SerialName("objectionDate") val objectionDate: String? = null,
    @SerialName("reason") val reason: String? = null,
)