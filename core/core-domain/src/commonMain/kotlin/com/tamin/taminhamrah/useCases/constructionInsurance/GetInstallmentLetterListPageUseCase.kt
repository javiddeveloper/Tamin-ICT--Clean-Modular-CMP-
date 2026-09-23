package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetInstallmentLetterListPageUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(workshopId: String, branchId: String, query: ApiQueryParamDN): Flow<PageDN<InstallmentLetterDN>> =
        repository.getInstallmentLetterListPage(workshopId, branchId, query)
}
