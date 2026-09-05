package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.UserInfoDTO
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import kotlinx.serialization.json.JsonElement

interface HistoryApiServices {

    @GET("history-services/dastmozdinfos")
    suspend fun getDastmozdInfos(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<DastmozdInfoDTO>

    @GET("history-services/talfighinfos")
    suspend fun getTalfighInfos(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<TalfighInfoDTO>

    @GET("history-services/historyjobinfos")
    suspend fun getHistoryJobInfos(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<HistoryJobInfoDTO>
    @GET("history-services/userinfos")
    suspend fun getUserInfos(): BaseDTO<UserInfoDTO>

    @GET("historyreport-services/sendinstitution")
    suspend fun sendToInstitution(
        @Query("type1") allHistorySelected: Boolean,
        @Query("type2") historyAndWageSelected: Boolean,
        @Query("type3") combineHistorySelected: Boolean
    ): BaseDTO<JsonElement?>

}
