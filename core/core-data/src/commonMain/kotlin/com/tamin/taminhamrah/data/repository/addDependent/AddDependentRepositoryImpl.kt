package com.tamin.taminhamrah.data.repository.addDependent

import com.tamin.taminhamrah.data.mapper.addDependent.toDomain
import com.tamin.taminhamrah.data.mapper.addDependent.toDto
import com.tamin.taminhamrah.dataSource.addDependent.AddDependentRemoteDataSource
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

class AddDependentRepositoryImpl(
    private val remoteDataSource: AddDependentRemoteDataSource
) : AddDependentRepository {

    override fun getDependentInfo(): Flow<List<DependentInfoDN>> = flow {
        val result = remoteDataSource.getDependentInfo().map { it.toDomain() }
        emit(result)
    }

    override fun getActiveBranches(): Flow<List<BranchDN>> = flow {
        val result = remoteDataSource.getActiveBranches().map { it.toDomain() }
        emit(result)
    }

    override fun getFamilyRelationships(
        filter: List<ApiFilterDN>
    ): Flow<List<FamilyRelationshipDN>> = flow {
        val result = remoteDataSource.getFamilyRelationships(filter).map { it.toDomain() }
        emit(result)
    }

    override fun getFamilyRelationshipsFromProxy(
        filter: List<ApiFilterDN>
    ): Flow<List<FamilyRelationshipDN>> = flow {
        val result = remoteDataSource.getFamilyRelationshipsFromProxy(filter).map { it.toDomain() }
        emit(result)
    }

    override fun inquiryRegistry(
        dependentNationalId: String,
        birthDateTimeStamp: String,
        dependencyCode: String
    ): Flow<RegistryDataDN> = flow {
        val result = remoteDataSource.inquiryRegistry(dependentNationalId, birthDateTimeStamp, dependencyCode).toDomain()
        emit(result)
    }

    override fun inquiryEducationCode(
        nationalId: String,
        educationCode: String
    ): Flow<String> = flow {
        val result = remoteDataSource.inquiryEducationCode(nationalId, educationCode)
        emit(result)
    }

    override fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        mimeType: String
    ): Flow<UploadImageDN> = flow {
        val result = remoteDataSource.uploadImage(imageBytes, fileName, mimeType).toDomain()
        emit(result)
    }

    override fun addNewDependent(request: RequestAddDependentDN): Flow<GeneralResultDN> = flow {
        val result = remoteDataSource.addNewDependent(request.toDto()).toDomain()
        emit(result)
    }
}
