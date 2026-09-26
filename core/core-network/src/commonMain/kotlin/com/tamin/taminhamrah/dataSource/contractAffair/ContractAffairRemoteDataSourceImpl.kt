package com.tamin.taminhamrah.dataSource.contractAffair

import com.tamin.taminhamrah.tools.requireSuccessStatus
import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.contractAffair.ContractAffairApiService
import com.tamin.taminhamrah.model.contractAffair.CancelContractRequestDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDTO
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDTO
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDTO
import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.contractAffair.ContractStateDTO
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive

class ContractAffairRemoteDataSourceImpl(
    private val contractAffairApiService: ContractAffairApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : ContractAffairRemoteDataSource {

    override suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO> {
        return errorParser.safeCall("getContracts") {
            contractAffairApiService.getContractList(apiQueryBuilder.buildQuery(query)).extractData()
        }
    }

    override suspend fun getContractStates(query: ApiQueryParamDN): ListData<ContractStateDTO> {
        return errorParser.safeCall("getContractStates") {
            contractAffairApiService.getContractStates(apiQueryBuilder.buildQuery(query)).extractData()
        }
    }

    override suspend fun cancelContract(
        premiumType: ContractPremiumType,
        stateCode: Int,
        request: CancelContractRequestDTO,
    ) {
        errorParser.safeCall("cancelContract") {
            val response = if (premiumType == ContractPremiumType.OPTIONAL) {
                contractAffairApiService.cancelOptionalContract(stateCode, request)
            } else {
                contractAffairApiService.cancelFreelanceContract(stateCode, request)
            }
            response.requireSuccessStatus()
        }
    }

    override suspend fun getContractPaymentHistory(
        contractNumber: String,
    ): List<ContractPaymentHistoryItemDTO> {
        return errorParser.safeCall("getContractPaymentHistory") {
            val rows = contractAffairApiService.getContractPaymentHistory(contractNumber)
                .extractData().list.orEmpty()
            rows.map { it.toPaymentHistoryItem() }
        }
    }

    override suspend fun downloadContractReport(premiumType: ContractPremiumType): PdfDownloadDTO {
        return errorParser.safeCall("downloadContractReport") {
            // `old_android` sends `System.currentTimeMillis()` as a cache-busting path segment;
            // the backend resolves the contract from the authenticated session.
            val timestamp = getTimeMillis()
            val statement = when (premiumType) {
                ContractPremiumType.OPTIONAL ->
                    contractAffairApiService.getOptionalContractReport(timestamp)

                ContractPremiumType.FRACTION ->
                    contractAffairApiService.getFractionContractReport(timestamp)

                ContractPremiumType.FREELANCE ->
                    contractAffairApiService.getFreelanceContractReport(timestamp)
            }
            PdfDownloadDTO(pdf = InputStreamDTO(pdf = statement.body()))
        }
    }

    override suspend fun getContractDebit(
        premiumType: ContractPremiumType,
        month: Int,
    ): ContractDebitDTO {
        return errorParser.safeCall("getContractDebit") {
            val response = if (premiumType == ContractPremiumType.OPTIONAL) {
                contractAffairApiService.getOptionalContractDebit(month)
            } else {
                contractAffairApiService.getFreelanceContractDebit(month)
            }
            response.extractData()
        }
    }

    override suspend fun getContractLastPayment(
        premiumType: ContractPremiumType,
    ): ContractLastPaymentDTO {
        return errorParser.safeCall("getContractLastPayment") {
            if (premiumType == ContractPremiumType.OPTIONAL) {
                val timestamp = contractAffairApiService.getOptionalLastPayment().data
                ContractLastPaymentDTO(lastPaymentTimestamp = timestamp?.toString())
            } else {
                val data = contractAffairApiService.getFreelanceLastPayment().data
                ContractLastPaymentDTO(
                    lastPaymentTimestamp = data?.lastPaymentDate,
                    chekReloLap = data?.chekReloLap,
                    medicalRsltResend = data?.medicalRsltResend,
                )
            }
        }
    }

    override suspend fun getPaymentCalculationDetails(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): List<PaymentCalculationRowDTO> {
        return errorParser.safeCall("getPaymentCalculationDetails") {
            val response = if (premiumType == ContractPremiumType.OPTIONAL) {
                contractAffairApiService.getOptionalPaymentDetails(
                    startDate = startDate.toString(),
                    endDate = endDate.toString(),
                )
            } else {
                contractAffairApiService.getFreelancePaymentDetails(
                    startDate = startDate.toString(),
                    endDate = endDate.toString(),
                )
            }
            response.extractData().list.orEmpty().map { it.toPaymentCalculationRow() }
        }
    }

    /**
     * `freelance-payment-history-head-with-contractNumber` answers with positional arrays; the
     * index each field lives at is mirrored from `old_android`'s
     * `ServiceRepository.getContractsPaymentsListFreelance`.
     */
    private fun JsonArray.toPaymentHistoryItem(): ContractPaymentHistoryItemDTO =
        ContractPaymentHistoryItemDTO(
            nationalId = stringAt(1),
            insuranceId = stringAt(2),
            debtNumber = stringAt(3),
            startTermPayment = stringAt(5),
            endTermPayment = stringAt(6),
            totalDebt = doubleAt(7),
            paymentDeadline = stringAt(8),
            amountPayment = doubleAt(9),
            datePayment = stringAt(10),
            statusContract = stringAt(11),
            statusRecipient = stringAt(12),
        )

    /**
     * `freelance-payment-details` / `payment-details` answer with positional arrays
     * `[year, month, day, description, wage, amount]`, mirrored from `old_android`'s
     * `ServiceRepository.getPaymentCalculationDetailList`.
     */
    private fun JsonArray.toPaymentCalculationRow(): PaymentCalculationRowDTO =
        PaymentCalculationRowDTO(
            year = stringAt(0),
            month = stringAt(1),
            day = stringAt(2),
            description = stringAt(3),
            wage = doubleAt(4),
            amount = doubleAt(5),
        )

    private fun JsonArray.cellAt(index: Int): JsonElement? =
        getOrNull(index)?.takeUnless { it is JsonNull }

    private fun JsonArray.stringAt(index: Int): String? =
        cellAt(index)?.jsonPrimitive?.contentOrNull

    private fun JsonArray.doubleAt(index: Int): Double? =
        cellAt(index)?.jsonPrimitive?.doubleOrNull
}
