/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.core.network.model.common.CityNameDto
import com.tamin.core.network.model.common.MainServiceDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.core.network.tools.BaseResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Url

internal interface CommonApiService {
    @GET("proxy/models/city/")
    suspend fun getCityName(
        @QueryMap parameters: Map<String, String>
    ): BaseResponse<CityNameDto>

    @GET("proxy/models/province/")
    suspend fun getProvinceName(
        @QueryMap parameters: Map<String, String>
    ): BaseResponse<ProvinceNameDto>

    @GET
    suspend fun getMainMenu(@Url url: String): BaseResponse<List<MainServiceDto>>
}
