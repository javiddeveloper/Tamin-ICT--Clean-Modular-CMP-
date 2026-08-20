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
import com.tamin.taminhamrah.model.common.BeneficiaryDTO
import com.tamin.taminhamrah.model.common.JobTitleDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData
import io.ktor.client.statement.HttpStatement

interface CommonRemoteDataSource {
    suspend fun getCityName(cityNameRequest: ApiQueryParamDN): CityNameDto
    suspend fun getProvinceName(provinceNameRequest: ApiQueryParamDN): ProvinceNameDto
    suspend fun getCitiesByProvince(query: ApiQueryParamDN): CityNameDto
    suspend fun getInsuranceTypes(query: ApiQueryParamDN): ListData<InsuranceTypeDto>?
    suspend fun getMainMenu(versionCode: String,forceUpdate: Boolean): List<MainServiceDto>
    suspend fun getBeneficiary(query: ApiQueryParamDN): ListData<BeneficiaryDTO>
    suspend fun getRecipientList(query: ApiQueryParamDN): ListData<RecipientDTO>
    suspend fun getRegistrationDeclarationForm(): HttpStatement
    suspend fun getJobTitle(query: ApiQueryParamDN): ListData<JobTitleDTO>?
}
