package com.tamin.taminhamrah.apiService.historyObjection

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface HistoryObjectionApiService {

    @GET("historyprotest-services/checkstatusnotexist")
    suspend fun checkStatusNotExist(): BaseDTO<Boolean>

    @GET("historyprotest-services/getnotexistrequests")
    suspend fun getNotExistRequests(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<NotExistRequestDTO>>
}
