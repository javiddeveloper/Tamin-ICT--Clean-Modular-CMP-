package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTreatmentRepository : TreatmentRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Treatment Repository Error")

    var getDeservedTreatmentResult: List<DeservedTreatmentDN> = emptyList()
    var getDependantUnderEighteenResult: List<DependantUserUnderEighteenDN> = emptyList()

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> = flow {
        if (shouldThrowError) throw error
        emit(getDeservedTreatmentResult)
    }

    override suspend fun getDependantUnderEighteen(
        nationalCode: String, filters: List<ApiFilterDN>
    ): Flow<List<DependantUserUnderEighteenDN>> = flow {
        if (shouldThrowError) throw error
        emit(getDependantUnderEighteenResult)
    }
}
