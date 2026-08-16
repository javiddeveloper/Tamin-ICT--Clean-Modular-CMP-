package com.tamin.taminhamrah.repository.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import kotlinx.coroutines.flow.Flow

interface OrotezProtezRepository {
    fun getRequestInsuredMainInfo(): Flow<RequestInsuredMainInfoDN?>
}
