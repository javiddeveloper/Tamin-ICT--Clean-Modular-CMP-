/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.userSource

import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationResponse
import com.tamin.taminhamrah.model.bankAccount.BankAccountResponse
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileResponse
import com.tamin.taminhamrah.model.subDominant.SubDominantResponseData
import com.tamin.taminhamrah.model.subDominant.insuredActiveBranch.InsuredActiveBranchData
import com.tamin.taminhamrah.model.user.EditMobileResponseDto
import com.tamin.taminhamrah.model.user.TaminRelationResponse
import com.tamin.taminhamrah.model.user.VerifyMobileReq
import com.tamin.taminhamrah.model.utils.ListData

interface UserRemoteDataSource {
    suspend fun getIdentityInfo(): IdentityInfoDto
    suspend fun getUserProfileImage(): String
    suspend fun fetchTaminRelation(): TaminRelationResponse
    suspend fun sendImageRequest(branchCode: String, filter: List<ApiFilterDN>): String
    suspend fun changeMobile(filter: List<ApiFilterDN>): EditMobileResponseDto
    suspend fun verifyChangeMobileCode(request: VerifyMobileReq): String

    suspend fun getSubDominantsInfo(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): SubDominantResponseData

    suspend fun getBankAccountList(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): ListData<BankAccountResponse>?

    suspend fun getInsuredActiveBranch() : List<InsuredActiveBranchData>?

    suspend fun getRelationTaminAll(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): ListData<ActiveRelationResponse>?

    suspend fun getElectronicFile(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): ListData<ElectronicFileResponse>?
}
