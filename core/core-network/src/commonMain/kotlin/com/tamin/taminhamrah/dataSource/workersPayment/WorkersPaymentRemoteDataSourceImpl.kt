package com.tamin.taminhamrah.dataSource.workersPayment

import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.workersPayment.WorkersPaymentApiService
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitRequestDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDataDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage

internal class WorkersPaymentRemoteDataSourceImpl(
    private val apiService: WorkersPaymentApiService,
    private val errorParser: ErrorParser,
) : WorkersPaymentRemoteDataSource {

    override suspend fun getWorkersPaymentInfo(): WorkersPaymentInfoDataDTO {
        return errorParser.safeCall("getWorkersPaymentInfo") {
            apiService.getWorkersPaymentInfo().extractData()
        }
    }

    override suspend fun payWorkersDebit(
        request: WorkersPayDebitRequestDTO,
        redirectUrl: String,
    ): WorkersPayDebitDTO {
        return errorParser.safeCall("payWorkersDebit") {
            apiService.payWorkersDebit(body = request, redirectUrl = redirectUrl).extractData()
        }
    }

    override suspend fun inspectTicket(ticket: String?, paymentInfo: String?): String {
        return errorParser.safeCall("inspectTicket") {
            apiService.inspectTicket(ticket = ticket, paymentInfo = paymentInfo).extractMessage()
        }
    }
}
