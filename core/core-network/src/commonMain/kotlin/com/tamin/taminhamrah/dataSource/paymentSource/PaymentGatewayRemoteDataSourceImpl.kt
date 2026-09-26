package com.tamin.taminhamrah.dataSource.paymentSource

import kotlinx.coroutines.CancellationException
import io.ktor.serialization.JsonConvertException
import com.tamin.taminhamrah.apiService.payment.PaymentGatewayApiService
import com.tamin.taminhamrah.model.payment.PaymentInfoDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkRequestDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.extractMessage
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException

internal class PaymentGatewayRemoteDataSourceImpl(
    private val apiService: PaymentGatewayApiService,
    private val errorParser: ErrorParser,
) : PaymentGatewayRemoteDataSource {

    override suspend fun getPaymentInfo(ticket: String): PaymentInfoDTO = call {
        apiService.getPaymentInfo(ticket).extractData()
    }

    override suspend fun createPaymentLink(
        ticket: String,
        request: PaymentLinkRequestDTO,
    ): PaymentLinkDTO = call {
        apiService.createPaymentLink(ticket, request).extractData()
    }

    override suspend fun cancelPayment(ticket: String) {
        call { apiService.cancelPayment(ticket).extractMessage() }
    }

    private suspend fun <T> call(block: suspend () -> T): T = try {
        block()
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
