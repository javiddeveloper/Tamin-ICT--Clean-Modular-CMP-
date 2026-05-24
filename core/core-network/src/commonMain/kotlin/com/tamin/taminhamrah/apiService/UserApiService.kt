/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.core.network.model.auth.TokenResponseDto
import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.tools.BaseResponse
import com.tamin.taminhamrah.utils.NetworkConstants
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Url

internal interface UserApiService {
    @GET("central-reg/personal")
    suspend fun getIdentityInfo(): BaseResponse<IdentityInfoDto>

    @GET("booklet-req/profile-image")
    suspend fun getUserProfileImage(): BaseResponse<String>

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
    ): BaseResponse<TokenResponseDto>

    @FormUrlEncoded
    @POST
    suspend fun refreshToken(
        @Url url: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String = NetworkConstants.CLIENT_ID,
    ): BaseResponse<TokenResponseDto>

}
