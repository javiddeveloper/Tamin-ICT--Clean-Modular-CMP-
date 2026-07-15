package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Configurable fake [TreatmentRepository] for the dashboard ViewModel tests.
 *
 * Defaults emit realistic [TreatmentTestData] so success paths work out of the box;
 * set [shouldThrowError] to drive failure paths. The error is thrown inside the emitted
 * flow so the ViewModel's `catch` handles it exactly as in production.
 */
class FakeTreatmentRepository : TreatmentRepository {

    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Treatment Repository Error")

    var deservedResult: List<DeservedTreatmentDN> = listOf(TreatmentTestData.deserved())
    var dependantResult: List<DependantUserUnderEighteenDN> = listOf(TreatmentTestData.dependant())

    private fun <T> result(value: T): Flow<T> = flow {
        if (shouldThrowError) throw error
        emit(value)
    }

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> =
        result(deservedResult)

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        filters: List<ApiFilterDN>
    ): Flow<List<DependantUserUnderEighteenDN>> = result(dependantResult)
}
