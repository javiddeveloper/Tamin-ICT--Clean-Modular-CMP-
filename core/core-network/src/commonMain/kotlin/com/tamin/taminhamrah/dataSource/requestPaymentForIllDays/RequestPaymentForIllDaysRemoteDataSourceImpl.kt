package com.tamin.taminhamrah.dataSource.requestPaymentForIllDays

import com.tamin.taminhamrah.apiService.requestPaymentForIllDays.RequestPaymentForIllDaysApiService
import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultListDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessResponseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class RequestPaymentForIllDaysRemoteDataSourceImpl(
    private val apiService: RequestPaymentForIllDaysApiService,
    private val errorParser: ErrorParser,
) : RequestPaymentForIllDaysRemoteDataSource {

    override suspend fun getLatestInsuranceInfo(): IllDaysInsuredMainInfoDTO? {
        return try {
            apiService.getLatestInsuranceInfo().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getCovidResult(): CovidResultListDTO? {
        return try {
            apiService.getCovidResult().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendRequestForIllDay(
        request: SaveShortTermIllnessRequestDTO
    ): SaveShortTermIllnessResponseDTO? {
        return try {
            apiService.sendRequestForIllDay(request).extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
