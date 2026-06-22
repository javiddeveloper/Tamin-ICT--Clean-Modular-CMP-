/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.workshop.PaymentSheetDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.http.cio.Response

internal interface WorkShopsApiService {

    @GET("workshop-services/employer/get-all-employer-agreement-by-national-id")
    suspend fun getAllEmployerAgreementByNationalId(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<EmployerAgreementDTO>>


    @GET("workshop-services/payment-sheets")
    suspend fun getWorkshopPaymentSheets(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<PaymentSheetDTO>>

    @GET("debit-online-payment/workshop-debit/{workshopId}/{branchCode}")
    suspend fun getWorkshopDebit(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopDebitDTO>>
}
