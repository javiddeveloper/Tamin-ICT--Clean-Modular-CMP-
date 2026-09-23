package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetConstructionFilesPageUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<ConstructionFileDN>> {
        return repository.getConstructionFilesPage(query)
    }
}
