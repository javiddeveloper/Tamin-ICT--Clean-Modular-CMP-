package com.tamin.taminhamrah.dataSource.inquiryEducation

import com.tamin.taminhamrah.apiService.inquiryEducation.InquiryEducationApiService
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsListDTO
import com.tamin.taminhamrah.tools.BaseDTO
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.HttpStatusErrorMapper
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class InquiryEducationRemoteDataSourceImpl(
    private val inquiryEducationApiService: InquiryEducationApiService,
    private val errorParser: ErrorParser,
) : InquiryEducationRemoteDataSource {

    override suspend fun getDataForEducation(): EducationDependentsListDTO? {
        return try {
            inquiryEducationApiService.getDataForEducation().extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun inquiryEducationCertificate(
        code: String,
        educationCode: String,
    ): String? {
        return try {
            inquiryEducationApiService
                .inquiryEducationCertificate(code = code, educationCode = educationCode)
                .extractNullableData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
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
