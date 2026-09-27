package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

/** Paged city list keyed by province code. Offline-first: emits the cached page, then the network page — collect the whole flow, not `.first()`. */
class GetCitiesByProvincePageUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>> =
        repository.getCitiesByProvincePage(provinceCode, query)
}
