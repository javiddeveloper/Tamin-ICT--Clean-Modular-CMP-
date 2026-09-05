package com.tamin.taminhamrah.dataSource.calculateWagePension

import com.tamin.taminhamrah.apiService.calculateWagePension.CalculateWagePensionApiService
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class CalculateWagePensionRemoteDataSourceImpl(
    private val apiService: CalculateWagePensionApiService,
    private val errorParser: ErrorParser
) : CalculateWagePensionRemoteDataSource {

    override suspend fun getPersonalInfo(): MultipleWorkshopPersonalInfoDTO {
        return try {
            apiService.getPersonalInfo().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun isMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): MultipleWorkshopResultDTO {
        return try {
            apiService.isMultipleWorkshops(branchCode, insuranceNumber).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun calculateMultipleWorkshops(
        branchCode: String,
        insuranceNumber: String
    ): MultipleWorkshopResultDTO {
        return try {
            apiService.calculateMultipleWorkshops(branchCode, insuranceNumber).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
