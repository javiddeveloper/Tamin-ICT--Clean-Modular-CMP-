/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.tools.apiQueryBuilder

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.ApiSortDN
import com.tamin.taminhamrah.util.CommonRequestConstants
import kotlinx.serialization.json.Json

internal class ApiQueryBuilderImpl : ApiQueryBuilder {
    override fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN()

    override fun buildQuery(query: ApiQueryParamDN): Map<String, String> {
        return buildMap<String, String> {
            put("page", query.page.toString())
            put("start", query.start.toString())
            put("limit", query.limit.toString())

            val filterVal: String = if (query.filters.isNotEmpty()) {
                buildFilterJson(query.filters)
            } else {
                CommonRequestConstants.FILTER
            }
            put("filter", filterVal)

            val sortVal: String = if (query.sorts.isNotEmpty()) {
                buildSortJson(query.sorts)
            } else {
                CommonRequestConstants.SORT
            }
            put("sort", sortVal)
        }
    }

    override fun buildFilterJson(filters: List<ApiFilterDN>): String {
        return Json.encodeToString(filters)
    }

    private fun buildSortJson(sorts: List<ApiSortDN>): String {
        return Json.encodeToString(sorts)
    }
}
