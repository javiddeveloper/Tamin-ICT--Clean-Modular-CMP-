package com.tamin.taminhamrah.dataSource.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO

interface CalculateWagePensionRemoteDataSource {
    suspend fun getPersonalInfo(): MultipleWorkshopPersonalInfoDTO
    suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): MultipleWorkshopResultDTO

    suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): MultipleWorkshopResultDTO
}
