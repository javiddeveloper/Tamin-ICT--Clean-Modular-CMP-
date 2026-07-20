/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.userSource

import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDTO
import com.tamin.taminhamrah.model.bankAccount.BankAccountDTO
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDTO
import com.tamin.taminhamrah.model.subDominant.SubDominantResponseDTO
import com.tamin.taminhamrah.model.subDominant.insuredActiveBranch.InsuredActiveBranchDTO
import com.tamin.taminhamrah.model.user.EditMobileResponseDto
import com.tamin.taminhamrah.model.user.TaminRelationDTO
import com.tamin.taminhamrah.model.user.VerifyMobileRequest
import com.tamin.taminhamrah.model.utils.ListData

import com.tamin.taminhamrah.model.user.UserProfileDto

interface UserRemoteDataSource {
    suspend fun getIdentityInfo(): IdentityInfoDto
    suspend fun getUserProfileImage(): String
    suspend fun fetchTaminRelation(): TaminRelationDTO
    suspend fun sendImageRequest(branchCode: String, filter: List<ApiFilterDN>): String
    suspend fun changeMobile(mobile: String): EditMobileResponseDto
    suspend fun verifyChangeMobileCode(request: VerifyMobileRequest): String
    suspend fun checkUserIsNew(nationalId: String): Boolean

    suspend fun getSubDominantsInfo(
        query: ApiQueryParamDN
    ): SubDominantResponseDTO

    suspend fun getBankAccountList(
        query: ApiQueryParamDN
    ): ListData<BankAccountDTO>?

    suspend fun getInsuredActiveBranch() : List<InsuredActiveBranchDTO>?

    suspend fun getRelationTaminAll(
        query: ApiQueryParamDN
    ): ListData<ActiveRelationDTO>?

    suspend fun getElectronicFile(
        query: ApiQueryParamDN
    ): ListData<ElectronicFileDTO>?

    suspend fun getUserProfile(): UserProfileDto?
}
