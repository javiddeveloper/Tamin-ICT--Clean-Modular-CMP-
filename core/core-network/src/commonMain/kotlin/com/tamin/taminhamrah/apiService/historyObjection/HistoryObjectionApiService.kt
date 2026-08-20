package com.tamin.taminhamrah.apiService.historyObjection

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDTO
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.QueryMap

interface HistoryObjectionApiService {

    @GET("historyprotest-services/checkstatusnotexist")
    suspend fun checkStatusNotExist(): BaseDTO<Boolean>

    @GET("historyprotest-services/getnotexistrequests")
    suspend fun getNotExistRequests(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<NotExistRequestDTO>>

    @POST("historyprotest-services/savenotexist")
    suspend fun saveNotExist(
        @Body request: SaveNotExistRequestDTO
    ): BaseDTO<Boolean>
}
