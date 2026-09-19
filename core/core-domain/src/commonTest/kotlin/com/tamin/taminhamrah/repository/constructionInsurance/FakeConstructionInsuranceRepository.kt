package com.tamin.taminhamrah.repository.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var constructionFilesResult: List<ConstructionFileDN> = emptyList()
    var constructionFilesPageResult: PageDN<ConstructionFileDN> = PageDN(items = emptyList(), total = 0)
    var beneficiariesPageResult: PageDN<BeneficiaryConstructionDN> = PageDN(items = emptyList(), total = 0)
    var paymentSheetsResult: List<PaymentSheetConstructionFileDN> = emptyList()
    var certificatePdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var issuanceMessageResult: String = "OK"
    var installmentLettersPageResult: PageDN<InstallmentLetterDN> = PageDN(items = emptyList(), total = 0)

    var shouldThrowError = false
    var thrownError: Throwable = RuntimeException("Error")

    var lastConstructionFilesSearch: ConstructionFileSearchParamsDN? = null
    var lastConstructionFilesPageQuery: ApiQueryParamDN? = null
    var lastBeneficiariesPageQuery: ApiQueryParamDN? = null
    var lastPaymentSheetDebitNumber: String? = null
    var lastCertificateDebitNumber: String? = null
    var lastCertificateBranchCode: String? = null
    var lastIssuanceDebitNumber: String? = null
    var lastInstallmentWorkshopId: String? = null
    var lastInstallmentBranchId: String? = null
    var lastInstallmentPageQuery: ApiQueryParamDN? = null

    override fun getConstructionFiles(search: ConstructionFileSearchParamsDN?): Flow<List<ConstructionFileDN>> = flow {
        lastConstructionFilesSearch = search
        if (shouldThrowError) throw thrownError
        emit(constructionFilesResult)
    }

    override fun getConstructionFilesPage(query: ApiQueryParamDN): Flow<PageDN<ConstructionFileDN>> = flow {
        lastConstructionFilesPageQuery = query
        if (shouldThrowError) throw thrownError
        emit(constructionFilesPageResult)
    }

    override fun getBeneficiariesWorkshopPage(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>> = flow {
        lastBeneficiariesPageQuery = query
        if (shouldThrowError) throw thrownError
        emit(beneficiariesPageResult)
    }

    override fun getPaymentSheetConstructionInfo(debitNumber: String): Flow<List<PaymentSheetConstructionFileDN>> = flow {
        lastPaymentSheetDebitNumber = debitNumber
        if (shouldThrowError) throw thrownError
        emit(paymentSheetsResult)
    }

    override fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): Flow<PdfDownloadDN> = flow {
        lastCertificateDebitNumber = debitNumber
        lastCertificateBranchCode = branchCode
        if (shouldThrowError) throw thrownError
        emit(certificatePdfResult)
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        lastIssuanceDebitNumber = debitNumber
        if (shouldThrowError) throw thrownError
        emit(issuanceMessageResult)
    }

    override fun getInstallmentLetterListPage(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentLetterDN>> = flow {
        lastInstallmentWorkshopId = workshopId
        lastInstallmentBranchId = branchId
        lastInstallmentPageQuery = query
        if (shouldThrowError) throw thrownError
        emit(installmentLettersPageResult)
    }
}
