package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTreatmentRepository : TreatmentRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Treatment Repository Error")

    var getDeservedTreatmentResult: List<DeservedTreatmentDN> = emptyList()
    var getElectronicPrescriptionListResult: List<ElectronicPrescriptionDN> = emptyList()
    var getElectronicPrescriptionDetailResult: List<ElectronicPrescriptionDetailDN> = emptyList()
    var getElectronicPrescriptionPriceResult: List<ElectronicPrescriptionPriceDN> = emptyList()
    var getDependantUnderEighteenResult: List<DependantUserUnderEighteenDN> = emptyList()
    var getPrescriptionPdfFileResult: PdfDownloadDN = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
    var downloadLabResultPdfResult: PdfDownloadDN = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
    var getTreatmentCostsResult: List<TreatmentCostDN> = emptyList()
    var getTreatmentCostsPDFResult: PdfDownloadDN = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
    var sendToInboxTreatmentCostsResult: String = ""

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> = flow {
        if (shouldThrowError) throw error
        emit(getDeservedTreatmentResult)
    }

    override suspend fun getElectronicPrescriptionList(
        requestTypeId: String, nationalCode: String, patientNationalCode: String,
        startDate: String, endDate: String
    ): Flow<List<ElectronicPrescriptionDN>> = flow {
        if (shouldThrowError) throw error
        emit(getElectronicPrescriptionListResult)
    }

    override suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String, nationalCode: String, patientNationalCode: String,
        flagSata: String, type: String
    ): Flow<List<ElectronicPrescriptionDetailDN>> = flow {
        if (shouldThrowError) throw error
        emit(getElectronicPrescriptionDetailResult)
    }

    override suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String, nationalCode: String
    ): Flow<List<ElectronicPrescriptionPriceDN>> = flow {
        if (shouldThrowError) throw error
        emit(getElectronicPrescriptionPriceResult)
    }

    override suspend fun getDependantUnderEighteen(
        nationalCode: String
    ): Flow<List<DependantUserUnderEighteenDN>> = flow {
        if (shouldThrowError) throw error
        emit(getDependantUnderEighteenResult)
    }

    override suspend fun getPrescriptionPdfFile(prescriptionID: String): Flow<PdfDownloadDN> = flow {
        if (shouldThrowError) throw error
        emit(getPrescriptionPdfFileResult)
    }

    override suspend fun downloadLabResultPdf(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?
    ): Flow<PdfDownloadDN> = flow {
        if (shouldThrowError) throw error
        emit(downloadLabResultPdfResult)
    }

    override suspend fun getTreatmentCosts(filters: List<ApiFilterDN>): Flow<List<TreatmentCostDN>> = flow {
        if (shouldThrowError) throw error
        emit(getTreatmentCostsResult)
    }

    override suspend fun getTreatmentCostsPDF(repId: String): Flow<PdfDownloadDN> = flow {
        if (shouldThrowError) throw error
        emit(getTreatmentCostsPDFResult)
    }

    override suspend fun sendToInboxTreatmentCosts(repId: String): Flow<String> = flow {
        if (shouldThrowError) throw error
        emit(sendToInboxTreatmentCostsResult)
    }
}
