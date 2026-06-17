package com.tamin.taminhamrah.repository.request

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.RequestDN
import kotlinx.coroutines.flow.Flow

interface RequestRepository {
    fun getMyRequests(query: ApiQueryParamDN): Flow<List<RequestDN>>
}
