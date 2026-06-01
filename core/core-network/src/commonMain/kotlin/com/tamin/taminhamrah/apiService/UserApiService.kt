/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.auth.TokenResponseDto
import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.model.dependent.SubdominantResponse
import com.tamin.taminhamrah.model.user.TaminRelationResponse
import com.tamin.taminhamrah.tools.BaseResponse
import com.tamin.taminhamrah.util.NetworkConstants
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.http.cio.Response

internal interface UserApiService {
    @GET("central-reg/personal")
    suspend fun getIdentityInfo(): BaseResponse<IdentityInfoDto>

    @GET("booklet-req/profile-image")
    suspend fun getUserProfileImage(): BaseResponse<String>

    @GET("personals/relation")
    suspend fun fetchTaminRelation(): BaseResponse<TaminRelationResponse>

    @GET("personals/image-v2/{branchCode}")
    suspend fun sendImageRequest(
        @Path("branchCode") branchCode: String,
        @Query("filter") filter: String
    ): BaseResponse<String>

    @FormUrlEncoded
    @POST
    suspend fun signIn(
        @Url url: String,
        @Field("redirect_uri") redirectUrl: String = "mytamin://login",
        @Field("client_id") clientId: String = NetworkConstants.CLIENT_ID,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") codeFromServer: String = "",
        @Field("code_verifier") codeVerifier: String = "",
        @Field("audience") audience: String = "https://es.tamin.ir,https://eservices.tamin.ir",
    ): TokenResponseDto

    @FormUrlEncoded
    @POST
    suspend fun refreshToken(
        @Url url: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String = NetworkConstants.CLIENT_ID,
    ): TokenResponseDto


    @GET("personals/subdominant")
    suspend fun getSubDominantsInfo(
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ) : BaseResponse<SubdominantResponse>

}
