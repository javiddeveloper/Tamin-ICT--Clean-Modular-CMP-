package com.tamin.taminhamrah.repository.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDN
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDN
import kotlinx.coroutines.flow.Flow

interface CalculateWagePensionRepository {
    suspend fun getPersonalInfo(): Flow<MultipleWorkshopPersonalInfoDN>

    suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): Flow<MultipleWorkshopResultDN>

    suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): Flow<MultipleWorkshopResultDN>
}
