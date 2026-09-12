package com.tamin.taminhamrah.apiService.payment

import com.tamin.taminhamrah.model.payment.PaymentInfoDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkRequestDTO
import com.tamin.taminhamrah.tools.BaseDTO
import kotlinx.serialization.json.JsonElement
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

/**
 * The payment gateway (TFH), on its own host — see `BaseUrlKey.TFH`.
 *
 * These three calls are the whole of it. Every "pay" endpoint in the app, whichever service owns
 * it, ends by handing a ticket to this interface.
 */
interface PaymentGatewayApiService {

    /** Amount, reason, remaining validity and status of one ticket. */
    @GET("ticket/current-user/{ticket}")
    suspend fun getPaymentInfo(@Path("ticket") ticket: String): BaseDTO<PaymentInfoDTO>

    /** Exchanges a ticket plus a payer for the page the user pays on. */
    @POST("payment-link/{ticket}")
    suspend fun createPaymentLink(
        @Path("ticket") ticket: String,
        @Body request: PaymentLinkRequestDTO,
    ): BaseDTO<PaymentLinkDTO>

    /** Releases a ticket the user backed out of. */
    @GET("cancel-by-user/{ticket}")
    suspend fun cancelPayment(@Path("ticket") ticket: String): BaseDTO<JsonElement?>
}
