package com.tamin.taminhamrah.data.repository.city

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

internal object CityByProvinceQuery {
    fun build(provinceCode: String): ApiQueryParamDN = ApiQueryParamDN(
        page = 0,
        start = 0,
        limit = 200,
        filters = listOf(
            ApiFilterDN(
                property = FilterProperty.PROVINCE_CODE_CITY,
                operator = FilterOperator.EQ,
                value = provinceCode,
            ),
        ),
    )
}
