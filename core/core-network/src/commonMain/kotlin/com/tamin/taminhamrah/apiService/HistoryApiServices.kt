package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface HistoryApiServices {

    @GET("history-services/talfighinfos")
    suspend fun getTalfighInfos(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<TalfighInfoDTO>

}
