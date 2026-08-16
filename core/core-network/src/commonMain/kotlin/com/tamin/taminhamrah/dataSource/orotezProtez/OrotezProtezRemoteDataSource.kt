package com.tamin.taminhamrah.dataSource.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDTO

interface OrotezProtezRemoteDataSource {
    suspend fun getRequestInsuredMainInfo(): RequestInsuredMainInfoDTO?
}
