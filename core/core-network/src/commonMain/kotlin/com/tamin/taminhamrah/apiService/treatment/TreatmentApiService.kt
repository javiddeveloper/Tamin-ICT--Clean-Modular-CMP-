package com.tamin.taminhamrah.apiService.treatment

import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDTO
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
}
