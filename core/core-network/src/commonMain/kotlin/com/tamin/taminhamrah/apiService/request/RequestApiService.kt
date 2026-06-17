package com.tamin.taminhamrah.apiService.request

import com.tamin.taminhamrah.model.request.RequestDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface RequestApiService {

    @GET("requests")
    suspend fun getRequests(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<RequestDTO>>
}
