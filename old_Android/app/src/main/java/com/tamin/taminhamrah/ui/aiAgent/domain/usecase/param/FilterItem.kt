package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param


sealed class AgentFilter(val key: FilterKey, open val value: String) {
    data class StartDate(override val value: String) : AgentFilter(FilterKey.START_DATE, value)
    data class EndDate(override val value: String) : AgentFilter(FilterKey.END_DATE, value)
    data class NationalCode(override val value: String) : AgentFilter(FilterKey.NATIONAL_CODE, value)
    data class RequestType(override val value: String) : AgentFilter(FilterKey.REQUEST_TYPE_ID, value)
    data class DependantUserNationalCode(override val value: String) : AgentFilter(FilterKey.DEPENDANT_USER_NATIONAL_CODE, value)


    companion object {
        fun createFromKey(key: String, value: String): AgentFilter? {
            return when (key) {
                FilterKey.START_DATE.key -> StartDate(value)
                FilterKey.END_DATE.key -> EndDate(value)
                FilterKey.NATIONAL_CODE.key -> NationalCode(value)
                FilterKey.REQUEST_TYPE_ID.key -> RequestType(value)
                FilterKey.DEPENDANT_USER_NATIONAL_CODE.key -> DependantUserNationalCode(value)
                else -> null
            }
        }
        fun createFromKey(key: FilterKey, value: String): AgentFilter? {
            return when (key) {
                FilterKey.START_DATE -> StartDate(value)
                FilterKey.END_DATE -> EndDate(value)
                FilterKey.NATIONAL_CODE -> NationalCode(value)
                FilterKey.REQUEST_TYPE_ID -> RequestType(value)
                FilterKey.DEPENDANT_USER_NATIONAL_CODE -> DependantUserNationalCode(value)
            }
        }

        fun getFilterFromString(key: String?): FilterKey? {
            if (key == null) return null
            return when (key) {
                FilterKey.START_DATE.key -> FilterKey.START_DATE
                FilterKey.END_DATE.key -> FilterKey.END_DATE
                FilterKey.NATIONAL_CODE.key -> FilterKey.NATIONAL_CODE
                FilterKey.REQUEST_TYPE_ID.key -> FilterKey.REQUEST_TYPE_ID
                FilterKey.DEPENDANT_USER_NATIONAL_CODE.key -> FilterKey.DEPENDANT_USER_NATIONAL_CODE
                else -> null
            }
        }


}
}

enum class FilterKey(val key: String) {
    START_DATE("startDate"),
    END_DATE("endDate"),
    NATIONAL_CODE("nationalCode"),
    DEPENDANT_USER_NATIONAL_CODE("dependantUserNationalCode"),
    REQUEST_TYPE_ID("requestTypeId")

}
