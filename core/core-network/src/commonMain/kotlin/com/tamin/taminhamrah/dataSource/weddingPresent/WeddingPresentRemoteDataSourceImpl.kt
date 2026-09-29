package com.tamin.taminhamrah.dataSource.weddingPresent

import com.tamin.taminhamrah.apiService.weddingPresent.WeddingPresentApiService
import com.tamin.taminhamrah.model.weddingPresent.ShortTermMarriageRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpStatusErrorMapper
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import kotlinx.coroutines.CancellationException
import io.ktor.serialization.JsonConvertException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException

class WeddingPresentRemoteDataSourceImpl(
    private val weddingPresentApiService: WeddingPresentApiService,
    private val errorParser: ErrorParser,
) : WeddingPresentRemoteDataSource {

    override suspend fun getWeddingPresentInfo(): WeddingPresentInfoDTO? {
        return try {
            weddingPresentApiService.getWeddingPresentInfo().extractData()
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

    override suspend fun submitWeddingPresent(request: ShortTermMarriageRequestDTO) {
        try {
            weddingPresentApiService.submitWeddingPresent(request).extractNullableData()
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

    override suspend fun calculateMarriageAllowance(timeStamp: String): List<String>? {
        return try {
            weddingPresentApiService.calculateMarriageAllowance(timeStamp).extractData()
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

    /** Like [extractData], but allows null [BaseDTO.data] on 2xx (legacy GeneralRes success). */
    private fun <T> BaseDTO<T>.extractNullableData(): T? {
        return when {
            hasProblems -> {
                throw TaminErrorUriException(
                    uri = ErrorUri.SERVER_PROBLEM,
                    serverMessage = problemMessage ?: reason,
                )
            }
            status in 200..299 -> data
            else -> {
                val mapped = HttpStatusErrorMapper.map(
                    status = status,
                    rawMessage = reason,
                    cause = null,
                )
                throw TaminErrorUriException(
                    uri = mapped.uri,
                    serverMessage = mapped.userMessage,
                    navigateBack = mapped.navigateBack,
                )
            }
        }
    }
}
