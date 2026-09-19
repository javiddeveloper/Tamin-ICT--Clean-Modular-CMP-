package com.tamin.taminhamrah.dataSource.constructionInsurance

import com.tamin.taminhamrah.apiService.constructionInsurance.ConstructionInsuranceApiService
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDTO
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDTO
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import com.tamin.taminhamrah.tools.readPdfChannel

/**
 * Sent by the legacy native app on every `getConstructionFiles`/`getBeneficiariesWorkshop` call
 * (`ConstructionInsurancePremiumViewModel`, `D:\my-tamin`) — a fixed literal unrelated to the real
 * `page`/`start`/`limit` paging params below, kept for backend parity.
 */
private val POSITION_PARAM = "position" to "1"

internal class ConstructionInsuranceRemoteDataSourceImpl(
    private val apiService: ConstructionInsuranceApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : ConstructionInsuranceRemoteDataSource {

    override suspend fun getConstructionFiles(
        query: ApiQueryParamDN
    ): ListData<ConstructionFileDTO> = call {
        apiService.getConstructionFiles(queryBuilder.buildQuery(query) + POSITION_PARAM).extractData()
    }

    override suspend fun getBeneficiariesWorkshop(query: ApiQueryParamDN): ListData<BeneficiaryConstructionDTO> = call {
        apiService.getBeneficiariesWorkshop(queryBuilder.buildQuery(query) + POSITION_PARAM).extractData()
    }

    override suspend fun getPaymentSheetConstructionInfo(
        debitNumber: String
    ): ListData<PaymentSheetConstructionFileDTO> = call {
        apiService.getPaymentSheetConstructionInfo(debitNumber).extractData()
    }

    override suspend fun getCertificatePaymentSheetPdf(
        debitNumber: String,
        branchCode: String,
    ): PdfDownloadDTO = call {
        PdfDownloadDTO(
            pdf = InputStreamDTO(
                pdf = apiService.getCertificatePaymentSheetPdf(debitNumber, branchCode).readPdfChannel()
            )
        )
    }

    override suspend fun issuancePaymentSheet(debitNumber: String): String = call {
        apiService.issuancePaymentSheet(debitNumber).extractMessage()
    }

    override suspend fun getInstallmentLetterList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): ListData<InstallmentLetterDTO> = call {
        apiService.getInstallmentLetterList(workshopId, branchId, queryBuilder.buildQuery(query)).extractData()
    }

    private suspend fun <T> call(block: suspend () -> T): T = try {
        block()
    } catch (e: TaminErrorUriException) {
        throw errorParser.parseGeneralError(e)
    } catch (e: Exception) {
        throw errorParser.parseGeneralError(
            TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
        )
    }
}
