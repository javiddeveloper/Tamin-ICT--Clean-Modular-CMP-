package com.tamin.taminhamrah.paging

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.util.CommonRequestConstants

data class PaginationConfig(
    val pageSize: Int = CommonRequestConstants.LIMIT,
    val firstPage: Int = 0,
)

internal fun ApiQueryParamDN.forPage(
    pageNumber: Int,
    config: PaginationConfig,
): ApiQueryParamDN = copy(
    page = pageNumber,
    start = (pageNumber - config.firstPage) * config.pageSize,
    limit = config.pageSize,
)
