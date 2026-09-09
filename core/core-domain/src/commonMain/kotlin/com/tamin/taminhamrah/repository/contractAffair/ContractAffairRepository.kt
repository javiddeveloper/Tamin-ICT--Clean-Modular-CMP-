package com.tamin.taminhamrah.repository.contractAffair

import com.tamin.taminhamrah.model.contractAffair.CancelContractParamsDN
import com.tamin.taminhamrah.model.contractAffair.ContractDN
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDN
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contractAffair.ContractStateDN
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

/**
 * امور قراردادها و پرداخت — the data contract for the standalone
 * `feature/contractsAndPaymentAffair` module. Network-only (no Room cache); each method mirrors an
 * `special-insured-services` endpoint.
 */
interface ContractAffairRepository {

    /**
     * Page-at-a-time special-insured contract list for the امور قراردادها و پرداخت screen,
     * paginated through [com.tamin.taminhamrah.paging.Paginator].
     */
    fun getContractsPage(query: ApiQueryParamDN): Flow<PageDN<ContractDN>>

    /** علت خاتمه قرارداد — reasons offered when cancelling a contract. */
    fun getContractStates(): Flow<List<ContractStateDN>>

    /** غیرفعال کردن قرارداد. Completes normally on success, throws on failure. */
    fun cancelContract(params: CancelContractParamsDN): Flow<Unit>

    /** مشاهده پرداخت‌ها for a single contract. */
    fun getContractPaymentHistory(contractNumber: String): Flow<List<ContractPaymentHistoryItemDN>>

    /** مشاهده قرارداد — the contract report as a downloadable PDF. */
    fun downloadContractReport(premiumType: ContractPremiumType): Flow<PdfDownloadDN>

    /** محاسبهٔ حق بیمه — payable amount and period for [month] months. */
    fun getContractDebit(premiumType: ContractPremiumType, month: Int): Flow<ContractDebitDN>

    /** آخرین پرداخت حق بیمه for the contract kind. */
    fun getContractLastPayment(premiumType: ContractPremiumType): Flow<ContractLastPaymentDN>

    /** جزئیات برگ پرداخت for a calculated payment period. */
    fun getPaymentCalculationDetails(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): Flow<List<PaymentCalculationRowDN>>
}
