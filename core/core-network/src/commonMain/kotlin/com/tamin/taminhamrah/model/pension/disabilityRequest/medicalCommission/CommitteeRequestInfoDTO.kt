package com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitteeRequestInfoDTO(
    @SerialName("requestInfoId") val requestInfoId: String? = null,
    @SerialName("requestNumber") val requestNumber: String? = null,
    @SerialName("committeeDemandInfo") val committeeDemandInfo: String? = null,
    @SerialName("doctorInfoId") val doctorInfoId: String? = null,
    @SerialName("requestSaveDate") val requestSaveDate: Long? = null,
    @SerialName("hasDrugUsage") val hasDrugUsage: String? = null,
    @SerialName("hasSurgery") val hasSurgery: String? = null,
    @SerialName("hasHospitalization") val hasHospitalization: String? = null,
    @SerialName("hasOtherDoctor") val hasOtherDoctor: String? = null,
    @SerialName("hasOtherDarman") val hasOtherDarman: String? = null,
    @SerialName("otherDarmanDesc") val otherDarmanDesc: String? = null,
    @SerialName("hasCommissionOtherOrgan") val hasCommissionOtherOrgan: String? = null,
    @SerialName("hasCommissionTaminOrgan") val hasCommissionTaminOrgan: String? = null,
    @SerialName("commissionOtherOrganDesc") val commissionOtherOrganDesc: String? = null,
    @SerialName("hasSupportOrgan") val hasSupportOrgan: String? = null,
    @SerialName("supportOrganDesc") val supportOrganDesc: String? = null,
    @SerialName("bookletTypeCode") val bookletTypeCode: String? = null,
    @SerialName("refrenceReasonCode") val refrenceReasonCode: String? = null,
    @SerialName("darmanDocument") val darmanDocument: String? = null,
    @SerialName("illnessDesc") val illnessDesc: String? = null,
    @SerialName("mainDoctorFirstName") val mainDoctorFirstName: String? = null,
    @SerialName("mainDoctorLastName") val mainDoctorLastName: String? = null,
    @SerialName("mainDoctorSpeciality") val mainDoctorSpeciality: String? = null,
    @SerialName("hasDrugUsageBoolean") val hasDrugUsageBoolean: Boolean? = null,
    @SerialName("hasSurgeryBoolean") val hasSurgeryBoolean: Boolean? = null,
    @SerialName("hasOtherDarmanBoolean") val hasOtherDarmanBoolean: Boolean? = null,
    @SerialName("committeeRequestInfoDocumentList") val committeeRequestInfoDocumentList: List<CommitteeRequestInfoDocumentDTO>? = null,
)
