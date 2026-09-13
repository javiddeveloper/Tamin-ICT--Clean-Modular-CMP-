package com.tamin.taminhamrah.dataSource.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface ConstructionInsuranceRemoteDataSource {
    suspend fun getConstructionFiles(
        query: ApiQueryParamDN
    ): ListData<ConstructionFileDTO>
}
