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
            query.filters?.forEach { (key, value) ->
                put(key, value)
            }
            query.page?.let { put("page", it.toString()) }
            query.pageSize?.let { put("pageSize", it.toString()) }
            query.sortBy?.let { put("sortBy", it) }
            query.sortOrder?.let { put("sortOrder", it) }
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
