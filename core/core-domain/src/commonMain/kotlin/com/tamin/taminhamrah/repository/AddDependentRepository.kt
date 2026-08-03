package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import kotlinx.coroutines.flow.Flow

interface AddDependentRepository {
    fun getActiveBranches(): Flow<List<BranchDN>>
    fun getFamilyRelationships(
        page: Int = 1,
        pageSize: Int = 10,
        queryJson: String? = null
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
