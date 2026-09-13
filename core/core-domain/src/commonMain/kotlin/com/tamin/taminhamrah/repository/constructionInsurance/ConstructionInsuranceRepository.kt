package com.tamin.taminhamrah.repository.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import kotlinx.coroutines.flow.Flow

interface ConstructionInsuranceRepository {
    fun getConstructionFiles(
        search: ConstructionFileSearchParamsDN? = null
    ): Flow<List<ConstructionFileDN>>
}
