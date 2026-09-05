package com.tamin.taminhamrah.repository.treatment

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import kotlinx.coroutines.flow.Flow

interface TreatmentRepository {
    suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>>

    /**
     * [patientNationalCode] is the person whose records are wanted — pass [nationalCode] itself for
     * the insured, or a dependant's code. The impl encodes "self" as the endpoint expects.
     */
    suspend fun getElectronicPrescriptionList(
        requestTypeId: String,
        nationalCode: String,
        patientNationalCode: String,
        startDate: String,
        endDate: String
    ): Flow<List<ElectronicPrescriptionDN>>

    /**
     * [patientNationalCode] follows the same rule as [getElectronicPrescriptionList]; [flagSata]
     * may be blank and the impl sends the value the endpoint expects for "absent".
     */
    suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String,
        nationalCode: String,
        patientNationalCode: String,
        flagSata: String,
        type: String
    ): Flow<List<ElectronicPrescriptionDetailDN>>

    suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String,
        nationalCode: String
    ): Flow<List<ElectronicPrescriptionPriceDN>>

    suspend fun getDependantUnderEighteen(
        nationalCode: String
    ): Flow<List<DependantUserUnderEighteenDN>>

    suspend fun getPrescriptionPdfFile(prescriptionID: String): Flow<PdfDownloadDN>

    suspend fun downloadLabResultPdf(
        patientID: String?,
        noteHeadEprescID: String?,
        currentUserNationalCode: String?
    ): Flow<PdfDownloadDN>

    /**
     * The «خسارت متفرقه» certificates. Paging and filters are decided in the implementation, as
     * with every other treatment list — the use case only asks for them.
     */
    suspend fun getTreatmentCosts(): Flow<List<TreatmentCostDN>>

    suspend fun getTreatmentCostsPDF(repId: String): Flow<PdfDownloadDN>

    /** Queues the certificate for the person's inbox; emits the service's acknowledgement. */
    suspend fun sendToInboxTreatmentCosts(repId: String): Flow<String>

    suspend fun getMedicalConfirmations(): Flow<List<MedicalConfirmationDN>>

    suspend fun getMedicalConfirmationPdf(repId: String): Flow<PdfDownloadDN>

    suspend fun sendToInboxMedicalConfirmation(repId: String): Flow<String>
}
