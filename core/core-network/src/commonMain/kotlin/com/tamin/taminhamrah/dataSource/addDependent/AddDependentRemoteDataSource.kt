package com.tamin.taminhamrah.dataSource.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDTO
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDTO
import com.tamin.taminhamrah.model.addDependent.RegistryDataDTO
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDTO
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDTO
import com.tamin.taminhamrah.model.addDependent.DependentInfoDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN

interface AddDependentRemoteDataSource {
    suspend fun getDependentInfo(): List<DependentInfoDTO>
    suspend fun getActiveBranches(): List<BranchDTO>
    suspend fun getFamilyRelationships(
        filter: List<ApiFilterDN> = emptyList()
    ): List<FamilyRelationshipDTO>
    suspend fun getFamilyRelationshipsFromProxy(
        filter: List<ApiFilterDN> = emptyList()
    ): List<FamilyRelationshipProxyDTO>
    suspend fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): RegistryDataDTO
    suspend fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): String
    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): UploadImageResponseDTO
    suspend fun addNewDependent(
        request: RequestAddDependentDTO
    ): GeneralResponseDTO
}
