package com.tamin.taminhamrah.repository.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface ConstructionInsuranceRepository {
    fun getConstructionFiles(
        search: ConstructionFileSearchParamsDN? = null
    ): Flow<List<ConstructionFileDN>>

    /** پرونده‌های ساختمانی list screen — paged version of [getConstructionFiles], per [[Pagination]]. */
    fun getConstructionFilesPage(query: ApiQueryParamDN): Flow<PageDN<ConstructionFileDN>>

    /**
     * ذینفعان کارگاه — network-only, reached from the عملیات menu's «ذینفعان کارگاه» option. Paged
     * per [[Pagination]]; [query]'s filters carry request/file number and request date.
     */
    fun getBeneficiariesWorkshopPage(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>>

    /** صدور و مدیریت برگه پرداخت — payment sheets already issued for [debitNumber]. */
    fun getPaymentSheetConstructionInfo(debitNumber: String): Flow<List<PaymentSheetConstructionFileDN>>

    /** مشاهده گواهی برگه پرداخت — the payment-sheet certificate as a downloadable PDF. */
    fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): Flow<PdfDownloadDN>

    /** صدور برگه پرداخت. Emits the server's confirmation message on success, throws on failure. */
    fun issuancePaymentSheet(debitNumber: String): Flow<String>

    /** مدیریت پرداخت اقساط — installment (debit) letters for one workshop/branch. Paged per [[Pagination]]. */
    fun getInstallmentLetterListPage(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentLetterDN>>

    /** بدهی‌های تقسیط‌شده — flat per-installment debit detail rows for one debit letter. Paged per [[Pagination]]. */
    fun getDetailDebitListPage(
        debitNumber: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentDebitListDN>>

    /** مدیریت اقساط و برگ پرداخت — individual installments under one debit letter. Paged per [[Pagination]]. */
    fun getInstallmentConstructionListPage(
        debitNumber: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentConstructionListDN>>
}
