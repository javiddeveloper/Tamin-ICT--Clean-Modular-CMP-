package com.tamin.taminhamrah.apiService.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDTO
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDTO
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement
import kotlinx.serialization.json.JsonElement

interface ConstructionInsuranceApiService {

    @GET("bld-request-services/building-workshops/normal")
    suspend fun getConstructionFiles(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ConstructionFileDTO>>

    /** ذینفعان کارگاه — عملیات menu option "4". */
    @GET("bld-request-services/building-workshops-owners")
    suspend fun getBeneficiariesWorkshop(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<BeneficiaryConstructionDTO>>

    /** صدور و مدیریت برگه پرداخت — payment sheets already issued for a debit. */
    @GET("bld-request-services/building-payment-sheet-list-info/{debitNumber}/0/normal")
    suspend fun getPaymentSheetConstructionInfo(
        @Path("debitNumber") debitNumber: String,
    ): BaseDTO<ListData<PaymentSheetConstructionFileDTO>>

    /** مشاهده گواهی برگه پرداخت — PDF byte stream, read via [com.tamin.taminhamrah.tools.readPdfChannel]. */
    @Streaming
    @GET("bld-request-services/building-workshop-certificate-report/{debitNumber}/normal/{branchCode}")
    suspend fun getCertificatePaymentSheetPdf(
        @Path("debitNumber") debitNumber: String,
        @Path("branchCode") branchCode: String,
    ): HttpStatement

    /** صدور برگه پرداخت. */
    @PUT("bld-request-services/issuance-payment-sheet-building-workshop-request/{debitNumber}/0/normal")
    suspend fun issuancePaymentSheet(
        @Path("debitNumber") debitNumber: String,
    ): BaseDTO<JsonElement?>

    /** مدیریت پرداخت اقساط — installment (debit) letters for one workshop/branch. */
    @GET("bld-request-services/building-workshop-installment-head/{workshopId}/{branchId}")
    suspend fun getInstallmentLetterList(
        @Path("workshopId") workshopId: String,
        @Path("branchId") branchId: String,
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<InstallmentLetterDTO>>
}
