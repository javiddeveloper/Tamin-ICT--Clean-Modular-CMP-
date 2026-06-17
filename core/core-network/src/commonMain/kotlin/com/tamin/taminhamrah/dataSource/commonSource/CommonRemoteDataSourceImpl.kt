/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.core.network.datasource.commonSource.CommonRemoteDataSource
import com.tamin.core.network.model.common.CityNameDto
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.taminhamrah.model.common.RecipientDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.apiService.CommonApiService
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

internal class CommonRemoteDataSourceImpl(
    private val commonApiService: CommonApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : CommonRemoteDataSource {

    override suspend fun getCityName(cityNameRequest: ApiQueryParamDN): CityNameDto {
        return try {
            val response = commonApiService.getCityName(
                queryBuilder.buildQuery(cityNameRequest)
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        }
    }

    override suspend fun getProvinceName(provinceNameRequest: ApiQueryParamDN): ProvinceNameDto {
        return try {
            val response = commonApiService.getProvinceName(
                queryBuilder.buildQuery(provinceNameRequest)
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        }
    }

    override suspend fun getMainMenu(
        versionCode: String,
        forceUpdate: Boolean
    ): List<MainServiceDto> {
        return try {
            val serviceUrl = buildString {
                append("https://ssodcfs.tamin.ir/eservices/menu_data_")
                append(versionCode)
                append(".txt")
            }
            val response = commonApiService.getMainMenu(serviceUrl)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun getRecipientList(query: ApiQueryParamDN): ListData<RecipientDTO> {
        return try {
            val response = commonApiService.getRecipientList(
                queryBuilder.buildQuery(query)
            )
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
