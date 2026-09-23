package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetInstallmentConstructionListPageUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(debitNumber: String, branchId: String, query: ApiQueryParamDN): Flow<PageDN<InstallmentConstructionListDN>> =
        repository.getInstallmentConstructionListPage(debitNumber, branchId, query)
}
