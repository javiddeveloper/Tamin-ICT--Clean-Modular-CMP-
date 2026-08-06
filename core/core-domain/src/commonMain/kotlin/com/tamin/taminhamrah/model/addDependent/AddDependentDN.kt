package com.tamin.taminhamrah.model.addDependent

data class BranchDN(
    val branchCode: String? = null,
    val branchName: String? = null,
    val workshopCode: String? = null,
    val workshopName: String? = null,
)

data class RegistryDataDN(
    val age: Int? = null,
    val birthDate: String? = null,
    val fatherName: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val nationalId: String? = null,
    val gender: String? = null,
    val registryConfirmState: String? = null,
)

data class FamilyRelationshipDN(
    val id: Int? = null,
    val relationCode: String? = null,
    val relationDesc: String? = null,
    val bailCode: String? = null
)

data class InquiryEducationCodeDN(
    val universityOrSchoolName: String? = null
)

data class UploadImageDN(
    val guid: String = ""
)

data class GeneralResultDN(
    val isSuccess: Boolean = false,
    val message: String? = null,
    val code: Int? = null
)

data class RequestAddDependentDN(
    val bailTypeCode: String? = null,
    val branchCode: String? = null,
    val cityOfBirthId: String? = null,
    val cityOfIssueId: String? = null,
    val countryId: String = "0001",
    val dateOfBirth: String? = null,
    val dependencyId: Int? = null,
    val dependentTypeCode: String? = null,
    val firstName: String? = null,
    val id: String? = null,
    val lastName: String? = null,
    val nation: String = "01",
    val nationalId: String? = null,
    val parentId: String? = null,
    val requestFileList: List<RequestFileDN>? = null
)

data class RequestFileDN(
    val documentFileId: String? = null,
    val documentType: String? = null,
    val id: String? = null,
    val personal: String? = null
)

data class DependentInfoDN(
    val id: String = "",
    val nationalId: String = "",
    val fullName: String = "",
    val relationshipDesc: String = "",
    val isInsuranceActive: Boolean = true,
    val birthDatePersian: String = ""
)
