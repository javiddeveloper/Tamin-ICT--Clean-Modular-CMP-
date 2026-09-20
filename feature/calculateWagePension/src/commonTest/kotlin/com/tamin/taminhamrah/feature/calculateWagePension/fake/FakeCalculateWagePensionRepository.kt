package com.tamin.taminhamrah.feature.calculateWagePension.fake

import com.tamin.taminhamrah.model.calculateWagePension.MULTIPLE_WORKSHOPS_YES
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDN
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDN
import com.tamin.taminhamrah.repository.calculateWagePension.CalculateWagePensionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCalculateWagePensionRepository : CalculateWagePensionRepository {
    var personalInfoResult = MultipleWorkshopPersonalInfoDN(
        branchCode = "12345",
        insuranceNumber = "9876543210",
        branch = "Tehran Main",
    )
    var isMultipleResult = MultipleWorkshopResultDN(result = MULTIPLE_WORKSHOPS_YES)
    var calculateResult = MultipleWorkshopResultDN(result = 25_000_000)

    var personalInfoError: Throwable? = null
    var isMultipleError: Throwable? = null
    var calculateError: Throwable? = null

    var isMultipleCalls: Int = 0
        private set
    var calculateCalls: Int = 0
        private set

    override suspend fun getPersonalInfo(): Flow<MultipleWorkshopPersonalInfoDN> = flow {
        personalInfoError?.let { throw it }
        emit(personalInfoResult)
    }

    override suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String,
    ): Flow<MultipleWorkshopResultDN> = flow {
        isMultipleCalls++
        isMultipleError?.let { throw it }
        emit(isMultipleResult)
    }

    override suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String,
    ): Flow<MultipleWorkshopResultDN> = flow {
        calculateCalls++
        calculateError?.let { throw it }
        emit(calculateResult)
    }
}
