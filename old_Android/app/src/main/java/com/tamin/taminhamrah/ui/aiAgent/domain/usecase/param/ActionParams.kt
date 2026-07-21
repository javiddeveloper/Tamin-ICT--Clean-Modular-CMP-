package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param

import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentResponseData
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import saman.zamani.persiandate.PersianDateFormat


data class ServiceParams(
    val serviceName: ServiceNameEnum,
    val payload: Map<String, Any?>? = null,
    val data: List<AgentResponseData>? = null,
    val message: String? = null
)

fun ServiceParams.getFilters(): Map<String, String> {
    val filters = mutableMapOf<String, String>()
    val filterList = this.payload?.get("filter") as? List<String>
    filterList?.forEach {
        val parts = it.split(":", limit = 2)
        if (parts.size >= 2) {
            filters[parts[0]] = parts[1]
        }
    }
    val cleanFilters = filters.mapKeys { it.key.trim() }
    return cleanFilters
}

fun ServiceParams.getFilterList(): HashMap<FilterKey, AgentFilter> {
    val filters = getFilters()
    val outPutFilters = HashMap<FilterKey, AgentFilter>()

    filters.forEach {
        val agentKey = AgentFilter.getFilterFromString(it.key)
        agentKey?.apply {
            val filter = AgentFilter.createFromKey(this, it.value)
            filter?.let {
                outPutFilters[this] = it

            }
        }

    }
    return outPutFilters
}

fun HashMap<FilterKey, AgentFilter>.toFilterHash(): HashMap<String, String> {
    val outPutFilters = HashMap<String, String>()

    forEach {
        outPutFilters[it.key.key] = it.value.value
    }
    return outPutFilters


}


fun ServiceParams.getDateTimestampFilter(): DateTimeStampFilter {
    val filters = getFilters()
    var startDate: Long? = null
    var endDate: Long? = null
    val persianDateFormat = PersianDateFormat()
    if (filters.containsKey("startDate")) {
        val startDateString = filters["startDate"]
        startDateString?.apply {
            startDate =
                persianDateFormat.parse(startDateString, "yyyyMMdd").time
        }
    }

    if (filters.containsKey("endDate")) {
        val endDateString = filters["endDate"]
        endDateString?.apply {
            endDate =
                persianDateFormat.parse(endDateString, "yyyyMMdd").time
        }
    }

    return DateTimeStampFilter(startDate, endDate)
}

fun ServiceParams.getDateFilter(): DateFilter {
    val filters = getFilters()
    var startDate: DateSplitFilter? = null
    var endDate: DateSplitFilter? = null
    if (filters.containsKey("startDate")) {
        val startDateString = filters["startDate"]
        startDateString?.apply {
            val year = startDateString.substring(0..3)
            val month = startDateString.substring(4..5)
            val day = startDateString.substring(6..7)
            startDate = DateSplitFilter(
                year = year,
                month = month,
                day = day,
                completeDate = buildString {
                    append(year)
                    append("/")
                    append(month)
                    append("/")
                    append(day)
                }
            )
        }
    }
    if (filters.containsKey("endDate")) {
        val endDateString = filters["endDate"]
        endDateString?.apply {
            val year = endDateString.substring(0..3)
            val month = endDateString.substring(4..5)
            val day = endDateString.substring(6..7)
            endDate = DateSplitFilter(
                year = year,
                month = month,
                day = day,
                completeDate = buildString {
                    append(year)
                    append("/")
                    append(month)
                    append("/")
                    append(day)
                }
            )
        }
    }
    return DateFilter(startDate, endDate)
}


fun ServiceParams.getNationalCodeFilter(): String? {
    val filters = getFilters()
    val nationalID =
        if (filters.containsKey("nationalId")) filters["nationalId"] else if (filters.containsKey(
                "NationalId"
            )
        ) filters["NationalId"] else null
    return nationalID
}

fun ServiceParams.getFishFilters(): FishFilter {
    val filters = getFilters()
    var startDate: Long? = null
    var endDate: Long? = null
    val persianDateFormat = PersianDateFormat()
    if (filters.containsKey("startDate")) {
        val startDateString = filters["startDate"]
        startDateString?.apply {
            startDate =
                persianDateFormat.parse(startDateString, "yyyyMMdd").time
        }
    }

    if (filters.containsKey("endDate")) {
        val endDateString = filters["endDate"]
        endDateString?.apply {
            endDate =
                persianDateFormat.parse(endDateString, "yyyyMMdd").time
        }
    }
    val nationalID =
        if (filters.containsKey("nationalId")) filters["nationalId"] else if (filters.containsKey(
                "NationalId"
            )
        ) filters["NationalId"] else null

    val paymentType = if (filters.containsKey("paymentType")) filters["paymentType"] else null

    return FishFilter(
        startDate = startDate,
        endDate = endDate,
        nationalId = nationalID,
        pensionerId = null,
        paymentType = paymentType
    )
}
