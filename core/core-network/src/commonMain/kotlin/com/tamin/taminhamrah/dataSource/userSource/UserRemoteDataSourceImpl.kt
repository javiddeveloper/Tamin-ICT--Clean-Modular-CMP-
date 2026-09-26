/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.userSource

import com.tamin.taminhamrah.tools.safeCall
import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.tools.extractTypedData
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
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
    private val errorParser: ErrorParser,
    private val json: Json
) : UserRemoteDataSource {

    override suspend fun getIdentityInfo(): IdentityInfoDto {
        return errorParser.safeCall("getIdentityInfo") {
            val response = userApiService.getIdentityInfo()
            response.extractData()
        }
    }

    override suspend fun getUserProfileImage(): String {
        return errorParser.safeCall("getUserProfileImage") {
            val response = userApiService.getUserProfileImage()
            response.extractData()
        }
    }

    override suspend fun fetchTaminRelation(): TaminRelationDTO {
        return errorParser.safeCall("fetchTaminRelation") {
            val response = userApiService.fetchTaminRelation()
            response.extractData()
        }
    }

    override suspend fun sendImageRequest(
        branchCode: String,
        filter: List<ApiFilterDN>
    ): String {
        return errorParser.safeCall("sendImageRequest") {
            val response =
                userApiService.sendImageRequest(branchCode, queryBuilder.buildFilterJson(filter))
            response.extractData()
        }
    }

    override suspend fun getSubDominantsInfo(
        query: ApiQueryParamDN
    ): SubDominantResponseDTO {
        return errorParser.safeCall("getSubDominantsInfo") {
            val response = userApiService.getSubDominantsInfo(queryBuilder.buildQuery(query))
            response.extractData()
        }
    }

    override suspend fun getBankAccountList(
        query: ApiQueryParamDN
    ): ListData<BankAccountDTO> {
        return errorParser.safeCall("getBankAccountList") {
            val response = userApiService.getBankAccountList(queryBuilder.buildQuery(query))
            response.extractData()
        }
    }

    override suspend fun getInsuredActiveBranch(): List<InsuredActiveBranchDTO> {
        return errorParser.safeCall("getInsuredActiveBranch") {
            val response = userApiService.getInsuredActiveBranch()
            response.extractData()
        }
    }

    override suspend fun getRelationTaminAll(
        query: ApiQueryParamDN
    ): ListData<ActiveRelationDTO> {
        return errorParser.safeCall("getRelationTaminAll") {
            val response = userApiService.getRelationTaminAll(queryBuilder.buildQuery(query))
            response.extractData()
        }
    }

    override suspend fun getElectronicFile(
        query: ApiQueryParamDN
    ): ListData<ElectronicFileDTO> {
        return errorParser.safeCall("getElectronicFile") {
            val response = userApiService.getElectronicFile(queryBuilder.buildQuery(query))
            response.extractData()
        }
    }

    override suspend fun downloadDocument(url: String): PdfDownloadDTO {
        return errorParser.safeCall("downloadDocument") {
            PdfDownloadDTO(
                pdf = InputStreamDTO(
                    pdf = userApiService.downloadDocument(url).readPdfChannel()
                )
            )
        }
    }

    override suspend fun changeMobile(
        mobile: String
    ): EditMobileResponseDto {
        return errorParser.safeCall("changeMobile") {
            val response = userApiService.changeMobile(
//                referer = NetworkConstants.REFERER_MOBILE,
                url = NetworkConstants.EDIT_MOBILE_URL,
                mobile = mobile
            )
            response.extractTypedData(json, EditMobileResponseDto.serializer())
        }
    }

    override suspend fun verifyChangeMobileCode(request: VerifyMobileRequest): String {
        return errorParser.safeCall("verifyChangeMobileCode") {
            val response = userApiService.verifyChangeMobileCode(
//                referer = NetworkConstants.REFERER_MOBILE,
                url = NetworkConstants.VERIFY_EDIT_MOBILE_URL,
                loginRequest = request
            )
            response.extractTypedData(json, String.serializer())
        }
    }

    override suspend fun getUserProfile(): UserProfileDto {
        return errorParser.safeCall("getUserProfile") {
            val response = userApiService.getUserProfile()
            response.extractData()
        }
    }
    override suspend fun getCurrentUser(): CurrentUserDto {
        return errorParser.safeCall("getCurrentUser") {
            userApiService.getCurrentUser().extractData()
        }
    }

    override suspend fun checkUserIsNew(nationalId: String): Boolean {
        return errorParser.safeCall("checkUserIsNew") {
            val response = userApiService.checkUserIsNew(nationalId)
            response.extractData()
        }
    }

    override suspend fun registerBankAccount(
        request: BankAccountRequestDTO,
    ): BankAccountCreatedDTO {
        return errorParser.safeCall("registerBankAccount") {
            val response = userApiService.registerBankAccount(request)
            response.extractData()
        }
    }

    override suspend fun getStatusCertificateReport(filter: List<ApiFilterDN>): String {
        return errorParser.safeCall("getStatusCertificateReport") {
            val response = userApiService.getStatusCertificateReport(queryBuilder.buildFilterJson(filter))
            response.extractMessage()
        }
    }

    override suspend fun getWageCertificateReport(filter: List<ApiFilterDN>): String {
        return errorParser.safeCall("getWageCertificateReport") {
            val response = userApiService.getWageCertificateReport(queryBuilder.buildFilterJson(filter))
            response.extractMessage()
        }
    }

    override suspend fun getRecipients(query: ApiQueryParamDN): ListData<RecipientDTO> {
        return errorParser.safeCall("getRecipients") {
            val response = userApiService.getRecipients(queryBuilder.buildQuery(query.copy(limit = 1000)))
            response.extractData()
        }
    }
}
