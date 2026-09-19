package com.tamin.taminhamrah.dataSource.paymentSource

import com.tamin.taminhamrah.model.payment.PaymentInfoDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkRequestDTO

/**
 * The gateway's three calls, one level up from Ktorfit.
 *
 * Two implementations exist behind this: the real one, and a fake selected from Developer Options
 * — see [PaymentGatewayRemoteDataSourceSelector].
 */
interface PaymentGatewayRemoteDataSource {

    suspend fun getPaymentInfo(ticket: String): PaymentInfoDTO

    suspend fun createPaymentLink(ticket: String, request: PaymentLinkRequestDTO): PaymentLinkDTO

    suspend fun cancelPayment(ticket: String)
}
