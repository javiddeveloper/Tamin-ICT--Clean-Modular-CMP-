package com.tamin.taminhamrah.dataSource.request

import com.tamin.taminhamrah.model.request.RequestDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface RequestRemoteDataSource {
    suspend fun getRequests(query: ApiQueryParamDN): ListData<RequestDTO>
}
