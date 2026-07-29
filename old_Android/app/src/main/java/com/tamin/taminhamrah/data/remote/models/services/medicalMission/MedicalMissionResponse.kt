package com.tamin.taminhamrah.data.remote.models.services.medicalMission

import com.tamin.taminhamrah.data.remote.models.ListDataModel


class MedicalMissionResponse : ListDataModel<MedicalMission>()

data class MedicalMission (
    var demandInfoId: String? = null,
    var demandTypeCode: String? = null,
    var demandSaveDate: Long? = 0,
    var commFirstName: String? = null,
    var commLastName: String? = null,
    var commFatherName: String? = null,
    var commBirthDate: Long? = 0,
    var commGender: String? = null,
    var commMarriageStatus: String? = null,
    var commIdNumber: String? = null,
    var commExpCityCode: String? = null,
    var commRelationTypeCode: String? = null,
    var commNationalCode: String? = null,
    var guardianNationalCode: Any? = null,
    var commInsuredTypeCode: String? = null,
    var commResidenceCityCode: String? = null,
    var commAddress: String? = null,
    var commMobileNumber: String? = null,
    var commTelephoneNumber: String? = null,
    var commNationality: String? = null,
    var deadDate: Any? = null,
    var insuranceNumber: String? = null,
    var pensionerCode: Any? = null,
    var isuTypeCode: String? = null,
    var nationalCode: String? = null,
    var referBadviCode: Any? = null,
    var status: String? = null,
    var referTypeCode: String? = null,
    var lastJobDesc: String? = null,
    var lastJobCode: String? = null,
    var jobHistoryDesc: Any? = null,
    var hasDrivingCertificate: String? = null,
    var hasVisitBeforeJob: String? = null,
    var hasVisitInJob: String? = null,
    var hasHealthyCertificate: String? = null,
    var hasContract: String? = null,
    var hasExpertJob: String? = null,
    var historyConfirm: Any? = null,
    var militaryStatusCode: String? = null,
    var commissionInResidenceCity: Any? = null,
    var dependencyTypeCode: String? = null,
    var branchCode: String? = null,
    var divan: String? = null,
    var isConfirmed: Any? = null,
    var refId: String? = null,
    var commPostalCode: String? = null,
    var commCaseTypeCode: String? = null,
    var isuStatusTypeCode: String? = null,
    var demandStage: String? = null,
    var sendCentralCommittee: String? = null,
    var commissionCentralId: String? = null,
    var commissionPollDesc: Any? = null,
    var committeePollType: Any? = null,
    var committeeDisapprovalType: Any? = null,
    var commissionPollDate: Any? = null,
    var branchDisapprovalDesc: Any? = null,
    var baseDate: Any? = null,
    var commExpCityName: String? = null,
    var objectionMessage: Any? = null,
    var commResidenceProvinceCode: String? = null,
    var committeeDemandInfoDocumentList: ArrayList<CommitteeDemandInfoDocumentList>? = null,
    var committeeRequestInfoList: ArrayList<CommitteeRequestInfoList>? = null
){
    fun getRequestNumber() = committeeRequestInfoList?.get(0)?.requestNumber?:"-"
    fun getIllnessDesc() = committeeRequestInfoList?.get(0)?.illnessDesc?:"-"
    fun getDoctorName() = "${committeeRequestInfoList?.get(0)?.mainDoctorFirstName} ${committeeRequestInfoList?.get(0)?.mainDoctorLastName}"
    fun getMedicalSystemNumber() = committeeRequestInfoList?.get(0)?.doctorInfoIdTemp?:"-"
    fun getDoctorExpertise() = committeeRequestInfoList?.get(0)?.mainDoctorSpeciality?:"-"
}

data class CommitteeDemandInfoDocumentList (
    var documentId: String? = null,
    var committeeDemandInfo: String? = null,
    var documentTypeId: String? = null,
    var documentFileId: String? = null
)

data class CommitteeRequestInfoDocumentList (
    var documentId: String? = null,
    var committeeRequestInfo: String? = null,
    var documentTypeId: String? = null,
    var documentFileId: String? = null
)

data class CommitteeRequestInfoList (
    var requestInfoId: String? = null,
    var requestNumber: String? = null,
    var committeeDemandInfo: String? = null,
    var doctorInfoId: Any? = null,
    var requestSaveDate: Long = 0,
    var hasDrugUsage: String? = null,
    var hasSurgery: String? = null,
    var hasHospitalization: String? = null,
    var hasOtherDoctor: String? = null,
    var hasOtherDarman: String? = null,
    var otherDarmanDesc: String? = null,
    var hasCommissionOtherOrgan: String? = null,
    var hasCommissionTaminOrgan: String? = null,
    var commissionOtherOrganDesc: Any? = null,
    var hasSupportOrgan: String? = null,
    var supportOrganDesc: Any? = null,
    var bookletTypeCode: String? = null,
    var refrenceReasonCode: String? = null,
    var darmanDocument: Any? = null,
    var illnessDesc: String? = null,
    var mainDoctorFirstName: String? = null,
    var mainDoctorLastName: String? = null,
    var mainDoctorSpeciality: String? = null,
    var doctorInfoIdTemp: String? = null,
    var hasDrugUsageBoolean: Boolean = false,
    var hasSurgeryBoolean: Boolean = false,
    var hasOtherDarmanBoolean: Boolean = false,
    var demandInfoIdTemp: Any? = null,
    var committeeDisapprovalType: Any? = null,
    var branchDisapprovalDesc: Any? = null,
    var status: String? = null,
    var committeeRequestInfoDoctorList: ArrayList<Any>? = null,
    var committeeRequestInfoHospitalList: ArrayList<Any>? = null,
    var committeeRequestInfoDocumentList: ArrayList<CommitteeRequestInfoDocumentList>? = null
)


