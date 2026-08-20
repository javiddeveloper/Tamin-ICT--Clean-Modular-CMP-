package com.tamin.taminhamrah.data.repository.calculateWagePension

import com.tamin.taminhamrah.data.mapper.calculateWagePension.toDomain
import com.tamin.taminhamrah.dataSource.calculateWagePension.CalculateWagePensionRemoteDataSource
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDN
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDN
import com.tamin.taminhamrah.repository.calculateWagePension.CalculateWagePensionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CalculateWagePensionRepositoryImpl(
    private val remoteDataSource: CalculateWagePensionRemoteDataSource
) : CalculateWagePensionRepository {

    override suspend fun getPersonalInfo(): Flow<MultipleWorkshopPersonalInfoDN> = flow {
        emit(remoteDataSource.getPersonalInfo().toDomain())
    }

    override suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): Flow<MultipleWorkshopResultDN> = flow {
        emit(remoteDataSource.isMultipleWorkshops(branchCode, insuranceNumber).toDomain())
    }

    override suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): Flow<MultipleWorkshopResultDN> = flow {
        emit(remoteDataSource.calculateMultipleWorkshops(branchCode, insuranceNumber).toDomain())
    }
}
