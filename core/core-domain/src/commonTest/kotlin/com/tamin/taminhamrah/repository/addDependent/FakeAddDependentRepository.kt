package com.tamin.taminhamrah.repository.addDependent

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

class FakeAddDependentRepository : AddDependentRepository {

    var activeBranchesResult: List<BranchDN> = emptyList()
    var familyRelationshipsResult: List<FamilyRelationshipDN> = emptyList()
    var familyRelationshipsFromProxyResult: List<FamilyRelationshipDN> = emptyList()
    var dependentInfoResult: List<DependentInfoDN> = emptyList()
    var registryDataResult: RegistryDataDN = RegistryDataDN()
    var educationCodeResult: String = ""
    var uploadImageResult: UploadImageDN = UploadImageDN()
    var addNewDependentResult: GeneralResultDN = GeneralResultDN()

    var shouldThrowError: Boolean = false
    var error: Throwable = RuntimeException("Fake error")

    var lastInquiryNationalId: String? = null
    var lastInquiryEducationCode: String? = null
    var lastUploadedFileName: String? = null
    var lastAddedRequest: RequestAddDependentDN? = null
    var lastFamilyRelationshipsFilter: List<ApiFilterDN>? = null
    var lastFamilyRelationshipsFromProxyFilter: List<ApiFilterDN>? = null

    override fun getDependentInfo(): Flow<List<DependentInfoDN>> = flow {
        if (shouldThrowError) throw error
        emit(dependentInfoResult)
    }

    override fun getActiveBranches(): Flow<List<BranchDN>> = flow {
        if (shouldThrowError) throw error
        emit(activeBranchesResult)
    }

    override fun getFamilyRelationships(filter: List<ApiFilterDN>): Flow<List<FamilyRelationshipDN>> = flow {
        if (shouldThrowError) throw error
        lastFamilyRelationshipsFilter = filter
        emit(familyRelationshipsResult)
    }

    override fun getFamilyRelationshipsFromProxy(filter: List<ApiFilterDN>): Flow<List<FamilyRelationshipDN>> = flow {
        if (shouldThrowError) throw error
        lastFamilyRelationshipsFromProxyFilter = filter
        emit(familyRelationshipsFromProxyResult)
    }

    override fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): Flow<RegistryDataDN> = flow {
        if (shouldThrowError) throw error
        lastInquiryNationalId = dependentNationalId
        emit(registryDataResult)
    }

    override fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): Flow<String> = flow {
        if (shouldThrowError) throw error
        lastInquiryNationalId = nationalId
        lastInquiryEducationCode = educationCode
        emit(educationCodeResult)
    }

    override fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Flow<UploadImageDN> = flow {
        if (shouldThrowError) throw error
        lastUploadedFileName = fileName
        emit(uploadImageResult)
    }

    override fun addNewDependent(request: RequestAddDependentDN): Flow<GeneralResultDN> = flow {
        if (shouldThrowError) throw error
        lastAddedRequest = request
        emit(addNewDependentResult)
    }
}
