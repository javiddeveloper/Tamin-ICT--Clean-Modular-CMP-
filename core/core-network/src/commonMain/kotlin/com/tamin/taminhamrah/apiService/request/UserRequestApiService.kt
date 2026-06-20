package com.tamin.taminhamrah.apiService.request

import com.tamin.taminhamrah.model.request.UserRequestDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface UserRequestApiService {

    @GET("requests")
    suspend fun getUserRequests(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<UserRequestDTO>>
}
