package com.tamin.taminhamrah.data.repository.treatment

import com.tamin.taminhamrah.dataSource.treatment.TreatmentRemoteDataSource
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.data.mapper.treatment.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class TreatmentRepositoryImpl(
    private val treatmentRemoteDataSource: TreatmentRemoteDataSource
) : TreatmentRepository {

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> = flow {
        val result = treatmentRemoteDataSource.getDeservedTreatment(nationalCode)
        emit(result?.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        filters: List<ApiFilterDN>
    ): Flow<List<DependantUserUnderEighteenDN>> = flow {
        val result = treatmentRemoteDataSource.getDependantUnderEighteen(
            nationalCode,
            ApiQueryParamDN(filters = filters)
        )
        emit(result?.list?.map { it.toDomain() } ?: emptyList())
    }
}
