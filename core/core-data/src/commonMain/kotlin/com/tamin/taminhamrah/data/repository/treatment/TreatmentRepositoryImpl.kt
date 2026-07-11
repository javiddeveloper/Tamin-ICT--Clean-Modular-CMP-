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

    override suspend fun getTreatmentCosts(
        filters: List<ApiFilterDN>
    ): Flow<List<TreatmentCostDN>> = flow {
        val result = treatmentRemoteDataSource.getTreatmentCosts(
            ApiQueryParamDN(filters = filters)
        )
        emit(result?.list?.map { it.toDomain() } ?: emptyList())
    }

    override suspend fun getTreatmentCostsPDF(repId: String): Flow<PdfDownloadDN> = flow {
        val result = treatmentRemoteDataSource.getTreatmentCostsPDF(repId)
        emit(result.toDomain())
    }

    override suspend fun sendToInboxTreatmentCosts(repId: String): Flow<String> = flow {
        val result = treatmentRemoteDataSource.sendToInboxTreatmentCosts(repId)
        emit(result)
    }
}
