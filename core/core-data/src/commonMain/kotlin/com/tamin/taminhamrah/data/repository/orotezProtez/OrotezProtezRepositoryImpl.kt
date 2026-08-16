package com.tamin.taminhamrah.data.repository.orotezProtez

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.orotezProtez.OrotezProtezRemoteDataSource
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.repository.orotezProtez.OrotezProtezRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrotezProtezRepositoryImpl(
    private val orotezProtezRemoteDataSource: OrotezProtezRemoteDataSource
) : OrotezProtezRepository {

    override fun getRequestInsuredMainInfo(): Flow<RequestInsuredMainInfoDN?> = flow {
        emit(orotezProtezRemoteDataSource.getRequestInsuredMainInfo()?.toDomain())
    }
}
