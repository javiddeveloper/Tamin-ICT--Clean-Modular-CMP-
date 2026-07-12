package com.tamin.taminhamrah.data.repository.treatment

import com.tamin.taminhamrah.data.local.dao.TreatmentDao
import com.tamin.taminhamrah.data.mapper.treatment.toDomain
import com.tamin.taminhamrah.data.mapper.treatment.toEntity
import com.tamin.taminhamrah.dataSource.treatment.TreatmentRemoteDataSource
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import com.tamin.taminhamrah.data.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class TreatmentRepositoryImpl(
    private val treatmentRemoteDataSource: TreatmentRemoteDataSource,
    private val treatmentDao: TreatmentDao
) : TreatmentRepository {

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> = flow {
        val localDeservedTreatment = treatmentDao.getDeservedTreatment(nationalCode).first()
        if (localDeservedTreatment.isNotEmpty()) {
            emit(localDeservedTreatment.map { it.toDomain() })
        }
        try {
            val result = treatmentRemoteDataSource.getDeservedTreatment(nationalCode)
            val remote = result?.list?.map { it.toDomain() } ?: emptyList()
            treatmentDao.clearDeservedTreatment(nationalCode)
            treatmentDao.insertDeservedTreatment(remote.map { it.toEntity(nationalCode) })
        } catch (e: Exception) {
            if (localDeservedTreatment.isEmpty()) throw e
        }
        emitAll(treatmentDao.getDeservedTreatment(nationalCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getElectronicPrescriptionList(
        requestTypeId: String,
        nationalCode: String,
        dependantUserNationalCode: String,
        startDate: String,
        endDate: String,
        filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionDN>> = flow {
        val localElectronicPrescriptionList = treatmentDao.getElectronicPrescriptions(dependantUserNationalCode).first()
        if (localElectronicPrescriptionList.isNotEmpty()) {
            emit(localElectronicPrescriptionList.map { it.toDomain() })
        }
        try {
            val result = treatmentRemoteDataSource.getElectronicPrescriptionList(
                requestTypeId,
                nationalCode,
                dependantUserNationalCode,
                startDate,
                endDate,
                ApiQueryParamDN(filters = filters)
            )
            val remote = result?.list?.map { it.toDomain() } ?: emptyList()
            treatmentDao.clearElectronicPrescriptions(dependantUserNationalCode)
            treatmentDao.insertElectronicPrescriptions(remote.map { it.toEntity(dependantUserNationalCode) })
        } catch (e: Exception) {
            if (localElectronicPrescriptionList.isEmpty()) throw e
        }
        emitAll(treatmentDao.getElectronicPrescriptions(dependantUserNationalCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getElectronicPrescriptionDetail(
        noteHeadID: String,
        nationalCode: String,
        childNationalCode: String,
        flagSata: String,
        type: String,
        filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionDetailDN>> = flow {
        val localElectronicPrescriptionDetail = treatmentDao.getElectronicPrescriptionDetails(noteHeadID).first()
        if (localElectronicPrescriptionDetail.isNotEmpty()) {
            emit(localElectronicPrescriptionDetail.map { it.toDomain() })
        }
        try {
            val result = treatmentRemoteDataSource.getElectronicPrescriptionDetail(
                noteHeadID,
                nationalCode,
                childNationalCode,
                flagSata,
                type,
                ApiQueryParamDN(filters = filters)
            )
            val remote = result?.list?.map { it.toDomain() } ?: emptyList()
            treatmentDao.clearElectronicPrescriptionDetails(noteHeadID)
            treatmentDao.insertElectronicPrescriptionDetails(remote.map { it.toEntity(noteHeadID) })
        } catch (e: Exception) {
            if (localElectronicPrescriptionDetail.isEmpty()) throw e
        }
        emitAll(treatmentDao.getElectronicPrescriptionDetails(noteHeadID).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getElectronicPrescriptionPrice(
        noteHeadID: String,
        nationalCode: String,
        filters: List<ApiFilterDN>
    ): Flow<List<ElectronicPrescriptionPriceDN>> = flow {
        val localElectronicPrescriptionPrice = treatmentDao.getElectronicPrescriptionPrices(noteHeadID).first()
        if (localElectronicPrescriptionPrice.isNotEmpty()) {
            emit(localElectronicPrescriptionPrice.map { it.toDomain() })
        }
        try {
            val result = treatmentRemoteDataSource.getElectronicPrescriptionPrice(
                noteHeadID,
                nationalCode,
                ApiQueryParamDN(filters = filters)
            )
            val remote = result?.list?.map { it.toDomain() } ?: emptyList()
            treatmentDao.clearElectronicPrescriptionPrices(noteHeadID)
            treatmentDao.insertElectronicPrescriptionPrices(remote.map { it.toEntity(noteHeadID) })
        } catch (e: Exception) {
            if (localElectronicPrescriptionPrice.isEmpty()) throw e
        }
        emitAll(treatmentDao.getElectronicPrescriptionPrices(noteHeadID).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        filters: List<ApiFilterDN>
    ): Flow<List<DependantUserUnderEighteenDN>> = flow {
        val localDependantUnderEighteen = treatmentDao.getDependantsUnderEighteen(nationalCode).first()
        if (localDependantUnderEighteen.isNotEmpty()) {
            emit(localDependantUnderEighteen.map { it.toDomain() })
        }
        try {
            val result = treatmentRemoteDataSource.getDependantUnderEighteen(
                nationalCode,
                ApiQueryParamDN(filters = filters)
            )
            val remote = result?.list?.map { it.toDomain() } ?: emptyList()
            treatmentDao.clearDependantsUnderEighteen(nationalCode)
            treatmentDao.insertDependantsUnderEighteen(remote.map { it.toEntity(nationalCode) })
        } catch (e: Exception) {
            if (localDependantUnderEighteen.isEmpty()) throw e
        }
        emitAll(treatmentDao.getDependantsUnderEighteen(nationalCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

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
