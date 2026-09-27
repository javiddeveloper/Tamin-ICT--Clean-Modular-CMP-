package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import kotlinx.coroutines.flow.Flow

/** Paged province list. Offline-first: emits the cached page, then the network page — collect the whole flow, not `.first()`. */
class GetProvincesPageUseCase(
    private val repository: CityProvinceRepository
) {
    operator fun invoke(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = repository.getProvincesPage(query)
}
