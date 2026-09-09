package com.tamin.taminhamrah.dataSource.paymentSource

import com.tamin.taminhamrah.model.payment.PaymentInfoDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkRequestDTO
import com.tamin.taminhamrah.model.payment.PaymentMockMode
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository

/**
 * Sends each gateway call either to the real gateway or to a fake, according to the payment mock
 * mode currently chosen in Developer Options.
 *
 * The mode is read per call rather than once at construction, so switching it takes effect on the
 * next payment instead of after an app restart — the base-URL overrides work the other way round
 * only because they are baked into `HttpClient` singletons, and there is no reason to inherit that
 * limitation here.
 *
 * [DeveloperOptionsRepository.getPaymentMockMode] returns
 * [PaymentMockMode.DISABLED] in release builds whatever is stored, so a released app can never
 * reach the fakes.
 */
internal class PaymentGatewayRemoteDataSourceSelector(
    private val real: PaymentGatewayRemoteDataSource,
    private val successFake: PaymentGatewayRemoteDataSource,
    private val failureFake: PaymentGatewayRemoteDataSource,
    private val developerOptionsRepository: DeveloperOptionsRepository,
) : PaymentGatewayRemoteDataSource {

    private val current: PaymentGatewayRemoteDataSource
        get() = when (developerOptionsRepository.getPaymentMockMode()) {
            PaymentMockMode.DISABLED -> real
            PaymentMockMode.SUCCESS -> successFake
            PaymentMockMode.FAILURE -> failureFake
        }

    override suspend fun getPaymentInfo(ticket: String): PaymentInfoDTO =
        current.getPaymentInfo(ticket)

    override suspend fun createPaymentLink(
        ticket: String,
        request: PaymentLinkRequestDTO,
    ): PaymentLinkDTO = current.createPaymentLink(ticket, request)

    override suspend fun cancelPayment(ticket: String) = current.cancelPayment(ticket)
}
