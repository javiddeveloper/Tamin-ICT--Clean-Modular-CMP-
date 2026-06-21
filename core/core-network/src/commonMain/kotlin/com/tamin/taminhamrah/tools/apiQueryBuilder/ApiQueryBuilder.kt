/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.tools.apiQueryBuilder

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface ApiQueryBuilder {
    fun defaultQuery(): ApiQueryParamDN
    fun buildQuery(query: ApiQueryParamDN): Map<String, String>
    fun buildFilterJson(filters: List<ApiFilterDN>): String
}
