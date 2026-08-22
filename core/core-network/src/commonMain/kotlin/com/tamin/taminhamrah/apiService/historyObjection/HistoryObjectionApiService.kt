package com.tamin.taminhamrah.apiService.historyObjection

import com.tamin.taminhamrah.model.historyObjection.ConfirmNotExistItemDTO
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDTO
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
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

    @DELETE("historyprotest-services/deletenotexist/{requestNumber}/{rowIndex}")
    suspend fun deleteNotExist(
        @Path("requestNumber") requestNumber: String,
        @Path("rowIndex") rowIndex: String,
    ): BaseDTO<Boolean>

    @POST("historyprotest-services/confirmnotexist")
    suspend fun confirmNotExist(
        @Body items: List<ConfirmNotExistItemDTO>
    ): BaseDTO<Boolean>

    @POST("historyprotest-services/finalconfirmnotexist")
    suspend fun finalConfirmNotExist(
        @Body body: String = ""
    ): BaseDTO<String>
}
