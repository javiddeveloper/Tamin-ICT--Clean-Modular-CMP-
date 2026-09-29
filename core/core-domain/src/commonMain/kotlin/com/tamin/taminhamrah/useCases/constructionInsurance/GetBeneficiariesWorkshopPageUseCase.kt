package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

/**
 * Offline-first: emits the cached page (if any), then the network page. Collect the whole flow
 * (`Paginator(loadPages = …)`), not `.first()`.
 */
class GetBeneficiariesWorkshopPageUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>> =
        repository.getBeneficiariesWorkshopPage(query)
}
