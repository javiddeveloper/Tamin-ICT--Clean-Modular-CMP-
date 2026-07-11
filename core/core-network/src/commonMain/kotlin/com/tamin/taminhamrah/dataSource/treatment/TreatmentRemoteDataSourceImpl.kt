package com.tamin.taminhamrah.dataSource.treatment

import com.tamin.taminhamrah.apiService.treatment.TreatmentApiService
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO
import com.tamin.taminhamrah.model.treatment.MedicalAuthoritiesDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

internal class TreatmentRemoteDataSourceImpl(
    private val apiService: TreatmentApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : TreatmentRemoteDataSource {

    override suspend fun getDeservedTreatment(nationalCode: String): ListData<DeservedTreatmentDTO>? {
        return try {
            val response = apiService.getDeservedTreatment(nationalCode = nationalCode)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getDependantUnderEighteen(
        nationalCode: String,
        query: ApiQueryParamDN
    ): ListData<DependantUserUnderEighteenDTO>? {
        return try {
            val params = queryBuilder.buildQuery(query)
            val response = apiService.getDependantUnderEighteen(nationalCode, params)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getConfirmationMedicalAuthorities(
        query: ApiQueryParamDN
    ): ListData<MedicalAuthoritiesDTO>? {
        return try {
            val params = queryBuilder.buildQuery(query)
            val response = apiService.getConfirmationMedicalAuthorities(params)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}
