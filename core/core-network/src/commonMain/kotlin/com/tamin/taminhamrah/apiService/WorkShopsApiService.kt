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
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDTO
import com.tamin.taminhamrah.model.workshop.WorkshopDebitDTO
import com.tamin.taminhamrah.model.workshop.WorkshopMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberDTO
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDTO
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelDTO
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

    @GET("workshop-services/workshop-debit/{workshopId}/{branchCode}")
    suspend fun getWorkshopDebtInquiry(
        @Path("workshopId") workshopId: String,
        @Path("branchCode") branchCode: String
    ): BaseDTO<WorkshopDebtInquiryDTO>

    @GET("debit-objection/objection-workshop-debit/{workshopNumber}/{branchCode}")
    suspend fun getWorkshopObjectionableDebitList(
        @Path("workshopNumber") workshopNumber: String,
        @Path("branchCode") branchCode: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkShopDebtDTO>>

    @GET("employers")
    suspend fun getWorkshopRecentlyAddedMembers(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopNewMemberDTO>>

    @GET("debit-objection/management-workshop-debit/{workshopId}/{branchId}")
    suspend fun getWorkshopsDebtsList(
        @Path("workshopId") workshopId: String,
        @Path("branchId") branchId: String,
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopsDebtListModelDTO>>

    @GET("workshop-services/member/get-all")
    suspend fun getWorkshopMembers(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopMemberDTO>>

    @GET("workshop-services/workshop-stackholders/get-all")
    suspend fun getWorkshopStackHolders(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<WorkshopStackHolderDTO>>

}
