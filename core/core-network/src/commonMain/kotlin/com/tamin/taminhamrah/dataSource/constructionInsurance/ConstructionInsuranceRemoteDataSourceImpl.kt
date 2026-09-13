package com.tamin.taminhamrah.dataSource.constructionInsurance

import com.tamin.taminhamrah.apiService.constructionInsurance.ConstructionInsuranceApiService
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

internal class ConstructionInsuranceRemoteDataSourceImpl(
    private val apiService: ConstructionInsuranceApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : ConstructionInsuranceRemoteDataSource {

    override suspend fun getConstructionFiles(
        query: ApiQueryParamDN
    ): ListData<ConstructionFileDTO> {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = apiService.getConstructionFiles(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }
}
