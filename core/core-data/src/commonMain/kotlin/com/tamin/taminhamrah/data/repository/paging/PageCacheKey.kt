package com.tamin.taminhamrah.data.repository.paging

import com.tamin.taminhamrah.model.request.ApiQueryParamDN

/**
 * Identifies one paged list in a `*_pages` cache table: its parent ids ([scope], e.g. workshop and
 * branch) plus the query's filters and sorts — everything except the paging fields. Two requests
 * for different pages of the same list share a key; a different search or parent gets its own
 * rows, so replacing one list's cache never wipes another's.
 */
internal fun ApiQueryParamDN.pageCacheKey(vararg scope: String?): String =
    (scope.map { it.orEmpty() } + filters.toString() + sorts.toString()).joinToString("|")
