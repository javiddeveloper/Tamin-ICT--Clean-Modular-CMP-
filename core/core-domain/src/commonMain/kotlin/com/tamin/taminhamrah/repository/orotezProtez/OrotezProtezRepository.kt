package com.tamin.taminhamrah.repository.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface OrotezProtezRepository {
    fun getRequestInsuredMainInfo(): Flow<RequestInsuredMainInfoDN?>
    fun getInsuredPersons(query: ApiQueryParamDN?): Flow<List<InsuredPersonDN>>
}
