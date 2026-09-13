package com.tamin.taminhamrah.dataSource.fractionContract

import com.tamin.taminhamrah.apiService.fractionContract.FractionContractApiService
import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDTO
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDTO
import com.tamin.taminhamrah.model.fractionContract.MakeFractionContractRequestDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpStatusErrorMapper
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.safeCall

class FractionContractRemoteDataSourceImpl(
    private val fractionContractApiService: FractionContractApiService,
    private val errorParser: ErrorParser,
) : FractionContractRemoteDataSource {

    override suspend fun checkAgeAndHistory(): FractionEligibilityDTO? =
        errorParser.safeCall("checkFractionAgeAndHistory") {
            fractionContractApiService.checkAgeAndHistory().extractNullableData()
        }

    override suspend fun makeFractionContract(
        request: MakeFractionContractRequestDTO,
    ): FractionContractResultDTO =
        errorParser.safeCall("makeFractionContract") {
            fractionContractApiService.makeFractionContract(request).extractData()
        }

    /**
     * Like [extractData], but allows null [BaseDTO.data] on 2xx
     * (legacy shows a register-error layout when eligibility data is null).
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
