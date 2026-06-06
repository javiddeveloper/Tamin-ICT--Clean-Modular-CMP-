/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.userSource

import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.apiService.UserApiService
import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountResponse
import com.tamin.taminhamrah.model.subDominant.SubDominantResponseData
import com.tamin.taminhamrah.model.subDominant.insuredActiveBranch.InsuredActiveBranchData
import com.tamin.taminhamrah.model.user.TaminRelationResponse
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder

internal class UserRemoteDataSourceImpl(
    private val userApiService: UserApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : UserRemoteDataSource {

    override suspend fun getIdentityInfo(): IdentityInfoDto {
        return try {
            val response = userApiService.getIdentityInfo()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getUserProfileImage(): String {
        return try {
            val response = userApiService.getUserProfileImage()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun fetchTaminRelation(): TaminRelationResponse {
        return try {
            val response = userApiService.fetchTaminRelation()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendImageRequest(
        branchCode: String,
        filter: List<ApiFilterDN>
    ): String {
        return try {
            val response =
                userApiService.sendImageRequest(branchCode, queryBuilder.buildFilterJson(filter))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getSubDominantsInfo(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): SubDominantResponseData {
        return try {
            val response = userApiService.getSubDominantsInfo(page, start, limit, filter, sort)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getBankAccountList(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): ListData<BankAccountResponse>? {
        return try {
            val response = userApiService.getBankAccountList(page, start, limit, filter, sort)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getInsuredActiveBranch(): List<InsuredActiveBranchData>? {
        return try {
            val response = userApiService.getInsuredActiveBranch()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
