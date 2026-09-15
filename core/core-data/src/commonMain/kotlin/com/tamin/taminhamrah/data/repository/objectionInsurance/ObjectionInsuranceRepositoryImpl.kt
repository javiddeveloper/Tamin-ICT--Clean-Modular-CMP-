package com.tamin.taminhamrah.data.repository.objectionInsurance

import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.objectionInsurance.ObjectionInsuranceRemoteDataSource
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val CONFLICT_HISTORIES_PAGE_LIMIT = 60

internal class ObjectionInsuranceRepositoryImpl(
    private val objectionInsuranceRemoteDataSource: ObjectionInsuranceRemoteDataSource,
) : ObjectionInsuranceRepository {

    override fun checkStatusConflict(): Flow<Boolean> = flow {
        emit(objectionInsuranceRemoteDataSource.checkStatusConflict())
    }

    override fun getConflictHistories(): Flow<List<ObjectionInsuranceHistoryDN>> = flow {
        val response = objectionInsuranceRemoteDataSource.getConflictHistories(
            ApiQueryParamDN(limit = CONFLICT_HISTORIES_PAGE_LIMIT)
        )
        emit(response.list.orEmpty().map { it.toDomain() })
    }

    override fun saveConflict(items: List<ObjectionInsuranceHistoryDN>): Flow<String?> = flow {
        emit(objectionInsuranceRemoteDataSource.saveConflict(items.map { it.toDTO() }))
    }

    override fun confirmConflict(description: String?): Flow<Boolean> = flow {
        emit(objectionInsuranceRemoteDataSource.confirmConflict(description))
    }

    override fun finalConfirmConflict(): Flow<String> = flow {
        emit(objectionInsuranceRemoteDataSource.finalConfirmConflict())
    }
}
