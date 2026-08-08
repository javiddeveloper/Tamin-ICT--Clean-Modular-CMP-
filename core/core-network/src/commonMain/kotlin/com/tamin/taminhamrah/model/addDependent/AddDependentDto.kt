package com.tamin.taminhamrah.model.addDependent

import com.tamin.taminhamrah.tools.ErrorCarrier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestAddDependentDto(
    @SerialName("bailType") val bailType: BailTypeDto? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("cityOfBirthId") val cityOfBirthId: String? = null,
    @SerialName("cityOfIssueId") val cityOfIssueId: String? = null,
    @SerialName("countryId") val countryId: String = "0001",
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
    @SerialName("dependency") val dependency: DependencyDto? = null,
    @SerialName("dependentType") val dependentType: DependentTypeDto? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nation") val nation: String = "01",
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("parentId") val parentId: ParentIdDto? = ParentIdDto(),
    @SerialName("requestFileList") val requestFileList: List<RequestFileDto>? = null
)

@Serializable
data class RequestFileDto(
    @SerialName("documentFile") val documentFile: DocumentFileDto? = null,
    @SerialName("documentType") val documentType: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("personal") val personal: String? = null
)

@Serializable
data class DocumentFileDto(
    @SerialName("id") val id: String? = null
)

@Serializable
data class BailTypeDto(
    @SerialName("code") val code: String? = null
)

@Serializable
data class DependencyDto(
    @SerialName("id") val id: Int? = null
)

@Serializable
data class DependentTypeDto(
    @SerialName("code") val code: String? = null
)

@Serializable
data class ParentIdDto(
    @SerialName("id") val id: String? = null
)

@Serializable
data class BranchDto(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
)

@Serializable
data class BranchListResponseDto(
    @SerialName("isSuccess") val isSuccess: Boolean = false,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: List<BranchDto>? = null
)

@Serializable
data class RegistryDataDto(
    @SerialName("age") val age: Int? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("insuranceId") val registryConfirmState: String? = null,
    @SerialName("message") override val message: String? = null,
    @SerialName("cause") override val cause: String? = null,
) : ErrorCarrier

@Serializable
data class InquiryRegistryResponseDto(
    @SerialName("isSuccess") val isSuccess: Boolean = false,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: RegistryDataDto = RegistryDataDto()
)

@Serializable
data class FamilyRelationshipDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("dependencyCode") val relationCode: String? = null,
    @SerialName("dependencyDesc") val relationDesc: String? = null,
    @SerialName("reasonCode") val bailCode: String? = null
)

@Serializable
data class FamilyRelationshipProxyDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("dependencyCode") val relationCode: String? = null,
    @SerialName("dependencyDesc") val relationDesc: String? = null,
    @SerialName("reasonCode") val bailCode: String? = null
)

@Serializable
data class FamilyRelationshipResponseDto(
    @SerialName("isSuccess") val isSuccess: Boolean = false,
    @SerialName("data") val data: List<FamilyRelationshipDto>? = null,
    @SerialName("totalCount") val totalCount: Int = 0
)

@Serializable
data class InquiryEducationCodeResponseDto(
    @SerialName("isSuccess") val isSuccess: Boolean = false,
    @SerialName("message") val message: String? = null,
    @SerialName("data") val data: String? = null
)

@Serializable
data class UploadImageResponseDto(
    @SerialName("isSuccess") val isSuccess: Boolean = false,
    @SerialName("message") val message: String? = null,
    @SerialName("guid") val guid: String = ""
)

@Serializable
data class GeneralResponseDto(
    @SerialName("isSuccess") val isSuccess: Boolean = false,
    @SerialName("message") override val message: String? = null,
    @SerialName("code") val code: Int? = null
) : ErrorCarrier

@Serializable
data class DependentInfoDto(
    @SerialName("id") val id: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("relationshipDesc") val relationshipDesc: String? = null,
    @SerialName("isInsuranceActive") val isInsuranceActive: Boolean? = null,
    @SerialName("birthDatePersian") val birthDatePersian: String? = null
)
