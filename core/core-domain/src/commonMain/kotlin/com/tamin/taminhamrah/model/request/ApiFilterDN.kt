/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.model.request

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
    @SerialName("ASC") ASC("ASC"),
    @SerialName("DESC") DESC("DESC"),
}

@Serializable
data class ApiSortDN(
    val property: String,
    val direction: SortDirection,
)

@Serializable
enum class FilterOperator(val value: String) {
    @SerialName("EQUAL") EQUAL("EQUAL"),
    @SerialName("CONTAINS") CONTAINS("CONTAINS"),
    @SerialName("LIKE") LIKE("LIKE"),
    @SerialName("EQ") EQ("EQ"),
    @SerialName("equal") EQUAL_LOWER("equal")
}

@Serializable
enum class FilterProperty(val key: String) {
    @SerialName("serialId") SERIAL_ID("serialId"),
    @SerialName("mobile") MOBILE("mobile"),
    @SerialName("cityCode") CITY_CODE("cityCode"),
    @SerialName("provinceCode") PROVINCE_CODE("provinceCode"),
    @SerialName("pensionerId") PENSIONER_ID("pensionerId"),
    @SerialName("startDate") START_DATE("startDate")
}

@Serializable
data class ApiFilterDN(
    val property: FilterProperty,
    val value: String,
    val operator: FilterOperator,
)
