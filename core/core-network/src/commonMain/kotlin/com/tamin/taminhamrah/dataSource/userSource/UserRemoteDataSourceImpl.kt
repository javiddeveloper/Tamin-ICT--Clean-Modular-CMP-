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
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.apiService.UserApiService
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDTO
import com.tamin.taminhamrah.model.bankAccount.BankAccountDTO
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.certificate.RecipientDTO
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.subDominant.SubDominantResponseDTO
import com.tamin.taminhamrah.model.subDominant.insuredActiveBranch.InsuredActiveBranchDTO
import com.tamin.taminhamrah.model.user.EditMobileResponseDto
import com.tamin.taminhamrah.model.user.TaminRelationDTO
import com.tamin.taminhamrah.model.user.CurrentUserDto
import com.tamin.taminhamrah.model.user.UserProfileDto
import com.tamin.taminhamrah.model.user.VerifyMobileRequest
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.readPdfChannel
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.model.bankAccount.BankAccountCreatedDTO
import com.tamin.taminhamrah.model.bankAccount.BankAccountRequestDTO

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

    override suspend fun fetchTaminRelation(): TaminRelationDTO {
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
        query: ApiQueryParamDN
    ): SubDominantResponseDTO {
        return try {
            val response = userApiService.getSubDominantsInfo(queryBuilder.buildQuery(query))
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
        query: ApiQueryParamDN
    ): ListData<BankAccountDTO>? {
        return try {
            val response = userApiService.getBankAccountList(queryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getInsuredActiveBranch(): List<InsuredActiveBranchDTO>? {
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

    override suspend fun getRelationTaminAll(
        query: ApiQueryParamDN
    ): ListData<ActiveRelationDTO>? {
        return try {
            val response = userApiService.getRelationTaminAll(queryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getElectronicFile(
        query: ApiQueryParamDN
    ): ListData<ElectronicFileDTO>? {
        return try {
            val response = userApiService.getElectronicFile(queryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun downloadDocument(url: String): PdfDownloadDTO {
        return try {
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = userApiService.downloadDocument(url).readPdfChannel()
                )
            )
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun changeMobile(
        mobile: String
    ): EditMobileResponseDto {
        return try {
            val response = userApiService.changeMobile(
//                referer = NetworkConstants.REFERER_MOBILE,
                url = NetworkConstants.EDIT_MOBILE_URL,
                mobile = mobile
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun verifyChangeMobileCode(request: VerifyMobileRequest): String {
        return try {
            val response = userApiService.verifyChangeMobileCode(
//                referer = NetworkConstants.REFERER_MOBILE,
                url = NetworkConstants.VERIFY_EDIT_MOBILE_URL,
                loginRequest = request
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getUserProfile(): UserProfileDto? {
        return try {
            val response = userApiService.getUserProfile()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
    override suspend fun getCurrentUser(): CurrentUserDto? {
        return try {
            userApiService.getCurrentUser().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun checkUserIsNew(nationalId: String): Boolean {
        return try {
            val response = userApiService.checkUserIsNew(nationalId)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun registerBankAccount(
        request: BankAccountRequestDTO,
    ): BankAccountCreatedDTO? {
        return try {
            val response = userApiService.registerBankAccount(request)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getStatusCertificateReport(filter: List<ApiFilterDN>): String? {
        return try {
            val response = userApiService.getStatusCertificateReport(queryBuilder.buildFilterJson(filter))
            response.extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getRecipients(query: ApiQueryParamDN): ListData<RecipientDTO>? {
        return try {
            val response = userApiService.getRecipients(queryBuilder.buildQuery(query.copy(limit = 1000)))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}
