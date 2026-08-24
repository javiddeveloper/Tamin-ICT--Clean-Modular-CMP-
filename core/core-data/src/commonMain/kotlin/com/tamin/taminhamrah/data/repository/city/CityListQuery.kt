package com.tamin.taminhamrah.data.repository.city

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

internal object CityListQuery {
    fun build(cityName: String? = null, provinceCode: String? = null): ApiQueryParamDN {
        val filters = buildList {
            if (!cityName.isNullOrBlank()) {
                add(
                    ApiFilterDN(
                        property = FilterProperty.CITY_NAME,
                        operator = FilterOperator.CONTAINS,
                        value = cityName,
                    ),
                )
            }
            // Narrowing server-side is what makes this fast: without it every province selection
            // pulled the whole country's city list and threw most of it away here — and anything
            // past the 500 cap simply never arrived.
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
        return ApiQueryParamDN(
            page = 1,
            start = 0,
            limit = 500,
            filters = filters,
        )
    }
}
