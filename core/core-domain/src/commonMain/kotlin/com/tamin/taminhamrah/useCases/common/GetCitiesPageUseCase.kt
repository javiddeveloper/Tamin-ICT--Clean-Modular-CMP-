package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

/** Search by city name (with optional province filter), paged. Offline-first: emits the cached page, then the network page — collect the whole flow, not `.first()`. */
class GetCitiesPageUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = repository.getCitiesPage(query)
}
