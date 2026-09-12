package com.tamin.taminhamrah.dataSource.contractAffair

import com.tamin.taminhamrah.model.contractAffair.CancelContractRequestDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDTO
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDTO
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDTO
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contractAffair.ContractStateDTO
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface ContractAffairRemoteDataSource {
    suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO>
    suspend fun getContractStates(query: ApiQueryParamDN): ListData<ContractStateDTO>
    suspend fun cancelContract(
        premiumType: ContractPremiumType,
        stateCode: Int,
        request: CancelContractRequestDTO,
    )
    suspend fun getContractPaymentHistory(contractNumber: String): List<ContractPaymentHistoryItemDTO>
    suspend fun downloadContractReport(premiumType: ContractPremiumType): PdfDownloadDTO

    /** محاسبهٔ حق بیمه for [month] months. */
    suspend fun getContractDebit(premiumType: ContractPremiumType, month: Int): ContractDebitDTO

    /** آخرین پرداخت حق بیمه for the contract kind. */
    suspend fun getContractLastPayment(premiumType: ContractPremiumType): ContractLastPaymentDTO

    /** جزئیات برگ پرداخت for the calculated payment period. */
    suspend fun getPaymentCalculationDetails(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): List<PaymentCalculationRowDTO>
}
