package com.tamin.taminhamrah.data.repository.payment

import com.tamin.taminhamrah.data.mapper.buildPaymentLinkRequest
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.paymentSource.PaymentGatewayRemoteDataSource
import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentLinkDN
import com.tamin.taminhamrah.model.payment.PaymentPreviewDN
import com.tamin.taminhamrah.repository.payment.PaymentGatewayRepository

class PaymentGatewayRepositoryImpl(
    private val remoteDataSource: PaymentGatewayRemoteDataSource,
) : PaymentGatewayRepository {

    override suspend fun getPreview(ticket: String): PaymentPreviewDN =
        remoteDataSource.getPaymentInfo(ticket).toDomain(requestedTicket = ticket)

    override suspend fun createPaymentLink(
        ticket: String,
        payerType: PayerType,
        payerIdentifier: String,
    ): PaymentLinkDN = remoteDataSource
        .createPaymentLink(ticket, buildPaymentLinkRequest(payerType, payerIdentifier))
        .toDomain()

    override suspend fun cancelPayment(ticket: String) = remoteDataSource.cancelPayment(ticket)
}
