package com.tamin.taminhamrah.dataSource.pension

import com.tamin.taminhamrah.apiService.pension.PensionApiService
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData

class PensionRemoteDataSourceImpl(
    private val pensionApiService: PensionApiService,
    private val apiQueryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : PensionRemoteDataSource {
    override suspend fun getPensionInquiry(query: ApiQueryParamDN): ListData<PensionInquiryDTO> {
        return try {
            val response =
                pensionApiService.getPensionInquiry(apiQueryBuilder.buildQuery(query))
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR)
            )
        }
    }

    override suspend fun getPensionerId(): List<PensionIdDTO> {
        return try {
            val response = pensionApiService.getPensionerId()
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
