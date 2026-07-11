package com.tamin.taminhamrah.data.repository.treatment

import com.tamin.taminhamrah.dataSource.treatment.TreatmentRemoteDataSource
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.data.mapper.treatment.*
import com.tamin.taminhamrah.data.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class TreatmentRepositoryImpl(
    private val treatmentRemoteDataSource: TreatmentRemoteDataSource
) : TreatmentRepository {

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> = flow {
        val result = treatmentRemoteDataSource.getDeservedTreatment(nationalCode)
        emit(result?.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getElectronicPrescriptionList(
        requestTypeId: String,
        nationalCode: String,
        dependantUserNationalCode: String,
        startDate: String,
        endDate: String,
        filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionDN>> = flow {
        val result = treatmentRemoteDataSource.getElectronicPrescriptionList(
            requestTypeId,
            nationalCode,
            dependantUserNationalCode,
            startDate,
            endDate,
            ApiQueryParamDN(filters = filters)
        )
        emit(result?.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String,
        nationalCode: String,
        childNationalCode: String,
        flagSata: String,
        type: String,
        filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionDetailDN>> = flow {
        val result = treatmentRemoteDataSource.getElectronicPrescriptionDetail(
            noteHeadID,
            nationalCode,
            childNationalCode,
            flagSata,
            type,
            ApiQueryParamDN(filters = filters)
        )
        emit(result?.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String,
        nationalCode: String,
        filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionPriceDN>> = flow {
        val result = treatmentRemoteDataSource.getElectronicPrescriptionPrice(
            noteHeadID,
            nationalCode,
            ApiQueryParamDN(filters = filters)
        )
        emit(result?.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        filters: List<ApiFilterDN>
    ): Flow<List<DependantUserUnderEighteenDN>> = flow {
        val result = treatmentRemoteDataSource.getDependantUnderEighteen(
            nationalCode,
            ApiQueryParamDN(filters = filters)
        )
        emit(result?.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getPrescriptionPdfFile(prescriptionID: String): Flow<PdfDownloadDN> = flow {
        val result = treatmentRemoteDataSource.getPrescriptionPdfFile(prescriptionID)
        emit(result.toDomain())
    }

    override suspend fun downloadTestResultPdf(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?
    ): Flow<PdfDownloadDN> = flow {
        val result = treatmentRemoteDataSource.downloadTestResultPdf(patientID, noteHeadEprescID, currentUserNationalCode)
        emit(result.toDomain())
    }
}
