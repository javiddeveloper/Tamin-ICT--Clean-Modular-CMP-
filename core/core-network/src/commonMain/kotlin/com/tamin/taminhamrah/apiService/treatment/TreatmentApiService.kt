package com.tamin.taminhamrah.apiService.treatment

import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDTO
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationDTO
import com.tamin.taminhamrah.model.treatment.TreatmentCostDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import io.ktor.client.statement.HttpStatement

internal interface TreatmentApiService {

    @GET("booklet-req/lackEntitlement/{nationalCode}")
    suspend fun getDeservedTreatment(
        @Path("nationalCode") nationalCode: String,
    ): BaseDTO<ListData<DeservedTreatmentDTO>>

    @GET("patient-history/{nationalCode}/{dependantUserNationalCode}/{requestType}/{startDate}/{endDate}")
    suspend fun getElectronicPrescriptionList(
        @Path("requestType") requestTypeId: String,
        @Path("nationalCode") nationalCode: String,
        @Path("dependantUserNationalCode") dependantUserNationalCode: String,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String,
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ElectronicPrescriptionDTO>>

    @GET("patient-history/detail/{noteHeadID}/{nationalCode}/{childNationalCode}/{type}/{flagSata}")
    suspend fun getElectronicPrescriptionDetail(
        @Path("noteHeadID") noteHeadID: String,
        @Path("nationalCode") nationalCode: String,
        @Path("childNationalCode") childNationalCode: String,
        @Path("flagSata") flagSata: String,
        @Path("type") type: String,
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ElectronicPrescriptionDetailDTO>>

    @GET("patient-history/price/{noteHeadID}/{nationalCode}")
    suspend fun getElectronicPrescriptionPrice(
        @Path("noteHeadID") noteHeadID: String,
        @Path("nationalCode") nationalCode: String,
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ElectronicPrescriptionPriceDTO>>

    @GET("patient-history/get-dependent-children/{nationalCode}")
    suspend fun getDependantUnderEighteen(
        @Path("nationalCode") nationalCode: String,
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<DependantUserUnderEighteenDTO>>

    @GET("patient-history/reports-prescription-PDF/{prescriptionID}")
    suspend fun getPrescriptionPdfFile(
        @Path("prescriptionID") prescriptionID: String
    ): HttpStatement

    @GET("patient-history/lab-result-PDF/{patientID}/{noteHeadEprescID}/{currentUserNationalCode}")
    suspend fun downloadLabResultPdf(
        @Path("patientID") patientID: String = "",
        @Path("noteHeadEprescID") noteHeadEprescID: String = "",
        @Path("currentUserNationalCode") currentUserNationalCode: String = ""
    ): HttpStatement

    @GET("health/tcr-price-certificate")
    suspend fun getTreatmentCosts(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<TreatmentCostDTO>>

    @GET("health/tcr-price-certificate/report/{repId}")
    suspend fun getTreatmentCostsPDF(
        @Path("repId") repId: String
    ): HttpStatement

    @GET("health/tcr-price-certificate/announcement/{repId}")
    suspend fun sendToInboxTreatmentCosts(
        @Path("repId") repId: String
    ): BaseDTO<String>

    /**
     * `confrimation` is misspelt **on the server**. Leave it exactly as written -- correcting it
     * to `confirmation` gives a 404. The query keys this takes (`page`, `start`, `limit`,
     * `filter`, `sort`) are the ones `ApiQueryBuilder` already emits.
     */
    @GET("shortterm-request/commission-confrimation")
    suspend fun getMedicalConfirmations(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<MedicalConfirmationDTO>>

    /**
     * **Unverified route.** The old app has no certificate download for medical confirmations --
     * `shortterm-request` exposes no `report`/`announcement` sibling, and the list payload carries
     * no `repId` to address a row with. The path below is modeled on the tcr-price-certificate
     * pair, which is a guess, not a contract. The UI keeps both actions hidden until a row
     * actually arrives with a `repId`, so nothing calls this until the service grows one.
     */
    @GET("shortterm-request/commission-confrimation/report/{repId}")
    suspend fun getMedicalConfirmationPdf(
        @Path("repId") repId: String
    ): HttpStatement

    /** Unverified route -- see [getMedicalConfirmationPdf]. */
    @GET("shortterm-request/commission-confrimation/announcement/{repId}")
    suspend fun sendToInboxMedicalConfirmation(
        @Path("repId") repId: String
    ): BaseDTO<String>
}
