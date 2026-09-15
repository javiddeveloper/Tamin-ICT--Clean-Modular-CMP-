package com.tamin.taminhamrah.repository.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var constructionFilesResult: List<ConstructionFileDN> = emptyList()
    var beneficiariesResult: List<BeneficiaryConstructionDN> = emptyList()
    var paymentSheetsResult: List<PaymentSheetConstructionFileDN> = emptyList()
    var certificatePdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var issuanceMessageResult: String = "OK"
    var installmentLettersResult: List<InstallmentLetterDN> = emptyList()

    var shouldThrowError = false
    var thrownError: Throwable = RuntimeException("Error")

    var lastConstructionFilesSearch: ConstructionFileSearchParamsDN? = null
    var lastBeneficiariesRequestNumber: Long? = null
    var lastBeneficiariesFileNumber: Long? = null
    var lastBeneficiariesRequestDate: String? = null
    var lastPaymentSheetDebitNumber: String? = null
    var lastCertificateDebitNumber: String? = null
    var lastCertificateBranchCode: String? = null
    var lastIssuanceDebitNumber: String? = null
    var lastInstallmentWorkshopId: String? = null
    var lastInstallmentBranchId: String? = null

    override fun getConstructionFiles(search: ConstructionFileSearchParamsDN?): Flow<List<ConstructionFileDN>> = flow {
        lastConstructionFilesSearch = search
        if (shouldThrowError) throw thrownError
        emit(constructionFilesResult)
    }

    override fun getBeneficiariesWorkshop(
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String?,
    ): Flow<List<BeneficiaryConstructionDN>> = flow {
        lastBeneficiariesRequestNumber = requestNumber
        lastBeneficiariesFileNumber = fileNumber
        lastBeneficiariesRequestDate = requestDate
        if (shouldThrowError) throw thrownError
        emit(beneficiariesResult)
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

    override fun getInstallmentLetterList(workshopId: String, branchId: String): Flow<List<InstallmentLetterDN>> = flow {
        lastInstallmentWorkshopId = workshopId
        lastInstallmentBranchId = branchId
        if (shouldThrowError) throw thrownError
        emit(installmentLettersResult)
    }
}
