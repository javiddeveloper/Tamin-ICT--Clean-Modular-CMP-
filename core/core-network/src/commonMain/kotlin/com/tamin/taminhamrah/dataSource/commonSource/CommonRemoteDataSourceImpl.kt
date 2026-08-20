/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.core.network.model.common.CityNameDto
import com.tamin.taminhamrah.model.common.InsuranceTypeDto
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.taminhamrah.model.common.RecipientDTO
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.errorHandling.ErrorUri
import com.tamin.taminhamrah.tools.errorHandling.TaminErrorUriException
import com.tamin.taminhamrah.tools.extractData
import io.ktor.client.statement.HttpStatement
import com.tamin.taminhamrah.apiService.CommonApiService
import com.tamin.taminhamrah.model.common.BeneficiaryDTO
import com.tamin.taminhamrah.model.common.JobTitleDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import io.ktor.client.statement.HttpResponse

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

    override suspend fun getCitiesByProvince(query: ApiQueryParamDN): CityNameDto {
        return try {
            val response = commonApiService.getCitiesByProvince(
                queryBuilder.buildQuery(query)
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getInsuranceTypes(query: ApiQueryParamDN): ListData<InsuranceTypeDto>? {
        return try {
            val response = commonApiService.getInsuranceTypes(
                queryBuilder.buildQuery(query)
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getMainMenu(
        versionCode: String,
        forceUpdate: Boolean
    ): List<MainServiceDto> {
        return try {
            // Temporarily returning local data as requested
            mockMenuData
            /*
            val serviceUrl = buildString {
                append("https://ssodcfs.tamin.ir/eservices/menu_data_")
                append(versionCode)
                append(".txt")
            }
            val response = commonApiService.getMainMenu(serviceUrl)
            response.extractData()
            */
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(
                TaminErrorUriException(ErrorUri.UNKNOWN)
            )
        }
    }

    override suspend fun getBeneficiary(query: ApiQueryParamDN): ListData<BeneficiaryDTO> {
        return try {
            val response = commonApiService.getBeneficiary(
                queryBuilder.buildQuery(query)
            )
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
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

    override suspend fun getRegistrationDeclarationForm(): HttpStatement {
        return try {
            commonApiService.getRegistrationDeclarationForm()
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }

    override suspend fun getJobTitle(query: ApiQueryParamDN): ListData<JobTitleDTO>? {
        val queries = queryBuilder.buildQuery(query)
        return try {
            val response = commonApiService.getJobTitle(queries)
            response.extractData()
        } catch (e: TaminErrorUriException) {
            throw errorParser.parseGeneralError(e)
        } catch (e: Exception) {
            throw errorParser.parseGeneralError(TaminErrorUriException(ErrorUri.NO_CONNECTION_ERROR))
        }
    }
}
