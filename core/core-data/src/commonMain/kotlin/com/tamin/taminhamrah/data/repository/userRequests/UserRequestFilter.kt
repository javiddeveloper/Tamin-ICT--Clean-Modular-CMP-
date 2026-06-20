package com.tamin.taminhamrah.data.repository.userRequests

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.userRequest.UserRequestSearchParams

enum class UserRequestFilter(
    val property: FilterProperty,
    val operator: FilterOperator,
) {
    OPERATION(FilterProperty.OPERATION, FilterOperator.EQUAL),
    REF_CODE(FilterProperty.REF_CODE, FilterOperator.EQ),
    REQUEST_TYPE_ID(FilterProperty.REQUEST_TYPE_ID, FilterOperator.EQ),
    ;

    fun toApiFilterDN(value: String): ApiFilterDN = ApiFilterDN(
        property = property,
        value = value,
        operator = operator,
    )

    companion object {
        const val DEFAULT_OPERATION = "03"

        fun buildFilters(search: UserRequestSearchParams = UserRequestSearchParams()): List<ApiFilterDN> =
            buildFilters(
                refCode = search.refCode,
                requestTypeId = search.requestTypeId,
            )

        fun buildFilters(
            refCode: String? = null,
            requestTypeId: String? = null,
        ): List<ApiFilterDN> = buildList {
            add(OPERATION.toApiFilterDN(DEFAULT_OPERATION))
            refCode?.trim()?.takeIf { it.isNotEmpty() }?.let { add(REF_CODE.toApiFilterDN(it)) }
            requestTypeId?.trim()?.takeIf { it.isNotEmpty() }?.let { add(REQUEST_TYPE_ID.toApiFilterDN(it)) }
        }
    }
}
