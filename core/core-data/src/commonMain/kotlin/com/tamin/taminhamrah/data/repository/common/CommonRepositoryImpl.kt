package com.tamin.taminhamrah.data.repository.common

import com.tamin.core.network.datasource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CommonRepositoryImpl(
    private val commonRemoteDataSource: CommonRemoteDataSource
) : CommonRepository {
    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = flow {
        try {
            val response = commonRemoteDataSource.getBeneficiary(ApiQueryParamDN(filters = filters))
            emit(response.list?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            throw e
        }
    }
}
