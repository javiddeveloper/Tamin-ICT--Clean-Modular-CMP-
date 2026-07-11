package com.tamin.taminhamrah.dataSource.treatment

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface TreatmentRemoteDataSource {
    suspend fun getDeservedTreatment(nationalCode: String): ListData<DeservedTreatmentDTO>?

    suspend fun getElectronicPrescriptionList(
        requestTypeId: String,
        nationalCode: String,
        dependantUserNationalCode: String,
        startDate: String,
        endDate: String,
        query: ApiQueryParamDN
    ): ListData<ElectronicPrescriptionDTO>?

    suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String,
        nationalCode: String,
        childNationalCode: String,
        flagSata: String,
        type: String,
        query: ApiQueryParamDN
    ): ListData<ElectronicPrescriptionDetailDTO>?

    suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String,
        nationalCode: String,
        query: ApiQueryParamDN
    ): ListData<ElectronicPrescriptionPriceDTO>?

    suspend fun getDependantUnderEighteen(
        nationalCode: String,
        query: ApiQueryParamDN
    ): ListData<DependantUserUnderEighteenDTO>?

    suspend fun getPrescriptionPdfFile(prescriptionID: String): PdfDownloadDTO

    suspend fun downloadTestResultPdf(
        patientID: String?,
        noteHeadEprescID: String?,
        currentUserNationalCode: String?
    ): PdfDownloadDTO
}
