package com.tamin.taminhamrah.apiService.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface UserRequestApiService {

    @GET("requests")
    suspend fun getUserRequests(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<UserRequestDTO>>

    @GET("request-type")
    suspend fun getRequestTypes(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<UserRequestTypeDTO>>
}
