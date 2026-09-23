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
import com.tamin.taminhamrah.tools.safeCall

class ObjectionInsuranceRemoteDataSourceImpl(
    private val objectionInsuranceApiService: ObjectionInsuranceApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : ObjectionInsuranceRemoteDataSource {

    override suspend fun checkStatusConflict(): Boolean =
        errorParser.safeCall(TAG_CHECK_STATUS) {
            objectionInsuranceApiService.checkStatusConflict().extractData()
        }

    override suspend fun getConflictHistories(query: ApiQueryParamDN): ListData<ObjectionInsuranceHistoryDTO> =
        errorParser.safeCall(TAG_GET_HISTORIES) {
            objectionInsuranceApiService.getConflictHistories(apiQueryBuilder.buildQuery(query)).extractData()
        }

    override suspend fun saveConflict(items: List<ObjectionInsuranceHistoryDTO>): String? =
        errorParser.safeCall(TAG_SAVE_CONFLICT) {
            objectionInsuranceApiService.saveConflict(items).extractNullableData()
        }

    override suspend fun confirmConflict(description: String?): Boolean =
        errorParser.safeCall(TAG_CONFIRM) {
            objectionInsuranceApiService.confirmConflict(
                listOf(ConfirmConflictItemDTO(userDesc = description))
            ).extractData()
        }

    override suspend fun finalConfirmConflict(): String =
        errorParser.safeCall(TAG_FINAL_CONFIRM) {
            objectionInsuranceApiService.finalConfirmConflict().extractData()
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

    private companion object {
        const val TAG_CHECK_STATUS = "checkStatusConflict"
        const val TAG_GET_HISTORIES = "getConflictHistories"
        const val TAG_SAVE_CONFLICT = "saveConflict"
        const val TAG_CONFIRM = "confirmConflict"
        const val TAG_FINAL_CONFIRM = "finalConfirmConflict"
    }
}
