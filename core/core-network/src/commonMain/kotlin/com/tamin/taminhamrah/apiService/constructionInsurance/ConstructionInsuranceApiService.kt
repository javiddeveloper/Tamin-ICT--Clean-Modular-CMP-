package com.tamin.taminhamrah.apiService.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.tools.BaseDTO
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

interface ConstructionInsuranceApiService {

    @GET("bld-request-services/building-workshops/normal")
    suspend fun getConstructionFiles(
        @QueryMap parameters: Map<String, String>
    ): BaseDTO<ListData<ConstructionFileDTO>>
}
