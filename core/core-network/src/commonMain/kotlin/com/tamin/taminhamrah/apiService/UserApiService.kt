/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.auth.TokenResponseDto
import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDTO
import com.tamin.taminhamrah.model.bankAccount.BankAccountDTO
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDTO
import com.tamin.taminhamrah.model.subDominant.SubDominantResponseDTO
import com.tamin.taminhamrah.model.subDominant.insuredActiveBranch.InsuredActiveBranchDTO
import com.tamin.taminhamrah.model.user.EditMobileResponseDto
import com.tamin.taminhamrah.model.user.TaminRelationDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.user.VerifyMobileRequest
import com.tamin.taminhamrah.tools.BaseDTO
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
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Url

import com.tamin.taminhamrah.model.user.UserProfileDto

internal interface UserApiService {
    @GET("central-reg/personal")
    suspend fun getIdentityInfo(): BaseDTO<IdentityInfoDto>

    @GET("booklet-req/profile-image")
    suspend fun getUserProfileImage(): BaseDTO<String>

    @GET("personals/relation")
    suspend fun fetchTaminRelation(): BaseDTO<TaminRelationDTO>

    @GET("personals/image-v2/{branchCode}")
    suspend fun sendImageRequest(
        @Path("branchCode") branchCode: String,
        @Query("filter") filter: String
    ): BaseDTO<String>



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
//        @Header("Referer") referer: String,
        @Url url: String,
        @Query("mobile") mobile: String
    ): BaseDTO<EditMobileResponseDto>

    @POST
    suspend fun verifyChangeMobileCode(
//        @Header("Referer") referer: String,
        @Url url:String,
        @Body loginRequest: VerifyMobileRequest,
    ): BaseDTO<String>


    @GET("personals/subdominant")
    suspend fun getSubDominantsInfo(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<SubDominantResponseDTO>

    @GET("personals/accounts")
    suspend fun getBankAccountList(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<BankAccountDTO>>


    @GET("subdominants/getInsuredActiveBranch")
    suspend fun getInsuredActiveBranch(): BaseDTO<List<InsuredActiveBranchDTO>>

    @GET("relation-tamins/all")
    suspend fun getRelationTaminAll(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ActiveRelationDTO>>

    @GET("erecords/images")
    suspend fun getElectronicFile(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ElectronicFileDTO>>

    @GET("users/current-user")
    suspend fun getUserProfile(): BaseDTO<UserProfileDto>

    @GET("relation-tamins/isnew/{nationalId}")
    suspend fun checkUserIsNew(
        @Path("nationalId") nationalId: String
    ): BaseDTO<Boolean>
}
