/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/
package com.tamin.taminhamrah.apiService

import com.tamin.core.network.model.common.CityNameDto
import com.tamin.taminhamrah.model.common.InsuranceTypeDto
import com.tamin.taminhamrah.model.common.MainServiceDto
import com.tamin.core.network.model.common.ProvinceNameDto
import com.tamin.taminhamrah.model.common.RecipientDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.common.BeneficiaryDTO
import com.tamin.taminhamrah.model.common.JobTitleDTO
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.client.statement.HttpResponse
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpStatement
import com.tamin.taminhamrah.util.NetworkConstants

internal interface CommonApiService {
    @GET("proxy/models/city/")
    suspend fun getCityName(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<CityNameDto>

    @GET("proxy/models/province/")
    suspend fun getProvinceName(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ProvinceNameDto>

    @GET("special-insured-services/cities")
    suspend fun getCitiesByProvince(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<CityNameDto>

    @GET("proxy/models/insurance-type/")
    suspend fun getInsuranceTypes(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<InsuranceTypeDto>>

    @GET
    suspend fun getMainMenu(@Url url: String): BaseDTO<List<MainServiceDto>>


    @GET("beneficiary")
    suspend fun getBeneficiary(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<BeneficiaryDTO>>


    @GET("recipients")
    suspend fun getRecipientList(@QueryMap parameters: Map<String, String>): BaseDTO<ListData<RecipientDTO>>

    @GET
    @Streaming
    suspend fun getRegistrationDeclarationForm(
        @Url url: String = "${NetworkConstants.BASE_URL_VIEW}assets/pdfs/questionair.pdf"
    ): HttpStatement

    @GET("baseinfo/job")
    suspend fun getJobTitle(
        @QueryMap queries: Map<String, String>
    ): BaseDTO<ListData<JobTitleDTO>>
}
