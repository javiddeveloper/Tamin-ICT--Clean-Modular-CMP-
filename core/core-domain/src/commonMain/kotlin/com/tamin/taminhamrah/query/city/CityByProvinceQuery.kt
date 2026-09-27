package com.tamin.taminhamrah.query.city

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

/** Shared filter shape for [com.tamin.taminhamrah.useCases.common.GetCitiesByProvincePageUseCase] callers. */
object CityByProvinceQuery {
    /** Filters only — paging (`page`/`start`/`limit`) is applied by [com.tamin.taminhamrah.paging.Paginator]. */
    fun filters(provinceCode: String): List<ApiFilterDN> = listOf(
        ApiFilterDN(
            property = FilterProperty.PROVINCE_CODE_CITY,
            operator = FilterOperator.EQ,
            value = provinceCode,
        ),
    )
}
