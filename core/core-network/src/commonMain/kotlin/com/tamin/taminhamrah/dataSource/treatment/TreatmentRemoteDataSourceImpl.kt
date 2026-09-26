package com.tamin.taminhamrah.dataSource.treatment

import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.treatment.TreatmentApiService
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDTO
import com.tamin.taminhamrah.model.treatment.TreatmentCostDTO
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationDTO
import com.tamin.taminhamrah.model.utils.ListData

import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.readPdfChannel

internal class TreatmentRemoteDataSourceImpl(
    private val apiService: TreatmentApiService,
    private val errorParser: ErrorParser
) : TreatmentRemoteDataSource {

    override suspend fun getDeservedTreatment(nationalCode: String): ListData<DeservedTreatmentDTO> {
        return errorParser.safeCall("getDeservedTreatment") {
            val response = apiService.getDeservedTreatment(nationalCode = nationalCode)
            response.extractData()
        }
    }

    override suspend fun getElectronicPrescriptionList(
        requestTypeId: String,
        nationalCode: String,
        dependantUserNationalCode: String,
        startDate: String,
        endDate: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionDTO> {
        return errorParser.safeCall("getElectronicPrescriptionList") {
            val response = apiService.getElectronicPrescriptionList(
                requestTypeId, nationalCode, dependantUserNationalCode, startDate, endDate, params
            )
            response.extractData()
        }
    }

    override suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String,
        nationalCode: String,
        childNationalCode: String,
        flagSata: String,
        type: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionDetailDTO> {
        return errorParser.safeCall("getElectronicPrescriptionDetail") {
            val response = apiService.getElectronicPrescriptionDetail(
                noteHeadID, nationalCode, childNationalCode, flagSata, type, params
            )
            response.extractData()
        }
    }

    override suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String,
        nationalCode: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionPriceDTO> {
        return errorParser.safeCall("getElectronicPrescriptionPrice") {
            val response = apiService.getElectronicPrescriptionPrice(noteHeadID, nationalCode, params)
            response.extractData()
        }
    }

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        params: Map<String, String>
    ): ListData<DependantUserUnderEighteenDTO> {
        return errorParser.safeCall("getDependantUnderEighteen") {
            val response = apiService.getDependantUnderEighteen(nationalCode, params)
            response.extractData()
        }
    }

    override suspend fun getPrescriptionPdfFile(prescriptionID: String): PdfDownloadDTO {
        return errorParser.safeCall("getPrescriptionPdfFile") {
            PdfDownloadDTO(pdf = InputStreamDTO(pdf = apiService.getPrescriptionPdfFile(prescriptionID).readPdfChannel()))
        }
    }

    override suspend fun downloadLabResultPdf(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?
    ): PdfDownloadDTO {
        return errorParser.safeCall("downloadLabResultPdf") {
            val statement = apiService.downloadLabResultPdf(
                patientID ?: "", noteHeadEprescID ?: "", currentUserNationalCode ?: ""
            )
            PdfDownloadDTO(pdf = InputStreamDTO(pdf = statement.readPdfChannel()))
        }
    }

    override suspend fun getTreatmentCosts(params: Map<String, String>): ListData<TreatmentCostDTO> {
        return errorParser.safeCall("getTreatmentCosts") {
            val response = apiService.getTreatmentCosts(params)
            response.extractData()
        }
    }

    override suspend fun getTreatmentCostsPDF(repId: String): PdfDownloadDTO {
        return errorParser.safeCall("getTreatmentCostsPDF") {
            // Drained through readPdfChannel like every other PDF here: the raw response body is a
            // single-use stream that is already closed by the time a caller reads it.
            PdfDownloadDTO(pdf = InputStreamDTO(pdf = apiService.getTreatmentCostsPDF(repId).readPdfChannel()))
        }
    }

    override suspend fun sendToInboxTreatmentCosts(repId: String): String {
        return errorParser.safeCall("sendToInboxTreatmentCosts") {
            apiService.sendToInboxTreatmentCosts(repId).extractData()
        }
    }

    override suspend fun getMedicalConfirmations(params: Map<String, String>): ListData<MedicalConfirmationDTO> {
        return errorParser.safeCall("getMedicalConfirmations") {
            val response = apiService.getMedicalConfirmations(params)
            response.extractData()
        }
    }

    override suspend fun getMedicalConfirmationPdf(repId: String): PdfDownloadDTO {
        return errorParser.safeCall("getMedicalConfirmationPdf") {
            PdfDownloadDTO(pdf = InputStreamDTO(pdf = apiService.getMedicalConfirmationPdf(repId).readPdfChannel()))
        }
    }

    override suspend fun sendToInboxMedicalConfirmation(repId: String): String {
        return errorParser.safeCall("sendToInboxMedicalConfirmation") {
            apiService.sendToInboxMedicalConfirmation(repId).extractData()
        }
    }
}
