package com.tamin.taminhamrah.repository.treatment

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow

interface TreatmentRepository {
    suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>>

    suspend fun getElectronicPrescriptionList(
        requestTypeId: String,
        nationalCode: String,
        dependantUserNationalCode: String,
        startDate: String,
        endDate: String,
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<ElectronicPrescriptionDN>>

    suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String,
        nationalCode: String,
        childNationalCode: String,
        flagSata: String,
        type: String,
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<ElectronicPrescriptionDetailDN>>

    suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String,
        nationalCode: String,
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<ElectronicPrescriptionPriceDN>>

    suspend fun getDependantUnderEighteen(
        nationalCode: String,
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<DependantUserUnderEighteenDN>>

    suspend fun getPrescriptionPdfFile(prescriptionID: String): Flow<PdfDownloadDN>

    suspend fun downloadTestResultPdf(
        patientID: String?,
        noteHeadEprescID: String?,
        currentUserNationalCode: String?
    ): Flow<PdfDownloadDN>
}
