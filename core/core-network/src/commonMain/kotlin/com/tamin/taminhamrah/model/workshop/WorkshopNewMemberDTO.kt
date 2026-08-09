package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopNewMemberDTO(
    @SerialName("id") var id: Long?,
    @SerialName("dateOfStart") var dateOfStart: Long?,
    @SerialName("insuranceId") var insuranceId: String?,
    @SerialName("relationWithTamin") var relationWithTamin: Int?,
    @SerialName("organizationId") var organizationId: String?,
    @SerialName("workshopId") var workshopId: String?,
    @SerialName("job") var job: String?
)
