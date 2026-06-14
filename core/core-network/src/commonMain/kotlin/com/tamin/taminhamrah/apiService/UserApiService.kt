/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.auth.TokenResponseDto
import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationResponse
import com.tamin.taminhamrah.model.bankAccount.BankAccountResponse
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileResponse
import com.tamin.taminhamrah.model.subDominant.SubDominantResponseData
import com.tamin.taminhamrah.model.subDominant.insuredActiveBranch.InsuredActiveBranchData
import com.tamin.taminhamrah.model.user.EditMobileResponseDto
import com.tamin.taminhamrah.model.user.TaminRelationResponse
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.user.VerifyMobileReq
import com.tamin.taminhamrah.tools.BaseResponse
import com.tamin.taminhamrah.util.HeaderConstant
import com.tamin.taminhamrah.util.NetworkConstants
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Url

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


    @GET
    suspend fun signOut(
        @Header(HeaderConstant.AUTHORIZATION) token: String,
        @Url url: String = "${NetworkConstants.BASE_URL_ACCOUNT}signout",
        @Query("redirect_uri") redirectUrl: String = "https://eservices.tamin.ir/view/index.html?redirect_uri=https://eservices.tamin.ir/auth/access",
        @Query("response_type") responseType: String = "assertion",
        @Query("client_id") clientId: String = NetworkConstants.CLIENT_ID
    )

    @FormUrlEncoded
    @POST
    suspend fun revokeToken(
        @Url url: String = "${NetworkConstants.BASE_URL_ACCOUNT}revoke",
        @Header(HeaderConstant.AUTHORIZATION) accessToken: String?,
        @Field("refresh_token") refreshToken: String?
    )

    @FormUrlEncoded
    @POST
    suspend fun refreshToken(
        @Url url: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String = NetworkConstants.CLIENT_ID,
        @Field("audience") audience: String = "https://es.tamin.ir,https://eservices.tamin.ir"
    ): TokenResponseDto

    @GET
    suspend fun changeMobile(
        @Url url: String,
        @Query("filter") filter: String
    ): BaseResponse<EditMobileResponseDto>

    @POST
    suspend fun verifyChangeMobileCode(
        @Url url: String,
        @Body loginRequest: VerifyMobileReq,
    ): BaseResponse<String>


    @GET("personals/subdominant")
    suspend fun getSubDominantsInfo(
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): BaseResponse<SubDominantResponseData>

    @GET("personals/accounts")
    suspend fun getBankAccountList(
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): BaseResponse<ListData<BankAccountResponse>>


    @GET("subdominants/getInsuredActiveBranch")
    suspend fun getInsuredActiveBranch(): BaseResponse<List<InsuredActiveBranchData>>

    @GET("relation-tamins/all")
    suspend fun getRelationTaminAll(
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): BaseResponse<ListData<ActiveRelationResponse>>

    @GET("erecords/images")
    suspend fun getElectronicFile(
        @Query("page") page: String = "1",
        @Query("start") start: String = "0",
        @Query("limit") limit: String = "10",
        @Query("filter") filter: String = "[]",
        @Query("sort") sort: String = "[]",
    ): BaseResponse<ListData<ElectronicFileResponse>>

}
