package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkShopObjectionDTO(
    @SerialName("seqNo") val seqNo: Long? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("objectionType") val objectionType: String? = null,
    @SerialName("objectionDate") val objectionDate: String? = null,
    @SerialName("objectionDesc") val objectionDesc: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("voteType") val voteType: VoteTypeDTO? = null,
)

@Serializable
data class VoteTypeDTO(
    @SerialName("voteTypeDesc") val description: String? = null,
)

@Serializable
data class SmsMessageDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("smsDescription") val smsDescription: String? = null,
    @SerialName("status") val status: String? = null,
)
