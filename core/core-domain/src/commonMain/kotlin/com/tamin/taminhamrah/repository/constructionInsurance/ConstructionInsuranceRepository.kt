package com.tamin.taminhamrah.repository.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import kotlinx.coroutines.flow.Flow

interface ConstructionInsuranceRepository {
    fun getConstructionFiles(
        search: ConstructionFileSearchParamsDN? = null
    ): Flow<List<ConstructionFileDN>>

    /** ذینفعان کارگاه — network-only, reached from the عملیات menu's «ذینفعان کارگاه» option. */
    fun getBeneficiariesWorkshop(
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String? = null,
    ): Flow<List<BeneficiaryConstructionDN>>

    /** صدور و مدیریت برگه پرداخت — payment sheets already issued for [debitNumber]. */
    fun getPaymentSheetConstructionInfo(debitNumber: String): Flow<List<PaymentSheetConstructionFileDN>>

    /** مشاهده گواهی برگه پرداخت — the payment-sheet certificate as a downloadable PDF. */
    fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): Flow<PdfDownloadDN>

    /** صدور برگه پرداخت. Emits the server's confirmation message on success, throws on failure. */
    fun issuancePaymentSheet(debitNumber: String): Flow<String>

    /** مدیریت پرداخت اقساط — installment (debit) letters for one workshop/branch. */
    fun getInstallmentLetterList(workshopId: String, branchId: String): Flow<List<InstallmentLetterDN>>
}
