package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param

import saman.zamani.persiandate.PersianDate

data class DateTimeStampFilter(val startDate: Long?, val endDate: Long?)
data class DateFilter(val startDate: DateSplitFilter?, val endDate: DateSplitFilter?)
data class DateSplitFilter(
    val year: String,
    val month: String,
    val day: String,
    val completeDate: String
){
    fun toTimeStamp(): Long? {
        val persianDate = PersianDate().setShYear(year.toInt()).setShMonth(month.toInt()).setShDay(day.toInt())
        return persianDate.time
    }


}

