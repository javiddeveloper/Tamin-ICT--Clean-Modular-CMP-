package com.tamin.taminhamrah.model.addDependent

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class BranchPR(
    val branchCode: String = "",
    val branchName: String = "",
    val workshopCode: String = "",
    val workshopName: String = ""
)

@Immutable
@Serializable
data class RegistryDataPR(
    val age: Int = 0,
    val birthDate: String = "",
    val fatherName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val nationalId: String = "",
    val gender: String = "",
    val registryConfirmState: String = ""
)

@Immutable
@Serializable
data class FamilyRelationshipPR(
    val id: Int? = null,
    val relationCode: String? = null,
    val relationDesc: String? = null,
    val bailCode: String? = null
)

@Immutable
@Serializable
data class InquiryEducationCodePR(
    val universityOrSchoolName: String = ""
)

@Immutable
@Serializable
data class UploadImagePR(
    val guid: String = ""
)

@Immutable
@Serializable
data class GeneralResultPR(
    val isSuccess: Boolean = false,
    val message: String = "",
    val code: Int = 0
)

@Immutable
@Serializable
data class RequestAddDependentPR(
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
    val requestFileList: List<RequestFilePR>? = null
)

@Immutable
@Serializable
data class RequestFilePR(
    val documentFileId: String? = null,
    val documentType: String? = null,
    val id: String? = null,
    val personal: String? = null
)
