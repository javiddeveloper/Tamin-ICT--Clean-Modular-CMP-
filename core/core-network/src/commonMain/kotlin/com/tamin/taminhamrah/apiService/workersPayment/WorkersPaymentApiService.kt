package com.tamin.taminhamrah.apiService.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitRequestDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDataDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.json.JsonElement

internal interface WorkersPaymentApiService {

    /** Construction-worker premium/penalty items the insured can pay. Auth header is added by the Ktor Auth plugin. */
    @GET("workers/payment-info")
    suspend fun getWorkersPaymentInfo(): BaseDTO<WorkersPaymentInfoDataDTO>

    /**
     * Creates a payment ticket for the selected item(s).
     * @param redirectUrl app deep-link the payment gateway returns to; sent as `?type=`.
     */
    @POST("workers/payDebit")
    suspend fun payWorkersDebit(
        @Body body: WorkersPayDebitRequestDTO,
        @Query("type") redirectUrl: String,
    ): BaseDTO<WorkersPayDebitDTO>

    /** Verifies a payment after the gateway callback. */
    @GET("workers/inpectTicket")
    suspend fun inspectTicket(
        @Query("ticket") ticket: String?,
        @Query("paymentInfo") paymentInfo: String?,
    ): BaseDTO<JsonElement?>
}
