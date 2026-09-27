package com.tamin.taminhamrah.dataSource.contractAffair

import com.tamin.taminhamrah.tools.readPdfChannel
import kotlinx.coroutines.CancellationException
import io.ktor.serialization.JsonConvertException
import com.tamin.taminhamrah.tools.requireSuccessStatus
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
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException

class ContractAffairRemoteDataSourceImpl(
    private val contractAffairApiService: ContractAffairApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : ContractAffairRemoteDataSource {

    override suspend fun getContracts(query: ApiQueryParamDN): ListData<ContractDTO> {
        return try {
            contractAffairApiService.getContractList(apiQueryBuilder.buildQuery(query)).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getContractStates(query: ApiQueryParamDN): ListData<ContractStateDTO> {
        return try {
            contractAffairApiService.getContractStates(apiQueryBuilder.buildQuery(query)).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }

    override suspend fun cancelContract(
        premiumType: ContractPremiumType,
        stateCode: Int,
        request: CancelContractRequestDTO,
    ) {
        try {
            val response = if (premiumType == ContractPremiumType.OPTIONAL) {
                contractAffairApiService.cancelOptionalContract(stateCode, request)
            } else {
                contractAffairApiService.cancelFreelanceContract(stateCode, request)
            }
            response.requireSuccessStatus()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }

    override suspend fun getContractPaymentHistory(
        contractNumber: String,
    ): List<ContractPaymentHistoryItemDTO> {
        return try {
            val rows = contractAffairApiService.getContractPaymentHistory(contractNumber)
                .extractData().list.orEmpty()
            rows.map { it.toPaymentHistoryItem() }
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }

    override suspend fun downloadContractReport(premiumType: ContractPremiumType): PdfDownloadDTO {
        return try {
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
            PdfDownloadDTO(pdf = InputStreamDTO(pdf = statement.readPdfChannel()))
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }

    override suspend fun getContractDebit(
        premiumType: ContractPremiumType,
        month: Int,
    ): ContractDebitDTO {
        return try {
            val response = if (premiumType == ContractPremiumType.OPTIONAL) {
                contractAffairApiService.getOptionalContractDebit(month)
            } else {
                contractAffairApiService.getFreelanceContractDebit(month)
            }
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }

    override suspend fun getContractLastPayment(
        premiumType: ContractPremiumType,
    ): ContractLastPaymentDTO {
        return try {
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
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
        }
    }

    override suspend fun getPaymentCalculationDetails(
        premiumType: ContractPremiumType,
        startDate: Long,
        endDate: Long,
    ): List<PaymentCalculationRowDTO> {
        return try {
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
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR),
            )
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
