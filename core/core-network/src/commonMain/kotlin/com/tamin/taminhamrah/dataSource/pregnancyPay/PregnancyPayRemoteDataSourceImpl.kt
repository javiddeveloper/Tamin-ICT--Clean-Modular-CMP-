package com.tamin.taminhamrah.dataSource.pregnancyPay

import kotlinx.coroutines.CancellationException
import io.ktor.serialization.JsonConvertException
import com.tamin.taminhamrah.apiService.pregnancyPay.PregnancyPayApiService
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionListDTO
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDTO
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayResponseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class PregnancyPayRemoteDataSourceImpl(
    private val pregnancyPayApiService: PregnancyPayApiService,
    private val errorParser: ErrorParser,
) : PregnancyPayRemoteDataSource {

    override suspend fun getMainInfo(): PregnancyMainInfoDTO? {
        return try {
            val response = pregnancyPayApiService.getMainInfo()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPregnancyStatusList(): PregnancyOptionListDTO? {
        return try {
            val response = pregnancyPayApiService.getPregnancyStatusList()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPregnancyTypeList(): PregnancyOptionListDTO? {
        return try {
            val response = pregnancyPayApiService.getPregnancyTypeList()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun sendPregnancyPayRequest(
        request: SendPregnancyPayRequestDTO
    ): SendPregnancyPayResponseDTO? {
        return try {
            val response = pregnancyPayApiService.sendPregnancyPayRequest(request)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun calculateEstimate(
        startDateTimeStamp: String,
        endDateTimeStamp: String,
    ): List<String?>? {
        return try {
            val response = pregnancyPayApiService.calculateEstimate(startDateTimeStamp, endDateTimeStamp)
            val result = response.extractData()
            if (result?.getOrNull(1).isNullOrBlank()) {
                throw TaminErrorUriException(
                    uri = ErrorUri.SERVER_PROBLEM,
                    serverMessage = result?.getOrNull(0),
                )
            }
            result
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
