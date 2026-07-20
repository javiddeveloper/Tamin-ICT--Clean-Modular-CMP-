package com.tamin.taminhamrah.data.repository.treatment

import com.tamin.taminhamrah.data.local.dao.TreatmentDao
import com.tamin.taminhamrah.data.mapper.treatment.toDomain
import com.tamin.taminhamrah.data.mapper.treatment.toEntity
import com.tamin.taminhamrah.dataSource.treatment.TreatmentRemoteDataSource
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
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
}
