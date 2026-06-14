/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.core.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiQueryParamDN(
    val page: Int = 0,
    val start: Int = 0,
    val limit: Int = 10,
    val filters: List<ApiFilterDN> = emptyList(),
    val sorts: List<ApiSortDN> = emptyList(),
)

@Serializable
enum class SortDirection(val value: String) {
    ASC("ASC"),
    DESC("DESC"),
}

@Serializable
data class ApiSortDN(
    val property: String,
    val direction: SortDirection,
)

@Serializable
enum class FilterOperator(val value: String) {
    EQUAL("EQUAL"),
    CONTAINS("CONTAINS"),
    LIKE("LIKE"),
    EQ("EQ"),
    EQUAL_LOWER("equal")
}

@Serializable
enum class FilterProperty(val key: String) {
    SERIAL_ID("serialId"),
    MOBILE("mobile"),
    CITY_CODE("cityCode"),
    PROVINCE_CODE("provinceCode")
}

@Serializable
data class ApiFilterDN(
    val property: FilterProperty,
    val operator: FilterOperator,
    val value: String,
)
