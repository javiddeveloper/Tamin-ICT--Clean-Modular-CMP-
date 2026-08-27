package com.tamin.taminhamrah.dataSource.inquiryEducation

import com.tamin.taminhamrah.apiService.inquiryEducation.InquiryEducationApiService
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsListDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpStatusErrorMapper
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.safeCall

class InquiryEducationRemoteDataSourceImpl(
    private val inquiryEducationApiService: InquiryEducationApiService,
    private val errorParser: ErrorParser,
) : InquiryEducationRemoteDataSource {

    override suspend fun getDataForEducation(): EducationDependentsListDTO? =
        errorParser.safeCall("getDataForEducation") {
            inquiryEducationApiService.getDataForEducation().extractData()
        }


    override suspend fun inquiryEducationCertificate(
        code: String,
        educationCode: String,
    ): String? = errorParser.safeCall("inquiryEducationCertificate"){
            inquiryEducationApiService
                .inquiryEducationCertificate(code = code, educationCode = educationCode)
                .extractNullableData()
        }
    }

    /**
     * Like [extractData], but allows null [BaseDTO.data] on 2xx (legacy "not found" success).
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
