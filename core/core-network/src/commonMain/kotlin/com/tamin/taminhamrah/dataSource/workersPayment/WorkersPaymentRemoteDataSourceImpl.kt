package com.tamin.taminhamrah.dataSource.workersPayment

import com.tamin.taminhamrah.apiService.workersPayment.WorkersPaymentApiService
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitRequestDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDataDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage

internal class WorkersPaymentRemoteDataSourceImpl(
    private val apiService: WorkersPaymentApiService,
    private val errorParser: ErrorParser,
) : WorkersPaymentRemoteDataSource {

    override suspend fun getWorkersPaymentInfo(): WorkersPaymentInfoDataDTO {
        return try {
            apiService.getWorkersPaymentInfo().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun payWorkersDebit(
        request: WorkersPayDebitRequestDTO,
        redirectUrl: String,
    ): WorkersPayDebitDTO {
        return try {
            apiService.payWorkersDebit(body = request, redirectUrl = redirectUrl).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun inspectTicket(ticket: String?, paymentInfo: String?): String {
        return try {
            apiService.inspectTicket(ticket = ticket, paymentInfo = paymentInfo).extractMessage()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
