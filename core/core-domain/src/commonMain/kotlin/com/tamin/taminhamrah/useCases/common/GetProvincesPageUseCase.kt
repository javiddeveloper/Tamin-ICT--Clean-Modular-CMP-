package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

/** Paged, online province list. Network-only. */
class GetProvincesPageUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = repository.getProvincesPage(query)
}
