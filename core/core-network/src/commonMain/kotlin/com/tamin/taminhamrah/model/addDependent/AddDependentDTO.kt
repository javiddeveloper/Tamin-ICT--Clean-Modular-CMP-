package com.tamin.taminhamrah.model.addDependent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestAddDependentDTO(
    @SerialName("bailType") val bailType: BailTypeDTO? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("cityOfBirthId") val cityOfBirthId: String? = null,
    @SerialName("cityOfIssueId") val cityOfIssueId: String? = null,
    @SerialName("countryId") val countryId: String = "0001",
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
    @SerialName("dependency") val dependency: DependencyDTO? = null,
    @SerialName("dependentType") val dependentType: DependentTypeDTO? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nation") val nation: String = "01",
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("parentId") val parentId: ParentIdDTO? = ParentIdDTO(),
    @SerialName("requestFileList") val requestFileList: List<RequestFileDTO>? = null
)

@Serializable
data class RequestFileDTO(
    @SerialName("documentFile") val documentFile: DocumentFileDTO? = null,
    @SerialName("documentType") val documentType: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("personal") val personal: String? = null
)

@Serializable
data class DocumentFileDTO(
    @SerialName("id") val id: String? = null
)

@Serializable
data class BailTypeDTO(
    @SerialName("code") val code: String? = null
)

@Serializable
data class DependencyDTO(
    @SerialName("id") val id: Int? = null
)

@Serializable
data class DependentTypeDTO(
    @SerialName("code") val code: String? = null
)

@Serializable
data class ParentIdDTO(
    @SerialName("id") val id: String? = null
)

@Serializable
data class BranchDTO(
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
)


@Serializable
data class RegistryDataDTO(
    @SerialName("age") val age: Int? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("insuranceId") val registryConfirmState: String? = null,
)


@Serializable
data class FamilyRelationshipDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("dependencyCode") val relationCode: String? = null,
    @SerialName("dependencyDesc") val relationDesc: String? = null,
    @SerialName("reasonCode") val bailCode: String? = null
)

@Serializable
data class FamilyRelationshipProxyDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("dependencyCode") val relationCode: String? = null,
    @SerialName("dependencyDesc") val relationDesc: String? = null,
    @SerialName("reasonCode") val bailCode: String? = null
)


@Serializable
data class UploadImageResponseDTO(
    @SerialName("isSuccess") val isSuccess: Boolean = false,
    @SerialName("message") val message: String? = null,
    @SerialName("guid") val guid: String = ""
)

@Serializable
data class GeneralResponseDTO(
    @SerialName("isSuccess") val isSuccess: Boolean = false,
    @SerialName("message") val message: String? = null,
    @SerialName("code") val code: Int? = null
)

@Serializable
data class DependentInfoDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("relationshipDesc") val relationshipDesc: String? = null,
    @SerialName("isInsuranceActive") val isInsuranceActive: Boolean? = null,
    @SerialName("birthDatePersian") val birthDatePersian: String? = null
)
