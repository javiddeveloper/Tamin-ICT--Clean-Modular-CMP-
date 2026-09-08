package com.tamin.taminhamrah.apiService.userRequest

import com.tamin.taminhamrah.model.userRequest.ArticleSixteenRequestInfoDTO
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentInfoDTO
import com.tamin.taminhamrah.model.userRequest.FollowUpObjectionHistoryDTO
import com.tamin.taminhamrah.model.userRequest.PregnancyLookupDTO
import com.tamin.taminhamrah.model.userRequest.RequestErrorDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestInfoDTO
import com.tamin.taminhamrah.model.userRequest.ShortTermRequestStatusDTO
import com.tamin.taminhamrah.model.userRequest.SmartGuideDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestDTO
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import kotlinx.serialization.json.JsonElement
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

interface UserRequestApiService {

    @GET("requests")
    suspend fun getUserRequests(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<UserRequestDTO>>

    @GET("request-type")
    suspend fun getRequestTypes(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<UserRequestTypeDTO>>

    @GET("request-error")
    suspend fun getMyRequestErrorList(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<RequestErrorDTO>>

    @GET("faq/limitation")
    suspend fun getSmartGuideList(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<SmartGuideDTO>>

    @GET("requests/{id}")
    suspend fun getUserRequestDetail(
        @Path("id") id: Long
    ): BaseDTO<UserRequestDTO>

    @GET("shortterm-request/getProcessData/{referenceId}")
    suspend fun getShortTermRequestStatus(
        @Path("referenceId") referenceId: String
    ): BaseDTO<ListData<ShortTermRequestStatusDTO>>

    @GET("shortterm-request/getShorttermRequestLoadData/{referenceId}")
    suspend fun getShortTermRequestLoadData(
        @Path("referenceId") referenceId: String
    ): BaseDTO<ListData<ShortTermRequestInfoDTO>>

    @GET("StpBaseinfo/ShorttermBarTypes")
    suspend fun getPregnancyStatus(): BaseDTO<ListData<PregnancyLookupDTO>>

    @GET("StpBaseinfo/ShorttermBarChild")
    suspend fun getPregnancyTypes(): BaseDTO<ListData<PregnancyLookupDTO>>

    @GET("debit-objection/objection-request/{objectionNumber}")
    suspend fun getArticleSixteenRequestInfo(
        @Path("objectionNumber") objectionNumber: Long
    ): BaseDTO<ArticleSixteenRequestInfoDTO>

    @GET("wage-assignment/request/{requestId}")
    suspend fun getDeferredInstallmentInfo(
        @Path("requestId") requestId: String
    ): BaseDTO<DeferredInstallmentInfoDTO>

    @GET("historyprotest-services/getprotestresult/{referenceId}")
    suspend fun getFollowUpObjectionHistory(
        @Path("referenceId") referenceId: String
    ): BaseDTO<ListData<FollowUpObjectionHistoryDTO>>

    /**
     * A document's bytes, base64 in `data`.
     *
     * Typed `JsonElement` rather than `String` because `data` is **not** always a string: a failure
     * puts an object there (`{cause, message}`), and a `BaseDTO<String>` cannot deserialize that —
     * it throws `JsonConvertException` before `extractData` ever sees the envelope, so the caller
     * reports a transport failure and the service's own «… یافت نشد» is lost. A `JsonElement` holds
     * both shapes, which is what lets the real reason reach the screen.
     */
    @GET("upload-image/{guid}/0/0")
    suspend fun downloadDocument(
        @Path("guid") guid: String
    ): BaseDTO<JsonElement>
}

