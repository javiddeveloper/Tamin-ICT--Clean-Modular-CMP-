package com.tamin.taminhamrah.ui.home.services.objectionInsuranceHistory.model

data class MonthModel(
    val monthName: String,
    var registeredValue: String,
    var editedValue: String,
    var isLeapYear: Boolean = false,
    var maxDayAvailable:Int = 31
)
