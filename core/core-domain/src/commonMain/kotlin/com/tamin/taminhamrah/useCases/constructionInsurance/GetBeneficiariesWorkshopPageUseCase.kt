package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetBeneficiariesWorkshopPageUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>> =
        repository.getBeneficiariesWorkshopPage(query)
}
