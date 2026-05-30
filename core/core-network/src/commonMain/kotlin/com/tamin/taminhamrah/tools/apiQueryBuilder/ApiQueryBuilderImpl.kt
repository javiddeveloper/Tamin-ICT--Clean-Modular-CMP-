/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.tools.apiQueryBuilder

import com.tamin.taminhamrah.core.model.request.ApiFilterDN
import com.tamin.taminhamrah.core.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.core.model.request.ApiSortDN

internal class ApiQueryBuilderImpl : ApiQueryBuilder {
    override fun buildQuery(query: ApiQueryParamDN): Map<String, String> {
        return buildMap {
            put("page", query.page.toString())
            put("start", query.start.toString())
            put("limit", query.limit.toString())

            if (query.filters.isNotEmpty()) {
                put("filter", buildFilterJson(query.filters))
            }

            if (query.sorts.isNotEmpty()) {
                put("sort", buildSortJson(query.sorts))
            }
        }
    }

    private fun buildFilterJson(filters: List<ApiFilterDN>): String {
        val filterObjects = filters.map { filter ->
            """{"property":"${filter.property}","operator":"${filter.operator.value}","value":"${filter.value}"}"""
        }
        return "[${filterObjects.joinToString(",")}]"
    }

    private fun buildSortJson(sorts: List<ApiSortDN>): String {
        val sortObjects = sorts.map { sort ->
            """{"property":"${sort.property}","direction":"${sort.direction.value}"}"""
        }
        return "[${sortObjects.joinToString(",")}]"
    }
}
