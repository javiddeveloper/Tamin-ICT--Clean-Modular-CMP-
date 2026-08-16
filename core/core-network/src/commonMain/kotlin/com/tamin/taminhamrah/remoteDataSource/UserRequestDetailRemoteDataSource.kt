package com.tamin.taminhamrah.remoteDataSource

import com.tamin.taminhamrah.model.request.*
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.*
import kotlinx.serialization.json.JsonElement

interface UserRequestDetailRemoteDataSource {

    @GET("shortterm-request/getProcessData/{referenceId}")
    suspend fun getShortTermRequestStatus(
        @Path("referenceId") referenceId: String
    ): BaseDTO<ShortTermStatusDto>

    @GET("shortterm-request/getShorttermRequestLoadData/{referenceId}")
    suspend fun getShortTermRequestLoadData(
        @Path("referenceId") referenceId: String
    ): BaseDTO<ShortTermRequestInfoDto>

    @GET("StpBaseinfo/ShorttermBarTypes")
    suspend fun getPregnancyStatus(): BaseDTO<PregnancyStatusDto>

    @GET("StpBaseinfo/ShorttermBarChild")
    suspend fun getPregnancyTypes(): BaseDTO<PregnancyTypeDto>

    @GET("debit-objection/objection-request/{objectionNumber}")
    suspend fun getArticle16RequestInfo(
        @Path("objectionNumber") objectionNumber: Long
    ): BaseDTO<Article16RequestInfoDto>

    @GET("wage-assignment/request/{requestId}")
    suspend fun getDeferredInstallmentInfo(
        @Path("requestId") requestId: String
    ): BaseDTO<DeferredInstallmentInfoDto>

    @GET("historyprotest-services/getprotestresult/{referenceId}")
    suspend fun getFollowUpObjectionHistory(
        @Path("referenceId") referenceId: String
    ): BaseDTO<ResultFollowUpObjectionDto>

    @GET("upload-image/{guid}/0/0")
    suspend fun downloadDocument(
        @Path("guid") guid: String
    ): BaseDTO<DownloadFileDto>

    @GET("assets/data/objection-type.json")
    suspend fun getObjectionType(): BaseDTO<ObjectionTypeDto>

}
