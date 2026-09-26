package com.tamin.taminhamrah.dataSource.requestPaymentForIllDays

import com.tamin.taminhamrah.tools.safeCall
import com.tamin.taminhamrah.apiService.requestPaymentForIllDays.RequestPaymentForIllDaysApiService
import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultListDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessResponseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData

class RequestPaymentForIllDaysRemoteDataSourceImpl(
    private val apiService: RequestPaymentForIllDaysApiService,
    private val errorParser: ErrorParser,
) : RequestPaymentForIllDaysRemoteDataSource {

    override suspend fun getLatestInsuranceInfo(): IllDaysInsuredMainInfoDTO {
        return errorParser.safeCall("getLatestInsuranceInfo") {
            apiService.getLatestInsuranceInfo().extractData()
        }
    }

    override suspend fun getCovidResult(): CovidResultListDTO {
        return errorParser.safeCall("getCovidResult") {
            apiService.getCovidResult().extractData()
        }
    }

    override suspend fun calcIllness(
        startDateTimeStamp: String,
        endDateTimeStamp: String,
        maritalStatus: String,
    ): List<String>? {
        return errorParser.safeCall("calcIllness") {
            apiService.calcIllness(
                startDateTimeStamp = startDateTimeStamp,
                endDateTimeStamp = endDateTimeStamp,
                maritalStatus = maritalStatus,
            ).extractData()
        }
    }

    override suspend fun sendRequestForIllDay(
        request: SaveShortTermIllnessRequestDTO
    ): SaveShortTermIllnessResponseDTO {
        return errorParser.safeCall("sendRequestForIllDay") {
            apiService.sendRequestForIllDay(request).extractData()
        }
    }
}
