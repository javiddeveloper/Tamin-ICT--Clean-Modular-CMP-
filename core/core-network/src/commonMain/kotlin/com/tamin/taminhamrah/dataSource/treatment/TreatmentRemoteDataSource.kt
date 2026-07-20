package com.tamin.taminhamrah.dataSource.treatment

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.utils.ListData

interface TreatmentRemoteDataSource {
    suspend fun getDeservedTreatment(nationalCode: String): ListData<DeservedTreatmentDTO>?

    suspend fun getElectronicPrescriptionList(
        requestTypeId: String,
        nationalCode: String,
        dependantUserNationalCode: String,
        startDate: String,
        endDate: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionDTO>?

    suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String,
        nationalCode: String,
        childNationalCode: String,
        flagSata: String,
        type: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionDetailDTO>?

    suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String,
        nationalCode: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionPriceDTO>?

    suspend fun getDependantUnderEighteen(
        nationalCode: String,
        params: Map<String, String>
    ): ListData<DependantUserUnderEighteenDTO>?

    suspend fun getPrescriptionPdfFile(prescriptionID: String): PdfDownloadDTO

    suspend fun downloadTestResultPdf(
        patientID: String?,
        noteHeadEprescID: String?,
        currentUserNationalCode: String?
    ): PdfDownloadDTO
}
