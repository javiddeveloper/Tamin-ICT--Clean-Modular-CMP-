package com.tamin.taminhamrah.data.repository.city

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

internal object CityListQuery {
    fun build(cityName: String? = null): ApiQueryParamDN {
        val filters = if (cityName.isNullOrBlank()) {
            emptyList()
        } else {
            listOf(
                ApiFilterDN(
                    property = FilterProperty.CITY_NAME,
                    operator = FilterOperator.CONTAINS,
                    value = cityName,
                ),
            )
        }
        return ApiQueryParamDN(
            page = 1,
            start = 0,
            limit = 500,
            filters = filters,
        )
    }
}
