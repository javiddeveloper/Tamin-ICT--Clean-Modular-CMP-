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
import com.tamin.taminhamrah.tools.safeCall

class WeddingPresentRemoteDataSourceImpl(
    private val weddingPresentApiService: WeddingPresentApiService,
    private val errorParser: ErrorParser,
) : WeddingPresentRemoteDataSource {

    override suspend fun getWeddingPresentInfo(): WeddingPresentInfoDTO? =
        errorParser.safeCall("getWeddingPresentInfo") {
            weddingPresentApiService.getWeddingPresentInfo().extractData()
        }

    override suspend fun submitWeddingPresent(request: ShortTermMarriageRequestDTO) {
        errorParser.safeCall("submitWeddingPresent") {
            weddingPresentApiService.submitWeddingPresent(request).extractNullableData()
        }
    }

    /** Like [extractData], but allows null [BaseDTO.data] on 2xx (legacy GeneralRes success). */
    private fun <T> BaseDTO<T>.extractNullableData(): T? {
        return when {
            hasProblems -> {
                val firstProblem = problems?.firstOrNull()
                throw TaminErrorUriException(
                    uri = ErrorUri.SERVER_PROBLEM,
                    serverMessage = problemMessage ?: reason,
                    errorCode = firstProblem?.errorCode,
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
