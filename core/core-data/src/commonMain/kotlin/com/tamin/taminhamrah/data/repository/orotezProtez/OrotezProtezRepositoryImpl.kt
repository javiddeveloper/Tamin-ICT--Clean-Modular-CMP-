package com.tamin.taminhamrah.data.repository.orotezProtez

import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.orotezProtez.OrotezProtezRemoteDataSource
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisRequestDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.orotezProtez.OrotezProtezRepository
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrotezProtezRepositoryImpl(
    private val orotezProtezRemoteDataSource: OrotezProtezRemoteDataSource,
    private val apiQueryBuilder: ApiQueryBuilder,
) : OrotezProtezRepository {

    override fun getRequestInsuredMainInfo(): Flow<RequestInsuredMainInfoDN?> = flow {
        emit(orotezProtezRemoteDataSource.getRequestInsuredMainInfo()?.toDomain())
    }

    override fun getInsuredPersons(query: ApiQueryParamDN?): Flow<List<InsuredPersonDN>> = flow {
        val effectiveQuery = query ?: apiQueryBuilder.defaultQuery()
        val response = orotezProtezRemoteDataSource.getInsuredPersons(effectiveQuery)
        emit(response?.list?.map { it.toDomain() } ?: emptyList())
    }

    override fun saveShortTermOrthosis(request: SaveShortTermOrthosisRequestDN): Flow<String?> = flow {
        emit(orotezProtezRemoteDataSource.saveShortTermOrthosis(request.toDTO())?.shorttermRequest?.resultMessage)
    }
}
