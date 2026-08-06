package com.tamin.taminhamrah.dataSource.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDto
import com.tamin.taminhamrah.model.addDependent.DependentInfoDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDto
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDto
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDto
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDto
import com.tamin.taminhamrah.model.request.ApiFilterDN

interface AddDependentRemoteDataSource {
    suspend fun getDependentInfo(): List<DependentInfoDto>
    suspend fun getActiveBranches(): List<BranchDto>
    suspend fun getFamilyRelationships(
        filter: List<ApiFilterDN> = emptyList()
    ): List<FamilyRelationshipDto>
    suspend fun getFamilyRelationshipsFromProxy(): List<FamilyRelationshipProxyDto>
    suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): RegistryDataDto
    suspend fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): String
    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): UploadImageResponseDto
    suspend fun addNewDependent(
        request: RequestAddDependentDto
    ): GeneralResponseDto
}
