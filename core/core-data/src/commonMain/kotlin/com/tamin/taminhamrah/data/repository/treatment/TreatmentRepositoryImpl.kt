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
        val local = treatmentDao.getDeservedTreatment(nationalCode).first()
        if (local.isNotEmpty()) {
            emit(local.map { it.toDomain() })
        }
        try {
            val result = treatmentRemoteDataSource.getDeservedTreatment(nationalCode)
            val remote = result?.list?.map { it.toDomain() } ?: emptyList()
            treatmentDao.clearDeservedTreatment(nationalCode)
            treatmentDao.insertDeservedTreatment(remote.map { it.toEntity(nationalCode) })
        } catch (e: Exception) {
            if (local.isEmpty()) throw e
        }
        emitAll(treatmentDao.getDeservedTreatment(nationalCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        filters: List<ApiFilterDN>
    ): Flow<List<DependantUserUnderEighteenDN>> = flow {
        val local = treatmentDao.getDependantsUnderEighteen(nationalCode).first()
        if (local.isNotEmpty()) {
            emit(local.map { it.toDomain() })
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
            if (local.isEmpty()) throw e
        }
        emitAll(treatmentDao.getDependantsUnderEighteen(nationalCode).map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getTreatmentCosts(
        filters: List<ApiFilterDN>
    ): Flow<List<TreatmentCostDN>> = flow {
        val local = treatmentDao.getTreatmentCosts().first()
        if (local.isNotEmpty()) {
            emit(local.map { it.toDomain() })
        }
        try {
            val result = treatmentRemoteDataSource.getTreatmentCosts(
                ApiQueryParamDN(filters = filters)
            )
            val remote = result?.list?.map { it.toDomain() } ?: emptyList()
            treatmentDao.clearTreatmentCosts()
            treatmentDao.insertTreatmentCosts(remote.map { it.toEntity() })
        } catch (e: Exception) {
            if (local.isEmpty()) throw e
        }
        emitAll(treatmentDao.getTreatmentCosts().map { list -> list.map { it.toDomain() } })
    }.distinctUntilChanged()

    override suspend fun getTreatmentCostsPDF(repId: String): Flow<PdfDownloadDN> = flow {
        val result = treatmentRemoteDataSource.getTreatmentCostsPDF(repId)
        emit(result.toDomain())
    }

    override suspend fun sendToInboxTreatmentCosts(repId: String): Flow<String> = flow {
        val result = treatmentRemoteDataSource.sendToInboxTreatmentCosts(repId)
        emit(result)
    }
}
