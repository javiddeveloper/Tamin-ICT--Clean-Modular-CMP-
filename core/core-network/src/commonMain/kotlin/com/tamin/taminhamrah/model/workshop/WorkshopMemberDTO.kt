package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopMemberDTO(
    @SerialName("leavingWorkStatus") var leavingWorkStatus: String? ,
    @SerialName("leavingWorkDate") var leavingWorkDate: String? ,
    @SerialName("specialSubType") var specialSubType: String?,
)
