package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

/** Online search by city name (with optional province filter), paged. Network-only. */
class GetCitiesPageUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = repository.getCitiesPage(query)
}
