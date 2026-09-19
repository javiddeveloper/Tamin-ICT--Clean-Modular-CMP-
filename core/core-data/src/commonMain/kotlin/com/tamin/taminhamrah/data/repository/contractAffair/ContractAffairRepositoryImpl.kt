package com.tamin.taminhamrah.data.repository.contractAffair

import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toRequestDto
import com.tamin.taminhamrah.dataSource.contractAffair.ContractAffairRemoteDataSource
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
import kotlinx.coroutines.flow.map

/**
 * امور قراردادها و پرداخت — network-only repository (no Room cache, no schema bump) backing the
 * standalone `feature/contractsAndPaymentAffair` module.
 */
class ContractAffairRepositoryImpl(
    private val contractAffairRemoteDataSource: ContractAffairRemoteDataSource,
) : ContractAffairRepository {

    override fun getContractsPage(query: ApiQueryParamDN): Flow<PageDN<ContractDN>> = flow {
        val response = contractAffairRemoteDataSource.getContracts(query)
        emit(
            PageDN(
                items = response.list.orEmpty().map { it.toDomain() },
                total = response.total,
            ),
        )
    }

    override fun getContractStates(): Flow<List<ContractStateDN>> = flow {
        val response = contractAffairRemoteDataSource.getContractStates(contractStatesQuery())
        emit(response.list.orEmpty().map { it.toDomain() })
    }

    override fun cancelContract(params: CancelContractParamsDN): Flow<Unit> = flow {
        contractAffairRemoteDataSource.cancelContract(
            premiumType = params.premiumType,
            stateCode = params.stateCode,
            request = params.toRequestDto(),
        )
        emit(Unit)
    }

    override fun getContractPaymentHistory(
        contractNumber: String,
    ): Flow<List<ContractPaymentHistoryItemDN>> = flow {
        emit(
            contractAffairRemoteDataSource.getContractPaymentHistory(contractNumber)
                .map { it.toDomain() },
        )
    }

    override fun downloadContractReport(premiumType: ContractPremiumType): Flow<PdfDownloadDN> = flow {
        emit(contractAffairRemoteDataSource.downloadContractReport(premiumType).toDomain())
    }

    override fun getContractDebit(
        premiumType: ContractPremiumType,
        month: Int,
    ): Flow<ContractDebitDN> = flow {
        emit(contractAffairRemoteDataSource.getContractDebit(premiumType, month).toDomain())
    }

    override fun getContractLastPayment(
        premiumType: ContractPremiumType,
    ): Flow<ContractLastPaymentDN> = flow {
        emit(contractAffairRemoteDataSource.getContractLastPayment(premiumType).toDomain())
    }

    override fun getPaymentCalculationDetails(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): Flow<List<PaymentCalculationRowDN>> = flow {
        emit(
            contractAffairRemoteDataSource
                .getPaymentCalculationDetails(premiumType, startDate, endDate)
                .toDomain(),
        )
    }

    // list-self-contract-state is itself paged (total ~24); ask for a page large enough to hold
    // every termination reason in one round-trip, matching how the picker consumes it.
    private fun contractStatesQuery(): ApiQueryParamDN = ApiQueryParamDN(
        page = 0,
        start = 0,
        limit = 100,
    )
}
