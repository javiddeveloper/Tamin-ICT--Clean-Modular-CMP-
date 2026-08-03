package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.repository.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class GetActiveBranchesUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(): Flow<List<BranchDN>> = repository.getActiveBranches()
}

class GetFamilyRelationshipsUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        page: Int = 1,
        pageSize: Int = 10,
        queryJson: String? = null
    ): Flow<List<FamilyRelationshipDN>> = repository.getFamilyRelationships(page, pageSize, queryJson)
}

class InquiryRegistryUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): Flow<RegistryDataDN> = repository.inquiryRegistry(dependentNationalId, birthDateTimeStamp, dependencyCode)
}

class InquiryEducationCodeUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        nationalId: String,
        educationCode: String
    ): Flow<String> = repository.inquiryEducationCode(nationalId, educationCode)
}

class UploadDependentImageUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Flow<UploadImageDN> = repository.uploadImage(imageBytes, fileName, mimeType)
}

class AddNewDependentUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        request: RequestAddDependentDN
    ): Flow<GeneralResultDN> = repository.addNewDependent(request)
}
