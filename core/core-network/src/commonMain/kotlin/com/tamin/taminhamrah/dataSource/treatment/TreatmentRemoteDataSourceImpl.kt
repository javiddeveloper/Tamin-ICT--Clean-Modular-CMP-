package com.tamin.taminhamrah.dataSource.treatment

import com.tamin.taminhamrah.apiService.treatment.TreatmentApiService
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

internal class TreatmentRemoteDataSourceImpl(
    private val apiService: TreatmentApiService,
    private val errorParser: ErrorParser
) : TreatmentRemoteDataSource {

    override suspend fun getDeservedTreatment(nationalCode: String): ListData<DeservedTreatmentDTO>? {
        return try {
            val response = apiService.getDeservedTreatment(nationalCode = nationalCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getElectronicPrescriptionList(
        requestTypeId: String,
        nationalCode: String,
        dependantUserNationalCode: String,
        startDate: String,
        endDate: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionDTO>? {
        return try {
            val response = apiService.getElectronicPrescriptionList(
                requestTypeId, nationalCode, dependantUserNationalCode, startDate, endDate, params
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String,
        nationalCode: String,
        childNationalCode: String,
        flagSata: String,
        type: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionDetailDTO>? {
        return try {
            val response = apiService.getElectronicPrescriptionDetail(
                noteHeadID, nationalCode, childNationalCode, flagSata, type, params
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String,
        nationalCode: String,
        params: Map<String, String>
    ): ListData<ElectronicPrescriptionPriceDTO>? {
        return try {
            val response = apiService.getElectronicPrescriptionPrice(noteHeadID, nationalCode, params)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        params: Map<String, String>
    ): ListData<DependantUserUnderEighteenDTO>? {
        return try {
            val response = apiService.getDependantUnderEighteen(nationalCode, params)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getPrescriptionPdfFile(prescriptionID: String): PdfDownloadDTO {
        return try {
            val response = apiService.getPrescriptionPdfFile(prescriptionID)
            PdfDownloadDTO(pdf = InputStreamDTO(pdf = response.body()))
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun downloadLabResultPdf(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?
    ): PdfDownloadDTO {
        return try {
            val response = apiService.downloadLabResultPdf(
                patientID ?: "", noteHeadEprescID ?: "", currentUserNationalCode ?: ""
            )
            PdfDownloadDTO(pdf = InputStreamDTO(pdf = response.body()))
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}
