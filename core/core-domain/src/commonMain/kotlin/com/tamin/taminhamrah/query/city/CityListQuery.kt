package com.tamin.taminhamrah.query.city

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

/** Shared filter shape for [com.tamin.taminhamrah.useCases.common.GetCitiesPageUseCase] callers. */
object CityListQuery {
    /** Filters only — paging (`page`/`start`/`limit`) is applied by [com.tamin.taminhamrah.paging.Paginator]. */
    fun filters(cityName: String? = null, provinceCode: String? = null): List<ApiFilterDN> = buildList {
        if (!cityName.isNullOrBlank()) {
            // `proxy/models/city/` is the same Oracle-backed proxy as the branch/job lookups
            // elsewhere in the app (InspectionViewModel, WorkshopRecentlyAddedMembersViewModel) —
            // it only recognizes its own LIKE + `*term*` wildcard convention. CONTAINS isn't a
            // filter this endpoint understands and made every city-name search 404.
            add(
                ApiFilterDN(
                    property = FilterProperty.CITY_NAME,
                    operator = FilterOperator.LIKE,
                    value = "*$cityName*",
                ),
            )
        }
        // Narrowing server-side is what makes this fast: without it every province selection
        // pulled the whole country's city list and threw most of it away here.
        if (!provinceCode.isNullOrBlank()) {
            add(
                ApiFilterDN(
                    property = FilterProperty.PROVINCE_CODE_CITY,
                    operator = FilterOperator.EQUAL,
                    value = provinceCode,
                ),
            )
        }
    }
}
