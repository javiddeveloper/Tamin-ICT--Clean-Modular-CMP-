package com.tamin.taminhamrah.ui.aiAgent.mapper

sealed class AgentSuccessMessageParamsNames(val key: String, open val value: String) {
    data class StartDate(override val value: String) : AgentSuccessMessageParamsNames(START_DATE,value)
    data class EndDate(override val value: String) : AgentSuccessMessageParamsNames(END_DATE,value)


    companion object {
        const val START_DATE = "@startDate"
        const val END_DATE = "@endDate"


//        fun fromString(key: String?): AgentSuccessMessageParamsNames? {
//            if (key == null) return null
//            return when (key) {
//                START_DATE -> StartDate
//                END_DATE -> StartDate
//                else -> null
//            }
//        }
    }
}

data class AgentMessageParam(val name:AgentSuccessMessageParamsNames, val value:String)