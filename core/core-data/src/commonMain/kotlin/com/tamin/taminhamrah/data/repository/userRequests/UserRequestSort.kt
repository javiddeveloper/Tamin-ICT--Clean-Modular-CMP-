package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.model.request.ApiSortDN
import com.tamin.taminhamrah.model.request.SortDirection

enum class UserRequestSort(
    val property: String,
    val direction: SortDirection,
) {
    REF_CODE_DESC("refCode", SortDirection.DESC),
    ;

    fun toApiSortDN(): ApiSortDN = ApiSortDN(
        property = property,
        direction = direction,
    )

    companion object {
        fun defaultSorts(): List<ApiSortDN> = listOf(REF_CODE_DESC.toApiSortDN())
    }
}
