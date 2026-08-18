package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.UserInfoDTO
import com.tamin.taminhamrah.model.history.HistoryJobInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement
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

    /**
     * Who the signed-in person is, as the sign-in service itself answers it.
     *
     * The payload is a bare list of relation codes — `["05", …]` for a مستمری‌بگیر — which is what
     * the previous app read before letting anyone onto «مجموع سوابق». It is deliberately this
     * endpoint and not `userinfos`: a retired person still carries an insurance number, so the
     * insurance number cannot tell the two apart.
     */
    @GET("login-services/logininfo")
    suspend fun getLoginInfo(): BaseDTO<ListData<String>>

    @GET("historyreport-services/sendinstitution")
    suspend fun sendToInstitution(
        @Query("type1") allHistorySelected: Boolean,
        @Query("type2") historyAndWageSelected: Boolean,
        @Query("type3") combineHistorySelected: Boolean
    ): BaseDTO<JsonElement?>

    /** «کلیه سوابق» as a PDF. */
    @Streaming
    @GET("historyreport-services/year")
    suspend fun downloadAllHistoryReport(): HttpStatement

    /** «سوابق و ریز دستمزدها» as a PDF. */
    @Streaming
    @GET("historyreport-services/dastmozd")
    suspend fun downloadWageHistoryReport(): HttpStatement

    /** «سوابق تلفیقی» as a PDF. */
    @Streaming
    @GET("historyreport-services/talfigh")
    suspend fun downloadCombinedHistoryReport(): HttpStatement
}
