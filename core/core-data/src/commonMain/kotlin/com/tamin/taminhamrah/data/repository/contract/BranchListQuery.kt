package com.tamin.taminhamrah.data.repository.contract

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

internal object BranchListQuery {
    fun build(cityCode: String): ApiQueryParamDN = ApiQueryParamDN(
        page = 0,
        start = 0,
        limit = 100,
        filters = listOf(
            ApiFilterDN(
                property = FilterProperty.CITY_CODE,
                operator = FilterOperator.EQ,
                value = cityCode,
            ),
        ),
    )
}
