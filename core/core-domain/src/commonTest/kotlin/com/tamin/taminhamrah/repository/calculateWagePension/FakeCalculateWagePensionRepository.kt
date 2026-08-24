package com.tamin.taminhamrah.repository.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDN
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCalculateWagePensionRepository : CalculateWagePensionRepository {
    var personalInfoResult = MultipleWorkshopPersonalInfoDN(
        branchCode = "12345",
        insuranceNumber = "9876543210",
        branch = "Tehran Main"
    )
    var isMultipleResult = MultipleWorkshopResultDN(result = 1)
    var calculateResult = MultipleWorkshopResultDN(result = 25_000_000)
    var shouldThrowError: Boolean = false
    var error: Throwable? = null
    var lastBranchCode: String? = null
    var lastInsuranceNumber: String? = null

    override suspend fun getPersonalInfo(): Flow<MultipleWorkshopPersonalInfoDN> = flow {
        if (shouldThrowError) throw error!!
        emit(personalInfoResult)
    }

    override suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): Flow<MultipleWorkshopResultDN> = flow {
        lastBranchCode = branchCode
        lastInsuranceNumber = insuranceNumber
        if (shouldThrowError) throw error!!
        emit(isMultipleResult)
    }

    override suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): Flow<MultipleWorkshopResultDN> = flow {
        lastBranchCode = branchCode
        lastInsuranceNumber = insuranceNumber
        if (shouldThrowError) throw error!!
        emit(calculateResult)
    }
}
