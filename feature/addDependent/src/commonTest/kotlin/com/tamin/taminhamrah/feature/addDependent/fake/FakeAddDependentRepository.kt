package com.tamin.taminhamrah.feature.addDependent.fake

import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.DependentInfoDN
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeAddDependentRepository : AddDependentRepository {
    var activeBranchesResult: List<BranchDN> = listOf(BranchDN(branchCode = "0101", branchName = "شعبه یک"))
    var registryDataResult: RegistryDataDN = RegistryDataDN(age = 19, firstName = "علی", lastName = "محمدی")
    var educationCodeResult: String = "دانشگاه تهران"

    override fun getDependentInfo(): Flow<List<DependentInfoDN>> = flow { emit(emptyList()) }

    override fun getActiveBranches(): Flow<List<BranchDN>> = flow { emit(activeBranchesResult) }

    override fun getFamilyRelationships(filter: List<ApiFilterDN>): Flow<List<FamilyRelationshipDN>> =
        flow { emit(emptyList()) }

    override fun getFamilyRelationshipsFromProxy(filter: List<ApiFilterDN>): Flow<List<FamilyRelationshipDN>> =
        flow { emit(emptyList()) }

    override fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): Flow<RegistryDataDN> = flow { emit(registryDataResult) }

    override fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): Flow<String> = flow { emit(educationCodeResult) }

    override fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Flow<UploadImageDN> = flow { emit(UploadImageDN()) }

    override fun addNewDependent(request: RequestAddDependentDN): Flow<GeneralResultDN> =
        flow { emit(GeneralResultDN(isSuccess = true)) }

    override fun refreshDependents(): Flow<GeneralResultDN> = flowOf()
}
