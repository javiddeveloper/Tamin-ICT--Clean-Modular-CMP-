/*
*
* @author: Javid Sattar
* @email: javiddeveloper@gmail.com
*
*/

package com.tamin.taminhamrah.tools.apiQueryBuilder

import com.tamin.taminhamrah.core.model.request.ApiQueryParamDN

interface ApiQueryBuilder {
    fun buildQuery(query: ApiQueryParamDN): Map<String, String>
}
