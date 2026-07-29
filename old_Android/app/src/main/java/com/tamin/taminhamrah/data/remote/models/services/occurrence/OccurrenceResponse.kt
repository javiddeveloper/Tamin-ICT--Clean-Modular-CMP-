package com.tamin.taminhamrah.data.remote.models.services.occurrence

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


class OccurrenceResponse(val data: ResponseDetail? = null) : BaseResponseNew()

class ResponseDetail {
    var reportId: String? = null
    var reportRefrenceNumber: String? = null
    var reportDate: Float = 0f
    var reporterType: String? = null
    var insuranceId: String? = null
    var marriageStatusCode: String? = null
    var employeeDate: Float = 0f
    var jobDesc: String? = null
    var reportJobLocation: String? = null
    var reportAddress: String? = null
    var reportPostalCode: String? = null
    var reportTelephone: String? = null
    var nationCode: String? = null
    var isuTypecode: String? = null
    var workshopCode: String? = null
    var workshopName: String? = null
    var workshopBranchCode: String? = null
    var bossFullName: String? = null
    var bossMobileNumber: String? = null
    var workshopAddress: String? = null
    var workshopPostalCode: String? = null
    var workshopTelephone: String? = null
    var occurrenceDate: Float = 0f
    var occurrenceTime: String? = null
    var occurrenceAddress: String? = null
    var occurrenceResult: String? = null
    var occurrenceDesc: String? = null
    var pNationalCode: String? = null
    var pLastName: String? = null
    var pFirstName: String? = null
    var gender: String? = null
    var branchCode: String? = null
    var userId: String? = null
    var status: String? = null
    var vehicle: String? = null
    var rwworkstart: String? = null
    var rwworkfinish: String? = null
    var birthDate: String? = null
    var occurrenceDocumentList: ArrayList<occurrenceDocument> = ArrayList()
}

data class occurrenceDocument(
    val documentId: String? = null,
    val documentFile: DocFile? = null,
    val ocurrenceDocumentType: DocType? = null
)

data class DocFile(
    val createdBy: String? = null,
    val creationTime: Long? = 0,
    val lastModifiedBy: String? = null,
    val lastModificationTime: String? = null,
    val id: String? = null,
    val image: String? = null
)

data class DocType(
    val docTypeId: String? = null,
    val docDesc: String? = null
)