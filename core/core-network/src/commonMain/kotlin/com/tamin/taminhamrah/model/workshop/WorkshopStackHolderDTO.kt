package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopStackHolderDTO(
    @SerialName("stackId") var stackId: Int?,
    @SerialName("mobile") var mobile: String?,
    @SerialName("birthDate") var birthDate: Long?,
    @SerialName("telephon") var telephon: String?,
    @SerialName("userId") var userId: String?,
    @SerialName("nationalId") var nationalId: String?,
    @SerialName("stackType") var stackType: String?,
    @SerialName("startDate") var startDate: Long?,
    @SerialName("email") var email: String?,
)
