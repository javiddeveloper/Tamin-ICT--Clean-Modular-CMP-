package com.tamin.taminhamrah.data.repository.common

import com.tamin.taminhamrah.dataSource.commonSource.CommonRemoteDataSource
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.ktor.client.statement.HttpStatement

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

    override suspend fun getRegistrationDeclarationForm(): HttpStatement {
        return commonRemoteDataSource.getRegistrationDeclarationForm()
    }

    override suspend fun getJobTitle(query: ApiQueryParamDN): JobTitleListDN? {
        val response = commonRemoteDataSource.getJobTitle(query)
        return response?.let {
            JobTitleListDN(list = it.list?.map { item -> item.toDomain() }, total = it.total)
        }
    }
}
