package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One row of `workshop-services/member/get-all` — a کارکنان entry. */
@Serializable
data class WorkshopMemberDTO(
    @SerialName("insurance") val insurance: WorkshopInsuranceDTO? = null,
    @SerialName("relationType") val relationType: WorkshopRelationTypeDTO? = null,
    @SerialName("leavingWorkStatus") val leavingWorkStatus: String? = null,
    @SerialName("leavingWorkDate") val leavingWorkDate: String? = null,
    @SerialName("specialSubType") val specialSubType: String? = null,
)

@Serializable
data class WorkshopInsuranceDTO(
    /** شماره بیمه. A string on the wire even though it reads as a number. */
    @SerialName("id") val id: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
    @SerialName("nation") val nation: WorkshopNationDTO? = null,
)

@Serializable
data class WorkshopRelationTypeDTO(
    @SerialName("relationTypeCode") val relationTypeCode: String? = null,
    @SerialName("relationTypeDescription") val relationTypeDescription: String? = null,
)
