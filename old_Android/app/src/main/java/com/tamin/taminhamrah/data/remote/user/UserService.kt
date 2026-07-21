package com.tamin.taminhamrah.data.remote.user

import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.BaseListResponse
import com.tamin.taminhamrah.data.remote.models.BaseResponse
import com.tamin.taminhamrah.data.remote.models.profile.ProfileResponse
import com.tamin.taminhamrah.data.remote.models.profile.RelatedPerson
import com.tamin.taminhamrah.data.remote.models.profile.TaminRelationResponse
import com.tamin.taminhamrah.data.remote.models.refreshtoken.RefreshTokenResponse
import com.tamin.taminhamrah.data.remote.models.refreshtoken.RevokeResponse
import com.tamin.taminhamrah.data.remote.models.responses.DeleteItemResponse
import com.tamin.taminhamrah.data.remote.models.responses.InquiryLicenseResponse
import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.data.remote.models.services.GeneralRes
import com.tamin.taminhamrah.data.remote.models.services.GeneralStringRes
import com.tamin.taminhamrah.data.remote.models.services.PostLogResponse
import com.tamin.taminhamrah.data.remote.models.user.DeviceDetailModel
import com.tamin.taminhamrah.data.remote.models.user.EditMobileResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxItem
import com.tamin.taminhamrah.data.remote.models.user.InboxPdfItem
import com.tamin.taminhamrah.data.remote.models.user.InboxResponseNew
import com.tamin.taminhamrah.data.remote.models.user.InboxSizeResponse
import com.tamin.taminhamrah.data.remote.models.user.InboxType
import com.tamin.taminhamrah.data.remote.models.user.LackEntitlementResponse
import com.tamin.taminhamrah.data.remote.models.user.LoginResponse
import com.tamin.taminhamrah.data.remote.models.user.MyRequestListResponse
import com.tamin.taminhamrah.data.remote.models.user.RequestErrorResponse
import com.tamin.taminhamrah.data.remote.models.user.RequestTypeResponse
import com.tamin.taminhamrah.data.remote.models.user.SmartGuideResponse
import com.tamin.taminhamrah.data.remote.models.user.VerifyMobileReq
import com.tamin.taminhamrah.data.repository.LoginRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface UserService {
    //    @GET("/api/medical-support/v2.0/{nationalCode}")
//    suspend fun getEligibility(@Path("nationalCode") nationalCode : String ): Response<Any>
    @GET
    suspend fun getEligibility(@Url eligibilityRequest: String): Response<GeneralRes?>?


    /*    https://account.tamin.ir/auth/signout?
        // redirect_uri=https://eservices.tamin.ir/view/index.html?redirect_uri=https://eservices.tamin.ir/auth/access&
        // response_type=assertion&
        // client_id=1e3923181a0818041c1d192c3805040f*/

    @GET
    suspend fun signOut(
        @Header(Constants.AUTHENTICATION) token: String,
        @Url url: String = "https://account.tamin.ir/auth/signout",
        @Query("redirect_uri") redirectUrl: String = "redirect_uri=https://eservices.tamin.ir/view/index.html?redirect_uri=https://eservices.tamin.ir/auth/access",
        @Query("response_type") response_type: String = "assertion",
        @Query("client_id") clientId: String = Constants.CLIENT_ID
    ): Response<GeneralRes?>?


    @FormUrlEncoded
    @POST
    suspend fun signIn(
        @Url url: String,
//        @Body req:LoginNewReq
        @Field("redirect_uri") redirectUrl: String = "mytamin://login",
        @Field("client_id") clientId: String = Constants.CLIENT_ID,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") codeFromServer: String = "",
        @Field("code_verifier") codeVerifier: String = "",
        @Field("audience") audience: String = "https://es.tamin.ir,https://eservices.tamin.ir,https://profile-api.tamin.ir",
    ): Response<LoginResponse?>?



    @GET
    suspend fun testMenu(
        @Url url: String
    ): ResponseBody


    @GET("users/current-user")
    suspend fun getProfileInfo(
        @Header(Constants.AUTHENTICATION) token: String,
    ): Response<ProfileResponse?>


    @POST("login-mobile")
    suspend fun postDeviceDataModel(
        @Header(Constants.AUTHENTICATION) token: String,
        @Body jsonObject: DeviceDetailModel
    ): Response<PostLogResponse?>?


    @GET("version-mobile")
    suspend fun checkUpdate(@Header(Constants.AUTHENTICATION) token: String): Response<CheckUpdateResponse?>?


    //    @GET("sms-ticket/request-ticket")
    @GET
    suspend fun changeMobile(
        @Header(Constants.AUTHENTICATION) token: String,
        @Header("Referer") referer: String,
        @Url url: String,
        @Query("mobile") mobile: String
    ): Response<EditMobileResponse?>?

    @GET("personals/relation")
    suspend fun fetchTaminRelation(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<TaminRelationResponse?>?

    @GET("personals/subdominant")
    suspend fun getRelatedPersons(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<RelatedPerson?>?

    @GET("personals/image-v2/{branchCode}")
    suspend fun sendImageRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("branchCode") branchCode: String,
        @Query("filter") filter: String
    ): Response<GeneralRes?>?

    @POST
    suspend fun verifyChangeMobileCode(
        @Header(Constants.AUTHENTICATION) token: String,
        @Header("Referer") referer: String,
        @Url url:String,
        @Body loginRequest: VerifyMobileReq,
    ): Response<GeneralRes?>?

    @GET("requests")
    suspend fun getMyRequestList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<MyRequestListResponse?>?

    //    https://eservices.tamin.ir/api/faq/limitation?requestType=3&requestStatus=0018&isPublic=1
    @GET("faq/limitation")
    suspend fun getSmartGuideList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("requestType") requestType: Int?,
        @Query("requestStatus") requestStatus: String?,
        @Query("isPublic") isPublic: Int?,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<SmartGuideResponse?>?

    @GET("request-type")
    suspend fun getRequestTypeList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = Constants.QUERY_PAGE_SIZE_10.toString(),
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<RequestTypeResponse?>?


    @GET("request-error")
    suspend fun getMyRequestErrorList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String,
    ): Response<RequestErrorResponse?>?


    @GET("announcement/to-user")
    suspend fun getInbox(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<BaseResponse<BaseListResponse<InboxItem>?>?>?


    @GET("announcement/to-user")
    suspend fun getInboxNew(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String,
        @Query("start") start: String,
        @Query("limit") limit: String,
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): Response<InboxResponseNew?>?


    @GET("announcement/size-personal-box")
    suspend fun getInboxSize(
        @Header(Constants.AUTHENTICATION) token: String
    ): Response<InboxSizeResponse?>?


    @GET("announcement/to-user/{requestId}/pdf")
    suspend fun getMyRequestPDF(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: String
    ): Response<InboxPdfItem?>?

    @DELETE("announcement/to-user/{requestId}")
    suspend fun deleteMyRequest(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: String,
    ): Response<DeleteItemResponse?>?

    @PUT("announcement/to-user/{requestId}")
    suspend fun inboxInquiryLicense(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("requestId") requestId: String,
        @Body body: LoginRepository.InboxReqOperation
    ): Response<InquiryLicenseResponse?>?

    @GET("announcement/type")
    suspend fun getInboxSystemList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "100",
        @Query("filter") filter: String = "[]",
    ): Response<BaseResponse<BaseListResponse<InboxType>?>?>?

    @GET("announcement/sub-type")
    suspend fun getInboxSubjectList(
        @Header(Constants.AUTHENTICATION) token: String,
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "100",
        @Query("filter") filter: String = "[]",
    ): Response<BaseResponse<BaseListResponse<InboxType>?>?>?

    @GET("booklet-req/profile-image")
    suspend fun getUserProfileImage(@Header(Constants.AUTHENTICATION) token: String): Response<GeneralStringRes?>?

    @FormUrlEncoded
    @POST
    fun refreshToken(
        @Url url: String = "${Constants.BASE_URL_ACCOUNT}server/v2/token",
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String = Constants.CLIENT_ID,
        @Field("audience") audience: String = "https://es.tamin.ir,https://eservices.tamin.ir,https://profile-api.tamin.ir",
        ): Call<RefreshTokenResponse?>

    @FormUrlEncoded
    @POST
    suspend fun revokeToken(
        @Url url: String = "${Constants.BASE_URL_ACCOUNT}revoke",
        @Header("Authorization") accessToken: String?,
        @Field("refresh_token") refreshToken: String?
    ): RevokeResponse

    @GET("booklet-req/lackEntitlement/{nationalCode}")
    suspend fun getLackEntitlement(
        @Header(Constants.AUTHENTICATION) token: String,
        @Path("nationalCode") nationalCode: String,
    ): Response<LackEntitlementResponse?>?

}