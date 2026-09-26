package com.tamin.taminhamrah.dataSource.calculateWagePension

import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.calculateWagePension.CalculateWagePensionApiService
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData

class CalculateWagePensionRemoteDataSourceImpl(
    private val apiService: CalculateWagePensionApiService,
    private val errorParser: ErrorParser
) : CalculateWagePensionRemoteDataSource {

    override suspend fun getPersonalInfo(): MultipleWorkshopPersonalInfoDTO {
        return errorParser.safeCall("getPersonalInfo") {
            apiService.getPersonalInfo().extractData()
        }
    }

    override suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): MultipleWorkshopResultDTO {
        return errorParser.safeCall("isMultipleWorkshops") {
            apiService.isMultipleWorkshops(branchCode, insuranceNumber).extractData()
        }
    }

    override suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): MultipleWorkshopResultDTO {
        return errorParser.safeCall("calculateMultipleWorkshops") {
            apiService.calculateMultipleWorkshops(branchCode, insuranceNumber).extractData()
        }
    }
}
