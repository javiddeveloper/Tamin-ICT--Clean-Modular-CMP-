/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.core.network.model.common.CityNameDto
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.taminhamrah.model.common.RecipientDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.common.BeneficiaryDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Url

internal interface CommonApiService {
    @GET("proxy/models/city/")
    suspend fun getCityName(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<CityNameDto>

    @GET("proxy/models/province/")
    suspend fun getProvinceName(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ProvinceNameDto>

    @GET
    suspend fun getMainMenu(@Url url: String): BaseDTO<List<MainServiceDto>>


    @GET("beneficiary")
    suspend fun getBeneficiary(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<BeneficiaryDTO>>


    @GET("recipients")
    suspend fun getRecipientList(@QueryMap parameters: Map<String, String>): BaseDTO<ListData<RecipientDTO>>


}
