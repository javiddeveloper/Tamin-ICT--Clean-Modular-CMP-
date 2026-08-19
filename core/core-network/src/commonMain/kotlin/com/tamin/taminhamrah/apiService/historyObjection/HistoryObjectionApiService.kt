package com.tamin.taminhamrah.apiService.historyObjection

import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET

interface HistoryObjectionApiService {

    @GET("historyprotest-services/checkstatusnotexist")
    suspend fun checkStatusNotExist(): BaseDTO<Boolean>
}
