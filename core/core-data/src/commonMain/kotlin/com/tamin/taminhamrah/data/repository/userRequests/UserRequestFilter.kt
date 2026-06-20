package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

enum class UserRequestFilter(
    val value: String,
    val property: FilterProperty = FilterProperty.OPERATION,
    val operator: FilterOperator = FilterOperator.EQUAL,
) {
    MY_REQUESTS("03"),
    ;

    fun toApiFilterDN(): ApiFilterDN = ApiFilterDN(
        property = property,
        value = value,
        operator = operator,
    )

    companion object {
        fun defaultFilters(): List<ApiFilterDN> = listOf(MY_REQUESTS.toApiFilterDN())
    }
}
