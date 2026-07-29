package com.tamin.taminhamrah.data.remote.models.services.insuranceRegistration

import com.google.gson.annotations.SerializedName

data class RegistrationReq(
    val personal: PersonalInfo? = null,
    val relationWithTamin: RelationWithTamin? = null
)

data class PersonalInfo(
    val branchCode: String? = null,
    val cityOfBirthId: String? = null,
    val cityOfIssueId: String? = null,
    @SerializedName("fatherName")
    val serialNumber: String? = null,
    val requestFileList: MutableList<RequestFile>? = null
)

data class RequestFile(
    val documentFile: DocumentFile? = null,
    val documentType: String? = null,
    val id: String?=null ,
    val personal: Any?=null

)

data class DocumentFile(
    val id: String? = null
)

data class RelationWithTamin(
    val relationWithTamin: String? = null
)

