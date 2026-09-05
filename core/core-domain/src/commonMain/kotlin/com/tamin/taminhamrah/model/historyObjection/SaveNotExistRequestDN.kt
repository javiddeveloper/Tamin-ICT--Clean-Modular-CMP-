package com.tamin.taminhamrah.model.historyObjection

data class SaveNotExistRequestDN(
    val branchCode: String,
    val branchName: String,
    val cityCode: String,
    val cityName: String,
    val endDate: Long,
    val insuranceType: String,
    val provinceCode: String,
    val provinceName: String,
    val workshopId: String,
    val workshopName: String,
    val workshopManager: String,
    val workshopAddress: String,
    val startDate: Long,
    val workDays: String,
)
