package com.tamin.taminhamrah.data.local.models

import com.tamin.taminhamrah.utils.ConvertDate

data class ToolBarStepperModel(
    var userName : String = "",
    var nationalID : String = "" ,
    var accountNum : String = ""
) {
    fun getPersianDate(timeStamp: Long?): String {
        return ConvertDate.convertTimestampToPersianDate(timeStamp ?: 0)
    }
}


