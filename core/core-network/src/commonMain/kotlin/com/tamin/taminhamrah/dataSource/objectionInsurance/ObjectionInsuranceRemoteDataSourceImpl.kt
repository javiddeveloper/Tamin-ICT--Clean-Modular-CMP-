package com.tamin.taminhamrah.dataSource.objectionInsurance

import com.tamin.taminhamrah.apiService.objectionInsurance.ObjectionInsuranceApiService
import com.tamin.taminhamrah.model.objectionInsurance.ConfirmConflictItemDTO
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpStatusErrorMapper
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class ObjectionInsuranceRemoteDataSourceImpl(
    private val objectionInsuranceApiService: ObjectionInsuranceApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : ObjectionInsuranceRemoteDataSource {

    override suspend fun checkStatusConflict(): Boolean {
        return fetchData { objectionInsuranceApiService.checkStatusConflict() }
    }

    override suspend fun getConflictHistories(query: ApiQueryParamDN): ListData<ObjectionInsuranceHistoryDTO> {
        return fetchData {
            objectionInsuranceApiService.getConflictHistories(apiQueryBuilder.buildQuery(query))
        }
    }

    override suspend fun saveConflict(items: List<ObjectionInsuranceHistoryDTO>): String? {
        return try {
            objectionInsuranceApiService.saveConflict(items).extractNullableData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun confirmConflict(description: String?): Boolean {
        return fetchData {
            objectionInsuranceApiService.confirmConflict(
                listOf(ConfirmConflictItemDTO(userDesc = description))
            )
        }
    }

    override suspend fun finalConfirmConflict(): String {
        return fetchData { objectionInsuranceApiService.finalConfirmConflict() }
    }

    private suspend fun <T> fetchData(call: suspend () -> BaseDTO<T>): T {
        return try {
            call().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    /**
     * Like [extractData], but allows null [BaseDTO.data] on 2xx (legacy null-data success).
     */
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
