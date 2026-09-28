package com.tamin.taminhamrah.dataSource.calculateWagePension

import kotlinx.coroutines.CancellationException
import io.ktor.serialization.JsonConvertException
import com.tamin.taminhamrah.apiService.calculateWagePension.CalculateWagePensionApiService
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException

class CalculateWagePensionRemoteDataSourceImpl(
    private val apiService: CalculateWagePensionApiService,
    private val errorParser: ErrorParser
) : CalculateWagePensionRemoteDataSource {

    override suspend fun getPersonalInfo(): MultipleWorkshopPersonalInfoDTO {
        return try {
            apiService.getPersonalInfo().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: JsonConvertException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.INTERNAL_ERROR)
            )
        } catch (e: HttpRequestTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: ConnectTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: SocketTimeoutException) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.SERVICE_TIMEOUT)
            )
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
