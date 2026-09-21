package com.tamin.taminhamrah.dataSource.constructionInsurance

import com.tamin.taminhamrah.apiService.constructionInsurance.ConstructionInsuranceApiService
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDTO
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListDTO
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListDTO
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDTO
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.tools.readPdfChannel
import com.tamin.taminhamrah.tools.safeCall

internal class ConstructionInsuranceRemoteDataSourceImpl(
    private val apiService: ConstructionInsuranceApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : ConstructionInsuranceRemoteDataSource {

    override suspend fun getConstructionFiles(
        query: ApiQueryParamDN
    ): ListData<ConstructionFileDTO> = errorParser.safeCall("getConstructionFiles") {
        apiService.getConstructionFiles(queryBuilder.buildQuery(query)).extractData()
    }

    override suspend fun getBeneficiariesWorkshop(
        query: ApiQueryParamDN
    ): ListData<BeneficiaryConstructionDTO> = errorParser.safeCall("getBeneficiariesWorkshop") {
        apiService.getBeneficiariesWorkshop(queryBuilder.buildQuery(query)).extractData()
    }

    override suspend fun getPaymentSheetConstructionInfo(
        debitNumber: String
    ): ListData<PaymentSheetConstructionFileDTO> = errorParser.safeCall("getPaymentSheetConstructionInfo") {
        apiService.getPaymentSheetConstructionInfo(debitNumber).extractData()
    }

    override suspend fun getCertificatePaymentSheetPdf(
        debitNumber: String,
        branchCode: String,
    ): PdfDownloadDTO = errorParser.safeCall("getCertificatePaymentSheetPdf") {
        PdfDownloadDTO(
            pdf = InputStreamDTO(
                pdf = apiService.getCertificatePaymentSheetPdf(debitNumber, branchCode).readPdfChannel()
            )
        )
    }

    override suspend fun issuancePaymentSheet(debitNumber: String): String =
        errorParser.safeCall("issuancePaymentSheet") {
            apiService.issuancePaymentSheet(debitNumber).extractMessage()
        }

    override suspend fun getInstallmentLetterList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): ListData<InstallmentLetterDTO> = errorParser.safeCall("getInstallmentLetterList") {
        apiService.getInstallmentLetterList(workshopId, branchId, queryBuilder.buildQuery(query)).extractData()
    }

    override suspend fun getDetailDebitList(
        debitNumber: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): ListData<InstallmentDebitListDTO> = errorParser.safeCall("getDetailDebitList") {
        apiService.getDetailDebitList(debitNumber, branchId, queryBuilder.buildQuery(query)).extractData()
    }

    override suspend fun getInstallmentConstructionList(
        debitNumber: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): ListData<InstallmentConstructionListDTO> = errorParser.safeCall("getInstallmentConstructionList") {
        apiService.getInstallmentConstructionList(debitNumber, branchId, queryBuilder.buildQuery(query)).extractData()
    }
}
