package com.tamin.taminhamrah.data.remote.models.services.occurrence

class OccurrenceReq {
    var pNationalCode: String? = null
    var pFirstName: String? = null
    var pLastName: String? = null
    var nationCode: Long? = 0
    var birthDate: String? = null
    var gender: Long? = 0
    var marriageStatusCode: Long? = 0
    var jobDesc: String? = null
    var reportJobLocation: String? = null
    var branchCode: String? = null
    var branchName: String? = null
    var reportAddress: String? = null
    var reportTelephone: String? = null
    var reportPostalCode: String? = null
    var insuranceID: String? = null
    var employeeDate: String? = null
    var isuTypecode: String? = null
    var isuTypeDesc: String? = null
    var vehicle: String? = null
    var rwworkstart: String? = null
    var rwworkfinish: String? = null
    var workshopCode: String? = null
    var workshopBranchCode: String? = null
    var workshopName: String? = null
    var bossFullName: String? = null
    var bossMobileNumber: String? = null
    var workshopAddress: String? = null
    var workshopTelephone: String? = null
    var workshopPostalCode: String? = null
    var occurrenceDate: String? = null
    var occurrenceTime: String? = null
    var occurrenceAddress: String? = null
    var occurrenceResult: Long? = 0
    var occurrenceDesc: String? = null
    var reporterType: String? = null
    var occurrenceDocumentList: ArrayList<OccurrenceImage>? = null
}

data class OccurrenceImage(
    var ocurrenceDocumentType: OccurrenceDocumentType? = null,
    var documentFile: OccurrenceDocumentFile? = null
)

data class OccurrenceDocumentType(
    var docTypeId:String?=null
)
data class OccurrenceDocumentFile(
    var id:String?=null
)
