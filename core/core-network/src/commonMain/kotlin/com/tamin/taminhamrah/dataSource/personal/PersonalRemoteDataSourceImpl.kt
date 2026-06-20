package com.tamin.taminhamrah.dataSource.personal

import com.tamin.taminhamrah.apiService.personal.PersonalApiService
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
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
            response?.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
