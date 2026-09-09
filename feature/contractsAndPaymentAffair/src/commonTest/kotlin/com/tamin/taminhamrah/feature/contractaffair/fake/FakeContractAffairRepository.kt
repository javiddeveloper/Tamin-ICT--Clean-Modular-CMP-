package com.tamin.taminhamrah.feature.contractaffair.fake

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
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Hand-written double for [ContractAffairRepository] used by the امور قراردادها و پرداخت ViewModel
 * tests. Mirrors the core-domain fake of the same name (which those tests cannot see across the
 * source-set boundary).
 */
class FakeContractAffairRepository : ContractAffairRepository {

    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")

    var contractsPageResult: PageDN<ContractDN> = PageDN(items = emptyList(), total = 0)
    var contractStatesResult: List<ContractStateDN> = emptyList()
    var paymentHistoryResult: List<ContractPaymentHistoryItemDN> = emptyList()
    var contractReportResult: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var contractDebitResult: ContractDebitDN = ContractDebitDN(
        total = 0L,
        insurancePremiums = 0L,
        previousDebit = 0L,
        startDate = 0L,
        endDate = 0L,
        payPremiumDate = null,
        infoMessage = null,
    )
    var contractLastPaymentResult: ContractLastPaymentDN = ContractLastPaymentDN(
        lastPaymentTimestamp = null,
        checkReloLap = null,
        medicalResultResend = null,
    )
    var paymentCalculationDetailsResult: List<PaymentCalculationRowDN> = emptyList()

    var lastPageQuery: ApiQueryParamDN? = null
    var lastCancelParams: CancelContractParamsDN? = null
    var cancelContractCalled = false
    var lastPaymentHistoryContractNumber: String? = null
    var lastReportPremiumType: ContractPremiumType? = null
    var lastDebitPremiumType: ContractPremiumType? = null
    var lastDebitMonth: Int? = null
    var lastLastPaymentPremiumType: ContractPremiumType? = null
    var lastCalcDetailsArgs: Triple<ContractPremiumType, Long, Long>? = null

    override fun getContractsPage(query: ApiQueryParamDN): Flow<PageDN<ContractDN>> = flow {
        lastPageQuery = query
        if (shouldThrowError) throw error
        emit(contractsPageResult)
    }

    override fun getContractStates(): Flow<List<ContractStateDN>> = flow {
        if (shouldThrowError) throw error
        emit(contractStatesResult)
    }

    override fun cancelContract(params: CancelContractParamsDN): Flow<Unit> = flow {
        lastCancelParams = params
        cancelContractCalled = true
        if (shouldThrowError) throw error
        emit(Unit)
    }

    override fun getContractPaymentHistory(
        contractNumber: String,
    ): Flow<List<ContractPaymentHistoryItemDN>> = flow {
        lastPaymentHistoryContractNumber = contractNumber
        if (shouldThrowError) throw error
        emit(paymentHistoryResult)
    }

    override fun downloadContractReport(premiumType: ContractPremiumType): Flow<PdfDownloadDN> = flow {
        lastReportPremiumType = premiumType
        if (shouldThrowError) throw error
        emit(contractReportResult)
    }

    override fun getContractDebit(
        premiumType: ContractPremiumType,
        month: Int,
    ): Flow<ContractDebitDN> = flow {
        lastDebitPremiumType = premiumType
        lastDebitMonth = month
        if (shouldThrowError) throw error
        emit(contractDebitResult)
    }

    override fun getContractLastPayment(
        premiumType: ContractPremiumType,
    ): Flow<ContractLastPaymentDN> = flow {
        lastLastPaymentPremiumType = premiumType
        if (shouldThrowError) throw error
        emit(contractLastPaymentResult)
    }

    override fun getPaymentCalculationDetails(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): Flow<List<PaymentCalculationRowDN>> = flow {
        lastCalcDetailsArgs = Triple(premiumType, startDate, endDate)
        if (shouldThrowError) throw error
        emit(paymentCalculationDetailsResult)
    }
}
