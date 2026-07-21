package com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent


data class RequestAddDependent(
    var bailType: BailType? ,
    var branchCode: String? ,
    var cityOfBirthId: String?,
    var cityOfIssueId: String? ,
    var countryId: String ="0001",
    var dateOfBirth: String? ,
    var dependency: Dependency? ,
    var dependentType: DependentType? ,
    var firstName: String? ,
    var id: String? = null ,
    var lastName: String? ,
    var nation: String = "01" ,
    var nationalId: String?,
    var parentId: ParentId = ParentId() ,
    var requestFileList: List<RequestFile>?
)

    data class RequestFile(
        var documentFile: DocumentFile? ,
        var documentType: String? ,
        var id:String? = null ,
        var personal: String? = null
    )

    data class ParentId(
        var id: String? = null
    )

    data class BailType(
        var code: String?
    )
    data class Dependency(
        var id: Int?
    )

    data class DependentType(
        var code: String?
    )

    data class DocumentFile(
        var id: String?
    )
