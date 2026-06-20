package com.tamin.taminhamrah.dataSource.request

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.UserRequestDTO
import com.tamin.taminhamrah.model.request.UserRequestTypeDTO
import com.tamin.taminhamrah.model.utils.ListData

interface UserRequestRemoteDataSource {
    suspend fun getUserRequests(query: ApiQueryParamDN): ListData<UserRequestDTO>

    suspend fun getRequestTypes(query: ApiQueryParamDN): ListData<UserRequestTypeDTO>
}
