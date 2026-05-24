/*
*
* @author: Javid Sattar 
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.core.model.request


data class ApiQueryParamDN(
    val filters: Map<String, String>? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
    val sortBy: String? = null,
    val sortOrder: String? = null
)

enum class SortDirection(val value: String) {
    ASC("ASC"),
    DESC("DESC")
}

data class ApiSortDN(
    val property: String,
    val direction: SortDirection
)

enum class FilterOperator(val value: String) {
    EQUAL("EQUAL"),
    CONTAINS("CONTAINS"),
    LIKE("LIKE"),
}

data class ApiFilterDN(
    val property: String,
    val operator: FilterOperator,
    val value: String
)
