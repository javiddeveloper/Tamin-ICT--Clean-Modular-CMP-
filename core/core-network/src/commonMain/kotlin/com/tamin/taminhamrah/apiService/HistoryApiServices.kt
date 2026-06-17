package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.http.cio.Response

interface HistoryApiServices {

    @GET("history-services/dastmozdinfos")
    suspend fun getDastmozdInfos(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<DastmozdInfoDTO>

    @GET("history-services/talfighinfos")
    suspend fun getTalfighInfos(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<TalfighInfoDTO>

}
