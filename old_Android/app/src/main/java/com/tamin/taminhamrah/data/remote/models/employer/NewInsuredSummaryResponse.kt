package com.tamin.taminhamrah.data.remote.models.employer

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class NewInsuredSummaryResponse(
    var data: NewInsuredSummary? = null
) : BaseResponseNew()

data class NewInsuredSummary(
    var refCode: String? = null,
    var nationalId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var relationDescription: String? = null,
    var jobDescription: String? = null,
    var dateOfBirth: Long? = 0,
    var cityOfBirth: String? = null,
    var cityOfIssue: String? = null,
    var account: Any? = null,
    var relationWithTamin: RelationWithTamin? = null,
    var contact: Any? = null,
    var education: Any? = null,
    var documents: ArrayList<InsuredDoc>? = null,
    var subdominants: Any? = null
) : BaseResponseNew()


data class Personal(
    @SerializedName("@id")
    var id2: Long? = null,
    var id: Long? = null,
    var nationalId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var idCardNumber: Any? = null,
    var idCardSerial1: Any? = null,
    var idCardSerial2: Any? = null,
    var fatherName: Any? = null,
    var dateOfBirth: Long? = null,
    var countryId: String? = null,
    var cityOfBirthId: String? = null,
    var cityOfIssueId: String? = null,
    var foreignId: Any? = null,
    var nation: String? = null,
    var isForien: Any? = null,
    var gender: Any? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var createdBy: String? = null,
    var lastModifiedBy: Any? = null,
    var refrenceCode: String? = null,
    var request: Request? = null,
    var parentId: Any? = null,
    var dependentType: Any? = null,
    var bailType: Any? = null,
    var accounts: Any? = null,
    var contacts: Any? = null,
    var educations: Any? = null,
    var relationWithTamins: Any? = null,
    var user: Any? = null,
    var ssn: Any? = null,
    var dependency: Any? = null,
    var portalRequestId: Any? = null,
    var requestFileList: Any? = null,
    var branchCode: Any? = null
)

data class RelationWithTamin(
    var id: Int? = null,
    var dateOfStart: Long? = null,
    var endDate: Any? = null,
    var endReasonType: Any? = null,
    var insuranceId: Any? = null,
    var personal: Personal? = null,
    var relationWithTamin: Int? = null,
    var organizationId: String? = null,
    var workshopId: String? = null,
    var workshopName: Any? = null,
    var job: String? = null,
    var creationTime: Any? = null,
    var lastModificationTime: Any? = null,
    var createdBy: Any? = null,
    var lastModifiedBy: Any? = null
)

/*data class Request(
    var id: Int? = 0,
    var createdBy: String? = null,
    var creationTime: Long = 0,
    var lastModifiedBy: Any? = null,
    var lastModificationTime: Long = 0
    var refCode: String? = null,
    var userName: String? = null,
    var status: Any? = null,
    var title: String? = null,
    var comment: Any? = null,
    var template: Any? = null,
    var requestType: RequestType? = null,
    var deliverCode: Any? = null,
    var refrenceid: Any? = null,
    var requestDetails: Any? = null,
    var requestChid: Any? = null,
    var fullName: Any? = null,
    var createByName: Any? = null
)*/

/*data class RequestType(
    var createdBy: Any? = null,
    var creationTime: Any? = null,
    var lastModifiedBy: Any? = null,
    var lastModificationTime: Any? = null,
    var id: Int? = 0,
    var title: String? = null,
    var description: String? = null
)*/
