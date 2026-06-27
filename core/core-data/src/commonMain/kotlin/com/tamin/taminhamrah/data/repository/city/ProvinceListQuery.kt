package com.tamin.taminhamrah.data.repository.city

import com.tamin.taminhamrah.model.request.ApiQueryParamDN

internal object ProvinceListQuery {
    fun build(): ApiQueryParamDN = ApiQueryParamDN(
        page = 1,
        start = 0,
        limit = 500,
    )
}
