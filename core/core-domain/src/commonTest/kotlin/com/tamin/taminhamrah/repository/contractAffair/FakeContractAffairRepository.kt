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
import kotlinx.coroutines.flow.flow

class FakeContractAffairRepository : ContractAffairRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")

    var lastPageQuery: ApiQueryParamDN? = null
    var contractsPageResult: PageDN<ContractDN> = PageDN(items = emptyList(), total = 0)
    var contractStatesResult: List<ContractStateDN> = emptyList()
    var lastCancelParams: CancelContractParamsDN? = null
    var cancelContractCalled = false
    var lastPaymentHistoryContractNumber: String? = null
    var paymentHistoryResult: List<ContractPaymentHistoryItemDN> = emptyList()
    var lastReportPremiumType: ContractPremiumType? = null
    var contractReportResult: PdfDownloadDN = PdfDownloadDN(pdf = null)

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

    var contractDebitResult: ContractDebitDN = ContractDebitDN(
        total = 0L,
        insurancePremiums = 0L,
        previousDebit = 0L,
        startDate = 0L,
        endDate = 0L,
        payPremiumDate = null,
        infoMessage = null,
    )
    var lastDebitPremiumType: ContractPremiumType? = null
    var lastDebitMonth: Int? = null

    override fun getContractDebit(
        premiumType: ContractPremiumType,
        month: Int,
    ): Flow<ContractDebitDN> = flow {
        lastDebitPremiumType = premiumType
        lastDebitMonth = month
        if (shouldThrowError) throw error
        emit(contractDebitResult)
    }

    var contractLastPaymentResult: ContractLastPaymentDN = ContractLastPaymentDN(
        lastPaymentTimestamp = null,
        checkReloLap = null,
        medicalResultResend = null,
    )
    var lastLastPaymentPremiumType: ContractPremiumType? = null

    override fun getContractLastPayment(
        premiumType: ContractPremiumType,
    ): Flow<ContractLastPaymentDN> = flow {
        lastLastPaymentPremiumType = premiumType
        if (shouldThrowError) throw error
        emit(contractLastPaymentResult)
    }

    var paymentCalculationDetailsResult: List<PaymentCalculationRowDN> = emptyList()
    var lastCalcDetailsArgs: Triple<ContractPremiumType, Long, Long>? = null

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
