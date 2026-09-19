package com.tamin.taminhamrah.dataSource.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDTO
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDTO
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface ConstructionInsuranceRemoteDataSource {
    suspend fun getConstructionFiles(
        query: ApiQueryParamDN
    ): ListData<ConstructionFileDTO>

    /** ذینفعان کارگاه — [query]'s filters carry request/file number and request date (`bldprdate`). */
    suspend fun getBeneficiariesWorkshop(query: ApiQueryParamDN): ListData<BeneficiaryConstructionDTO>

    suspend fun getPaymentSheetConstructionInfo(debitNumber: String): ListData<PaymentSheetConstructionFileDTO>

    suspend fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): PdfDownloadDTO

    /** Returns the server's bare success message (see [com.tamin.taminhamrah.tools.extractMessage]). */
    suspend fun issuancePaymentSheet(debitNumber: String): String

    suspend fun getInstallmentLetterList(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): ListData<InstallmentLetterDTO>
}
