package com.tamin.taminhamrah.data.remote.models.employer

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class NewInsuredUserInfoResponse(
    var data: NewInsuredUserInfo? = null
) : BaseResponseNew()

data class NewInsuredUserInfo(
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

data class Request(
    var id: Long? = null,
    var createdBy: String? = null,
    var creationTime: Long? = null,
    var lastModifiedBy: Any? = null,
    var lastModificationTime: Long? = null,
    var refCode: String? = null,
    var userName: String? = null,
    var status: Any? = null,
    var title: String? = null,
    var comment: Any? = null,
    var template: Any? = null,
    var requestType: RequestType? = null,
    var deliverCode: Any? = null,
    var refrenceid: Any? = null,
    var requestDetails: List<RequestDetail>? = null,
    var requestChid: Any? = null,
    var fullName: Any? = null,
    var createByName: Any? = null
)

data class RequestDetail
    (
    var id: Int? = null,
    var request: Int? = null,
    var organizationId: String? = null,
    var organizationName: Any? = null
)

data class RequestType
    (
    var createdBy: Any? = null,
    var creationTime: Any? = null,
    var lastModifiedBy: Any? = null,
    var lastModificationTime: Any? = null,
    var id: Int? = null,
    var title: Any? = null,
    var description: Any? = null
)



