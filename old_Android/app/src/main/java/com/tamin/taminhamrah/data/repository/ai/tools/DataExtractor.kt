package com.tamin.taminhamrah.data.repository.ai.tools

import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tamin.taminhamrah.data.remote.models.services.WageAndHistoryModel
import java.net.URLDecoder

fun extractFiltersFromUrl(url: String): List<FilterModel> {
    return try {
        val filterStart = url.indexOf("filter=") + 7
        if (filterStart == 6) return emptyList()

        val filterEnd = url.indexOf("&", filterStart).let {
            if (it == -1) url.length else it
        }
        val filterJson = URLDecoder.decode(url.substring(filterStart, filterEnd), "UTF-8")
        if (filterJson.trim() == "[]") {
            return emptyList()
        }

        val gson = Gson()
        val type = object : TypeToken<List<FilterModel>>() {}.type
        gson.fromJson<List<FilterModel>>(filterJson, type) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
}

fun applyFiltersToWageHistory(
    originalList: List<WageAndHistoryModel>?,
    filters: List<FilterModel>?
): List<WageAndHistoryModel>? {

    if (filters.isNullOrEmpty()) return originalList

    val yearRanges = mutableListOf<Pair<Int, Int>>()

    var i = 0
    while (i < filters.size) {
        val f = filters[i]
        if (f.property == "hisyear") {
            val startYear = f.value.toIntOrNull()
            var endYear = startYear
            if (i + 1 < filters.size && filters[i + 1].property == "hisyear") {
                endYear = filters[i + 1].value.toIntOrNull() ?: startYear
                i++
            }

            if (startYear != null && endYear != null) {
                yearRanges.add(startYear to endYear)
            }
        }
        i++
    }

    return originalList?.filter { wageHistory ->
        val year = wageHistory.hisyear.toIntOrNull()
        yearRanges.any { range ->
            year != null && year in range.first..range.second
        }
    }
}

fun extractPrescriptionParamsFromUrl(url: String): MutableMap<String, String> {
    return try {
        val uri = url.toUri()
        val pathSegments = uri.pathSegments
        if (pathSegments != null && pathSegments.size >= 4 && pathSegments[0] == "api" ) {
            mutableMapOf(
                "startDate" to pathSegments[2],
                "endDate" to pathSegments[3]
            )
        } else {
            mutableMapOf()
        }
    } catch (e: Exception) {
        mutableMapOf()
    }
}



data class FilterModel(
    val property: String,
    val value: String,
    val operator: String
)
