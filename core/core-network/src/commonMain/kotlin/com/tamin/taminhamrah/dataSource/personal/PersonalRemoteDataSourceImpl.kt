package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.apiService.personal.PersonalApiService
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class PersonalRemoteDataSourceImpl(
    private val personalApiService: PersonalApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser,
) : PersonalRemoteDataSource {

    override suspend fun getPersonalInfo(): PersonalInfoDTO? {
        return try {
            val response = personalApiService.getPersonalInfo()
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getAge(birthDate: Long): AgeDTO {
        return try {
            val response = personalApiService.getAge(birthDate)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getDisabilityDependentInfo(query: ApiQueryParamDN): List<DisabilityDependentDTO> {
        return try {
            val response = personalApiService.getDisabilityDependentInfo(
                queryBuilder.buildQuery(query)
            )
            response.extractData().list ?: emptyList()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun confirmSurvivorsList(query: ApiQueryParamDN): List<ConfirmSurvivorDTO> {
        return try {
            val response = personalApiService.confirmSurvivorsList(
                queryBuilder.buildQuery(query)
            )
            response.extractData().list ?: emptyList()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun submitFinalSurvivorPension(
        requestId: Int,
        body: SubmitFinalSurvivorPensionRequest
    ): String? {
        return try {
            val response = personalApiService.submitFinalSurvivorPension(requestId, body)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }
}
