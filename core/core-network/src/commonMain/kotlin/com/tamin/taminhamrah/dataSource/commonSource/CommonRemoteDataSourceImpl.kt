/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.dataSource.commonSource

import com.tamin.core.network.model.common.CityNameDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.taminhamrah.apiService.CommonApiService
import com.tamin.taminhamrah.model.common.BeneficiaryDTO
import com.tamin.taminhamrah.model.common.InsuranceTypeDTO
import com.tamin.taminhamrah.model.common.JobTitleDTO
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.taminhamrah.model.common.RecipientDTO
import com.tamin.taminhamrah.model.common.UserInsuredInfoDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.apiQueryBuilder.ApiQueryBuilder
import com.tamin.taminhamrah.tools.errorHandling.ErrorParser
import com.tamin.taminhamrah.tools.extractData
import com.tamin.taminhamrah.tools.safeCall
import io.ktor.client.statement.HttpStatement

internal class CommonRemoteDataSourceImpl(
    private val commonApiService: CommonApiService,
    private val queryBuilder: ApiQueryBuilder,
    private val errorParser: ErrorParser
) : CommonRemoteDataSource {

    override suspend fun getCityName(cityNameRequest: ApiQueryParamDN): CityNameDto {
        return errorParser.safeCall("getCityName") {
            val response = commonApiService.getCityName(
                queryBuilder.buildQuery(cityNameRequest)
            )
            response.extractData()
        }
    }

    override suspend fun getProvinceName(provinceNameRequest: ApiQueryParamDN): ProvinceNameDto {
        return errorParser.safeCall("getProvinceName") {
            val response = commonApiService.getProvinceName(
                queryBuilder.buildQuery(provinceNameRequest)
            )
            response.extractData()
        }
    }

    override suspend fun getCitiesByProvince(query: ApiQueryParamDN): CityNameDto {
        return errorParser.safeCall("getCitiesByProvince") {
            val response = commonApiService.getCitiesByProvince(
                queryBuilder.buildQuery(query)
            )
            response.extractData()
        }
    }

    override suspend fun getInsuranceTypes(query: ApiQueryParamDN): ListData<InsuranceTypeDTO> {
        return errorParser.safeCall("getInsuranceTypes") {
            val response = commonApiService.getInsuranceTypes(
                queryBuilder.buildQuery(query)
            )
            response.extractData()
        }
    }

    override suspend fun getMainMenu(
        versionCode: String,
        forceUpdate: Boolean
    ): List<MainServiceDto> {
        return errorParser.safeCall("getMainMenu") {
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
        }
    }

    override suspend fun getBeneficiary(query: ApiQueryParamDN): ListData<BeneficiaryDTO> {
        return errorParser.safeCall("getBeneficiary") {
            val response = commonApiService.getBeneficiary(
                queryBuilder.buildQuery(query)
            )
            response.extractData()
        }
    }

    override suspend fun getRecipientList(query: ApiQueryParamDN): ListData<RecipientDTO> {
        return errorParser.safeCall("getRecipientList") {
            val response = commonApiService.getRecipientList(
                queryBuilder.buildQuery(query)
            )
            response.extractData()
        }
    }

    override suspend fun getRegistrationDeclarationForm(): HttpStatement {
        return errorParser.safeCall("getRegistrationDeclarationForm") {
            commonApiService.getRegistrationDeclarationForm()
        }
    }

    override suspend fun getJobTitle(query: ApiQueryParamDN): ListData<JobTitleDTO> {
        val queries = queryBuilder.buildQuery(query)
        return errorParser.safeCall("getJobTitle") {
            val response = commonApiService.getJobTitle(queries)
            response.extractData()
        }
    }

    override suspend fun checkInsuredInfo(): UserInsuredInfoDTO {
        return errorParser.safeCall("checkInsuredInfo") {
            val response = commonApiService.checkInsuredInfo()
            response.extractData()
        }
    }
}
