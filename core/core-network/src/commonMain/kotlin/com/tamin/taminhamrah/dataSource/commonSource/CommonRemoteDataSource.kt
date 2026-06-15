/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.core.network.datasource.commonSource

import com.tamin.core.network.model.common.CityNameDto
import com.tamin.core.network.model.common.MainServiceDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface CommonRemoteDataSource {
    suspend fun getCityName(cityNameRequest: ApiQueryParamDN): CityNameDto
    suspend fun getProvinceName(provinceNameRequest: ApiQueryParamDN): ProvinceNameDto
    suspend fun getMainMenu(versionCode: String,forceUpdate: Boolean): List<MainServiceDto>
}
