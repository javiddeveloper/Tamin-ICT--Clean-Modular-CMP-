/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.core.model.request

data class ApiQueryParamDN(
    val page: Int = 0,
    val start: Int = 0,
    val limit: Int = 10,
    val filters: List<ApiFilterDN> = emptyList(),
    val sorts: List<ApiSortDN> = emptyList(),
)

enum class SortDirection(val value: String) {
    ASC("ASC"),
    DESC("DESC"),
}

data class ApiSortDN(
    val property: String,
    val direction: SortDirection,
)

enum class FilterOperator(val value: String) {
    EQUAL("EQUAL"),
    CONTAINS("CONTAINS"),
    LIKE("LIKE"),
}

data class ApiFilterDN(
    val property: String,
    val operator: FilterOperator,
    val value: String,
)
