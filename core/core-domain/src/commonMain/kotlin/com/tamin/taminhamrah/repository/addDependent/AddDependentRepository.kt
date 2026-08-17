package com.tamin.taminhamrah.repository.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.DependentInfoDN
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow

interface AddDependentRepository {
    fun getDependentInfo(): Flow<List<DependentInfoDN>>
    fun getActiveBranches(): Flow<List<BranchDN>>
    fun getFamilyRelationships(
        filter: List<ApiFilterDN> = emptyList()
    ): Flow<List<FamilyRelationshipDN>>
    fun getFamilyRelationshipsFromProxy(
        filter: List<ApiFilterDN> = emptyList()
    ): Flow<List<FamilyRelationshipDN>>
    fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): Flow<RegistryDataDN>
    fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): Flow<String>
    fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Flow<UploadImageDN>
    fun addNewDependent(
        request: RequestAddDependentDN
    ): Flow<GeneralResultDN>
}
